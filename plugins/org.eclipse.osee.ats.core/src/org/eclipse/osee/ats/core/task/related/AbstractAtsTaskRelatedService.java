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

package org.eclipse.osee.ats.core.task.related;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.TaskNameData;
import org.eclipse.osee.ats.api.task.related.IAtsTaskRelatedService;
import org.eclipse.osee.ats.api.task.related.TaskRelatedData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.ChangeReportTasksUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsTaskRelatedService implements IAtsTaskRelatedService {

   protected final AtsApi atsApi;

   public AbstractAtsTaskRelatedService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public TaskRelatedData getTaskRelatedData(TaskRelatedData trd) {
      getDerivedTeamWf(trd);
      if (trd.getResults().isErrors()) {
         return trd;
      }
      getRelatedChangedArtifact(trd);
      if (trd.getResults().isErrors()) {
         return trd;
      }
      getRelatedChangedArtifactFromChangeReport(trd);
      if (trd.getResults().isErrors()) {
         return trd;
      }
      return trd;
   }

   private TaskRelatedData getRelatedChangedArtifact(TaskRelatedData trd) {
      if (trd.getDerivedFromTeamWf() == null) {
         trd.getResults().error("Requirement Team Workflow can't be found\n");
      }

      ArtifactId artifact = atsApi.getAttributeResolver().getSoleAttributeValue(trd.getTask(),
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
      if (artifact.isValid()) {
         getRelatedChangedArtifact(trd, artifact);
         return trd;
      } else {
         trd.getResults().errorf("No related change artifact for %s", trd.getTask().toStringWithId());
      }

      return trd;
   }

   @Override
   public TaskRelatedData getDerivedTeamWf(TaskRelatedData trd) {
      if (trd.getTask() == null) {
         trd.getResults().error("Task must be specified");
      }
      IAtsTeamWorkflow teamWf = trd.getTask().getParentTeamWorkflow();
      ArtifactToken derivedFromArt =
         atsApi.getRelationResolver().getRelatedOrSentinel(teamWf, AtsRelationTypes.Derive_From);
      if (derivedFromArt.isValid()) {
         trd.setDerived(true);
         if (!derivedFromArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            trd.getResults().error("Derived-From is not a Team Workflow");
         }
         teamWf = atsApi.getWorkItemService().getTeamWf(derivedFromArt);
         trd.setDerivedFromTeamWf(teamWf);
      } else {
         // Assume task parent owns changes
         trd.setDerived(false);
         trd.setDerivedFromTeamWf(teamWf);
      }
      return trd;
   }

   private void getRelatedChangedArtifact(TaskRelatedData trd, ArtifactId relatedArtifact) {
      final TaskNameData nameData = new TaskNameData(trd.getTask());

      if (nameData.isCdb()) {
         trd.getResults().error("No changed artifact to show for CDB");
         return;
      }
      if (!nameData.isRequirement()) {
         trd.getResults().error("Task is not against changed artifact or is named incorrectly.\n\n" + //
            "Must be \"Code|Test \"<partition>\" for \"<requirement name>\"");
      }

      findHeadArtifact(trd, relatedArtifact, nameData.getAddDetails());
      if (trd.getResults().isErrors()) {
         return;
      }
      if (trd.getHeadArtifact() == null) {
         trd.getResults().error("Corresponding changed artifact can not be found.");
      }
      ArtifactToken latestArt = null;
      if (!atsApi.getStoreService().isDeleted(trd.getHeadArtifact())) {
         latestArt = atsApi.getQueryService().getArtifact(trd.getHeadArtifact(), atsApi.getAtsBranch(),
            DeletionFlag.INCLUDE_DELETED);
         trd.setLatestArt(latestArt);
      }
      return;
   }

   private void findHeadArtifact(TaskRelatedData trd, ArtifactId relatedArtifact, String addDetails) {
      if (trd.getDerivedFromTeamWf() == null) {
         trd.getResults().error("Derived From Team Wf can not be null");
         return;
      }
      boolean foundBranchOrTransId = false;
      BranchId workingBranch = atsApi.getBranchService().getWorkingBranchInWork(trd.getDerivedFromTeamWf());
      if (workingBranch.isValid()) {
         ArtifactToken headArt = atsApi.getQueryService().getArtifact(relatedArtifact, workingBranch);
         trd.setHeadArtifact(headArt);
         foundBranchOrTransId = true;
      }
      TransactionToken transaction = atsApi.getBranchService().getEarliestTransactionId(trd.getDerivedFromTeamWf());
      if (transaction.isValid()) {
         ArtifactToken headArt = atsApi.getQueryService().getHistoricalArtifactOrNull(relatedArtifact, transaction,
            DeletionFlag.INCLUDE_DELETED);
         trd.setHeadArtifact(headArt);
         foundBranchOrTransId = true;
      }
      if (!foundBranchOrTransId) {
         trd.getResults().error("Derived relation but no working branch or transaction found.");
      }
   }

   @Override
   public boolean isAutoGenCodeTestTaskArtifact(IAtsTask task) {
      for (String tag : task.getTags()) {
         if (tag.contains(ChangeReportTasksUtil.DISABLE_CODE_TEST_TASK_GENERATION)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean isAutoGenCodeTestTaskArtifacts(Collection<IAtsTask> tasks) {
      for (IAtsTask task : tasks) {
         if (!isAutoGenCodeTestTaskArtifact(task)) {
            return false;
         }
      }
      return true;
   }

}
