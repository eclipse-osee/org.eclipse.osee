/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ats.rest.metrics;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Stephen J. Molaro
 */
public final class DevProgressMetricsReport implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private String programVersion;
   private final String targetVersion;
   private final Date startDate;
   private final Date endDate;
   private final int dayOfWeek;
   private final int duration;
   private final boolean periodic;
   private final boolean nonPeriodic;
   private final boolean periodicTask;
   private final boolean nonPeriodicTask;

   private ExcelXmlWriter writer;
   private final QueryBuilder query;
   private final Calendar baseCalendar;

   private final Collection<String> stateList;

   Pattern UI_NAME = Pattern.compile("\\{.*\\}");
   Pattern UI_DELETED = Pattern.compile("^\\(Deleted\\)$");

   private final DevProgressItemId[] actionColumns = {
      DevProgressItemId.ACT,
      DevProgressItemId.ActionName,
      DevProgressItemId.Program,
      DevProgressItemId.Build,
      DevProgressItemId.Date,
      DevProgressItemId.Created,
      DevProgressItemId.WorkType,
      DevProgressItemId.TW,
      DevProgressItemId.State,
      DevProgressItemId.Analyze,
      DevProgressItemId.Authorize,
      DevProgressItemId.Implement,
      DevProgressItemId.Complete,
      DevProgressItemId.Cancelled,
      DevProgressItemId.TotalCount,
      DevProgressItemId.CompletedCount,
      DevProgressItemId.CancelledCount,
      DevProgressItemId.TotalAddModCount,
      DevProgressItemId.CompletedAddModCount,
      DevProgressItemId.CancelledAddModCount,
      DevProgressItemId.TotalDeletedCount,
      DevProgressItemId.CompletedDeletedCount,
      DevProgressItemId.CancelledDeletedCount};

   private final DevProgressItemId[] taskColumns = {
      DevProgressItemId.ACT,
      DevProgressItemId.TW,
      DevProgressItemId.Program,
      DevProgressItemId.Build,
      DevProgressItemId.TW,
      DevProgressItemId.TSK,
      DevProgressItemId.TSKName,
      DevProgressItemId.TSKType,
      DevProgressItemId.Date,
      DevProgressItemId.Created,
      DevProgressItemId.State,
      DevProgressItemId.TSKEndorse,
      DevProgressItemId.TSKAnalyze,
      DevProgressItemId.TSKAuthorize,
      DevProgressItemId.TSKImplement,
      DevProgressItemId.TSKComplete,
      DevProgressItemId.TSKCancelled};

   public DevProgressMetricsReport(OrcsApi orcsApi, AtsApi atsApi, String targetVersion, Date startDate, Date endDate, int dayOfWeek, int duration, boolean periodic, boolean nonPeriodic, boolean periodicTask, boolean nonPeriodicTask) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.programVersion = null;
      this.targetVersion = targetVersion;
      this.startDate = startDate;
      this.endDate = endDate;
      this.dayOfWeek = dayOfWeek;
      this.duration = duration;
      this.periodic = periodic;
      this.nonPeriodic = nonPeriodic;
      this.periodicTask = periodicTask;
      this.nonPeriodicTask = nonPeriodicTask;
      this.query = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch());
      this.baseCalendar = getBaseCalendar();
      this.stateList = List.of("Analyze", "Authorize", "Implement", "Completed", "Cancelled");

   }

   @Override
   public void write(OutputStream output) {
      try {
         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));
         writeReport();
         writer.endWorkbook();
      } catch (Exception ex) {
         try {
            writer.endWorkbook();
         } catch (IOException ex1) {
            throw new WebApplicationException(ex1);
         }
         throw new WebApplicationException(ex);
      }
   }

   private void writeReport() throws IOException {
      Collection<IAtsTeamWorkflow> workflows = getDatedWorkflows();
      if (!workflows.isEmpty()) {

         Set<IAtsAction> actionableItems = getDatedActions(workflows);

         if (periodic) {
            writer.startSheet("Periodic Data", actionColumns.length);
            fillActionableData(actionableItems, actionColumns.length);
         }
         if (nonPeriodic) {
            writer.startSheet("Non-Periodic Data", actionColumns.length);
            fillActionableData(actionableItems, actionColumns.length);
         }
         if (periodicTask) {
            writer.startSheet("Periodic Data", taskColumns.length);
            fillTaskData(workflows, taskColumns.length);
         }
         if (nonPeriodicTask) {
            writer.startSheet("Periodic Data", taskColumns.length);
            fillTaskData(workflows, taskColumns.length);
         }
      }
   }

   private Collection<IAtsTeamWorkflow> getDatedWorkflows() {
      ArtifactReadable versionId = query.andIsOfType(AtsArtifactTypes.Version).andAttributeIs(CoreAttributeTypes.Name,
         targetVersion).asArtifact();
      IAtsVersion version = atsApi.getVersionService().getVersionById(versionId);
      Collection<IAtsTeamWorkflow> workflowArts = atsApi.getVersionService().getTargetedForTeamWorkflows(version);

      Collection<IAtsTeamWorkflow> datedWorkflowArts = new ArrayList<IAtsTeamWorkflow>();
      for (IAtsTeamWorkflow workflow : workflowArts) {
         if (((workflow.isWorkType(WorkType.Requirements) || workflow.isWorkType(WorkType.Code)) || workflow.isWorkType(
            WorkType.Test)) && (workflow.getCreatedDate().before(endDate))) {
            if ((workflow.isCompleted() && workflow.getCompletedDate() != null && workflow.getCompletedDate().before(
               startDate)) || (workflow.isCancelled() && workflow.getCancelledDate() != null && workflow.getCancelledDate().before(
                  startDate))) {
               continue;
            }
            datedWorkflowArts.add(workflow);
         }
      }
      programVersion = atsApi.getRelationResolver().getRelatedOrSentinel(version,
         AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).getName();
      return datedWorkflowArts;
   }

   private Set<IAtsAction> getDatedActions(Collection<IAtsTeamWorkflow> workflows) {
      Set<IAtsAction> actionableItems = new HashSet<>();
      for (IAtsTeamWorkflow workflow : workflows) {
         actionableItems.add(workflow.getParentAction());
      }
      return actionableItems;
   }

   private void fillActionableData(Set<IAtsAction> actionableItems, int numColumns) throws IOException {
      Object[] buffer = new Object[numColumns];
      for (int i = 0; i < numColumns; ++i) {
         buffer[i] = actionColumns[i].getDisplayName();
      }
      writer.writeRow(buffer);

      for (IAtsAction actionItem : actionableItems) {
         buffer[0] = actionItem.getAtsId();
         buffer[1] = actionItem.getName();
         buffer[2] = programVersion;
         buffer[3] = targetVersion;
         Date createdDate = new Date();
         for (IAtsTeamWorkflow teamWorkflow : actionItem.getTeamWorkflows()) {
            if (teamWorkflow.getCreatedDate().before(createdDate)) {
               createdDate = teamWorkflow.getCreatedDate();
            }
         }
         buffer[5] = createdDate;

         if (periodic) {
            Calendar incrementedCalendar = new GregorianCalendar();
            incrementedCalendar.setTime(baseCalendar.getTime());
            while (incrementedCalendar.getTime().before(endDate)) {
               if (createdDate.before(incrementedCalendar.getTime())) {
                  fillTeamWfData(buffer, incrementedCalendar.getTime(), actionItem);
               }
               incrementedCalendar.add(Calendar.DAY_OF_MONTH, duration);
            }

         } else if (nonPeriodic) {
            fillTeamWfData(buffer, endDate, actionItem);
         }

      }
      writer.endSheet();
   }

   private void fillTeamWfData(Object[] buffer, Date rowDate, IAtsAction actionItem) {
      buffer[4] = rowDate;

      IAtsTeamWorkflow requirementsWorkflow = IAtsTeamWorkflow.SENTINEL;
      IAtsTeamWorkflow codeWorkflow = IAtsTeamWorkflow.SENTINEL;
      IAtsTeamWorkflow testWorkflow = IAtsTeamWorkflow.SENTINEL;

      for (IAtsTeamWorkflow teamWorkflow : actionItem.getTeamWorkflows()) {
         if (teamWorkflow.isWorkType(WorkType.Requirements)) {
            if (requirementsWorkflow.equals(IAtsTeamWorkflow.SENTINEL)) {
               requirementsWorkflow = teamWorkflow;
            } else if (requirementsWorkflow.isCancelled() && !teamWorkflow.isCancelled()) {
               requirementsWorkflow = teamWorkflow;
            }
         } else if (teamWorkflow.isWorkType(WorkType.Code)) {
            if (codeWorkflow.equals(IAtsTeamWorkflow.SENTINEL)) {
               codeWorkflow = teamWorkflow;
            } else if (codeWorkflow.isCancelled() && !teamWorkflow.isCancelled()) {
               codeWorkflow = teamWorkflow;
            }
         } else if (teamWorkflow.isWorkType(WorkType.Test)) {
            if (testWorkflow.equals(IAtsTeamWorkflow.SENTINEL)) {
               testWorkflow = teamWorkflow;
            } else if (testWorkflow.isCancelled() && !teamWorkflow.isCancelled()) {
               testWorkflow = teamWorkflow;
            }
         }
      }
      int reqTasks = 0;
      int reqAddModTasks = 0;
      int reqDeletedTasks = 0;

      //Code Workflow Parsing
      if (!codeWorkflow.equals(IAtsTeamWorkflow.SENTINEL) && !getStateAtDate(codeWorkflow, rowDate).isEmpty()) {
         Collection<IAtsTask> tasks = getTaskList(codeWorkflow, rowDate);
         int[] deletedCounts = getDeletedTaskCount(codeWorkflow, rowDate);
         buffer[6] = "Code";
         buffer[7] = codeWorkflow.getAtsId();
         buffer[8] = getStateAtDate(codeWorkflow, rowDate);
         int i = 9;
         for (String state : stateList) {
            buffer[i++] = getStateStartDate(codeWorkflow, rowDate, state);
         }
         int tasksCompleted = getTaskCompleted(codeWorkflow, rowDate, tasks);
         int tasksCancelled = getTaskCancelled(codeWorkflow, rowDate, tasks);

         buffer[14] = tasks.size();
         buffer[15] = tasksCompleted;
         buffer[16] = tasksCancelled;

         buffer[17] = tasks.size() - deletedCounts[0];
         buffer[18] = tasksCompleted - deletedCounts[1];
         buffer[19] = tasksCancelled - deletedCounts[2];

         buffer[20] = deletedCounts[0];
         buffer[21] = deletedCounts[1];
         buffer[22] = deletedCounts[2];

         try {
            writer.writeRow(buffer);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }

      //Test Workflow Parsing
      if (!testWorkflow.equals(IAtsTeamWorkflow.SENTINEL) && !getStateAtDate(testWorkflow, rowDate).isEmpty()) {
         Collection<IAtsTask> tasks = getTaskList(testWorkflow, rowDate);
         int[] deletedCounts = getDeletedTaskCount(testWorkflow, rowDate);
         buffer[6] = "Test";
         buffer[7] = testWorkflow.getAtsId();
         buffer[8] = getStateAtDate(testWorkflow, rowDate);
         int i = 9;
         for (String state : stateList) {
            buffer[i++] = getStateStartDate(testWorkflow, rowDate, state);
         }
         reqTasks = tasks.size();
         reqAddModTasks = tasks.size() - deletedCounts[0];
         reqDeletedTasks = deletedCounts[0];

         int tasksCompleted = getTaskCompleted(testWorkflow, rowDate, tasks);
         int tasksCancelled = getTaskCancelled(testWorkflow, rowDate, tasks);

         buffer[14] = tasks.size();
         buffer[15] = tasksCompleted;
         buffer[16] = tasksCancelled;

         buffer[17] = tasks.size() - deletedCounts[0];
         buffer[18] = tasksCompleted - deletedCounts[1];
         buffer[19] = tasksCancelled - deletedCounts[2];

         buffer[20] = deletedCounts[0];
         buffer[21] = deletedCounts[1];
         buffer[22] = deletedCounts[2];

         try {
            writer.writeRow(buffer);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }

      //Requirements Workflow Parsing
      if (!requirementsWorkflow.equals(
         IAtsTeamWorkflow.SENTINEL) && !getStateAtDate(requirementsWorkflow, rowDate).isEmpty()) {
         String stateAtDate = getStateAtDate(requirementsWorkflow, rowDate);
         buffer[6] = "Requirements";
         buffer[7] = requirementsWorkflow.getAtsId();
         buffer[8] = getStateAtDate(requirementsWorkflow, rowDate);
         int i = 9;
         for (String state : stateList) {
            buffer[i++] = getStateStartDate(requirementsWorkflow, rowDate, state);
         }
         buffer[14] = reqTasks;
         buffer[17] = reqAddModTasks;
         buffer[20] = reqDeletedTasks;
         if (stateAtDate.contains("Complete")) {
            buffer[15] = reqTasks;
            buffer[16] = 0;
            buffer[18] = reqAddModTasks;
            buffer[19] = 0;
            buffer[21] = reqDeletedTasks;
            buffer[22] = 0;
         } else if (stateAtDate.equals("Cancelled")) {
            buffer[15] = 0;
            buffer[16] = reqTasks;
            buffer[18] = 0;
            buffer[19] = reqAddModTasks;
            buffer[21] = 0;
            buffer[22] = reqDeletedTasks;
         } else {
            buffer[15] = 0;
            buffer[16] = 0;
            buffer[18] = 0;
            buffer[19] = 0;
            buffer[21] = 0;
            buffer[22] = 0;
         }

         try {
            writer.writeRow(buffer);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }

   }

   private void fillTaskData(Collection<IAtsTeamWorkflow> workflows, int numColumns) throws IOException {
      Object[] buffer = new Object[numColumns];
      for (int i = 0; i < numColumns; ++i) {
         buffer[i] = taskColumns[i].getDisplayName();
      }
      writer.writeRow(buffer);

      for (IAtsTeamWorkflow workflow : workflows) {
         buffer[0] = workflow.getParentAction().getAtsId();
         buffer[1] = workflow.getAtsId();
         buffer[2] = programVersion;
         buffer[3] = targetVersion;
         Collection<IAtsTask> tasks = getTaskList(workflow, endDate);

         for (IAtsTask task : tasks) {
            buffer[4] = task.getAtsId();
            buffer[5] = task.getName();
            if (periodicTask) {
               Calendar incrementedCalendar = new GregorianCalendar();
               incrementedCalendar.setTime(baseCalendar.getTime());
               while (incrementedCalendar.getTime().before(endDate)) {
                  if (workflow.getCreatedDate().before(incrementedCalendar.getTime()) && task.getCreatedDate().before(
                     incrementedCalendar.getTime())) {
                     fillTaskStateData(buffer, incrementedCalendar.getTime(), task);
                  }
                  incrementedCalendar.add(Calendar.DAY_OF_MONTH, duration);
               }
            } else if (nonPeriodicTask) {
               fillTaskStateData(buffer, endDate, task);
            }
         }

      }
      writer.endSheet();
   }

   private void fillTaskStateData(Object[] buffer, Date rowDate, IAtsTask task) {
      buffer[6] = rowDate;
      buffer[7] = task.getCreatedDate();
      buffer[8] = getStateAtDate(task, rowDate);
      int i = 9;
      for (String state : stateList) {
         buffer[i++] = getStateStartDate(task, rowDate, state);
      }
      try {
         writer.writeRow(buffer);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private String getStateAtDate(IAtsWorkItem teamWorkflow, Date iterationDate) {
      String stateName = "";
      Date stateStartDate = new GregorianCalendar(1916, 7, 15).getTime();
      try {
         for (String visitedState : teamWorkflow.getStateMgr().getVisitedStateNames()) {
            if (stateList.contains(visitedState)) {
               Date newStateStartDate = teamWorkflow.getStateMgr().getStateStartedData(visitedState).getDate();
               if (newStateStartDate.before(iterationDate) && newStateStartDate.after(stateStartDate)) {
                  stateName = visitedState;
                  stateStartDate = newStateStartDate;
               }
            }
         }
      } catch (Exception ex) {
         //Do Nothing
      }
      return stateName;
   }

   private String getStateStartDate(IAtsWorkItem teamWorkflow, Date iterationDate, String stateName) {
      try {
         IAtsLogItem stateStartedData = teamWorkflow.getStateMgr().getStateStartedData(stateName);
         if (stateStartedData.getDate().before(iterationDate)) {
            return stateStartedData.getDate("MM/dd/yyyy");
         }
      } catch (Exception ex) {
         //Do Nothing
      }
      return null;
   }

   private Collection<IAtsTask> getTaskList(IAtsTeamWorkflow teamWorkflow, Date iterationDate) {
      Collection<IAtsTask> tasks = new ArrayList<IAtsTask>();
      Collection<String> taskUINames = new ArrayList<String>();
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWorkflow)) {
         Matcher m = UI_NAME.matcher(task.getName());
         if (m.find()) {
            String taskUIName = m.group();
            if (task.getCreatedDate().before(iterationDate) && !taskUINames.contains(taskUIName)) {
               taskUINames.add(taskUIName);
               tasks.add(task);
            }
         }
      }
      return tasks;
   }

   private int[] getDeletedTaskCount(IAtsTeamWorkflow teamWorkflow, Date iterationDate) {
      int[] deletedCounts = new int[3];
      int deletedCount = 0;
      int deletedCompleteCount = 0;
      int deletedCancelledCount = 0;
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWorkflow)) {
         Matcher m = UI_DELETED.matcher(task.getName());
         if (m.find()) {
            try {
               if (task.getCreatedDate().before(iterationDate)) {
                  deletedCount++;
                  if (task.isCompleted() && task.getCompletedDate().before(iterationDate)) {
                     deletedCompleteCount++;
                  } else if (task.isCancelled() && task.getCancelledDate().before(iterationDate)) {
                     deletedCancelledCount++;
                  }
               }
            } catch (Exception Ex) {
               //Do Nothing
            }
         }
      }
      deletedCounts[0] = deletedCount;
      deletedCounts[1] = deletedCompleteCount;
      deletedCounts[2] = deletedCancelledCount;
      return deletedCounts;
   }

   private int getTaskCompleted(IAtsTeamWorkflow teamWorkflow, Date iterationDate, Collection<IAtsTask> tasks) {
      Collection<IAtsTask> iterationTasks = new ArrayList<IAtsTask>();
      for (IAtsTask task : tasks) {
         try {
            if ((task.isCompleted() && task.getCompletedDate().before(
               iterationDate)) || task.getStateMgr().getCurrentState().getName().equals("No_Change")) {
               iterationTasks.add(task);
            }
         } catch (Exception Ex) {
            //Do Nothing
         }
      }
      return iterationTasks.size();
   }

   private int getTaskCancelled(IAtsTeamWorkflow teamWorkflow, Date iterationDate, Collection<IAtsTask> tasks) {
      Collection<IAtsTask> iterationTasks = new ArrayList<IAtsTask>();
      for (IAtsTask task : tasks) {
         try {
            if (task.isCancelled() && task.getCancelledDate().before(iterationDate)) {
               iterationTasks.add(task);
            }
         } catch (Exception Ex) {
            //Do Nothing
         }
      }
      return iterationTasks.size();
   }

   private Calendar getBaseCalendar() {
      Calendar calendarIncrement = new GregorianCalendar();
      calendarIncrement.setTime(startDate);
      int weekAdjustment = (dayOfWeek - calendarIncrement.get(Calendar.DAY_OF_WEEK)) % 7;
      calendarIncrement.add(Calendar.DAY_OF_MONTH, weekAdjustment);
      return calendarIncrement;
   }

}