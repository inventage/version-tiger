package com.inventage.tools.versiontiger.ui.edit;

import org.eclipse.osgi.util.NLS;

/**
 * UI Message Constants.
 * 
 * @author Beat Steiger
 */
public class Messages extends NLS {

	static {
		initializeMessages(Messages.class.getName(), Messages.class);
	}

	public static String editVersionWizardPageTitle;
	public static String editVersionWizardPageDescription;
	public static String editVersionWizardPageUniverseDefinitionLabel;
	public static String editVersionWizardPageOsgiVersionLabel;
	public static String editVersionWizardPageMavenVersionLabel;
	public static String editVersionWizardPageOsgiVersionError;
	public static String editVersionWizardPageMavenVersionError;
	public static String editVersionWizardPageUniverseGroupText;
	public static String editVersionWizardPageVersionGroupText;

	public static String editVersionWizardPageColumnHeaderProjectName;
	public static String editVersionWizardPageColumnHeaderCurrentVersion;
	public static String editVersionWizardPageColumnHeaderNewVersion;
	public static String editVersionWizardPageWarningToolTipWrongVersionFormat;
}
