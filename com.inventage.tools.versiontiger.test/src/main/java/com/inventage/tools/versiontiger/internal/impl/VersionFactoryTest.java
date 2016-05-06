package com.inventage.tools.versiontiger.internal.impl;

import static com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy.OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION;
import static com.inventage.tools.versiontiger.OsgiVersion.OSGI_DEFAULT_RELEASE_SUFFIX;
import static com.inventage.tools.versiontiger.OsgiVersion.OSGI_DEFAULT_SNAPSHOT_SUFFIX;
import static com.inventage.tools.versiontiger.VersionRangeChangeStrategy.ADAPTIVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;

public class VersionFactoryTest {
	
	VersionFactory versionFactory = new VersionFactory(OSGI_DEFAULT_RELEASE_SUFFIX, OSGI_DEFAULT_SNAPSHOT_SUFFIX, ADAPTIVE, OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION);
	
	@Test
	public void shouldCreateMavenVersionFromString() throws Exception {
		// given
		String mavenVersion = "1.2.3-RC3-SNAPSHOT";
		
		// when
		MavenVersion result = new VersionFactory(OSGI_DEFAULT_RELEASE_SUFFIX, OSGI_DEFAULT_SNAPSHOT_SUFFIX, ADAPTIVE, OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION).createMavenVersion(mavenVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof MavenVersionImpl);
		assertEquals(mavenVersion, result.toString());
	}
	
	@Test
	public void shouldCreateOsgiVersionFromString() throws Exception {
		// given
		String mavenVersion = "1.2.3.qualifier";
		
		// when
		OsgiVersion result = new VersionFactory(OSGI_DEFAULT_RELEASE_SUFFIX, OSGI_DEFAULT_SNAPSHOT_SUFFIX, ADAPTIVE, OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION).createOsgiVersion(mavenVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof OsgiVersionImpl);
		assertEquals(mavenVersion, result.toString());
	}
	
	@Test
	public void shouldCreateOsgiVersionFromMavenSnapshot() throws Exception {
		// given
		MavenVersion osgiVersion = new MavenVersionImpl("1.2.3-RC3-SNAPSHOT", versionFactory);
		
		// when
		OsgiVersion result = new VersionFactory(OSGI_DEFAULT_RELEASE_SUFFIX, OSGI_DEFAULT_SNAPSHOT_SUFFIX, ADAPTIVE, OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION).createOsgiVersion(osgiVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof OsgiVersionImpl);
		assertEquals("1.2.3.qualifier", result.toString());
	}
	
	@Test
	public void shouldCreateOsgiVersionFromMavenWithSuffix() throws Exception {
		// given
		MavenVersion mavenVersion = new MavenVersionImpl("1.2.3-BLA", versionFactory);
		
		// when
		OsgiVersion result = new VersionFactory(OSGI_DEFAULT_RELEASE_SUFFIX, OSGI_DEFAULT_SNAPSHOT_SUFFIX, ADAPTIVE, OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION).createOsgiVersion(mavenVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof OsgiVersionImpl);
		assertEquals("1.2.3", result.toString());
	}
	
	@Test
	public void shouldCreateOsgiVersionFromMavenRelease() throws Exception {
		// given
		MavenVersion mavenVersion = new MavenVersionImpl("1.2.3", versionFactory);
		
		// when
		OsgiVersion result = new VersionFactory(OSGI_DEFAULT_RELEASE_SUFFIX, OSGI_DEFAULT_SNAPSHOT_SUFFIX, ADAPTIVE, OSGI_QUALIFIER_FOR_SNAPSHOT_DISTINCTION).createOsgiVersion(mavenVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof OsgiVersionImpl);
		assertEquals("1.2.3", result.toString());
	}
	
}
