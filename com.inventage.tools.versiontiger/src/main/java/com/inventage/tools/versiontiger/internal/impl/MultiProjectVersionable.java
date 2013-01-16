package com.inventage.tools.versiontiger.internal.impl;

import java.util.Collection;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.Versionable;

class MultiProjectVersionable implements Versionable {

	private final Collection<Project> projects;

	MultiProjectVersionable(Collection<Project> projects) {
		this.projects = projects;
	}

	public void setVersion(MavenVersion newVersion) {
		for (Project project : projects) {
			project.setVersion(newVersion);
		}
	}

	public void incrementMajorVersionAndSnapshot() {
		for (Project project : projects) {
			project.incrementMajorVersionAndSnapshot();
		}
	}

	public void incrementMinorVersionAndSnapshot() {
		for (Project project : projects) {
			project.incrementMinorVersionAndSnapshot();
		}
	}

	public void incrementBugfixVersionAndSnapshot() {
		for (Project project : projects) {
			project.incrementBugfixVersionAndSnapshot();
		}
	}

	public void useReleaseVersion() {
		for (Project project : projects) {
			project.useReleaseVersion();
		}
	}

	public void useSnapshotVersion() {
		for (Project project : projects) {
			project.useSnapshotVersion();
		}
	}

}
