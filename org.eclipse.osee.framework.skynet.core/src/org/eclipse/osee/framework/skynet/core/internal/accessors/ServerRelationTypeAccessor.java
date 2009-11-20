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

import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.RelationTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerRelationTypeAccessor extends AbstractServerDataAccessor<RelationType> {

   private final AbstractOseeCache<ArtifactType> artCache;

   public ServerRelationTypeAccessor(IOseeModelFactoryServiceProvider factoryProvider, AbstractOseeCache<ArtifactType> artCache) {
      super(factoryProvider);
      this.artCache = artCache;
   }

   protected RelationTypeFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getRelationTypeFactory();
   }

   @Override
   public void load(AbstractOseeCache<RelationType> cache) throws OseeCoreException {
      artCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected void updateCache(AbstractOseeCache<RelationType> cache, CacheUpdateResponse<RelationType> updateResponse) throws OseeCoreException {
      for (RelationType updated : updateResponse.getItems()) {
         ArtifactType aSideType = artCache.getByGuid(updated.getArtifactTypeSideA().getGuid());
         ArtifactType bSideType = artCache.getByGuid(updated.getArtifactTypeSideB().getGuid());
         getFactory().createOrUpdate(cache, updated.getId(), updated.getModificationType(), updated.getGuid(),
               updated.getName(), updated.getSideAName(), updated.getSideBName(), aSideType, bSideType,
               updated.getMultiplicity(), updated.getDefaultOrderTypeGuid());

      }
   }
}
