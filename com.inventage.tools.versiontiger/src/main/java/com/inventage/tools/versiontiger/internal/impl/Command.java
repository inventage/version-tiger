package com.inventage.tools.versiontiger.internal.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Parses a command line interpreter line and offers the command as well as the single arguments to clients.
 * 
 * @author nw
 */
public class Command {

	private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\".*?\"|'.*?'|[\\S]+");
	private static final String WHITESPACE_PATTERN = "\\s+";
	private static final String COMMENT_PREFIX = "#"; 
	
	private final String originalLine;
	private final List<String> arguments = new ArrayList<String>();
	
	private boolean comment;
	private VersionTigerBatchOperation operation;
	
	public Command(String line) {
		
		this.originalLine = line;
		String trimmedLine = line.trim();
		
		if (trimmedLine.isEmpty() || trimmedLine.startsWith(COMMENT_PREFIX)) {
			comment = true;
			return;
		}
		
		parseOperation(trimmedLine);
		
		/* We only look for arguments if the operation is kosher. */
		if (isValidOperation()) {
			parseArguments(trimmedLine);
		}
	}
	
	public boolean isComment() {
		return comment;
	}
	
	public boolean isValidOperation() {
		return operation != null;
	}
	
	private void parseOperation(String line) {
		List<String> splitted = new ArrayList<String>(Arrays.asList(line.split(WHITESPACE_PATTERN)));
		try {
			operation = VersionTigerBatchOperation.valueOf(splitted.get(0).toUpperCase());
		}
		catch (IllegalArgumentException e) {
			/* If we don't succeed to determine the operation, it must be an unknown one. The operation field thus remains null. */
			return;
		}
	}
	
	private void parseArguments(String line) {
		
		List<String> splitted = new ArrayList<String>(Arrays.asList(line.split(WHITESPACE_PATTERN)));
		
		/* If there is only one entry in this array, there are no arguments. */
		if (splitted.size() <= 1) {
			return;
		}
		
		/* We're now cutting away the operation token. */
		String argumentString = line.substring(splitted.remove(0).length()).trim();
		
		/* The arguments are separated by each other with a space. One may use quotation marks as argument limiters. */
		Scanner scanner = new Scanner(argumentString);
		try {
			String arg;
			while ((arg = scanner.findInLine(ARGUMENT_PATTERN)) != null) {
				/* We're removing any quotation marks. */
				arguments.add(arg.replace("\"", "").replace("\'", ""));
			}
		} finally {
			scanner.close();
		}
	}
	
	public VersionTigerBatchOperation getOperation() {
		return operation;
	}
	
	public List<String> getArguments() {
		return arguments;
	}
	
	public String getArgument(int index) {
		return arguments.get(index);
	}
	
	public int getArgumentCount() {
		return arguments.size();
	}
	
	public String getOriginalLine() {
		return originalLine;
	}
	
}
