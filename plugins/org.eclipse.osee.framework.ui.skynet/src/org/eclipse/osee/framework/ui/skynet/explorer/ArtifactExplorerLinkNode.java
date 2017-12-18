/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Robert A. Fisher
 */
public class ArtifactExplorerLinkNode {
   private final Artifact artifact;
   private final RelationType relationType;
   private final boolean parentIsOnSideA;

   private final int artifactId;
   private final String relationTypeName;

   public ArtifactExplorerLinkNode(Artifact artifact, RelationType relationType, boolean parentIsOnSideA) {
      super();
      this.artifact = artifact;
      this.relationType = relationType;
      this.parentIsOnSideA = parentIsOnSideA;

      // Used for simple equals/hashcode impl
      this.artifactId = artifact.getArtId();
      this.relationTypeName = relationType.getName();
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public RelationType getRelationType() {
      return relationType;
   }

   public boolean isParentIsOnSideA() {
      return parentIsOnSideA;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + artifactId;
      result = prime * result + (parentIsOnSideA ? 1231 : 1237);
      result = prime * result + (relationTypeName == null ? 0 : relationTypeName.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      ArtifactExplorerLinkNode other = (ArtifactExplorerLinkNode) obj;
      if (artifactId != other.artifactId) {
         return false;
      }
      if (parentIsOnSideA != other.parentIsOnSideA) {
         return false;
      }
      if (relationTypeName == null) {
         if (other.relationTypeName != null) {
            return false;
         }
      } else if (!relationTypeName.equals(other.relationTypeName)) {
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

   public int getArtifactId() {
      return artifactId;
   }

   public String getRelationTypeName() {
      return relationTypeName;
   }
}