package com.inventage.tools.versiontiger;

import com.inventage.tools.versiontiger.internal.impl.VersionFactory;

public enum MavenToOsgiVersionMappingStrategy {

	/**
	 * Looses the maven suffix and uses the OSGi qualifier to distinguish snapshots and releases.
	 * 
	 * @see VersionFactory#getOsgiReleaseQualifier()
	 * @see VersionFactory#getOsgiSnapshotQualifier()
	 */
	OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION("qualifierForSnapshotDistinction", true) {
		@Override
		public OsgiVersion createOsgiFromMaven(MavenVersion mavenVersion, VersionFactory versionFactory) {
			return versionFactory.createOsgiVersion(
					mavenVersion.major(),
					mavenVersion.minor(),
					mavenVersion.bugfix(),
					mavenVersion.isSnapshot() ? versionFactory.getOsgiSnapshotQualifier() : versionFactory.getOsgiReleaseQualifier()
				);
		}
		
		@Override
		public OsgiVersion createOsgiSnapshot(Integer major, Integer minor, Integer bugfix, String qualifier, VersionFactory versionFactory) {
			return versionFactory.createOsgiVersion(major, minor, bugfix, versionFactory.getOsgiSnapshotQualifier());
		}
		
		@Override
		public OsgiVersion createOsgiRelease(Integer major, Integer minor, Integer bugfix, String qualifier, VersionFactory versionFactory) {
			return versionFactory.createOsgiVersion(major, minor, bugfix, versionFactory.getOsgiReleaseQualifier());
		}
	},
	
	/**
	 * Uses the suffix from Maven as qualifier.
	 */
	MAVEN_SUFFIX_TO_OSGI_QUALIFIER("suffixToQualifier", false) {
		@Override
		public OsgiVersion createOsgiFromMaven(MavenVersion mavenVersion, VersionFactory versionFactory) {
			return versionFactory.createOsgiVersion(
					mavenVersion.major(),
					mavenVersion.minor(),
					mavenVersion.bugfix(),
					mavenVersion.suffix()
				);
		}
		
		@Override
		public OsgiVersion createOsgiSnapshot(Integer major, Integer minor, Integer bugfix, String qualifier, VersionFactory versionFactory) {
			return versionFactory.createOsgiVersion(major, minor, bugfix, qualifier);
		}
		
		@Override
		public OsgiVersion createOsgiRelease(Integer major, Integer minor, Integer bugfix, String qualifier, VersionFactory versionFactory) {
			return versionFactory.createOsgiVersion(major, minor, bugfix, qualifier);
		}
	};

	public static MavenToOsgiVersionMappingStrategy create(String key) {
		for (MavenToOsgiVersionMappingStrategy strategy : values()) {
			if (strategy.key.equalsIgnoreCase(key)) {
				return strategy;
			}
		}
		throw new IllegalArgumentException("Unknown strategy key: " + key);
	}

	private final String key;
	private final boolean useCustomQualifiers;
	
	private MavenToOsgiVersionMappingStrategy(String key, boolean useCustomQualifiers) {
		this.key = key;
		this.useCustomQualifiers = useCustomQualifiers;
	}
	
	public String getKey() {
		return key;
	}
	
	public boolean useCustomQualifiers() {
		return useCustomQualifiers;
	}
	
	public abstract OsgiVersion createOsgiFromMaven(MavenVersion mavenVersion, VersionFactory versionFactory);
	
	public abstract OsgiVersion createOsgiSnapshot(Integer major, Integer minor, Integer bugfix, String qualifier, VersionFactory versionFactory);
	
	public abstract OsgiVersion createOsgiRelease(Integer major, Integer minor, Integer bugfix, String qualifier, VersionFactory versionFactory);

}
