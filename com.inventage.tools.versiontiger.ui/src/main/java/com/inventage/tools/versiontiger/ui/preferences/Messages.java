package com.inventage.tools.versiontiger.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * UI Preferences Message Constants.
 * 
 * @author Nico Waldispuehl
 */
public class Messages extends NLS {

	static {
		initializeMessages(Messages.class.getName(), Messages.class);
	}
	
	public static String preferencesOsgiQualifierTitleLabel;
	public static String preferencesOsgiReleaseQualifierLabel;
	public static String preferencesOsgiSnapshotQualifierLabel;
	public static String PreferencesPageModel_ADAPTIVE;
	public static String PreferencesPageModel_BUGFIX;
	public static String PreferencesPageModel_MAJOR;
	public static String PreferencesPageModel_MINOR;
	public static String PreferencesPageModel_NOCHANGE;
	public static String PreferencesPageModel_STRICT;
	public static String PreferencesPageModel_UnknownStrategy;
	public static String PreferencesPageModel_UPPER_TO_BUGFIX;
	public static String PreferencesPageModel_UPPER_TO_MAJOR;
	public static String PreferencesPageModel_UPPER_TO_MINOR;
	public static String PreferencesPageModel_VERSION_MAPPING_SNAPSHOT_DISTINCTION;
	public static String PreferencesPageModel_VERSION_MAPPING_SUFFIX_TO_QUALIFIER;
	public static String VersionTigerPreferencesPage_DependencyGroup;
	
}
