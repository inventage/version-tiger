package com.inventage.tools.versiontiger.strategy;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.internal.impl.VersioningImpl;

public class SetVersionManuallyStrategy extends AbstractVersioningStrategy {

	private String manuallySetVersion = null;

	@Override
	public MavenVersion apply(MavenVersion version) {
		return new VersioningImpl().createMavenVersion(manuallySetVersion);
	}

	@Override
	public String toString() {
		return "Set version manually";
	}

	@Override
	public boolean requiresDataInput() {
		return true;
	}

	@Override
	public VersioningStrategy setData(Object data) {
		manuallySetVersion = (String) data;
		return this;
	}

}
