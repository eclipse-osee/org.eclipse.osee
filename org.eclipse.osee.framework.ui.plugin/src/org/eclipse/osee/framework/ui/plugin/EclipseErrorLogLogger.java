package org.eclipse.osee.framework.ui.plugin;

import java.util.logging.Level;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.ILoggerFilter;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;

public class EclipseErrorLogLogger implements ILoggerListener {

   private boolean isDeveloper = false;

   public EclipseErrorLogLogger() {
      isDeveloper = OseeProperties.isDeveloper();
   }

   public ILoggerFilter getFilter() {
      return null;
   }

   public void log(String loggerName, String bundleId, Level level, String message, Throwable th) {
      int statusLevel = 0;
      if (level.intValue() >= Level.SEVERE.intValue()) {
         statusLevel = Status.ERROR;
      } else if (level.intValue() >= Level.WARNING.intValue()) {
         statusLevel = Status.WARNING;
      } else if (level.intValue() >= Level.INFO.intValue()) {
         statusLevel = Status.INFO;
      } else if (isDeveloper) {
         statusLevel = Status.INFO;
      } else {
         return;
      }
      PluginCoreActivator.getInstance().getLog().log(new Status(statusLevel, bundleId, statusLevel, message, th));
   }

}