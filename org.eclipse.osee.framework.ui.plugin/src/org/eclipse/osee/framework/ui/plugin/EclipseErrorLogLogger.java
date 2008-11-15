package org.eclipse.osee.framework.ui.plugin;

import java.util.logging.Level;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;

public class EclipseErrorLogLogger implements ILoggerListener {

   public EclipseErrorLogLogger() {
   }

   public void log(String loggerName, Level level, String message, Throwable th) {
      int statusLevel = 0;
      if (level.intValue() >= Level.SEVERE.intValue()) {
         statusLevel = Status.ERROR;
      } else if (level.intValue() >= Level.WARNING.intValue()) {
         statusLevel = Status.WARNING;
      } else if (level.intValue() >= Level.INFO.intValue()) {
         statusLevel = Status.INFO;
      } else if (OseeCodeVersion.isDevelopment()) {
         statusLevel = Status.INFO;
      } else {
         return;
      }
      PluginCoreActivator.getInstance().getLog().log(new Status(statusLevel, loggerName, statusLevel, message, th));
   }

}