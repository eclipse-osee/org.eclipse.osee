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
package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.data.RelationTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.RelationTypeCacheUpdateResponse.RelationTypeRow;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.RelationTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ClientRelationTypeAccessor extends AbstractClientDataAccessor<RelationType> {

   private final AbstractOseeCache<ArtifactType> artCache;

   public ClientRelationTypeAccessor(IOseeModelFactoryServiceProvider factoryProvider, AbstractOseeCache<ArtifactType> artCache) {
      super(factoryProvider);
      this.artCache = artCache;
   }

   protected RelationTypeFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getRelationTypeFactory();
   }

   @Override
   public void load(IOseeCache<RelationType> cache) throws OseeCoreException {
      artCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected Collection<RelationType> updateCache(IOseeCache<RelationType> cache) throws OseeCoreException {
      List<RelationType> updatedItems = new ArrayList<RelationType>();

      RelationTypeCacheUpdateResponse response =
            requestUpdateMessage(cache, CoreTranslatorId.RELATION_TYPE_CACHE_UPDATE_RESPONSE);

      RelationTypeFactory factory = getFactory();
      for (RelationTypeRow row : response.getRelationTypeRows()) {
         ArtifactType aSideType = artCache.getById(row.getArtifactTypeSideA());
         ArtifactType bSideType = artCache.getById(row.getArtifactTypeSideB());

         RelationType type =
               factory.createOrUpdate(cache, row.getId(), row.getModType(), row.getGuid(), row.getName(),
                     row.getSideAName(), row.getSideBName(), aSideType, bSideType, row.getMultiplicity(),
                     row.getDefaultOrderTypeGuid());
         updatedItems.add(type);
      }
      return updatedItems;
   }
}
