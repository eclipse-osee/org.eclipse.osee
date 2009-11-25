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

import java.util.Collection;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
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
      //      RelationTypeFactory factory = getFactory();
      //      for (RelationType srcItem : items) {
      //         ArtifactType aSideType = artCache.getByGuid(srcItem.getArtifactTypeSideA().getGuid());
      //         ArtifactType bSideType = artCache.getByGuid(srcItem.getArtifactTypeSideB().getGuid());
      //         factory.createOrUpdate(cache, srcItem.getId(), srcItem.getModificationType(), srcItem.getGuid(),
      //               srcItem.getName(), srcItem.getSideAName(), srcItem.getSideBName(), aSideType, bSideType,
      //               srcItem.getMultiplicity(), srcItem.getDefaultOrderTypeGuid());
      //      }
      return null;
   }
}
