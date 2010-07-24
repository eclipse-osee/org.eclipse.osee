/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public class DateUtil {

   public static Calendar getCalendar(Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return calendar;
   }

   public static int getWorkingDaysBetween(Date fromDate, Date toDate) {
      return getWorkingDaysBetween(getCalendar(fromDate), getCalendar(toDate));
   }

   public static int getWorkingDaysBetween(Calendar fromDate, Calendar toDate) {
      int workingDays = 0;
      while (!fromDate.after(toDate)) {
         int day = fromDate.get(Calendar.DAY_OF_WEEK);
         if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
            workingDays++;
         }

         fromDate.add(Calendar.DATE, 1);
      }
      return workingDays;
   }

}
