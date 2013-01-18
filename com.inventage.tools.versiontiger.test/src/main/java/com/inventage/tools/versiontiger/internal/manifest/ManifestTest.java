package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.inventage.tools.versiontiger.internal.manifest.ManifestParser;
import com.inventage.tools.versiontiger.NullVersioningLogger;
import com.inventage.tools.versiontiger.internal.impl.OsgiVersionImpl;
import com.inventage.tools.versiontiger.internal.manifest.Manifest;
import com.inventage.tools.versiontiger.internal.manifest.ManifestSection;

public class ManifestTest {

	@Test
	public void shouldPrint() throws Exception {
		// given
		Manifest manifest = new Manifest();
		ManifestSection section1 = mock(ManifestSection.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				StringBuilder result = (StringBuilder) invocation.getArguments()[0];
				result.append("Foo1: bar\n");
				return null;
			}
		}).when(section1).print(any(StringBuilder.class));
		manifest.addSection(section1);
		ManifestSection section2 = mock(ManifestSection.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				StringBuilder result = (StringBuilder) invocation.getArguments()[0];
				result.append("Foo2: bar\n");
				return null;
			}
		}).when(section2).print(any(StringBuilder.class));
		manifest.addSection(section2);
		manifest.setNewLine("\n");
		manifest.appendLastNewLine("\r\n");
		manifest.appendLastNewLine("\n");
		
		// when
		String result = manifest.print();
		
		// then
		assertEquals("Manifest-Version: 1.0\nFoo1: bar\n\nFoo2: bar\n\r\n\n", result);
	}
	
	@Test
	public void shouldUpdateRequireBundleReference() throws Exception {
		// given
		String input = "Manifest-Version: 1.0\nRequire-Bundle: foo.bar;bundle-version=\"1.0\"," +
				"asdf.b.c;bundle-version=\"12.12.23\";visibility:=reexport," +
				"foo;resolution:=optional\nOther-Header: foo\n\n";
		Manifest manifest = new ManifestParser(input).manifest();
		
		// when
		boolean result = manifest.updateRequireBundleReference("asdf.b.c", new OsgiVersionImpl("12.12.23"), new OsgiVersionImpl("33.1.0.qualifier"), new NullVersioningLogger().createVersioningLoggerItem());
		
		// then
		assertTrue(result);
		assertEquals("Manifest-Version: 1.0\nRequire-Bundle: foo.bar;bundle-version=\"1.0\",\n " +
				"asdf.b.c;bundle-version=\"33.1.0\";visibility:=reexport,\n " +
				"foo;resolution:=optional\nOther-Header: foo\n\n", manifest.print());
	}
	
	@Test
	public void shouldUpdateFragmentHostReference() throws Exception {
		// given
		String id = "asdf.b.c";
		String oldVersion = "1.2.3";
		String newVersion = "2.0.0";
		String input =
				"Manifest-Version: 1.0\n" +
				"Fragment-Host: " + id + ";bundle-version=\"" + oldVersion + "\";visibility:=reexport\n" +
				"Other-Header: foo\n\n";
		Manifest manifest = new ManifestParser(input).manifest();
		
		// when
		boolean result = manifest.updateFragmentHostReference(id, new OsgiVersionImpl(oldVersion), new OsgiVersionImpl(newVersion + ".qualifier"), new NullVersioningLogger().createVersioningLoggerItem());
		
		// then
		assertTrue(result);
		assertEquals("Manifest-Version: 1.0\n" +
				"Fragment-Host: " + id + ";bundle-version=\"" + newVersion + "\";visibility:=reexport\n" +
				"Other-Header: foo\n\n", manifest.print());
	}

}
