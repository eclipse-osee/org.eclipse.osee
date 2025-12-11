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

/**
 * @author Ryan D. Brooks
 */
public class CriteriaRelatedRecursive extends org.eclipse.osee.orcs.search.ds.RelationTypeCriteria<RelationTypeToken> {
   private final ArtifactId startArtifact;
   private final boolean upstream;

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
}