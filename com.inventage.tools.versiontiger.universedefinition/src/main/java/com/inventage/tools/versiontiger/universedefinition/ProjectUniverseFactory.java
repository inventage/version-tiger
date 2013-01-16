package com.inventage.tools.versiontiger.universedefinition;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;

import com.google.common.collect.Sets;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Element;

public class ProjectUniverseFactory {
	private static final Logger LOGGER = UniverseDefinitionPlugin.getDefault().getLogger();

	private static final String PROJECTUNIVERSE_TAG = "projectuniverse";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String PROJECT_TAG = "project";
	private static final String LOCATION_ATTRIBUTE = "location";

	public ProjectUniverse create(String id, String name, Collection<String> projectLocations, VersioningLogger logger) {
		ProjectUniverse universe = UniverseDefinitionPlugin.getDefault().getVersioning().createUniverse(id, name, logger);
		return addProjectLocations(universe, projectLocations);
	}

	public ProjectUniverse create(File file, VersioningLogger logger) {
		String fileContent = new FileHandler().readFileContent(file);

		String id = file.getAbsolutePath();
		Element projectuniverseElement = new XmlHandler().getElement(fileContent, PROJECTUNIVERSE_TAG);
		Attribute nameAttribute = projectuniverseElement.getAttribute(NAME_ATTRIBUTE);
		String name = nameAttribute == null ? null : nameAttribute.getValue();

		Set<String> projectLocations = Sets.newHashSet();
		for (Element projectElement : projectuniverseElement.getChildren(PROJECT_TAG)) {
			Attribute locationAttribute = projectElement.getAttribute(LOCATION_ATTRIBUTE);
			if (locationAttribute != null) {
				projectLocations.add(locationAttribute.getValue());
			}
		}

		return create(id, name, projectLocations, logger);
	}

	private ProjectUniverse addProjectLocations(ProjectUniverse universe, Collection<String> projectLocations) {
		for (String projectLocation : projectLocations) {
			universe.addProjectPath(substituteVariables(projectLocation));
		}
		return universe;
	}

	private String substituteVariables(String path) {
		try {
			return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(path);
		} catch (CoreException e) {
			LOGGER.error("Failed to substitute varibales in ''{0}''", path);
		}
		return null;
	}

}
