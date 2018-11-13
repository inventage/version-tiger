package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import com.inventage.tools.versiontiger.MavenProject;
import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectId;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.Version;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

import de.pdark.decentxml.Element;

class MavenProjectImpl implements MavenProject {

	private final String projectPath;

	private String pomContent;
	private ProjectId id;
	private MavenVersion version;
	private MavenVersion oldVersion;
	private final VersioningLogger logger;
	private final VersionFactory versionFactory;

	protected MavenProjectImpl(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		this.projectPath = projectPath;
		this.logger = logger;
		this.versionFactory = versionFactory;
	}

	protected VersioningLogger getLogger() {
		return logger;
	}

	protected VersionFactory getVersionFactory() {
		return versionFactory;
	}

	public String projectPath() {
		return projectPath;
	}

	public ProjectId id() {
		if (id == null) {
			id = ProjectIdImpl.create(groupId(), artifactId());
		}

		return id;
	}

	@Override
	public int compareTo(Project o) {
		ProjectId thisId = id();
		ProjectId otherId = o.id();
		if (thisId != null && otherId != null) {
			return thisId.compareTo(otherId);
		}
		return 0;
	}

	public MavenVersion getVersion() {
		if (version == null) {
			String currentVersion = new XmlHandler().readElement(getPomContent(), "project/version");
			if (currentVersion == null) {
				currentVersion = new XmlHandler().readElement(getPomContent(), "project/parent/version");
			}
			version = getVersionFactory().createMavenVersion(currentVersion);
		}

		return version;
	}

	@Override
	public boolean isVersionInherited() {
		return new XmlHandler().readElement(getPomContent(), "project/version") == null;
	}

	public void setVersion(MavenVersion newVersion) {
		/* We store the old version for logging purposes. */
		oldVersion = getVersion();
		version = newVersion;

		setProjectVersionInPom(newVersion);
		setVersionInKarafFiles(oldVersion, newVersion);
	}

	private void setProjectVersionInPom(MavenVersion newVersion) {
		pomContent = new XmlHandler().writeElement(getPomContent(), "project/version", newVersion.toString());

		new FileHandler().writeFileContent(getPomXmlFile(), pomContent);

		logSuccess(getPomXmlFile() + ": project/version = " + newVersion, oldVersion, newVersion);
	}

	public void incrementMajorVersionAndSnapshot() {
		setVersion(getVersion().incrementMajorAndSnapshot());
	}

	public void incrementMinorVersionAndSnapshot() {
		setVersion(getVersion().incrementMinorAndSnapshot());
	}

	public void incrementBugfixVersionAndSnapshot() {
		setVersion(getVersion().incrementBugfixAndSnapshot());
	}

	public void useReleaseVersion() {
		setVersion(getVersion().releaseVersion());
	}

	@Override
	public void useReleaseVersionWithSuffix(String newSuffix) {
		setVersion(getVersion().releaseVersionWithSuffix(newSuffix));
	}

	public void useSnapshotVersion() {
		setVersion(getVersion().snapshotVersion());
	}

	public void setProperty(String key, String value) {
		String oldValue = getProperty(key);
		if (oldValue == null) {
			logError("Unknown property '" + key + "' in project: " + id(), null, null);
			return;
		}
		
		pomContent = new XmlHandler().writeElement(getPomContent(), "project/properties/" + key, value);
		new FileHandler().writeFileContent(getPomXmlFile(), pomContent);

		logSuccess(getPomXmlFile() + ": project/properties/" + key + " = " + value, null, null);
	}

	@Override
	public String getProperty(String key) {
		Element element = new XmlHandler().getElement(getPomContent(), "project/properties/" + key);

		if (element != null) {
			return element.getText();
		}

		return null;
	}

	public void updateReferencesFor(ProjectId id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		Element projectElement = new XmlHandler().getElement(getPomContent(), "project");
		Element dependenciesElement = projectElement.getChild("dependencies");
		Element dependencyManagementElement = projectElement.getChild("dependencyManagement/dependencies");
		
		if (oldVersion == null || !oldVersion.equals(newVersion)) {
			if (updateDependencies(dependenciesElement, id, oldVersion, newVersion) | updateDependencies(dependencyManagementElement, id, oldVersion, newVersion)
					| updateParent(projectElement, id, oldVersion, newVersion, projectUniverse)) {

				pomContent = projectElement.getDocument().toXML();
				new FileHandler().writeFileContent(getPomXmlFile(), pomContent);
			}

			for (File karafFile : getKarafFiles()) {
				Set<String> updatedReferences = new KarafFeature(karafFile).updateReferencesFor(id, oldVersion, newVersion);
				for (String updatedKarafReference : updatedReferences) {
					logReferenceSuccess(karafFile + ": " + updatedKarafReference, oldVersion, newVersion, id);
				}
			}
		}
	}

	private String groupId() {
		String groupId = new XmlHandler().readElement(getPomContent(), "project/groupId");
		if (groupId == null) {
			groupId = new XmlHandler().readElement(getPomContent(), "project/parent/groupId");
		}
		return groupId;
	}

	private String artifactId() {
		return new XmlHandler().readElement(getPomContent(), "project/artifactId");
	}

	private boolean updateDependencies(Element dependenciesElement, ProjectId id, MavenVersion oldVersion, MavenVersion newVersion) {
		boolean hasModifications = false;

		if (dependenciesElement != null) {
			for (Element dependencyElement : dependenciesElement.getChildren()) {
				Element groupIdElement = dependencyElement.getChild("groupId");
				Element artifactIdElement = dependencyElement.getChild("artifactId");
				Element versionElement = dependencyElement.getChild("version");
				ProjectId curId = getIdFromElements(groupIdElement, artifactIdElement);
				if (curId.equalsIgnoreGroupIfUnknown(id) && versionElement != null
						&& (oldVersion == null || oldVersion.toString().equals(versionElement.getTrimmedText()))) {

					// reference match
					versionElement.setText(newVersion.toString());
					hasModifications = true;

					logReferenceSuccess(getPomXmlFile() + ": " + versionElement.getChildPath() + " = " + newVersion, oldVersion, newVersion, id);
				}
			}
		}

		return hasModifications;
	}

	private boolean updateParent(Element projectElement, ProjectId id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		boolean hasModifications = false;

		Element parentElement = projectElement.getChild("parent");
		if (parentElement != null) {
			Element groupIdElement = parentElement.getChild("groupId");
			Element artifactIdElement = parentElement.getChild("artifactId");
			Element versionElement = parentElement.getChild("version");
			ProjectId curId = getIdFromElements(groupIdElement, artifactIdElement);
			
			if (curId.equalsIgnoreGroupIfUnknown(id) && versionElement != null
					&& (oldVersion == null || versionElement.getTrimmedText().equals(oldVersion.toString()))) {

				versionElement.setText(newVersion.toString());
				hasModifications = true;
				logReferenceSuccess(getPomXmlFile() + ": " + versionElement.getChildPath() + " = " + newVersion, oldVersion, newVersion, id);

				if (isVersionInherited()) {
					// our (inherited) version changed so lets propagate our version change too
					projectUniverse.updateReferencesFor(id(), oldVersion, newVersion);
				}
			}
		}

		return hasModifications;
	}

	private ProjectId getIdFromElements(Element groupIdElement, Element artifactIdElement) {
		ProjectId compositeId = null;
		if (groupIdElement != null && artifactIdElement != null) {
			compositeId = ProjectIdImpl.create(groupIdElement.getTrimmedText(), artifactIdElement.getTrimmedText());
		}
		return compositeId;
	}

	private Set<File> getKarafFiles() {
		Set<File> result = new LinkedHashSet<File>();

		Element propertyElement = new XmlHandler().getElement(getPomContent(), "project/properties/versionTigerFiles");
		if (propertyElement != null) {
			String propertyValue = propertyElement.getText();
			if (propertyValue != null) {
				for (String fileDef : propertyValue.trim().split("\\s*,\\s*")) {
					if (fileDef.startsWith("karaf:")) {
						File karafFile = new FileHandler().createFileFromPath(projectPath, fileDef.substring(6));
						if (karafFile.exists()) {
							result.add(karafFile);
						}
					}
				}
			}
		}

		return result;
	}

	private void setVersionInKarafFiles(MavenVersion oldVersion, MavenVersion newVersion) {
		for (File karafFile : getKarafFiles()) {
			Set<String> updatedReferences = new KarafFeature(karafFile).updateReferencesFor(id, oldVersion, newVersion);
			for (String updatedKarafReference : updatedReferences) {
				logSuccess(karafFile + ": " + updatedKarafReference, oldVersion, newVersion);
			}
		}
	}

	private File getPomXmlFile() {
		return new File(projectPath, "pom.xml");
	}

	private String getPomContent() {
		if (pomContent == null) {
			pomContent = new FileHandler().readFileContent(getPomXmlFile());
		}

		return pomContent;
	}

	protected void logSuccess(String message, Version oldVersion, Version newVersion) {
		log(message, VersioningLoggerStatus.SUCCESS, oldVersion, newVersion, null);
	}

	protected void logReferenceSuccess(String message, Version oldVersion, Version newVersion, ProjectId originalProjectId) {
		log(message, VersioningLoggerStatus.SUCCESS, oldVersion, newVersion, originalProjectId);
	}

	protected void logWarning(String message, Version oldVersion, Version newVersion) {
		log(message, VersioningLoggerStatus.WARNING, oldVersion, newVersion, null);
	}

	protected void logError(String message, Version oldVersion, Version newVersion) {
		log(message, VersioningLoggerStatus.ERROR, oldVersion, newVersion, null);
	}

	protected void logReferenceError(String message, Version oldVersion, Version newVersion, ProjectId originalProjectId) {
		log(message, VersioningLoggerStatus.ERROR, oldVersion, newVersion, originalProjectId);
	}

	private void log(String message, VersioningLoggerStatus status, Version oldVersion, Version newVersion, ProjectId originalProjectId) {
		VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();

		loggerItem.setProject(this);
		loggerItem.setStatus(status);

		loggerItem.setOriginalProject(originalProjectId);

		loggerItem.setOldVersion(oldVersion);
		loggerItem.setNewVersion(newVersion);

		loggerItem.appendToMessage(message);

		logger.addVersioningLoggerItem(loggerItem);
	}

	@Override
	public String toString() {
		return "MavenProjectImpl [projectPath=" + projectPath + ", id=" + id + ", version=" + version + "]";
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public boolean ensureIsSnapshot() {
		if (!getVersion().isSnapshot()) {
			logError("Should not be a release version: " + getVersion(), null, null);
			return false;
		}
		return true;
	}

	@Override
	public boolean ensureIsRelease() {
		if (getVersion().isSnapshot()) {
			logError("Should not be a snapshot version: " + getVersion(), null, null);
			return false;
		}
		return true;
	}

	@Override
	public boolean ensureStrictOsgiDependencyTo(ProjectId projectId) {
		return true;
	}

}
