/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.search.ds.Criteria;
import org.eclipse.osee.orcs.search.ds.Options;

/**
 * @author Luciano Vaglienti
 */
public class CriteriaNotRelatedTo extends Criteria {
   private final ArtifactId artifactId;
   private final RelationTypeSide relationType;

   public RelationTypeSide getType() {
      return relationType;
   }

   public CriteriaNotRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      this.relationType = relationTypeSide;
      this.artifactId = artifactId;
   }

   public ArtifactId getId() {
      return artifactId;
   }

   @Override
   public String toString() {
      return "CriteriaRelatedTo [relationTypeSide=" + getType() + ", artifactId=" + artifactId + "]";
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkValid(relationType, "relation type");
   }
}