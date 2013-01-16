package com.inventage.tools.versiontiger;

public interface OsgiVersion extends Version {

	String qualifier();

	OsgiVersion incrementMajorAndSnapshot();

	OsgiVersion incrementMinorAndSnapshot();

	OsgiVersion incrementBugfixAndSnapshot();

	OsgiVersion releaseVersion();

	OsgiVersion snapshotVersion();

}
