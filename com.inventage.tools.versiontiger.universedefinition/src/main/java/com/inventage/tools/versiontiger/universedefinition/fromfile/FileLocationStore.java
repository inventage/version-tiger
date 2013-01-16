package com.inventage.tools.versiontiger.universedefinition.fromfile;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitionPlugin;

public class FileLocationStore {

	private static final String SEPARATOR = "|"; //$NON-NLS-1$

	private final IPreferenceStore preferenceStore;

	private final String preferenceId;

	public FileLocationStore(String preferenceId) {
		this(UniverseDefinitionPlugin.getDefault().getPreferenceStore(), preferenceId);
	}

	public FileLocationStore(IPreferenceStore preferenceStore, String preferenceId) {
		this.preferenceStore = Preconditions.checkNotNull(preferenceStore);
		this.preferenceId = preferenceId;
	}

	public Set<File> read() {
		String[] keys = loadKeys();
		Set<File> files = Sets.newHashSet();
		if (keys != null && keys.length > 0) {
			files.addAll(loadFiles(keys));
		}
		return files;
	}

	private String[] loadKeys() {
		String string = preferenceStore.getString(preferenceId);
		if (string != null && !string.isEmpty()) {
			return Pattern.compile(SEPARATOR, Pattern.LITERAL).split(string);
		}
		return null;
	}

	private void storeKeys(Collection<String> keys) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : keys) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(SEPARATOR);
			}
			stringBuilder.append(key);
		}
		preferenceStore.setValue(preferenceId, stringBuilder.toString());
	}

	private Set<File> loadFiles(String[] keys) {
		Set<File> files = Sets.newHashSet();
		for (String key : keys) {
			String fileLocation = preferenceStore.getString(key);
			File file = createFile(fileLocation);
			if (file != null) {
				files.add(file);
			}
		}
		return files;
	}

	private File createFile(String fileLocation) {
		if (fileLocation == null || fileLocation.isEmpty()) {
			return null;
		}
		File file = new File(fileLocation);
		if (file.exists() && file.canRead()) {
			return file;
		}
		return null;
	}

	public void write(Set<File> files) {
		clearOldFileLocations();
		storeFileLocations(files);
	}

	private void storeFileLocations(Set<File> files) {
		int i = 0;
		Set<String> keys = Sets.newHashSet();
		for (File file : files) {
			String key = preferenceId + i++;
			preferenceStore.setValue(key, file.getAbsolutePath());
			keys.add(key);
		}
		storeKeys(keys);
	}

	private void clearOldFileLocations() {
		String[] keys = loadKeys();
		if (keys != null && keys.length > 0) {
			for (String key : keys) {
				preferenceStore.setToDefault(key);
			}
		}
	}
}
