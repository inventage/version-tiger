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

	
	public class NullVersioningLoggerItem implements VersioningLoggerItem {

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
		public void setStatus(VersioningLoggerStatus versioningLoggerStatus) {
		}

		@Override
		public void appendToMessage(String message) {
		}
		
	}
}
