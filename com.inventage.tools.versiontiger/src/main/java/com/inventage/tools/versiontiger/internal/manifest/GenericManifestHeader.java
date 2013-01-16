package com.inventage.tools.versiontiger.internal.manifest;


public class GenericManifestHeader implements ManifestHeader {

	private String name;
	private String value;
	private String newLine;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void print(StringBuilder result) {
		result.append(name);
		result.append(": ");
		
		result.append(getValue());
		result.append(getNewLine());
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getNewLine() {
		return newLine;
	}

	public void setNewLine(String newLine) {
		this.newLine = newLine;
	}

}
