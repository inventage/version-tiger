package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Element;

class EclipseUpdateSite extends MavenProjectImpl {

	private String siteContent;

	EclipseUpdateSite(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		super(projectPath, logger, versionFactory);
	}

	@Override
	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		super.updateReferencesFor(id, oldVersion, newVersion, projectUniverse);

		updateSiteFeatures(id, asOsgiVersion(oldVersion), asOsgiVersion(newVersion));
	}

	private OsgiVersion asOsgiVersion(MavenVersion mavenVersion) {
		return mavenVersion == null ? null : getVersionFactory().createOsgiVersion(mavenVersion);
	}

	private void updateSiteFeatures(String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		Element siteElement = new XmlHandler().getElement(getSiteXmlContent(), "site");

		boolean hasModifications = false;
		for (Element featureElement : siteElement.getChildren("feature")) {
			Attribute idAttribute = featureElement.getAttribute("id");
			if (idAttribute != null && id.equals(idAttribute.getValue())) {
				Attribute versionAttribute = featureElement.getAttribute("version");
				String attributeOldVersion = versionAttribute.getValue();
				if (versionAttribute != null && (oldVersion == null || oldVersion.toString().equals(attributeOldVersion))) {
					versionAttribute.setValue(newVersion.toString());
					hasModifications = true;
					logReferenceSuccess(getSiteXmlFile() + ": " + featureElement.getChildPath() + "#version = " + newVersion, oldVersion, newVersion, id);

					Attribute urlAttribute = featureElement.getAttribute("url");
					if (urlAttribute != null) {
						String oldUrl = urlAttribute.getValue();
						Matcher matcher = Pattern.compile(id + "_" + attributeOldVersion, Pattern.LITERAL).matcher(oldUrl);
						StringBuffer newUrl = new StringBuffer();
						while (matcher.find()) {
							matcher.appendReplacement(newUrl, id + "_" + newVersion);
						}
						matcher.appendTail(newUrl);

						urlAttribute.setValue(newUrl.toString());
						logReferenceSuccess(getSiteXmlFile() + ": " + featureElement.getChildPath() + "#url = " + newUrl, oldVersion, newVersion, id);
					}
				}
			}
		}

		if (hasModifications) {
			siteContent = siteElement.getDocument().toXML();
			new FileHandler().writeFileContent(getSiteXmlFile(), siteContent);
		}
	}

	private String getSiteXmlContent() {
		if (siteContent == null) {
			siteContent = new FileHandler().readFileContent(getSiteXmlFile());
		}
		return siteContent;
	}

	private File getSiteXmlFile() {
		return new File(projectPath(), "site.xml");
	}

}
