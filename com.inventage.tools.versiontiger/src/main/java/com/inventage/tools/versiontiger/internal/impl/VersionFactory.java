package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy;
import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;

public class VersionFactory {
	
	private String osgiReleaseQualifier;
	private String osgiSnapshotQualifier;
	private VersionRangeChangeStrategy versionRangeChangeStrategy;
	private MavenToOsgiVersionMappingStrategy mavenToOsgiVersionMappingStrategy;
	
	public VersionFactory(String osgiReleaseQualifier, String osgiSnapshotQualifier,
			VersionRangeChangeStrategy versionRangeChangeStrategy, MavenToOsgiVersionMappingStrategy mavenToOsgiVersionMappingStrategy) {
		setOsgiReleaseQualifier(osgiReleaseQualifier);
		setOsgiSnapshotQualifier(osgiSnapshotQualifier);
		setVersionRangeChangeStrategy(versionRangeChangeStrategy);
		setMavenToOsgiVersionMappingStrategy(mavenToOsgiVersionMappingStrategy);
	}

	public MavenVersion createMavenVersion(String mavenVersion) {
		return new MavenVersionImpl(mavenVersion, this);
	}
	
	public MavenVersion createMavenVersion(Integer major, Integer minor, Integer bugfix, String suffix, String suffixDelimiter, boolean snapshot) {
		return new MavenVersionImpl(major, minor, bugfix, suffix, suffixDelimiter, snapshot, this);
	}
	
	public OsgiVersion createOsgiVersion(String osgiVersion) {
		return new OsgiVersionImpl(osgiVersion, this);
	}
	
	public OsgiVersion createOsgiVersion(Integer major, Integer minor, Integer bugfix, String qualifier) {
		return new OsgiVersionImpl(major, minor, bugfix, qualifier, this);
	}
	
	public OsgiVersion createOsgiVersion(MavenVersion mavenVersion) {
		return getMavenToOsgiVersionMappingStrategy().createOsgiFromMaven(mavenVersion, this);
	}
	
	public void setOsgiReleaseQualifier(String osgiReleaseQualifier) {
		if (osgiReleaseQualifier == null) {
			throw new IllegalArgumentException("OSGI release qualifier must not be null!");
		}
		this.osgiReleaseQualifier = osgiReleaseQualifier;
	}
	
	public String getOsgiReleaseQualifier() {
		return osgiReleaseQualifier;
	}
	
	public void setOsgiSnapshotQualifier(String osgiSnapshotQualifier) {
		if (osgiSnapshotQualifier == null) {
			throw new IllegalArgumentException("OSGI snapshot qualifier must not be null!");
		}
		this.osgiSnapshotQualifier = osgiSnapshotQualifier;
	}
	
	public String getOsgiSnapshotQualifier() {
		return osgiSnapshotQualifier;
	}
	
	public void setVersionRangeChangeStrategy(VersionRangeChangeStrategy versionRangeChangeStrategy) {
		if (versionRangeChangeStrategy == null) {
			throw new IllegalArgumentException("Version range change strategy must not be null!");
		}
		this.versionRangeChangeStrategy = versionRangeChangeStrategy;
	}
	
	public VersionRangeChangeStrategy getVersionRangeChangeStrategy() {
		return versionRangeChangeStrategy;
	}

	public void setMavenToOsgiVersionMappingStrategy(MavenToOsgiVersionMappingStrategy mavenToOsgiVersionMappingStrategy) {
		if (mavenToOsgiVersionMappingStrategy == null) {
			throw new IllegalArgumentException("Maven to OSGi version mapping strategy must not be null!");
		}
		this.mavenToOsgiVersionMappingStrategy = mavenToOsgiVersionMappingStrategy;
	}
	
	public MavenToOsgiVersionMappingStrategy getMavenToOsgiVersionMappingStrategy() {
		return mavenToOsgiVersionMappingStrategy;
	}

}
