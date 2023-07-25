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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;

/**
 * @author Stephen J. Molaro
 */
public final class DevProgressMetrics implements StreamingOutput {
   private final AtsApi atsApi;
   private String programVersion;
   private final String targetVersion;
   private final Date startDate;
   private final Date endDate;
   private final boolean allTime;

   private ExcelXmlWriter writer;

   Pattern UI_NAME = Pattern.compile("\\{.*\\}");
   Pattern UI_DELETED = Pattern.compile("^.*\\(Deleted\\)$");

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

   public DevProgressMetrics(AtsApi atsApi, String targetVersion, Date startDate, Date endDate, boolean allTime) {
      this.atsApi = atsApi;
      this.programVersion = null;
      this.targetVersion = targetVersion;
      this.startDate = startDate;
      this.endDate = endDate;
      this.allTime = allTime;
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
      List<IAtsAction> actionableItems = getDatedWorkflows();
      if (!actionableItems.isEmpty()) {
         writer.startSheet("Non-Periodic Data", actionColumns.length);
         fillActionableData(actionableItems, actionColumns.length);
      }
   }

   private List<IAtsAction> getDatedWorkflows() {
      ArtifactToken versionId = atsApi.getQueryService().getArtifactFromTypeAndAttribute(AtsArtifactTypes.Version,
         CoreAttributeTypes.Name, targetVersion, atsApi.getAtsBranch());
      IAtsVersion version = atsApi.getVersionService().getVersionById(versionId);
      Collection<IAtsTeamWorkflow> workflowArts = atsApi.getVersionService().getTargetedForTeamWorkflows(version);
      List<IAtsAction> actionableItems = new ArrayList<>();

      for (IAtsTeamWorkflow workflow : workflowArts) {
         try {
            if (actionableItems.contains(workflow.getParentAction())) {
               continue;
            }
            if ((workflow.isWorkType(WorkType.Requirements) || workflow.isWorkType(
               WorkType.Code)) || workflow.isWorkType(WorkType.Test)) {
               if (allTime) {
                  actionableItems.add(workflow.getParentAction());
               } else if ((workflow.getCreatedDate().before(endDate))) {
                  if ((workflow.isCompleted() && workflow.getCompletedDate() != null && workflow.getCompletedDate().before(
                     startDate)) || (workflow.isCancelled() && workflow.getCancelledDate() != null && workflow.getCancelledDate().before(
                        startDate))) {
                     continue;
                  }
                  actionableItems.add(workflow.getParentAction());
               }
            }
         } catch (Exception ex) {
            continue;
         }

      }
      programVersion = atsApi.getRelationResolver().getRelatedOrSentinel(version,
         AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).getName();
      return actionableItems;
   }

   private void fillActionableData(List<IAtsAction> actionableItems, int numColumns) throws IOException {
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
         buffer[4] = endDate;
         Date createdDate = new Date();
         for (IAtsTeamWorkflow teamWorkflow : actionItem.getTeamWorkflows()) {
            if (teamWorkflow.getCreatedDate().before(createdDate)) {
               createdDate = teamWorkflow.getCreatedDate();
            }
         }
         buffer[5] = createdDate;
         fillTeamWfData(buffer, endDate, actionItem);
      }
      writer.endSheet();
   }

   private void fillTeamWfData(Object[] buffer, Date rowDate, IAtsAction actionItem) {
      double scale = Math.pow(10, 2);
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
      double reqAddModTasks = 0;
      double reqDeletedTasks = 0;

      //Code Workflow Parsing
      if (!codeWorkflow.equals(IAtsTeamWorkflow.SENTINEL) && !getStateAtDate(codeWorkflow, rowDate).isEmpty()) {
         Collection<IAtsTask> tasks = getTaskList(codeWorkflow, rowDate);
         double[] deletedCounts = getDeletedTaskCount(codeWorkflow, rowDate, tasks);
         String stateAtDate = getStateAtDate(codeWorkflow, rowDate);
         buffer[6] = "Code";
         buffer[7] = codeWorkflow.getAtsId();
         buffer[8] = stateAtDate;
         buffer[9] = null;
         buffer[10] = null;
         buffer[11] = null;
         buffer[12] = null;
         buffer[13] = null;
         switch (stateAtDate) {
            case "Analyze":
               buffer[9] = getStateStartedDate(codeWorkflow, rowDate, stateAtDate);
               break;
            case "Authorize":
               buffer[10] = getStateStartedDate(codeWorkflow, rowDate, stateAtDate);
               buffer[9] = getStateStartedDate(codeWorkflow, rowDate, "Analyze");
               break;
            case "Test":
            case "In_Work":
            case "Implement":
               buffer[11] = getStateStartedDate(codeWorkflow, rowDate, stateAtDate);
               buffer[10] = getStateStartedDate(codeWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(codeWorkflow, rowDate, "Analyze");
               break;
            case "Complete":
            case "Completed":
               buffer[12] = getStateStartedDate(codeWorkflow, rowDate, stateAtDate);
               buffer[11] = getStateStartedDate(codeWorkflow, rowDate, codeWorkflow.getCompletedFromState());
               buffer[10] = getStateStartedDate(codeWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(codeWorkflow, rowDate, "Analyze");
               break;
            case "Close":
            case "Closed with Problem":
            case "Cancelled":
               buffer[13] = getStateStartedDate(codeWorkflow, rowDate, stateAtDate);
               buffer[11] = getStateStartedDate(codeWorkflow, rowDate, codeWorkflow.getCancelledFromState());
               buffer[10] = getStateStartedDate(codeWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(codeWorkflow, rowDate, "Analyze");
            case "None":
               break;
         }
         double tasksCompleted = getTaskCompleted(codeWorkflow, rowDate, tasks);
         int tasksCancelled = getTaskCancelled(codeWorkflow, rowDate, tasks);
         double addModStat = tasks.size() - deletedCounts[0];
         double addModComp = tasksCompleted - deletedCounts[1];
         double addModCanc = tasksCancelled - deletedCounts[2];
         buffer[14] = tasks.size();
         buffer[15] = Math.round(tasksCompleted * scale) / scale;
         buffer[16] = tasksCancelled;
         buffer[17] = Math.round(addModStat * scale) / scale;
         buffer[18] = Math.round(addModComp * scale) / scale;
         buffer[19] = Math.round(addModCanc * scale) / scale;
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
         double[] deletedCounts = getDeletedTaskCount(testWorkflow, rowDate, tasks);
         String stateAtDate = getStateAtDate(testWorkflow, rowDate);
         buffer[6] = "Test";
         buffer[7] = testWorkflow.getAtsId();
         buffer[8] = stateAtDate;
         buffer[9] = null;
         buffer[10] = null;
         buffer[11] = null;
         buffer[12] = null;
         buffer[13] = null;
         switch (stateAtDate) {
            case "Analyze":
               buffer[9] = getStateStartedDate(testWorkflow, rowDate, stateAtDate);
               break;
            case "Authorize":
               buffer[10] = getStateStartedDate(testWorkflow, rowDate, stateAtDate);
               buffer[9] = getStateStartedDate(testWorkflow, rowDate, "Analyze");
               break;
            case "Test":
            case "In_Work":
            case "Implement":
               buffer[11] = getStateStartedDate(testWorkflow, rowDate, stateAtDate);
               buffer[10] = getStateStartedDate(testWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(testWorkflow, rowDate, "Analyze");
               break;
            case "Complete":
            case "Completed":
               buffer[12] = getStateStartedDate(testWorkflow, rowDate, stateAtDate);
               buffer[11] = getStateStartedDate(testWorkflow, rowDate, testWorkflow.getCompletedFromState());
               buffer[10] = getStateStartedDate(testWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(testWorkflow, rowDate, "Analyze");
               break;
            case "Close":
            case "Closed with Problem":
            case "Cancelled":
               buffer[13] = getStateStartedDate(testWorkflow, rowDate, stateAtDate);
               buffer[11] = getStateStartedDate(testWorkflow, rowDate, testWorkflow.getCancelledFromState());
               buffer[10] = getStateStartedDate(testWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(testWorkflow, rowDate, "Analyze");
            case "None":
               break;
         }
         reqTasks = tasks.size();
         reqAddModTasks = tasks.size() - deletedCounts[0];
         reqDeletedTasks = deletedCounts[0];
         double tasksCompleted = getTaskCompleted(testWorkflow, rowDate, tasks);
         int tasksCancelled = getTaskCancelled(testWorkflow, rowDate, tasks);
         double addModStat = tasks.size() - deletedCounts[0];
         double addModComp = tasksCompleted - deletedCounts[1];
         double addModCanc = tasksCancelled - deletedCounts[2];
         buffer[14] = tasks.size();
         buffer[15] = tasksCompleted;
         buffer[16] = tasksCancelled;
         buffer[17] = Math.round(addModStat * scale) / scale;
         buffer[18] = Math.round(addModComp * scale) / scale;
         buffer[19] = Math.round(addModCanc * scale) / scale;
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
         buffer[8] = stateAtDate;
         buffer[9] = null;
         buffer[10] = null;
         buffer[11] = null;
         buffer[12] = null;
         buffer[13] = null;
         switch (stateAtDate) {
            case "Analyze":
               buffer[9] = getStateStartedDate(requirementsWorkflow, rowDate, stateAtDate);
               break;
            case "Authorize":
               buffer[10] = getStateStartedDate(requirementsWorkflow, rowDate, stateAtDate);
               buffer[9] = getStateStartedDate(requirementsWorkflow, rowDate, "Analyze");
               break;
            case "Test":
            case "In_Work":
            case "Implement":
               buffer[11] = getStateStartedDate(requirementsWorkflow, rowDate, stateAtDate);
               buffer[10] = getStateStartedDate(requirementsWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(requirementsWorkflow, rowDate, "Analyze");
               break;
            case "Complete":
            case "Completed":
               buffer[12] = getStateStartedDate(requirementsWorkflow, rowDate, stateAtDate);
               buffer[11] =
                  getStateStartedDate(requirementsWorkflow, rowDate, requirementsWorkflow.getCompletedFromState());
               buffer[10] = getStateStartedDate(requirementsWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(requirementsWorkflow, rowDate, "Analyze");
               break;
            case "Close":
            case "Closed with Problem":
            case "Cancelled":
               buffer[13] = getStateStartedDate(requirementsWorkflow, rowDate, stateAtDate);
               buffer[11] =
                  getStateStartedDate(requirementsWorkflow, rowDate, requirementsWorkflow.getCancelledFromState());
               buffer[10] = getStateStartedDate(requirementsWorkflow, rowDate, "Authorize");
               buffer[9] = getStateStartedDate(requirementsWorkflow, rowDate, "Analyze");
            case "None":
               break;
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

   private String getStateAtDate(IAtsWorkItem teamWf, Date iterationDate) {
      if (allTime) {
         return teamWf.getCurrentStateName();
      }
      String stateName = teamWf.getCurrentStateName();
      Date stateStartDate = new GregorianCalendar(1916, 7, 15).getTime();
      try {
         Date newStateStartDate = atsApi.getWorkItemService().getStateStartedData(teamWf, stateName).getDate();
         if (newStateStartDate.after(iterationDate)) {
            for (String visitedState : teamWf.getLog().getVisitedStateNames()) {
               newStateStartDate = atsApi.getWorkItemService().getStateStartedData(teamWf, visitedState).getDate();
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

   private Date getStateStartedDate(IAtsWorkItem teamWf, Date iterationDate, String stateName) {
      try {
         IAtsLogItem stateStartedData = atsApi.getWorkItemService().getStateStartedData(teamWf, stateName);
         if (stateStartedData.getDate().before(iterationDate)) {
            return stateStartedData.getDate();
         }
      } catch (Exception ex) {
         //DO NOTHING
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

   private double[] getDeletedTaskCount(IAtsTeamWorkflow teamWorkflow, Date iterationDate, Collection<IAtsTask> tasks) {
      double[] deletedCounts = new double[3];
      double deletedCount = 0;
      double deletedCompleteCount = 0;
      double deletedCancelledCount = 0;
      for (IAtsTask task : tasks) {
         if (task.getCreatedDate().after(iterationDate)) {
            continue;
         }
         Matcher m = UI_DELETED.matcher(task.getName());
         if (m.find()) {
            try {
               deletedCount++;
               StateDefinition state = stateNameToDefinition(task, iterationDate);
               if (task.isCompleted() && (allTime || task.getCompletedDate().before(iterationDate))) {
                  deletedCompleteCount++;
               } else if (task.isCancelled() && (allTime || task.getCancelledDate().before(iterationDate))) {
                  deletedCancelledCount++;
               } else if (state.getRecommendedPercentComplete() != null && state.getRecommendedPercentComplete() > 0) {
                  deletedCompleteCount += (state.getRecommendedPercentComplete() / 100);
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

   private double getTaskCompleted(IAtsTeamWorkflow teamWorkflow, Date iterationDate, Collection<IAtsTask> tasks) {
      double completedTasks = 0;
      for (IAtsTask task : tasks) {
         try {
            StateDefinition state = stateNameToDefinition(task, iterationDate);
            if (state == null) {
               throw new OseeCoreException(
                  "In DevProgressMetrics.getTaskCompleted, the local variable \"state\" is null which is dereferenced");
            }
            if ((task.isCompleted() && task.getCompletedDate().before(iterationDate)) || state.getName().equals(
               "No_Change")) {
               completedTasks++;
            } else if (state.getRecommendedPercentComplete() != null && state.getRecommendedPercentComplete() > 0 && !state.getName().equals(
               "Cancelled")) {
               completedTasks += (state.getRecommendedPercentComplete() / 100.0);
            }
         } catch (Exception Ex) {
            //Do Nothing
         }
      }
      return completedTasks;
   }

   private StateDefinition stateNameToDefinition(IAtsWorkItem item, Date iterationDate) {
      if (allTime) {
         return item.getStateDefinition();
      }
      String stateAtDate = getStateAtDate(item, iterationDate);
      if (stateAtDate.equals(item.getCurrentStateName())) {
         return item.getStateDefinition();
      }
      for (StateDefinition state : item.getWorkDefinition().getStates()) {
         if (state.getName().equals(stateAtDate)) {
            return state;
         }
      }
      return null;
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
}