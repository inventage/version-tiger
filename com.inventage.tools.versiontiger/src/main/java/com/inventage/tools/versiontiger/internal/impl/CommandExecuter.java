package com.inventage.tools.versiontiger.internal.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.Versioning;
import com.inventage.tools.versiontiger.VersioningLogger;

class CommandExecuter {
	private static final String COMMAND_EXECUTER_UNIVERSE_ID = "commandExecuterUniverse";

	private final Versioning versioning;
	private final ProjectUniverse universe;
	private final String rootPath;
	private final VersioningLogger logger;
	
	CommandExecuter(Versioning versioning, String rootPath, VersioningLogger logger) {
		this.versioning = versioning;
		this.rootPath = rootPath;
		this.logger = logger;
		universe = versioning.createUniverse(COMMAND_EXECUTER_UNIVERSE_ID, null, rootPath, logger);
	}
	
	public Versioning getVersioning() {
		return versioning;
	}
	
	public ProjectUniverse getUniverse() {
		return universe;
	}
	
	public String getRootPath() {
		return rootPath;
	}
	
	public VersioningLogger getLogger() {
		return logger;
	}

	/**
	 * Executes the line oriented versioning tool commands provided by the
	 * {@link BufferedReader} passed as arguments. Writes back output and error
	 * messages to the {@link BufferedWriter}. It is assumed that the caller -
	 * which provides the reader and writers - closes them by itself.
	 */
	void executeCommands(BufferedReader commandReader) throws IOException {
		String line;
		while ((line = commandReader.readLine()) != null) {
			
			Command command = new Command(line);
			if (!command.isComment()) {
				execute(command);
			}
		}
	}

	void execute(Command command) {
		command.getOperation().execute(this, command);
	}
}
