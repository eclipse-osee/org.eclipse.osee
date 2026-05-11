/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.orcs.search.ds.RelationTypeCriteria;

/**
 * @author Ryan D. Brooks
 */
public class CriteriaRelatedRecursive extends RelationTypeCriteria {
   private ArtifactId startArtifact;
   private boolean upstream;

   public CriteriaRelatedRecursive() {
      // for jax-rs
      super(RelationTypeToken.SENTINEL);
   }

   public CriteriaRelatedRecursive(RelationTypeToken relationType, ArtifactId startArtifact) {
      super(relationType);
      this.startArtifact = startArtifact;
      this.upstream = false;
   }

   public CriteriaRelatedRecursive(RelationTypeToken relationType, ArtifactId startArtifact, boolean upstream) {
      super(relationType);
      this.startArtifact = startArtifact;
      this.upstream = upstream;
   }

   public ArtifactId getStartArtifact() {
      return startArtifact;
   }

   public boolean isUpstream() {
      return upstream;
   }

   public void setStartArtifact(ArtifactId startArtifact) {
      this.startArtifact = startArtifact;
   }

   public void setUpstream(boolean upstream) {
      this.upstream = upstream;
   }
}