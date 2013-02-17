package com.inventage.tools.versiontiger.internal.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.MavenProject;
import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.Versionable;
import com.inventage.tools.versiontiger.VersioningLogger;

class ProjectUniverseImpl implements ProjectUniverse {

	private final Map<String, Project> projects = new TreeMap<String, Project>();
	private final String id;
	private final String name;
	private final ProjectFactory projectFactory;
	private VersioningLogger logger;
	
	ProjectUniverseImpl(String id, ProjectFactory projectFactory, VersioningLogger logger) {
		this(id, null, projectFactory, logger);
	}

	ProjectUniverseImpl(String id, String name, ProjectFactory projectFactory, VersioningLogger logger) {
		this.id = id;
		this.name = name;
		this.projectFactory = projectFactory;
		
		this.logger = logger;
	}

	@Override
	public String id() {
		return id;
	}

	public String name() {
		return name;
	}

	@Override
	public Project addProjectPath(String projectRootFilePath) {
		MavenProject project = projectFactory.createProjectFromRootFilePath(projectRootFilePath, logger);

		if (project != null) {
			projects.put(project.id(), project);
		}

		return project;
	}
	
	@Override
	public Set<Project> addRootProjectPath(String projectRootFilePath) {
		Set<MavenProject> recursiveProjects = projectFactory.createRecursiveProjectsFromRootFilePath(projectRootFilePath, logger);
		Set<Project> result = new HashSet<Project>();
		
		for (MavenProject project: recursiveProjects) {
			projects.put(project.id(), project);
			result.add(project);
		}
		
		return result;
	}

	@Override
	public String idForProjectPath(String projectPath) {
		for (Project project : projects.values()) {
			if (project.projectPath().equals(projectPath)) {
				return project.id();
			}
		}
		return null;
	}

	@Override
	public void removeProject(Project versioningProject) {
		if (versioningProject != null) {
			String id = versioningProject.id();
			projects.remove(id);

			System.out.println("Removed project: " + id);
		} else {
			System.out.println("ProjectUniverse#removeProject(Project) : Project must not be null");
		}
	}

	@Override
	public void clearProjects() {
		projects.clear();
	}
	
	public Set<Project> listAllProjects() {
		Set<Project> result = new LinkedHashSet<Project>();

		for (Project project : projects.values()) {
			result.add(new ReferencesUpdater(project, this));
		}

		return result;
	}

	@Override
	public Project getProjectWithId(String projectId) {
		if (projects.containsKey(projectId)) {
			return new ReferencesUpdater(projects.get(projectId), this);
		}
		return null;
	}

	@Override
	public Versionable getAllProjects() {
		return new MultiProjectVersionable(listAllProjects());
	}

	@Override
	public Versionable getAllProjectsWithMatchingIdPattern(String projectIdPattern) {
		Set<Project> result = new LinkedHashSet<Project>();
		Pattern pattern = Pattern.compile(projectIdPattern);

		for (Project project : listAllProjects()) {
			if (pattern.matcher(project.id()).matches()) {
				result.add(project);
			}
		}

		return new MultiProjectVersionable(result);
	}

	@Override
	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion) {
		for (Project project : listAllProjects()) {
			project.updateReferencesFor(id, oldVersion, newVersion);
		}
	}

}
