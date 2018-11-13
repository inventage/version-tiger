package com.inventage.tools.versiontiger.internal.impl;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy;
import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.Version;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;
import com.inventage.tools.versiontiger.Versionable;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

/**
 * Denotes the allowed batch operations of the version tiger.
 * 
 * @author nw
 */
public enum VersionTigerBatchOperation {
	
	PROJECT(1, "project <path>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Project project = commandExecuter.getUniverse().addProjectPath(command.getArgument(0));
			if (project != null && project.exists()) {
				logSuccess(commandExecuter.getLogger(), "Added project: " + project.id());
			}
		}
		
	},
	PROJECTROOT(1, "projectRoot <path>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Set<Project> projects = commandExecuter.getUniverse().addRootProjectPath(command.getArgument(0));
			for (Project p : new TreeSet<Project>(projects)) {
				logSuccess(commandExecuter.getLogger(), "Added project: " + p.id());
			}
			logMessage(commandExecuter.getLogger(), "Summary: Added " + projects.size() + " projects.");
		}
	},
	IGNOREPROJECT(1, "ignoreProject <my.project.id>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			String projectId = command.getArgument(0);
			commandExecuter.getUniverse().removeProject(projectId);
			logSuccess(commandExecuter.getLogger(), "Ignored project: " + projectId);
		}
	},
	IGNOREPATH(1, "ignorePath <path>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			String path = command.getArgument(0);
			Set<Project> removedProjects = commandExecuter.getUniverse().removeProjectsInPath(path);
			logSuccess(commandExecuter.getLogger(), "Ignored " + removedProjects.size() + " projects in: " + path);
		}
	},
	VERSION(2, "version <my.project.id-pattern> <1.2.3-SNAPSHOT>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable versionable = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			versionable.setVersion(commandExecuter.getVersioning().createMavenVersion(command.getArgument(1)));
		}
	},
	INCREMENTMAJOR(1, "incrementMajor <my.project.id-pattern>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable versionable = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			versionable.incrementMajorVersionAndSnapshot();
		}
	},
	INCREMENTMINOR(1, "incrementMinor <my.project.id-pattern>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable versionable = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			versionable.incrementMinorVersionAndSnapshot();
		}
	},
	INCREMENTBUGFIX(1, "incrementBugfix <my.project.id-pattern>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable versionable = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			versionable.incrementBugfixVersionAndSnapshot();
		}
	},
	RELEASE(1, "release <my.project.id-pattern>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable versionable = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			versionable.useReleaseVersion();
		}
	},
	SNAPSHOT(1, "snapshot <my.project.id-pattern>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable versionable = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			versionable.useSnapshotVersion();
		}
	},
	RELEASEWITHSUFFIX(2, "releaseWithSuffix <my.project.id-pattern> <suffix>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable versionable = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			versionable.useReleaseVersionWithSuffix(command.getArgument(1));
		}
	},
	UPDATEREFERENCES(2, "updateReferences <my.project.id> <1.2.3-SNAPSHOT>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			commandExecuter.getUniverse().updateReferencesFor(command.getArgument(0), null, commandExecuter.getVersioning().createMavenVersion(command.getArgument(1)));
		}
	},
	INCLUDE(1, "include </path/to/commandsfile>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			new FileExecution().execute(command.getArgument(0), commandExecuter);
		}
	},
	LIST(0, "list") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			List<Project> projects = new ArrayList<Project>(commandExecuter.getUniverse().listAllProjects());
			Collections.sort(projects);

			for (Project project : projects) {
				logMessage(commandExecuter.getLogger(), project.id().toString());
			}
		}
	},
	PROPERTY(3, "property <project-id> <key> <value>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Project project = commandExecuter.getUniverse().getProjectWithId(command.getArgument(0));
			if (project == null) {
				logError(commandExecuter.getLogger(), "Project not found: " + command.getArgument(0));
				return;
			}
			
			project.setProperty(command.getArgument(1), command.getArgument(2));
		}
	},
	SETTING(2, "setting <key> <value>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			
			VersioningLogger logger = commandExecuter.getLogger();
			
			String key = command.getArgument(0);
			String value = command.getArgument(1);
			
			/* Perform operations according to configuration. */
			if ("osgi.version.mapping".equals(key)) {
				
				commandExecuter.getVersioning().setMavenToOsgiVersionMappingStrategy(MavenToOsgiVersionMappingStrategy.create(value));
				logSuccess(logger, "Updated setting: " + key + " " + value);
			}
			else if ("osgi.release.qualifier".equals(key)) {
				
				commandExecuter.getVersioning().setOsgiReleaseQualifier((String) value);
				logSuccess(logger, "Updated setting: " + key + " " + value);
			}
			else if ("osgi.snapshot.qualifier".equals(key)) {
				
				commandExecuter.getVersioning().setOsgiSnapshotQualifier((String) value);
				logSuccess(logger, "Updated setting: " + key + " " + value);
			}
			else if ("dependency.range.change".equals(key)) {
				
				commandExecuter.getVersioning().setVersionRangeChangeStrategy(VersionRangeChangeStrategy.create(value));
				logSuccess(logger, "Updated setting: " + key + " " + value);
			}
			else {
				logError(logger, "Unknown setting: " + key);
			}
		}
	},
	ENSUREVERSION(1, "ensureVersion 1.3") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			MavenVersion requiredVersion = commandExecuter.getVersioning().createMavenVersion(command.getArgument(0));
			
			Version currentVersion = commandExecuter.getVersioning().createMavenVersion(getVersionTigerVersion());
			if (currentVersion.isLowerThan(requiredVersion, false)) {
				logError(commandExecuter.getLogger(), "Versiontiger version too old, required: " + requiredVersion + ", current: " + currentVersion);
				commandExecuter.failAndQuit();
			}
			else {
				logSuccess(commandExecuter.getLogger(), "Version tiger version ok (required: " + requiredVersion + ", current: " + currentVersion + ")");
			}
		}
	},
	ENSURESNAPSHOT(1, "ensureSnapshot <my.project.id-pattern>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable projects = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			if (projects.ensureIsSnapshot()) {
				logSuccess(commandExecuter.getLogger(), "All matched projects are snapshot versions.");
			}
			else {
				logError(commandExecuter.getLogger(), "Not all matched projects are snapshot versions.");
				commandExecuter.failAndQuit();
			}
		}
	},
	ENSURERELEASE(1, "ensureRelease <my.project.id-pattern>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			Versionable projects = commandExecuter.getUniverse().getAllProjectsWithMatchingIdPattern(command.getArgument(0));
			if (projects.ensureIsRelease()) {
				logSuccess(commandExecuter.getLogger(), "All matched projects are release versions.");
			}
			else {
				logError(commandExecuter.getLogger(), "Not all matched projects are release versions.");
				commandExecuter.failAndQuit();
			}
		}
	},
	ENSURESTRICTOSGIDEPENDENCYTO(1, "ensureStrictOsgiDependencyTo <project-id>") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			if (commandExecuter.getUniverse().ensureStrictOsgiDependencyTo(command.getArgument(0))) {
				logSuccess(commandExecuter.getLogger(), "All dependencies to '" + command.getArgument(0) + "' are strict.");
			}
			else {
				logError(commandExecuter.getLogger(), "Not all dependencies to '" + command.getArgument(0) + "' are strict.");
				commandExecuter.failAndQuit();
			}
		}
	},
	HELP(0, "help") {
		@Override
		void internalExecute(CommandExecuter commandExecuter, Command command) {
			printUsage(commandExecuter.getLogger());
		}
	};
	
	public int argumentCount;
	private String usage;
	
	private static final String PROPERTY_NAME_VERSION = "version:";
	private static final String PROPERTY_NAME_OSGIVERSION = "osgiVersion:";
	private static final String PROPERTY_NAME_PROPERTY = "property:";
	private static final Pattern PATTERN_PROPERTY = Pattern.compile("\\$\\{(.+?)\\}");
	
	private VersionTigerBatchOperation(int argumentCount, String usage) {
		this.argumentCount = argumentCount;
		this.usage = usage;
	}
	
	void execute(CommandExecuter commandExecuter, Command command) {
		VersioningLogger logger = commandExecuter.getLogger();
		
		if (!argumentsMatch(command)) {
			logError(logger, "Wrong number of arguments. Usage: " + usage);
			return;
		}
		
		try {
			replacePropertiesInArguments(commandExecuter, command);
		
			internalExecute(commandExecuter, command);
		}
		catch (Exception e) {
			logError(commandExecuter.getLogger(), "Cannot execute command (" + command.getOriginalLine() + "). Reason: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	};
	
	private boolean argumentsMatch(Command command) {
		return command.getArgumentCount() == argumentCount;
	}
	
	abstract void internalExecute(CommandExecuter commandExecuter, Command command);
	
	private static void logSuccess(VersioningLogger logger, String line) {
		log(logger, line, VersioningLoggerStatus.SUCCESS);
	}
	
	private static void logError(VersioningLogger logger, String line) {
		log(logger, line, VersioningLoggerStatus.ERROR);
	}
	
	public static void logUnknownCommand(Command command, VersioningLogger logger) {
		logError(logger, "Unknown command: " + command.getOriginalLine() + " -- Use 'help' for a list of commands.");
	}
	
	private static void logMessage(VersioningLogger logger, String line) {
		log(logger, line, VersioningLoggerStatus.MESSAGE);
	}
	
	private static void log(VersioningLogger logger, String line, VersioningLoggerStatus status) {
		VersioningLoggerItem item = logger.createVersioningLoggerItem();
		item.appendToMessage(line);
		item.setStatus(status);
		logger.addVersioningLoggerItem(item);
	}

	private void replacePropertiesInArguments(CommandExecuter commandExecuter, Command command) {
		for (int i = 0; i < command.getArguments().size(); i++) {
			command.getArguments().set(i, processProperties(commandExecuter, command.getArguments().get(i)));
		}
	}

	private String processProperties(CommandExecuter commandExecuter, String argument) {
		Matcher matcher = PATTERN_PROPERTY.matcher(argument);

		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String propertyName = matcher.group(1);
			matcher.appendReplacement(result, escapeGroupReferences(evaluateProperty(commandExecuter, propertyName)));
		}
		matcher.appendTail(result);

		return result.toString();
	}
	
	private String escapeGroupReferences(String replacement) {
		return replacement.replace("$", "\\$");
	}

	private String evaluateProperty(CommandExecuter commandExecuter, String propertyName) {
		if (propertyName.startsWith(PROPERTY_NAME_VERSION)) {
			return getProjectVersion(commandExecuter, propertyName.substring(PROPERTY_NAME_VERSION.length())).toString();
		} else if (propertyName.startsWith(PROPERTY_NAME_OSGIVERSION)) {
			MavenVersion projectVersion = getProjectVersion(commandExecuter, propertyName.substring(PROPERTY_NAME_OSGIVERSION.length()));
			return commandExecuter.getVersioning().createOsgiVersion(projectVersion).toString();
		} else if (propertyName.startsWith(PROPERTY_NAME_PROPERTY)) {
			String projectId = propertyName.substring(PROPERTY_NAME_PROPERTY.length(), propertyName.indexOf(":", PROPERTY_NAME_PROPERTY.length()));
			String key = propertyName.substring(PROPERTY_NAME_PROPERTY.length() + projectId.length() + 1);

			String value = getProjectProperty(commandExecuter, projectId, key);
			return value != null ? value : "";
		}
		else {
			String environmentValue = System.getenv(propertyName);
			if (environmentValue != null) {
				return environmentValue;
			}
		}

		return "${" + propertyName + "}";
	}

	private MavenVersion getProjectVersion(CommandExecuter commandExecuter, String projectId) {
		Project project = commandExecuter.getUniverse().getProjectWithId(projectId);
		if (project == null) {
			throw new IllegalStateException("Unknown project: " + projectId);
		}
		return project.getVersion();
	}

	private String getProjectProperty(CommandExecuter commandExecuter, String projectId, String key) {
		Project project = commandExecuter.getUniverse().getProjectWithId(projectId);
		return project != null ? project.getProperty(key) : null;
	}
	
	private static void printUsage(VersioningLogger logger) {
		logMessage(logger, "Usage:");
		for (VersionTigerBatchOperation op: VersionTigerBatchOperation.values()) {
			logMessage(logger, "  " + op.usage);
		}
		logMessage(logger, "Available properties in arguments: ${version:my.artifact.id}, ${osgiVersion:my.artifact.id}, ${property:x}, ${ENVVAR}");
	}
	
	private static String getVersionTigerVersion() {
		InputStream propertiesFileStream = null;
		try {
			Properties properties = new Properties();
			propertiesFileStream = VersionTigerBatchOperation.class.getResourceAsStream(
					"/META-INF/maven/com.inventage.tools.versiontiger/com.inventage.tools.versiontiger/pom.properties");
			properties.load(propertiesFileStream);
			return properties.getProperty("version");
		} catch (Exception e) {
			return String.valueOf(Integer.MAX_VALUE);
		}
		finally {
			if (propertiesFileStream != null) {
				try {
					propertiesFileStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
