package com.inventage.tools.versiontiger;

public interface Project extends Versionable, Comparable<Project>, PropertyHolder {

	String id();

	String projectPath();

	MavenVersion getVersion();

	void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse);

	boolean isVersionInherited();

}
