package com.inventage.tools.versiontiger.universedefinition.mavenrootprojects;

import static com.inventage.tools.versiontiger.universedefinition.UniverseDefinitions.MAVEN_ROOT_PROJECTS_UNIVERSE_ID;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import com.google.common.collect.Sets;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.universedefinition.Messages;
import com.inventage.tools.versiontiger.universedefinition.ProjectUniverseProvider;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitionPlugin;

public class MavenRootProjectsUniverseProvider implements ProjectUniverseProvider {

	@Override
	public Object create() throws CoreException {
		return new MavenRootProjectsUniverseProvider();
	}

	@Override
	public Set<ProjectUniverse> getProjectUniverses(VersioningLogger logger) {
		return Sets.newHashSet(createMavenRootProjectsUniverse(logger));
	}

	private ProjectUniverse createMavenRootProjectsUniverse(VersioningLogger logger) {
		return UniverseDefinitionPlugin.getDefault().getVersioning().createUniverse(MAVEN_ROOT_PROJECTS_UNIVERSE_ID, Messages.mavenRootProjectsUniverseName, logger);
	}

}
