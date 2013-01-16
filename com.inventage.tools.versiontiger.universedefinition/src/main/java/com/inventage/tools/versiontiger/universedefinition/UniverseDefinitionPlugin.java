package com.inventage.tools.versiontiger.universedefinition;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.inventage.tools.versiontiger.Versioning;

/**
 * The activator class controls the plug-in life cycle
 */
public class UniverseDefinitionPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.inventage.tools.versiontiger.universedefinition"; //$NON-NLS-1$

	// The universe definition extension point ID
	public static final String PROJECT_UNIVERSE_PROVIDERS_EXTENSION_POINT_ID = PLUGIN_ID + ".projectUniverseProviders"; //$NON-NLS-1$

	// The shared instance
	private static UniverseDefinitionPlugin plugin;

	private Versioning versioning;

	/**
	 * The constructor
	 */
	public UniverseDefinitionPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		versioning = loadVersioning(context);
	}

	private Versioning loadVersioning(BundleContext context) {
		ServiceReference versioningReference = context.getServiceReference(Versioning.class.getName());
		return (Versioning) context.getService(versioningReference);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static UniverseDefinitionPlugin getDefault() {
		return plugin;
	}

	public Versioning getVersioning() {
		return versioning;
	}

	public Logger getLogger() {
		return new Logger(getLog(), PLUGIN_ID);
	}
}
