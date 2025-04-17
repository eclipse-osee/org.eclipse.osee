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

package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.core.ds.Criteria;

public class CriteriaRelatedToThruRels extends Criteria {
   private final LinkedList<RelationTypeSide> relationTypeSides;
   private final ArtifactId artifactId;

   public CriteriaRelatedToThruRels(LinkedList<RelationTypeSide> relationTypeSides, ArtifactId artifactId) {
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
}