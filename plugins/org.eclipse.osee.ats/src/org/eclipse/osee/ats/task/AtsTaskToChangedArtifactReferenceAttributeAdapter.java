/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeAdapter;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskToChangedArtifactReferenceAttributeAdapter implements AttributeAdapter<Artifact> {

   private static final List<AttributeTypeId> TASK_CHANGED_ARTIFACT_REFERENCE_ATTR =
      Arrays.asList(AtsAttributeTypes.TaskToChangedArtifactReference);

   @Override
   public Collection<AttributeTypeId> getSupportedTypes() {
      return TASK_CHANGED_ARTIFACT_REFERENCE_ATTR;
   }

   @Override
   public Artifact adapt(Attribute<?> attribute, Id identity) throws OseeCoreException {
      Artifact retArt = null;

      int uuid = identity.getId() <= 0 ? 0 : identity.getId().intValue();
      if (uuid > 0) {
         Artifact artifact = attribute.getArtifact();
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArt = (TaskArtifact) artifact;
            TeamWorkFlowArtifact parentTeamWf = taskArt.getParentTeamWorkflow();
            Artifact derivedArt = parentTeamWf.getRelatedArtifactOrNull(AtsRelationTypes.Derive_From);
            if (derivedArt != null && derivedArt instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact derivedTeamWf = (TeamWorkFlowArtifact) derivedArt;
               // First, attempt to get from Working Branch if still exists
               BranchId workingBranch = AtsClientService.get().getBranchService().getWorkingBranch(derivedTeamWf);
               if (workingBranch != null && branchIsInWork(workingBranch)) {
                  retArt = ArtifactQuery.getArtifactFromIdOrNull(uuid, workingBranch, DeletionFlag.INCLUDE_DELETED);
               } else {
                  // Else get from first commit transaction
                  // NOTE: Each workflow has it's own commit in parallel dev
                  TransactionToken earliestTransactionId =
                     AtsClientService.get().getBranchService().getEarliestTransactionId(derivedTeamWf);
                  if (earliestTransactionId != null) {
                     retArt = ArtifactQuery.getHistoricalArtifactFromIdOrNull(uuid, earliestTransactionId,
                        DeletionFlag.INCLUDE_DELETED);
                  }
               }
            }

         }
      }
      return retArt;
   }

   private boolean branchIsInWork(BranchId workingBranch) {
      BranchState state = BranchManager.getState(workingBranch);
      return (state.isCreated() || state.isModified());
   }

}