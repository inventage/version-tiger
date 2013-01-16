package com.inventage.tools.versiontiger.internal.manifest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.inventage.tools.versiontiger.internal.manifest.RequireBundle;
import com.inventage.tools.versiontiger.internal.manifest.RequireBundleAttribute;

public class RequireBundleTest {

	@Test
	public void shouldPrint() throws Exception {
		// given
		final StringBuilder result = new StringBuilder();
		RequireBundle requireBundle = new RequireBundle();
		requireBundle.setId("foo.bar");
		RequireBundleAttribute requireBundleAttribute1 = mock(RequireBundleAttribute.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("bundle-version=\"1\"");
				return null;
			}
		}).when(requireBundleAttribute1).print(result);
		requireBundle.addRequireBundleAttribute(requireBundleAttribute1);
		RequireBundleAttribute requireBundleAttribute2 = mock(RequireBundleAttribute.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				result.append("visibility:=package");
				return null;
			}
		}).when(requireBundleAttribute2).print(result);
		requireBundle.addRequireBundleAttribute(requireBundleAttribute2);
		
		// when
		requireBundle.print(result);
		
		// then
		assertEquals("foo.bar;bundle-version=\"1\";visibility:=package", result.toString());
	}

}
