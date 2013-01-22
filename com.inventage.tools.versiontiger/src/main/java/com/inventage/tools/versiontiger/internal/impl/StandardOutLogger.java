package com.inventage.tools.versiontiger.internal.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;

public class StandardOutLogger implements VersioningLogger {

	@Override
	public VersioningLoggerItem createVersioningLoggerItem() {
		return new StandardOutLoggerItem();
	}

	@Override
	public void addVersioningLoggerItem(VersioningLoggerItem loggerItem) {
		StandardOutLoggerItem item = (StandardOutLoggerItem) loggerItem;
		
		/* We don't log items containing no logger status. */
		if (item.getStatus() == null) {
			return;
		}
		
		PrintStream outStream = item.getStatus().getConsolePrintStream();
		BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter(outStream));
		try {
			item.writeTo(outWriter);
			outWriter.append("\n");
			outWriter.flush();
		}
		catch (IOException e) {
			throw new RuntimeException("I am not able to write to the console!", e);
		}
	}

}
