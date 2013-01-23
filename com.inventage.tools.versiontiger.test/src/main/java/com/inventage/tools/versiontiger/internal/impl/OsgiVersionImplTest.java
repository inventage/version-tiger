package com.inventage.tools.versiontiger.internal.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.Version;

public class OsgiVersionImplTest {
	
	VersionFactory versionFactory = new VersionFactory(OsgiVersion.OSGI_DEFAULT_RELEASE_SUFFIX, OsgiVersion.OSGI_DEFAULT_SNAPSHOT_SUFFIX);

	@Test
	public void shouldMajor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier", versionFactory);

		// when
		int result = version.major();

		// then
		assertEquals(1, result);
	}

	@Test
	public void shouldMinor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier", versionFactory);

		// when
		int result = version.minor();

		// then
		assertEquals(2, result);
	}

	@Test
	public void shouldBugfix() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier", versionFactory);

		// when
		int result = version.bugfix();

		// then
		assertEquals(3, result);
	}

	@Test
	public void shouldQualifier() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier", versionFactory);

		// when
		String result = version.qualifier();

		// then
		assertEquals(result, "qualifier");
	}

	@Test
	public void shouldToString() {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.0.0.qualifier", versionFactory);

		// when
		String result = version.toString();

		// then
		assertEquals("1.0.0.qualifier", result);
	}

	@Test
	public void shouldIncrementMajor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier", versionFactory);

		// when
		OsgiVersion result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2.0.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMajorWhenRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3", versionFactory);

		// when
		Version result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2.0.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMajorWhenOnlyMajorRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.0.0.qualifier", versionFactory);

		// when
		Version result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2.0.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMinor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier", versionFactory);

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.3.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3", versionFactory);

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.3.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenOnlyMajor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.0.0.qualifier", versionFactory);

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.1.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenOnlyMajorAndRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1", versionFactory);

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.1.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementBugfix() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier", versionFactory);

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.4.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementBugfixWhenRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3", versionFactory);

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.4.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementBugfixWhenOnlyMinorAndRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2", versionFactory);

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.1.qualifier", result.toString());
	}

	@Test
	public void shouldAcceptValidOsgiVersions() {
		// given
		String versionWithoutQualifier = "1.2.3";
		String versionWithQualifier = "1.2.3.qualifier";
		String versionWithNumericQualifier = "1.2.3.456";
		String versionWithArbitraryQualifier = "1.2.3.Ab-_45";
		String versionWithoutMicro = "1.2";

		// when
		boolean areAllValid = true;
		try {
			new OsgiVersionImpl(versionWithoutQualifier, versionFactory);
			new OsgiVersionImpl(versionWithQualifier, versionFactory);
			new OsgiVersionImpl(versionWithNumericQualifier, versionFactory);
			new OsgiVersionImpl(versionWithArbitraryQualifier, versionFactory);
			new OsgiVersionImpl(versionWithoutMicro, versionFactory);
		} catch (IllegalArgumentException e) {
			areAllValid = false;
		}

		// then
		Assert.assertTrue(areAllValid);
	}

	@Test
	public void shouldDenyInvalidOsgiVersions() {
		// given
		String versionWithTooManySubversions = "1.2.3.4.qualifier";
		String versionWithInvalidQualifier = "1.2.3.ä";
		String versionWithNoMicroButWithQualifier = "1.2.qualifier";

		// when
		boolean allValid = true;
		try {
			new OsgiVersionImpl(versionWithTooManySubversions, versionFactory);
			new OsgiVersionImpl(versionWithInvalidQualifier, versionFactory);
			new OsgiVersionImpl(versionWithNoMicroButWithQualifier, versionFactory);
		} catch (IllegalArgumentException e) {
			allValid = false;
		}

		// then
		Assert.assertFalse(allValid);
	}

	@Test
	public void shouldIsLower() throws Exception {
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.3.qualifier", versionFactory), true));
		assertFalse(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.3.qualifier", versionFactory), false));
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.4.qualifier", versionFactory), true));
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("2.2.3.qualifier", versionFactory), false));
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("1.3.3.qualifier", versionFactory), false));
		assertFalse(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.2.qualifier", versionFactory), true));
		assertFalse(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("1.1.3.qualifier", versionFactory), true));
		assertFalse(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("0.2.3.qualifier", versionFactory), true));
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.3.OTHER", versionFactory), true));
		assertTrue(new OsgiVersionImpl("1.2.3", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.3", versionFactory), true));
		assertFalse(new OsgiVersionImpl("1.2.3", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.3", versionFactory), false));
		assertFalse(new OsgiVersionImpl("1.2.3", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.3.qualifier", versionFactory), false));
		assertTrue(new OsgiVersionImpl("1.2.3", versionFactory).isLowerThan(new OsgiVersionImpl("1.2.3.qualifier", versionFactory), true));
	}

	@Test
	public void shouldUseCustomQualifier() {
		
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.1", new VersionFactory("release", "qualifier"));
		
		// when
		String result = version.toString();
		
		// then
		assertEquals("1.2.1", result);
	}
	
	@Test
	public void shouldUseCustomReleaseQualifier() {
		
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.0", new VersionFactory("release", "qualifier"));
		
		// when
		String result = version.releaseVersion().toString();
		
		// then
		assertEquals("1.2.0.release", result);
	}
	
	@Test
	public void shouldReturnTruncatedVersionOnRelease() {
		
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2", versionFactory);
		
		// when
		String result = version.toString();
		
		// then
		assertEquals("1.2", result);
	}
	
	@Test
	public void shouldUseCustomSnapshotQualifier() {
		
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2", new VersionFactory("release", "snapshot"));
		
		// when
		String result = version.snapshotVersion().toString();
		
		// then
		assertEquals("1.2.0.snapshot", result);
	}
	
	@Test
	public void shouldWithoutQualifier() throws Exception {
		// given
		OsgiVersion version = new OsgiVersionImpl("1.2.0.qualifier", new VersionFactory("release", "snapshot"));
		
		// when
		String result = version.withoutQualifier().toString();
		
		// then
		assertEquals("1.2.0", result);
	}

	@Test
	public void shouldEquals() throws Exception {
		// given
		OsgiVersion version1 = new OsgiVersionImpl("1.2.0.qualifier", new VersionFactory("release", "snapshot"));
		OsgiVersion version2 = new OsgiVersionImpl("1.2.0.qualifier", new VersionFactory("release", "snapshot"));
		
		// when
		boolean result = version1.equals(version2);
		
		// then
		assertTrue(result);
	}

}
