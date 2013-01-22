package com.inventage.tools.versiontiger;


/**
 * Represents a version update status item on a single file that was changed.
 * 
 * @author nw
 */
public interface VersioningLoggerItem {
	
	void setProject(Project project);
	void setOriginalProject(String originalProject);
	void setOldVersion(Version oldVersion);
	void setNewVersion(Version newVersion);
	void setStatus(VersioningLoggerStatus versioningLoggerStatus);
	void appendToMessage(String message);

}
