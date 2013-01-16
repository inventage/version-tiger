package com.inventage.tools.versiontiger.strategy;

import com.inventage.tools.versiontiger.MavenVersion;

public class IncrementMajorAndSnapshotStrategy extends AbstractVersioningStrategy {

	@Override
	public MavenVersion apply(MavenVersion version) {
		return version.incrementMajorAndSnapshot();
	}

	@Override
	public String toString() {
		return "Increment major and set to snapshot";
	}

}
