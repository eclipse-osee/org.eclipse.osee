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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;

/**
 * @author Roberto E. Escobar
 */
public class HighLevelSummary implements ITestRunReport {
   private static final String[] HEADER = new String[] {
      CoreAttributeTypes.Partition.getName(),
      CoreAttributeTypes.Subsystem.getName(),
      "ScriptCount",
      "RunTime Issues",
      "Scripts with Failures",
      "Scripts Pass"};
   private final Map<String, CollectedData> dataMap;

   public HighLevelSummary() {
      this.dataMap = new HashMap<>();
   }

   @Override
   public void gatherData(IProgressMonitor monitor, TestRunOperator... items) throws Exception {
      clear();
      for (int index = 0; index < items.length; index++) {
         TestRunOperator operator = items[index];
         monitor.subTask(String.format("Processing [%s of%s]", index + 1, items.length));
         String partition = operator.getPartition();
         String subsystem = operator.getSubsystem();
         String key = String.format("%s:%s", partition, subsystem);
         CollectedData data = dataMap.get(key);
         if (data == null) {
            data = new CollectedData(partition, subsystem);
            dataMap.put(key, data);
         }
         try {
            processData(data, operator);
         } catch (Exception ex) {
            OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex);
         }
         if (monitor.isCanceled()) {
            break;
         }
      }
   }

   @Override
   public String[][] getBody() {
      int numRows = dataMap.size();
      List<String> keys = new ArrayList<>(dataMap.keySet());
      String[][] toReturn = new String[numRows][getHeader().length];
      for (int row = 0; row < numRows; row++) {
         String key = keys.get(row);
         CollectedData collectedData = dataMap.get(key);
         toReturn[row] = addRow(collectedData);
      }
      return toReturn;
   }

   private String[] addRow(CollectedData data) {
      List<String> values = new ArrayList<>();
      values.add(data.getPartition());
      values.add(data.getSubsytem());
      values.add(Integer.toString(data.getScriptCount()));
      values.add(Integer.toString(data.getRunTimeIssues()));
      values.add(Integer.toString(data.getScriptWithFailures()));
      values.add(Integer.toString(data.getScriptPassed()));
      return values.toArray(new String[values.size()]);
   }

   @Override
   public String[] getHeader() {
      return HEADER;
   }

   @Override
   public String getTitle() {
      return "High Level Test Run Summary";
   }

   private void processData(CollectedData data, TestRunOperator operator)  {
      data.incrementScriptCount();

      int failed = operator.getTestPointsFailed();
      int passed = operator.getTestPointsPassed();
      int total = operator.getTotalTestPoints();
      boolean wasAborted = operator.wasAborted();

      if (wasAborted) {
         data.incrementRunTimeIssues();
      } else if (total == 0) {
         data.incrementRunTimeIssues();
      } else if (failed > 0) {
         data.incrementScriptWithFailures();
      } else if (passed == total && failed == 0) {
         data.incrementScriptPassed();
      }
   }

   private final class CollectedData {
      private final String partition;
      private final String subsytem;
      private int scriptCount;
      private int runTimeIssues;
      private int scriptWithFailures;
      private int scriptPassed;

      public CollectedData(String partition, String subsytem) {
         this.partition = partition;
         this.subsytem = subsytem;
         this.scriptCount = 0;
         this.runTimeIssues = 0;
         this.scriptWithFailures = 0;
         this.scriptPassed = 0;
      }

      public String getPartition() {
         return partition;
      }

      public String getSubsytem() {
         return subsytem;
      }

      public int getScriptCount() {
         return scriptCount;
      }

      public int getRunTimeIssues() {
         return runTimeIssues;
      }

      public int getScriptWithFailures() {
         return scriptWithFailures;
      }

      public int getScriptPassed() {
         return scriptPassed;
      }

      public void incrementScriptCount() {
         this.scriptCount++;
      }

      public void incrementRunTimeIssues() {
         this.runTimeIssues++;
      }

      public void incrementScriptWithFailures() {
         this.scriptWithFailures++;
      }

      public void incrementScriptPassed() {
         this.scriptPassed++;
      }
   }

   @Override
   public String getDescription() {
      return "Generates a summary report of all test runs.";
   }

   @Override
   public void clear() {
      this.dataMap.clear();
   }

}
