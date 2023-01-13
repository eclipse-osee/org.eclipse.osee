/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.util.health;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class HealthCheckResults {

   private final HashCollection<String, String> testNameToResultsMap = new HashCollection<>(50);
   private final HashCollection<String, String> testNameToIdMap = new HashCollection<>(50);
   private boolean persist = false;

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
         xResultData.log("<b>" + testName + "</b>");
         for (String result : testNameToResultsMap.getValues(testName)) {
            xResultData.log("   - " + result);
         }
         // uniqueize ids
         Set<String> idStrs = new HashSet<>();
         Collection<String> values = testNameToIdMap.getValues(testName);
         if (values != null) {
            idStrs.addAll(values);
         }
         xResultData.logf("<b>" + testName + "</b> Ids: " + Collections.toString(",", idStrs) + "\n\n");
      }
   }

   public boolean isPersist() {
      return persist;
   }

   public void setPersist(boolean persist) {
      this.persist = persist;
   }

}
