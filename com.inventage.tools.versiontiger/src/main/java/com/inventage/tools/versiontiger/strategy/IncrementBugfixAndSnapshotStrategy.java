package com.inventage.tools.versiontiger.strategy;

import com.inventage.tools.versiontiger.MavenVersion;

public class IncrementBugfixAndSnapshotStrategy extends AbstractVersioningStrategy {

	@Override
	public MavenVersion apply(MavenVersion version) {
		return version.incrementBugfixAndSnapshot();
	}

	@Override
	public String toString() {
		return "Increment bugfix and set to snapshot";
	}
}
