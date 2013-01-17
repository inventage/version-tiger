package com.inventage.tools.versiontiger;

import java.io.BufferedReader;
import java.io.IOException;

public interface Versioning {

	ProjectUniverse createUniverse(String id, VersioningLogger logger);

	ProjectUniverse createUniverse(String id, String name, VersioningLogger logger);
	
	ProjectUniverse createUniverse(String id, String name, String rootPath, VersioningLogger logger);

	MavenVersion createMavenVersion(String mavenVersion);
	
	OsgiVersion createOsgiVersion(MavenVersion mavenVersion);

	void executeCommandScript(BufferedReader commandScript, String rootPath, VersioningLogger logger) throws IOException;

	void setOsgiReleaseQualifier(String osgiReleaseQualifier);
	
	void setOsgiSnapshotQualifier(String osgiSnapshotQualifier);
}
