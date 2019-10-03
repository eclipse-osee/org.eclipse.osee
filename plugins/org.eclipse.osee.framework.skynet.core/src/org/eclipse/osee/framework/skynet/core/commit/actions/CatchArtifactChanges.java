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
package org.eclipse.osee.framework.skynet.core.commit.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;

/**
 * @author Megumi Telles
 */
public class CatchArtifactChanges implements CommitAction {

   @Override
   public void runCommitAction(BranchId sourceBranch, BranchId destinationBranch) {

      Collection<Change> changes = new ArrayList<>();
      IOperation operation = ChangeManager.compareTwoBranchesHead(sourceBranch, destinationBranch, changes);
      Operations.executeWorkAndCheckStatus(operation);

      Set<Integer> orphanedArts = new HashSet<>();
      Set<Integer> shouldBeDeletedArts = new HashSet<>();
      for (Change change : changes) {
         Artifact artifactChanged = change.getChangeArtifact();
         if (artifactChanged.isOfType(CoreArtifactTypes.AbstractImplementationDetails,
            CoreArtifactTypes.AbstractSoftwareRequirement)) {
            if (artifactChanged.isDeleted()) {
               checkArtIsFullyDeleted(shouldBeDeletedArts, artifactChanged);
            } else {
               checkForOrphans(orphanedArts, artifactChanged);
            }
         }
      }

      String err = null;
      if (!orphanedArts.isEmpty()) {
         err = String.format("Commit Branch Failed. The following artifacts are orphaned. " //
            + " Please appropriately parent the artifact, then recommit : [%s]\n\n", orphanedArts.toString());
      }
      if (!shouldBeDeletedArts.isEmpty()) {
         String temp =
            String.format("Commit Branch Failed. The following artifacts are deleted but still have relations. " //
               + " Please remove the relations, then recommit : [%s]\n\n", shouldBeDeletedArts.toString());
         err = err == null ? temp : err + temp;
      }

      if (err != null) {
         throw new OseeCoreException(err);
      }

   }

   private void checkArtIsFullyDeleted(Set<Integer> shouldBeDeletedArts, Artifact artifactChanged) {
      Artifact relatedArt = artifactChanged.getRelatedArtifactOrNull(CoreRelationTypes.DefaultHierarchical_Parent);
      if (relatedArt != null) {
         shouldBeDeletedArts.add(artifactChanged.getArtId());
      }
   }

   private void checkForOrphans(Set<Integer> orphanedArts, Artifact artifactChanged) {
      Artifact relatedArt = artifactChanged.getRelatedArtifactOrNull(CoreRelationTypes.DefaultHierarchical_Parent);
      if (relatedArt == null) {
         orphanedArts.add(artifactChanged.getArtId());
      }
   }

}
