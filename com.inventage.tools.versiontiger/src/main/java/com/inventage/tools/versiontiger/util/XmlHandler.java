package com.inventage.tools.versiontiger.util;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Document;
import de.pdark.decentxml.Element;
import de.pdark.decentxml.XMLParser;

public class XmlHandler {

	public Element getElement(String xml, String path) {
		Document document = XMLParser.parse(xml);

		return document.getChild(path);
	}

	public String readElement(String xml, String path) {
		Element child = getElement(xml, path);

		if (child != null) {
			return child.getTrimmedText();
		}

		return null;
	}

	public String writeElement(String xml, String path, String newValue) {
		Element child = getElement(xml, path);

		if (child != null) {
			child.setText(newValue);

			return child.getDocument().toXML();
		}

		return xml;
	}

	public String readAttribute(String xml, String path, String attributeName) {
		Element child = getElement(xml, path);
		if (child != null) {
			Attribute attribute = child.getAttribute(attributeName);

			if (attribute != null) {
				return attribute.getValue();
			}
		}

		return null;
	}

	public String writeAttribute(String xml, String path, String attributeName, String newValue) {
		Element child = getElement(xml, path);
		if (child != null) {
			Attribute attribute = child.getAttribute(attributeName);

			if (attribute != null) {
				attribute.setValue(newValue);

				return child.getDocument().toXML();
			}
		}

		return xml;
	}

}
