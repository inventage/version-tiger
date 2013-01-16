package com.inventage.tools.versiontiger.internal.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.Version;
import com.inventage.tools.versiontiger.internal.impl.OsgiVersionImpl;

public class OsgiVersionImplTest {

	@Test
	public void shouldMajor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier");

		// when
		int result = version.major();

		// then
		assertEquals(1, result);
	}

	@Test
	public void shouldMinor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier");

		// when
		int result = version.minor();

		// then
		assertEquals(2, result);
	}

	@Test
	public void shouldBugfix() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier");

		// when
		int result = version.bugfix();

		// then
		assertEquals(3, result);
	}

	@Test
	public void shouldQualifier() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier");

		// when
		String result = version.qualifier();

		// then
		assertNull(result);
	}

	@Test
	public void shouldToString() {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.0.0.qualifier");

		// when
		String result = version.toString();

		// then
		assertEquals("1.0.0.qualifier", result);
	}

	@Test
	public void shouldIncrementMajor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier");

		// when
		OsgiVersion result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2.0.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMajorWhenRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3");

		// when
		Version result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2.0.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMajorWhenOnlyMajorRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.0.0.qualifier");

		// when
		Version result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2.0.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMinor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier");

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.3.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3");

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.3.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenOnlyMajor() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.0.0.qualifier");

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.1.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenOnlyMajorAndRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1");

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.1.0.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementBugfix() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3.qualifier");

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.4.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementBugfixWhenRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2.3");

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.4.qualifier", result.toString());
	}

	@Test
	public void shouldIncrementBugfixWhenOnlyMinorAndRelease() throws Exception {
		// given
		OsgiVersionImpl version = new OsgiVersionImpl("1.2");

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
			new OsgiVersionImpl(versionWithoutQualifier);
			new OsgiVersionImpl(versionWithQualifier);
			new OsgiVersionImpl(versionWithNumericQualifier);
			new OsgiVersionImpl(versionWithArbitraryQualifier);
			new OsgiVersionImpl(versionWithoutMicro);
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
			new OsgiVersionImpl(versionWithTooManySubversions);
			new OsgiVersionImpl(versionWithInvalidQualifier);
			new OsgiVersionImpl(versionWithNoMicroButWithQualifier);
		} catch (IllegalArgumentException e) {
			allValid = false;
		}

		// then
		Assert.assertFalse(allValid);
	}

	@Test
	public void shouldIsLower() throws Exception {
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("1.2.3.qualifier"), true));
		assertFalse(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("1.2.3.qualifier"), false));
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("1.2.4.qualifier"), true));
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("2.2.3.qualifier"), false));
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("1.3.3.qualifier"), false));
		assertFalse(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("1.2.2.qualifier"), true));
		assertFalse(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("1.1.3.qualifier"), true));
		assertFalse(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("0.2.3.qualifier"), true));
		assertTrue(new OsgiVersionImpl("1.2.3.qualifier").isLowerThan(new OsgiVersionImpl("1.2.3.OTHER"), true));
		assertTrue(new OsgiVersionImpl("1.2.3").isLowerThan(new OsgiVersionImpl("1.2.3"), true));
		assertFalse(new OsgiVersionImpl("1.2.3").isLowerThan(new OsgiVersionImpl("1.2.3"), false));
		assertFalse(new OsgiVersionImpl("1.2.3").isLowerThan(new OsgiVersionImpl("1.2.3.qualifier"), false));
		assertTrue(new OsgiVersionImpl("1.2.3").isLowerThan(new OsgiVersionImpl("1.2.3.qualifier"), true));
	}

}
