package com.inventage.tools.versiontiger.internal.manifest;

import java.util.ArrayList;
import java.util.List;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class RequireBundleHeader implements ManifestHeader {
	public static final String NAME = "Require-Bundle";

	private final List<RequireBundle> requireBundles = new ArrayList<RequireBundle>();
	private String newLine = "\n";
	
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

	public void addRequireBundle(RequireBundle requireBundle) {
		requireBundles.add(requireBundle);
	}
	
	@Override
	public void print(StringBuilder result) {
		result.append(getName());
		result.append(": ");
		
		if (!requireBundles.isEmpty()) {
			requireBundles.get(0).print(result);
			for (int i = 1; i < requireBundles.size(); i++) {
				result.append(",");
				result.append(newLine); // FIXME poor mans solution
				result.append(" ");
				requireBundles.get(i).print(result);
			}
		}
		
		result.append(newLine);
	}

	public boolean updateReferenceVersion(String id, OsgiVersion oldVersion, OsgiVersion newVersion, VersioningLoggerItem loggerItem) {
		for (RequireBundle requireBundle : requireBundles) {
			if (id.equals(requireBundle.getId())) {
				loggerItem.appendToMessage(NAME);
				loggerItem.appendToMessage(": ");
				return requireBundle.updateVersionIfOldMatches(oldVersion, newVersion, loggerItem);
			}
		}
		return false;
	}

}
