package com.inventage.tools.versiontiger;

import java.util.Set;

public interface ProjectUniverse {

	String id();

	String name();

	Project addProjectPath(String projectRootFilePath);
	
	Set<Project> addRootProjectPath(String projectRootFilePath);

	String idForProjectPath(String projectPath);

	void removeProject(Project versioningProject);
	
	void clearProjects();

	Project getProjectWithId(String projectId);

	Versionable getAllProjects();

	Set<Project> listAllProjects();

	Versionable getAllProjectsWithMatchingIdPattern(String projectIdPattern);

	void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion);

}
