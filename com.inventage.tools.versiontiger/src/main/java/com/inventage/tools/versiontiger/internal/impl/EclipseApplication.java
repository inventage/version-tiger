package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Element;

class EclipseApplication extends MavenProjectImpl {

	private String productContent;

	EclipseApplication(String projectPath, VersioningLogger logger) {
		super(projectPath, logger);
	}

	@Override
	public void setVersion(MavenVersion newVersion) {
		MavenVersion oldVersion = getVersion();
		
		super.setVersion(newVersion);

		setProductVersion(asOsgiVersion(newVersion), asOsgiVersion(oldVersion));
	}

	private OsgiVersion asOsgiVersion(MavenVersion mavenVersion) {
		return new VersionFactory().createOsgiVersion(mavenVersion);
	}

	private void setProductVersion(OsgiVersion newVersion, OsgiVersion oldVersion) {
		productContent = new XmlHandler().writeAttribute(getProductXmlContent(), "product", "version", newVersion.toString());

		new FileHandler().writeFileContent(getProductXmlFile(), productContent);

		logSuccess(getProductXmlFile() + ": product#version = " + newVersion, oldVersion, newVersion);
	}

	@Override
	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion) {
		super.updateReferencesFor(id, oldVersion, newVersion);

		updateFeatureReferences(id, asOsgiVersion(oldVersion), asOsgiVersion(newVersion));
	}

	private void updateFeatureReferences(String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		Element featuresElement = new XmlHandler().getElement(getProductXmlContent(), "product/features");

		boolean hasModifications = false;
		for (Element featureElement : featuresElement.getChildren("feature")) {
			Attribute idAttribute = featureElement.getAttribute("id");
			Attribute versionAttribute = featureElement.getAttribute("version");

			if (idAttribute != null && versionAttribute != null && id.equals(idAttribute.getValue())) {
				if (oldVersion == null || oldVersion.toString().equals(versionAttribute.getValue())) {
					versionAttribute.setValue(newVersion.toString());
					hasModifications = true;

					logSuccess(getProductXmlFile() + ": " + featureElement.getChildPath() + "#version = " + newVersion, oldVersion, newVersion);
				}
			}
		}

		if (hasModifications) {
			productContent = featuresElement.getDocument().toXML();
			new FileHandler().writeFileContent(getProductXmlFile(), productContent);
		}
	}

	private String getProductXmlContent() {
		if (productContent == null) {
			productContent = new FileHandler().readFileContent(getProductXmlFile());
		}
		return productContent;
	}

	private File getProductXmlFile() {
		return new File(projectPath(), id() + ".product");
	}

}
