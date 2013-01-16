package com.inventage.tools.versiontiger.strategy;

public abstract class AbstractVersioningStrategy implements VersioningStrategy {

	@Override
	public boolean requiresDataInput() {
		return false;
	}

	@Override
	public VersioningStrategy setData(Object data) {
		return this;
	}

}
