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
package org.eclipse.osee.ats.health;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ValidateResults {

   private final CountingMap<String> testNameToTimeSpentMap = new CountingMap<>();
   private final HashCollection<String, String> testNameToResultsMap = new HashCollection<>(50);
   private final HashCollection<String, Long> testNameToIdMap = new HashCollection<>(50);

   public void logTestTimeSpent(Date date, String testName) {
      Date now = new Date();
      int spent = new Long(now.getTime() - date.getTime()).intValue();
      testNameToTimeSpentMap.put(testName, spent);
   }

   public void log(Artifact artifact, String testName, String message) {
      if (artifact != null) {
         testNameToIdMap.put(testName, artifact.getId());
      }
      log(testName, message);
      System.err.println(testName + " - " + message);
   }

   public void log(String testName, String message) {
      testNameToResultsMap.put(testName, message);
   }

   public void addResultsMapToResultData(XResultData xResultData) {
      String[] keys = testNameToResultsMap.keySet().toArray(new String[testNameToResultsMap.keySet().size()]);
      Arrays.sort(keys);
      for (String testName : keys) {
         xResultData.log(testName);
         for (String result : testNameToResultsMap.getValues(testName)) {
            xResultData.log(result);
         }
         // uniqueize ids
         Set<Long> idStrs = new HashSet<>();
         Collection<Long> values = testNameToIdMap.getValues(testName);
         if (values != null) {
            idStrs.addAll(values);
         }
         xResultData.log(testName + "IDS: " + Collections.toString(",", idStrs) + "\n");
      }
   }

   public void addTestTimeMapToResultData(XResultData xResultData) {
      xResultData.log("\n\nTime Spent in Tests");
      long totalTime = 0;
      for (Entry<String, MutableInteger> entry : testNameToTimeSpentMap.getCounts()) {
         xResultData.log(entry.getKey() + " - " + entry.getValue() + " ms");
         totalTime += entry.getValue().getValue();
      }
      xResultData.log("TOTAL - " + totalTime + " ms");

      xResultData.log("\n");
   }

   public HashCollection<String, String> getTestNameToResultsMap() {
      return testNameToResultsMap;
   }

}
