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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.data.ArtifactTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.ArtifactTypeCacheUpdateResponse.ArtifactTypeRow;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public class ClientArtifactTypeAccessor extends AbstractClientDataAccessor<ArtifactType> {

   private final AbstractOseeCache<AttributeType> attrCache;
   private final AbstractOseeCache<Branch> branchCache;

   public ClientArtifactTypeAccessor(IOseeModelFactoryServiceProvider factoryProvider, AbstractOseeCache<AttributeType> attrCache, AbstractOseeCache<Branch> branchCache) {
      super(factoryProvider);
      this.attrCache = attrCache;
      this.branchCache = branchCache;
   }

   protected ArtifactTypeFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getArtifactTypeFactory();
   }

   @Override
   public void load(IOseeCache<ArtifactType> cache) throws OseeCoreException {
      branchCache.ensurePopulated();
      attrCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected Collection<ArtifactType> updateCache(IOseeCache<ArtifactType> cache) throws OseeCoreException {
      List<ArtifactType> updatedItems = new ArrayList<ArtifactType>();

      ArtifactTypeCacheUpdateResponse response =
            sendUpdateMessage(cache, CoreTranslatorId.ARTIFACT_TYPE_CACHE_UPDATE_RESPONSE);

      ArtifactTypeFactory factory = getFactory();
      for (ArtifactTypeRow row : response.getArtTypeRows()) {
         ArtifactType cached =
               factory.createOrUpdate(cache, row.getId(), row.getModType(), row.getGuid(), row.isAbstract(),
                     row.getName());
         updatedItems.add(cached);
      }

      HashCollection<ArtifactType, ArtifactType> superTypes =
            new HashCollection<ArtifactType, ArtifactType>(false, HashSet.class);
      for (Entry<Integer, Integer> entry : response.getBaseToSuperTypes().entrySet()) {
         ArtifactType superType = cache.getById(entry.getValue());
         if (superType != null) {
            ArtifactType baseType = cache.getById(entry.getKey());
            superTypes.put(baseType, superType);
         }
      }
      for (ArtifactType baseType : superTypes.keySet()) {
         baseType.setSuperType((Set<ArtifactType>) superTypes.getValues(baseType));
      }

      for (Triplet<Integer, Integer, Integer[]> entry : response.getAttributeTypes()) {
         ArtifactType baseType = cache.getById(entry.getFirst());
         Branch branch = branchCache.getById(entry.getSecond());
         List<AttributeType> types = new ArrayList<AttributeType>();
         for (Integer typeId : entry.getThird()) {
            types.add(attrCache.getById(typeId));
         }
         baseType.setAttributeTypes(types, branch);
      }
      return updatedItems;
   }
}
