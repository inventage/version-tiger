package com.inventage.tools.versiontiger.internal.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.RootPathProvider;
import com.inventage.tools.versiontiger.Versioning;
import com.inventage.tools.versiontiger.VersioningLogger;

class CommandExecuter implements RootPathProvider {

	private final Versioning versioning;
	private final ProjectUniverse universe;
	private final VersioningLogger logger;
	private final Deque<String> rootPath = new ArrayDeque<String>();
	
	private boolean shouldQuitWithFailure;
	
	CommandExecuter(Versioning versioning, VersioningLogger logger) {
		this(versioning, getCurrentDirectory(), logger);
	}
	
	CommandExecuter(Versioning versioning, String initialRootPath, VersioningLogger logger) {
		this.versioning = versioning;
		this.logger = logger;
		this.universe = versioning.createUniverse("commandExecuterUniverse", "commandExecuterUniverse", this, logger);
		enterNewRootPath(initialRootPath);
	}

	private static String getCurrentDirectory() {
		return System.getProperty("user.dir");
	}

	public Versioning getVersioning() {
		return versioning;
	}
	
	public ProjectUniverse getUniverse() {
		return universe;
	}
	
	public String getRootPath() {
		return rootPath.peek();
	}
	
	public void enterNewRootPath(String rootPath) {
		this.rootPath.push(rootPath);
	}
	
	public void leaveCurrentRootPath() {
		if (this.rootPath.size() <= 1) {
			throw new IllegalStateException("Cannot leave initial root path.");
		}
		this.rootPath.pop();
	}
	
	public VersioningLogger getLogger() {
		return logger;
	}
	
	public void failAndQuit() {
		this.shouldQuitWithFailure = true;
	}
	
	public boolean isFailed() {
		return this.shouldQuitWithFailure;
	}

	/**
	 * Executes the line oriented versioning tool commands provided by the
	 * {@link BufferedReader} passed as arguments. Writes back output and error
	 * messages to the {@link BufferedWriter}. It is assumed that the caller -
	 * which provides the reader and writers - closes them by itself.
	 */
	void executeCommands(BufferedReader commandReader) throws IOException {
		String line;
		while (!shouldQuitWithFailure && (line = commandReader.readLine()) != null) {
			
			Command command = new Command(line);
			if (!command.isComment()) {
				execute(command);
			}
		}
	}

	void execute(Command command) {
		if (!command.isValidOperation()) {
			VersionTigerBatchOperation.logUnknownCommand(command, logger);
			return;
		}
		
		command.getOperation().execute(this, command);
	}
}
