package com.inventage.tools.versiontiger.internal.manifest;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class FragmentHostHeader implements ManifestHeader {
	
	public static final String NAME = "Fragment-Host";
	private RequireBundle hostBundle;
	private String newLine;

	@Override
	public String getName() {
		return NAME;
	}
	
	public String getNewLine() {
		return newLine;
	}

	public void setNewLine(String newLine) {
		this.newLine = newLine;
	}

	public void setHostBundle(RequireBundle hostBundle) {
		this.hostBundle = hostBundle;
	}

	@Override
	public void print(StringBuilder result) {
		result.append(getName());
		result.append(": ");
		hostBundle.print(result);
		if (newLine != null) {
			result.append(newLine);
		}
	}

	public boolean updateReferenceVersion(String id, OsgiVersion oldVersion, OsgiVersion newVersion, VersioningLoggerItem loggerItem) {
		if (id.equals(hostBundle.getId())) {
			loggerItem.appendToMessage(NAME);
			loggerItem.appendToMessage(": ");
			return hostBundle.updateVersionIfOldMatches(oldVersion, newVersion, loggerItem);
		}
		return false;
	}

}
