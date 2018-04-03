/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Provide ATS Caching by id, guid and application tags. These are evacuating caches that will expire 15 minutes after
 * being cached.
 *
 * @author Donald G. Dunne
 */
public class AtsCache implements IAtsCache {

   private final AtsApi atsApi;
   private final LoadingCache<Long, IAtsObject> idToAtsObjectCache;

   public AtsCache(AtsApi atsApi) {
      this.atsApi = atsApi;
      idToAtsObjectCache = CacheBuilder.newBuilder() //
         .expireAfterWrite(15, TimeUnit.MINUTES) //
         .build(idToAtsObjectCacheLoader);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends IAtsObject> T getAtsObject(Long id) {
      Conditions.checkNotNull(id, "id");
      try {
         return (T) idToAtsObjectCache.get(id);
      } catch (Exception ex) {
         return null;
      }
   }

   @Override
   public <T extends IAtsObject> T getAtsObject(ArtifactId artifact) {
      return getAtsObject(artifact.getId());
   }

   @Override
   public void cacheAtsObject(IAtsObject atsObject) {
      Conditions.checkNotNull(atsObject, "atsObject");
      idToAtsObjectCache.put(atsObject.getId(), atsObject);
   }

   private final CacheLoader<Long, IAtsObject> idToAtsObjectCacheLoader = new CacheLoader<Long, IAtsObject>() {
      @Override
      public IAtsObject load(Long id) {
         return atsApi.getConfigItemFactory().getConfigObject(atsApi.getQueryService().getArtifact(id));
      }
   };

   @Override
   public void invalidate() {
      idToAtsObjectCache.invalidateAll();
   }

   @Override
   public void deCacheAtsObject(IAtsObject atsObject) {
      Conditions.checkNotNull(atsObject, "atsObject");
      idToAtsObjectCache.invalidate(atsObject.getId());
   }
}