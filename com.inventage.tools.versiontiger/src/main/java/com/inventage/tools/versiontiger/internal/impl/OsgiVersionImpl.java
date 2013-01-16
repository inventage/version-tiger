package com.inventage.tools.versiontiger.internal.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.Version;


public class OsgiVersionImpl implements OsgiVersion {

	private static final Pattern PATTERN_VERSION = Pattern.compile("^\\d+(\\.\\d+(\\.\\d+(\\.([\\w\\-\\_]+))?)?)?$");
	private static final String OSGI_DELIMITER = ".";
	private static final String OSGI_SNAPSHOT_SUFFIX = "qualifier";
	
	private final GeneralVersion gv;
	private final String qualifier;
	
	public OsgiVersionImpl(String version) {
		Matcher matcher = PATTERN_VERSION.matcher(version);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid OSGi version: " + version);
		}
		
		String inputQualifier = matcher.group(4);
		int qualifierStartIndex = matcher.start(4);
		String gvVersion = version.substring(0, qualifierStartIndex < 0 ? version.length() : qualifierStartIndex - 1);
		
		if (OSGI_SNAPSHOT_SUFFIX.equals(inputQualifier)) {
			qualifier = null;
			gv = new GeneralVersion(gvVersion, true);
		} else {
			qualifier = inputQualifier;
			gv = new GeneralVersion(gvVersion, false);
		}
	}
	
	public OsgiVersionImpl(Integer major, Integer minor, Integer bugfix, String qualifier, boolean snapshot) {
		if (minor == null && (qualifier != null || snapshot)) {
			minor = 0;
		}
		if (bugfix == null && (qualifier != null || snapshot)) {
			bugfix = 0;
		}
		gv = new GeneralVersion(major, minor, bugfix, snapshot);
		this.qualifier = qualifier;
	}
	
	@Override
	public String qualifier() {
		return qualifier;
	}
	
	@Override
	public String toString() {
		StringBuilder version = new StringBuilder(gv.versionString());
		
		if (isSnapshot()) {
			version.append(OSGI_DELIMITER);
			version.append(OSGI_SNAPSHOT_SUFFIX);
		}
		else if (qualifier != null && !qualifier.isEmpty()) {
			version.append(OSGI_DELIMITER);
			version.append(qualifier);
		}

		return version.toString();
	}

	@Override
	public OsgiVersion incrementMajorAndSnapshot() {
		GeneralVersion inc = gv.incrementMajorAndSnapshot();
		
		return new OsgiVersionImpl(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), qualifier, true);
	}

	@Override
	public OsgiVersion incrementMinorAndSnapshot() {
		GeneralVersion inc = gv.incrementMinorAndSnapshot();
		
		return new OsgiVersionImpl(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), qualifier, true);
	}

	@Override
	public OsgiVersion incrementBugfixAndSnapshot() {
		GeneralVersion inc = gv.incrementBugfixAndSnapshot();
		
		return new OsgiVersionImpl(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), qualifier, true);
	}

	@Override
	public OsgiVersion releaseVersion() {
		return gv.isSnapshot() ? new OsgiVersionImpl(gv.major(), gv.minor(), gv.bugfix(), qualifier, false) : this;
	}

	@Override
	public OsgiVersion snapshotVersion() {
		return gv.isSnapshot() ? this : new OsgiVersionImpl(gv.major(), gv.minor(), gv.bugfix(), qualifier, true);
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
