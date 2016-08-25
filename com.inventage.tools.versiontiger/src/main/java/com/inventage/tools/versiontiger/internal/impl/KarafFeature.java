package com.inventage.tools.versiontiger.internal.impl;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.util.FileHandler;
import com.inventage.tools.versiontiger.util.XmlHandler;

import de.pdark.decentxml.Element;

class KarafFeature {

	private static final String ELEMENT_ROOT = "features";
	private static final String ELEMENT_BUNDLE = "bundle";
	private static final String BUNDLE_MVN_PREFIX = "mvn:";
	private static final String GAV_SEPARATOR = "/";
	
	private final File filePath;

	private String content;

	KarafFeature(File filePath) {
		this.filePath = filePath;
	}
	
	Set<String> updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion) {
		XmlHandler xmlHandler = new XmlHandler();
		Set<String> result = new LinkedHashSet<String>();
		
		Element features = xmlHandler.getElement(getFileContent(), ELEMENT_ROOT);
		for (Element feature : features.getChildren()) {
			for (Element bundle : feature.getChildren()) {
				if (ELEMENT_BUNDLE.equals(bundle.getName())) {
					String bundleReference = updateBundleReferencesFor(id, oldVersion, newVersion, bundle);
					if (bundleReference != null) {
						result.add(bundleReference);
					}
				}
			}
		}
		
		if (!result.isEmpty()) {
			content = features.getDocument().toXML();
			new FileHandler().writeFileContent(filePath, content);
		}
		
		return result;
	}

	private String updateBundleReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion, Element bundle) {
		String bundleReference = bundle.getTrimmedText();
		if (bundleReference.startsWith(BUNDLE_MVN_PREFIX)) {
			bundleReference = bundleReference.substring(4);
			String[] gav = bundleReference.split(GAV_SEPARATOR, 3);
			if (gav != null && gav.length == 3) {
				if (id.equals(gav[1]) && (oldVersion == null || gav[2].equals(oldVersion.toString()))) {
					bundleReference = BUNDLE_MVN_PREFIX + gav[0] + GAV_SEPARATOR + gav[1] + GAV_SEPARATOR + newVersion;
					bundle.setText(bundleReference);
					return bundle.getChildPath() + " = " + bundleReference;
				}
			}
		}
		return null;
	}

	private String getFileContent() {
		if (content == null) {
			content = new FileHandler().readFileContent(filePath);
		}

		return content;
	}

}
