package com.inventage.tools.versiontiger.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;
import com.inventage.tools.versiontiger.Versioning;
import com.inventage.tools.versiontiger.ui.preferences.PreferencesStoreUtil;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitions;

/**
 * The activator class controls the plug-in life cycle
 */
public class VersioningUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.inventage.tools.versiontiger.ui"; //$NON-NLS-1$

	// The shared instance
	private static VersioningUIPlugin plugin;

	private Versioning versioning;
	private UniverseDefinitions universeDefinitions;

	/**
	 * The constructor
	 */
	public VersioningUIPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		versioning = loadVersioning(context);
		
		/* On first start, we update the versioning settings. They also get updated on every preferences update. */
		PreferencesStoreUtil store = new PreferencesStoreUtil(getPreferenceStore());
		versioning.setMavenToOsgiVersionMappingStrategy(MavenToOsgiVersionMappingStrategy.create(store.loadMavenToOsgiVersionMappingStrategy()));
		versioning.setOsgiReleaseQualifier(store.loadReleaseQualifier());
		versioning.setOsgiSnapshotQualifier(store.loadSnapshotQualifier());
		versioning.setVersionRangeChangeStrategy(VersionRangeChangeStrategy.create(store.loadVersionRangeChangeStrategy()));
		
		universeDefinitions = loadUniverseDefinitions(context);
	}

	private Versioning loadVersioning(BundleContext context) {
		ServiceReference versioningReference = context.getServiceReference(Versioning.class.getName());
		return (Versioning) context.getService(versioningReference);
	}

	private UniverseDefinitions loadUniverseDefinitions(BundleContext context) {
		ServiceReference universeDefinitionsReference = context.getServiceReference(UniverseDefinitions.class.getName());
		return (UniverseDefinitions) context.getService(universeDefinitionsReference);
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
	public static VersioningUIPlugin getDefault() {
		return plugin;
	}

	public Versioning getVersioning() {
		return versioning;
	}

	public UniverseDefinitions getUniverseDefinitions() {
		return universeDefinitions;
	}
}
