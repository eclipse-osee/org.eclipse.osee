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

package org.eclipse.osee.framework.jdk.core.util;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link DateIterator}
 * 
 * @author Shawn F. Cook
 */
@RunWith(Parameterized.class)
public class DateIteratorTest {
   private final Calendar startCal;
   private final Calendar endCal;
   private final int calendarFieldIter; //Which field to iterate on
   private final int iterRate; //How much to iterate by
   private final boolean normalize; //Should DateIterator normalize to a Calendar field?
   private final int calendarFieldNormalize; //If normalize==false then this will be ignored
   private final int calendarValueNormalize; //If normalize==false then this will be ignored

   //   private final List<Calendar> calsExpected = new ArrayList<>();

   public DateIteratorTest(Calendar startCal, Calendar endCal, int calendarFieldIter, int iterRate, boolean normalize, int calendarFieldNormalize, int calendarValueNormalize) {
      this.startCal = startCal;
      this.endCal = endCal;
      this.calendarFieldIter = calendarFieldIter;
      this.iterRate = iterRate;
      this.normalize = normalize;
      this.calendarFieldNormalize = calendarFieldNormalize;
      this.calendarValueNormalize = calendarValueNormalize;
   }

   @Test
   public void testDefaultDateIterationOverCenturyYearChange() {
      //Collection object to retain the iterated dates from the DateIterator
      List<Calendar> calsFromIter = new ArrayList<>();
      List<Calendar> calsExpected = new ArrayList<>();

      //Generate expected output
      Calendar iCal = (Calendar) startCal.clone();
      //normalize, if needed
      if (normalize) {
         while (iCal.get(calendarFieldNormalize) != calendarValueNormalize) {
            iCal.add(Calendar.DATE, -1);
         }
      }
      while (iCal.before(endCal)) {
         Calendar tempCal = (Calendar) iCal.clone();
         calsExpected.add(tempCal);
         iCal.add(calendarFieldIter, iterRate);
      }
      calsExpected.add((Calendar) iCal.clone());

      //Perform the iteration
      DateIterator dateIter = new DateIterator(startCal, endCal, calendarFieldIter, iterRate, normalize,
         calendarFieldNormalize, calendarValueNormalize);
      while (dateIter.hasNext()) {
         Calendar cal = dateIter.next();
         calsFromIter.add(cal);
      }

      //***  DEBUGGING OUTPUT ***
      //      String start = DateUtil.get(startCal.getTime());
      //      String end = DateUtil.get(endCal.getTime());
      //      System.out.println("start:" + start + " end:" + end);
      //      for (Calendar cal : calsExpected) {
      //         String calStr = DateUtil.get(cal.getTime(), "MM/dd/yyyy hh:mm:ss:SS a");
      //         //         System.out.println("Expected: " + calStr);
      //      }
      //      for (Calendar cal : calsFromIter) {
      //         String calStr = DateUtil.get(cal.getTime(), "MM/dd/yyyy hh:mm:ss:SS a");
      //         //         System.out.println("Actual: " + calStr);
      //      }

      //Asserts
      for (Calendar cal : calsExpected) {
         assertTrue("calsFromIter missing date: " + DateUtil.getMMDDYYHHMM(cal.getTime()), calsFromIter.contains(cal));
      }

      for (Calendar cal : calsFromIter) {
         assertTrue("calsFromIter has extra date: " + DateUtil.getMMDDYYHHMM(cal.getTime()),
            calsExpected.contains(cal));
      }
   }

   @Parameters
   public static Collection<Object[]> dateIterationTestData() {
      Collection<Object[]> data = new ArrayList<>();

      Calendar startCal = Calendar.getInstance();
      Calendar endCal = Calendar.getInstance();

      //****  #1 *****
      //Every day between Jan 4,2012 and Mar 25, 2012
      startCal.set(2012, 0, 4);//Jan 4th, 2012 = Wednesday
      startCal.set(Calendar.HOUR_OF_DAY, 0);
      startCal.set(Calendar.MINUTE, 0);
      startCal.set(Calendar.SECOND, 0);
      startCal.set(Calendar.MILLISECOND, 0);
      endCal.set(2012, 2, 25);//Mar 25, 2012 = Sunday
      endCal.set(Calendar.HOUR_OF_DAY, 0);
      endCal.set(Calendar.MINUTE, 0);
      endCal.set(Calendar.SECOND, 0);
      endCal.set(Calendar.MILLISECOND, 0);
      data.add(new Object[] {startCal.clone(), endCal.clone(), Calendar.DATE, 1, false, 0, 0});

      //****  #2 *****
      //Every MONDAY between Jan 2,2012 and Mar 25, 2012 - NOte: Same date range as above, but normalized to mondays
      data.add(new Object[] {
         startCal.clone(),
         endCal.clone(),
         Calendar.WEEK_OF_YEAR,
         1,
         true,
         Calendar.DAY_OF_WEEK,
         Calendar.MONDAY});

      //****  #3 *****
      //Test date iteration that spans year change
      startCal.set(2011, 11, 26);//Dec 26, 2011
      startCal.set(Calendar.HOUR_OF_DAY, 0);
      startCal.set(Calendar.MINUTE, 0);
      startCal.set(Calendar.SECOND, 0);
      startCal.set(Calendar.MILLISECOND, 0);
      endCal.set(2012, 0, 5);//Jan 5th, 2012
      endCal.set(Calendar.HOUR_OF_DAY, 0);
      endCal.set(Calendar.MINUTE, 0);
      endCal.set(Calendar.SECOND, 0);
      endCal.set(Calendar.MILLISECOND, 0);
      data.add(new Object[] {startCal.clone(), endCal.clone(), Calendar.DATE, 1, false, 0, 0});

      //****  #4 *****
      //Same date range as above, but normalized on Mondays
      data.add(new Object[] {
         startCal.clone(),
         endCal.clone(),
         Calendar.WEEK_OF_YEAR,
         1,
         true,
         Calendar.DAY_OF_WEEK,
         Calendar.MONDAY});

      return data;
   }

   //   @Test
   //   public void testDefaultDateIteration() {
   //
   //      //Verify all dates are accounted for
   //      Calendar startCal = Calendar.getInstance();
   //      startCal.set(2012, 0, 4);//Jan 4th, 2012 = Wednesday
   //      startCal.set(Calendar.HOUR_OF_DAY, 0);
   //      startCal.set(Calendar.MINUTE, 0);
   //      startCal.set(Calendar.SECOND, 0);
   //      startCal.set(Calendar.MILLISECOND, 0);
   //
   //      Calendar endCal = Calendar.getInstance();
   //      endCal.set(2012, 2, 25);//Mar 25, 2012 = Sunday
   //      endCal.set(Calendar.HOUR_OF_DAY, 0);
   //      endCal.set(Calendar.MINUTE, 0);
   //      endCal.set(Calendar.SECOND, 0);
   //      endCal.set(Calendar.MILLISECOND, 0);
   //
   //      String start = DateUtil.get(startCal.getTime());
   //      String end = DateUtil.get(endCal.getTime());
   //
   //      List<Calendar> calsFromIter = new ArrayList<>();
   //      List<Calendar> calsExpected = new ArrayList<>();
   //
   //      //Populate calsExpected -
   //      // Every day between startCal and endCal
   //      Calendar iCal = (Calendar) startCal.clone();
   //      while (iCal.before(endCal)) {
   //         Calendar tempCal = (Calendar) iCal.clone();
   //         calsExpected.add(tempCal);
   //         iCal.add(Calendar.DATE, 1);
   //      }
   //      calsExpected.add(iCal);//Add last date just after endCal
   //
   //      DateIterator dateIter = new DateIterator(startCal, endCal);
   //      while (dateIter.hasNext()) {
   //         Calendar cal = dateIter.next();
   //         calsFromIter.add(cal);
   //      }
   //
   //      System.out.println("start:" + start + " end:" + end);
   //      for (Calendar cal : calsExpected) {
   //         String calStr = DateUtil.get(cal.getTime(), "MM/dd/yyyy hh:mm:ss:SS a");
   //         System.out.println("Expected: " + calStr);
   //      }
   //
   //      for (Calendar cal : calsFromIter) {
   //         String calStr = DateUtil.get(cal.getTime(), "MM/dd/yyyy hh:mm:ss:SS a");
   //         System.out.println("Actual: " + calStr);
   //      }
   //
   //      for (Calendar cal : calsExpected) {
   //         assertTrue("calsFromIter missing date: " + DateUtil.getMMDDYYHHMM(cal.getTime()), calsFromIter.contains(cal));
   //      }
   //
   //      for (Calendar cal : calsFromIter) {
   //         assertTrue("calsFromIter has extra date: " + DateUtil.getMMDDYYHHMM(cal.getTime()), calsExpected.contains(cal));
   //      }
   //   }
   //
   //   @Test
   //   public void testMondayDateIteration() {
   //
   //      //Verify all dates are accounted for
   //      Calendar startCal = Calendar.getInstance();
   //      startCal.set(2012, 0, 4);//Jan 4th, 2012 = Wednesday
   //      startCal.set(Calendar.HOUR_OF_DAY, 0);
   //      startCal.set(Calendar.MINUTE, 0);
   //      startCal.set(Calendar.SECOND, 0);
   //      startCal.set(Calendar.MILLISECOND, 0);
   //
   //      Calendar endCal = Calendar.getInstance();
   //      endCal.set(2012, 2, 25);//Mar 25, 2012 = Sunday
   //      endCal.set(Calendar.HOUR_OF_DAY, 0);
   //      endCal.set(Calendar.MINUTE, 0);
   //      endCal.set(Calendar.SECOND, 0);
   //      endCal.set(Calendar.MILLISECOND, 0);
   //
   //      String start = DateUtil.get(startCal.getTime());
   //      String end = DateUtil.get(endCal.getTime());
   //
   //      List<Calendar> calsFromIter = new ArrayList<>();
   //      List<Calendar> calsExpected = new ArrayList<>();
   //
   //      //Populate calsExpected -
   //      // All Mondays between startCal and endCal.  Including first Monday prior to startCal and first Monday after endCal
   //      Calendar iCal = Calendar.getInstance();
   //      iCal.set(2012, 0, 2);//Jan 2nd, 2012 = Monday
   //      iCal.set(Calendar.HOUR_OF_DAY, 0);
   //      iCal.set(Calendar.MINUTE, 0);
   //      iCal.set(Calendar.SECOND, 0);
   //      iCal.set(Calendar.MILLISECOND, 0);
   //      while (iCal.before(endCal)) {
   //         Calendar tempCal = (Calendar) iCal.clone();
   //         calsExpected.add(tempCal);
   //         iCal.add(Calendar.WEEK_OF_YEAR, 1);
   //      }
   //      calsExpected.add(iCal);//Add last date just after endCal
   //
   //      DateIterator dateIter =
   //         new DateIterator(startCal, endCal, Calendar.WEEK_OF_YEAR, 1, true, Calendar.DAY_OF_WEEK, Calendar.MONDAY);
   //      while (dateIter.hasNext()) {
   //         Calendar cal = dateIter.next();
   //         calsFromIter.add(cal);
   //      }
   //
   //      System.out.println("start:" + start + " end:" + end);
   //      for (Calendar cal : calsExpected) {
   //         String calStr = DateUtil.get(cal.getTime(), "MM/dd/yyyy hh:mm:ss:SS a");
   //         System.out.println("Expected: " + calStr);
   //      }
   //
   //      for (Calendar cal : calsFromIter) {
   //         String calStr = DateUtil.get(cal.getTime(), "MM/dd/yyyy hh:mm:ss:SS a");
   //         System.out.println("Actual: " + calStr);
   //      }
   //
   //      for (Calendar cal : calsExpected) {
   //         assertTrue("calsFromIter missing date: " + DateUtil.getMMDDYYHHMM(cal.getTime()), calsFromIter.contains(cal));
   //      }
   //
   //      for (Calendar cal : calsFromIter) {
   //         assertTrue("calsFromIter has extra date: " + DateUtil.getMMDDYYHHMM(cal.getTime()), calsExpected.contains(cal));
   //      }
   //   }
}
