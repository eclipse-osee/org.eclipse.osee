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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Donald G. Dunne
 */
public class DateUtil {

   public static final long MILLISECONDS_IN_A_WEEK = 604800000;
   public static final long MILLISECONDS_IN_A_DAY = 86400000;
   public final static String MMDDYY = "MM/dd/yyyy";
   public final static String YYYYMMDD = "yyyy/MM/dd";
   public final static String YYYY_MM_DD = "yyyy_MM_dd";
   public final static String MMDDYYHHMM = "MM/dd/yyyy hh:mm a";
   public final static String HHMMSS = "hh:mm:ss";
   public final static String HHMMSSSS = "hh:mm:ss:SS";
   public final static String HHMM = "hh:mm";
   public static final HashMap<String, DateFormat> dateFormats = new HashMap<String, DateFormat>();

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

   public static String getHHMM(Date date) {
      return get(date, HHMM);
   }

   public static String getHHMMSS(Date date) {
      return get(date, HHMMSS);
   }

   public static String getYYYYMMDD() {
      return getYYYYMMDD(new Date());
   }

   public static String getYYYYMMDD(Date date) {
      return get(date, YYYYMMDD);
   }

   public static String getMMDDYY(Date date) {
      return get(date, MMDDYY);
   }

   public static String getMMDDYYHHMM() {
      return getMMDDYYHHMM(new Date());
   }

   public static String getMMDDYYHHMM(Date date) {
      return get(date, MMDDYYHHMM);
   }

   public static String getDateNow() {
      return getDateNow(new Date());
   }

   public static String getDateNow(Date date) {
      return getDateNow(date, MMDDYY);
   }

   public static String getTimeStamp() {
      return getDateNow(new Date(), HHMMSSSS);
   }

   public static String getDateStr(Date date, String format) {
      if (date == null) {
         return "";
      }
      DateFormat dateFormat = dateFormats.get(format);
      if (dateFormat == null) {
         dateFormat = new SimpleDateFormat(format);
         dateFormats.put(format, dateFormat);
      }
      return dateFormat.format(date);
   }

   public static String getDateNow(String format) {
      return get(new Date(), format);
   }

   public static String getDateNow(Date date, String format) {
      return get(date, format);
   }

   public static String get(Date date) {
      if (date == null) {
         return "";
      }
      return DateFormat.getDateInstance().format(date);
   }

   public static String get(Date date, String pattern) {
      return get(date, new SimpleDateFormat(pattern));
   }

   public static String get(Date date, DateFormat dateFormat) {
      if (date == null) {
         return "";
      }
      String result = dateFormat.format(date);
      return result;
   }

   public static int getDifference(Date a, Date b) {
      int tempDifference = 0;
      int difference = 0;
      Calendar earlier = Calendar.getInstance();
      Calendar later = Calendar.getInstance();

      if (a.compareTo(b) < 0) {
         earlier.setTime(a);
         later.setTime(b);
      } else {
         earlier.setTime(b);
         later.setTime(a);
      }

      while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
         tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
         difference += tempDifference;

         earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
      }

      if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR)) {
         tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
         difference += tempDifference;

         earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
      }

      return difference;
   }

}
