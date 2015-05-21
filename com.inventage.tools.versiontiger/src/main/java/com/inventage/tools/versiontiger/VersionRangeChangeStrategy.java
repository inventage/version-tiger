package com.inventage.tools.versiontiger;

import com.inventage.tools.versiontiger.internal.manifest.VersionRange;

public enum VersionRangeChangeStrategy {

	UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE("upperToMajor") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
			versionRange.setEndVersion(newVersion.incrementMajorAndSnapshot().withoutQualifier());
			versionRange.setEndInclusive(false);
		}
	},
	
	UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE("upperToMinor") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
			versionRange.setEndVersion(newVersion.incrementMinorAndSnapshot().withoutQualifier());
			versionRange.setEndInclusive(false);
		}
	},
	
	UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE("upperToBugfix") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
			versionRange.setEndVersion(newVersion.incrementBugfixAndSnapshot().withoutQualifier());
			versionRange.setEndInclusive(false);
		}
	},
	
	UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW("major") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
			versionRange.setStartVersion(newVersion.withoutQualifier());
			versionRange.setStartInclusive(true);
			versionRange.setEndVersion(newVersion.incrementMajorAndSnapshot().withoutQualifier());
			versionRange.setEndInclusive(false);
		}
	},
	
	UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW("minor") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
			versionRange.setStartVersion(newVersion.withoutQualifier());
			versionRange.setStartInclusive(true);
			versionRange.setEndVersion(newVersion.incrementMinorAndSnapshot().withoutQualifier());
			versionRange.setEndInclusive(false);
		}
	},
	
	UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW("bugfix") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
			versionRange.setStartVersion(newVersion.withoutQualifier());
			versionRange.setStartInclusive(true);
			versionRange.setEndVersion(newVersion.incrementBugfixAndSnapshot().withoutQualifier());
			versionRange.setEndInclusive(false);
		}
	},
	
	STRICT("strict") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
			versionRange.setStartVersion(newVersion);
			versionRange.setStartInclusive(true);
			versionRange.setEndVersion(newVersion);
			versionRange.setEndInclusive(true);
		}
	},
	
	ADAPTIVE("adaptive") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
			OsgiVersion origStartVersion = versionRange.getStartVersion();
			OsgiVersion origEndVersion = versionRange.getEndVersion();
			
			versionRange.setStartVersion(changeToNewIfMatches(origStartVersion, oldVersion, newVersion));
			
			if (versionRange.isRange()) {
				OsgiVersion changed = changeToNewWithoutQualifierIfMatches(origEndVersion, origStartVersion.incrementMajorAndSnapshot(), newVersion.incrementMajorAndSnapshot());
				
				if (origEndVersion.equals(changed)) {
					changed = changeToNewWithoutQualifierIfMatches(origEndVersion, origStartVersion.incrementMinorAndSnapshot(), newVersion.incrementMinorAndSnapshot());
				}
				
				if (origEndVersion.equals(changed)) {
					changed = changeToNewWithoutQualifierIfMatches(origEndVersion, origStartVersion.incrementBugfixAndSnapshot(), newVersion.incrementBugfixAndSnapshot());
				}
				
				if (origEndVersion.equals(oldVersion)) {
					changed = changeToNewIfMatches(origEndVersion, oldVersion, newVersion);
				}
				
				versionRange.setEndVersion(changed);
			}
		}
		
		private OsgiVersion changeToNewIfMatches(OsgiVersion current, OsgiVersion comparisonVersion, OsgiVersion newVersion) {
			if (comparisonVersion == null) {
				return newVersion.withoutQualifier();
			}
			else if (current.qualifier() != null && !current.qualifier().isEmpty()) {
				if (current.equals(comparisonVersion)) {
					return newVersion;
				}
			} else if (current.equals(comparisonVersion.withoutQualifier())) {
				return newVersion.withoutQualifier();
			}
			return current;
		}
		
		private OsgiVersion changeToNewWithoutQualifierIfMatches(OsgiVersion current, OsgiVersion comparisonVersion, OsgiVersion newVersion) {
			if (current.withoutQualifier().equals(comparisonVersion.withoutQualifier())) {
				return newVersion.withoutQualifier();
			}
			return current;
		}
	},
	
	NO_CHANGE("noChange") {
		@Override
		public void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion) {
		}
	};
	
	
	private final String key;

	private VersionRangeChangeStrategy(String key) {
		this.key = key;
	}
	
	
	public static VersionRangeChangeStrategy create(String key) {
		for (VersionRangeChangeStrategy strategy : values()) {
			if (strategy.key.equalsIgnoreCase(key)) {
				return strategy;
			}
		}
		throw new IllegalArgumentException("Unknown strategy key: " + key);
	}

	public String getKey() {
		return key;
	}

	public abstract void change(VersionRange versionRange, OsgiVersion oldVersion, OsgiVersion newVersion);

}
