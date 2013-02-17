package com.inventage.tools.versiontiger.internal.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.inventage.tools.versiontiger.MavenProject;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;
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
			logWarning(logger, "Ignoring unknown project type: " + projectFile);
			return null;
		}
	}
	
	Set<MavenProject> createRecursiveProjectsFromRootFilePath(String projectPath, VersioningLogger logger) {
		
		File projectPathFile = new FileHandler().createFileFromPath(rootPath, projectPath);
		if (!exists(projectPathFile)) {
			throw new IllegalStateException("Directory does not exist: " + projectPathFile);
		}
		
		Set<MavenProject> result = new HashSet<MavenProject>();
		Set<String> visitedDirectories = new HashSet<String>();
		
		if (projectPathFile.canExecute() && projectPathFile.canRead()) {
			addMavenProjectsUnderRoot(projectPathFile, visitedDirectories, result, logger);
		}
		
		visitedDirectories.clear();
		
		return result;
	}
	
	private void addMavenProjectsUnderRoot(File rootPathFile, Set<String> visitedDirectories, Set<MavenProject> foundProjects, VersioningLogger logger) {
		
		String currentCanonicalPath;
		try {
			currentCanonicalPath = rootPathFile.getCanonicalPath();
		}
		catch (IOException e) {
			logFileWarning(logger, rootPathFile.getAbsolutePath(), e);
			return;
		}
		
		visitedDirectories.add(currentCanonicalPath);
		
		try {
			if (hasPom(rootPathFile)) {
				foundProjects.add(createProjectFromRootFilePath(currentCanonicalPath, logger));
				
				/* Check if this pom file has a packaging and if it is different than 'pom'. If this is the case, return. Otherwise recurse further. */
				String mavenPackaging = getMavenPackaging(rootPathFile);
				
				if (mavenPackaging != null && !"pom".equals(mavenPackaging.toLowerCase())) {
					return;
				}
			}
		}
		catch(IllegalStateException e) {
			// If we can't parse the file, we add a log entry and go on with adding sub elements.
			logFileWarning(logger, currentCanonicalPath, e);
		}
		
		for (File subElement: rootPathFile.listFiles()) {
			try {
				boolean isDirectory = subElement.isDirectory();
				boolean isVisible = !subElement.isHidden();
				boolean isReadable = subElement.canExecute() && subElement.canRead();
				boolean notYetVisited = !visitedDirectories.contains(subElement.getCanonicalPath());
				
				if (isDirectory && isVisible && isReadable && notYetVisited) {
					addMavenProjectsUnderRoot(subElement, visitedDirectories, foundProjects, logger);
				}
			}
			catch (IOException e) {
				/* If there was a problem with determining the canonical path of one of the directory entries, 
				 * we give a warning and don't process it further. */
				logFileWarning(logger, subElement.getAbsolutePath(), e);
			}
		}
	}
	
	private void logFileWarning(VersioningLogger logger, String path, Exception e) {
		logWarning(logger, "Did not add: " + path + " - Message: " + e.getMessage());
	}
	
	private void logWarning(VersioningLogger logger, String message) {
		VersioningLoggerItem item = logger.createVersioningLoggerItem();
		item.appendToMessage(message);
		item.setStatus(VersioningLoggerStatus.WARNING);
		logger.addVersioningLoggerItem(item);
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
