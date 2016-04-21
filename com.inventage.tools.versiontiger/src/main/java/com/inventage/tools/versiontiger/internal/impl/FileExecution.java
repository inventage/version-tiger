package com.inventage.tools.versiontiger.internal.impl;

public class FileExecution {
	
	private CommandExecuter commandExecuter = new CommandExecuter(new VersioningImpl(), System.getProperty("user.dir"), new StandardOutLogger());
	private String[] args;
	
	public FileExecution(String[] args) {
		this.args = args;
		this.execute();
	}
	
	private void execute() {
		Command command = new Command(VersionTigerBatchOperation.INCLUDE.toString() + " " + args[0]);
		commandExecuter.execute(command);
	}
}
