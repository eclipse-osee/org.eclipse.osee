/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Date;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;

/**
 * @author Donald G. Dunne
 */
public class ElapsedTime {

   Date startDate;
   Date endDate;
   private String name;
   private final boolean logStart;
   private boolean on = true;

   public ElapsedTime(String name) {
      this(name, true, true);
   }

   public ElapsedTime(String name, boolean on) {
      this(name, true, on);
   }

   public ElapsedTime(String name, boolean logStart, boolean on) {
      this.logStart = logStart;
      this.on = on;
      start(name);
   }

   public void start(String name) {
      this.name = name;
      startDate = new Date();
      if (isOn() && logStart) {
         XConsoleLogger.err(name + " - start " + DateUtil.getTimeStamp() + "\n");
      }
   }

   public void logPoint(String pointName) {
      if (isOn()) {
         XConsoleLogger.err(name + " - [" + pointName + "] " + DateUtil.getTimeStamp() + "\n");
      }
   }

   public static enum Units {
      SEC,
      MSEC,
      MIN
   }

   public String end() {
      return end(Units.MIN);
   }

   public String end(Units units) {
      return end(units, isOn());
   }

   public String end(Units units, boolean printToSysErr) {
      if (!isOn()) {
         return "";
      }
      endDate = new Date();
      long timeSpent = endDate.getTime() - startDate.getTime();
      long time = timeSpent; // milliseconds
      String milliseconds = "";
      if (units == Units.SEC) {
         time = time / 1000; // convert from milliseconds to seconds
         milliseconds = "";
      } else if (units == Units.MIN) {
         time = time / 60000; // convert from milliseconds to minutes
         milliseconds = " ( " + timeSpent + " ms ) ";
      }
      String str = String.format("%s - elapsed %d %s%s - start %s - end %s\n", name, time, units.name(), milliseconds,
         DateUtil.getDateStr(startDate, DateUtil.HHMMSSSS), DateUtil.getDateStr(endDate, DateUtil.HHMMSSSS));
      if (printToSysErr) {
         XConsoleLogger.err(str);
      }
      return str;
   }

   /**
    * Milliseconds spent so far. Does not call end().
    */
   public Long getTimeSpent() {
      Date endDate = new Date();
      return endDate.getTime() - startDate.getTime();
   }

   public void off() {
      setOn(false);
   }

   public boolean isOn() {
      return on;
   }

   public void setOn(boolean on) {
      this.on = on;
   }
}
