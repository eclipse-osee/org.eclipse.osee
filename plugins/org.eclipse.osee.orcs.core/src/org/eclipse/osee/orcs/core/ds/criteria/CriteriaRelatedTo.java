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

package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.core.ds.RelationTypeCriteria;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaRelatedTo extends RelationTypeCriteria<RelationTypeSide> {
   private final Collection<? extends ArtifactId> artifactIds;
   private final ArtifactId artifactId;

   public CriteriaRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      super(relationTypeSide);
      this.artifactId = artifactId;
      this.artifactIds = null;
   }

   public CriteriaRelatedTo(RelationTypeSide relationTypeSide, Collection<? extends ArtifactId> artifactIds) {
      super(relationTypeSide);
      this.artifactId = null;
      this.artifactIds = artifactIds;
   }

   public Collection<? extends ArtifactId> getIds() {
      return artifactIds;
   }

   public ArtifactId getId() {
      return artifactId;
   }

   public boolean hasMultipleIds() {
      return artifactId == null;
   }

   @Override
   public String toString() {
      return "CriteriaRelatedTo [relationTypeSide=" + getType() + ", artifactIds=" + artifactIds + "]";
   }
}