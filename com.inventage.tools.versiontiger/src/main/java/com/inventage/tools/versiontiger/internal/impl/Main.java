package com.inventage.tools.versiontiger.internal.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private StandardOutLogger logger = new StandardOutLogger();

	public static void main(String[] args) {
		new Main().executeCommands(args);
	}

	public void executeCommands(String[] args) {
		try {
			CommandExecuter commandExecuter = new CommandExecuter(new VersioningImpl(), getCurrentDirectory(), logger);
			if (args.length == 1)
				executeCommandFromArgumentFile(args, commandExecuter);
			else if (args.length == 0) {
				System.out.println("Reading commands from standard input:");
				commandExecuter.executeCommands(stdIn);
			}
			else System.err.println("Invalid number of arguments provided");
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read commands.", e);
		} finally {
			try {
				stdIn.close();
			} catch (IOException e) {
				// nop
			}
			System.out.println("Quitting.");
		}
	}

	private String getCurrentDirectory() {
		return System.getProperty("user.dir");
	}

	private void executeCommandFromArgumentFile(String[] args, CommandExecuter commandExecuter) {
		if (args.length >= 1) {
			commandExecuter.execute(new Command(VersionTigerBatchOperation.INCLUDE.toString() + " " + args[0]));
		}
	}

}
