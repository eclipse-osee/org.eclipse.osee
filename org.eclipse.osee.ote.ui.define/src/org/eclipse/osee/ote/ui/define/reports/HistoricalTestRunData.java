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
package org.eclipse.osee.ote.ui.define.reports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;

/**
 * @author Roberto E. Escobar
 */
public class HistoricalTestRunData implements ITestRunReport {
   private static final String DATE_HEADER = "Date";
   private static final String[] PER_SCRIPT_HEADER = new String[] {"Passed", "Failed", "Total", "Status"};

   private static final DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

   private HashMap<Date, Map<String, TestRunOperator>> runByCollection;
   private Set<String> runsByName;

   public HistoricalTestRunData() {
      this.runByCollection = new HashMap<Date, Map<String, TestRunOperator>>();
      this.runsByName = new TreeSet<String>();
   }

   public void gatherData(IProgressMonitor monitor, TestRunOperator... artifacts) throws Exception {
      clear();
      for (TestRunOperator operator : artifacts) {
         try {
            Date date = operator.getEndDate();
            Map<String, TestRunOperator> theMap = runByCollection.get(date);
            if (theMap == null) {
               theMap = new HashMap<String, TestRunOperator>();
               runByCollection.put(date, theMap);
            }
            String name = operator.getScriptSimpleName();
            theMap.put(name, operator);
            runsByName.add(name);
         } catch (Exception ex) {
            OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
         }
      }
   }

   public String[][] getBody() {
      String[][] toReturn = new String[runByCollection.size() + 1][getHeader().length];
      int index = 0;
      toReturn[index++] = getTitleRow();
      List<Date> dateList = new ArrayList<Date>(runByCollection.keySet());
      Collections.sort(dateList);
      for (Date date : dateList) {
         Map<String, TestRunOperator> theMap = runByCollection.get(date);
         toReturn[index++] = getRow(date, theMap);
      }
      return toReturn;
   }

   private String[] getRow(Date date, Map<String, TestRunOperator> theMap) {
      List<String> row = new ArrayList<String>();
      row.add(formatter.format(date));
      for (String name : runsByName) {
         TestRunOperator operator = theMap.get(name);
         for (String entry : getData(operator)) {
            row.add(entry);
         }
      }
      return row.toArray(new String[row.size()]);
   }

   private String[] getData(TestRunOperator operator) {
      String[] data = new String[PER_SCRIPT_HEADER.length];
      Arrays.fill(data, ONE_SPACE_STRING);
      try {
         if (operator != null) {
            data[0] = Integer.toString(operator.getTestPointsPassed());
            data[1] = Integer.toString(operator.getTestPointsFailed());
            data[2] = Integer.toString(operator.getTotalTestPoints());
            data[3] = operator.getTestResultStatus();
         }
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
      }
      return data;
   }

   private String[] getTitleRow() {
      List<String> header = new ArrayList<String>();
      header.add(ONE_SPACE_STRING);
      for (int index = 0; index < runsByName.size(); index++) {
         header.addAll(Arrays.asList(PER_SCRIPT_HEADER));
      }
      return header.toArray(new String[header.size()]);
   }

   public String getDescription() {
      return "Creates a table of run results by date.";
   }

   public String[] getHeader() {
      List<String> header = new ArrayList<String>();
      header.add(DATE_HEADER);
      for (String name : runsByName) {
         header.add(name);
         header.add(ONE_SPACE_STRING);
         header.add(ONE_SPACE_STRING);
         header.add(ONE_SPACE_STRING);
      }
      return header.toArray(new String[header.size()]);
   }

   public String getTitle() {
      return "Test Run Historical Trend";
   }

   @Override
   public void clear() {
      this.runByCollection.clear();
      this.runsByName.clear();
   }

}
