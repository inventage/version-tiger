package com.inventage.tools.versiontiger.internal.manifest;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class VersionAttribute implements RequireBundleAttribute {

	private VersionRange versionRange;
	private boolean quotes = true;
	
	public VersionRange getVersionRange() {
		return versionRange;
	}

	public void setVersionRange(VersionRange versionRange) {
		this.versionRange = versionRange;
	}

	public boolean hasQuotes() {
		return quotes || versionRange.isRange();
	}

	public void setQuotes(boolean quotes) {
		this.quotes = quotes;
	}

	@Override
	public void print(StringBuilder result) {
		result.append("bundle-version=");
		if (hasQuotes()) {
			result.append('"');
		}
		versionRange.print(result);
		if (hasQuotes()) {
			result.append('"');
		}
	}

	public boolean updateVersionIfOldMatches(OsgiVersion oldVersion, OsgiVersion newVersion, VersioningLoggerItem loggerItem, VersionRangeChangeStrategy versionRangeChangeStrategy) {
		boolean result = versionRange.updateVersionIfOldMatches(oldVersion, newVersion, loggerItem, versionRangeChangeStrategy);
		
		StringBuilder message = new StringBuilder();
		print(message);
		loggerItem.appendToMessage(message.toString());
		
		return result;
	}

	public boolean ensureStrictDependency(VersioningLoggerItem loggerItem) {
		if (!versionRange.ensureIsStrict()) {
			loggerItem.appendToMessage("No strict version dependency (" + versionRange + ")");
			return false;
		}
		return true;
	}

}
