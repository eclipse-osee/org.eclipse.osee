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
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ArtifactTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.ArtifactTypeCacheUpdateResponse.ArtifactTypeRow;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.Triplet;

/**
 * @author Roberto E. Escobar
 */
public class ClientArtifactTypeAccessor extends AbstractClientDataAccessor<Long, ArtifactType> {

   private final AbstractOseeCache<Long, AttributeType> attrCache;
   private final AbstractOseeCache<String, Branch> branchCache;

   private final ArtifactTypeFactory artifactTypeFactory;

   public ClientArtifactTypeAccessor(ArtifactTypeFactory artifactTypeFactory, AbstractOseeCache<Long, AttributeType> attrCache, AbstractOseeCache<String, Branch> branchCache) {
      this.artifactTypeFactory = artifactTypeFactory;
      this.attrCache = attrCache;
      this.branchCache = branchCache;
   }

   private ArtifactTypeFactory getFactory() {
      return artifactTypeFactory;
   }

   @Override
   public void load(IOseeCache<Long, ArtifactType> cache) throws OseeCoreException {
      attrCache.ensurePopulated();
      branchCache.ensurePopulated();
      super.load(cache);
   }

   @Override
   protected Collection<ArtifactType> updateCache(IOseeCache<Long, ArtifactType> cache) throws OseeCoreException {
      List<ArtifactType> updatedItems = new ArrayList<ArtifactType>();

      ArtifactTypeCacheUpdateResponse response =
         requestUpdateMessage(cache, CoreTranslatorId.ARTIFACT_TYPE_CACHE_UPDATE_RESPONSE);

      ArtifactTypeFactory factory = getFactory();
      for (ArtifactTypeRow row : response.getArtTypeRows()) {
         ArtifactType cached =
            factory.createOrUpdate(cache, row.getId(), row.getStorageState(), row.getGuid(), row.isAbstract(),
               row.getName());
         updatedItems.add(cached);
      }

      for (Entry<Integer, Integer[]> entry : response.getBaseToSuperTypes().entrySet()) {
         ArtifactType baseType = cache.getById(entry.getKey());
         Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
         for (Integer superId : entry.getValue()) {
            ArtifactType superType = cache.getById(superId);
            if (superType != null) {
               superTypes.add(superType);
            }
         }
         baseType.setSuperTypes(superTypes);
      }

      CompositeKeyHashMap<ArtifactType, IOseeBranch, Collection<AttributeType>> attrs =
         new CompositeKeyHashMap<ArtifactType, IOseeBranch, Collection<AttributeType>>();

      for (Triplet<Long, String, Long> entry : response.getAttributeTypes()) {
         ArtifactType key1 = cache.getByGuid(entry.getFirst());
         IOseeBranch key2 = branchCache.getByGuid(entry.getSecond());
         Collection<AttributeType> types = attrs.get(key1, key2);
         if (types == null) {
            types = new HashSet<AttributeType>();
            attrs.put(key1, key2, types);
         }
         types.add(attrCache.getByGuid(entry.getThird()));
      }

      for (Entry<Pair<ArtifactType, IOseeBranch>, Collection<AttributeType>> entry : attrs.entrySet()) {
         entry.getKey().getFirst().setAttributeTypes(entry.getValue(), entry.getKey().getSecond());
      }
      return updatedItems;
   }
}
