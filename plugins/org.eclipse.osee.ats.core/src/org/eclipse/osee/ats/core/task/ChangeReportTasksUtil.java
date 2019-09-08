/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task;

import java.util.List;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTasksUtil {

   public static String AUTO_GENERATED_STATIC_ID = "AutoGenTask";
   public static final String NO_MATCHING_CHANGE_REPORT_ARTIFACT = "No Match to Change Report Artifact; ";
   public static final String TASKS_MUST_BE_AUTOGEN_CODE_OR_TEST_TASKS = "Tasks must be Auto Generated Tasks";
   private static WorkType workType;

   private ChangeReportTasksUtil() {
      // helper methods
   }

   public static void getBranchOrCommitChangeData(ChangeReportTaskData crtd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      IAtsTeamWorkflow chgRptTeamWf = atsApi.getWorkItemService().getTeamWf(crtd.getChgRptTeamWf());

      // If working branch, get change data from branch
      List<ChangeItem> changeData = null;
      BranchId workOrParentBranch = null;
      if (atsApi.getBranchService().isWorkingBranchInWork(chgRptTeamWf)) {
         IOseeBranch workingBranch = atsApi.getBranchService().getWorkingBranch(chgRptTeamWf);
         workOrParentBranch = workingBranch;
         changeData = atsApi.getBranchService().getChangeData(workingBranch);
         crtd.getResults().logf("Using Working Branch %s\n", workingBranch.toStringWithId());
      }
      // Else get change data from earliest transaction
      else if (atsApi.getBranchService().isCommittedBranchExists(chgRptTeamWf)) {
         TransactionToken tx = atsApi.getBranchService().getEarliestTransactionId(chgRptTeamWf);
         workOrParentBranch = tx.getBranch();
         changeData = atsApi.getBranchService().getChangeData(tx);
         crtd.getResults().logf("Using Commit Branch %s\n",
            atsApi.getBranchService().getBranch(tx.getBranch()).toStringWithId());
      }
      crtd.setWorkOrParentBranch(workOrParentBranch);
      crtd.setChangeData(changeData);
   }

   @SuppressWarnings("unchecked")
   public static void processChangeData(ChangeReportTaskData crtd) {
      for (ChangeItem item : crtd.getChangeData()) {
         if (item.getChangeType() == ChangeType.ARTIFACT_CHANGE && item.isDeleted()) {
            crtd.getDeletedArts().add(item.getArtId());
         } else {
            if (item.getChangeType() == ChangeType.ATTRIBUTE_CHANGE || item.getChangeType() == ChangeType.ARTIFACT_CHANGE) {
               crtd.getAddedModifiedArts().add(item.getArtId());
            } else if (item.getChangeType() == ChangeType.RELATION_CHANGE) {
               crtd.getRelArts().add(item.getArtId());
               crtd.getRelArts().add(item.getArtIdB());
            }
         }
      }
      crtd.getAllArtifacts().addAll(org.eclipse.osee.framework.jdk.core.util.Collections.setUnion(
         crtd.getAddedModifiedArts(), crtd.getDeletedArts(), crtd.getRelArts()));
   }

   public static void getArifactNames(ChangeReportTaskTeamWfData crd, CreateTasksDefinition setDef, List<ChangeItem> changeData) {
      for (ArtifactId artifact : crd.getAddedModifiedArts()) {
         if (crd.getTaskedArtToName().keySet().contains(artifact)) {
            continue;
         }

         if (!isArtifactTypeTaskable(artifact, crd, setDef)) {
            return;
         }
         if (isArtifactDeleted(artifact, crd, setDef)) {
            crd.addTaskedArtToName(artifact, getTaskTitleDeleted(artifact, crd, setDef));
            return;
         }

         for (ChangeItem change : changeData) {
            if (isAttributeTypeTaskable(change.getItemTypeId(), setDef)) {
               String taskTitle = getTaskTitle(artifact, crd, setDef);
               if (Strings.isValid(taskTitle)) {
                  crd.addTaskedArtToName(artifact, taskTitle);
               }
            }
         }
      }
   }

   public static void getReferencedArtsToTasks(ChangeReportTaskTeamWfData crd, CreateTasksDefinition setDef, WorkType workType, IAtsTeamWorkflow destTeamWf) {
      AtsApi atsApi = AtsApiService.get();
      for (IAtsTask task : atsApi.getTaskService().getTasks(destTeamWf)) {
         ArtifactId relArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(task,
            AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
         if (relArt.isValid()) {
            crd.getReferencedArtsToTasks().put(relArt, task);
         }
      }
   }

   private static String getTaskTitleDeleted(ArtifactId artifact, ChangeReportTaskTeamWfData crd, CreateTasksDefinition setDef) {
      String taskTitle = getTaskTitle(artifact, crd, setDef) + " (Deleted)";
      if (Strings.isValid(taskTitle)) {
         crd.addTaskedArtToName(artifact, taskTitle);
      }
      return taskTitle;
   }

   private static boolean isArtifactDeleted(ArtifactId artifact, ChangeReportTaskTeamWfData crd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      return atsApi.getStoreService().isDeleted(artifact);
   }

   private static String getTaskTitle(ArtifactId artifact, ChangeReportTaskTeamWfData crd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      @Nullable
      ArtifactToken art = atsApi.getQueryService().getArtifact(artifact);
      if (art == null) {
         crd.getRd().errorf("Can't retrive artifact for id %s", artifact.getIdString());
      } else {
         return String.format("%s for [%s]%s", workType.name(), art.getName(), "");
      }
      return null;
   }

   private static boolean isAttributeTypeTaskable(Id itemTypeId, CreateTasksDefinition setDef) {
      AttributeTypeId attrType = AttributeTypeId.valueOf(itemTypeId.getId());
      if (setDef.getChgRptOptions().getAttributeTypes().contains(
         attrType) || !setDef.getChgRptOptions().getNotAttributeTypes().contains(attrType)) {
         return true;
      }
      return false;
   }

   private static boolean isArtifactTypeTaskable(ArtifactId artifact, ChangeReportTaskTeamWfData crd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      ArtifactTypeToken artType = atsApi.getStoreService().getArtifactType(artifact);
      if (setDef.getChgRptOptions().getArtifactTypes().contains(
         artType) || !setDef.getChgRptOptions().getNotArtifactTypes().contains(artType)) {
         return true;
      }
      return false;
   }

   public static void getTaskNamesFromChanges(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      getModifiedArifactNames(crtd, crttwd, atsApi);
      getDeletedArifactNames(crtd, crttwd, atsApi);
      getRelArtifactNames(crtd, crttwd, atsApi);
      getExtensionArtifactNames(crtd, crttwd, atsApi);
   }

   private static void getExtensionArtifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      // TBD - Allow extensions to add tasks
   }

   private static void getRelArtifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      for (ArtifactId chgRptArt : crtd.getRelArts()) {
         ArtifactToken art =
            atsApi.getQueryService().getArtifact(chgRptArt, crtd.getWorkOrParentBranch(), DeletionFlag.INCLUDE_DELETED);
         crttwd.addTaskedArtToName(chgRptArt, String.format("Handle change to %s", art.toStringWithId()));
      }
   }

   public static void getModifiedArifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      for (ArtifactId chgRptArt : crtd.getAddedModifiedArts()) {
         ArtifactToken art =
            atsApi.getQueryService().getArtifact(chgRptArt, crtd.getWorkOrParentBranch(), DeletionFlag.INCLUDE_DELETED);
         crttwd.addTaskedArtToName(chgRptArt, String.format("Handle change to %s", art.toStringWithId()));
      }
   }

   public static void getDeletedArifactNames(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, AtsApi atsApi) {
      for (ArtifactId chgRptArt : crtd.getDeletedArts()) {
         ArtifactToken art =
            atsApi.getQueryService().getArtifact(chgRptArt, crtd.getWorkOrParentBranch(), DeletionFlag.INCLUDE_DELETED);
         crttwd.addTaskedArtToName(chgRptArt, String.format("Handle change to %s", art.toStringWithId()));
      }
   }

}
