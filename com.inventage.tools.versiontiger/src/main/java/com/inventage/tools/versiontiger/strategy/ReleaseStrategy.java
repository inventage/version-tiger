package com.inventage.tools.versiontiger.strategy;

import com.inventage.tools.versiontiger.MavenVersion;

public class ReleaseStrategy extends AbstractVersioningStrategy {

	@Override
	public MavenVersion apply(MavenVersion version) {
		return version.releaseVersion();
	}

	@Override
	public String toString() {
		return "Release";
	}

}
