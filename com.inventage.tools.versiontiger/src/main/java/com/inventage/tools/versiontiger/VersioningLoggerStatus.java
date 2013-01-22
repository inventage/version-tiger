package com.inventage.tools.versiontiger;

import java.io.PrintStream;

public enum VersioningLoggerStatus {
	
	SUCCESS(System.out),
	MESSAGE(System.out),
	WARNING(System.err),
	ERROR(System.err);
	
	PrintStream consolePrintStream;
	
	private VersioningLoggerStatus(PrintStream consolePrintStream) {
		this.consolePrintStream = consolePrintStream;
	}
	
	public PrintStream getConsolePrintStream() {
		return consolePrintStream;
	}
	
}
