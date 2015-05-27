package com.inventage.tools.versiontiger.universedefinition.fromfile;

import java.io.File;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import com.google.common.collect.Sets;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.universedefinition.ProjectUniverseFactory;
import com.inventage.tools.versiontiger.universedefinition.ProjectUniverseProvider;
import com.inventage.tools.versiontiger.universedefinition.ui.VersioningPreferencePage;

public class FileProjectUniverseProvider implements ProjectUniverseProvider {

	@Override
	public Object create() throws CoreException {
		return this;
	}

	@Override
	public Set<ProjectUniverse> getProjectUniverses(VersioningLogger logger) {
		Set<ProjectUniverse> universes = Sets.newHashSet();
		for (File file : getFilesFromWorkspacePreference()) {
			ProjectUniverse universe = new ProjectUniverseFactory().create(file, logger);
			universes.add(universe);
		}
		return universes;
	}

	private Set<File> getFilesFromWorkspacePreference() {
		return new FileLocationStore(VersioningPreferencePage.UNIVERSE_DEFINITION_FILE_LOCATIONS_PREFERENCE).read();
	}
}
