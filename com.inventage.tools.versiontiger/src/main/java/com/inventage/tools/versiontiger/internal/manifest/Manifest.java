package com.inventage.tools.versiontiger.internal.manifest;

import java.util.ArrayList;
import java.util.List;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class Manifest {

	private final List<ManifestSection> sections = new ArrayList<ManifestSection>();
	private String newLine;
	private String lastNewLine;
	
	public List<ManifestSection> getSections() {
		return sections;
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
	
	public String getLastNewLine() {
		return lastNewLine;
	}

	public void setLastNewLine(String lastNewLine) {
		this.lastNewLine = lastNewLine;
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
		
		if (lastNewLine != null) {
			result.append(lastNewLine);
		}
	}

	public void setBundleVersion(String newVersion) {
		if (sections.isEmpty()) {
			addSection(new ManifestSection());
		}
		sections.get(0).setBundleVersion(newVersion);
	}

	public boolean updateRequireBundleReference(String id, OsgiVersion oldVersion, OsgiVersion newVersion,
			VersioningLoggerItem loggerItem) {
		if (!sections.isEmpty()) {
			return sections.get(0).updateRequireBundleReference(id, oldVersion, newVersion, loggerItem);
		}
		return false;
	}

	public boolean updateFragmentHostReference(String id, OsgiVersion oldVersion, OsgiVersion newVersion,
			VersioningLoggerItem loggerItem) {
		if (!sections.isEmpty()) {
			return sections.get(0).updateFragmentHostReference(id, oldVersion, newVersion, loggerItem);
		}
		return false;
	}
	
}
