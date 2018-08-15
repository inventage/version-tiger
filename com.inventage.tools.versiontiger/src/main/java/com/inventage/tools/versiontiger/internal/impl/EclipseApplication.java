package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.ProjectId;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Element;

class EclipseApplication extends MavenProjectImpl {

	private String productContent;

	EclipseApplication(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		super(projectPath, logger, versionFactory);
	}

	@Override
	public void setVersion(MavenVersion newVersion) {
		MavenVersion oldVersion = getVersion();
		
		super.setVersion(newVersion);

		setProductVersion(asOsgiVersion(newVersion), asOsgiVersion(oldVersion));
	}

	private OsgiVersion asOsgiVersion(MavenVersion mavenVersion) {
		return mavenVersion == null ? null : getVersionFactory().createOsgiVersion(mavenVersion);
	}

	private void setProductVersion(OsgiVersion newVersion, OsgiVersion oldVersion) {
		updateProductVersion(id(), oldVersion, newVersion);

		new FileHandler().writeFileContent(getProductXmlFile(), productContent);
	}

	@Override
	public void updateReferencesFor(ProjectId id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		super.updateReferencesFor(id, oldVersion, newVersion, projectUniverse);

		OsgiVersion oldOsgiVersion = asOsgiVersion(oldVersion);
		OsgiVersion newOsgiVersion = asOsgiVersion(newVersion);
		
		if (updateFeatureReferences(id, oldOsgiVersion, newOsgiVersion) | updateProductVersion(id, oldOsgiVersion, newOsgiVersion)) {
			new FileHandler().writeFileContent(getProductXmlFile(), productContent);
		}
	}
	
	private boolean updateProductVersion(ProjectId id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		if (id().equalsIgnoreGroupIfUnknown(id)) {
			productContent = new XmlHandler().writeAttribute(getProductXmlContent(), "product", "version", newVersion.toString());
			
			logSuccess(getProductXmlFile() + ": product#version = " + newVersion, oldVersion, newVersion);
			return true;
		}
		return false;
	}

	private boolean updateFeatureReferences(ProjectId id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		Element featuresElement = new XmlHandler().getElement(getProductXmlContent(), "product/features");
		if (featuresElement == null) {
			return false;
		}

		boolean hasModifications = false;
		for (Element featureElement : featuresElement.getChildren("feature")) {
			Attribute idAttribute = featureElement.getAttribute("id");
			Attribute versionAttribute = featureElement.getAttribute("version");

			if (idAttribute != null && versionAttribute != null && id.getArtifactId().equals(idAttribute.getValue())) {
				if (oldVersion == null || oldVersion.toString().equals(versionAttribute.getValue())) {
					versionAttribute.setValue(newVersion.toString());
					hasModifications = true;

					logReferenceSuccess(getProductXmlFile() + ": " + featureElement.getChildPath() + "#version = " + newVersion, oldVersion, newVersion, id);
				}
			}
		}
		
		if (hasModifications) {
			productContent = featuresElement.getDocument().toXML();
		}
		return hasModifications;
	}

	private String getProductXmlContent() {
		if (productContent == null) {
			productContent = new FileHandler().readFileContent(getProductXmlFile());
		}
		return productContent;
	}

	private File getProductXmlFile() {
		return new File(projectPath(), id().getArtifactId() + ".product");
	}

}
