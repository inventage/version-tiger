package com.inventage.tools.versiontiger.ui.edit;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;

class VersioningProject extends AbstractPropertyChangeSupport implements Comparable<VersioningProject> {
	public static final String PN_PROJECT_ID = "projectId"; //$NON-NLS-1$
	public static final String PN_OLD_VERSION = "oldVersion"; //$NON-NLS-1$
	public static final String PN_SELECTED = "selected"; //$NON-NLS-1$
	public static final String PN_NEW_VERSION = "newVersion"; //$NON-NLS-1$

	private final Project project;
	private boolean selected;
	private MavenVersion newVersion;

	public VersioningProject(Project project) {
		this.project = project;
		this.newVersion = project.getVersion();
	}

	@Override
	public int compareTo(VersioningProject o) {
		return getProjectId().compareTo(o.getProjectId());
	}

	public void applyNewVersion() {
		project.setVersion(newVersion);
	}

	public String getProjectId() {
		return project.id().getArtifactId();
	}

	public MavenVersion getOldVersion() {
		return project.getVersion();
	}

	public MavenVersion getNewVersion() {
		return isSelected() ? newVersion : null;
	}

	public void setNewVersion(MavenVersion newVersion) {
		MavenVersion oldVersion = this.newVersion;
		this.newVersion = newVersion;
		firePropertyChange(PN_NEW_VERSION, oldVersion, this.newVersion);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (this.selected != selected && !isInexisting()) {
			MavenVersion oldVersion = getNewVersion();
			this.selected = selected;
			firePropertyChange(PN_SELECTED, !this.selected, this.selected);
			firePropertyChange(PN_NEW_VERSION, oldVersion, getNewVersion());
		}
	}
	
	public boolean isInexisting() {
		return !project.exists();
	}

}