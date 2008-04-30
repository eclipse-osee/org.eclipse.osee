package org.eclipse.osee.framework.servlet.log;

import java.util.logging.Level;

public interface ILogger {

   public void log(Level level, String message);

   public void log(Level level, String message, Throwable ex);
}
