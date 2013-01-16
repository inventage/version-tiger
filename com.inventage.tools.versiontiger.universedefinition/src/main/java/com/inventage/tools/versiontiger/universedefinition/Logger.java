package com.inventage.tools.versiontiger.universedefinition;

import java.text.MessageFormat;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Logger {
	private final ILog log;
	private final String pluginId;

	Logger(ILog log, String pluginId) {
		this.log = log;
		this.pluginId = pluginId;
	}

	// ERROR

	public void error(String message) {
		log.log(status(IStatus.ERROR, message));
	}

	public void error(String pattern, Object... arguments) {
		log.log(status(IStatus.ERROR, pattern, arguments));
	}

	public void error(Throwable e, String message) {
		log.log(status(IStatus.ERROR, e, message));
	}

	public void error(Throwable e, String pattern, Object... arguments) {
		log.log(status(IStatus.ERROR, e, pattern, arguments));
	}

	// INFO

	public void info(String message) {
		log.log(status(IStatus.INFO, message));
	}

	public void info(String pattern, Object... arguments) {
		log.log(status(IStatus.INFO, pattern, arguments));
	}

	public void info(Throwable e, String message) {
		log.log(status(IStatus.INFO, e, message));
	}

	public void info(Throwable e, String pattern, Object... arguments) {
		log.log(status(IStatus.INFO, e, pattern, arguments));
	}

	// WARNING

	public void warn(String message) {
		log.log(status(IStatus.WARNING, message));
	}

	public void warn(String pattern, Object... arguments) {
		log.log(status(IStatus.WARNING, pattern, arguments));
	}

	public void warn(Throwable e, String message) {
		log.log(status(IStatus.WARNING, e, message));
	}

	public void warn(Throwable e, String pattern, Object... arguments) {
		log.log(status(IStatus.WARNING, e, pattern, arguments));
	}

	private IStatus status(int severity, String pattern, Object... arguments) {
		String message = MessageFormat.format(pattern, arguments);
		return new Status(severity, pluginId, message);
	}

	private IStatus status(int severity, Throwable e, String pattern, Object... arguments) {
		String message = MessageFormat.format(pattern, arguments);
		return new Status(severity, pluginId, message, e);
	}
}
