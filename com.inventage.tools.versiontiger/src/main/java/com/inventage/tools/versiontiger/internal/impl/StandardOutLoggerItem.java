package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.AbstractVersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

public class StandardOutLoggerItem extends AbstractVersioningLoggerItem {

	public VersioningLoggerStatus getStatus() {
		return super.getStatus();
	}
	
	@Override
	public String formatLine() {
		
		/* If this is a message, we only print the message itself. Otherwise the complete context. */
		if (VersioningLoggerStatus.MESSAGE.equals(getStatus())) {
			return getMessage();
		}
		
		return super.formatLine();
	}
}
