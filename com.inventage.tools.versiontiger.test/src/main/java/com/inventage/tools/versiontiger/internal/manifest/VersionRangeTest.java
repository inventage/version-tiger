package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.internal.manifest.VersionRange;

public class VersionRangeTest {

	@Test
	public void shouldIsRange() throws Exception {
		// given
		VersionRange versionRange = new VersionRange();
		versionRange.setEndVersion(mock(OsgiVersion.class));
		
		// when
		boolean result = versionRange.isRange();
		
		// then
		assertTrue(result);
	}
	
	@Test
	public void shouldNotIsRange() throws Exception {
		// given
		VersionRange versionRange = new VersionRange();
		
		// when
		boolean result = versionRange.isRange();
		
		// then
		assertFalse(result);
	}
	
	@Test
	public void shouldPrintRange() throws Exception {
		// given
		VersionRange versionRange = new VersionRange();
		OsgiVersion startVersion = mock(OsgiVersion.class);
		when(startVersion.toString()).thenReturn("1.0.1.qualifier");
		versionRange.setStartVersion(startVersion);
		OsgiVersion endVersion = mock(OsgiVersion.class);
		when(endVersion.toString()).thenReturn("2.0.3");
		versionRange.setEndVersion(endVersion);
		StringBuilder result = new StringBuilder();
		
		// when
		versionRange.print(result);
		
		// then
		assertEquals("[1.0.1.qualifier,2.0.3)", result.toString());
	}

	@Test
	public void shouldPrintSingleVersion() throws Exception {
		// given
		VersionRange versionRange = new VersionRange();
		OsgiVersion startVersion = mock(OsgiVersion.class);
		when(startVersion.toString()).thenReturn("1.0.1.qualifier");
		versionRange.setStartVersion(startVersion);
		StringBuilder result = new StringBuilder();
		
		// when
		versionRange.print(result);
		
		// then
		assertEquals("1.0.1.qualifier", result.toString());
	}

	@Test
	public void shouldPrintRangeWithNonDefaultBounds() throws Exception {
		// given
		VersionRange versionRange = new VersionRange();
		OsgiVersion startVersion = mock(OsgiVersion.class);
		when(startVersion.toString()).thenReturn("1.0.1.qualifier");
		versionRange.setStartVersion(startVersion);
		OsgiVersion endVersion = mock(OsgiVersion.class);
		when(endVersion.toString()).thenReturn("2.0.3");
		versionRange.setEndVersion(endVersion);
		versionRange.setStartInclusive(false);
		versionRange.setEndInclusive(true);
		StringBuilder result = new StringBuilder();
		
		// when
		versionRange.print(result);
		
		// then
		assertEquals("(1.0.1.qualifier,2.0.3]", result.toString());
	}
}
