package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

class ReferencesUpdater implements Project {

	private final Project project;
	private final ProjectUniverse projectUniverse;
	private final VersioningLogger logger;

	ReferencesUpdater(Project project, ProjectUniverse projectUniverse, VersioningLogger logger) {
		this.project = project;
		this.projectUniverse = projectUniverse;
		this.logger = logger;
	}

	public void setVersion(MavenVersion newVersion) {
		if (isVersionInherited()) {
			logWarning("Cannot set version for project that heirs version from parent.");
			return;
		}
		
		MavenVersion oldVersion = getVersion();

		project.setVersion(newVersion);

		projectUniverse.updateReferencesFor(project.id(), oldVersion, newVersion);
	}

	public String id() {
		return project.id();
	}

	@Override
	public String projectPath() {
		return project.projectPath();
	}

	public MavenVersion getVersion() {
		return project.getVersion();
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

	public void useSnapshotVersion() {
		setVersion(getVersion().snapshotVersion());
	}
	
	@Override
	public void useReleaseVersionWithSuffix(String newSuffix) {
		setVersion(getVersion().releaseVersionWithSuffix(newSuffix));
	}

	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		project.updateReferencesFor(id, oldVersion, newVersion, projectUniverse);
	}

	@Override
	public void setProperty(String key, String value) {
		project.setProperty(key, value);
	}

	@Override
	public String getProperty(String key) {
		return project.getProperty(key);
	}

	public int compareTo(Project o) {
		return project.compareTo(o);
	}
	
	@Override
	public boolean isVersionInherited() {
		return project.isVersionInherited();
	}

	private void logWarning(String message) {
		VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();
		
		loggerItem.setProject(project);
		loggerItem.setStatus(VersioningLoggerStatus.WARNING);
		loggerItem.appendToMessage(message);
		
		logger.addVersioningLoggerItem(loggerItem);
	}

	@Override
	public boolean exists() {
		return project.exists();
	}
	
	@Override
	public boolean ensureIsSnapshot() {
		return project.ensureIsSnapshot();
	}
	
	@Override
	public boolean ensureIsRelease() {
		return project.ensureIsRelease();
	}
	
	@Override
	public boolean ensureStrictOsgiDependencyTo(String projectId) {
		return project.ensureStrictOsgiDependencyTo(projectId);
	}

}
