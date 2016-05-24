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

	@Override
	public void useReleaseVersionWithSuffix(String newSuffix) {
		for (Project project : projects) {
			project.useReleaseVersionWithSuffix(newSuffix);
		}
	}
	
	@Override
	public boolean ensureIsSnapshot() {
		for (Project project : projects) {
			if (!project.ensureIsSnapshot()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean ensureIsRelease() {
		boolean result = true;
		for (Project project : projects) {
			if (!project.ensureIsRelease()) {
				result = false;
			}
		}
		return result;
	}

}
