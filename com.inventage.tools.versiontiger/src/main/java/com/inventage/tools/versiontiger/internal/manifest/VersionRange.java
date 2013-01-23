package com.inventage.tools.versiontiger.internal.manifest;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

public class VersionRange {

	private OsgiVersion startVersion;
	private OsgiVersion endVersion;
	
	private boolean startInclusive = true;
	private boolean endInclusive = false;
	
	public boolean isRange() {
		return endVersion != null;
	}
	
	public OsgiVersion getStartVersion() {
		return startVersion;
	}

	public void setStartVersion(OsgiVersion startVersion) {
		this.startVersion = startVersion;
	}

	public OsgiVersion getEndVersion() {
		return endVersion;
	}

	public void setEndVersion(OsgiVersion endVersion) {
		this.endVersion = endVersion;
	}

	public boolean isStartInclusive() {
		return startInclusive;
	}

	public void setStartInclusive(boolean startInclusive) {
		this.startInclusive = startInclusive;
	}

	public boolean isEndInclusive() {
		return endInclusive;
	}

	public void setEndInclusive(boolean endInclusive) {
		this.endInclusive = endInclusive;
	}

	public void print(StringBuilder result) {
		if (isRange()) {
			result.append(startInclusive ? "[" : "(");
		}
		result.append(startVersion.toString());
		if (isRange()) {
			result.append(",");
			result.append(endVersion.toString());
			result.append(endInclusive ? "]" : ")");
		}
	}

	public boolean updateVersionIfOldMatches(OsgiVersion oldVersion, OsgiVersion newVersion, VersioningLoggerItem loggerItem) {
		if (rangeMatches(oldVersion)) {
			OsgiVersion oldStartVersion = startVersion;
			newVersion = newVersion.withoutQualifier();
			
			if (!oldStartVersion.equals(newVersion)) {
				startVersion = newVersion;
				startInclusive = true;
				if (isRange()) {
					endVersion = startVersion.incrementMajorAndSnapshot().withoutQualifier();
					endInclusive = false;
				}
				
				/* We previously set the status to null. We now need to set it to success for the item being logged. We used this to prevent logging if nothing 
				 * interesting happens. */
				loggerItem.setStatus(VersioningLoggerStatus.SUCCESS);
				return true;
			}
		}
		else {
			/* Produce warning if there is matching artifact which has 'wrong' version. */
			loggerItem.setStatus(VersioningLoggerStatus.WARNING);
			loggerItem.appendToMessage(" -> Version not matching!");
		}
			
		return false;
	}

	private boolean rangeMatches(OsgiVersion version) {
		if (startVersion.isLowerThan(version, startInclusive)) {
			return !isRange() || version.isLowerThan(endVersion, endInclusive);
		}
		return false;
	}

}
