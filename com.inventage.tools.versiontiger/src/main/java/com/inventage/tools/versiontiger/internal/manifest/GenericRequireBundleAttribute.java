package com.inventage.tools.versiontiger.internal.manifest;

public class GenericRequireBundleAttribute implements RequireBundleAttribute {

	private String name;
	private String value;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void print(StringBuilder result) {
		if (name == null || value == null) {
			throw new IllegalStateException("Name/value not set: " + name + "=" + value);
		}
		
		result.append(name);
		result.append("=");
		result.append(value);
	}

}
