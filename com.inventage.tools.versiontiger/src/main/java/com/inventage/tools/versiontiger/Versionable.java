package com.inventage.tools.versiontiger;

public interface Versionable {

	void setVersion(MavenVersion newVersion);

	void incrementMajorVersionAndSnapshot();

	void incrementMinorVersionAndSnapshot();

	void incrementBugfixVersionAndSnapshot();

	void useReleaseVersion();

	void useSnapshotVersion();

}
