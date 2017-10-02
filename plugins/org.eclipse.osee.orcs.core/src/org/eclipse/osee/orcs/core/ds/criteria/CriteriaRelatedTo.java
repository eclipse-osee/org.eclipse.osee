/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaRelatedTo extends Criteria {
   private final RelationTypeSide relationTypeSide;
   private final Collection<? extends ArtifactId> artifactIds;
   private final ArtifactId artifactId;

   public CriteriaRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      this.relationTypeSide = relationTypeSide;
      this.artifactId = artifactId;
      this.artifactIds = null;
   }

   public CriteriaRelatedTo(RelationTypeSide relationTypeSide, Collection<? extends ArtifactId> artifactIds) {
      this.relationTypeSide = relationTypeSide;
      this.artifactId = null;
      this.artifactIds = artifactIds;
   }

   public RelationTypeSide getType() {
      return relationTypeSide;
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
   public void checkValid(Options options)  {
      Conditions.checkNotNull(getType(), "relation type side");
   }

   @Override
   public String toString() {
      return "CriteriaRelatedTo [relationTypeSide=" + relationTypeSide + ", artifactIds=" + artifactIds + "]";
   }

}
