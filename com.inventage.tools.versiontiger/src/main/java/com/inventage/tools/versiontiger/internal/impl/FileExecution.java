package com.inventage.tools.versiontiger.internal.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;
import com.inventage.tools.versiontiger.util.FileHandler;

public class FileExecution {
	
	private final FileHandler fileHandler = new FileHandler();

	public void execute(File statementsFile) {
		execute(statementsFile, new CommandExecuter(new VersioningImpl(), new StandardOutLogger()));
	}

	public void execute(String filePathRelativeToCurrentRoot, CommandExecuter commandExecuter) {
		File statementsFile = fileHandler.createFileFromPath(commandExecuter.getRootPath(), filePathRelativeToCurrentRoot);
		
		execute(statementsFile, commandExecuter);
	}
	
	private void execute(File statementsFile, CommandExecuter commandExecuter) {
		try {
			String statementsDirectoryPath = fileHandler.getDirectoryPath(statementsFile);
			commandExecuter.enterNewRootPath(statementsDirectoryPath);
			commandExecuter.executeCommands(new BufferedReader(new FileReader(statementsFile)));
			
		} catch (FileNotFoundException e) {
			logError(commandExecuter.getLogger(), "File not found: " + statementsFile.getAbsolutePath());
		} catch (IOException e) {
			logError(commandExecuter.getLogger(), "Cannot read commands from file: " + statementsFile.getAbsolutePath());
		}
		finally {
			commandExecuter.leaveCurrentRootPath();
		}
	}
	
	private static void logError(VersioningLogger logger, String line) {
		VersioningLoggerItem item = logger.createVersioningLoggerItem();
		item.appendToMessage(line);
		item.setStatus(VersioningLoggerStatus.ERROR);
		logger.addVersioningLoggerItem(item);
	}

}
