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
 * @author Andrew M. Finkbeiner
 */
public class OseeLog {

   private static StatusManager sm;
   private static OseeLogger log;

   static void makevalid() {
      sm = new StatusManager();
      log = new OseeLogger();
   }

   private static OseeLogger getLog() {
      if (log == null || sm == null) {
         makevalid();
      }
      return log;
   }

   private static StatusManager getSM() {
      if (sm == null || log == null) {
         makevalid();
      }
      return sm;
   }

   public static void log(Class<?> activatorClass, Level level, String message) {
      getLog().log(activatorClass.getName(), activatorClass.getName(), level, message);
   }

   public static void log(Class<?> activatorClass, Level level, Throwable th) {
      getLog().log(activatorClass.getName(), activatorClass.getName(), level, th.getLocalizedMessage(), th);
   }

   public static void log(Class<?> activatorClass, Level level, String message, Throwable th) {
      getLog().log(activatorClass.getName(), activatorClass.getName(), level, message, th);
   }

   public static void log(String loggerName, String bundleId, Level level, String message, Throwable th) {
      getLog().log(loggerName, bundleId, level, message, th);
   }

   public static void format(String loggerName, String bundleId, Level level, String message, Object... objects) {
      getLog().format(loggerName, bundleId, level, message, objects);
   }

   public static void format(Throwable th, String loggerName, String bundleId, Level level, String message, Object... objects) {
      getLog().format(th, loggerName, bundleId, level, message, objects);
   }

   public static void registerLoggerListener(ILoggerListener listener) {
      getLog().registerLoggerListener(listener);
   }

   public static void unregisterLoggerListener(ILoggerListener listener) {
      getLog().unregisterLoggerListener(listener);
   }

   public static void setLevel(String loggerName, Level level) {
      getLog().setLevel(loggerName, level);
   }

   public static void report(IHealthStatus status) {
      getSM().report(status);
   }

   public static void report(List<IHealthStatus> status) {
      for (IHealthStatus s : status) {
         getSM().report(s);
      }
   }

   public static boolean register(IStatusListener listener, IStatusListenerFilter filter) {
      return getSM().register(listener, filter);
   }

   public static boolean register(IStatusListener listener) {
      return getSM().register(listener);
   }

   public static void deregister(IStatusListener listener) {
      getSM().deregister(listener);
   }
}
