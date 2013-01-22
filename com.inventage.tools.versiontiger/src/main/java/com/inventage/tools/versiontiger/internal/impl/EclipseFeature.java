package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;
import java.util.List;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Element;

class EclipseFeature extends MavenProjectImpl {

	private String featureContent;

	EclipseFeature(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		super(projectPath, logger, versionFactory);
	}

	@Override
	public void setVersion(MavenVersion newVersion) {
		MavenVersion oldVersion = getVersion();
		
		super.setVersion(newVersion);

		setFeatureVersion(asOsgiVersion(newVersion), asOsgiVersion(oldVersion));
	}

	private OsgiVersion asOsgiVersion(MavenVersion mavenVersion) {
		return getVersionFactory().createOsgiVersion(mavenVersion);
	}

	private void setFeatureVersion(OsgiVersion newVersion, OsgiVersion oldVersion) {
		featureContent = new XmlHandler().writeAttribute(getFeatureXmlContent(), "feature", "version", newVersion.toString());

		new FileHandler().writeFileContent(getFeatureXmlFile(), featureContent);

		logSuccess(getFeatureXmlFile() + ": feature#version = " + newVersion, oldVersion, newVersion);
	}

	@Override
	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion) {
		super.updateReferencesFor(id, oldVersion, newVersion);

		updateFeatureXmlReferencesFor(id, asOsgiVersion(oldVersion), asOsgiVersion(newVersion));
	}

	private void updateFeatureXmlReferencesFor(String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		Element featureElement = new XmlHandler().getElement(getFeatureXmlContent(), "feature");

		if (updatePluginReferences(featureElement, "plugin", id, oldVersion, newVersion)
				| updatePluginReferences(featureElement, "includes", id, oldVersion, newVersion)) {

			featureContent = featureElement.getDocument().toXML();
			new FileHandler().writeFileContent(getFeatureXmlFile(), featureContent);
		}
	}

	private boolean updatePluginReferences(Element featureElement, String elementName, String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		boolean hasModifications = false;

		List<Element> children = featureElement.getChildren(elementName);
		for (Element child : children) {
			Attribute idAttribute = child.getAttribute("id");
			if (idAttribute != null && id.equals(idAttribute.getValue())) {
				Attribute versionAttribute = child.getAttribute("version");
				if (versionAttribute != null && (oldVersion == null || oldVersion.toString().equals(versionAttribute.getValue()))) {
					versionAttribute.setValue(newVersion.toString());
					hasModifications = true;

					logReferenceSuccess(getFeatureXmlFile() + ": " + child.getChildPath() + "#version = " + newVersion, oldVersion, newVersion, id);
				}
			}
		}

		return hasModifications;
	}

	private String getFeatureXmlContent() {
		if (featureContent == null) {
			featureContent = new FileHandler().readFileContent(getFeatureXmlFile());
		}

		return featureContent;
	}

	private File getFeatureXmlFile() {
		return new File(projectPath(), "feature.xml");
	}

}
