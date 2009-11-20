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
import org.eclipse.osee.framework.core.model.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerArtifactTypeAccessor extends AbstractServerDataAccessor<ArtifactType> {

   private final AbstractOseeCache<AttributeType> attrCache;

   public ServerArtifactTypeAccessor(IOseeModelFactoryServiceProvider factoryProvider, AbstractOseeCache<AttributeType> attrCache) {
      super(factoryProvider);
      this.attrCache = attrCache;
   }

   protected ArtifactTypeFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getArtifactTypeFactory();
   }

   @Override
   public void load(AbstractOseeCache<ArtifactType> cache) throws OseeCoreException {
      attrCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected void updateCache(AbstractOseeCache<ArtifactType> cache, CacheUpdateResponse<ArtifactType> updateResponse) throws OseeCoreException {
      for (ArtifactType updated : updateResponse.getItems()) {
         ArtifactType type =
               getFactory().createOrUpdate(cache, updated.getId(), updated.getModificationType(), updated.getGuid(),
                     updated.isAbstract(), updated.getName());
      }

      for (ArtifactType updated : updateResponse.getItems()) {
         //         for (Entry<Branch, Collection<AttributeType>> entry : updated.getLocalAttributeTypes().entrySet()) {
         //            type.setAttributeTypes(entry.getValue(), entry.getKey());
         //         }
         //         type.setSuperType(new HashSet<ArtifactType>(updated.getSuperArtifactTypes()));
      }
   }
}
