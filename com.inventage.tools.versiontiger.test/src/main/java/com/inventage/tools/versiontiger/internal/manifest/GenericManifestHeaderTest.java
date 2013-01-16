package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.*;

import org.junit.Test;

import com.inventage.tools.versiontiger.internal.manifest.GenericManifestHeader;

public class GenericManifestHeaderTest {

	@Test
	public void shouldPrint() throws Exception {
		// given
		GenericManifestHeader genericManifestHeader = new GenericManifestHeader();
		genericManifestHeader.setName("foo");
		genericManifestHeader.setValue("line1\n line2\r\n line3");
		genericManifestHeader.setNewLine("\r\n");
		StringBuilder result = new StringBuilder();
		
		// when
		genericManifestHeader.print(result);
		
		// then
		assertEquals("foo: line1\n line2\r\n line3\r\n", result.toString());
	}

}
