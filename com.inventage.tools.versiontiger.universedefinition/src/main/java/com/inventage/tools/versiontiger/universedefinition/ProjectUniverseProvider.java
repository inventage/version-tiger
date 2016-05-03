package com.inventage.tools.versiontiger.universedefinition;

import java.util.Set;

import org.eclipse.core.runtime.IExecutableExtensionFactory;

import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;

/**
 * @author Beat Steiger
 */
public interface ProjectUniverseProvider extends IExecutableExtensionFactory {

	/**
	 * @return the ProjectUniverseModel of this
	 *         {@link ProjectUniverseProvider}.
	 */
	Set<ProjectUniverse> getProjectUniverses(VersioningLogger logger);

}
