package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.Versionable;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;
import com.inventage.tools.versiontiger.util.FileHandler;

class ProjectUniverseImpl implements ProjectUniverse {

	private final Map<String, Project> projects = new TreeMap<String, Project>();
	private final String id;
	private final String name;
	private final ProjectFactory projectFactory;
	private final VersioningLogger logger;
	
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
	public Project createProjectFromPath(String projectRootFilePath) {
		return projectFactory.createProjectFromRootFilePath(projectRootFilePath, logger);
	}
	
	@Override
	public void addProject(Project project) {
		if (project != null) {
			projects.put(project.id(), project);
		}
	}
	
	@Override
	public Project addProjectPath(String projectRootFilePath) {
		Project project = projectFactory.createProjectFromRootFilePath(projectRootFilePath, logger);

		if (project != null && project.exists()) {
			if (projects.containsKey(project.id())) {
				VersioningLoggerItem logItem = logger.createVersioningLoggerItem();
				logItem.setStatus(VersioningLoggerStatus.WARNING);
				Project existingProject = projects.get(project.id());
				logItem.setProject(existingProject);
				logItem.appendToMessage("Removing project from universe (id conflict): " + existingProject.projectPath());
				logger.addVersioningLoggerItem(logItem);
			}
		
			projects.put(project.id(), project);
		}

		return project;
	}
	
	@Override
	public Set<Project> addRootProjectPath(String projectRootFilePath) {
		Set<Project> recursiveProjects = projectFactory.createRecursiveProjectsFromRootFilePath(projectRootFilePath, logger);
		Set<Project> result = new HashSet<Project>();
		
		for (Project project: recursiveProjects) {
			projects.put(project.id(), project);
			result.add(project);
		}
		
		return result;
	}

	@Override
	public String idForProjectPath(String projectPath) {
		File canonicalProjectPath = new FileHandler().getCanonicalFile(new File(projectPath));
		
		for (Project project : projects.values()) {
			if (canonicalProjectPath.equals(new File(project.projectPath()))) {
				return project.id();
			}
		}
		return null;
	}

	@Override
	public void removeProject(String projectId) {
		if (projectId != null) {
			projects.remove(projectId);
		}
	}
	
	@Override
	public Set<Project> removeProjectsInPath(String path) {
		FileHandler fileHandler = new FileHandler();
		String matcherPath = fileHandler.getCanonicalPath(new File(path));
		
		Set<Project> result = new HashSet<Project>();
		for (Project project : new ArrayList<Project>(projects.values())) {
			String currentPath = fileHandler.getCanonicalPath(new File(project.projectPath()));
			if (currentPath.startsWith(matcherPath)) {
				projects.remove(project.id());
				result.add(project);
				
				VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();
				loggerItem.setStatus(VersioningLoggerStatus.SUCCESS);
				loggerItem.appendToMessage("Removed project: " + project.id());
				logger.addVersioningLoggerItem(loggerItem);
			}
		}
		
		return result;
	}

	public Set<Project> listAllProjects() {
		Set<Project> result = new LinkedHashSet<Project>();

		for (Project project : projects.values()) {
			result.add(new ReferencesUpdater(project, this, logger));
		}

		return result;
	}

	@Override
	public Project getProjectWithId(String projectId) {
		if (projects.containsKey(projectId)) {
			return new ReferencesUpdater(projects.get(projectId), this, logger);
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
			project.updateReferencesFor(id, oldVersion, newVersion, this);
		}
	}

}
