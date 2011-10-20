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

import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
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

}
