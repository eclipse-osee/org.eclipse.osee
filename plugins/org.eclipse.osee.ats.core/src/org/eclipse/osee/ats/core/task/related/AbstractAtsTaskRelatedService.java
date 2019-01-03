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
package org.eclipse.osee.ats.core.task.related;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.TaskNameData;
import org.eclipse.osee.ats.api.task.related.IAtsTaskRelatedService;
import org.eclipse.osee.ats.api.task.related.TaskRelatedData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsTaskRelatedService implements IAtsTaskRelatedService {

   protected final AtsApi atsApi;

   public AbstractAtsTaskRelatedService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * Attempt first to retrieve TaskRelatedData from AtsAttributeTypes.TaskToChangedArtifactReference and branch. Else
    * retrieve from change report.
    */
   public TaskRelatedData getRelatedRequirementArtifact(IAtsTask task) {
      IAtsTeamWorkflow derivedFromTeamWf = getDefivedFromTeamWf(task);
      if (derivedFromTeamWf == null) {
         return new TaskRelatedData(new Result("Requirement Team Workflow can't be found"));
      }

      ArtifactId artifact = atsApi.getAttributeResolver().getSoleAttributeValue(task,
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
      if (artifact.isValid()) {
         return getRelatedRequirementArtifact(task, derivedFromTeamWf, artifact);
      }

      return getRelatedRequirementArtifactFromChangeReport(derivedFromTeamWf, task);
   }

   /**
    * @return team workflow that owns the branch that this task as generated from</br>
    * If Requirements code/test task, it is the requirement workflow</br>
    * If Code code/test task, it's the code workflow</br>
    * They should be related by the Derived relation
    */
   @Override
   public IAtsTeamWorkflow getDefivedFromTeamWf(IAtsTask task) {
      IAtsTeamWorkflow teamWf = task.getParentTeamWorkflow();
      ArtifactToken derivedFromArt =
         atsApi.getRelationResolver().getRelatedOrSentinel(teamWf, AtsRelationTypes.Derive_From);
      if (derivedFromArt.isInvalid()) {
         // For OSEE CPCRs, the Code Workflow can own the branch where changes were made
         if (isCodeWorkflow(teamWf)) {
            return teamWf;
         }
         throw new OseeStateException("No derived artifact for generated task %s", task.toStringWithId());
      } else {
         Conditions.assertTrue(atsApi.getStoreService().isOfType(derivedFromArt, AtsArtifactTypes.TeamWorkflow),
            "derivedFromArt not Team Workflow");
         return atsApi.getWorkItemService().getTeamWf(derivedFromArt);
      }
   }

   @Override
   public boolean isCodeWorkflow(IAtsTeamWorkflow teamWf) {
      WorkType lbaWorkType = atsApi.getProgramService().getWorkType(teamWf);
      if (!isRequirementsWorkflow(teamWf) && lbaWorkType.equals(WorkType.Code)) {
         return true;
      }
      return false;
   }

   @Override
   public boolean isRequirementsWorkflow(IAtsTeamWorkflow teamWf) {
      WorkType lbaWorkType = atsApi.getProgramService().getWorkType(teamWf);
      if (lbaWorkType.equals(WorkType.Code)) {
         return true;
      }
      return false;
   }

   @Override
   public TaskRelatedData getRelatedRequirementArtifact(IAtsTask task, IAtsTeamWorkflow derivedfromTeamWf, ArtifactId relatedArtifact) {
      final TaskNameData data = new TaskNameData(task);

      if (data.isCdb()) {
         return new TaskRelatedData(new Result("No requirement to show for CDB"));
      }
      if (!data.isRequirement()) {
         new TaskRelatedData(new Result(
            "Task is not against requirement or is named incorrectly.\n\n" + "Must be \"Code|Test \"<partition>\" for \"<requirement name>\""));
      }

      ArtifactToken headArtifact = findHeadArtifact(derivedfromTeamWf, relatedArtifact, data.getAddDetails());
      if (headArtifact == null) {
         return new TaskRelatedData(new Result("Corresponding requirement can not be found."));
      }
      ArtifactToken latestArt = null;
      if (!atsApi.getStoreService().isDeleted(headArtifact)) {
         latestArt =
            atsApi.getQueryService().getArtifact(headArtifact, atsApi.getAtsBranch(), DeletionFlag.INCLUDE_DELETED);
      }
      return new TaskRelatedData(atsApi.getStoreService().isDeleted(headArtifact), headArtifact, latestArt,
         Result.TrueResult);
   }

   @Override
   public ArtifactToken findHeadArtifact(IAtsTeamWorkflow derivedfromTeamWf, ArtifactId relatedArtifact, String addDetails) {
      BranchId workingBranch = atsApi.getBranchService().getWorkingBranchInWork(derivedfromTeamWf);
      if (workingBranch.isValid()) {
         return atsApi.getQueryService().getArtifact(relatedArtifact, workingBranch);
      }
      TransactionToken transaction = atsApi.getBranchService().getEarliestTransactionId(derivedfromTeamWf);
      if (transaction.isValid()) {
         return atsApi.getQueryService().getHistoricalArtifactOrNull(relatedArtifact, transaction,
            DeletionFlag.INCLUDE_DELETED);
      }
      return null;
   }

}
