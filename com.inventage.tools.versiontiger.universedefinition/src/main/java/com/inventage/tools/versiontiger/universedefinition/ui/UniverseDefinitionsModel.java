package com.inventage.tools.versiontiger.universedefinition.ui;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

public class UniverseDefinitionsModel extends AbstractPropertyChangeSupport {

	public static final String PN_UNIVERSE_FILES = "universeFiles";

	private final Set<UniverseFile> universeFiles = Sets.newHashSet();

	UniverseDefinitionsModel(Set<File> files) {
		setFiles(files);
	}

	private void setFiles(Set<File> files) {
		universeFiles.clear();
		for (File file : files) {
			universeFiles.add(new UniverseFile(file));
		}
	}

	public void addFile(File file) {
		Set<UniverseFile> oldUniverseFiles = Sets.newHashSet(universeFiles);
		if (universeFiles.add(new UniverseFile(file))) {
			firePropertyChange(PN_UNIVERSE_FILES, oldUniverseFiles, universeFiles);
		}
	}

	public void removeFiles(Collection<UniverseFile> universeFile) {
		Set<UniverseFile> oldUniverseFiles = Sets.newHashSet(universeFiles);
		if (universeFiles.removeAll(universeFile)) {
			firePropertyChange(PN_UNIVERSE_FILES, oldUniverseFiles, universeFiles);
		}
	}

	public Set<File> getFiles() {
		Set<File> files = Sets.newHashSet();
		for (UniverseFile universeFile : universeFiles) {
			files.add(universeFile.getFile());
		}
		return files;
	}

	public Set<UniverseFile> getUniverseFiles() {
		return universeFiles;
	}

	public StringBuilder validate(StringBuilder error) {
		for (UniverseFile universeFile : universeFiles) {
			universeFile.validate(error);
		}
		return error;
	}
}
