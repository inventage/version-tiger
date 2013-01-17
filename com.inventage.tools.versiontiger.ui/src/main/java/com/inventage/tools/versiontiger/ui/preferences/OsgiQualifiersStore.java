package com.inventage.tools.versiontiger.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import com.inventage.tools.versiontiger.OsgiVersion;

public class OsgiQualifiersStore {

	public static final String OSGI_RELEASE_QUALIFIER_PREFERENCE = "osgiReleaseQualifier"; //$NON-NLS-1$
	public static final String OSGI_SNAPSHOT_QUALIFIER_PREFERENCE = "osgiSnapshotQualifier"; //$NON-NLS-1$
	
	private IPreferenceStore preferenceStore;
	
	public OsgiQualifiersStore(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
		
		this.preferenceStore.setDefault(OSGI_RELEASE_QUALIFIER_PREFERENCE, OsgiVersion.OSGI_DEFAULT_RELEASE_SUFFIX);
		this.preferenceStore.setDefault(OSGI_SNAPSHOT_QUALIFIER_PREFERENCE, OsgiVersion.OSGI_DEFAULT_SNAPSHOT_SUFFIX);
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
	
	public boolean containsReleaseQualifier() {
		return preferenceStore.contains(OSGI_RELEASE_QUALIFIER_PREFERENCE);
	}
	
	public boolean containsSnapshotQualifier() {
		return preferenceStore.contains(OSGI_SNAPSHOT_QUALIFIER_PREFERENCE);
	}
}
