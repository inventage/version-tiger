package com.inventage.tools.versiontiger;

public interface Project extends Versionable, Comparable<Project>, PropertyHolder {

	ProjectId id();

	String projectPath();

	MavenVersion getVersion();

	void updateReferencesFor(ProjectId id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse);

	boolean isVersionInherited();

	boolean exists();

	boolean ensureStrictOsgiDependencyTo(ProjectId projectId);

}
