package com.inventage.tools.versiontiger;

/**
 * You should use a more concrete interface, e.g. {@link MavenVersion}.
 * 
 * @author Beat Strasser
 */
public interface Version extends Comparable<Version> {

	Integer major();

	Integer minor();

	Integer bugfix();

	boolean isSnapshot();
	
	Version incrementMajorAndSnapshot();

	Version incrementMinorAndSnapshot();

	Version incrementBugfixAndSnapshot();

	Version releaseVersion();

	Version snapshotVersion();

	boolean isLowerThan(Version other, boolean inclusive);

}
