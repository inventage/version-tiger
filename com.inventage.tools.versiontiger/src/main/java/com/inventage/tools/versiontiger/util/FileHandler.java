package com.inventage.tools.versiontiger.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class FileHandler {
	public static final String LINE_DELIMITER = "\r?\n";

	public String readFileContent(File path) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
			return readFileContent(bufferedReader);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read file: " + path, e);
		}
	}

	public String readFileContent(Reader reader) {
		try {
			try {
				StringBuilder content = new StringBuilder();
				char[] data = new char[1024];
				int dataRead;
				while ((dataRead = reader.read(data)) > 0) {
					content.append(data, 0, dataRead);
				}
				return content.toString();
			}
			finally {
				reader.close();
			}
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read file", e);
		}
	}

	public void writeFileContent(File path, String content) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
			try {
				bufferedWriter.write(content);
			} finally {
				bufferedWriter.close();
			}
		} catch (IOException e) {
			throw new IllegalStateException("Cannot write to file: " + path, e);
		}
	}
	
	public File createFileFromPath(String rootPath, String relativeOrAbsolutePath) {
		File result = new File(relativeOrAbsolutePath);
		
		if (!result.isAbsolute()) {
			result = new File(rootPath, relativeOrAbsolutePath);
		}
		
		return result;
	}

}
