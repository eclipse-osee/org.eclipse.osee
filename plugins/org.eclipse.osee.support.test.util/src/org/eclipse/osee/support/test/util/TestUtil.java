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
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;

/**
 * @author Donald G. Dunne
 */
public class TestUtil {
   private static final String DEMO_DB_TYPE = "demo";
   public static final Collection<String> ignoreLogging =
      Arrays.asList("No image was defined for art type", "Unable to load the image for [SAVED]");

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static void checkDbInitSuccess() {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
   }

   public static boolean isDbInitSuccessful() {
      return OseeInfo.getValue("DbInitSuccess").equals("true");
   }

   public static void setDbInitSuccessful(boolean success) {
      OseeInfo.setValue("DbInitSuccess", String.valueOf(success));
   }

   /**
    * Need to match methods in OseeProperties
    */
   public static void setIsInTest(boolean isInTest) {
      System.setProperty("osee.isInTest", String.valueOf(isInTest));
   }

   public static boolean isProductionDb() {
      return ClientSessionManager.isProductionDataStore();
   }

   public static boolean isTestDb() {
      return !isProductionDb(); // && !isDemoDb();
   }

   public static boolean isDemoDb() {
      return DEMO_DB_TYPE.equals(OseeInfo.getValue(OseeInfo.DB_TYPE_KEY));
   }

   public static void setDemoDb(boolean set) {
      OseeInfo.setValue(OseeInfo.DB_TYPE_KEY, set ? DEMO_DB_TYPE : "");
   }

   public static SevereLoggingMonitor severeLoggingStart() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      return monitorLog;
   }

   public static void severeLoggingEnd(SevereLoggingMonitor monitorLog) throws Exception {
      severeLoggingEnd(monitorLog, ignoreLogging);
   }

   public static void severeLoggingEnd(SevereLoggingMonitor monitorLog, Collection<String> ignoreLogging) throws Exception {
      OseeLog.unregisterLoggerListener(monitorLog);
      Collection<IHealthStatus> healthStatuses = monitorLog.getAllLogs();
      int numExceptions = 0;
      StringBuilder builder = new StringBuilder();
      if (!healthStatuses.isEmpty()) {
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
                  builder.append("\nSevereLoggingException [");
                  builder.append(numExceptions);
                  builder.append("]: ");
                  if (status.getException() != null) {
                     builder.append(Lib.exceptionToString(status.getException()));
                  } else {
                     builder.append(status.getMessage());
                  }
                  numExceptions++;
               }
            }
         }
         if (numExceptions > 0) {
            throw new OseeStateException("SevereLoggingMonitor found [%d] exceptions - [%s]", numExceptions,
               builder.toString());
         }
      }
   }

}
