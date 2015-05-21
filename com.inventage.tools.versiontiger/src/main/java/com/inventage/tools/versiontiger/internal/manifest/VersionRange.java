package com.inventage.tools.versiontiger.internal.manifest;

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;

public class VersionRange {

	private OsgiVersion startVersion;
	private OsgiVersion endVersion;
	
	private boolean startInclusive = true;
	private boolean endInclusive = false;
	
	public VersionRange() {
	}
	
	public VersionRange(VersionRange other) {
		this.startVersion = other.startVersion;
		this.endVersion = other.endVersion;
		this.startInclusive = other.startInclusive;
		this.endInclusive = other.endInclusive;
	}
	
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		print(sb);
		return sb.toString();
	}

	public boolean updateVersionIfOldMatches(OsgiVersion oldVersion, OsgiVersion newVersion, VersioningLoggerItem loggerItem, VersionRangeChangeStrategy changeStrategy) {
		if (oldVersion == null || rangeMatches(oldVersion)) {
			VersionRange oldRange = new VersionRange(this);
			
			changeStrategy.change(this, oldVersion, newVersion);
			
			if (!oldRange.equals(this)) {
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

	public boolean rangeMatches(OsgiVersion version) {
		if (startVersion.isLowerThan(version, startInclusive)) {
			return !isRange() || version.isLowerThan(endVersion, endInclusive);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (endInclusive ? 1231 : 1237);
		result = prime * result + ((endVersion == null) ? 0 : endVersion.hashCode());
		result = prime * result + (startInclusive ? 1231 : 1237);
		result = prime * result + ((startVersion == null) ? 0 : startVersion.hashCode());
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
		VersionRange other = (VersionRange) obj;
		if (endInclusive != other.endInclusive)
			return false;
		if (endVersion == null) {
			if (other.endVersion != null)
				return false;
		} else if (!endVersion.equals(other.endVersion))
			return false;
		if (startInclusive != other.startInclusive)
			return false;
		if (startVersion == null) {
			if (other.startVersion != null)
				return false;
		} else if (!startVersion.equals(other.startVersion))
			return false;
		return true;
	}
	
}
