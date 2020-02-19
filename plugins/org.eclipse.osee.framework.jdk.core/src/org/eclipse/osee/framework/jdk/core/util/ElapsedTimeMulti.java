/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;

/**
 * Utility to log multiple elapsed time counts.
 *
 * @author Donald G. Dunne
 */
public class ElapsedTimeMulti {

   Map<String, Long> idToTime = new HashMap<>();
   Map<String, Date> idToStartDate = new HashMap<>();
   private String name;

   /**
    * Start timer for name
    */
   public ElapsedTimeMulti(String name) {
      restart(name);
   }

   /**
    * Clear all timers and start for name
    */
   public void restart(String name) {
      idToTime.clear();
      idToStartDate.clear();
      this.name = name;
      start(name);
   }

   /**
    * Starts timer for name or resumes name if already there
    */
   public void start(String name) {
      idToStartDate.put(name, new Date());
   }

   /**
    * Stops (pauses) timer for name
    */
   public void stop(String name) {
      Long stored = idToTime.get(name);
      Long elapsed = (new Date()).getTime() - idToStartDate.get(name).getTime();
      if (stored == null) {
         stored = elapsed;
      } else {
         stored += elapsed;
      }
      idToTime.put(name, stored);
   }

   /**
    * Display report to syserr
    */
   public void report(Units units) {
      Date now = new Date();
      XConsoleLogger.err("\n=========== Start ============");
      printName(now, name, units);
      for (Entry<String, Long> entry : idToTime.entrySet()) {
         if (!entry.getKey().equals(name)) {
            printName(now, entry.getKey(), units);
         }
      }
      XConsoleLogger.err("=========== End ============\n");
   }

   private void printName(Date endDate, String name, Units units) {
      Date startDate = idToStartDate.get(name);
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
      String str = String.format("%s- elapsed %d %s%s - start %s - end %s\n", name, time, units.name(), milliseconds,
         DateUtil.getDateStr(startDate, DateUtil.HHMMSSSS), DateUtil.getDateStr(endDate, DateUtil.HHMMSSSS));
      XConsoleLogger.err(str);
   }

}
