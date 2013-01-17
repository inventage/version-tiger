package com.inventage.tools.versiontiger.internal.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommandTest {

	@Test
	public void shouldRecognizeEmptyLines() {
		
		// given
		String commandLine = "  ";
		
		// when
		Command command = new Command(commandLine);
		
		// then
		assertTrue(command.isComment());
	}
	
	@Test
	public void shouldRecognizeCommentedLines() {

		// given
		String commandLine = "# This is a comment";
		
		// when
		Command command = new Command(commandLine);
		
		// then
		assertTrue(command.isComment());
	}
	
	@Test
	public void shouldRecognizeCommentedLinesWithSpacesInFront() {

		// given
		String commandLine = "     # This is a comment";
		
		// when
		Command command = new Command(commandLine);
		
		// then
		assertTrue(command.isComment());
	}
	
	@Test
	public void shouldRecognizeUnknownCommand() {
		
		// given
		String commandLine = "xyz abc z";
		
		// when
		Command command = new Command(commandLine);
		
		// then
		assertFalse(command.isValidOperation());
	}
	
	@Test
	public void shouldRecognizeKnownCommand() {

		// given
		String commandLine = "setting key value";
		
		// when
		Command command = new Command(commandLine);
		
		// then
		assertTrue(command.isValidOperation());
	}
	
	@Test
	public void shouldRecognizeRightNumberOfArguments() {
		
		// given
		String commandLine1 = "list";
		String commandLine2 = "release version";
		String commandLine3 = "setting key value";
		String commandLine4 = "property artifact-id key value";
		
		// when
		Command command1 = new Command(commandLine1);
		Command command2 = new Command(commandLine2);
		Command command3 = new Command(commandLine3);
		Command command4 = new Command(commandLine4);
		
		// then
		assertEquals(0, command1.getArgumentCount());
		assertEquals(1, command2.getArgumentCount());
		assertEquals(2, command3.getArgumentCount());
		assertEquals(3, command4.getArgumentCount());
	}
	
	@Test
	public void shouldRecognizeArgumentsInQuotes() {
		
		// given
		String commandLine = "setting \"my\" 'precious'";
		
		// when
		Command command = new Command(commandLine);
		
		// then
		assertEquals("my", command.getArgument(0));
		assertEquals("precious", command.getArgument(1));
	}
	
	@Test
	public void shouldRecognizeEmptyArgumentsInQuotes() {
		
		// given
		String commandLine = "setting \"my\" ''";
		
		// when
		Command command = new Command(commandLine);
		
		// then
		assertEquals("my", command.getArgument(0));
		assertEquals("", command.getArgument(1));
	}
	
	@Test
	public void shouldRecognizeArgumentsInQuotesWithSpacesInside() {
		
		// given
		String commandLine = "release 'my precious'";
		
		// when
		Command command = new Command(commandLine);
		
		// then
		assertEquals(1, command.getArgumentCount());
		assertEquals("my precious", command.getArgument(0));
	}
	
}
