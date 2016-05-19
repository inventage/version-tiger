package com.inventage.tools.versiontiger.strategy;

import com.inventage.tools.versiontiger.MavenVersion;

public class ReleaseWithSuffixStrategy extends AbstractVersioningStrategy {

	@Override
	public MavenVersion apply(MavenVersion version) {
		return version.releaseVersionWithSuffix(getData());
	}

	@Override
	public boolean requiresDataInput() {
		return true;
	}
	
	@Override
	protected String getData() {
		return (String) super.getData();
	}
	
	@Override
	public String toString() {
		return "Release with suffix";
	}

}
