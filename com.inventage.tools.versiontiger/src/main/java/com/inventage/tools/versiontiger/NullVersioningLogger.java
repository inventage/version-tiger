package com.inventage.tools.versiontiger;

/**
 * This implementation of the versioning logger may be passed instead of a null parameter. It does no harm.
 * 
 * @author nw
 */
public class NullVersioningLogger implements VersioningLogger {

	@Override
	public VersioningLoggerItem createVersioningLoggerItem() {
		return new NullVersioningLoggerItem();
	}

	@Override
	public void addVersioningLoggerItem(VersioningLoggerItem loggerItem) {
	}

	
	public class NullVersioningLoggerItem extends AbstractVersioningLoggerItem {}
}
