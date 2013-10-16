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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeAdapter;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskToChangedArtifactReferenceAttributeAdapter implements AttributeAdapter<Artifact> {

   @Override
   public Collection<IAttributeType> getSupportedTypes() {
      return Arrays.asList(AtsAttributeTypes.TaskToChangedArtifactReference);
   }

   @Override
   public Artifact adapt(Attribute<?> attribute, Identity<String> identity) throws OseeCoreException {
      Artifact retArt = null;

      String guid = identity.getGuid();
      if (GUID.isValid(guid)) {
         Artifact artifact = attribute.getArtifact();
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArt = (TaskArtifact) artifact;
            TeamWorkFlowArtifact parentTeamWf = taskArt.getParentTeamWorkflow();
            Artifact derivedArt = null;
            try {
               derivedArt = parentTeamWf.getRelatedArtifact(AtsRelationTypes.Derive_From);
            } catch (ArtifactDoesNotExist e) {
               //derivedArt = (remains) null;
            }
            if (derivedArt != null && derivedArt instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact derivedTeamWf = (TeamWorkFlowArtifact) derivedArt;
               // First, attempt to get from Working Branch if still exists
               Branch workingBranch = AtsBranchManagerCore.getWorkingBranch(derivedTeamWf);
               if (workingBranch != null) {
                  retArt = ArtifactQuery.getArtifactFromId(guid, workingBranch, DeletionFlag.EXCLUDE_DELETED);
               } else {
                  // Else get from first commit transaction
                  // NOTE: Each workflow has it's own commit in parallel dev
                  TransactionRecord earliestTransactionId =
                     AtsBranchManagerCore.getEarliestTransactionId(derivedTeamWf);
                  if (earliestTransactionId != null) {
                     retArt =
                        ArtifactQuery.getHistoricalArtifactFromId(guid, earliestTransactionId,
                           DeletionFlag.EXCLUDE_DELETED);
                  }
               }
            }

         }
      }
      return retArt;
   }

}