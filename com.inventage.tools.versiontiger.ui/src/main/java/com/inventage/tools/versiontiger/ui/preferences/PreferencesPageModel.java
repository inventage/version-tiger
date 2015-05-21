package com.inventage.tools.versiontiger.ui.preferences;

import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;

public class PreferencesPageModel extends AbstractPropertyChangeSupport {
	
	public static final String PN_OSGI_RELEASE_QUALIFIER = "osgiReleaseQualifier"; //$NON-NLS-1$
	public static final String PN_OSGI_SNAPSHOT_QUALIFIER = "osgiSnapshotQualifier"; //$NON-NLS-1$
	public static final String PN_VERSION_RANGE_CHANGE_STRATEGY = "versionRangeChangeStrategy"; //$NON-NLS-1$
	public static final String PN_VERSION_RANGE_CHANGE_STRATEGY_DESCRIPTION = "versionRangeChangeStrategyDescription"; //$NON-NLS-1$
	
	private String osgiReleaseQualifier = ""; //$NON-NLS-1$
	private String osgiSnapshotQualifier = ""; //$NON-NLS-1$
	private VersionRangeChangeStrategy versionRangeChangeStrategy = VersionRangeChangeStrategy.ADAPTIVE;
	private String versionRangeChangeStrategyDescription = createStrategyDescription(versionRangeChangeStrategy);
	
	private PreferencesStoreUtil store;
	
	public PreferencesPageModel(PreferencesStoreUtil store) {
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
	
	public void setVersionRangeChangeStrategy(VersionRangeChangeStrategy versionRangeChangeStrategy) {
		this.versionRangeChangeStrategy = versionRangeChangeStrategy;
		
		setVersionRangeChangeStrategyDescription(createStrategyDescription(versionRangeChangeStrategy));
	}
	
	public VersionRangeChangeStrategy getVersionRangeChangeStrategy() {
		return versionRangeChangeStrategy;
	}
	
	private String createStrategyDescription(VersionRangeChangeStrategy versionRangeChangeStrategy) {
		switch (versionRangeChangeStrategy) {
		case ADAPTIVE:
			return Messages.PreferencesPageModel_ADAPTIVE;
		case NO_CHANGE:
			return Messages.PreferencesPageModel_NOCHANGE;
		case STRICT:
			return Messages.PreferencesPageModel_STRICT;
		case UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE:
			return Messages.PreferencesPageModel_UPPER_TO_BUGFIX;
		case UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW:
			return Messages.PreferencesPageModel_BUGFIX;
		case UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE:
			return Messages.PreferencesPageModel_UPPER_TO_MAJOR;
		case UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW:
			return Messages.PreferencesPageModel_MAJOR;
		case UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE:
			return Messages.PreferencesPageModel_UPPER_TO_MINOR;
		case UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW:
			return Messages.PreferencesPageModel_MINOR;
		}
		return Messages.PreferencesPageModel_UnknownStrategy;
	}
	
	public String getVersionRangeChangeStrategyDescription() {
		return versionRangeChangeStrategyDescription;
	}
	
	public void setVersionRangeChangeStrategyDescription(String versionRangeChangeStrategyDescription) {
		String oldValue = this.versionRangeChangeStrategyDescription;
		
		this.versionRangeChangeStrategyDescription = versionRangeChangeStrategyDescription;
		
		firePropertyChange(PN_VERSION_RANGE_CHANGE_STRATEGY_DESCRIPTION, oldValue, versionRangeChangeStrategyDescription);
	}
	
	public void load() {
		osgiReleaseQualifier = store.loadReleaseQualifier();
		osgiSnapshotQualifier = store.loadSnapshotQualifier();
		versionRangeChangeStrategy = VersionRangeChangeStrategy.create(store.loadVersionRangeChangeStrategy());
	}
	
	public void save() {
		store.saveReleaseQualifier(osgiReleaseQualifier);
		store.saveSnapshotQualifier(osgiSnapshotQualifier);
		store.saveVersionRangeChangeStrategy(versionRangeChangeStrategy.getKey());
	}
}
