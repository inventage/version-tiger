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
	
}
