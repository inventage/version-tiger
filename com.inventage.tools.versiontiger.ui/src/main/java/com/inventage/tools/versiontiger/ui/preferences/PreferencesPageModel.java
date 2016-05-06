package com.inventage.tools.versiontiger.ui.preferences;

import com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;

public class PreferencesPageModel extends AbstractPropertyChangeSupport {
	
	public static final String PN_OSGI_RELEASE_QUALIFIER = "osgiReleaseQualifier"; //$NON-NLS-1$
	public static final String PN_OSGI_SNAPSHOT_QUALIFIER = "osgiSnapshotQualifier"; //$NON-NLS-1$
	public static final String PN_VERSION_RANGE_CHANGE_STRATEGY = "versionRangeChangeStrategy"; //$NON-NLS-1$
	public static final String PN_VERSION_RANGE_CHANGE_STRATEGY_DESCRIPTION = "versionRangeChangeStrategyDescription"; //$NON-NLS-1$
	public static final String PN_MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY = "mavenToOsgiVersionMappingStrategy"; //$NON-NLS-1$
	public static final String PN_MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY_DESCRIPTION = "mavenToOsgiVersionMappingStrategyDescription"; //$NON-NLS-1$
	
	private String osgiReleaseQualifier = ""; //$NON-NLS-1$
	private String osgiSnapshotQualifier = ""; //$NON-NLS-1$
	private VersionRangeChangeStrategy versionRangeChangeStrategy = VersionRangeChangeStrategy.ADAPTIVE;
	private String versionRangeChangeStrategyDescription = createStrategyDescription(versionRangeChangeStrategy);
	private MavenToOsgiVersionMappingStrategy mavenToOsgiVersionMappingStrategy = MavenToOsgiVersionMappingStrategy.OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION;
	private String mavenToOsgiVersionMappingStrategyDescription = createStrategyDescription(mavenToOsgiVersionMappingStrategy);
	
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
	
	public void setMavenToOsgiVersionMappingStrategy(MavenToOsgiVersionMappingStrategy mavenToOsgiVersionMappingStrategy) {
		MavenToOsgiVersionMappingStrategy oldValue = this.mavenToOsgiVersionMappingStrategy;
		
		this.mavenToOsgiVersionMappingStrategy = mavenToOsgiVersionMappingStrategy;
		
		setMavenToOsgiVersionMappingStrategyDescription(createStrategyDescription(mavenToOsgiVersionMappingStrategy));
		firePropertyChange(PN_MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY, oldValue, mavenToOsgiVersionMappingStrategy);
	}
	
	public MavenToOsgiVersionMappingStrategy getMavenToOsgiVersionMappingStrategy() {
		return mavenToOsgiVersionMappingStrategy;
	}
	
	private String createStrategyDescription(MavenToOsgiVersionMappingStrategy mavenToOsgiVersionMappingStrategy) {
		switch (mavenToOsgiVersionMappingStrategy) {
		case MAVEN_SUFFIX_TO_OSGI_QUALIFIER:
			return Messages.PreferencesPageModel_VERSION_MAPPING_SUFFIX_TO_QUALIFIER;
		case OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION:
			return Messages.PreferencesPageModel_VERSION_MAPPING_SNAPSHOT_DISTINCTION;
		}
		return Messages.PreferencesPageModel_UnknownStrategy;
	}
	
	public String getMavenToOsgiVersionMappingStrategyDescription() {
		return mavenToOsgiVersionMappingStrategyDescription;
	}
	
	public void setMavenToOsgiVersionMappingStrategyDescription(String mavenToOsgiVersionMappingStrategyDescription) {
		String oldValue = this.mavenToOsgiVersionMappingStrategyDescription;
		
		this.mavenToOsgiVersionMappingStrategyDescription = mavenToOsgiVersionMappingStrategyDescription;
		
		firePropertyChange(PN_MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY_DESCRIPTION, oldValue, this.mavenToOsgiVersionMappingStrategyDescription);
	}
	
	public void load() {
		osgiReleaseQualifier = store.loadReleaseQualifier();
		osgiSnapshotQualifier = store.loadSnapshotQualifier();
		setVersionRangeChangeStrategy(VersionRangeChangeStrategy.create(store.loadVersionRangeChangeStrategy()));
		setMavenToOsgiVersionMappingStrategy(MavenToOsgiVersionMappingStrategy.create(store.loadMavenToOsgiVersionMappingStrategy()));
	}
	
	public void save() {
		store.saveReleaseQualifier(osgiReleaseQualifier);
		store.saveSnapshotQualifier(osgiSnapshotQualifier);
		store.saveVersionRangeChangeStrategy(versionRangeChangeStrategy.getKey());
		store.saveMavenToOsgiVersionMappingStrategy(mavenToOsgiVersionMappingStrategy.getKey());
	}
}
