package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;

public class VersionFactory {
	
	private String osgiReleaseQualifier;
	private String osgiSnapshotQualifier;
	
	public VersionFactory(String osgiReleaseQualifier, String osgiSnapshotQualifier) {
		setOsgiReleaseQualifier(osgiReleaseQualifier);
		setOsgiSnapshotQualifier(osgiSnapshotQualifier);
	}

	public MavenVersion createMavenVersion(String mavenVersion) {
		return new MavenVersionImpl(mavenVersion, this);
	}
	
	public MavenVersion createMavenVersion(Integer major, Integer minor, Integer bugfix, String suffix, boolean snapshot) {
		return new MavenVersionImpl(major, minor, bugfix, suffix, snapshot, this);
	}
	
	public OsgiVersion createOsgiVersion(String osgiVersion) {
		return new OsgiVersionImpl(osgiVersion, this);
	}
	
	public OsgiVersion createOsgiVersion(MavenVersion mavenVersion) {
		
		return new OsgiVersionImpl(
			mavenVersion.major(),
			mavenVersion.minor(),
			mavenVersion.bugfix(),
			mavenVersion.isSnapshot(),
			this
		);
	}
	
	public OsgiVersion createOsgiVersion(Integer major, Integer minor, Integer bugfix, boolean snapshot) {
		return new OsgiVersionImpl(major, minor, bugfix, snapshot, this);
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

}
