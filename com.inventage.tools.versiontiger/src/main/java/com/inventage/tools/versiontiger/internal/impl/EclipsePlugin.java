package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;
import com.inventage.tools.versiontiger.internal.manifest.Manifest;
import com.inventage.tools.versiontiger.internal.manifest.ManifestParser;
import com.inventage.tools.versiontiger.util.FileHandler;

class EclipsePlugin extends AbstractEclipseProject {

	private String manifestContent;
	
	EclipsePlugin(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		super(projectPath, logger, versionFactory);
	}

	protected boolean isTestsProject() {
		return getProjectId().endsWith(".tests");
	}

	@Override
	protected String getEclipsePackagingName() {
		return "eclipse-" + (isTestsProject() ? "test-plugin" : "plugin");
	}

	@Override
	protected String getProjectId() {
		StringBuilder buffer = new StringBuilder();
		parseManifest().getManifestHeader("Bundle-SymbolicName").print(buffer );
		int start = buffer.indexOf(":"), end = buffer.lastIndexOf(";");
		if (end > 0) {
			buffer.setLength(end);
		}
		String s = buffer.toString();
		if (start > 0) {
			s = s.substring(start + 1);
		}
		return s.trim();
	}

	@Override
	public void setVersion(MavenVersion newVersion) {
		MavenVersion oldVersion = getVersion();
		
		super.setVersion(newVersion);

		setPluginVersion(asOsgiVersion(newVersion), asOsgiVersion(oldVersion));
	}

	private OsgiVersion asOsgiVersion(MavenVersion mavenVersion) {
		return mavenVersion == null ? null : getVersionFactory().createOsgiVersion(mavenVersion);
	}

	private void setPluginVersion(OsgiVersion newVersion, OsgiVersion oldVersion) {
		try {
			updatePluginVersion(id(), oldVersion, newVersion);
			
			new FileHandler().writeFileContent(getManifestFile(), manifestContent);
		} catch (Exception e) {
			logError("Can't set plugin version in manifest: " + getManifestFile().toString() + ". " + e.getMessage(), oldVersion, newVersion);
		}
	}
	
	@Override
	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		super.updateReferencesFor(id, oldVersion, newVersion, projectUniverse);
		
		OsgiVersion oldOsgiVersion = asOsgiVersion(oldVersion);
		OsgiVersion newOsgiVersion = asOsgiVersion(newVersion);

		try {
			if (updateRequireBundleReferences(id, oldOsgiVersion, newOsgiVersion)
					| updateFragmentHostReference(id, oldOsgiVersion, newOsgiVersion)
					| updatePluginVersion(id, oldOsgiVersion, newOsgiVersion)) {
				new FileHandler().writeFileContent(getManifestFile(), manifestContent);
			}
		} catch (Exception e) {
			logReferenceError("Can't update references in manifest: " + getManifestFile() + ". " + e.getMessage(), oldOsgiVersion, newOsgiVersion, id);
		}
	}
	
	private boolean updatePluginVersion(String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		if (id().equals(id)) {
			Manifest manifest = parseManifest();
			manifest.setBundleVersion(newVersion.toString());
			manifestContent = manifest.print();
			
			logSuccess(getManifestFile() + ": Bundle-Version = " + newVersion, oldVersion, newVersion);
			
			return true;
		}
		return false;
	}
	
	private boolean updateRequireBundleReferences(String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		Manifest manifest = parseManifest();
		
		VersioningLoggerItem loggerItem = createLoggerItem(oldVersion, newVersion, id);
		boolean result = manifest.updateRequireBundleReference(id, oldVersion, newVersion, loggerItem, getVersionFactory().getVersionRangeChangeStrategy());
		getLogger().addVersioningLoggerItem(loggerItem);
		
		manifestContent = manifest.print();
		return result;
	}
	
	private boolean updateFragmentHostReference(String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		Manifest manifest = parseManifest();
		
		VersioningLoggerItem loggerItem = createLoggerItem(oldVersion, newVersion, id);
		boolean result = manifest.updateFragmentHostReference(id, oldVersion, newVersion, loggerItem, getVersionFactory().getVersionRangeChangeStrategy());
		getLogger().addVersioningLoggerItem(loggerItem);
		
		manifestContent = manifest.print();
		return result;
	}
	
	@Override
	public boolean ensureStrictOsgiDependencyTo(String projectId) {
		boolean result = super.ensureStrictOsgiDependencyTo(projectId);
		
		VersioningLoggerItem loggerItem = createLoggerItem(null, null, projectId);
		loggerItem.setStatus(VersioningLoggerStatus.ERROR);
		if (!parseManifest().ensureStrictDependencyTo(projectId, loggerItem)) {
			getLogger().addVersioningLoggerItem(loggerItem);
			return false;
		}
		return result;
	}
	
	private Manifest parseManifest() {
		try {
			return new ManifestParser(getManifestContent(), getVersionFactory()).parse();
		}
		catch (IllegalStateException e) {
			throw new IllegalStateException("Can't parse manifest: " + e.getMessage(), e);
		}
	}

	private String getManifestContent() {
		if (manifestContent == null) {
			manifestContent = new FileHandler().readFileContent(getManifestFile());
		}
		return manifestContent;
	}

	private File getManifestFile() {
		File metaInfFolder = new File(projectPath(), "META-INF");
		return new File(metaInfFolder, "MANIFEST.MF");
	}
	
	private VersioningLoggerItem createLoggerItem(OsgiVersion oldVersion, OsgiVersion newVersion, String originalProject) {
		VersioningLoggerItem loggerItem = getLogger().createVersioningLoggerItem();
		loggerItem.setProject(this);
		loggerItem.setOriginalProject(originalProject);
		loggerItem.setOldVersion(oldVersion);
		loggerItem.setNewVersion(newVersion);
		
		loggerItem.appendToMessage(getManifestFile().getAbsolutePath());
		loggerItem.appendToMessage(": ");
		
		/* By setting the status to null we indicate that the logger item should not be logged. This may be changed deep down in the subsequent method call 
		 * cascade if something noteworthy happens. Also, down there is where the rest of the message is daisy-chained together. */
		loggerItem.setStatus(null);
		
		return loggerItem;
	}

}
