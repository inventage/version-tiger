package mojos;

import org.apache.maven.plugin.logging.Log;

import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.internal.impl.StandardOutLoggerItem;

class MavenLogger implements VersioningLogger {
	
	private final Log log;

	MavenLogger(Log log) {
		this.log = log;
	}

	@Override
	public VersioningLoggerItem createVersioningLoggerItem() {
		return new StandardOutLoggerItem();
	}

	@Override
	public void addVersioningLoggerItem(VersioningLoggerItem loggerItem) {
		StandardOutLoggerItem item = (StandardOutLoggerItem) loggerItem;
		
		if (item.getStatus() != null) {
			switch (item.getStatus()) {
			case MESSAGE:
			case SUCCESS:
				log.info(item.formatLine());
				break;
			case WARNING:
				log.warn(item.formatLine());
			case ERROR:
				log.error(item.formatLine());
				break;
			default:
			}
		}
	}

}
