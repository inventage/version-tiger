package com.inventage.tools.versiontiger.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;

public class PreferencesStoreUtil {

	public static final String OSGI_RELEASE_QUALIFIER_PREFERENCE = "osgiReleaseQualifier"; //$NON-NLS-1$
	public static final String OSGI_SNAPSHOT_QUALIFIER_PREFERENCE = "osgiSnapshotQualifier"; //$NON-NLS-1$
	public static final String VERSION_RANGE_CHANGE_STRATEGY = "versionRangeChangeStrategy"; //$NON-NLS-1$
	public static final String MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY = "mavenToOsgiVersionMappingStrategy"; //$NON-NLS-1$
	
	private final IPreferenceStore preferenceStore;
	
	public PreferencesStoreUtil(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
		
		this.preferenceStore.setDefault(OSGI_RELEASE_QUALIFIER_PREFERENCE, OsgiVersion.OSGI_DEFAULT_RELEASE_SUFFIX);
		this.preferenceStore.setDefault(OSGI_SNAPSHOT_QUALIFIER_PREFERENCE, OsgiVersion.OSGI_DEFAULT_SNAPSHOT_SUFFIX);
		this.preferenceStore.setDefault(VERSION_RANGE_CHANGE_STRATEGY, VersionRangeChangeStrategy.ADAPTIVE.getKey());
		this.preferenceStore.setDefault(MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY, MavenToOsgiVersionMappingStrategy.OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION.getKey());
	}
	
	public String loadReleaseQualifier() {
		return preferenceStore.getString(OSGI_RELEASE_QUALIFIER_PREFERENCE);
	}
	
	public void saveReleaseQualifier(String releaseQualifier) {
		preferenceStore.setValue(OSGI_RELEASE_QUALIFIER_PREFERENCE, releaseQualifier);
	}
	
	public String loadSnapshotQualifier() {
		return preferenceStore.getString(OSGI_SNAPSHOT_QUALIFIER_PREFERENCE);
	}
	
	public void saveSnapshotQualifier(String snapshotQualifier) {
		preferenceStore.setValue(OSGI_SNAPSHOT_QUALIFIER_PREFERENCE, snapshotQualifier);
	}
	
	public String loadVersionRangeChangeStrategy() {
		return preferenceStore.getString(VERSION_RANGE_CHANGE_STRATEGY);
	}

	public void saveVersionRangeChangeStrategy(String mavenToOsgiVersionMappingStrategy) {
		preferenceStore.setValue(VERSION_RANGE_CHANGE_STRATEGY, mavenToOsgiVersionMappingStrategy);
	}
	
	public String loadMavenToOsgiVersionMappingStrategy() {
		return preferenceStore.getString(MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY);
	}

	public void saveMavenToOsgiVersionMappingStrategy(String mavenToOsgiVersionMappingStrategy) {
		preferenceStore.setValue(MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY, mavenToOsgiVersionMappingStrategy);
	}
	
}
