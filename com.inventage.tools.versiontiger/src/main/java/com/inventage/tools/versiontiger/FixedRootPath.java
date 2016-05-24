package com.inventage.tools.versiontiger;

public class FixedRootPath implements RootPathProvider {

	private final String rootPath;

	public FixedRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	
	@Override
	public String getRootPath() {
		return rootPath;
	}

}
