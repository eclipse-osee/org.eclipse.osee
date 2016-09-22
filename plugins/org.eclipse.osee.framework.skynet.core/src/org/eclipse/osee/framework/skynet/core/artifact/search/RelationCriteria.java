/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;

/**
 * @author Ryan D. Brooks
 */
public class RelationCriteria implements ArtifactSearchCriteria {
   private final IRelationType relationType;
   private final RelationSide relationSide;
   private final ArtifactId artifactId;

   /**
    * Constructor for search criteria that follows the relation link ending on the given side
    *
    * @param relationEnum the side to start following the link from
    */
   public RelationCriteria(IRelationTypeSide relationEnum) {
      this(relationEnum, relationEnum.getSide());
   }

   public RelationCriteria(IRelationType relationType) {
      this(relationType, null);
   }

   public RelationCriteria(IRelationType relationType, RelationSide relationSide) {
      this(ArtifactId.SENTINEL, relationType, relationSide);
   }

   public RelationCriteria(ArtifactId artifactId, IRelationType relationType, RelationSide relationSide) {
      this.artifactId = artifactId;
      this.relationType = relationType;
      this.relationSide = relationSide;
   }

   @Override
   public void addToQueryBuilder(QueryBuilder builder) throws OseeCoreException {
      if (artifactId.isValid()) {
         IRelationTypeSide rts =
            TokenFactory.createRelationTypeSide(relationSide, relationType.getId(), Strings.EMPTY_STRING);
         builder.andRelatedTo(rts, artifactId);
      } else if (relationSide == null) {
         builder.andExists(relationType);
      } else {
         IRelationTypeSide rts =
            TokenFactory.createRelationTypeSide(relationSide, relationType.getId(), "SearchRelationTypeSide");
         builder.andExists(rts);
      }
   }
}