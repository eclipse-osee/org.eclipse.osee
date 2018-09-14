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
package org.eclipse.osee.ats.workflow.task.related;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.task.TaskNameData;
import org.eclipse.osee.ats.api.task.related.TaskRelatedData;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.branch.AtsBranchManager;
import org.eclipse.osee.ats.core.task.related.AbstractAtsTaskRelatedService;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskRelatedService extends AbstractAtsTaskRelatedService {

   public AtsTaskRelatedService(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public TaskRelatedData getRelatedRequirementArtifactFromChangeReport(IAtsTeamWorkflow derivedFromTeamWf, IAtsTask task) {
      ChangeData changeData = AtsBranchManager.getChangeDataFromEarliestTransactionId(
         (TeamWorkFlowArtifact) derivedFromTeamWf.getStoreObject());
      return getRelatedRequirementArtifact(task, changeData);
   }

   private TaskRelatedData getRelatedRequirementArtifact(IAtsTask task, ChangeData changeData) {
      final TaskNameData data = new TaskNameData(task);

      if (data.isCdb()) {
         return new TaskRelatedData(new Result("No requirement to show for CDB"));
      }
      if (!data.isRequirement()) {
         return new TaskRelatedData(new Result(
            "Task is not against artifact or is named incorrectly.\n\n" + "Must be \"Code|Test \"<partition>\" for \"<requirement name>\""));
      }

      Artifact headArtifact = findHeadArtifact(changeData, data.getReqName(), data.getAddDetails());
      if (headArtifact == null) {
         return new TaskRelatedData(new Result("Corresponding requirement can not be found."));
      }
      Artifact latestArt = null;
      if (!headArtifact.isDeleted()) {
         latestArt = ArtifactQuery.getArtifactFromToken(headArtifact, DeletionFlag.INCLUDE_DELETED);
      }
      return new TaskRelatedData(headArtifact.isDeleted(), headArtifact, latestArt, Result.TrueResult);
   }

   public static Artifact findHeadArtifact(ChangeData changeData, String name, String appendedStr) {
      Artifact headArtifact = null;
      for (Artifact art : changeData.getArtifacts(KindType.ArtifactOrRelation, ModificationType.NEW,
         ModificationType.MODIFIED)) {
         if (Strings.isValid(appendedStr) && appendedStr.equals(IMPL_DETAILS) && //
            art.isOfType(CoreArtifactTypes.AbstractImplementationDetails)) {
            if (name.contains(art.getName())) {
               headArtifact = art;
               break;
            }
         } else if (Strings.isInValid(appendedStr) && //
            !art.isOfType(CoreArtifactTypes.AbstractImplementationDetails) && //
            art.getName().contains(name)) {
            headArtifact = art;
            break;
         }
      }
      if (headArtifact == null) {
         for (Artifact art : changeData.getArtifacts(KindType.ArtifactOrRelation, ModificationType.DELETED)) {
            if (appendedStr.equals(DELETED)) {
               if (name.contains(art.getName())) {
                  headArtifact = art;
                  break;
               }
            }
         }
      }
      return headArtifact;
   }

}
