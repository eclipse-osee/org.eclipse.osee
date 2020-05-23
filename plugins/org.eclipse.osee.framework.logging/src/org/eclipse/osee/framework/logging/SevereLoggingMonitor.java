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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * @author Roberto E. Escobar
 */
public class SevereLoggingMonitor implements ILoggerListener {

   private boolean ignore;
   private final List<IHealthStatus> status = new CopyOnWriteArrayList<>();

   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
      if (!ignore) {
         status.add(new BaseStatus(loggerName, level, message, th));
      }
   }

   public List<IHealthStatus> getAllLogs() {
      return status;
   }

   public void pause() {
      this.ignore = true;
   }

   public void resume() {
      this.ignore = false;
   }

   public List<IHealthStatus> getSevereLogs() {
      List<IHealthStatus> severeStatus = new ArrayList<>(status.size());

      //Remove all none severe levels
      for (IHealthStatus healthStatus : status) {
         if (healthStatus.getLevel() == Level.SEVERE) {
            severeStatus.add(healthStatus);
         }
      }
      return severeStatus;
   }

   public List<IHealthStatus> getLogsAtLevel(Level level) {
      List<IHealthStatus> warningStatus = new ArrayList<>(status.size());

      //Remove all none severe levels
      for (IHealthStatus healthStatus : status) {
         if (healthStatus.getLevel() == level) {
            warningStatus.add(healthStatus);
         }
      }
      return warningStatus;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(getSevereLogs().size());
      sb.append(" Severe logs captured.\n");
      for (IHealthStatus health : getSevereLogs()) {
         sb.append(health.getException() != null ? health.getException().getMessage() : health.getMessage());
         sb.append("\n");
      }
      return sb.toString();
   }
}
