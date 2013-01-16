package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class StandardOutLogger implements VersioningLogger {

	@Override
	public VersioningLoggerItem createVersioningLoggerItem() {
		return new StandardOutLoggerItem();
	}

	@Override
	public void addVersioningLoggerItem(VersioningLoggerItem loggerItem) {
		StandardOutLoggerItem item = (StandardOutLoggerItem) loggerItem;
		
		item.writeToStatusPrintStream();
	}

}
