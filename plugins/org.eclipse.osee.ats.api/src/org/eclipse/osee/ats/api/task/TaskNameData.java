/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.api.task;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.workflow.IAtsTask;

/**
 * @author Donald G. Dunne
 */
public class TaskNameData {

   private String codeTest = "";
   private String partition = "";
   private String reqName = "";
   private String wcafeType = "";
   private String addDetails = "";
   private boolean isWcafe = false;
   private boolean deleted = false;
   private static final Map<IAtsTask, TaskNameData> taskNameDataCache = new HashMap<>();

   private static final Pattern requirementPattern = Pattern.compile("^(.*?) +\"(.*?)\" +for +\"(.*?)\"(.*)");
   private static final Pattern wcafeRequirementPattern_newFormat =
      Pattern.compile("^(.*?) +\"(.*?)\" +for (Warning|Caution|Advisory|Fault|Exceedance) +\"(.*?)\"");

   public TaskNameData(IAtsTask task) {
      boolean didMatchWcafeReqPattern = false;
      Matcher m = wcafeRequirementPattern_newFormat.matcher(task.getName());
      if (m.find()) {
         codeTest = m.group(1);
         partition = m.group(2);
         wcafeType = m.group(3);
         reqName = m.group(4);
         didMatchWcafeReqPattern = true;
         isWcafe = true;
      }
      m = requirementPattern.matcher(task.getName());
      if (m.find() && !didMatchWcafeReqPattern) {
         codeTest = m.group(1);
         partition = m.group(2);
         reqName = m.group(3);
         setAddDetails(m.group(4));
      }
   }

   public static TaskNameData get(IAtsTask task) {
      if (!taskNameDataCache.containsKey(task)) {
         TaskNameData tnd = new TaskNameData(task);
         taskNameDataCache.put(task, tnd);
      }
      return taskNameDataCache.get(task);
   }

   @Override
   public String toString() {
      return "Type: " + getCodeTest() + "\nPartition: " + getPartition() + "\nReq: " + getReqName();
   }

   public boolean isRequirement() {
      return !reqName.equals("");
   }

   public boolean isWcafe() {
      return isWcafe;
   }

   public String getWcafeType() {
      return wcafeType;
   }

   public String getCodeTest() {
      return codeTest;
   }

   public void setCodeTest(String codeTest) {
      this.codeTest = codeTest;
   }

   public String getPartition() {
      return partition;
   }

   public void setPartition(String partition) {
      this.partition = partition;
   }

   public String getReqName() {
      return reqName;
   }

   public void setReqName(String reqName) {
      this.reqName = reqName;
   }

   public boolean isDeleted() {
      return deleted;
   }

   public void setDeleted(boolean deleted) {
      this.deleted = deleted;
   }

   public String getAddDetails() {
      return addDetails;
   }

   public void setAddDetails(String addDetails) {
      this.addDetails = addDetails;
   }
}
