package com.inventage.tools.versiontiger.internal.impl;

import java.io.PrintStream;

import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.Version;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

public class StandardOutLoggerItem implements VersioningLoggerItem {

	private StringBuilder message = new StringBuilder();
	private VersioningLoggerStatus status;
	
	@Override
	public void setProject(Project project) {
	}
	
	@Override
	public void setOldVersion(Version oldVersion) {
	}
	
	@Override
	public void setNewVersion(Version newVersion) {
	}
	
	@Override
	public void setStatus(VersioningLoggerStatus loggerStatus) {
		this.status = loggerStatus;
	}
	
	public VersioningLoggerStatus getStatus() {
		return status;
	}

	@Override
	public void appendToMessage(String message) {
		this.message.append(message);
	}
	
	public String getMessage() {
		return message.toString();
	}

	public void writeToStatusPrintStream() {
		/* We don't log items containing no logger status. */
		if (getStatus() == null) {
			return;
		}
		
		PrintStream outStream = getStatus().getConsolePrintStream();
		
		outStream.println(getMessage());
		outStream.flush();
	}

}
