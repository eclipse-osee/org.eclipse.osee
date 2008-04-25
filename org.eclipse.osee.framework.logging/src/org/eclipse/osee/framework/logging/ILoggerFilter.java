package org.eclipse.osee.framework.logging;

import java.util.logging.Level;
import java.util.regex.Pattern;

public interface ILoggerFilter {

	Level getLoggerLevel();

	Pattern bundleId();

	Pattern name();

}
