/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.rest.internal.workitem.sync.jira;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.jira.JiraByEpicData;
import org.eclipse.osee.ats.core.column.AssigneeColumn;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class JiraReportEpicDiffsOperation {

   private final AtsApi atsApi;
   private final XResultData results;
   private final JiraByEpicData data;
   private final List<ColumnName> colNames = new ArrayList<ColumnName>();
   private ColumnName[] colsOrdered;
   private final List<String> atsIds = new ArrayList<>();

   public JiraReportEpicDiffsOperation(JiraByEpicData data, AtsApi atsApi) {
      this.data = data;
      this.atsApi = atsApi;
      if (data.getResults() == null) {
         data.setResults(new XResultData());
      }
      this.results = data.getResults();
   }

   public XResultData run() {

      ElapsedTime allTime = new ElapsedTime(getClass().getSimpleName(), true);

      results.log("Sync JIRA by Epic\n");
      Map<String, JiraTask> jTaskMap = new HashMap<>();
      boolean firstLine = true;
      for (String line : data.getTabDelimReport().split("\r\n")) {
         if (firstLine) {
            for (String key : line.split("\t")) {
               for (ColumnName colName : ColumnName.values()) {
                  if (key.equals(colName.name().replaceAll("_", " "))) {
                     colNames.add(colName);
                  }
               }
            }
            colsOrdered = colNames.toArray(new ColumnName[colNames.size()]);
            firstLine = false;
         } else {
            Map<ColumnName, String> colToValue = new HashMap<>();
            String[] values = line.split("\t");
            int x = 0;
            for (String value : values) {
               ColumnName col = colsOrdered[x++];
               colToValue.put(col, value);
            }
            JiraTask jTask = getJiraTask(colToValue, data.getResults());
            if (jTask == null) {
               return results;
            }
            if (!jTask.getAtsIds().isEmpty()) {
               jTaskMap.put(jTask.getAtsIds().iterator().next(), jTask);
               atsIds.add(jTask.getAtsIds().iterator().next());
            }
         }
      }

      // Bulk load workflows
      Map<String, IAtsWorkItem> workItemMap = atsApi.getQueryService().getWorkItemsByAtsId(atsIds);

      // Process Workflows
      processWorkflows(workItemMap, jTaskMap, results);

      allTime.end(Units.MIN);
      return results;
   }

   /**
    * @return false to stop processing lines
    */
   private boolean processWorkflows(Map<String, IAtsWorkItem> workItemMap, Map<String, JiraTask> jTaskMap, XResultData rd) {
      for (Entry<String, JiraTask> entry : jTaskMap.entrySet()) {
         JiraTask jTask = entry.getValue();
         String atsId = null;
         if (!jTask.getAtsIds().isEmpty()) {
            atsId = jTask.getAtsIds().iterator().next();
         }
         String summary = jTask.getSummary();
         String amsId = jTask.getAmsId();
         if (Strings.isInValid(atsId)) {
            rd.errorf("JIRA entry has no TW/ATS ID in %s\n\n", getAmsStringWithId(summary, amsId));
            continue;
         }
         IAtsWorkItem workItem = workItemMap.get(atsId);
         if (workItem == null) {
            rd.errorf("Can not get workitem for id %s\n\n", atsId);
            continue;
         }
         String assignees = AssigneeColumn.getAssigneeStrr(workItem);
         if (!workItem.getName().equals(summary)) {
            rd.errorf("Titles do not match for %s\n[%s] (JIRA)\n[%s] (OSEE) - [%s]\n\n", atsId, summary,
               workItem.getName(), assignees);
         }
         String jPoints = jTask.getPoints();
         String oPoints = atsApi.getAgileService().getAgileTeamPointsStr(workItem);
         if (!jPoints.equals(oPoints)) {
            rd.errorf("Points do not match for %s - JIRA: [%s] - OSEE: [%s] - [%s]\n%s\n\n", atsId, jPoints, oPoints,
               assignees, workItem.toStringWithId());
         }
         String jState = jTask.getStatus();
         String oState = null;
         boolean stateNoMatch = workItem.isCompletedOrCancelled() && !jState.equals("Closed");
         if (!stateNoMatch) {
            oState = workItem.getStateMgr().getCurrentStateNameFast();
            stateNoMatch = oState.equals(TeamState.Analyze.getName()) && !jState.equals("To Do");
         }
         if (!stateNoMatch) {
            stateNoMatch = oState.equals(TeamState.Implement.getName()) && !jState.equals("In Progress");
         }
         if (!stateNoMatch) {
            stateNoMatch = oState.equals(TeamState.Review.getName()) && !jState.equals("In Review");
         }
         if (stateNoMatch) {
            rd.errorf("States do not match for %s - JIRA: [%s] - OSEE: [%s] - [%s]\n%s\n\n", atsId, jState, oState,
               assignees, workItem.toStringWithId());
         }
      }
      return true;
   }

   private JiraTask getJiraTask(Map<ColumnName, String> colToValue, XResultData rd) {
      JiraTask jTask = new JiraTask();
      String atsId = null;
      String summary = colToValue.get(ColumnName.Summary);
      jTask.setSummary(summary);
      String amsId = colToValue.get(ColumnName.Key);
      jTask.setAmsId(amsId);
      if (Strings.isInValid(amsId) || Strings.isInValid(summary)) {
         rd.errorf("Must export Key and Summary");
         return null;
      }
      if (Strings.isValid(colToValue.get(ColumnName.Description))) {
         atsId = SyncJiraOperation.getAtsId(colToValue.get(ColumnName.Description));
      }
      if (Strings.isInValid(atsId)) {
         if (Strings.isValid(colToValue.get(ColumnName.Summary))) {
            atsId = SyncJiraOperation.getAtsId(colToValue.get(ColumnName.Summary));
         }
      }
      if (Strings.isValid(atsId)) {
         jTask.getAtsIds().add(atsId);
      } else {
         rd.errorf("JIRA entry has no TW/ATS ID in %s\n\n", getAmsStringWithId(summary, amsId));
      }
      String jPoints = colToValue.get(ColumnName.Story_Points);
      jTask.setPoints(jPoints);
      String jState = colToValue.get(ColumnName.Status);
      jTask.setStatus(jState);
      return jTask;
   }

   private Object getAmsStringWithId(String summary, String amsId) {
      return String.format("[%s]-[%s]", amsId, summary);
   }

   private enum ColumnName {
      Key,
      Summary,
      Description,
      Epic_Link,
      Status,
      Story_Points;
   }
}
