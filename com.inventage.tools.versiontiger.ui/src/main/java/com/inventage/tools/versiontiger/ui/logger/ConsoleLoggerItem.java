package com.inventage.tools.versiontiger.ui.logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.Version;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

public class ConsoleLoggerItem implements VersioningLoggerItem {
	
	private final Date timestamp = new Date();
	private Project project;
	private Version oldVersion;
	private Version newVersion;
	private VersioningLoggerStatus loggerStatus = VersioningLoggerStatus.SUCCESS;
	private StringBuilder statusMessage = new StringBuilder();

	@Override
	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public void setOldVersion(Version oldVersion) {
		this.oldVersion = oldVersion;
	}

	@Override
	public void setNewVersion(Version newVersion) {
		this.newVersion = newVersion;
	}

	@Override
	public void setStatus(VersioningLoggerStatus loggerStatus) {
		this.loggerStatus = loggerStatus;
	}

	@Override
	public void appendToMessage(String message) {
		statusMessage.append(message);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public Project getProject() {
		return project;
	}

	public Version getOldVersion() {
		return oldVersion;
	}

	public Version getNewVersion() {
		return newVersion;
	}

	public VersioningLoggerStatus getStatus() {
		return loggerStatus;
	}

	public String getMessage() {
		return statusMessage.toString();
	}
	
	public void writeTo(BufferedWriter bufferedWriter) throws IOException {
		
		/* Status */
		bufferedWriter.append(loggerStatus.toString());
		bufferedWriter.append(ConsoleLogger.DELIMITER);
		
		/* Artefact */
		if (project != null) {
			bufferedWriter.append(project.id());
			bufferedWriter.append(ConsoleLogger.DELIMITER);
		}
		
		/* Version transition */
		if (oldVersion != null && newVersion != null) {
			bufferedWriter.append("[");
			bufferedWriter.append(oldVersion.toString());
			bufferedWriter.append("=>");
			bufferedWriter.append(newVersion.toString());
			bufferedWriter.append("]");
			bufferedWriter.append(ConsoleLogger.DELIMITER);
		}
		
		/* Message */
		bufferedWriter.append(statusMessage.toString());
		
		bufferedWriter.append(ConsoleLogger.LINE_DELIMITER);
		bufferedWriter.flush();
	}
}
