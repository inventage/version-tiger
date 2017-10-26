package com.inventage.tools.versiontiger.internal.manifest;

import java.util.ArrayList;
import java.util.List;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class ManifestSection {

	private final List<ManifestHeader> headers = new ArrayList<ManifestHeader>();

	public ManifestHeader getManifestHeader(String name) {
		for (ManifestHeader manifestHeader : headers) {
			if (name.equals(manifestHeader.getName())) {
				return manifestHeader;
			}
		}
		return null;
	}
	
	public void addHeader(ManifestHeader header) {
		headers.add(header);
	}
	
	public void print(StringBuilder result) {
		for (ManifestHeader header : headers) {
			header.print(result);
		}
	}

	public void setBundleVersion(String newVersion) {
		GenericManifestHeader attribute = (GenericManifestHeader) getAttributeHeaderFor("Bundle-Version");
		if (attribute == null) {
			attribute = new GenericManifestHeader();
			attribute.setName("Bundle-Version");
			headers.add(attribute);
		}
		attribute.setValue(newVersion);
	}
	
	private ManifestHeader getAttributeHeaderFor(String attributeName) {
		for (ManifestHeader header : headers) {
			if (attributeName.equals(header.getName())) {
				return header;
			}
		}
		return null;
	}

	public boolean updateRequireBundleReference(String id, OsgiVersion oldVersion, OsgiVersion newVersion, VersioningLoggerItem loggerItem,
			VersionRangeChangeStrategy versionRangeChangeStrategy) {
		RequireBundleHeader header = (RequireBundleHeader) getAttributeHeaderFor(RequireBundleHeader.NAME);
		if (header != null) {
			return header.updateReferenceVersion(id, oldVersion, newVersion, loggerItem, versionRangeChangeStrategy);
		}
		return false;
	}

	public boolean updateFragmentHostReference(String id, OsgiVersion oldVersion, OsgiVersion newVersion, VersioningLoggerItem loggerItem,
			VersionRangeChangeStrategy versionRangeChangeStrategy) {
		FragmentHostHeader header = (FragmentHostHeader) getAttributeHeaderFor(FragmentHostHeader.NAME);
		if (header != null) {
			return header.updateReferenceVersion(id, oldVersion, newVersion, loggerItem, versionRangeChangeStrategy);
		}
		return false;
	}

	public boolean ensureStrictDependencyTo(String id, VersioningLoggerItem loggerItem) {
		boolean result = true;
		RequireBundleHeader requireBundleHeader = (RequireBundleHeader) getAttributeHeaderFor(RequireBundleHeader.NAME);
		if (requireBundleHeader != null && !requireBundleHeader.ensureStrictDependencyTo(id, loggerItem)) {
			loggerItem.appendToMessage(". ");
			result = false;
		}
		
		FragmentHostHeader fragmentHostHeader = (FragmentHostHeader) getAttributeHeaderFor(FragmentHostHeader.NAME);
		if (fragmentHostHeader != null && !fragmentHostHeader.ensureStrictDependencyTo(id, loggerItem)) {
			result = false;
		}
		
		return result;
	}
}
