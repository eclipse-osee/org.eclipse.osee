/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.logging;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeLog {

   private static StatusManager sm;
   private static OseeLogger log;

   private static synchronized void makevalid() {
      if (log == null || sm == null) {
         sm = new StatusManager();
         log = new OseeLogger();
      }
   }

   private static OseeLogger getLog() {
      makevalid();
      return log;
   }

   private static StatusManager getSM() {
      makevalid();
      return sm;
   }

   public static void log(Class<?> activatorClass, Level level, String message) {
      if (level.intValue() == Level.SEVERE.intValue()) {
         getLog().log(activatorClass.getName(), level, message, new Exception("used to get a stack trace"));
      } else {
         getLog().log(activatorClass.getName(), level, message, null);
      }
   }

   public static void log(Class<?> activatorClass, Level level, Throwable th) {
      getLog().log(activatorClass.getName(), level, th.getLocalizedMessage(), th);
   }

   public static void log(Class<?> activatorClass, Level level, String message, Throwable th) {
      getLog().log(activatorClass.getName(), level, message, th);
   }

   public static void logf(Class<?> activatorClass, Level level, Throwable th, String message, Object... objects) {
      getLog().format(th, activatorClass.getName(), level, message, objects);
   }

   public static void logf(Class<?> activatorClass, Level level, String message, Object... objects) {
      getLog().format(activatorClass.getName(), level, message, objects);
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

   public static void reportStatus(IHealthStatus status) {
      getSM().report(status);
   }

   public static String getStatusReport() {
      return getSM().getReport();
   }

   public static boolean isStatusOk() {
      return getSM().isStatusOk();
   }

   public static IHealthStatus getStatusByName(String sourceName) {
      return getSM().getHealthStatusByName(sourceName);
   }

   public static Collection<IHealthStatus> getStatus() {
      return getSM().getHealthStatus();
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

   public static void reportStatus(List<IHealthStatus> statuses) {
      for (IHealthStatus status : statuses) {
         reportStatus(status);
      }
   }
}
