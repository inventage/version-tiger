package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.inventage.tools.versiontiger.internal.manifest.VersionAttribute;
import com.inventage.tools.versiontiger.internal.manifest.VersionRange;

public class VersionAttributeTest {

	@Test
	public void shouldPrintWithQuotes() throws Exception {
		// given
		final StringBuilder result = new StringBuilder();
		VersionAttribute versionAttribute = new VersionAttribute();
		VersionRange versionRange = mock(VersionRange.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("[1.0,2.0)");
				return null;
			}
		}).when(versionRange).print(result);
		versionAttribute.setVersionRange(versionRange);
		versionAttribute.setQuotes(true);
		
		// when
		versionAttribute.print(result);
		
		// then
		assertEquals("bundle-version=\"[1.0,2.0)\"", result.toString());
	}

	@Test
	public void shouldPrintWithoutQuotes() throws Exception {
		// given
		final StringBuilder result = new StringBuilder();
		VersionAttribute versionAttribute = new VersionAttribute();
		VersionRange versionRange = mock(VersionRange.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("1.0");
				return null;
			}
		}).when(versionRange).print(result);
		versionAttribute.setVersionRange(versionRange);
		versionAttribute.setQuotes(false);
		
		// when
		versionAttribute.print(result);
		
		// then
		assertEquals("bundle-version=1.0", result.toString());
	}

	@Test
	public void shouldPrintWithQuotesWhenRange() throws Exception {
		// given
		final StringBuilder result = new StringBuilder();
		VersionAttribute versionAttribute = new VersionAttribute();
		VersionRange versionRange = mock(VersionRange.class);
		when(versionRange.isRange()).thenReturn(true);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("[1.0,2.0)");
				return null;
			}
		}).when(versionRange).print(result);
		versionAttribute.setVersionRange(versionRange);
		versionAttribute.setQuotes(false);
		
		// when
		versionAttribute.print(result);
		
		// then
		assertEquals("bundle-version=\"[1.0,2.0)\"", result.toString());
	}

}
