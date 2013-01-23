package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

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
	
	@Test
	public void shouldUpdateVersionIfOldDoesNotMatch() throws Exception {
		// given
		VersionRange versionRange = new VersionRange();
		OsgiVersion oldVersion = mock(OsgiVersion.class);
		OsgiVersion newVersion = mock(OsgiVersion.class);
		VersioningLoggerItem loggerItem = mock(VersioningLoggerItem.class);
		OsgiVersion startVersion = mock(OsgiVersion.class);
		when(startVersion.isLowerThan(oldVersion, true)).thenReturn(false);
		versionRange.setStartVersion(startVersion);
		OsgiVersion endVersion = mock(OsgiVersion.class);
		versionRange.setEndVersion(endVersion);
		
		// when
		boolean result = versionRange.updateVersionIfOldMatches(oldVersion, newVersion, loggerItem);
		
		// then
		assertFalse(result);
		verify(loggerItem).setStatus(VersioningLoggerStatus.WARNING);
		assertSame(startVersion, versionRange.getStartVersion());
		assertSame(endVersion, versionRange.getEndVersion());
	}

	@Test
	public void shouldUpdateVersionIfOldMatchesAndNoChange() throws Exception {
		// given
		VersionRange versionRange = new VersionRange();
		OsgiVersion oldVersion = mock(OsgiVersion.class);
		OsgiVersion newVersion = mock(OsgiVersion.class);
		VersioningLoggerItem loggerItem = mock(VersioningLoggerItem.class);
		OsgiVersion startVersion = mock(OsgiVersion.class);
		when(startVersion.isLowerThan(oldVersion, true)).thenReturn(true);
		when(newVersion.withoutQualifier()).thenReturn(startVersion);
		versionRange.setStartVersion(startVersion);
		OsgiVersion endVersion = mock(OsgiVersion.class);
		when(oldVersion.isLowerThan(endVersion, false)).thenReturn(true);
		versionRange.setEndVersion(endVersion);
		
		// when
		boolean result = versionRange.updateVersionIfOldMatches(oldVersion, newVersion, loggerItem);
		
		// then
		assertFalse(result);
		verify(loggerItem, never()).setStatus(any(VersioningLoggerStatus.class));
		assertSame(startVersion, versionRange.getStartVersion());
		assertSame(endVersion, versionRange.getEndVersion());
	}

	@Test
	public void shouldUpdateVersionIfOldMatchesAndWithChange() throws Exception {
		// given
		VersionRange versionRange = new VersionRange();
		OsgiVersion oldVersion = mock(OsgiVersion.class);
		OsgiVersion newVersion = mock(OsgiVersion.class);
		VersioningLoggerItem loggerItem = mock(VersioningLoggerItem.class);
		OsgiVersion startVersion = mock(OsgiVersion.class);
		when(startVersion.isLowerThan(oldVersion, true)).thenReturn(true);
		OsgiVersion newVersionWQ = mock(OsgiVersion.class);
		when(newVersion.withoutQualifier()).thenReturn(newVersionWQ);
		OsgiVersion newVersionInc = mock(OsgiVersion.class);
		when(newVersionWQ.incrementMajorAndSnapshot()).thenReturn(newVersionInc);
		OsgiVersion newVersionIncWQ = mock(OsgiVersion.class);
		when(newVersionInc.withoutQualifier()).thenReturn(newVersionIncWQ);
		versionRange.setStartVersion(startVersion);
		OsgiVersion endVersion = mock(OsgiVersion.class);
		when(oldVersion.isLowerThan(endVersion, false)).thenReturn(true);
		versionRange.setEndVersion(endVersion);
		
		// when
		boolean result = versionRange.updateVersionIfOldMatches(oldVersion, newVersion, loggerItem);
		
		// then
		assertTrue(result);
		verify(loggerItem).setStatus(VersioningLoggerStatus.SUCCESS);
		assertSame(newVersionWQ, versionRange.getStartVersion());
		assertSame(newVersionIncWQ, versionRange.getEndVersion());
	}

}
