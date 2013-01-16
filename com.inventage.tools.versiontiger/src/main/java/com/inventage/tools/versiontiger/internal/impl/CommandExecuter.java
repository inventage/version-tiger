package com.inventage.tools.versiontiger.internal.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.Versionable;
import com.inventage.tools.versiontiger.Versioning;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;
import com.inventage.tools.versiontiger.util.FileHandler;

class CommandExecuter {
	private static final String COMMAND_EXECUTER_UNIVERSE_ID = "commandExecuterUniverse";

	private final Versioning versioning;
	private final ProjectUniverse universe;
	private final String rootPath;

	CommandExecuter(Versioning versioning, String rootPath, VersioningLogger logger) {
		this.versioning = versioning;
		this.rootPath = rootPath;
		universe = versioning.createUniverse(COMMAND_EXECUTER_UNIVERSE_ID, null, rootPath, logger);
	}

	/**
	 * Executes the line oriented versioning tool commands provided by the
	 * {@link BufferedReader} passed as arguments. Writes back output and error
	 * messages to the {@link BufferedWriter}. It is assumed that the caller -
	 * which provides the reader and writers - closes them by itself.
	 */
	void executeCommands(BufferedReader commandReader, VersioningLogger logger) throws IOException {
		String line;
		while ((line = commandReader.readLine()) != null) {
			if (!line.trim().isEmpty() && !line.startsWith("#")) {
				String commandLine = line.trim();
				execute(commandLine, logger);
			}
		}
	}

	void execute(String commandLine, VersioningLogger logger) {
		Command command = parse(commandLine, logger);
		command.execute();
	}

	private Command parse(String commandLine, VersioningLogger logger) {
		List<String> splitted = new ArrayList<String>(Arrays.asList(commandLine.split("\\s+")));
		String name = splitted.remove(0);

		return new Command(name, splitted, universe, logger);
	}

	private class Command {

		private final String PROPERTY_NAME_VERSION = "version:";
		private final String PROPERTY_NAME_OSGIVERSION = "osgiVersion:";
		private final String PROPERTY_NAME_PROPERTY = "property:";
		private final Pattern PATTERN_PROPERTY = Pattern.compile("\\$\\{(.+?)\\}");

		private final String name;
		private final List<String> arguments;
		private final ProjectUniverse universe;

		private final VersioningLogger logger;

		Command(String name, List<String> arguments, ProjectUniverse universe, VersioningLogger logger) {
			this.name = name;
			this.arguments = arguments;
			this.universe = universe;
			this.logger = logger;
		}

		void execute() {
			replacePropertiesInArguments();

			if ("project".equals(name)) {
				addProject(arguments.get(0));
			} else if ("version".equals(name)) {
				version(arguments.get(0), arguments.get(1));
			} else if ("incrementMajor".equals(name)) {
				incrementMajor(arguments.get(0));
			} else if ("incrementMinor".equals(name)) {
				incrementMinor(arguments.get(0));
			} else if ("incrementBugfix".equals(name)) {
				incrementBugfix(arguments.get(0));
			} else if ("release".equals(name)) {
				useReleaseVersion(arguments.get(0));
			} else if ("snapshot".equals(name)) {
				useSnapshotVersion(arguments.get(0));
			} else if ("updateReferences".equals(name)) {
				updateReferencesFor(arguments.get(0), arguments.get(1));
			} else if ("include".equals(name)) {
				includeCommandsFile(arguments.get(0));
			} else if ("list".equals(name)) {
				listProjects();
			} else if ("property".equals(name)) {
				setProperty(arguments.get(0), arguments.get(1), arguments.get(2));
			} else if ("help".equals(name) || "?".equals(name)) {
				printUsage();
			} else {
				logError("Unknown command: " + name);
				printUsage();
			}
		}

		private void listProjects() {
			List<Project> projects = new ArrayList<Project>(universe.listAllProjects());
			Collections.sort(projects);

			for (Project project : projects) {
				System.out.println(project.id());
			}
		}

		private void includeCommandsFile(String pathname) {
			File commandsFilePath = new FileHandler().createFileFromPath(rootPath, pathname);
			try {
				executeCommands(new BufferedReader(new FileReader(commandsFilePath)), logger);
			} catch (FileNotFoundException e) {
				logError("File not found: " + commandsFilePath.getAbsolutePath());
			} catch (IOException e) {
				logError("Cannot read commands from file: " + commandsFilePath.getAbsolutePath());
			}
		}
		
		void addProject(String projectPath) {
			Project project = universe.addProjectPath(projectPath);
			if (project != null) {
				logSuccess("Added project: " + project.id());
			}
		}

		void version(String projectId, String newVersion) {
			Versionable versionable = universe.getAllProjectsWithMatchingIdPattern(projectId);
			versionable.setVersion(versioning.createMavenVersion(newVersion));
		}

		void updateReferencesFor(String projectId, String newMavenVersion) {
			universe.updateReferencesFor(projectId, null, versioning.createMavenVersion(newMavenVersion));
		}

		void incrementMajor(String projectId) {
			Versionable versionable = universe.getAllProjectsWithMatchingIdPattern(projectId);
			versionable.incrementMajorVersionAndSnapshot();
		}

		void incrementMinor(String projectId) {
			Versionable versionable = universe.getAllProjectsWithMatchingIdPattern(projectId);
			versionable.incrementMinorVersionAndSnapshot();
		}

		void incrementBugfix(String projectId) {
			Versionable versionable = universe.getAllProjectsWithMatchingIdPattern(projectId);
			versionable.incrementBugfixVersionAndSnapshot();
		}

		void useReleaseVersion(String projectId) {
			Versionable versionable = universe.getAllProjectsWithMatchingIdPattern(projectId);
			versionable.useReleaseVersion();
		}

		void useSnapshotVersion(String projectId) {
			Versionable versionable = universe.getAllProjectsWithMatchingIdPattern(projectId);
			versionable.useSnapshotVersion();
		}

		void setProperty(String projectId, String key, String value) {
			Project project = universe.getProjectWithId(projectId);
			project.setProperty(key, value);
		}

		private void replacePropertiesInArguments() {
			for (int i = 0; i < arguments.size(); i++) {
				arguments.set(i, processProperties(arguments.get(i)));
			}
		}

		private String processProperties(String argument) {
			Matcher matcher = PATTERN_PROPERTY.matcher(argument);

			StringBuffer result = new StringBuffer();
			while (matcher.find()) {
				String propertyName = matcher.group(1);
				matcher.appendReplacement(result, evaluateProperty(propertyName));
			}
			matcher.appendTail(result);

			return result.toString();
		}

		String evaluateProperty(String propertyName) {
			if (propertyName.startsWith(PROPERTY_NAME_VERSION)) {
				return getProjectVersion(propertyName.substring(PROPERTY_NAME_VERSION.length())).toString();
			} else if (propertyName.startsWith(PROPERTY_NAME_OSGIVERSION)) {
				MavenVersion projectVersion = getProjectVersion(propertyName.substring(PROPERTY_NAME_OSGIVERSION.length()));
				return versioning.createOsgiVersion(projectVersion).toString();
			} else if (propertyName.startsWith(PROPERTY_NAME_PROPERTY)) {
				String projectId = propertyName.substring(PROPERTY_NAME_PROPERTY.length(), propertyName.indexOf(":", PROPERTY_NAME_PROPERTY.length()));
				String key = propertyName.substring(PROPERTY_NAME_PROPERTY.length() + projectId.length() + 1);

				String value = getProjectProperty(projectId, key);
				return value != null ? value : "";
			}

			return "${" + propertyName + "}";
		}

		private MavenVersion getProjectVersion(String projectId) {
			return universe.getProjectWithId(projectId).getVersion();
		}

		private String getProjectProperty(String projectId, String key) {
			Project project = universe.getProjectWithId(projectId);
			return project != null ? project.getProperty(key) : null;
		}
		
		private void printUsage() {
			logError("Usage:");
			logError("    project /path/to/directory");
			logError("    version my.artifact.id-pattern 1.2.3-SNAPSHOT");
			logError("    incrementMajor my.artifact.id-pattern");
			logError("    incrementMinor my.artifact.id-pattern");
			logError("    incrementBugfix my.artifact.id-pattern");
			logError("    release my.artifact.id-pattern");
			logError("    snapshot my.artifact.id-pattern");
			logError("    updateReferences my.artifact.id 1.2.3-SNAPSHOT");
			logError("    include /path/to/commandsfile");
			logError("    property project-id key value");
			logError("    list");
			logError("Available properties in arguments: ${version:my.artifact.id}, ${osgiVersion:my.artifact.id}");
		}
		
		private void logSuccess(String line) {
			log(line, VersioningLoggerStatus.SUCCESS);
		}

		private void logError(String line) {
			log(line, VersioningLoggerStatus.ERROR);
		}
		
		private void log(String line, VersioningLoggerStatus status) {
			VersioningLoggerItem item = logger.createVersioningLoggerItem();
			item.appendToMessage(line);
			item.setStatus(status);
			logger.addVersioningLoggerItem(item);
		}
	}
}
