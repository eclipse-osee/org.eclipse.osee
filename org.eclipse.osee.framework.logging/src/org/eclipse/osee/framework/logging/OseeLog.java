/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

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

   public static void log(Class<?> loggerName, Level level, String message) {
      log.log(loggerName.getName(), loggerName.getName(), level, message);
   }

   public static void log(Class<?> loggerName, Level level, Throwable th) {
      log.log(loggerName.getName(), loggerName.getName(), level, th.getLocalizedMessage());
   }

   public static void log(String loggerName, Level level, String message, Throwable th) {
      log.log(loggerName, loggerName, level, message, th);
   }

   public static void log(Class<?> loggerName, Level level, String message, Throwable th) {
      log.log(loggerName.getName(), loggerName.getName(), level, message, th);
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
