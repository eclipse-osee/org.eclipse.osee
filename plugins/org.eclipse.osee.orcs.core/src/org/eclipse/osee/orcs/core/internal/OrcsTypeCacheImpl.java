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
package org.eclipse.osee.orcs.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.orcs.DataStoreTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTypeCacheImpl implements DataStoreTypeCache {

   private IOseeCachingService cachingService;

   public OrcsTypeCacheImpl() {
      super();
   }

   public void setCache(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   @Override
   public AttributeTypeCache getAttributeTypeCache() {
      return cachingService.getAttributeTypeCache();
   }

   @Override
   public ArtifactTypeCache getArtifactTypeCache() {
      return cachingService.getArtifactTypeCache();
   }

   @Override
   public RelationTypeCache getRelationTypeCache() {
      return cachingService.getRelationTypeCache();
   }

   @Override
   public List<RelationType> getValidRelationTypes(IArtifactType artifactType, IOseeBranch branch) throws OseeCoreException {
      Collection<RelationType> relationTypes = cachingService.getRelationTypeCache().getAll();
      List<RelationType> validRelationTypes = new ArrayList<RelationType>();
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

   @Override
   public int getRelationSideMax(RelationType relationType, IArtifactType artifactType, RelationSide relationSide) throws OseeCoreException {
      int toReturn = 0;
      ArtifactType type = getArtifactTypeCache().get(artifactType);
      if (relationType.isArtifactTypeAllowed(relationSide, type)) {
         toReturn = relationType.getMultiplicity().getLimit(relationSide);
      }
      return toReturn;
   }

}
