package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;

import com.inventage.tools.versiontiger.MavenProject;
import com.inventage.tools.versiontiger.VersioningLogger;

abstract class AbstractEclipseProject extends MavenProjectImpl {

	AbstractEclipseProject(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		super(projectPath, logger, versionFactory);
	}

	private MavenProject parentProject;
	
	protected MavenProject getParentProject() {
		return parentProject;
	}

	public void setParentProject(MavenProject parentProject) {
		this.parentProject = parentProject;
	}
	
	protected boolean isPomLess() {
		return parentProject != null;
	}
	
	protected File getPomXmlFile() {
		return (isPomLess() ? null : super.getPomXmlFile());
	}

	protected String getPomContent() {
		if (pomContent == null && isPomLess()) {
			pomContent = synthesisePom();
		}
		return super.getPomContent();
	}
	
	protected String synthesisePom() {
		return
				"<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "\txsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
				+ "\t<modelVersion>4.0.0</modelVersion>\n"
				+ "\t<artifactId>" + getProjectId() + "</artifactId>\n"
				+ "\t<packaging>" + getEclipsePackagingName() + "</packaging>\n"
				+ "\n"
				+ "\t<parent>\n"
				+ "\t\t<groupId>" + getGroupId() + "</groupId>\n"
				+ "\t\t<artifactId>" + getParentArtifactId() + "</artifactId>\n"
				+ "\t\t<version>" + getParentProject().getVersion() + "</version>\n"
				+ "\t\t<relativePath>..</relativePath>"
				+ "\t</parent>\n"
				+ "</project>\n"
				;
	}

	protected abstract String getEclipsePackagingName();
	
	protected abstract String getProjectId();
	
	protected String getParentArtifactId() {
		return getParentProject().getProperty("artifactId");
	}

	protected String getGroupId() {
		return getParentProject().getProperty("groupId");
	}
}
