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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SevereLoggingMonitor implements ILoggerListener {

   private List<IHealthStatus> status = new ArrayList<IHealthStatus>();

   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
      status.add(new BaseStatus(loggerName, level, message, th));
   }

   public List<IHealthStatus> getAllLogs() {
      return status;
   }

   public List<IHealthStatus> getSevereLogs() {
      List<IHealthStatus> severeStatus = new ArrayList<IHealthStatus>(status.size());

      //Remove all none severe levels
      for (IHealthStatus healthStatus : status) {
         if (healthStatus.getLevel() == Level.SEVERE) {
            severeStatus.add(healthStatus);
         }
      }
      return severeStatus;
   }

   public List<IHealthStatus> getLogsAtLevel(Level level) {
      List<IHealthStatus> warningStatus = new ArrayList<IHealthStatus>(status.size());

      //Remove all none severe levels
      for (IHealthStatus healthStatus : status) {
         if (healthStatus.getLevel() == level) {
            warningStatus.add(healthStatus);
         }
      }
      return warningStatus;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(status.size());
      sb.append(" Severe logs captured.\n");
      for (IHealthStatus health : status) {
         sb.append(health.getException().getMessage());
         sb.append("\n");
      }
      return sb.toString();
   }

}
