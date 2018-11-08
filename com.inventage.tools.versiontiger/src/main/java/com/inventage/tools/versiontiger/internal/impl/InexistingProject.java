package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectId;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.Version;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

public class InexistingProject implements Project {

	private final String projectPath;
	private final VersioningLogger logger;
	private final VersionFactory versionFactory;

	protected InexistingProject(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		this.projectPath = projectPath;
		this.logger = logger;
		this.versionFactory = versionFactory;
	}
	
	private void logWarning(Version newVersion) {
		VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();
		loggerItem.setStatus(VersioningLoggerStatus.ERROR);
		loggerItem.setProject(this);
		loggerItem.setNewVersion(newVersion);
		loggerItem.appendToMessage("Project does not exist.");
		logger.addVersioningLoggerItem(loggerItem);
	}
	
	@Override
	public void setVersion(MavenVersion newVersion) {
		logWarning(newVersion);
	}

	@Override
	public void incrementMajorVersionAndSnapshot() {
		logWarning(null);
	}

	@Override
	public void incrementMinorVersionAndSnapshot() {
		logWarning(null);
	}

	@Override
	public void incrementBugfixVersionAndSnapshot() {
		logWarning(null);
	}

	@Override
	public void useReleaseVersion() {
		logWarning(null);
	}

	@Override
	public void useSnapshotVersion() {
		logWarning(null);
	}
	
	@Override
	public void useReleaseVersionWithSuffix(String newSuffix) {
		logWarning(null);
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

	@Override
	public void setProperty(String key, String value) {
		logWarning(null);
	}

	@Override
	public String getProperty(String key) {
		logWarning(null);
		return null;
	}

	@Override
	public ProjectId id() {
		return ProjectIdImpl.create("_INEXISTING", projectPath);
	}

	@Override
	public String projectPath() {
		return projectPath;
	}

	@Override
	public MavenVersion getVersion() {
		return versionFactory.createMavenVersion(0, 0, 0, null, null, true);
	}

	@Override
	public void updateReferencesFor(ProjectId id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
	}

	@Override
	public boolean isVersionInherited() {
		return false;
	}

	@Override
	public boolean exists() {
		return false;
	}
	
	@Override
	public boolean ensureIsSnapshot() {
		logWarning(null);
		return true;
	}

	@Override
	public boolean ensureIsRelease() {
		logWarning(null);
		return true;
	}
	
	@Override
	public boolean ensureStrictOsgiDependencyTo(ProjectId projectId) {
		return true;
	}

}
