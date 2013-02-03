package com.inventage.tools.versiontiger.universedefinition;

import java.util.Set;

import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;

public interface UniverseDefinitions {

	String ALL_WORKSPACE_PROJECTS_UNIVERSE_ID = "allWorkspaceProjectsUniverse";
	String MAVEN_ROOT_PROJECTS_UNIVERSE_ID = "mavenRootProjectsUniverse";

	Set<ProjectUniverse> getProjectUniverses(VersioningLogger logger);
}
