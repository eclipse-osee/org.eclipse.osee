/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.logging;

import java.util.List;
import java.util.logging.Level;

/**
 * @author afinkbei
 */
public class OseeLog {

   private static StatusManager sm;
   private static OseeLogger log;

   static void makevalid() {
      sm = Activator.getInstance().getStatusManager();
      log = Activator.getInstance().getLogger();
      register(new IStatusListener() {

         @Override
         public void onStatus(IHealthStatus status) {
            log.log(status.getPlugin(), status.getPlugin(), status.getLevel(), status.getMessage(),
                  status.getException());

         }

      });
   }

   public static void log(String loggerName, String bundleId, Level level, String message) {
      log.log(loggerName, bundleId, level, message);
   }

   public static void log(String loggerName, Level level, String message) {
      log.log(loggerName, loggerName, level, message);
   }

   public static void log(String loggerName, Level level, String message, Throwable th) {
      log.log(loggerName, loggerName, level, message, th);
   }

   public static void log(String loggerName, String bundleId, Level level, String message, Throwable th) {
      log.log(loggerName, bundleId, level, message, th);
   }

   public static void format(String loggerName, String bundleId, Level level, String message, Object... objects) {
      log.format(loggerName, bundleId, level, message, objects);
   }

   public static void format(Throwable th, String loggerName, String bundleId, Level level, String message, Object... objects) {
      log.format(th, loggerName, bundleId, level, message, objects);
   }

   public static void registerLoggerListener(ILoggerListener listener) {
      log.registerLoggerListener(listener);
   }

   public static void unregisterLoggerListener(ILoggerListener listener) {
      log.unregisterLoggerListener(listener);
   }

   public static void setLevel(String loggerName, Level level) {
      log.setLevel(loggerName, level);
   }

   public static void report(IHealthStatus status) {
      sm.report(status);
   }

   public static void report(List<IHealthStatus> status) {
      for (IHealthStatus s : status) {
         sm.report(s);
      }
   }

   public static boolean register(IStatusListener listener, IStatusListenerFilter filter) {
      return sm.register(listener, filter);
   }

   public static boolean register(IStatusListener listener) {
      return sm.register(listener);
   }

   public static void deregister(IStatusListener listener) {
      sm.deregister(listener);
   }
}
