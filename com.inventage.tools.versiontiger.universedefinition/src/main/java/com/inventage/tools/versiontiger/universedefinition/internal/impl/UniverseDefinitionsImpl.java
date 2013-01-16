package com.inventage.tools.versiontiger.universedefinition.internal.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.universedefinition.ProjectUniverseProvider;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitionPlugin;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitions;

public class UniverseDefinitionsImpl implements UniverseDefinitions {

	public static final String EXTENSION_ATTRIBUTE_PROJECT_UNIVERSE_PROVIDER_CLASS = "class";

	@Override
	public Set<ProjectUniverse> getProjectUniverses(VersioningLogger logger) {
		Set<ProjectUniverse> projectUniverses = Sets.newHashSet();
		for (ProjectUniverseProvider provider : getProjectUniverseProviders()) {
			projectUniverses.addAll(provider.getProjectUniverses(logger));
		}
		return projectUniverses;
	}

	private List<ProjectUniverseProvider> getProjectUniverseProviders() {
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				UniverseDefinitionPlugin.PROJECT_UNIVERSE_PROVIDERS_EXTENSION_POINT_ID);

		List<ProjectUniverseProvider> universeDefinitions = Lists.newArrayList();
		for (IConfigurationElement configurationElement : configurationElements) {
			ProjectUniverseProvider universeProvider;
			try {
				universeProvider = (ProjectUniverseProvider) configurationElement
						.createExecutableExtension(EXTENSION_ATTRIBUTE_PROJECT_UNIVERSE_PROVIDER_CLASS);
				if (universeProvider != null) {
					universeDefinitions.add(universeProvider);
				}
			} catch (CoreException e) {
				throw new IllegalStateException(MessageFormat.format("Failed to instanciate Project Universe Provider from configuration element {0}",
						configurationElement), e);
			}
		}
		return universeDefinitions;
	}
}
