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
import junit.framework.TestCase;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;

/**
 * @author Donald G. Dunne
 */
public class TestUtil {
   private static final String DEMO_DB_TYPE = "demo";
   public static final String DEMO_CODE_TEAM_WORKFLOW_ARTIFACT = "Demo Code Team Workflow";
   public static final String DEMO_REQ_TEAM_WORKFLOW_ARTIFACT = "Demo Req Team Workflow";
   public static final String DEMO_TEST_TEAM_WORKFLOW_ARTIFACT = "Demo Test Team Workflow";
   public static final Collection<String> ignoreLogging =
         Arrays.asList("No image was defined for art type", "Unable to load the image for [SAVED]");

   public static boolean isProductionDb() throws OseeCoreException {
      return ClientSessionManager.isProductionDataStore();
   }

   public static boolean isTestDb() throws OseeCoreException {
      return !isProductionDb(); // && !isDemoDb();
   }

   public static boolean isDemoDb() throws OseeCoreException {
      String dbType = OseeInfo.getValue("osee.db.type");
      return DEMO_DB_TYPE.equals(dbType);
   }

   public static void sleep(long milliseconds) throws Exception {
      System.out.println("Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      System.out.println("Awake");
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

   public static void severeLoggingEnd(SevereLoggingMonitor monitorLog) throws Exception {
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
                  }
               }
               if (ignoreIt) {
                  continue;
               }
               if (status.getException() != null) {
                  StringBuilder sb = new StringBuilder();
                  exceptionToString(status.getException(), sb);
                  System.err.println("SevereLogging Exception: " + sb.toString());
               } else {
                  System.err.println("SevereLogging Exception: " + status.getMessage());
               }
            }
         }
         if (numExceptions > 0) {
            throw new OseeStateException("SevereLoggingMonitor found " + numExceptions + " exceptions!");
         }
      }
   }

   private static void exceptionToString(Throwable ex, StringBuilder sb) {
      if (ex == null) {
         sb.append("Exception == null; can't display stack");
         return;
      }
      sb.append(ex.getMessage() + "\n");
      StackTraceElement st[] = ex.getStackTrace();
      for (int i = 0; i < st.length; i++) {
         StackTraceElement ste = st[i];
         sb.append("   at " + ste.toString() + "\n");
      }
      Throwable cause = ex.getCause();
      if (cause != null) {
         sb.append("   caused by ");
         exceptionToString(cause, sb);
      }
   }

   public static void checkThatIncreased(Map<String, Integer> prevCount, Map<String, Integer> postCount) {
      for (String name : prevCount.keySet()) {
         String incStr = postCount.get(name) > prevCount.get(name) ? "Increased" : "ERROR, Not Increased";
         System.out.println(String.format(incStr + ": [%s] pre[%d] vs post[%d]", name, prevCount.get(name),
               postCount.get(name)));
      }
      for (String name : prevCount.keySet()) {
         TestCase.assertTrue(String.format("[%s] did not increase as expected: pre[%d] vs post[%d]", name,
               prevCount.get(name), postCount.get(name)), postCount.get(name) > prevCount.get(name));
      }
   }

   public static void checkThatEqual(Map<String, Integer> prevCount, Map<String, Integer> postCount) {
      for (String tableName : prevCount.keySet()) {
         String equalStr = postCount.get(tableName).equals(prevCount.get(tableName)) ? "Equal" : "ERROR, NotEqual";
         System.out.println(String.format(equalStr + ": [%s] pre[%d] post[%d]", tableName, prevCount.get(tableName),
               postCount.get(tableName)));
      }
      for (String tableName : prevCount.keySet()) {
         TestCase.assertTrue(String.format("[%s] count not equal pre[%d] post[%d]", tableName,
               prevCount.get(tableName), postCount.get(tableName)), postCount.get(tableName).equals(
               prevCount.get(tableName)));
      }
   }

}
