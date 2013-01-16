package com.inventage.tools.versiontiger.internal.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.inventage.tools.versiontiger.util.XmlHandler;

public class XmlHandlerTest {

	@Test
	public void shouldReadElement() throws Exception {
		// given
		XmlHandler xmlHandler = new XmlHandler();

		// when
		String result = xmlHandler.readElement("<project><artifactId>a</artifactId><version> 1.2.3 </version></project>", "project/version");

		// then
		assertNotNull(result);
		assertEquals("1.2.3", result);
	}

	@Test
	public void shouldWriteElement() throws Exception {
		// given
		XmlHandler xmlHandler = new XmlHandler();

		// when
		String result = xmlHandler.writeElement("<project><artifactId>a</artifactId><version> 1.2.3 </version></project>", "project/version", "2.5");

		// then
		assertNotNull(result);
		assertEquals("<project><artifactId>a</artifactId><version>2.5</version></project>", result);
	}

	@Test
	public void shouldReadAttribute() throws Exception {
		// given
		XmlHandler xmlHandler = new XmlHandler();

		// when
		String result = xmlHandler.readAttribute("<product version=\"1.2.3\"></product>", "product", "version");

		// then
		assertNotNull(result);
		assertEquals("1.2.3", result);
	}

	@Test
	public void shouldWriteAttribute() throws Exception {
		// given
		XmlHandler xmlHandler = new XmlHandler();

		// when
		String result = xmlHandler.writeAttribute("<product version=\"1.2.3\"></product>", "product", "version", "2.5");

		// then
		assertNotNull(result);
		assertEquals("<product version=\"2.5\"></product>", result);
	}

}
