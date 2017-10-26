package com.inventage.tools.versiontiger.universedefinition.workspaceprojects;

import static com.inventage.tools.versiontiger.universedefinition.UniverseDefinitions.ALL_WORKSPACE_PROJECTS_UNIVERSE_ID;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.universedefinition.Messages;
import com.inventage.tools.versiontiger.universedefinition.ProjectUniverseProvider;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitionPlugin;

public class AllWorkspaceProjectsUniverseProvider implements ProjectUniverseProvider {

	@Override
	public Object create() throws CoreException {
		return this;
	}

	@Override
	public Set<ProjectUniverse> getProjectUniverses(VersioningLogger logger) {
		return Sets.newHashSet(createAllWorkspaceProjectsUniverse(logger));
	}

	private ProjectUniverse createAllWorkspaceProjectsUniverse(VersioningLogger logger) {
		ProjectUniverse universe = UniverseDefinitionPlugin.getDefault().getVersioning()
				.createUniverse(ALL_WORKSPACE_PROJECTS_UNIVERSE_ID, Messages.allWorkspaceProjectsUniverseName, logger);
		addAllWorkspaceProjects(universe);
		return universe;
	}

	private boolean includeClosedProjects = true;
	
	private void addAllWorkspaceProjects(ProjectUniverse universe) {
		for (IProject project : getWorkspaceProjects()) {
			if (includeClosedProjects || project.isOpen()) {
				universe.addProjectPath(getProjectPath(project));
			}
		}
	}

	private List<IProject> getWorkspaceProjects() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IProject[] projects = workspace.getRoot().getProjects();
		return Lists.newArrayList(projects);
	}

	private String getProjectPath(IProject project) {
		return project.getLocation().toString();
	}
}
