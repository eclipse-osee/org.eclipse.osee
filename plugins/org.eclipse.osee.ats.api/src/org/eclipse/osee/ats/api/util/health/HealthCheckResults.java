/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util.health;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class HealthCheckResults {

   private final CountingMap<String> testNameToTimeSpentMap = new CountingMap<>();
   private final HashCollection<String, String> testNameToResultsMap = new HashCollection<>(50);
   private final HashCollection<String, String> testNameToIdMap = new HashCollection<>(50);

   public void logTestTimeSpent(Date date, String testName) {
      Date now = new Date();
      int spent = new Long(now.getTime() - date.getTime()).intValue();
      testNameToTimeSpentMap.put(testName, spent);
   }

   public void log(ArtifactId artifact, String testName, String message) {
      if (artifact != null) {
         testNameToIdMap.put(testName, artifact.getIdString());
      }
      log(testName, message);
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
            xResultData.log("   - " + result);
         }
         // uniqueize ids
         Set<String> idStrs = new HashSet<>();
         Collection<String> values = testNameToIdMap.getValues(testName);
         if (values != null) {
            idStrs.addAll(values);
         }
         xResultData.log(testName + "IDs: " + Collections.toString(",", idStrs) + "\n");
      }
   }

   public void addTestTimeMapToResultData(XResultData xResultData) {
      xResultData.log("\n\nTime Spent in Tests");
      long totalTime = 0;
      for (Entry<String, MutableInteger> entry : testNameToTimeSpentMap.getCounts()) {
         xResultData.log(
            "   " + entry.getKey() + " - " + (entry.getValue().getValue() / 1000) + " sec " + " - " + entry.getValue() + " ms");
         totalTime += entry.getValue().getValue();
      }
      xResultData.log("TOTAL - " + (totalTime / 1000) + " sec " + totalTime + " ms");

      xResultData.log("\n");
   }

   public HashCollection<String, String> getTestNameToResultsMap() {
      return testNameToResultsMap;
   }

}
