package com.inventage.tools.versiontiger;

import java.io.BufferedWriter;
import java.io.IOException;

public abstract class AbstractVersioningLoggerItem implements VersioningLoggerItem {
	
	public final static String DELIMITER = " - ";

	private Project project;
	private ProjectId originalProjectId;
	private Version oldVersion;
	private Version newVersion;
	private VersioningLoggerStatus versioningLoggerStatus = VersioningLoggerStatus.SUCCESS;
	private StringBuilder statusMessage = new StringBuilder();
	
	@Override
	public void setProject(Project project) {
		this.project = project;
	}
	
	protected Project getProject() {
		return project;
	}
	
	private boolean hasProject() {
		return project != null;
	}

	@Override
	public void setOriginalProject(ProjectId originalProjectId) {
		this.originalProjectId = originalProjectId;
	}
	
	protected ProjectId getOriginalProject() {
		return originalProjectId;
	}
	
	private boolean isReference() {
		return originalProjectId != null;
	}

	@Override
	public void setOldVersion(Version oldVersion) {
		this.oldVersion = oldVersion;
	}
	
	protected Version getOldVersion() {
		return oldVersion;
	}

	@Override
	public void setNewVersion(Version newVersion) {
		this.newVersion = newVersion;
	}
	
	protected Version getNewVersion() {
		return newVersion;
	}
	
	private boolean hasVersions() {
		return oldVersion != null && newVersion != null;
	}

	@Override
	public void setStatus(VersioningLoggerStatus versioningLoggerStatus) {
		this.versioningLoggerStatus = versioningLoggerStatus;
	}
	
	protected VersioningLoggerStatus getStatus() {
		return versioningLoggerStatus;
	}
	
	private boolean hasStatus() {
		return versioningLoggerStatus != null;
	}

	@Override
	public void appendToMessage(String message) {
		statusMessage.append(message);
	}
	
	protected String getMessage() {
		return statusMessage.toString();
	}
	
	private boolean hasMessage() {
		return 0 < statusMessage.length(); 
	}
	
	public void writeTo(BufferedWriter bufferedWriter) throws IOException {
		bufferedWriter.append(formatLine());
	}
	
	/**
	 * This is the standard line output which may be overridden by extending classes.
	 */
	public String formatLine() {
		StringBuilder result = new StringBuilder();
		
		/* Status information */
		if (hasStatus()) {
			result.append(getStatus().toString());
		}
		
		/* Project information */
		if (hasProject()) {
			
			if (isReference()) {
				/* If this is a reference to the examined project in another project, we first display the examined project. */
				result.append(DELIMITER);
				result.append(originalProjectId);
				
				appendVersionTransistion(result);
				
				result.append(DELIMITER);
				result.append("REFERENCE FROM ");
				result.append(project.id());
			}
			else {
				result.append(DELIMITER);
				result.append(project.id());
				
				appendVersionTransistion(result);
			}
		}
		
		/* Message */
		if (hasMessage()) {
			result.append(DELIMITER);
			result.append(getMessage());
		}
		
		return result.toString();
	}
	
	private void appendVersionTransistion(StringBuilder stringbuilder) {
		if (hasVersions()) {
			stringbuilder.append(DELIMITER);
			stringbuilder.append("[");
			stringbuilder.append(oldVersion.toString());
			stringbuilder.append("=>");
			stringbuilder.append(newVersion.toString());
			stringbuilder.append("]");
		}
	}
}
