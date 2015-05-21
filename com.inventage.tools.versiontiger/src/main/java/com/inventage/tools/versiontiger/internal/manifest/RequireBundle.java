package com.inventage.tools.versiontiger.internal.manifest;

import java.util.ArrayList;
import java.util.List;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class RequireBundle {

	private final List<RequireBundleAttribute> requireBundleAttributes = new ArrayList<RequireBundleAttribute>();
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void addRequireBundleAttribute(RequireBundleAttribute requireBundleAttribute) {
		requireBundleAttributes.add(requireBundleAttribute);
	}

	public void print(StringBuilder result) {
		result.append(getId());
		
		for (RequireBundleAttribute requireBundleAttribute : requireBundleAttributes) {
			result.append(";");
			requireBundleAttribute.print(result);
		}
	}
	
	private VersionAttribute findVersionAttribute() {
		for (RequireBundleAttribute attribute : requireBundleAttributes) {
			if (attribute instanceof VersionAttribute) {
				return (VersionAttribute) attribute;
			}
		}
		return null;
	}

	public boolean updateVersionIfOldMatches(OsgiVersion oldVersion, OsgiVersion newVersion, VersioningLoggerItem loggerItem,
			VersionRangeChangeStrategy versionRangeChangeStrategy) {
		VersionAttribute versionAttribute = findVersionAttribute();
		if (versionAttribute != null) {
			return versionAttribute.updateVersionIfOldMatches(oldVersion, newVersion, loggerItem, versionRangeChangeStrategy);
		}
		
		return false;
	}

}
