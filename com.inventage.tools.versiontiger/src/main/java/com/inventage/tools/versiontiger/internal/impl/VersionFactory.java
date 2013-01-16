package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;

public class VersionFactory {

	public MavenVersion createMavenVersion(String mavenVersion) {
		return new MavenVersionImpl(mavenVersion);
	}
	
	public MavenVersion createMavenVersion(OsgiVersion osgiVersion) {
		return new MavenVersionImpl(
			osgiVersion.major(),
			osgiVersion.minor(),
			osgiVersion.bugfix(),
			null,
			osgiVersion.isSnapshot()
		);
	}
	
	public OsgiVersion createOsgiVersion(String osgiVersion) {
		return new OsgiVersionImpl(osgiVersion);
	}
	
	public OsgiVersion createOsgiVersion(MavenVersion mavenVersion) {
		return new OsgiVersionImpl(
			mavenVersion.major(),
			mavenVersion.minor(),
			mavenVersion.bugfix(),
			null,
			mavenVersion.isSnapshot()
		);
	}

}
