package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.inventage.tools.versiontiger.internal.manifest.ManifestHeader;
import com.inventage.tools.versiontiger.internal.manifest.ManifestSection;

public class ManifestSectionTest {

	@Test
	public void shouldPrint() throws Exception {
		// given
		final StringBuilder result = new StringBuilder();
		ManifestSection section = new ManifestSection();
		ManifestHeader header1 = mock(ManifestHeader.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("Foo1: bar\n");
				return null;
			}
		}).when(header1).print(result);
		section.addHeader(header1);
		ManifestHeader header2 = mock(ManifestHeader.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("Foo2: bar\n");
				return null;
			}
		}).when(header2).print(result);
		section.addHeader(header2);
		
		// when
		section.print(result);
		
		// then
		assertEquals("Foo1: bar\nFoo2: bar\n", result.toString());
	}

}
