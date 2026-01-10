/*********************************************************************
 * Copyright (c) 2012 Boeing
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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.search.ds.RelationTypeSideCriteria;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaRelatedTo extends RelationTypeSideCriteria {
   private Collection<ArtifactId> artifactIds = new ArrayList<>();
   private ArtifactId artifactId = ArtifactId.SENTINEL;

   public CriteriaRelatedTo() {
      // for jax-rs
      super(RelationTypeSide.SENTINEL);
   }

   public CriteriaRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      super(relationTypeSide);
      this.artifactId = ArtifactId.valueOf(artifactId.getId()); // for jax-rs
   }

   public CriteriaRelatedTo(RelationTypeSide relationTypeSide, Collection<? extends ArtifactId> artifactIds) {
      super(relationTypeSide);
      this.artifactIds = new ArrayList<>();
      // for jax-rs
      if (artifactIds != null) {
         for (ArtifactId artId : artifactIds) {
            this.artifactIds.add(ArtifactId.valueOf(artId.getId()));
         }
      }
   }

   public boolean hasMultipleIds() {
      return artifactId.isInvalid();
   }

   @Override
   public String toString() {
      return "CriteriaRelatedTo [relationTypeSide=" + getRelationTypeSide() + ", artifactIds=" + artifactIds + "]";
   }

   public Collection<ArtifactId> getArtifactIds() {
      return artifactIds;
   }

   public void setArtifactIds(Collection<ArtifactId> artifactIds) {
      this.artifactIds = new ArrayList<>();
      // for jax-rs
      for (ArtifactId artId : artifactIds) {
         this.artifactIds.add(ArtifactId.valueOf(artId));
      }
   }

   public ArtifactId getArtifactId() {
      return artifactId;
   }

   public void setArtifactId(ArtifactId artifactId) {
      // for jax-rs
      this.artifactId = ArtifactId.valueOf(artifactId.getId());
   }
}