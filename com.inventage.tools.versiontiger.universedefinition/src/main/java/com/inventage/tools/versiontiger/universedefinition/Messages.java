package com.inventage.tools.versiontiger.universedefinition;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	static {
		initializeMessages(Messages.class.getName().toLowerCase(), Messages.class);
	}

	public static String projectUniverseSectionTitle;
	public static String projectUniverseFilePropertyLabel;
	public static String projectUniverseNamePropertyLabel;
	public static String projectUniverseAddButtonText;
	public static String projectUniverseAddButtonTooltip;
	public static String projectUniverseRemoveButtonText;
	public static String projectUniverseRemoveButtonTooltip;
	public static String projectUniverseFileSelectionDialogTitle;
	public static String allWorkspaceProjectsUniverseName;
	public static String mavenRootProjectsUniverseName;
}
