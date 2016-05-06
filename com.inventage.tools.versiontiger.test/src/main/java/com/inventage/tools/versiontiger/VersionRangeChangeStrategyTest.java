package com.inventage.tools.versiontiger;

import static com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy.OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.ADAPTIVE;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.NO_CHANGE;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.STRICT;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.inventage.tools.versiontiger.internal.impl.OsgiVersionImpl;
import com.inventage.tools.versiontiger.internal.impl.VersionFactory;
import com.inventage.tools.versiontiger.internal.manifest.VersionRange;

public class VersionRangeChangeStrategyTest {

	private VersionFactory versionFactory = new VersionFactory("RELEASE", "qualifier", ADAPTIVE, OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION);

	private OsgiVersion version(String version) {
		return new OsgiVersionImpl(version, versionFactory);
	}
	
	private VersionRange range(boolean startInclusive, String startVersion, String endVersion, boolean endInclusive) {
		VersionRange range = new VersionRange();
		range.setStartInclusive(startInclusive);
		range.setStartVersion(version(startVersion));
		range.setEndVersion(version(endVersion));
		range.setEndInclusive(endInclusive);
		return range;
	}
	
	private VersionRange change(VersionRangeChangeStrategy strategy, VersionRange initialVersion, OsgiVersion oldVersion, OsgiVersion newVersion) {
		strategy.change(initialVersion, oldVersion, newVersion);
		return initialVersion;
	}
	
	@Test
	public void shouldChangeAdaptive() {
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(ADAPTIVE, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "3.0.0", false), change(ADAPTIVE, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.2.0")));
		assertEquals(range(false, "2.1.0", "3.0.0", false), change(ADAPTIVE, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "3.0.0", true), change(ADAPTIVE, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.2.0", false), change(ADAPTIVE, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.1.0", "1.1.1", false), change(ADAPTIVE, range(true, "1.0.0", "1.0.1", false), version("1.0.0"), version("1.1.0")));
		assertEquals(range(true, "2.1.0.QN", "3.0.0", false), change(ADAPTIVE, range(true, "1.0.0.QS", "2.0.0.QE", false), version("1.0.0.QS"), version("2.1.0.QN")));
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(ADAPTIVE, range(true, "1.0.0.QS", "2.0.0.QE", false), version("1.0.0.QS"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(ADAPTIVE, range(true, "1.0.0", "2.0.0", false), version("1.0.0.QS"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(ADAPTIVE, range(true, "1.0.0", "2.0.0", false), version("1.0.0.QS"), version("2.1.0.QN")));
		assertEquals(range(true, "1.2.0.QN", "1.2.0.QN", true), change(ADAPTIVE, range(true, "1.0.0.Q", "1.0.0.Q", true), version("1.0.0.Q"), version("1.2.0.QN")));
	}

	@Test
	public void shouldChangeMajor() {
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.1.0")));
		assertEquals(range(true, "3.1.0", "4.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("2.0.0"), version("3.1.0")));
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), null, version("2.1.0")));
	}

	@Test
	public void shouldChangeMinor() {
		assertEquals(range(true, "2.1.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.1.0")));
		assertEquals(range(true, "3.1.0", "3.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("2.0.0"), version("3.1.0")));
		assertEquals(range(true, "2.1.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), null, version("2.1.0")));
	}

	@Test
	public void shouldChangeBugfix() {
		assertEquals(range(true, "2.1.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.1.0")));
		assertEquals(range(true, "3.1.0", "3.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), version("2.0.0"), version("3.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_TO_NEW, range(true, "1.0.0", "2.0.0", false), null, version("2.1.0")));
	}

	@Test
	public void shouldChangeUpperToMajor() {
		assertEquals(range(true, "1.0.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "4.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("2.0.0"), version("3.1.0")));
		assertEquals(range(false, "1.0.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "3.0.0", false), change(UPPER_BOUND_TO_NEXT_MAJOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
	}

	@Test
	public void shouldChangeUpperToMinor() {
		assertEquals(range(true, "1.0.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "3.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("2.0.0"), version("3.1.0")));
		assertEquals(range(false, "1.0.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.2.0", false), change(UPPER_BOUND_TO_NEXT_MINOR_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
	}

	@Test
	public void shouldChangeUpperToBugfix() {
		assertEquals(range(true, "1.0.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "3.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("2.0.0"), version("3.1.0")));
		assertEquals(range(false, "1.0.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.1.1", false), change(UPPER_BOUND_TO_NEXT_BUGFIX_LOWER_BOUND_NO_CHANGE, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
	}

	@Test
	public void shouldChangeUpperToStrict() {
		assertEquals(range(true, "2.1.0", "2.1.0", true), change(STRICT, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.0", true), change(STRICT, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.1.0")));
		assertEquals(range(true, "3.1.0", "3.1.0", true), change(STRICT, range(true, "1.0.0", "2.0.0", false), version("2.0.0"), version("3.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.0", true), change(STRICT, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.0", true), change(STRICT, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "2.1.0", "2.1.0", true), change(STRICT, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
	}

	@Test
	public void shouldChangeUpperToNoChange() {
		assertEquals(range(true, "1.0.0", "2.0.0", false), change(NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.0.0", false), change(NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("1.1.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.0.0", false), change(NO_CHANGE, range(true, "1.0.0", "2.0.0", false), version("2.0.0"), version("3.1.0")));
		assertEquals(range(false, "1.0.0", "2.0.0", false), change(NO_CHANGE, range(false, "1.0.0", "2.0.0", false), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "2.0.0", true), change(NO_CHANGE, range(true, "1.0.0", "2.0.0", true), version("1.0.0"), version("2.1.0")));
		assertEquals(range(true, "1.0.0", "1.1.0", false), change(NO_CHANGE, range(true, "1.0.0", "1.1.0", false), version("1.0.0"), version("2.1.0")));
	}

}
