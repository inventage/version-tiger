package com.inventage.tools.versiontiger.internal.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.inventage.tools.versiontiger.util.ManifestHandler;

public class ManifestHandlerTest {

	@Test
	public void shouldReadVersion() throws Exception {
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-Version: 2.5\nBundle-SymbolicName: a"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-Version: 2.5\r\nBundle-SymbolicName: a"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-SymbolicName: a\nBundle-Version: 2.5\n"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-SymbolicName: a\r\nBundle-Version: 2.5\r\n"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-Version: 2.\n 5\n"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-Version: 2.\r\n 5\r\n"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-Version: 2.5\n"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-Version: 2.5\r\n"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-SymbolicName: a\nBundle-Version: 2.\n 5\n"));
		assertEquals("2.5", new ManifestHandler().readVersion("Bundle-SymbolicName: a\r\nBundle-Version: 2.\r\n 5\r\n"));
	}

	@Test
	public void shouldNotReadVersion() throws Exception {
		assertNull(new ManifestHandler().readVersion("Bundle-SymbolicName: Bundle-Version: 2.5\n\n"));
	}

	@Test
	public void shouldWriteVersion() throws Exception {
		assertEquals("Bundle-Version: 3.0\nBundle-SymbolicName: a", new ManifestHandler().writeVersion("Bundle-Version: 2.5\nBundle-SymbolicName: a", "3.0"));
		assertEquals("Bundle-Version: 3.0\r\nBundle-SymbolicName: a",
				new ManifestHandler().writeVersion("Bundle-Version: 2.5\r\nBundle-SymbolicName: a", "3.0"));
		assertEquals("Bundle-SymbolicName: a\nBundle-Version: 3.0\n",
				new ManifestHandler().writeVersion("Bundle-SymbolicName: a\nBundle-Version: 2.5\n", "3.0"));
		assertEquals("Bundle-SymbolicName: a\r\nBundle-Version: 3.0\r\n",
				new ManifestHandler().writeVersion("Bundle-SymbolicName: a\r\nBundle-Version: 2.5\r\n", "3.0"));
		assertEquals("Bundle-Version: 3.0\n", new ManifestHandler().writeVersion("Bundle-Version: 2.\n 5\n", "3.0"));
		assertEquals("Bundle-Version: 3.0\r\n", new ManifestHandler().writeVersion("Bundle-Version: 2.\r\n 5\r\n", "3.0"));
		assertEquals("Bundle-Version: 3.0\n", new ManifestHandler().writeVersion("Bundle-Version: 2.5\n", "3.0"));
		assertEquals("Bundle-Version: 3.0\r\n", new ManifestHandler().writeVersion("Bundle-Version: 2.5\r\n", "3.0"));
		assertEquals("Bundle-SymbolicName: a\nBundle-Version: 3.0\n",
				new ManifestHandler().writeVersion("Bundle-SymbolicName: a\nBundle-Version: 2.\n 5\n", "3.0"));
		assertEquals("Bundle-SymbolicName: a\r\nBundle-Version: 3.0\r\n",
				new ManifestHandler().writeVersion("Bundle-SymbolicName: a\r\nBundle-Version: 2.\r\n 5\r\n", "3.0"));
		assertEquals("Bundle-Version: 01234567890123456789012345678901234567890123456789012345678901\n 23456789012345678901234567890123456789\n",
				new ManifestHandler().writeVersion("Bundle-Version: 2.5\n",
						"0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"));
		assertEquals("Bundle-Version: 01234567890123456789012345678901234567890123456789012345678901\r\n 23456789012345678901234567890123456789\r\n",
				new ManifestHandler().writeVersion("Bundle-Version: 2.5\r\n",
						"0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"));
	}

}
