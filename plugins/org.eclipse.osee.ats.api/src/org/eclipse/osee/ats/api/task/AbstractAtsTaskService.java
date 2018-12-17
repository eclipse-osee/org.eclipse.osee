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
package org.eclipse.osee.ats.api.task;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsTaskService implements IAtsTaskService {

   private final AtsApi atsApi;

   public AbstractAtsTaskService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public Collection<IAtsTask> createTasks(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes, String commitComment) {
      NewTaskData newTaskData = getNewTaskData(teamWf, titles, assignees, createdDate, createdBy, relatedToState,
         taskWorkDef, attributes, commitComment);
      return createTasks(newTaskData);
   }

   @Override
   public NewTaskData getNewTaskData(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes) {
      return getNewTaskData(teamWf, titles, assignees, createdDate, createdBy, relatedToState, taskWorkDef, attributes,
         null);
   }

   @Override
   public Collection<IAtsTask> createTasks(NewTaskData newTaskData) {
      return createTasks(new NewTaskDatas(newTaskData));
   }

   @Override
   public NewTaskData getNewTaskData(IAtsTeamWorkflow teamWf, List<String> titles, List<IAtsUser> assignees, Date createdDate, IAtsUser createdBy, String relatedToState, String taskWorkDef, Map<String, List<Object>> attributes, String commitComment) {
      NewTaskData newTaskData = NewTaskDataFactory.get("Import Tasks from Simple List", createdBy, teamWf);
      if (createdDate == null) {
         createdDate = new Date();
      }

      for (String title : titles) {
         JaxAtsTask task = new JaxAtsTask();
         task.setName(title);
         if (assignees != null) {
            for (IAtsUser assignee : assignees) {
               task.addAssigneeUserIds(assignee.getUserId());
            }
         } else {
            task.addAssigneeUserIds(AtsCoreUsers.UNASSIGNED_USER.getUserId());
         }
         if (Strings.isValid(relatedToState)) {
            task.setRelatedToState(relatedToState);
         }
         task.setCreatedByUserId(createdBy.getUserId());
         task.setCreatedDate(createdDate);
         if (Strings.isValid(taskWorkDef)) {
            task.setTaskWorkDef(taskWorkDef);
         }
         newTaskData.getNewTasks().add(task);
         if (attributes != null) {
            for (Entry<String, List<Object>> entry : attributes.entrySet()) {
               task.addAttributes(entry.getKey(), entry.getValue());
            }
         }
         newTaskData.setCommitComment(commitComment);
      }
      return newTaskData;
   }

   @Override
   public Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf, IStateToken relatedToState) {
      ArtifactId artifact = atsApi.getArtifactResolver().get(teamWf);
      Conditions.checkNotNull(artifact, "teamWf", "Can't Find Artifact matching [%s]", teamWf.toString());
      List<IAtsTask> tasks = new LinkedList<>();
      for (IAtsTask task : atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.TeamWfToTask_Task,
         IAtsTask.class)) {
         if (atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.RelatedToState, "").equals(
            relatedToState.getName())) {
            tasks.add(task);
         }
      }
      return tasks;
   }

   @Override
   public Collection<? extends IAtsTask> getTasks(IAtsWorkItem workItem, IStateToken state) {
      ArtifactId artifact = atsApi.getArtifactResolver().get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      if (workItem instanceof IAtsTeamWorkflow) {
         return getTasks((IAtsTeamWorkflow) workItem, state);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf) {
      ArtifactId artifact = atsApi.getArtifactResolver().get(teamWf);
      Conditions.checkNotNull(artifact, "teamWf", "Can't Find Artifact matching [%s]", teamWf.toString());
      return atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.TeamWfToTask_Task, IAtsTask.class);
   }

   @Override
   public Collection<IAtsTask> getTask(IAtsWorkItem workItem) {
      if (workItem.isTeamWorkflow()) {
         return getTasks((IAtsTeamWorkflow) workItem);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public boolean hasTasks(IAtsTeamWorkflow teamWf) {
      return atsApi.getRelationResolver().getRelatedCount(teamWf, AtsRelationTypes.TeamWfToTask_Task) > 0;
   }

   @Override
   public boolean isRelatedToState(IAtsTask task, String stateName) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.RelatedToState, "").equals(
         stateName);
   }

   @Override
   public ArtifactId getTaskToRelatedArtifactChanged(IAtsTask task) {
      ArtifactId relatedArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(task,
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
      if (relatedArt.isValid()) {
         IAtsTeamWorkflow parentTeamWf = task.getParentTeamWorkflow();
         ArtifactToken derivedArt =
            atsApi.getRelationResolver().getRelatedOrNull(parentTeamWf, AtsRelationTypes.Derive_From);
         if (derivedArt != null && derivedArt instanceof IAtsTeamWorkflow) {
            IAtsTeamWorkflow derivedTeamWf = (IAtsTeamWorkflow) derivedArt;
            // First, attempt to get from Working Branch if still exists
            if (atsApi.getBranchService().isWorkingBranchInWork(derivedTeamWf)) {
               IOseeBranch workingBranch = atsApi.getBranchService().getWorkingBranch(derivedTeamWf);
               relatedArt = atsApi.getQueryService().getArtifact(relatedArt, BranchId.valueOf(workingBranch.getId()),
                  DeletionFlag.INCLUDE_DELETED);
            } else {
               // Else get from first commit transaction
               // NOTE: Each workflow has it's own commit in parallel dev
               TransactionToken earliestTransactionId =
                  atsApi.getBranchService().getEarliestTransactionId(derivedTeamWf);
               if (earliestTransactionId != null) {
                  relatedArt = atsApi.getQueryService().getHistoricalArtifactOrNull(relatedArt, earliestTransactionId,
                     DeletionFlag.INCLUDE_DELETED);
               }
            }
         }
      }
      return relatedArt;
   }
}
