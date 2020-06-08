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

package org.eclipse.osee.framework.skynet.core.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {

   private static IOseeCachingService getCacheService() {
      return ServiceUtil.getOseeCacheService();
   }

   public static AbstractOseeCache<RelationType> getCache() {
      return getCacheService().getRelationTypeCache();
   }

   public static List<RelationTypeToken> getValidTypes(ArtifactTypeToken artifactType, BranchId branch) {
      Collection<RelationType> relationTypes = getAllTypes();
      List<RelationTypeToken> validRelationTypes = new ArrayList<>();
      for (RelationType relationType : relationTypes) {
         int sideAMax = getRelationSideMax(relationType, artifactType, RelationSide.SIDE_A);
         int sideBMax = getRelationSideMax(relationType, artifactType, RelationSide.SIDE_B);
         boolean onSideA = sideBMax > 0;
         boolean onSideB = sideAMax > 0;
         if (onSideA || onSideB) {
            validRelationTypes.add(relationType);
         }
      }
      return validRelationTypes;
   }

   public static int getRelationSideMax(RelationTypeToken relType, ArtifactTypeToken artifactType, RelationSide relationSide) {
      int toReturn = 0;
      if (relType.isArtifactTypeAllowed(relationSide, artifactType)) {
         toReturn = relType.getMultiplicity().getLimit(relationSide);
      }
      return toReturn;
   }

   /**
    * @return all the relation types that are valid for the given branch
    */
   public static Collection<RelationType> getValidTypes(BranchId branch) {
      return getAllTypes();
   }

   /**
    * @return all Relation types
    */
   public static Collection<RelationType> getAllTypes() {
      return getCache().getAll();
   }

   public static RelationType getType(RelationTypeToken relationType) {
      if (relationType instanceof RelationType) {
         return (RelationType) relationType;
      }

      return getCache().get(relationType);
   }

   public static RelationTypeToken getType(String typeName) {
      return getCache().getByName(typeName);
   }
}