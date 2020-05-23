/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.client.test.framework.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class AssertLib {

   private static final Collection<String> DEFAULT_IGNORE_LOGS =
      Arrays.asList("No image was defined for art type", "Unable to load the image for [SAVED]");

   private AssertLib() {
      // Utility class
   }

   public static void assertLogEmpty(SevereLoggingMonitor monitorLog, String... ignoreLogs) {
      Collection<String> toIgnore = null;
      if (ignoreLogs != null) {
         toIgnore = Arrays.asList(ignoreLogs);
      }
      assertLogEmpty(monitorLog, toIgnore);
   }

   public static void assertLogEmpty(SevereLoggingMonitor monitorLog, Collection<String> ignoreLogs) {
      if (ignoreLogs == null || ignoreLogs.isEmpty()) {
         assertLogEmptyHelper(monitorLog, DEFAULT_IGNORE_LOGS);
      } else {
         assertLogEmptyHelper(monitorLog, mergeIgnoreLogs(ignoreLogs));
      }
   }

   private static void assertLogEmptyHelper(SevereLoggingMonitor monitorLog, Collection<String> ignoreLogs) {
      if (monitorLog != null) {
         Collection<IHealthStatus> healthStatuses = monitorLog.getAllLogs();
         int numExceptions = 0;
         StringBuilder builder = new StringBuilder();
         if (!healthStatuses.isEmpty()) {
            for (IHealthStatus status : healthStatuses) {
               if (Level.INFO != status.getLevel() && isValid(status, ignoreLogs)) {
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
            if (numExceptions > 0) {
               String message =
                  String.format("SevereLoggingMonitor found [%d] exceptions - [%s]", numExceptions, builder.toString());
               Assert.fail(message);
            }
         }
      }
   }

   private static boolean isValid(IHealthStatus status, Collection<String> ignoreLogs) {
      boolean result = true;
      String toCheck = status.getMessage();
      for (String str : ignoreLogs) {
         result = !toCheck.startsWith(str);
         if (!result) {
            break;
         }
      }
      return result;
   }

   private static Collection<String> mergeIgnoreLogs(Collection<String> ignoreLogs) {
      Collection<String> logsToIgnore = new HashSet<>();
      logsToIgnore.addAll(DEFAULT_IGNORE_LOGS);
      for (String toIgnore : ignoreLogs) {
         logsToIgnore.add(toIgnore);
      }
      return logsToIgnore;
   }
}
