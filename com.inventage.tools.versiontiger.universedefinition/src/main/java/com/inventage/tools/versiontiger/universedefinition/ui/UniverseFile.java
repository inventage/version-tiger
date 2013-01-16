package com.inventage.tools.versiontiger.universedefinition.ui;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.inventage.tools.versiontiger.NullVersioningLogger;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.universedefinition.ProjectUniverseFactory;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitionPlugin;

class UniverseFile extends AbstractPropertyChangeSupport {
	public static final String PN_NAME = "name";
	public static final String PN_LOCATION = "location";

	private final File file;

	UniverseFile(File file) {
		this.file = file;
	}

	public String getName() {
	    /* Note: Since we use the the factory only as a tool to get the file name, we pass a null logger. */
		return new ProjectUniverseFactory().create(getFile(), new NullVersioningLogger()).name();
	}

	public String getLocation() {
		return file.getAbsolutePath();
	}

	File getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UniverseFile other = (UniverseFile) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

	public StringBuilder validate(StringBuilder error) {
		try {
			ProjectUniverse universe = new ProjectUniverseFactory().create(getFile(), new NullVersioningLogger());
			if (universe.name() == null || universe.name().isEmpty()) {
				appendError(error, "No Name defined in ''{0}''", file.getAbsolutePath());
			}
			if (universe.listAllProjects().isEmpty()) {
				appendError(error, "No valid Project Locations defined in ''{0}''", file.getAbsolutePath());
			}
		} catch (Exception e) {
			UniverseDefinitionPlugin.getDefault().getLog()
					.log(new Status(IStatus.WARNING, UniverseDefinitionPlugin.PLUGIN_ID, "Error while validating universe definition file.", e));
			appendError(error, e.getMessage());
		}
		return error;
	}

	private StringBuilder appendError(StringBuilder error, String message, Object... arguments) {
		if (error.length() > 0) {
			error.append("\n");
		}
		error.append(MessageFormat.format(message, arguments));
		return error;
	}
}