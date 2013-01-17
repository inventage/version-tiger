package com.inventage.tools.versiontiger.internal.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.Version;


public class OsgiVersionImpl implements OsgiVersion {

	private static final Pattern PATTERN_VERSION = Pattern.compile("^\\d+(\\.\\d+(\\.\\d+(\\.([\\w\\-\\_]+))?)?)?$");
	private static final String OSGI_DELIMITER = ".";
	
	private final VersionFactory versionFactory;
	
	private final GeneralVersion gv;
	private final String inputQualifier;
	
	public OsgiVersionImpl(String version, VersionFactory versionFactory) {
		
		this.versionFactory = versionFactory;
		
		Matcher matcher = PATTERN_VERSION.matcher(version);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid OSGi version: " + version);
		}
		
		String qualifierMatch = matcher.group(4);
		this.inputQualifier = qualifierMatch == null ? "" :qualifierMatch; 
		int qualifierStartIndex = matcher.start(4);
		String gvVersion = version.substring(0, qualifierStartIndex < 0 ? version.length() : qualifierStartIndex - 1);
		
		gv = new GeneralVersion(gvVersion, false /* doesn't care */);
	}
	
	public OsgiVersionImpl(Integer major, Integer minor, Integer bugfix, boolean snapshot, VersionFactory versionFactory) {
		
		this.versionFactory = versionFactory;
		this.inputQualifier = null;
		
		boolean paddingNecessary = (snapshot && !versionFactory.getOsgiSnapshotQualifier().isEmpty())
				|| (!snapshot && !versionFactory.getOsgiReleaseQualifier().isEmpty());
		
		if (minor == null && paddingNecessary) {
			minor = 0;
		}
		if (bugfix == null && paddingNecessary) {
			bugfix = 0;
		}
		gv = new GeneralVersion(major, minor, bugfix, snapshot);
	}
	
	@Override
	public String qualifier() {
		return inputQualifier;
	}
	
	@Override
	public String toString() {
		StringBuilder version = new StringBuilder(gv.versionString());
		
		if (inputQualifier != null) {
			conditionallyAppendQualifier(version, inputQualifier);
		}
		else if (isSnapshot()) {
			conditionallyAppendQualifier(version, versionFactory.getOsgiSnapshotQualifier());
		}
		else {
			conditionallyAppendQualifier(version, versionFactory.getOsgiReleaseQualifier());
		}

		return version.toString();
	}
	
	private void conditionallyAppendQualifier(StringBuilder version, String qualifier) {
		if (qualifier != null && !qualifier.isEmpty()) {
			version.append(OSGI_DELIMITER);
			version.append(qualifier);
		}
	}

	@Override
	public OsgiVersion incrementMajorAndSnapshot() {
		GeneralVersion inc = gv.incrementMajorAndSnapshot();
		return versionFactory.createOsgiVersion(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), true);
	}

	@Override
	public OsgiVersion incrementMinorAndSnapshot() {
		GeneralVersion inc = gv.incrementMinorAndSnapshot();
		return versionFactory.createOsgiVersion(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), true);
	}

	@Override
	public OsgiVersion incrementBugfixAndSnapshot() {
		GeneralVersion inc = gv.incrementBugfixAndSnapshot();
		return versionFactory.createOsgiVersion(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), true);
	}

	@Override
	public OsgiVersion releaseVersion() {
		return versionFactory.createOsgiVersion(gv.major(), gv.minor(), gv.bugfix(), false);
	}

	@Override
	public OsgiVersion snapshotVersion() {
		return versionFactory.createOsgiVersion(gv.major(), gv.minor(), gv.bugfix(), true);
	}

	@Override
	public Integer major() {
		return gv.major();
	}
	
	@Override
	public Integer minor() {
		return gv.minor();
	}
	
	@Override
	public Integer bugfix() {
		return gv.bugfix();
	}
	
	@Override
	public boolean isSnapshot() {
		return gv.isSnapshot();
	}

	@Override
	public boolean isLowerThan(Version other, boolean inclusive) {
		return gv.isLowerThan(other, inclusive);
	}

	@Override
	public int compareTo(Version o) {
		return gv.compareTo(o);
	}
	
	private static int nullToZero(Integer integer) {
		return integer == null ? 0 : integer;
	}

}
