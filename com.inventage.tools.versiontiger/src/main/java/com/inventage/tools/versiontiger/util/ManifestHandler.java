package com.inventage.tools.versiontiger.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManifestHandler {

	private static final Pattern PATTERN_VERSION = Pattern.compile("^(.*\n)?Bundle-Version: (.+?)\r?\n([^ ].*)?$", Pattern.DOTALL);
	private static final Pattern PATTERN_LINE_BREAK = Pattern.compile("\r?\n ");

	public String readVersion(String manifest) {
		Matcher matcher = PATTERN_VERSION.matcher(manifest);

		if (matcher.matches()) {
			String rawHeaderValue = matcher.group(2);
			return PATTERN_LINE_BREAK.matcher(rawHeaderValue).replaceAll("");
		}

		return null;
	}

	public String writeVersion(String manifest, String newVersion) {
		Matcher matcher = PATTERN_VERSION.matcher(manifest);

		if (matcher.matches()) {
			StringBuilder result = new StringBuilder();

			result.append(manifest.substring(0, matcher.start(2)));
			result.append(wrapManifestHeaderValue("Bundle-Version", newVersion, guessLineEnding(manifest)));
			result.append(manifest.substring(matcher.end(2)));

			return result.toString();
		}

		return manifest;
	}

	private String guessLineEnding(String manifest) {
		return manifest.contains("\r") ? "\r\n" : "\n";
	}

	private String wrapManifestHeaderValue(String headerName, String headerValue, String lineEnding) {
		StringBuilder result = new StringBuilder();
		int nextLineLength = 80 - headerName.length() - 2 /* colon space */- 2 /*
																			 * \r
																			 * \
																			 * n
																			 */;

		while (headerValue != null) {
			if (headerValue.length() <= nextLineLength) {
				result.append(headerValue);
				headerValue = null;
			} else {
				String currentLine = headerValue.substring(0, nextLineLength);
				headerValue = headerValue.substring(nextLineLength);
				result.append(currentLine);
				result.append(lineEnding);
				result.append(" ");
			}
			nextLineLength = 77 /* 80 - 2 chars for newlines - space */;
		}

		return result.toString();
	}

}
