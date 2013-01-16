package com.inventage.tools.versiontiger.strategy;

import com.inventage.tools.versiontiger.MavenVersion;

public class IncrementMinorAndSnapshotStrategy extends AbstractVersioningStrategy {

	@Override
	public MavenVersion apply(MavenVersion version) {
		return version.incrementMinorAndSnapshot();
	}

	@Override
	public String toString() {
		return "Increment minor and set to snapshot";
	}

}
