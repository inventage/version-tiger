package com.inventage.tools.versiontiger.strategy;

public abstract class AbstractVersioningStrategy implements VersioningStrategy {

	private Object data;

	@Override
	public boolean requiresDataInput() {
		return false;
	}

	@Override
	public VersioningStrategy setData(Object data) {
		this.data = data;
		return this;
	}

	protected Object getData() {
		return data;
	}
	
}
