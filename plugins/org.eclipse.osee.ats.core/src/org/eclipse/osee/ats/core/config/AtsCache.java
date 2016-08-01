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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * Provide ATS Caching by uuid, guid and application tags. These are evacuating caches that will expire 15 minutes after
 * being cached.
 *
 * @author Donald G. Dunne
 */
public class AtsCache implements IAtsCache {

   private static IAtsServices services;
   private final LoadingCache<Long, IAtsObject> uuidToAtsObjectCache = CacheBuilder.newBuilder() //
      .expireAfterWrite(15, TimeUnit.MINUTES) //
      .build(uuidToAtsObjectCacheLoader);
   private final LoadingCache<Long, ArtifactId> uuidToArtifactIdCache = CacheBuilder.newBuilder() //
      .expireAfterWrite(15, TimeUnit.MINUTES) //
      .build(uuidToArtifactIdCacheLoader);
   private final LoadingCache<String, IAtsObject> tagToAtsObjectCache = CacheBuilder.newBuilder() //
      .expireAfterWrite(15, TimeUnit.MINUTES) //
      .build(tagToAtsObjectCacheLoader);
   private final LoadingCache<String, ArtifactId> tagToArtifactIdCache = CacheBuilder.newBuilder() //
      .expireAfterWrite(15, TimeUnit.MINUTES) //
      .build(tagToArtifactIdCacheLoader);

   public AtsCache(IAtsServices services) {
      AtsCache.services = services;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends IAtsObject> T getAtsObject(Long uuid) {
      Conditions.checkNotNull(uuid, "uuid");
      try {
         return (T) uuidToAtsObjectCache.get(uuid);
      } catch (Exception ex) {
         return null;
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends IAtsObject> T getAtsObjectByGuid(String guid) {
      Conditions.checkNotNullOrEmpty(guid, "guid");
      try {
         return (T) tagToAtsObjectCache.get(guid);
      } catch (Exception ex) {
         return null;
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends IAtsObject> T getAtsObjectByTag(String tag) {
      Conditions.checkNotNullOrEmpty(tag, "tag");
      try {
         return (T) tagToAtsObjectCache.get(tag);
      } catch (Exception ex) {
         return null;
      }
   }

   @Override
   public <T extends IAtsObject> T getAtsObjectByTag(String tag, Class<T> clazz) {
      Conditions.checkNotNullOrEmpty(tag, "tag");
      return getAtsObjectByTag(tag);
   }

   @Override
   public void cacheAtsObject(IAtsObject atsObject) {
      Conditions.checkNotNull(atsObject, "atsObject");
      if (atsObject.getStoreObject() != null) {
         tagToAtsObjectCache.put(atsObject.getStoreObject().getGuid(), atsObject);
         tagToArtifactIdCache.put(atsObject.getStoreObject().getGuid(), atsObject.getStoreObject());
         uuidToArtifactIdCache.put(atsObject.getId(), atsObject.getStoreObject());
      }
      uuidToAtsObjectCache.put(atsObject.getId(), atsObject);
   }

   @Override
   public void cacheAtsObjectByTag(String tag, IAtsObject atsObject) {
      Conditions.checkNotNullOrEmpty(tag, "tag");
      Conditions.checkNotNull(atsObject, "atsObject");
      tagToAtsObjectCache.put(tag, atsObject);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends ArtifactId> T getArtifact(Long uuid) {
      Conditions.checkNotNull(uuid, "uuid");
      try {
         return (T) uuidToArtifactIdCache.get(uuid);
      } catch (Exception ex) {
         return null;
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends ArtifactId> T getArtifactByGuid(String guid) {
      Conditions.checkNotNullOrEmpty(guid, "guid");
      try {
         return (T) tagToArtifactIdCache.get(guid);
      } catch (Exception ex) {
         return null;
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends ArtifactId> T getArtifactByTag(String tag) {
      Conditions.checkNotNullOrEmpty(tag, "tag");
      try {
         return (T) tagToArtifactIdCache.get(tag);
      } catch (Exception ex) {
         return null;
      }
   }

   @Override
   public void cacheArtifactByTag(String tag, ArtifactId artifact) {
      Conditions.checkNotNullOrEmpty(tag, "tag");
      Conditions.checkNotNull(artifact, "artifact");
      tagToArtifactIdCache.put(tag, artifact);
   }

   @Override
   public void cacheArtifact(ArtifactId artifact) {
      Conditions.checkNotNull(artifact, "artifact");
      tagToArtifactIdCache.put(artifact.getGuid(), artifact);
      uuidToArtifactIdCache.put(artifact.getId(), artifact);
   }

   static CacheLoader<Long, IAtsObject> uuidToAtsObjectCacheLoader = new CacheLoader<Long, IAtsObject>() {
      @Override
      public IAtsObject load(Long uuid) {
         return services.getConfigItemFactory().getConfigObject(services.getArtifact(uuid));
      }
   };

   static CacheLoader<Long, ArtifactId> uuidToArtifactIdCacheLoader = new CacheLoader<Long, ArtifactId>() {
      @Override
      public ArtifactId load(Long uuid) {
         return services.getArtifact(uuid);
      }
   };

   static CacheLoader<String, IAtsObject> tagToAtsObjectCacheLoader = new CacheLoader<String, IAtsObject>() {
      @Override
      public IAtsObject load(String tag) {
         if (GUID.isValid(tag)) {
            return services.getConfigItemFactory().getConfigObject(services.getArtifactByGuid(tag));
         }
         throw new IllegalStateException(String.format("IAtsObject not tagged with tag [%s]", tag));
      }
   };

   static CacheLoader<String, ArtifactId> tagToArtifactIdCacheLoader = new CacheLoader<String, ArtifactId>() {
      @Override
      public ArtifactId load(String tag) {
         if (GUID.isValid(tag)) {
            return services.getArtifactByGuid(tag);
         }
         throw new IllegalStateException(String.format("ArtifactId not tagged with tag [%s]", tag));
      }
   };

   @Override
   public <T extends IAtsObject> T getByUuid(Long uuid, Class<T> clazz) {
      Conditions.checkNotNull(uuid, "uuid");
      return getAtsObject(uuid);
   }

   @Override
   public void invalidate() {
      tagToAtsObjectCache.invalidateAll();
      uuidToAtsObjectCache.invalidateAll();
      tagToArtifactIdCache.invalidateAll();
      uuidToArtifactIdCache.invalidateAll();
   }

   @Override
   public void deCacheAtsObject(IAtsObject atsObject) {
      Conditions.checkNotNull(atsObject, "atsObject");
      if (atsObject.getStoreObject() != null) {
         tagToAtsObjectCache.invalidate(atsObject.getStoreObject().getGuid());
         tagToArtifactIdCache.invalidate(atsObject.getStoreObject().getGuid());
      }
      uuidToAtsObjectCache.invalidate(atsObject.getId());
      uuidToArtifactIdCache.invalidate(atsObject.getId());
   }

   @Override
   public ArtifactId getArtifact(IAtsObject atsObject) {
      if (atsObject.getStoreObject() != null) {
         return atsObject.getStoreObject();
      }
      return getArtifact(atsObject.getId());
   }

   @Override
   public <T extends IAtsObject> T getAtsObjectByToken(IArtifactToken token, Class<T> clazz) {
      Conditions.checkNotNull(token, "token");
      return getAtsObject(token.getId());
   }

}
