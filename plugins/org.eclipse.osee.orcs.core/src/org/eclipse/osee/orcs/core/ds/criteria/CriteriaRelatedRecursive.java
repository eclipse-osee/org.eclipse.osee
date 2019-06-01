/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.orcs.core.ds.Criteria;

/**
 * @author Ryan D. Brooks
 */
public class CriteriaRelatedRecursive extends Criteria {
   private final RelationTypeToken relationType;
   private final ArtifactId startArtifact;

   public CriteriaRelatedRecursive(RelationTypeToken relationType, ArtifactId startArtifact) {
      this.relationType = relationType;
      this.startArtifact = startArtifact;
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public ArtifactId getStartArtifact() {
      return startArtifact;
   }

   @Override
   public String toString() {
      return "<CriteriaFollowRelation startArtifact=" + startArtifact.getIdString() + " relation type: " + relationType.toStringWithId() + ">";
   }
}