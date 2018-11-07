/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * The DateIterator class is an iterable class that returns Calendar objects for every Monday date between startCal and
 * endCal. Note: All dates returned by DateIterator will have hours, minutes, seconds and milliseconds normalized to 00.
 * 
 * @author Shawn F. Cook
 */
public class DateIterator implements Iterator<Calendar>, Cloneable {
   private final Calendar startCal;
   private final Calendar endCal;
   private final int intervalField;
   private final int intervalQty;
   private final boolean normalize;
   private final int calendarFieldToNormalize;
   private final int calendarFieldValueToNormalizeOn;
   private Calendar currentCalIndex = null;

   /**
    * Use this constructor for the very simple case of iterating from startCal to endCal once each day
    */
   public DateIterator(Calendar startCal, Calendar endCal) {
      this(startCal.getTime(), endCal.getTime(), Calendar.DATE, 1);
   }

   /**
    * Use this constructor for the very simple case of iterating from startCal to endCal once each day
    */
   public DateIterator(Date startDate, Date endDate) {
      this(startDate, endDate, Calendar.DATE, 1);
   }

   /**
    * @param incrementInterval See date/time categories in Calendar class. i.e.: Calendar.WEEK_OF_YEAR
    */
   public DateIterator(Calendar startCal, Calendar endCal, int intervalField, int intervalQty) {
      this(startCal.getTime(), endCal.getTime(), intervalField, intervalQty);
   }

   public DateIterator(Date startDate, Date endDate, int intervalField, int intervalQty) {
      this(startDate, endDate, intervalField, intervalQty, false, 0, 0);
   }

   public DateIterator(Calendar startCal, Calendar endCal, int intervalField, int intervalQty, boolean normalize, int calendarFieldToNormalize, int calendarFieldValueToNormalizeOn) {
      this(startCal.getTime(), endCal.getTime(), intervalField, intervalQty, normalize, calendarFieldToNormalize,
         calendarFieldValueToNormalizeOn);
   }

   /**
    * Use this constructor to normalize on a particular Calendar field. For example: To only return Calendar dates on
    * Monday you would use this constructor like this:DateIterator(startDate, endDate, Calendar.WEEK_OF_YEAR, 1, true,
    * Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    * 
    * @param calendarFieldToNormalize If normalize==false then this will be ignored
    * @param calendarFieldValueToNormalizeOn If normalize==false then this will be ignored
    */
   public DateIterator(Date startDate, Date endDate, int intervalField, int intervalQty, boolean normalize, int calendarFieldToNormalize, int calendarFieldValueToNormalizeOn) {
      Calendar sCal = Calendar.getInstance();
      Calendar eCal = Calendar.getInstance();
      sCal.setTime(startDate);
      eCal.setTime(endDate);
      this.startCal = sCal;
      this.endCal = eCal;
      this.intervalField = intervalField;
      this.intervalQty = intervalQty;
      this.normalize = normalize;
      this.calendarFieldToNormalize = calendarFieldToNormalize;
      this.calendarFieldValueToNormalizeOn = calendarFieldValueToNormalizeOn;
      resetIterator();
   }

   /*
    * Resets the iterator index to the first Monday prior to startCal and sets the time to exactly midnight
    * (00:00:00.000)
    */
   public void resetIterator() {
      //Get the Monday (midnight) before startCal
      currentCalIndex = getFirstIteration();
   }

   @Override
   public boolean hasNext() {
      if (currentCalIndex != null) {
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Calendar next() {
      if (currentCalIndex == null) {
         return null;
      }
      Calendar retCal = (Calendar) currentCalIndex.clone();
      if (currentCalIndex.compareTo(endCal) >= 0) {
         currentCalIndex = null;
      } else {
         currentCalIndex.add(intervalField, intervalQty);
      }
      return retCal;
   }

   @Override
   public void remove() {
      //do nothing
   }

   public int getManyIterations() {
      int manyIters = 0;

      DateIterator di;
      di = (DateIterator) this.clone();
      while (di.hasNext()) {
         di.next();
         manyIters++;
      }

      return manyIters;
   }

   public int getIterationOfCalendar(Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int manyIters = 0;
      DateIterator di = (DateIterator) this.clone();
      Calendar curCal = (Calendar) startCal.clone();
      while (di.hasNext() && curCal.before(calendar)) {
         curCal = di.next();
         manyIters++;
      }

      return manyIters;
   }

   public Calendar getFirstIteration() {
      //Get the first Monday (@ midnight) before startCal
      Calendar cal = (Calendar) startCal.clone();
      if (normalize) {
         //while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
         while (cal.get(this.calendarFieldToNormalize) != this.calendarFieldValueToNormalizeOn) {
            cal.add(Calendar.DATE, -1);
         }
      }
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return cal;
   }

   public Calendar getLastIteration() {
      DateIterator di = new DateIterator(startCal, endCal, intervalField, intervalQty);
      Calendar cal = (Calendar) startCal.clone();
      while (di.hasNext()) {
         cal = di.next();
      }
      return cal;
   }

   /**
    * Given the unNormalDate return a date that will be the first iteration date at or after the unNormalDate.
    */
   public Date getNormalizedDate(Date unNormalDate) {
      Date normalDate;

      DateIterator di = (DateIterator) this.clone();
      normalDate = di.next().getTime();
      while (di.hasNext() && normalDate.before(unNormalDate)) {
         normalDate = di.next().getTime();
      }

      return normalDate;
   }

   public List<Date> getAllDates() {
      List<Date> allDates = new ArrayList<>();
      DateIterator di = (DateIterator) this.clone();
      while (di.hasNext()) {
         Calendar calendar = di.next();
         allDates.add(calendar.getTime());
      }

      return allDates;
   }

   @Override
   protected Object clone() {
      DateIterator cloneDI = new DateIterator((Calendar) startCal.clone(), (Calendar) endCal.clone(), intervalField,
         intervalQty, normalize, calendarFieldToNormalize, calendarFieldValueToNormalizeOn);
      return cloneDI;
   }
}
