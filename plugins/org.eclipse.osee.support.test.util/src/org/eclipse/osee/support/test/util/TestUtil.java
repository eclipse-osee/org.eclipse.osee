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
package org.eclipse.osee.support.test.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class TestUtil {
   private static final String DEMO_DB_TYPE = "demo";
   public static final Collection<String> ignoreLogging = Arrays.asList("No image was defined for art type",
      "Unable to load the image for [SAVED]");

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static void checkDbInitSuccess() throws OseeCoreException {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
   }

   public static boolean isDbInitSuccessful() throws OseeCoreException {
      return OseeInfo.isBoolean("DbInitSuccess");
   }

   public static void setDbInitSuccessful(boolean success) throws OseeCoreException {
      OseeInfo.setBoolean("DbInitSuccess", success);
   }

   /**
    * Need to match methods in OseeProperties
    */
   public static void setIsInTest(boolean isInTest) {
      System.setProperty("osee.isInTest", String.valueOf(isInTest));
   }

   public static boolean isProductionDb() throws OseeCoreException {
      return ClientSessionManager.isProductionDataStore();
   }

   public static boolean isTestDb() throws OseeCoreException {
      return !isProductionDb(); // && !isDemoDb();
   }

   public static boolean isDemoDb() throws OseeCoreException {
      return DEMO_DB_TYPE.equals(OseeInfo.getCachedValue(OseeInfo.DB_TYPE_KEY));
   }

   public static void setDemoDb(boolean set) throws OseeCoreException {
      OseeInfo.putValue(OseeInfo.DB_TYPE_KEY, set ? DEMO_DB_TYPE : "");
   }

   public static void sleep(long milliseconds) {
      //      System.out.println("Sleeping " + milliseconds);
      try {
         Thread.sleep(milliseconds);
      } catch (InterruptedException ex) {
         // do nothing
      }
      //      System.out.println("Awake");
   }

   public static SevereLoggingMonitor severeLoggingStart() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      return monitorLog;
   }

   public static int getNumberOfLogsAtLevel(SevereLoggingMonitor monitorLog, Level level) {
      int count = 0;
      for (IHealthStatus hStatus : monitorLog.getLogsAtLevel(level)) {
         // do not count the valid ignored logs
         for (String str : ignoreLogging) {
            if (hStatus.getMessage().startsWith(str) == false) {
               count++;
            }
         }
      }
      return count++;
   }

   public static void severeLoggingStop(SevereLoggingMonitor monitorLog) {
      OseeLog.unregisterLoggerListener(monitorLog);
   }

   public static void severeLoggingEnd(SevereLoggingMonitor monitorLog) throws Exception {
      severeLoggingEnd(monitorLog, ignoreLogging);
   }

   public static void severeLoggingEnd(SevereLoggingMonitor monitorLog, Collection<String> ignoreLogging) throws Exception {
      OseeLog.unregisterLoggerListener(monitorLog);
      Collection<IHealthStatus> healthStatuses = monitorLog.getAllLogs();
      int numExceptions = 0;
      if (healthStatuses.size() > 0) {
         for (IHealthStatus status : healthStatuses) {
            if (status.getLevel() != Level.INFO) {
               boolean ignoreIt = false;
               for (String str : ignoreLogging) {
                  if (status.getMessage().startsWith(str)) {
                     ignoreIt = true;
                     break;
                  }
               }
               if (!ignoreIt) {
                  if (status.getException() != null) {
                     System.err.println("SevereLogging Exception: " + Lib.exceptionToString(status.getException()));
                  } else {
                     System.err.println("SevereLogging Exception: " + status.getMessage());
                  }
                  numExceptions++;
               }
            }
         }
         if (numExceptions > 0) {
            throw new OseeStateException("SevereLoggingMonitor found [%d] exceptions (see console for details)!",
               numExceptions);
         }
      }
   }

   public static void checkThatIncreased(Map<String, Integer> prevCount, Map<String, Integer> postCount) {
      for (String name : prevCount.keySet()) {
         if (!OseeProperties.isInTest()) {
            String incStr = postCount.get(name) > prevCount.get(name) ? "Increased" : "ERROR, Not Increased";
            System.out.println(String.format(incStr + ": [%s] pre[%d] vs post[%d]", name, prevCount.get(name),
               postCount.get(name)));
         }
      }
      for (String name : prevCount.keySet()) {
         Assert.assertTrue(String.format("[%s] did not increase as expected: pre[%d] vs post[%d]", name,
            prevCount.get(name), postCount.get(name)), postCount.get(name) > prevCount.get(name));
      }
   }

   public static void checkThatEqual(Map<String, Integer> prevCount, Map<String, Integer> postCount) {
      for (String tableName : prevCount.keySet()) {
         if (!OseeProperties.isInTest()) {
            String equalStr = postCount.get(tableName).equals(prevCount.get(tableName)) ? "Equal" : "ERROR, NotEqual";
            System.out.println(String.format(equalStr + ": [%s] pre[%d] post[%d]", tableName, prevCount.get(tableName),
               postCount.get(tableName)));
         }
      }
      for (String tableName : prevCount.keySet()) {
         Assert.assertTrue(
            String.format("[%s] count not equal pre[%d] post[%d]", tableName, prevCount.get(tableName),
               postCount.get(tableName)), postCount.get(tableName).equals(prevCount.get(tableName)));
      }
   }

}
