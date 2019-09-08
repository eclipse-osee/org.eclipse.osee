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
import org.eclipse.osee.ats.api.task.create.ChangeReportData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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

   public static void getTaskNamesToBeCreated(ChangeReportData crd, CreateTasksDefinition setDef, WorkType workType) {
      ChangeReportTasksUtil.workType = workType;
      AtsApi atsApi = AtsApiService.get();
      XResultData rd = crd.getRd();

      IAtsTeamWorkflow sourceTeamWf = crd.getSourceTeamWf();
      List<ChangeItem> changeData;
      if (atsApi.getBranchService().isWorkingBranchInWork(sourceTeamWf)) {
         changeData =
            atsApi.getBranchService().getChangeData(atsApi.getBranchService().getWorkingBranchInWork(sourceTeamWf));
      } else if (atsApi.getBranchService().isCommittedBranchExists(sourceTeamWf)) {
         changeData =
            atsApi.getBranchService().getChangeData(atsApi.getBranchService().getEarliestTransactionId(sourceTeamWf));
      } else {
         rd.error("No Working Branch or Committed Transactions");
         return;
      }

      getArifactNames(crd, setDef, changeData);

   }

   public static void getArifactNames(ChangeReportData crd, CreateTasksDefinition setDef, List<ChangeItem> changeData) {
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

   public static void getReferencedArtsToTasks(ChangeReportData crd, CreateTasksDefinition setDef, WorkType workType, IAtsTeamWorkflow destTeamWf) {
      AtsApi atsApi = AtsApiService.get();
      for (IAtsTask task : atsApi.getTaskService().getTasks(destTeamWf)) {
         ArtifactId relArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(task,
            AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
         if (relArt.isValid()) {
            crd.getReferencedArtsToTasks().put(relArt, task);
         }
      }
   }

   private static String getTaskTitleDeleted(ArtifactId artifact, ChangeReportData crd, CreateTasksDefinition setDef) {
      String taskTitle = getTaskTitle(artifact, crd, setDef) + " (Deleted)";
      if (Strings.isValid(taskTitle)) {
         crd.addTaskedArtToName(artifact, taskTitle);
      }
      return taskTitle;
   }

   private static boolean isArtifactDeleted(ArtifactId artifact, ChangeReportData crd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      return atsApi.getStoreService().isDeleted(artifact);
   }

   private static String getTaskTitle(ArtifactId artifact, ChangeReportData crd, CreateTasksDefinition setDef) {
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

   private static boolean isArtifactTypeTaskable(ArtifactId artifact, ChangeReportData crd, CreateTasksDefinition setDef) {
      AtsApi atsApi = AtsApiService.get();
      ArtifactTypeToken artType = atsApi.getStoreService().getArtifactType(artifact);
      if (setDef.getChgRptOptions().getArtifactTypes().contains(
         artType) || !setDef.getChgRptOptions().getNotArtifactTypes().contains(artType)) {
         return true;
      }
      return false;
   }

}
