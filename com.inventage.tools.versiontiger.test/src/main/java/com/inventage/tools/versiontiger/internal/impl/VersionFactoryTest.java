package com.inventage.tools.versiontiger.internal.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.internal.impl.MavenVersionImpl;
import com.inventage.tools.versiontiger.internal.impl.OsgiVersionImpl;
import com.inventage.tools.versiontiger.internal.impl.VersionFactory;

public class VersionFactoryTest {

	@Test
	public void shouldCreateMavenVersionFromString() throws Exception {
		// given
		String mavenVersion = "1.2.3-RC3-SNAPSHOT";
		
		// when
		MavenVersion result = new VersionFactory().createMavenVersion(mavenVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof MavenVersionImpl);
		assertEquals(mavenVersion, result.toString());
	}
	
	@Test
	public void shouldCreateMavenVersionFromOsgiSnapshot() throws Exception {
		// given
		OsgiVersion osgiVersion = new OsgiVersionImpl("1.2.3.qualifier");
		
		// when
		MavenVersion result = new VersionFactory().createMavenVersion(osgiVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof MavenVersionImpl);
		assertEquals("1.2.3-SNAPSHOT", result.toString());
	}
	
	@Test
	public void shouldCreateMavenVersionFromOsgiWithQualifier() throws Exception {
		// given
		OsgiVersion osgiVersion = new OsgiVersionImpl("1.2.3.BLA");
		
		// when
		MavenVersion result = new VersionFactory().createMavenVersion(osgiVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof MavenVersionImpl);
		assertEquals("1.2.3", result.toString());
	}
	
	@Test
	public void shouldCreateMavenVersionFromOsgiRelease() throws Exception {
		// given
		OsgiVersion osgiVersion = new OsgiVersionImpl("1.2.3");
		
		// when
		MavenVersion result = new VersionFactory().createMavenVersion(osgiVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof MavenVersionImpl);
		assertEquals("1.2.3", result.toString());
	}
	
	@Test
	public void shouldCreateOsgiVersionFromString() throws Exception {
		// given
		String mavenVersion = "1.2.3.qualifier";
		
		// when
		OsgiVersion result = new VersionFactory().createOsgiVersion(mavenVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof OsgiVersionImpl);
		assertEquals(mavenVersion, result.toString());
	}
	
	@Test
	public void shouldCreateOsgiVersionFromMavenSnapshot() throws Exception {
		// given
		MavenVersion osgiVersion = new MavenVersionImpl("1.2.3-RC3-SNAPSHOT");
		
		// when
		OsgiVersion result = new VersionFactory().createOsgiVersion(osgiVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof OsgiVersionImpl);
		assertEquals("1.2.3.qualifier", result.toString());
	}
	
	@Test
	public void shouldCreateOsgiVersionFromMavenWithSuffix() throws Exception {
		// given
		MavenVersion mavenVersion = new MavenVersionImpl("1.2.3-BLA");
		
		// when
		OsgiVersion result = new VersionFactory().createOsgiVersion(mavenVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof OsgiVersionImpl);
		assertEquals("1.2.3", result.toString());
	}
	
	@Test
	public void shouldCreateOsgiVersionFromMavenRelease() throws Exception {
		// given
		MavenVersion mavenVersion = new MavenVersionImpl("1.2.3");
		
		// when
		OsgiVersion result = new VersionFactory().createOsgiVersion(mavenVersion);
		
		// then
		assertNotNull(result);
		assertTrue(result instanceof OsgiVersionImpl);
		assertEquals("1.2.3", result.toString());
	}
	
}
