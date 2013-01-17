package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;

import com.inventage.tools.versiontiger.internal.manifest.ManifestParser;
import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.internal.manifest.Manifest;
import com.inventage.tools.versiontiger.util.FileHandler;

class EclipsePlugin extends MavenProjectImpl {

	private String manifestContent;
	
	EclipsePlugin(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		super(projectPath, logger, versionFactory);
	}

	@Override
	public void setVersion(MavenVersion newVersion) {
		MavenVersion oldVersion = getVersion();
		
		super.setVersion(newVersion);

		setPluginVersion(asOsgiVersion(newVersion), asOsgiVersion(oldVersion));
	}

	private OsgiVersion asOsgiVersion(MavenVersion mavenVersion) {
		return getVersionFactory().createOsgiVersion(mavenVersion);
	}

	private void setPluginVersion(OsgiVersion newVersion, OsgiVersion oldVersion) {
		try {
			Manifest manifest = parseManifest();
			manifest.setBundleVersion(newVersion.toString());
			manifestContent = manifest.print();
			
			new FileHandler().writeFileContent(getManifestFile(), manifestContent);
			
			logSuccess(getManifestFile() + ": Bundle-Version = " + newVersion, oldVersion, newVersion);
		} catch (IllegalStateException e) {
			logError("Can't parse manifest: " + getManifestFile().toString() + ". " + e.getMessage(), oldVersion, newVersion);
		}
	}
	
	@Override
	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion) {
		super.updateReferencesFor(id, oldVersion, newVersion);
		
		OsgiVersion oldOsgiVersion = asOsgiVersion(oldVersion);
		OsgiVersion newOsgiVersion = asOsgiVersion(newVersion);

		try {
			if (updateRequireBundleReferences(id, oldOsgiVersion, newOsgiVersion)
					|| updateFragmentHostReference(id, oldOsgiVersion, newOsgiVersion)) {
				new FileHandler().writeFileContent(getManifestFile(), manifestContent);
			}
		} catch (IllegalStateException e) {
			logError("Can't parse manifest: " + getManifestFile() + ". " + e.getMessage(), oldOsgiVersion, newOsgiVersion);
		}
	}
	
	private boolean updateRequireBundleReferences(String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		Manifest manifest = parseManifest();
		
		VersioningLoggerItem loggerItem = getLoggerItem(oldVersion, newVersion);
		boolean result = manifest.updateRequireBundleReference(id, oldVersion, newVersion, loggerItem);
		getLogger().addVersioningLoggerItem(loggerItem);
		
		manifestContent = manifest.print();
		return result;
	}
	
	private boolean updateFragmentHostReference(String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		Manifest manifest = parseManifest();
		
		VersioningLoggerItem loggerItem = getLoggerItem(oldVersion, newVersion);
		boolean result = manifest.updateFragmentHostReference(id, oldVersion, newVersion, loggerItem);
		getLogger().addVersioningLoggerItem(loggerItem);
		
		manifestContent = manifest.print();
		return result;
	}
	
	private Manifest parseManifest() {
		try {
			return new ManifestParser(getManifestContent(), getVersionFactory()).parse();
		}
		catch (IllegalStateException e) {
			throw new IllegalStateException("Can't parse manifest: " + getManifestFile().toString() + ". " + e.getMessage(), e);
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
	
	private VersioningLoggerItem getLoggerItem(OsgiVersion oldVersion, OsgiVersion newVersion) {
		VersioningLoggerItem loggerItem = getLogger().createVersioningLoggerItem();
		loggerItem.setProject(this);
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
