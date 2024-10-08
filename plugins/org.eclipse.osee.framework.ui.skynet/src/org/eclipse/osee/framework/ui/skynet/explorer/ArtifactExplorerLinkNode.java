/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Robert A. Fisher
 */
public class ArtifactExplorerLinkNode {
   private final Artifact artifact;
   private final RelationTypeToken relationType;
   private final boolean parentIsOnSideA;

   public ArtifactExplorerLinkNode(Artifact artifact, RelationTypeToken relationType, boolean parentIsOnSideA) {
      this.artifact = artifact;
      this.relationType = relationType;
      this.parentIsOnSideA = parentIsOnSideA;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public boolean isParentIsOnSideA() {
      return parentIsOnSideA;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + artifact.hashCode();
      result = prime * result + (parentIsOnSideA ? 1231 : 1237);
      result = prime * result + relationType.hashCode();
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ArtifactExplorerLinkNode)) {
         return false;
      }
      ArtifactExplorerLinkNode other = (ArtifactExplorerLinkNode) obj;
      if (artifact.notEqual(other.artifact)) {
         return false;
      }
      if (parentIsOnSideA != other.parentIsOnSideA) {
         return false;
      }
      if (relationType.notEqual(other.relationType)) {
         return false;
      }
      return true;
   }

   public List<Artifact> getOppositeArtifacts() {
      List<Artifact> oppositeArtifacts = new ArrayList<>();
      RelationTypeSide relationSide =
         new RelationTypeSide(relationType, parentIsOnSideA ? RelationSide.SIDE_B : RelationSide.SIDE_A);
      oppositeArtifacts.addAll(artifact.getRelatedArtifacts(relationSide, DeletionFlag.EXCLUDE_DELETED));
      return oppositeArtifacts;
   }
}