package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.inventage.tools.versiontiger.internal.manifest.FragmentHostHeader;
import com.inventage.tools.versiontiger.internal.manifest.RequireBundle;

public class FragmentHostHeaderTest {

	@Test
	public void shouldPrint() throws Exception {
		// given
		final StringBuilder result = new StringBuilder();
		FragmentHostHeader fragmentHostHeader = new FragmentHostHeader();
		String newLine = "\r\n";
		fragmentHostHeader.setNewLine(newLine);
		RequireBundle hostBundle = mock(RequireBundle.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("foo;visibility:=private");
				return null;
			}
		}).when(hostBundle).print(result);
		fragmentHostHeader.setHostBundle(hostBundle);
		
		// when
		fragmentHostHeader.print(result);
		
		// then
		assertEquals("Fragment-Host: foo;visibility:=private" + newLine, result.toString());
	}

}
