package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectUniverse;

class ReferencesUpdater implements Project {

	private final Project project;
	private final ProjectUniverse projectUniverse;

	ReferencesUpdater(Project project, ProjectUniverse projectUniverse) {
		this.project = project;
		this.projectUniverse = projectUniverse;
	}

	public void setVersion(MavenVersion newVersion) {
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

}
