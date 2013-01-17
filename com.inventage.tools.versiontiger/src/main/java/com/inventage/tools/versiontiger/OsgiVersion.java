package com.inventage.tools.versiontiger;

public interface OsgiVersion extends Version {

	String OSGI_DEFAULT_SNAPSHOT_SUFFIX = "qualifier";
	String OSGI_DEFAULT_RELEASE_SUFFIX = "";
	
	String qualifier();

	OsgiVersion incrementMajorAndSnapshot();

	OsgiVersion incrementMinorAndSnapshot();

	OsgiVersion incrementBugfixAndSnapshot();

	OsgiVersion releaseVersion();

	OsgiVersion snapshotVersion();

}
