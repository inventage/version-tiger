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
	private final String qualifier;
	
	public OsgiVersionImpl(String version, VersionFactory versionFactory) {
		this.versionFactory = versionFactory;
		
		Matcher matcher = PATTERN_VERSION.matcher(version);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid OSGi version: " + version);
		}
		
		String qualifierMatch = matcher.group(4);
		this.qualifier = qualifierMatch == null ? "" : qualifierMatch; 
		int qualifierStartIndex = matcher.start(4);
		String gvVersion = version.substring(0, qualifierStartIndex < 0 ? version.length() : qualifierStartIndex - 1);
		
		gv = new GeneralVersion(gvVersion, false /* does not care */);
	}
	
	public OsgiVersionImpl(Integer major, Integer minor, Integer bugfix, String qualifier, VersionFactory versionFactory) {
		this.versionFactory = versionFactory;
		this.qualifier = qualifier == null ? "" : qualifier;
		
		boolean paddingNecessary = !this.qualifier.isEmpty();
		
		if (minor == null && paddingNecessary) {
			minor = 0;
		}
		if (bugfix == null && paddingNecessary) {
			bugfix = 0;
		}
		gv = new GeneralVersion(major, minor, bugfix, false /* does not care */);
	}
	
	@Override
	public String qualifier() {
		return qualifier;
	}
	
	@Override
	public String toString() {
		StringBuilder version = new StringBuilder(gv.versionString());
		
		if (!qualifier.isEmpty()) {
			version.append(OSGI_DELIMITER);
			version.append(qualifier);
		}

		return version.toString();
	}
	
	@Override
	public OsgiVersion incrementMajorAndSnapshot() {
		GeneralVersion inc = gv.incrementMajorAndSnapshot();
		return versionFactory.getMavenToOsgiVersionMappingStrategy().createOsgiSnapshot(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), qualifier, versionFactory);
	}

	@Override
	public OsgiVersion incrementMinorAndSnapshot() {
		GeneralVersion inc = gv.incrementMinorAndSnapshot();
		return versionFactory.getMavenToOsgiVersionMappingStrategy().createOsgiSnapshot(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), qualifier, versionFactory);
	}

	@Override
	public OsgiVersion incrementBugfixAndSnapshot() {
		GeneralVersion inc = gv.incrementBugfixAndSnapshot();
		return versionFactory.getMavenToOsgiVersionMappingStrategy().createOsgiSnapshot(inc.major(), nullToZero(inc.minor()), nullToZero(inc.bugfix()), qualifier, versionFactory);
	}

	@Override
	public OsgiVersion releaseVersion() {
		return versionFactory.getMavenToOsgiVersionMappingStrategy().createOsgiRelease(gv.major(), gv.minor(), gv.bugfix(), qualifier, versionFactory);
	}

	@Override
	public OsgiVersion snapshotVersion() {
		return versionFactory.getMavenToOsgiVersionMappingStrategy().createOsgiSnapshot(gv.major(), gv.minor(), gv.bugfix(), qualifier, versionFactory);
	}

	@Override
	public OsgiVersion withoutQualifier() {
		return versionFactory.createOsgiVersion(gv.versionString());
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
		return false;
	}

	@Override
	public boolean isLowerThan(Version other, boolean inclusive) {
		int comparison = compareTo(other);
		return inclusive ? comparison <= 0 : comparison < 0;
	}

	@Override
	public int compareTo(Version o) {
		int gvCompare = gv.compareTo(o);
		if (o instanceof OsgiVersion && gvCompare == 0) {
			String otherQualifier = ((OsgiVersion) o).qualifier();
			if (qualifier() == null) {
				return (otherQualifier != null) ? -1 : 0;
			}
			else if (otherQualifier == null) {
				return 1;
			}
			return qualifier().compareTo(otherQualifier);
		}
		return gvCompare;
	}
	
	private static int nullToZero(Integer integer) {
		return integer == null ? 0 : integer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + gv.hashCode();
		result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OsgiVersionImpl other = (OsgiVersionImpl) obj;
		if (!gv.equals(other.gv))
			return false;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
			return false;
		return true;
	}

}
