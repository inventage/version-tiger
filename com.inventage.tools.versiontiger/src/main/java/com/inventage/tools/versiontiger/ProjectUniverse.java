package com.inventage.tools.versiontiger;

import java.util.Set;

public interface ProjectUniverse {

	String id();

	String name();

	Project createProjectFromPath(String projectRootFilePath);
	
	void addProject(Project project);
	
	Project addProjectPath(String projectRootFilePath);
	
	Set<Project> addRootProjectPath(String projectRootFilePath);

	String idForProjectPath(String projectPath);

	Set<Project> removeProjectsInPath(String path);
	
	void removeProject(String projectId);

	Project getProjectWithId(String projectId);

	Versionable getAllProjects();

	Set<Project> listAllProjects();

	Versionable getAllProjectsWithMatchingIdPattern(String projectIdPattern);

	void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion);

	boolean ensureStrictOsgiDependencyTo(String argument);

}
