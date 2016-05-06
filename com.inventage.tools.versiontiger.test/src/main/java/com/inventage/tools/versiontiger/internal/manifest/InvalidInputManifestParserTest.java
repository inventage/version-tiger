package com.inventage.tools.versiontiger.internal.manifest;

import static com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy.OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION;
import static com.inventage.tools.versiontiger.OsgiVersion.OSGI_DEFAULT_RELEASE_SUFFIX;
import static com.inventage.tools.versiontiger.OsgiVersion.OSGI_DEFAULT_SNAPSHOT_SUFFIX;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.ADAPTIVE;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.inventage.tools.versiontiger.internal.impl.VersionFactory;

@RunWith(Parameterized.class)
public class InvalidInputManifestParserTest {

	private final String input;

	public InvalidInputManifestParserTest(String input) {
		this.input = input;
	}
	
	@Parameters
	public static Collection<Object[]> parameters() {
		return Arrays.asList(new Object[][] {
			{ "Manifest-Version: 1.0\nFoo: bar\nRequire-Bundle: foo%bar\nOther: bla\n\n" }, // invalid require bundle header
			{ "Manifest-Version: 1.0\nFoo: bar\nFragment-Host: foo%bar\nOther: bla\n\n" }, // invalid fragment host header
			{ "Manifest-Version: 1.0\nFoo: bar\nRequire-Bundle: bundle1;bundle-version=%,bundle2\nOther: bla\n\n" }, // invalid bundle-version attribute
		});
	}
	
	@Test(expected = ParseException.class)
	public void shouldNotAccept() throws Exception {
		Manifest manifest = new ManifestParser(input, new VersionFactory(OSGI_DEFAULT_RELEASE_SUFFIX,
				OSGI_DEFAULT_SNAPSHOT_SUFFIX, ADAPTIVE, OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION)).manifest();
		
		StringBuilder result = new StringBuilder();
		manifest.print(result);
	}

}
