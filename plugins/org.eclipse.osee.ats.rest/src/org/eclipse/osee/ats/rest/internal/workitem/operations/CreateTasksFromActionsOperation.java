/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.rest.internal.workitem.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.task.create.TasksFromAction;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsConstants;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsChangeSetListener;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.logging.OseeLog;

public class CreateTasksFromActionsOperation {

   private final TasksFromAction tfa;
   private final AtsApi atsApi;
   private IAtsWorkItem destTeamWf;
   private List<IAtsTeamWorkflow> sourceTeamWfs;

   public CreateTasksFromActionsOperation(TasksFromAction tfa, AtsApi atsApi) {
      this.tfa = tfa;
      this.atsApi = atsApi;
   }

   public TasksFromAction run() {
      validate();
      if (tfa.getRd().isErrors()) {
         return tfa;
      }
      create();
      return tfa;
   }

   private void create() {
      Date date = new Date();
      NewTaskSet taskSet = new NewTaskSet();
      AtsUser createdBy = atsApi.getUserService().getUserById(tfa.getCreatedBy());
      taskSet.setAsUserId(createdBy.getUserId());
      taskSet.setCommitComment(AtsConstants.CreateTasksFromActions.name());
      taskSet.setResults(tfa.getRd());
      NewTaskData taskData = new NewTaskData();
      taskSet.add(taskData);
      taskData.setTeamWfId(tfa.getDestTeamWf().getId());
      for (IAtsTeamWorkflow srcWf : sourceTeamWfs) {
         JaxAtsTask jTask = new JaxAtsTask();
         taskData.add(jTask);
         jTask.setName(srcWf.getName());
         jTask.setCreatedByUserId(createdBy.getUserId());
         jTask.setCreatedDate(date);
         jTask.setDescription("See Related Team Workflow");
         jTask.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, srcWf.getId());
         jTask.addRelation(AtsRelationTypes.Derive_From, srcWf.getId());
      }
      taskSet = atsApi.getTaskService().createTasks(taskSet, new CreateTasksChangeSetListener(tfa));
      if (taskSet.getTransaction().isValid()) {
         tfa.getRd().log("Success.  Created tasks and cancelled workflows");
         tfa.getRd().setTxId(taskSet.getTransaction().getIdString());
      }
   }

   private class CreateTasksChangeSetListener implements IAtsChangeSetListener {

      private final TasksFromAction tfa;

      public CreateTasksChangeSetListener(TasksFromAction tfa) {
         this.tfa = tfa;
      }

      @Override
      public void changesStoring(IAtsChangeSet changes) {
         try {
            List<IAtsWorkItem> workItems = new ArrayList<>();
            for (ArtifactToken sourceTeamWf : tfa.getSourceTeamWfs()) {
               IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(sourceTeamWf.getId());
               workItems.add(workItem);
            }
            TransitionData tData = new TransitionData(AtsConstants.CreateTasksFromActions.name(), workItems,
               TeamState.Cancelled.getName(), Collections.emptyList(), tfa.getReason(), changes, TransitionOption.None);
            tData.setExecute(false);
            TransitionResults results = atsApi.getWorkItemService().transition(tData);
            if (!results.isSuccess() || results.isErrors()) {
               tfa.getRd().errorf(results.toString());
            }
         } catch (Exception ex) {
            OseeLog.log(getClass(), Level.WARNING, "Error in NotifyFunctionalArea", ex);
         }
      }
   }

   public void validate() {
      destTeamWf = atsApi.getWorkItemService().getWorkItem(tfa.getDestTeamWf().getId());
      if (destTeamWf == null) {
         tfa.getRd().errorf("Destination Workflow [%s] does not exist", tfa.getDestTeamWf());
         return;
      }
      if (!destTeamWf.isTeamWorkflow()) {
         tfa.getRd().errorf("Destination Workflow Item %s is not a Team Workflow", destTeamWf.toStringWithAtsId());
         return;
      }
      if (destTeamWf.isCompletedOrCancelled()) {
         tfa.getRd().errorf("Destination Team Workflow in [%s] state: %s", destTeamWf.getCurrentStateType(),
            destTeamWf.toStringWithAtsId());
         return;
      }
      tfa.getRd().logf("Destination Team Workflow: %s\n", destTeamWf.toStringWithAtsId());
      sourceTeamWfs = new ArrayList<>();
      for (ArtifactToken sourceId : tfa.getSourceTeamWfs()) {
         ArtifactToken sourceArt = atsApi.getQueryService().getArtifact(sourceId.getId());
         IAtsWorkItem sourceWf = atsApi.getWorkItemService().getWorkItem(sourceArt);
         if (sourceWf == null) {
            tfa.getRd().errorf("Selected Team Workflow %s does not exist", sourceId);
            return;
         }
         if (!destTeamWf.isTeamWorkflow()) {
            tfa.getRd().errorf("Selected Workflow Item %s is not a Team Workflow", sourceWf.toStringWithAtsId());
            return;
         }
         IAtsTeamWorkflow sourceTeamWf = (IAtsTeamWorkflow) sourceWf;
         if (destTeamWf.isCompletedOrCancelled()) {
            tfa.getRd().errorf("Selected Team Workflow in %s state: %s", sourceWf.getCurrentStateType(),
               destTeamWf.toStringWithAtsId());
            return;
         }
         tfa.getRd().logf("Source Team Workflow: %s\n", sourceTeamWf.toStringWithAtsId());
         sourceTeamWfs.add(sourceTeamWf);
      }
   }

}
