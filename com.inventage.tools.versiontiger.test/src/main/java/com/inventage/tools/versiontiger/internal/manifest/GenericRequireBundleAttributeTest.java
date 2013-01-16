package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.*;

import org.junit.Test;

import com.inventage.tools.versiontiger.internal.manifest.GenericRequireBundleAttribute;

public class GenericRequireBundleAttributeTest {

	@Test
	public void shouldPrint() throws Exception {
		// given
		GenericRequireBundleAttribute attribute = new GenericRequireBundleAttribute();
		attribute.setName("foo");
		attribute.setValue("bar");
		StringBuilder result = new StringBuilder();
		
		// when
		attribute.print(result);
		
		// then
		assertEquals("foo=bar", result.toString());
	}

}
