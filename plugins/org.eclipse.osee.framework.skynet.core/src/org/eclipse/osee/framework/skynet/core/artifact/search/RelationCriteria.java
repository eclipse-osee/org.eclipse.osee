/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.client.QueryBuilder;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Ryan D. Brooks
 */
public class RelationCriteria implements ArtifactSearchCriteria {
   private final RelationTypeToken relationType;
   private final RelationSide relationSide;
   private final ArtifactId artifactId;

   /**
    * Constructor for search criteria that follows the relation link ending on the given side
    *
    * @param relationEnum the side to start following the link from
    */
   public RelationCriteria(RelationTypeSide relationEnum) {
      this(relationEnum, relationEnum.getSide());
   }

   public RelationCriteria(RelationTypeToken relationType) {
      this(relationType, null);
   }

   public RelationCriteria(RelationTypeToken relationType, RelationSide relationSide) {
      this(ArtifactId.SENTINEL, relationType, relationSide);
   }

   public RelationCriteria(ArtifactId artifactId, RelationTypeToken relationType, RelationSide relationSide) {
      this.artifactId = artifactId;
      this.relationType = relationType;
      this.relationSide = relationSide;
   }

   @Override
   public void addToQueryBuilder(QueryBuilder builder) {
      if (artifactId.isValid()) {
         RelationTypeSide rts = RelationTypeSide.create(relationType, relationSide);
         builder.andRelatedTo(rts, artifactId);
      } else if (relationSide == null) {
         builder.andExists(relationType);
      } else {
         RelationTypeSide rts = RelationTypeSide.create(relationType, relationSide);
         builder.andExists(rts);
      }
   }
}