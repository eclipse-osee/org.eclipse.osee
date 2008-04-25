package org.eclipse.osee.framework.logging;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ConsoleLogger implements ILoggerListener {

	public Set<Logger> initializedLoggers;
	public static Level filterLevel;

	static {
		String level = System.getProperty("osee.console", "SEVERE");

		filterLevel = Level.parse(level);

	}

	private ILoggerFilter filter = new ILoggerFilter(){
		public Pattern bundleId() {
			return null;
		}
		public Level getLoggerLevel() {
			return filterLevel;
		}
		public Pattern name() {
			return null;
		}
	};

	public ConsoleLogger(){
		initializedLoggers = new HashSet<Logger>(64);
	}

	public ILoggerFilter getFilter() {
		return filter;
	}

	public void log(String loggerName, String bundleId, Level level,
			String message, Throwable th) {
		Logger logger = Logger.getLogger(loggerName);
		if(initializedLoggers.add(logger)){
			logger.setUseParentHandlers(false);
			SimpleOseeHandler handler = new SimpleOseeHandler();
			logger.addHandler(handler);
			logger.setLevel(Level.ALL);
		}
		logger.log(level, message, th);
	}

}
