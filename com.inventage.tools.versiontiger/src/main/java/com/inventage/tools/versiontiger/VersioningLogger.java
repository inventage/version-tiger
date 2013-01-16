package com.inventage.tools.versiontiger;

/**
 * Holds the results of a versioning process, that is, a list of logger items which represent a single change operation each.
 * 
 * @author nw
 */
public interface VersioningLogger {
	
	VersioningLoggerItem createVersioningLoggerItem();
	
	void addVersioningLoggerItem(VersioningLoggerItem loggerItem);
}
