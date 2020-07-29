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
import org.eclipse.osee.ats.api.task.IAtsTaskProvider;
import org.eclipse.osee.ats.api.task.related.DerivedFromTaskData;
import org.eclipse.osee.ats.api.task.related.IAtsTaskRelatedService;
import org.eclipse.osee.ats.api.task.related.IAutoGenTaskData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.internal.AtsTaskProviderCollector;
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
   public DerivedFromTaskData populateDerivedFromTaskData(DerivedFromTaskData trd) {
      boolean deleted = atsApi.getAttributeResolver().getSoleAttributeValue(trd.getTask(),
         AtsAttributeTypes.TaskToChangedArtifactDeleted, false);
      trd.setDeleted(deleted);
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

   private DerivedFromTaskData getRelatedChangedArtifact(DerivedFromTaskData trd) {
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
   public DerivedFromTaskData getDerivedTeamWf(DerivedFromTaskData trd) {
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

   private void getRelatedChangedArtifact(DerivedFromTaskData dftd, ArtifactId relatedArtifact) {
      final IAutoGenTaskData nameData = atsApi.getTaskRelatedService().getAutoGenTaskData(dftd.getTask());
      if (nameData == null) {
         dftd.getResults().error("Can't retrieve Auto Gen Task Data");
         return;
      }
      if (nameData.isNoChangedArtifact()) {
         dftd.getResults().error("No changed artifact to show");
         return;
      }
      if (!nameData.hasRelatedArt()) {
         dftd.getResults().error("Task is not against changed artifact or is named incorrectly.\n\n" + //
            "Must be \"Code|Test \"<partition>\" for \"<requirement name>\"");
      }

      findHeadArtifact(dftd, relatedArtifact, nameData.getAddDetails());
      if (dftd.getResults().isErrors()) {
         return;
      }
      if (dftd.getHeadArtifact() == null) {
         dftd.getResults().error("Corresponding changed artifact can not be found.");
      }
      ArtifactToken latestArt = null;
      if (!atsApi.getStoreService().isDeleted(dftd.getHeadArtifact())) {
         latestArt = atsApi.getQueryService().getArtifact(dftd.getHeadArtifact(), atsApi.getAtsBranch(),
            DeletionFlag.INCLUDE_DELETED);
         dftd.setLatestArt(latestArt);
      }
      return;
   }

   private void findHeadArtifact(DerivedFromTaskData trd, ArtifactId relatedArtifact, String addDetails) {
      if (trd.getDerivedFromTeamWf() == null) {
         trd.getResults().error("Derived From Team Wf can not be null");
         return;
      }
      boolean foundBranchOrTransId = false;
      BranchId workingBranch = atsApi.getBranchService().getWorkingBranchInWork(trd.getDerivedFromTeamWf());
      if (workingBranch.isValid()) {
         ArtifactToken headArt =
            atsApi.getQueryService().getArtifact(relatedArtifact, workingBranch, DeletionFlag.INCLUDE_DELETED);
         trd.setHeadArtifact(headArt);
         foundBranchOrTransId = true;
      }
      if (!foundBranchOrTransId) {
         TransactionToken transaction = atsApi.getBranchService().getEarliestTransactionId(trd.getDerivedFromTeamWf());
         if (transaction.isValid()) {
            ArtifactToken headArt = atsApi.getQueryService().getHistoricalArtifactOrNull(relatedArtifact, transaction,
               DeletionFlag.INCLUDE_DELETED);
            trd.setHeadArtifact(headArt);
            foundBranchOrTransId = true;
         }
      }
      if (!foundBranchOrTransId) {
         trd.getResults().error("Derived relation but no working branch or transaction found.");
      }
   }

   @Override
   public boolean isAutoGenTask(IAtsTask task) {
      for (IAtsTaskProvider provider : AtsTaskProviderCollector.getTaskProviders()) {
         if (provider.isAutoGen(task)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isAutoGenTasks(Collection<IAtsTask> tasks) {
      for (IAtsTask task : tasks) {
         if (!isAutoGenTask(task)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean isAutoGenChangeReportRelatedTasks(Collection<IAtsTask> tasks) {
      for (IAtsTask task : tasks) {
         if (!isAutoGenChangeReportRelatedTask(task)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean isAutoGenChangeReportRelatedTask(IAtsTask task) {
      return isAutoGenTask(task) && atsApi.getAttributeResolver().getAttributeCount(task,
         AtsAttributeTypes.TaskToChangedArtifactReference) > 0;
   }

}
