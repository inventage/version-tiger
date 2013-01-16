package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.inventage.tools.versiontiger.internal.manifest.RequireBundle;
import com.inventage.tools.versiontiger.internal.manifest.RequireBundleHeader;

public class RequireBundleHeaderTest {

	@Test
	public void shouldPrint() throws Exception {
		// given
		final StringBuilder result = new StringBuilder();
		RequireBundleHeader requireBundleHeader = new RequireBundleHeader();
		requireBundleHeader.setNewLine("\r\n");
		RequireBundle requireBundle1 = mock(RequireBundle.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("foo;visibility:=private");
				return null;
			}
		}).when(requireBundle1).print(result);
		requireBundleHeader.addRequireBundle(requireBundle1);
		RequireBundle requireBundle2 = mock(RequireBundle.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("bar");
				return null;
			}
		}).when(requireBundle2).print(result);
		requireBundleHeader.addRequireBundle(requireBundle2);
		
		// when
		requireBundleHeader.print(result);
		
		// then
		assertEquals("Require-Bundle: foo;visibility:=private,\r\n bar\r\n", result.toString());
	}

}
