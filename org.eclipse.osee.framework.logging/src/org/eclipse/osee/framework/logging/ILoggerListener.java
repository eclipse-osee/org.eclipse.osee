package org.eclipse.osee.framework.logging;

import java.util.logging.Level;

public interface ILoggerListener {

	ILoggerFilter getFilter();

	void log(String loggerName, String bundleId, Level level, String message, Throwable th);

}
