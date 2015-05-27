package com.inventage.tools.versiontiger.ui.edit;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.common.collect.Sets;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.strategy.VersioningStrategy;
import com.inventage.tools.versiontiger.ui.VersioningUIPlugin;

class EditVersionModel extends AbstractPropertyChangeSupport {
	public static final String PN_PROJECT_UNIVERSE = "projectUniverse"; //$NON-NLS-1$
	public static final String PN_PROJECTS = "projects"; //$NON-NLS-1$
	public static final String PN_VERSIONING_STRATEGY = "versioningStrategy"; //$NON-NLS-1$

	private ProjectUniverse projectUniverse;
	private final Set<VersioningProject> projects = Sets.newHashSet();
	private VersioningStrategy versioningStrategy;
	private final Collection<IProject> selectedProjectsBeforeWizard = Sets.newHashSet();
	
	private VersioningLogger logger;
	
	public EditVersionModel(VersioningLogger logger, Collection<IProject> selectedProjects) {
		this.logger = logger;
		this.selectedProjectsBeforeWizard.addAll(selectedProjects);
	}

	public ProjectUniverse getProjectUniverse() {
		return projectUniverse;
	}

	public void setProjectUniverse(ProjectUniverse projectUniverse) {
		if (!isSameProjectUniverse(projectUniverse)) {
			firePropertyChange(PN_PROJECT_UNIVERSE, this.projectUniverse, this.projectUniverse = projectUniverse);
			updateProjects();
		}
	}

	private boolean isSameProjectUniverse(ProjectUniverse projectUniverse) {
		if (this.projectUniverse == null) {
			return projectUniverse == null;
		}
		return this.projectUniverse.equals(projectUniverse);
	}

	private void updateProjects() {
		Set<VersioningProject> newVersioningProjects = Sets.newHashSet();
		for (Project project : projectUniverse.listAllProjects()) {
			/* If one project fails to create, we want the others being added nevertheless. */
			try {
				VersioningProject newVersioningProject = new VersioningProject(project);
				newVersioningProject.setSelected(isSelected(project.id()));
	
				if (versioningStrategy != null) {
					newVersioningProject.setNewVersion(versioningStrategy.apply(project.getVersion()));
				}
				newVersioningProjects.add(newVersioningProject);
			}
			catch (IllegalArgumentException e) {
				StatusManager.getManager().handle(new Status(Status.ERROR, VersioningUIPlugin.PLUGIN_ID,
						"Was not able to add project: " + project.id() + " in " + project.projectPath(), e),
						StatusManager.SHOW);
			}
		}
		setProjects(newVersioningProjects);
	}

	public Set<VersioningProject> getProjects() {
		return projects;
	}

	private void setProjects(Set<VersioningProject> projects) {
		Set<VersioningProject> oldProjects = Sets.newHashSet(this.projects);
		this.projects.clear();
		this.projects.addAll(projects);
		firePropertyChange(PN_PROJECTS, oldProjects, this.projects);
	}

	private boolean isSelected(String projectId) {
		for (VersioningProject project : projects) {
			if (project.getProjectId().equals(projectId)) {
				return project.isSelected();
			}
		}
		
		for (IProject selectedProject : selectedProjectsBeforeWizard) {
			IPath projectLocation = selectedProject.getLocation();
			if (projectLocation != null) {
				if (projectId.equals(projectUniverse.idForProjectPath(projectLocation.toOSString()))) {
					return true;
				}
			}
		}

		return false;
	}

	public VersioningStrategy getVersioningStrategy() {
		return versioningStrategy;
	}

	public void setVersionStrategy(VersioningStrategy versioningStrategy) {
		VersioningStrategy oldVersioningStrategy = this.versioningStrategy;
		this.versioningStrategy = versioningStrategy;
		firePropertyChange(PN_VERSIONING_STRATEGY, oldVersioningStrategy, this.versioningStrategy);
		updateProjectVersions();
	}

	private void updateProjectVersions() {
		if (versioningStrategy != null) {
			for (VersioningProject project : projects) {
				project.setNewVersion(versioningStrategy.apply(project.getOldVersion()));
			}
		}
	}

	public void applyNewVersions() {
		for (VersioningProject project : projects) {
			if (project.isSelected()) {
				project.applyNewVersion();
			}
		}
	}

	void setProjectSelection(Set<String> projectIds) {
		for (VersioningProject project : projects) {
			project.setSelected(projectIds.contains(project.getProjectId()));
		}
	}

	public void setAllSelected(boolean selected) {
		for (VersioningProject project : projects) {
			project.setSelected(selected);
		}
	}
	
	public VersioningLogger getLogger() {
		return logger;
	}

	public boolean isInexistingProject(String projectId) {
		for (VersioningProject project : projects) {
			if (project.getProjectId().equals(projectId)) {
				return project.isInexisting();
			}
		}
		return true;
	}
}
