/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.orcs.search.ds.criteria;

import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.search.ds.Criteria;

public class CriteriaRelatedToThroughRels extends Criteria {
   private LinkedList<RelationTypeSide> relationTypeSides;
   private ArtifactId artifactId;

   public CriteriaRelatedToThroughRels() {
      // for jax-rs
   }

   public CriteriaRelatedToThroughRels(LinkedList<RelationTypeSide> relationTypeSides, ArtifactId artifactId) {
      this.relationTypeSides = relationTypeSides;
      this.artifactId = artifactId;
   }

   public ArtifactId getId() {
      return artifactId;
   }

   public LinkedList<RelationTypeSide> getRelationTypeSides() {
      return relationTypeSides;
   }

   @Override
   public String toString() {
      return "CriteriaRelatedToThruRels [relationTypeSide=" + getRelationTypeSides().toString() + ", artifactId=" + getId() + "]";
   }

   public ArtifactId getArtifactId() {
      return artifactId;
   }

   public void setArtifactId(ArtifactId artifactId) {
      this.artifactId = artifactId;
   }

   public void setRelationTypeSides(LinkedList<RelationTypeSide> relationTypeSides) {
      this.relationTypeSides = relationTypeSides;
   }
}
