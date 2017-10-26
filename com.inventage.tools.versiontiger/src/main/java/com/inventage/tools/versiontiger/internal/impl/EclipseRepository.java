package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
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

class EclipseRepository extends MavenProjectImpl {

	private String categoryContent;

	EclipseRepository(String projectPath, VersioningLogger logger, VersionFactory versionFactory) {
		super(projectPath, logger, versionFactory);
	}

	@Override
	public void setVersion(MavenVersion newVersion) {
		OsgiVersion oldOsgiVersion = asOsgiVersion(getVersion());
		OsgiVersion newOsgiVersion = asOsgiVersion(newVersion);

		super.setVersion(newVersion);

		for (File productFile : getProductXmlFiles()) {
			setProductVersion(newOsgiVersion, oldOsgiVersion, productFile);
		}
	}

	private OsgiVersion asOsgiVersion(MavenVersion mavenVersion) {
		return mavenVersion == null ? null : getVersionFactory().createOsgiVersion(mavenVersion);
	}

	private void setProductVersion(OsgiVersion newVersion, OsgiVersion oldVersion, File productFile) {
		String content = new FileHandler().readFileContent(productFile);

		content = new XmlHandler().writeAttribute(content, "product", "version", newVersion.toString());

		new FileHandler().writeFileContent(productFile, content);

		logSuccess(productFile + ": product#version = " + newVersion, oldVersion, newVersion);
	}

	@Override
	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion, ProjectUniverse projectUniverse) {
		super.updateReferencesFor(id, oldVersion, newVersion, projectUniverse);

		OsgiVersion oldOsgiVersion = asOsgiVersion(oldVersion);
		OsgiVersion newOsgiVersion = asOsgiVersion(newVersion);
		
		for (File productFile : getProductXmlFiles()) {
			updateProductReferences(productFile, id, oldOsgiVersion, newOsgiVersion, "product/features", "feature");
			updateProductReferences(productFile, id, oldOsgiVersion, newOsgiVersion, "product/plugins", "plugin");
			
			if (id().equals(id)) {
				setProductVersion(newOsgiVersion, oldOsgiVersion, productFile);
			}
		}

		updateCategoryFeatureReferences(getCategoryXmlFile(), id, oldOsgiVersion, newOsgiVersion);
	}

	private void updateProductReferences(File file, String id, OsgiVersion oldVersion, OsgiVersion newVersion, String elementsPath, String elementName) {
		String content = new FileHandler().readFileContent(file);
		Element elements = new XmlHandler().getElement(content, elementsPath);

		if (elements != null) {
			boolean hasModifications = false;
			for (Element element : elements.getChildren(elementName)) {
				Attribute idAttribute = element.getAttribute("id");
				Attribute versionAttribute = element.getAttribute("version");

				if (idAttribute != null && versionAttribute != null && id.equals(idAttribute.getValue())) {
					if (oldVersion == null || oldVersion.toString().equals(versionAttribute.getValue())) {
						versionAttribute.setValue(newVersion.toString());
						hasModifications = true;

						logReferenceSuccess(file + ": " + element.getChildPath() + "#version = " + newVersion, oldVersion, newVersion, id);
					}
				}
			}

			if (hasModifications) {
				content = elements.getDocument().toXML();
				new FileHandler().writeFileContent(file, content);
			}
		}
	}

	private void updateCategoryFeatureReferences(File categoryXmlFile, String id, OsgiVersion oldVersion, OsgiVersion newVersion) {
		if (categoryXmlFile.exists()) {
			String content = getCategoryXmlContent();
			Element siteElement = new XmlHandler().getElement(content, "site");

			boolean hasModifications = false;
			for (Element featureElement : siteElement.getChildren("feature")) {
				Attribute idAttribute = featureElement.getAttribute("id");
				Attribute versionAttribute = featureElement.getAttribute("version");

				if (idAttribute != null && versionAttribute != null && id.equals(idAttribute.getValue())) {
					String attributeOldVersion = versionAttribute.getValue();
					if (oldVersion == null || oldVersion.toString().equals(attributeOldVersion)) {
						hasModifications = true;

						versionAttribute.setValue(newVersion.toString());
						logReferenceSuccess(categoryXmlFile + ": " + featureElement.getChildPath() + "#version = " + newVersion, oldVersion, newVersion, id);

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
							logReferenceSuccess(categoryXmlFile + ": " + featureElement.getChildPath() + "#url = " + newUrl, oldVersion, newVersion, id);
						}
					}
				}
			}

			if (hasModifications) {
				categoryContent = siteElement.getDocument().toXML();
				new FileHandler().writeFileContent(categoryXmlFile, categoryContent);
			}
		}
	}

	private String getCategoryXmlContent() {
		if (categoryContent == null) {
			categoryContent = new FileHandler().readFileContent(getCategoryXmlFile());
		}

		return categoryContent;
	}

	private File getCategoryXmlFile() {
		return new File(projectPath(), "category.xml");
	}

	private List<File> getProductXmlFiles() {
		String[] names = new File(projectPath()).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".product");
			}
		});

		List<File> result = new ArrayList<File>();
		if (names != null) {
			for (String name : names) {
				result.add(new File(projectPath(), name));
			}
		}

		return result;
	}

}
