package com.inventage.tools.versiontiger.ui.edit;

import static org.eclipse.ui.handlers.HandlerUtil.getActiveShell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import com.inventage.tools.versiontiger.Versioning;
import com.inventage.tools.versiontiger.ui.VersioningUIPlugin;
import com.inventage.tools.versiontiger.ui.logger.ConsoleLogger;

public class RunCommandFileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		File commandFile = extractAbsolutePathFromSelection(getCurrentSelection(event));

		if (commandFile != null && hasUserConfirmed(commandFile, getActiveShell(event))) {
			ConsoleLogger logger = new ConsoleLogger();
			executeCommandsFile(commandFile, logger);
			
			try {
				logger.close();
			}
			catch (Exception e) {
				StatusManager.getManager().handle(new Status(Status.ERROR, VersioningUIPlugin.PLUGIN_ID, "Was not able to show console with results.", e),
						StatusManager.SHOW);
			}
		}

		return null;
	}
	
	private boolean hasUserConfirmed(File commandFile, Shell parent) {
		return MessageDialog.openConfirm(parent,
				"Run versioning commands",
				"Do you really like to execute the version update commands in " + commandFile + "?");
	}

	private void executeCommandsFile(File commandFile, ConsoleLogger logger) throws ExecutionException {

		BufferedReader inputFileReader = null;
		try {
			inputFileReader = getReaderForFile(commandFile);

			loadVersioning().executeCommandScript(inputFileReader, getCurrentFileDirectory(commandFile), logger);
		} catch (FileNotFoundException e) {
			logError("Command file not found: " + commandFile, e);
		} catch (IOException e) {
			logError("Error occurred while reading the instructions from " + commandFile, e);
		} catch (Exception e) {
			logError("Error occurred while running commands from file: " + commandFile, e);
		} finally {
			try {
				if (inputFileReader != null) {
					inputFileReader.close();
				}
			} catch (IOException e) {
				// nop
			}
		}
	}
	
	private void logError(String message, Throwable e) {
		StatusManager.getManager().handle(new Status(Status.ERROR, VersioningUIPlugin.PLUGIN_ID, message, e), StatusManager.SHOW);
	}

	private String getCurrentFileDirectory(File commandFile) {
		return Path.fromOSString(commandFile.getAbsolutePath()).removeLastSegments(1).toOSString();
	}

	private StructuredSelection getCurrentSelection(ExecutionEvent event) {
		return (StructuredSelection) HandlerUtil.getActiveMenuSelection(event);
	}

	private File extractAbsolutePathFromSelection(StructuredSelection selection) {
		File result = null;

		if (selection.getFirstElement() instanceof IFile) {
			IFile commandFile = (IFile) selection.getFirstElement();
			result = new File(commandFile.getLocation().toOSString());
		}

		return result;
	}

	private BufferedReader getReaderForFile(File file) throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}

	private Versioning loadVersioning() {
		return VersioningUIPlugin.getDefault().getVersioning();
	}
	
}
