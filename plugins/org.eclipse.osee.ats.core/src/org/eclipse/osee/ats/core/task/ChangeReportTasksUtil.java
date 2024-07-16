/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.task;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatch;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatchType;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.task.create.StaticTaskDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.change.ChangeItem;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTasksUtil {

   public static final String TASKS_MUST_BE_AUTOGEN_TASKS = "Tasks must be Auto Generated Tasks";
   public static final String TASKS_MUST_BE_AUTOGEN_CHANGE_REPORT_RELATED_TASKS =
      "Tasks must be Auto Generated Change Report Related Tasks";
   public static String DISABLE_AUTO_TASK_GENERATION = "disableTaskCreation";

   private ChangeReportTasksUtil() {
      // helper methods
   }

   public static void getBranchOrCommitChangeData(ChangeReportTaskData crtd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      IAtsTeamWorkflow chgRptTeamWf = atsApi.getWorkItemService().getTeamWf(crtd.getChgRptTeamWf());

      // If working branch, get change data from branch
      List<ChangeItem> changeItems = null;
      BranchToken workOrParentBranch = null;
      if (atsApi.getBranchService().isWorkingBranchInWork(chgRptTeamWf)) {
         BranchToken workingBranch = atsApi.getBranchService().getWorkingBranch(chgRptTeamWf);
         workOrParentBranch = workingBranch;
         changeItems = atsApi.getBranchService().getChangeData(BranchId.valueOf(workingBranch.getId()));
         crtd.getResults().logf("Using Working Branch %s\n", workingBranch.toStringWithId());
      }
      // Else get change data from earliest transaction
      else if (atsApi.getBranchService().isCommittedBranchExists(chgRptTeamWf)) {
         TransactionToken tx = atsApi.getBranchService().getEarliestTransactionId(chgRptTeamWf);
         workOrParentBranch = atsApi.getBranchService().getBranch(tx.getBranch());
         changeItems = atsApi.getBranchService().getChangeData(tx);
         crtd.getResults().logf("Using Commit Branch %s\n", workOrParentBranch.toStringWithId());
      }
      crtd.setWorkOrParentBranch(workOrParentBranch);
      if (changeItems == null) {
         crtd.getResults().warning("No Change Items (Change Report or Commit) Found");
         return;
      } else {
         crtd.setChangeItems(changeItems);
      }
   }

   /**
    * Compare already ChgRptTskCompAsNeeded task matches with existing tasks and determine fate.
    */
   public static void determinExistingTaskMatchType(Map<ArtifactId, ArtifactToken> idToArtifact, ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, CreateTasksDefinition setDef, WorkType workType, IAtsTeamWorkflow destTeamWf) {
      AtsApi atsApi = AtsApiService.get();
      Collection<IAtsTask> tasks = Collections.emptyList();
      if (destTeamWf != null) {
         tasks = atsApi.getTaskService().getTasks(destTeamWf);
      }

      // Find static task def matches
      List<IAtsTask> tasksFound = new LinkedList<IAtsTask>();
      for (IAtsTask task : tasks) {
         boolean found = false;
         for (StaticTaskDefinition taskDef : crtd.getSetDef().getStaticTaskDefs()) {
            if (task.getName().equals(taskDef.getName())) {
               for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
                  if (taskMatch.getTaskName().equals(task.getName())) {
                     taskMatch.setTaskName(task.getName());
                     taskMatch.setType(ChangeReportTaskMatchType.Match);
                     taskMatch.setTaskWf(task);
                     taskMatch.setTaskTok(task.getArtifactToken());
                     tasksFound.add(task);
                     found = true;
                     break;
                  }
               }
            }
            if (found) {
               break;
            }
         }
      }

      // Remove static matched tasks so we don't set them as non-matched
      tasks.removeAll(tasksFound);

      // Find artifact referenced matches
      for (IAtsTask task : tasks) {
         ArtifactId refChgArtId = atsApi.getAttributeResolver().getSoleArtifactIdReference(task,
            AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);

         // Search for artifact token (include deleted) cause needed name to compare
         if (refChgArtId.isValid()) {
            boolean found = false;
            for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
               // We found matching TaskMatch
               if (refChgArtId.getId().equals(taskMatch.getChgRptArt().getId()) && task.getName().equals(
                  taskMatch.getTaskName())) {
                  taskMatch.setTaskName(task.getName());
                  taskMatch.setType(ChangeReportTaskMatchType.Match);
                  taskMatch.setTaskWf(task);
                  taskMatch.setTaskTok(task.getArtifactToken());
                  found = true;
                  break;
               }
            }

            // If not, add task match that will probably be marked for removal
            if (!found) {
               ChangeReportTaskMatch newTaskMatch = new ChangeReportTaskMatch();
               newTaskMatch.setTaskName(task.getName());
               newTaskMatch.setTaskWf(task);
               newTaskMatch.setTaskTok(task.getArtifactToken());
               newTaskMatch.setType(ChangeReportTaskMatchType.TaskRefAttrButNoRefChgArt);
               crttwd.addTaskMatch(newTaskMatch);
            }
         } else {
            // No matching rel chg art attr; mark for removal
            ChangeReportTaskMatch taskMatch = new ChangeReportTaskMatch();
            taskMatch.setTaskName(task.getName());
            taskMatch.setTaskWf(task);
            taskMatch.setTaskTok(task.getArtifactToken());
            taskMatch.setType(ChangeReportTaskMatchType.TaskRefAttrMissing);
            crttwd.addTaskMatch(taskMatch);
         }
      }
   }

   /**
    * @return task match if task referenced
    */
   public static ChangeReportTaskMatch getTaskMatch(IAtsTask task, ArtifactId referencedChgArt, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      ArtifactId taskRefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(task,
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
      for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
         if (referencedChgArt.getId().equals(taskRefArt.getId())) {
            taskMatch.setTaskName(task.getName());
            taskMatch.setType(ChangeReportTaskMatchType.Match);
            taskMatch.setTaskWf(task);
            taskMatch.setTaskTok(task.getArtifactToken());
            return taskMatch;
         }
      }
      return null;
   }

   @SuppressWarnings("unlikely-arg-type")
   public static IAtsTeamWorkflow getDestTeamWfOrNull(ChangeReportTaskTeamWfData crttwd, WorkType workType, AtsApi atsApi, IAtsTeamWorkflow sourceTeamWf, IAtsTeamDefinition destTeamDef) {
      // Try to find by Derive_To first
      ArtifactToken chgRptTeamWf = crttwd.getChgRptTeamWf();

      Collection<ArtifactToken> derivedTo =
         atsApi.getRelationResolver().getRelated(chgRptTeamWf, AtsRelationTypes.Derive_To);
      IAtsAction parentAction = sourceTeamWf.getParentAction();
      Collection<IAtsTeamWorkflow> teamWorkflows = parentAction.getTeamWorkflows();

      for (ArtifactToken related : derivedTo) {
         if (related.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(related);
            if (teamWf.getTeamDefinition().equals(crttwd.getDestTeamDef())) {
               crttwd.setDestTeamWf(teamWf.getStoreObject());
               return teamWf;
            }
         }
      }
      // Else, look through siblings for matching team def
      for (IAtsTeamWorkflow teamWf : teamWorkflows) {
         if (!teamWf.equals(sourceTeamWf) && teamWf.getTeamDefinition().equals(destTeamDef)) {
            crttwd.setDestTeamWf(teamWf.getArtifactToken());
            return teamWf;
         }
      }
      return null;
   }

}
