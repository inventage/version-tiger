package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.inventage.tools.versiontiger.MavenProject;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

class ProjectFactory {
	
	private final String rootPath;
	
	private VersionFactory versionFactory;

	ProjectFactory(String rootPath, VersionFactory versionFactory) {
		this.rootPath = rootPath;
		this.versionFactory = versionFactory;
	}

	MavenProject createProjectFromRootFilePath(String projectPath, VersioningLogger logger) {
		
		File projectFile = new FileHandler().createFileFromPath(rootPath, projectPath);
		if (!projectFile.exists()) {
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
		if (!projectPathFile.exists() || !projectPathFile.isDirectory()) {
			throw new IllegalStateException("Directory does not exist: " + projectPathFile);
		}
		
		Set<MavenProject> result = new HashSet<MavenProject>();
		Set<String> visitedDirectories = new HashSet<String>();
		
		if (projectPathFile.canExecute() && projectPathFile.canRead()) {
			addMavenProjectsUnderRoot(projectPathFile, visitedDirectories, result, logger);
		}
		
		return result;
	}
	
	private void addMavenProjectsUnderRoot(File rootPathFile, Set<String> visitedDirectories, Set<MavenProject> foundProjects, VersioningLogger logger) {
		String currentCanonicalPath = new FileHandler().getCanonicalPath(rootPathFile);
		
		visitedDirectories.add(currentCanonicalPath);
		
		try {
			if (hasPom(rootPathFile)) {
				MavenProject project = createProjectFromRootFilePath(currentCanonicalPath, logger);
				foundProjects.add(project);
				
				if (cannotHaveSubProjects(rootPathFile)) {
					return;
				}
			}
		}
		catch(Exception e) {
			logCannotAddFileWarning(logger, currentCanonicalPath, e);
			return;
		}
		
		addSubProjects(rootPathFile, visitedDirectories, foundProjects, logger);
	}

	private boolean cannotHaveSubProjects(File rootPathFile) {
		return !getMavenPackaging(rootPathFile).toLowerCase().equals("pom");
	}

	private void addSubProjects(File rootPathFile, Set<String> visitedDirectories, Set<MavenProject> foundProjects, VersioningLogger logger) {
		for (File subElement: rootPathFile.listFiles()) {
			boolean isDirectory = subElement.isDirectory();
			boolean isVisible = !subElement.isHidden();
			boolean isReadable = subElement.canExecute() && subElement.canRead();
			boolean notYetVisited = notYetVisited(visitedDirectories, subElement, logger);
			
			if (isDirectory && isVisible && isReadable && notYetVisited) {
				addMavenProjectsUnderRoot(subElement, visitedDirectories, foundProjects, logger);
			}
		}
	}

	private boolean notYetVisited(Set<String> visitedDirectories, File subElement, VersioningLogger logger) {
		try {
			return !visitedDirectories.contains(subElement.getCanonicalPath());
		} catch (IOException e) {
			logCannotAddFileWarning(logger, subElement.getAbsolutePath(), e);
			return false;
		}
	}
	
	private void logCannotAddFileWarning(VersioningLogger logger, String path, Exception e) {
		logWarning(logger, "Could not add project " + path + ", reason: " + e.getMessage());
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
	
	private boolean hasPom(File projectPath) {
		return new File(projectPath, "pom.xml").exists();
	}

	private String getMavenPackaging(File projectPath) {
		String fileContent = new FileHandler().readFileContent(new File(projectPath, "pom.xml"));
		String packagingType = new XmlHandler().readElement(fileContent, "project/packaging");
		return packagingType != null ? packagingType : "pom";
	}

}
