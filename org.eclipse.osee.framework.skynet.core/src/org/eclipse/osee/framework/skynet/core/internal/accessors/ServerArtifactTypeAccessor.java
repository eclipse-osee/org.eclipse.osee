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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerArtifactTypeAccessor extends AbstractServerDataAccessor<ArtifactType> {

   private final AbstractOseeCache<AttributeType> attrCache;
   private final AbstractOseeCache<Branch> branchCache;

   public ServerArtifactTypeAccessor(IOseeModelFactoryServiceProvider factoryProvider, AbstractOseeCache<AttributeType> attrCache, AbstractOseeCache<Branch> branchCache) {
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
   protected void updateCache(IOseeCache<ArtifactType> cache, Collection<ArtifactType> items) throws OseeCoreException {
      List<ArtifactType> updatedTypes = new ArrayList<ArtifactType>();

      ArtifactTypeFactory factory = getFactory();
      for (ArtifactType srcType : items) {
         ArtifactType cached =
               factory.createOrUpdate(cache, srcType.getId(), srcType.getModificationType(), srcType.getGuid(),
                     srcType.isAbstract(), srcType.getName());
         updatedTypes.add(cached);

         for (Entry<Branch, Collection<AttributeType>> entries : srcType.getLocalAttributeTypes().entrySet()) {
            Branch branch = branchCache.getByGuid(entries.getKey().getGuid());
            List<AttributeType> attrTypes = new ArrayList<AttributeType>();
            for (AttributeType srcAttr : entries.getValue()) {
               AttributeType attrType = attrCache.getByGuid(srcAttr.getGuid());
               attrTypes.add(attrType);
            }
            cached.setAttributeTypes(attrTypes, branch);
         }
      }
      for (ArtifactType srcType : items) {
         ArtifactType baseType = cache.getById(srcType.getId());
         Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
         for (ArtifactType srcSuper : srcType.getSuperArtifactTypes()) {
            ArtifactType superType = cache.getById(srcSuper.getId());
            superTypes.add(superType);
         }
         baseType.setSuperType(superTypes);
      }
   }
}
