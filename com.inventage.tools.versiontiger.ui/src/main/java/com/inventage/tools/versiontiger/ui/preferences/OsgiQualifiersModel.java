package com.inventage.tools.versiontiger.ui.preferences;

public class OsgiQualifiersModel extends AbstractPropertyChangeSupport {
	
	public static final String PN_OSGI_RELEASE_QUALIFIER = "osgiReleaseQualifier";
	public static final String PN_OSGI_SNAPSHOT_QUALIFIER = "osgiSnapshotQualifier";
	
	private String osgiReleaseQualifier = "";
	private String osgiSnapshotQualifier = "";
	
	private OsgiQualifiersStore store;
	
	public OsgiQualifiersModel(OsgiQualifiersStore store) {
		this.store = store;
	}
	
	public void setOsgiReleaseQualifier(String osgiReleaseQualifier) {
		this.osgiReleaseQualifier = osgiReleaseQualifier;
	}
	
	public String getOsgiReleaseQualifier() {
		return osgiReleaseQualifier;
	}
	
	public void setOsgiSnapshotQualifier(String osgiSnapshotQualifier) {
		this.osgiSnapshotQualifier = osgiSnapshotQualifier;
	}
	
	public String getOsgiSnapshotQualifier() {
		return osgiSnapshotQualifier;
	}
	
	public void load() {
		osgiReleaseQualifier = store.loadReleaseQualifier();
		osgiSnapshotQualifier = store.loadSnapshotQualifier();
	}
	
	public void save() {
		store.saveReleaseQualifier(osgiReleaseQualifier);
		store.saveSnapshotQualifier(osgiSnapshotQualifier);
	}
}
