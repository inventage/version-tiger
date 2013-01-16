package com.inventage.tools.versiontiger.internal.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Version;
import com.inventage.tools.versiontiger.internal.impl.MavenVersionImpl;

public class MavenVersionImplTest {

	@Test
	public void shouldMajor() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3-SNAPSHOT");

		// when
		int result = version.major();

		// then
		assertEquals(1, result);
	}

	@Test
	public void shouldMinor() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3-SNAPSHOT");

		// when
		int result = version.minor();

		// then
		assertEquals(2, result);
	}

	@Test
	public void shouldBugfix() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3-SNAPSHOT");

		// when
		int result = version.bugfix();

		// then
		assertEquals(3, result);
	}

	@Test
	public void shouldSuffix() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3-RC3-SNAPSHOT");

		// when
		String result = version.suffix();

		// then
		assertEquals("RC3", result);
	}

	@Test
	public void shouldToString() {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1-SNAPSHOT");

		// when
		String result = version.toString();

		// then
		assertEquals("1-SNAPSHOT", result);
	}

	@Test
	public void shouldIncrementMajor() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3-SNAPSHOT");

		// when
		MavenVersion result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2.0.0-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementMajorWhenRelease() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3");

		// when
		Version result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2.0.0-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementMajorWhenOnlyMajorRelease() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1-SNAPSHOT");

		// when
		Version result = version.incrementMajorAndSnapshot();

		// then
		assertEquals("2-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementMinor() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3-SNAPSHOT");

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.3.0-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenRelease() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3");

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.3.0-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenOnlyMajor() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1-SNAPSHOT");

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.1-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementMinorWhenOnlyMajorAndRelease() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1");

		// when
		Version result = version.incrementMinorAndSnapshot();

		// then
		assertEquals("1.1-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementBugfix() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3-SNAPSHOT");

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.4-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementBugfixWhenRelease() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2.3");

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.4-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementBugfixWhenOnlyMinor() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2-SNAPSHOT");

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.1-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldIncrementBugfixWhenOnlyMinorAndRelease() throws Exception {
		// given
		MavenVersionImpl version = new MavenVersionImpl("1.2");

		// when
		Version result = version.incrementBugfixAndSnapshot();

		// then
		assertEquals("1.2.1-SNAPSHOT", result.toString());
	}

	@Test
	public void shouldAcceptExtendedQualifiersForMaven() {

		// given
		String extendedQualifierVersion1 = "1.2.4-RC5-SNAPSHOT";
		String extendedQualifierVersion2 = "1.2.4-RC_5-SNAPSHOT";

		// when
		MavenVersionImpl version1 = new MavenVersionImpl(extendedQualifierVersion1);
		MavenVersionImpl version2 = new MavenVersionImpl(extendedQualifierVersion2);

		// then
		assertEquals("1.2.4-RC5-SNAPSHOT", version1.toString());
		assertEquals("1.2.4-RC_5-SNAPSHOT", version2.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptBrokenExtendedQualifiersForMaven() {

		// given
		String extendedQualifierVersion = "1.2.4-RC5-SNAPSH.OT";

		// when
		new MavenVersionImpl(extendedQualifierVersion);

		// then
		// Exception should be thrown.
	}

	@Test
	public void shouldRemoveSnapshotPartForRelease() {

		// given
		String extendedQualifierVersion = "1.2.4-RC5-SNAPSHOT";

		// when
		MavenVersionImpl version = new MavenVersionImpl(extendedQualifierVersion);

		// then
		assertEquals("1.2.4-RC5", version.releaseVersion().toString());
	}

	@Test
	public void shouldRemoveOnlySnapshotPartOfSuffixForRelease() {

		// given
		String extendedQualifierVersion = "1.2.4-A-B-C-D-E-SNAPSHOT";

		// when
		MavenVersionImpl version = new MavenVersionImpl(extendedQualifierVersion);

		// then
		assertEquals("1.2.4-A-B-C-D-E", version.releaseVersion().toString());
	}

	@Test
	public void shouldAcceptValidMavenVersions() {
		// given
		String versionWithoutQualifier = "1.2.3";
		String versionWithQualifier = "1.2.3-SNAPSHOT";
		String versionWithNumericQualifier = "1.2.3-456";
		String versionWithArbitraryQualifier = "1.2.3-Ab_45";
		String versionWithoutMinor = "1";
		String versionWithoutMinorButWithQualifier = "1-SNAPSHOT";

		// when
		boolean areAllValid = true;
		try {
			new MavenVersionImpl(versionWithoutQualifier);
			new MavenVersionImpl(versionWithQualifier);
			new MavenVersionImpl(versionWithNumericQualifier);
			new MavenVersionImpl(versionWithArbitraryQualifier);
			new MavenVersionImpl(versionWithoutMinor);
			new MavenVersionImpl(versionWithoutMinorButWithQualifier);
		} catch (IllegalArgumentException e) {
			areAllValid = false;
		}

		// then
		Assert.assertTrue(areAllValid);
	}

	@Test
	public void shouldDenyInvalidMavenVersions() {
		// given
		String versionWithTooManySubversions = "1.2.3.4-SNAPSHOT";
		String versionWithInvalidQualifier = "1.2.3-ä";

		// when
		boolean allValid = true;
		try {
			new MavenVersionImpl(versionWithTooManySubversions);
			new MavenVersionImpl(versionWithInvalidQualifier);
		} catch (IllegalArgumentException e) {
			allValid = false;
		}

		// then
		Assert.assertFalse(allValid);
	}

	@Test
	public void shouldIsLower() throws Exception {
		assertTrue(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("1.2.3-SNAPSHOT"), true));
		assertFalse(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("1.2.3-SNAPSHOT"), false));
		assertTrue(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("1.2.4-SNAPSHOT"), true));
		assertTrue(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("2.2.3-SNAPSHOT"), false));
		assertTrue(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("1.3.3-SNAPSHOT"), false));
		assertFalse(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("1.2.2-SNAPSHOT"), true));
		assertFalse(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("1.1.3-SNAPSHOT"), true));
		assertFalse(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("0.2.3-SNAPSHOT"), true));
		assertTrue(new MavenVersionImpl("1.2.3-SNAPSHOT").isLowerThan(new MavenVersionImpl("1.2.3-OTHER"), true));
		assertTrue(new MavenVersionImpl("1.2.3").isLowerThan(new MavenVersionImpl("1.2.3"), true));
		assertFalse(new MavenVersionImpl("1.2.3").isLowerThan(new MavenVersionImpl("1.2.3"), false));
		assertFalse(new MavenVersionImpl("1.2.3").isLowerThan(new MavenVersionImpl("1.2.3-SNAPSHOT"), false));
		assertTrue(new MavenVersionImpl("1.2.3").isLowerThan(new MavenVersionImpl("1.2.3-SNAPSHOT"), true));
	}

}
