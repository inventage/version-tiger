package com.inventage.tools.versiontiger.universedefinition;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;

import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Element;

public class ProjectUniverseFactory {

	private static final Logger LOGGER = UniverseDefinitionPlugin.getDefault().getLogger();

	private static final String TAG_PROJECTUNIVERSE = "projectuniverse";
	private static final String TAG_PROJECT = "project";
	private static final String TAG_PROJECT_ROOT = "projectRoot";
	private static final String TAG_IGNORE = "ignore";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_LOCATION = "location";
	private static final String ATTRIBUTE_ID = "id";

	public ProjectUniverse create(File file, VersioningLogger logger) {
		String fileContent = new FileHandler().readFileContent(file);

		String id = file.getAbsolutePath();
		Element projectUniverseElement = new XmlHandler().getElement(fileContent, TAG_PROJECTUNIVERSE);
		Attribute nameAttribute = projectUniverseElement.getAttribute(ATTRIBUTE_NAME);
		String name = nameAttribute == null ? null : nameAttribute.getValue();

		ProjectUniverse universe = createEmptyUniverse(id, name, logger);
		
		addProjectLocations(universe, projectUniverseElement);
		addProjectRootLocations(universe, projectUniverseElement);
		ignoreProjectIds(universe, projectUniverseElement);
		
		return universe;
	}

	private ProjectUniverse createEmptyUniverse(String id, String name, VersioningLogger logger) {
		return UniverseDefinitionPlugin.getDefault().getVersioning().createUniverse(id, name, logger);
	}

	private void addProjectLocations(ProjectUniverse universe, Element projectUniverseElement) {
		for (Element projectElement : projectUniverseElement.getChildren(TAG_PROJECT)) {
			Attribute locationAttribute = projectElement.getAttribute(ATTRIBUTE_LOCATION);
			if (locationAttribute != null) {
				Project project = universe.createProjectFromPath(substituteVariables(locationAttribute.getValue()));
				if (project != null) {
					universe.addProject(project);
				}
			}
		}
	}

	private void addProjectRootLocations(ProjectUniverse universe, Element projectUniverseElement) {
		for (Element projectElement : projectUniverseElement.getChildren(TAG_PROJECT_ROOT)) {
			Attribute locationAttribute = projectElement.getAttribute(ATTRIBUTE_LOCATION);
			if (locationAttribute != null) {
				String directoryPath = substituteVariables(locationAttribute.getValue());
				try {
					universe.addRootProjectPath(directoryPath);
				}
				catch (IllegalStateException e) {
					universe.addProject(universe.createProjectFromPath(directoryPath));
				}
			}
		}
	}

	private String substituteVariables(String path) {
		try {
			return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(path);
		} catch (CoreException e) {
			LOGGER.error("Failed to substitute varibales in ''{0}''", path);
		}
		return path;
	}

	private void ignoreProjectIds(ProjectUniverse universe, Element projectUniverseElement) {
		for (Element ignoreElement : projectUniverseElement.getChildren(TAG_IGNORE)) {
			Attribute id = ignoreElement.getAttribute(ATTRIBUTE_ID);
			if (id != null) {
				universe.removeProject(id.getValue());
			}
		}
	}

}
