package com.inventage.tools.versiontiger.internal.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.inventage.tools.versiontiger.MavenProject;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.util.FileHandler;

import de.pdark.decentxml.Document;
import de.pdark.decentxml.Element;
import de.pdark.decentxml.XMLParseException;
import de.pdark.decentxml.XMLParser;
import de.pdark.decentxml.XMLStringBuilderSource;

class ProjectFactory {
	
	private final String rootPath;
	
	private VersionFactory versionFactory;

	ProjectFactory(String rootPath, VersionFactory versionFactory) {
		this.rootPath = rootPath;
		this.versionFactory = versionFactory;
	}

	MavenProject createProjectFromRootFilePath(String projectPath, VersioningLogger logger) {
		
		File projectFile = new FileHandler().createFileFromPath(rootPath, projectPath);
		if (!exists(projectFile)) {
			throw new IllegalStateException("Project does not exist: " + projectFile);
		}

		if (hasPom(projectFile)) {
			String packagingType = getMavenPackaging(projectFile);

			if ("eclipse-plugin".equals(packagingType) || "eclipse-test-plugin".equals(packagingType)) {
				return createPluginProject(projectFile, logger);
			} else if ("eclipse-feature".equals(packagingType)) {
				return createFeatureProject(projectFile, logger);
			} else if ("eclipse-repository".equals(packagingType)) {
				return createRepositoryProject(projectFile, logger);
			} else if ("eclipse-update-site".equals(packagingType)) {
				return createUpdatesiteProject(projectFile, logger);
			} else if ("eclipse-application".equals(packagingType)) {
				return createApplicationProject(projectFile, logger);
			}

			return createAnyMavenProject(projectFile, logger);
		} else {
			System.out.println("Ignoring unknown project type: " + projectFile);
			return null;
		}
	}

	private MavenProject createPluginProject(File projectPath, VersioningLogger logger) {
		return new EclipsePlugin(projectPath.getAbsolutePath(), logger, versionFactory);
	}

	private MavenProject createFeatureProject(File projectPath, VersioningLogger logger) {
		return new EclipseFeature(projectPath.getAbsolutePath(), logger, versionFactory);
	}

	private MavenProject createRepositoryProject(File projectPath, VersioningLogger logger) {
		return new EclipseRepository(projectPath.getAbsolutePath(), logger, versionFactory);
	}

	private MavenProject createUpdatesiteProject(File projectPath, VersioningLogger logger) {
		return new EclipseUpdateSite(projectPath.getAbsolutePath(), logger, versionFactory);
	}

	private MavenProject createApplicationProject(File projectPath, VersioningLogger logger) {
		return new EclipseApplication(projectPath.getAbsolutePath(), logger, versionFactory);
	}

	private MavenProject createAnyMavenProject(File projectPath, VersioningLogger logger) {
		return new MavenProjectImpl(projectPath.getAbsolutePath(), logger, versionFactory);
	}
	
	private boolean exists(File projectPath) {
		return projectPath.exists();
	}

	private boolean hasPom(File projectPath) {
		return new File(projectPath, "pom.xml").exists();
	}

	private String getMavenPackaging(File projectPath) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(projectPath, "pom.xml")));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}

			bufferedReader.close();

			Document doc = new XMLParser().parse(new XMLStringBuilderSource(sb));
			Element packagingType = doc.getChild("project/packaging");
			return packagingType != null ? packagingType.getText() : null;
			
		} 
		catch (XMLParseException e) {
			throw new IllegalStateException(e.getMessage() + ". Affected project: " + projectPath, e);
		} 
		catch (FileNotFoundException e) {
			throw new IllegalStateException(e.getMessage() + ". Affected project: " + projectPath, e);
		} 
		catch (IOException e) {
			throw new IllegalStateException(e.getMessage() + ". Affected project: " + projectPath, e);
		}
	}

}
