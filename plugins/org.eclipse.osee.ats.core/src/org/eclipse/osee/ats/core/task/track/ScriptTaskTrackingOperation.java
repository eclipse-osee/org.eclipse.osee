/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task.track;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.task.track.TaskTrackItem;
import org.eclipse.osee.ats.api.task.track.TaskTrackItems;
import org.eclipse.osee.ats.api.task.track.TaskTrackingData;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * See AtsTaskTrackingDesign.md for documentation
 *
 * @author Donald G. Dunne
 * @author Stephen J. Molaro
 */
public class ScriptTaskTrackingOperation {

   public static final ArtifactToken OseeProductTesting =
      ArtifactToken.valueOf(8006047, "OSEE Product Testing.xml", CoreArtifactTypes.GeneralDocument);
   private final AtsApi atsApi;
   private final TaskTrackingData trackData;
   private final XResultData rd;

   public ScriptTaskTrackingOperation(TaskTrackingData trackData, AtsApi atsApi) {
      this.trackData = trackData;
      this.rd = trackData.getResults();
      this.atsApi = atsApi;
   }

   public TaskTrackingData run() {

      //Gets the existing Artifact Token or SENTINEL
      ArtifactToken teamWfArt =
         atsApi.getQueryService().getArtifactByNameOrSentinel(AtsArtifactTypes.TeamWorkflow, trackData.getTitle());

      Date createDate = new Date();

      //Gets User
      AtsUser asUser = atsApi.getUserService().getUserByToken(SystemUser.OseeSystem);

      //Get User assigned to in trackData
      if (Strings.isValid(trackData.getAsUserArtId())) {
         asUser = atsApi.getUserService().getUserById(ArtifactId.valueOf(trackData.getAsUserArtId()));
      }

      //If the workflow exists, set it, else create it
      IAtsTeamWorkflow teamWf =
         teamWfArt.isValid() ? atsApi.getWorkItemService().getTeamWf(teamWfArt) : createNewTeamWorkflow(asUser,
            createDate);

      if (rd.isErrors()) {
         return trackData;
      }
      trackData.setTeamWf(teamWf.getArtifactToken());

      // Create or Update Tasks as needed
      String txComment = trackData.getTransactionComment();
      if (Strings.isInValid(txComment)) {
         txComment = "Create/Update Tracking Review";
      }
      IAtsChangeSet changes = atsApi.createChangeSet(txComment);

      NewTaskSet newTaskSet = NewTaskSet.create(txComment, asUser);
      NewTaskData newTaskData = NewTaskData.create(newTaskSet, teamWf);

      // Create from task json payload and to review each Action if doesn't already exist
      createUpdateTaskItems(teamWf, asUser, createDate, newTaskData, trackData.getTrackItems(), changes);

      // Create from static task json from artifact (if supplied)
      try {
         String taskTrackArtId = trackData.getTaskTrackArtId();
         if (Strings.isNumeric(taskTrackArtId)) {
            ArtifactToken staticTaskArt = atsApi.getQueryService().getArtifact(Long.valueOf(taskTrackArtId));
            String taskTrackItemsJson = atsApi.getAttributeResolver().getSoleAttributeValue(staticTaskArt,
               CoreAttributeTypes.GeneralStringData, "");
            TaskTrackItems taskTrackItems = JsonUtil.readValue(taskTrackItemsJson, TaskTrackItems.class);
            if (Strings.isValid(taskTrackItemsJson)) {
               createUpdateTaskItems(teamWf, asUser, createDate, newTaskData, taskTrackItems, changes);
            }
         }
      } catch (Exception ex) {
         rd.errorf("Exception loading/processing staticTaskArt data [%s]: %s", trackData.getTaskTrackArtId(),
            Lib.exceptionToString(ex));
      }

      // Assign users assigned to inWork tasks to the review workflow
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
         if (task.isInWork()) {
            for (AtsUser assignee : task.getAssignees()) {
               changes.addAssignee(teamWf, assignee);
            }
         }
      }

      changes.executeIfNeeded();

      if (!newTaskSet.getNewTaskDatas().iterator().next().getTasks().isEmpty()) {
         NewTaskSet createTasks = atsApi.getTaskService().createTasks(newTaskSet);
         if (createTasks.isErrors()) {
            rd.merge(createTasks.getResults());
         }
      }
      return trackData;
   }

   private IAtsTeamWorkflow createNewTeamWorkflow(AtsUser asUser, Date createDate) {
      IAtsActionableItem ai =
         atsApi.getActionableItemService().getActionableItemById(ArtifactId.valueOf(trackData.getAiArtId()));
      if (ai == null) {
         rd.errorf("Invalid Actionable Item [%s]", trackData.getAiArtId());
         return null;
      }

      if (Strings.isInvalid(trackData.getTitle())) {
         rd.errorf("Invalid Action title [%s]", trackData.getTitle());
         return null;
      }

      String txComment = trackData.getTransactionComment();
      if (Strings.isInValid(txComment)) {
         txComment = "Create/Update Tracking Review";
      }
      IAtsChangeSet changes = atsApi.createChangeSet(txComment);

      // Create Action if not found
      ActionResult actionResult = atsApi.getActionService().createAction(null, trackData.getTitle(),
         trackData.getDescription(), trackData.getChangeType(), trackData.getPriority(), false, null, Arrays.asList(ai),
         createDate, asUser, null, changes);
      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getFirstTeam(actionResult);
      if (teamWf == null) {
         rd.errorf("Unable to create/get team workflow\n");
         return null;
      }

      if (Strings.isValid(trackData.getTransitionTo())) {
         for (String stateName : trackData.getTransitionTo().split(",")) {
            // Transition Action to Implement state
            TeamWorkFlowManager workflowMgr = new TeamWorkFlowManager(teamWf, atsApi);
            Result result = workflowMgr.transitionToState(false, teamWf, TeamState.valueOf(stateName),
               teamWf.getAssignees(), changes, atsApi);
            if (result.isFalse()) {
               rd.error(result.toString());
               return null;
            }
         }
      }
      changes.execute();
      return teamWf;
   }

   private void createUpdateTaskItems(IAtsTeamWorkflow teamWf, AtsUser asUser, Date createDate, NewTaskData newTaskData,
      TaskTrackItems taskTrackItems, IAtsChangeSet changes) {
      for (TaskTrackItem taskItem : taskTrackItems.getTasks()) {
         IAtsTask taskArt = getValidateTask(teamWf, taskItem.getTitle());
         if (!taskArt.isValid()) {
            JaxAtsTask createJaxTask = JaxAtsTask.create(newTaskData, taskItem.getTitle(), asUser, createDate);
            createJaxTask.setDescription(taskItem.getDescription());
            IAtsTeamWorkflow supportingTeamWf = null;
            if (Strings.isValid(taskItem.getSupportingAtsId())) {
               ArtifactToken supportTeamWfArt =
                  atsApi.getQueryService().getArtifactByAtsId(taskItem.getSupportingAtsId());
               if (supportTeamWfArt == null) {
                  rd.errorf("Invalid supporting TeamWf [%s]", taskItem.getSupportingAtsId());
               } else {
                  supportingTeamWf = atsApi.getWorkItemService().getTeamWf(supportTeamWfArt);
                  createJaxTask.addRelation(CoreRelationTypes.SupportingInfo_IsSupportedBy, supportingTeamWf.getId());
               }
            }
            // Assign user specified in taskItem
            List<String> assigneeUserIds = new LinkedList<>();
            if (Strings.isValid(taskItem.getAssigneesArtIds())) {
               for (String assigneeArtId : taskItem.getAssigneesArtIds().split(",")) {
                  AtsUser user = atsApi.getUserService().getUserById(ArtifactId.valueOf(assigneeArtId));
                  if (user != null && !assigneeUserIds.contains(user.getUserId())) {
                     assigneeUserIds.add(user.getUserId());
                  }
               }
            }
            // If no assignees, set to current assignees or completed by from related supporting TW
            if (assigneeUserIds.isEmpty() && supportingTeamWf != null) {
               for (AtsUser assignee : supportingTeamWf.getAssignees()) {
                  assigneeUserIds.add(assignee.getUserId());
               }
               AtsUser completedUser = supportingTeamWf.getCompletedBy();
               if (completedUser != null && !completedUser.getUserId().equals(AtsCoreUsers.SYSTEM_USER.getUserId())) {
                  assigneeUserIds.add(completedUser.getUserId());
               }
            }

            createJaxTask.setAssigneeUserIds(assigneeUserIds);
         } else {
            if (taskArt.isCompleted() && trackData.getTasksReopen()) {
               TransitionData transData = new TransitionData("Transition to InWork", Arrays.asList(taskArt),
                  StateToken.InWork.getName(), taskArt.getAssignees(), null, null);
               atsApi.getWorkItemService().transition(transData);
            }
            changes.setSoleAttributeValue(taskArt, AtsAttributeTypes.Description, taskItem.getDescription());

            String defaultAssignee = taskItem.getDefaultAssigneesArtIds();
            String assignee = taskItem.getAssigneesArtIds();
            List<AtsUser> currentAssignees = taskArt.getAssignees();

            if (currentAssignees.size() == 1) {
               String currentAssignee = "";
               currentAssignee = currentAssignees.get(0).getIdString();
               if (currentAssignee.isEmpty() || currentAssignee.equals(defaultAssignee)) {
                  changes.setAssignee(taskArt, atsApi.getUserService().getUserById(ArtifactId.valueOf(assignee)));
               }
            }

         }
      }
   }

   // Find task by name
   private IAtsTask getValidateTask(IAtsTeamWorkflow teamWf, String taskName) {
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
         if (task.getName().equals(taskName)) {
            return task;
         }
      }
      return IAtsTask.SENTINEL;
   }

   public void setTaskCompleted() {
      ArtifactToken teamWfArt =
         atsApi.getQueryService().getArtifactByNameOrSentinel(AtsArtifactTypes.TeamWorkflow, trackData.getTitle());

      //Check if the workflow artifact is valid
      if (!teamWfArt.isValid()) {
         rd.errorf("Workflow not found for title: %s", trackData.getTitle());
         return;
      }

      //Iterate over all tasks in trackData
      for (TaskTrackItem taskItem : trackData.getTrackItems().getTasks()) {
         String taskName = taskItem.getTitle(); // Get the title of the current task
         IAtsTask task = getValidateTask(atsApi.getWorkItemService().getTeamWf(teamWfArt), taskName);

         //Check if the task is valid
         if (!task.isValid()) {
            rd.errorf("Task not found for name: %s", taskName);
            continue;
         }

         //Check if the task is already completed
         if (task.isCompleted()) {
            continue;
         }

         //Mark the task as complete
         TransitionData transitionData = new TransitionData("Complete Task", Arrays.asList(task),
            StateToken.Completed.getName(), task.getAssignees(), null, null);
         atsApi.getWorkItemService().transition(transitionData);
      }
   }

}
