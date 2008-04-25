package org.eclipse.osee.framework.ui.plugin;

import java.util.logging.Level;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.logging.ILoggerFilter;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;

public class EclipseErrorLogLogger implements ILoggerListener {

   public ILoggerFilter getFilter() {
      return null;
   }

   public void log(String loggerName, String bundleId, Level level, String message, Throwable th) {
      int statusLevel = 0;
      if (level.intValue() >= Level.SEVERE.intValue()) {
         statusLevel = Status.ERROR;
      } else if (level.intValue() >= Level.WARNING.intValue()) {
         statusLevel = Status.WARNING;
      } else {
         statusLevel = Status.INFO;
      }
      PluginCoreActivator.getInstance().getLog().log(new Status(statusLevel, bundleId, statusLevel, message, th));
   }

}