package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import com.inventage.tools.versiontiger.MavenProject;
import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
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

	protected String pomContent;
	private String id;
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

	public String id() {
		if (id == null) {
			id = new XmlHandler().readElement(getPomContent(), "project/artifactId");
		}

		return id;
	}

	@Override
	public int compareTo(Project o) {
		String thisId = id();
		String otherId = o.id();
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
		if (isVersionInherited()) {
			logWarning("Cannot set version in pom.xml for project that heirs version from parent.", getVersion(), newVersion);
			return;
		}
		pomContent = new XmlHandler().writeElement(getPomContent(), "project/version", newVersion.toString());

		updatePomXmlFile();

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
		pomContent = new XmlHandler().writeElement(getPomContent(), "project/properties/" + key, value);

		updatePomXmlFile();

		logSuccess(getPomXmlFile() + ": project/properties/" + key + " = " + value, null, null);
	}

	private void updatePomXmlFile() {
		File pomXmlFile = getPomXmlFile();
		if (pomXmlFile != null) {
			new FileHandler().writeFileContent(pomXmlFile, pomContent);
		}
	}

	@Override
	public String getProperty(String key) {
		Element element = new XmlHandler().getElement(getPomContent(), "project/properties/" + key);

		if (element != null) {
			return element.getText();
		}

		return null;
	}

	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		Element projectElement = new XmlHandler().getElement(getPomContent(), "project");
		Element dependenciesElement = projectElement.getChild("dependencies");
		Element dependencyManagementElement = projectElement.getChild("dependencyManagement/dependencies");

		if (oldVersion == null || !oldVersion.equals(newVersion)) {
			if (updateDependencies(dependenciesElement, id, oldVersion, newVersion) | updateDependencies(dependencyManagementElement, id, oldVersion, newVersion)
					| updateParent(projectElement, id, oldVersion, newVersion, projectUniverse)) {
	
				pomContent = projectElement.getDocument().toXML();
				updatePomXmlFile();
			}
			
			for (File karafFile : getKarafFiles()) {
				Set<String> updatedReferences = new KarafFeature(karafFile).updateReferencesFor(id, oldVersion, newVersion);
				for (String updatedKarafReference : updatedReferences) {
					logReferenceSuccess(karafFile + ": " + updatedKarafReference, oldVersion, newVersion, id);
				}
			}
		}
	}

	private boolean updateDependencies(Element dependenciesElement, String id, MavenVersion oldVersion, MavenVersion newVersion) {
		boolean hasModifications = false;

		if (dependenciesElement != null) {
			for (Element dependencyElement : dependenciesElement.getChildren()) {
				Element artifactIdElement = dependencyElement.getChild("artifactId");
				Element versionElement = dependencyElement.getChild("version");
				if (artifactIdElement != null && versionElement != null && id.equals(artifactIdElement.getTrimmedText())
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

	private boolean updateParent(Element projectElement, String id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		boolean hasModifications = false;

		Element parentElement = projectElement.getChild("parent");
		if (parentElement != null) {
			Element artifactIdElement = parentElement.getChild("artifactId");
			Element versionElement = parentElement.getChild("version");
			if (artifactIdElement != null && id.equals(artifactIdElement.getTrimmedText()) && versionElement != null
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

	protected File getPomXmlFile() {
		return new File(projectPath, "pom.xml");
	}

	protected String getPomContent() {
		if (pomContent == null) {
			pomContent = new FileHandler().readFileContent(getPomXmlFile());
		}

		return pomContent;
	}
	
	protected void logSuccess(String message, Version oldVersion, Version newVersion) {
		log(message, VersioningLoggerStatus.SUCCESS, oldVersion, newVersion, null);
	}
	
	protected void logReferenceSuccess(String message, Version oldVersion, Version newVersion, String originalProject) {
		log(message, VersioningLoggerStatus.SUCCESS, oldVersion, newVersion, originalProject);
	}
	
	protected void logWarning(String message, Version oldVersion, Version newVersion) {
		log(message, VersioningLoggerStatus.WARNING, oldVersion, newVersion, null);
	}
	
	protected void logError(String message, Version oldVersion, Version newVersion) {
		log(message, VersioningLoggerStatus.ERROR, oldVersion, newVersion, null);
	}
	
	protected void logReferenceError(String message, Version oldVersion, Version newVersion, String originalProject) {
		log(message, VersioningLoggerStatus.ERROR, oldVersion, newVersion, originalProject);
	}
	
	private void log(String message, VersioningLoggerStatus status, Version oldVersion, Version newVersion, String originalProject) {
		VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();
		
		loggerItem.setProject(this);
		loggerItem.setStatus(status);
		
		loggerItem.setOriginalProject(originalProject);
		
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
	public boolean ensureStrictOsgiDependencyTo(String projectId) {
		return true;
	}

}
