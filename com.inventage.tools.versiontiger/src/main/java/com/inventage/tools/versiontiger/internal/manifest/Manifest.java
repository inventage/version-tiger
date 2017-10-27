package com.inventage.tools.versiontiger.internal.manifest;

import java.util.ArrayList;
import java.util.List;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class Manifest {

	private final List<ManifestSection> sections = new ArrayList<ManifestSection>();
	private String newLine;
	private final StringBuilder lastNewLines = new StringBuilder();
	
	public List<ManifestSection> getSections() {
		return sections;
	}
	
	public ManifestHeader getManifestHeader(String name) {
		for (ManifestSection manifestSection : getSections()) {
			ManifestHeader manifestHeader = manifestSection.getManifestHeader(name);
			if (manifestHeader != null) {
				return manifestHeader;
			}
		}
		return null;
	}

	public void addSection(ManifestSection section) {
		sections.add(section);
	}
	
	public String getNewLine() {
		return newLine;
	}
	
	public void setNewLine(String newLine) {
		this.newLine = newLine;
	}
	
	public String getLastNewLines() {
		return lastNewLines.toString();
	}

	public void appendLastNewLine(String lastNewLine) {
		lastNewLines.append(lastNewLine);
	}

	public String print() {
		StringBuilder result = new StringBuilder();
		print(result);
		return result.toString();
	}
	
	public void print(StringBuilder result) {
		result.append("Manifest-Version: 1.0");
		if (sections.isEmpty()) {
			result.append(newLine);
		}
		else {
			for (ManifestSection section : sections) {
				result.append(newLine);
				section.print(result);
			}
		}
		
		if (lastNewLines != null) {
			result.append(lastNewLines);
		}
	}

	public void setBundleVersion(String newVersion) {
		if (sections.isEmpty()) {
			addSection(new ManifestSection());
		}
		sections.get(0).setBundleVersion(newVersion);
	}

	public boolean updateRequireBundleReference(String id, OsgiVersion oldVersion, OsgiVersion newVersion,
			VersioningLoggerItem loggerItem, VersionRangeChangeStrategy versionRangeChangeStrategy) {
		if (!sections.isEmpty()) {
			return sections.get(0).updateRequireBundleReference(id, oldVersion, newVersion, loggerItem, versionRangeChangeStrategy);
		}
		return false;
	}

	public boolean updateFragmentHostReference(String id, OsgiVersion oldVersion, OsgiVersion newVersion,
			VersioningLoggerItem loggerItem, VersionRangeChangeStrategy versionRangeChangeStrategy) {
		if (!sections.isEmpty()) {
			return sections.get(0).updateFragmentHostReference(id, oldVersion, newVersion, loggerItem, versionRangeChangeStrategy);
		}
		return false;
	}
	
	public boolean ensureStrictDependencyTo(String id, VersioningLoggerItem loggerItem) {
		if (!sections.isEmpty()) {
			return sections.get(0).ensureStrictDependencyTo(id, loggerItem);
		}
		return true;
	}
	
}
