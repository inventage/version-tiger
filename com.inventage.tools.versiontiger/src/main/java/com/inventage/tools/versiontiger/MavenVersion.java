package com.inventage.tools.versiontiger;

public interface MavenVersion extends Version {

	String suffix();

	String suffixDelimiter();

	MavenVersion incrementMajorAndSnapshot();

	MavenVersion incrementMinorAndSnapshot();

	MavenVersion incrementBugfixAndSnapshot();

	MavenVersion releaseVersion();

	MavenVersion snapshotVersion();

	MavenVersion releaseVersionWithSuffix(String suffix);
	
}
