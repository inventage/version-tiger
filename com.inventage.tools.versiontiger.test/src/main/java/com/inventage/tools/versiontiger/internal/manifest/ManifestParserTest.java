package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.internal.impl.VersionFactory;
import com.inventage.tools.versiontiger.internal.manifest.ManifestParser;
import com.inventage.tools.versiontiger.internal.manifest.Manifest;
import com.inventage.tools.versiontiger.util.FileHandler;

@RunWith(Parameterized.class)
public class ManifestParserTest {

	private final String input;
	private final String expected;

	public ManifestParserTest(String input, String expected) {
		this.input = input;
		this.expected = expected != null ? expected : input;
	}
	
	@Parameters
	public static Collection<Object[]> parameters() {
		return Arrays.asList(new Object[][] {
			{ readResourceFile("/Manifest0.MF"), null },
			{ readResourceFile("/Manifest1.MF"), readResourceFile("/Manifest1_Expected.MF") },
			{ readResourceFile("/Manifest2.MF"), null },
			{ readResourceFile("/Manifest3.MF"), null },
			{ readResourceFile("/Manifest4.MF"), null },
			{ "Manifest-Version: 1.0\nversion: a\nManifest-Version: 1.0\n\n", null },
			{ "Manifest-Version: 1.0\nBundle-SymbolicName: a\nBundle-Version: 1.0\n\n", null },
			{ "Manifest-Version: 1.0\nRequire-Bundle: foo.bar;bundle-version=\"1.0\",\n asdf.b.c;bundle-version=\"12.12.23\";export:=\"true\",\n foo\n\n", null },
			{ "Manifest-Version: 1.0\nRequire-Bundle: foo.bar;bundle-version=1.0;export:=true,\n foo\n\n", null },
			{ "Manifest-Version: 1.0\nRequire-Bundle: Require-Bundle\n\n", null },
			{ "Manifest-Version: 1.0\nRequire-Bundle: bundle-version\n\n", null },
			{ "Manifest-Version: 1.0\nFragment-Host: ch.my.bundle\n\n", null },
			{ "Manifest-Version: 1.0\nFragment-Host: ch.my.bundle;bundle-version=\"1.2.3\"\n\n", null },
			{ "Manifest-Version: 1.0\nFragment-Host: ch.my.bundle;bundle-version=\"[1.0,2.0)\"\n\n", null },
			{ "Manifest-Version: 1.0\nFragment-Host: ch.my.bundle;bundle-version=\"[1.0,2.0)\";myattribute=val\n\n", null },
			{ "Manifest-Version: 1.0\nFoo: line0\n line1\n line2\n line3\nBar: bla\n\n", null },
			{ "Manifest-Version: 1.0\nFoo: bar", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n\n", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n\n\n", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n\n\n\n", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n\nOther: bla", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n\nOther: bla\n", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n\nOther: bla\n\n", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n\nOther: bla\n\n\n", null },
			{ "Manifest-Version: 1.0\nFoo: bar\n\nOther: bla\n\n\n\n", null },
			{ "Manifest-Version: 1.0\nRequire-Bu\n ndle: bar\n\n", "Manifest-Version: 1.0\nRequire-Bundle: bar\n\n" }, // better than no newline support in header names
		});
	}
	
	@Test
	public void shouldAccept() throws Exception {
		Manifest manifest = new ManifestParser(input, new VersionFactory(OsgiVersion.OSGI_DEFAULT_RELEASE_SUFFIX, OsgiVersion.OSGI_DEFAULT_SNAPSHOT_SUFFIX)).manifest();
		
		StringBuilder result = new StringBuilder();
		manifest.print(result);
		assertEquals(expected, result.toString());
	}

	private static String readResourceFile(String name) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ManifestParserTest.class.getResourceAsStream(name)));
		return new FileHandler().readFileContent(reader);
	}

}
