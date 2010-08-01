/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactKey;

/**
 * @author Roberto E. Escobar
 */
public class ActiveArtifactCache extends AbstractArtifactCache {
   private static final double TEXT_PERCENT_INITIAL_CAPACITY = 0.20;
   private static final double STATIC_ID_PERCENT_INITIAL_CAPACITY = 0.20;

   private final CompositeKeyHashMap<IArtifactType, ArtifactKey, Object> byArtifactTypeCache;
   private final CompositeKeyHashMap<String, IOseeBranch, Object> keyedArtifactCache;
   private final HashCollection<String, Object> staticIdArtifactCache;

   public ActiveArtifactCache(int initialCapacity) {
      super(initialCapacity);
      byArtifactTypeCache = new CompositeKeyHashMap<IArtifactType, ArtifactKey, Object>(initialCapacity, true);

      int textCacheSize = getFromBase(initialCapacity, TEXT_PERCENT_INITIAL_CAPACITY);
      keyedArtifactCache = new CompositeKeyHashMap<String, IOseeBranch, Object>(textCacheSize, true);

      int staticIdCacheSize = getFromBase(initialCapacity, STATIC_ID_PERCENT_INITIAL_CAPACITY);
      staticIdArtifactCache = new HashCollection<String, Object>(true, HashSet.class, staticIdCacheSize);
   }

   private int getFromBase(int base, double percentage) {
      return (int) (base * percentage);
   }

   @Override
   public Object cache(Artifact artifact) {
      Object object = super.cache(artifact);
      byArtifactTypeCache.put(artifact.getArtifactType(), new ArtifactKey(artifact), object);
      return object;
   }

   @Override
   public void deCache(Artifact artifact) {
      super.deCache(artifact);
      byArtifactTypeCache.remove(artifact.getArtifactType(), new ArtifactKey(artifact));
      // TODO ?
      //      deCacheFromTextCache(artifact);
      //      deCacheFromStaticIdCache(artifact);
   }

   //   private void deCacheFromTextCache(Artifact artifact) {
   //      List<Pair<String, IOseeBranch>> toRemove = new ArrayList<Pair<String, IOseeBranch>>();
   //      for (Entry<Pair<String, IOseeBranch>, Object> entry : keyedArtifactCache.entrySet()) {
   //         Object object = entry.getValue();
   //         Artifact cachedArt = asArtifact(object);
   //         if (cachedArt == null || cachedArt.equals(artifact)) {
   //            toRemove.add(entry.getKey());
   //         }
   //      }
   //
   //      for (Pair<String, IOseeBranch> key : toRemove) {
   //         keyedArtifactCache.remove(key.getFirst(), key.getSecond());
   //      }
   //   }
   //
   //   private void deCacheFromStaticIdCache(Artifact artifact) {
   //      HashCollection<String, Object> keysToRemove = new HashCollection<String, Object>();
   //      for (String name : staticIdArtifactCache.keySet()) {
   //         Collection<Object> items = staticIdArtifactCache.getValues(name);
   //         if (items != null) {
   //            for (Object object : items) {
   //               Artifact cachedArt = asArtifact(object);
   //               if (cachedArt == null || cachedArt.equals(artifact)) {
   //                  keysToRemove.put(name, object);
   //               }
   //            }
   //         }
   //      }
   //
   //      for (String name : keysToRemove.keySet()) {
   //         for (Object object : keysToRemove.getValues(name)) {
   //            staticIdArtifactCache.removeValue(name, object);
   //         }
   //      }
   //   }

   @Override
   protected Integer getKey2(Artifact artifact) {
      return artifact.getBranch().getId();
   }

   public Artifact getById(Integer artId, Integer branchId) {
      return asArtifact(getObjectById(artId, branchId));
   }

   public Artifact getByGuid(String artGuid, Integer branchId) {
      return asArtifact(getObjectByGuid(artGuid, branchId));
   }

   public Artifact getByGuid(String artGuid, Branch branch) {
      return asArtifact(getByGuid(artGuid, branch.getId()));
   }

   public List<Artifact> getByType(ArtifactType artifactType) {
      List<Artifact> items = new ArrayList<Artifact>();
      Collection<Object> cachedItems = byArtifactTypeCache.getValues(artifactType);
      if (cachedItems != null) {
         for (Object obj : cachedItems) {
            Artifact artifact = asArtifact(obj);
            if (artifact != null) {
               items.add(artifact);
            }
         }
      }
      return items;
   }

   public Artifact getByText(String text, Branch branch) {
      return asArtifact(keyedArtifactCache.get(text, branch));
   }

   public Collection<Artifact> getByStaticId(String staticId) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<Object> cachedArts = staticIdArtifactCache.getValues(staticId);
      if (cachedArts != null) {
         for (Object obj : cachedArts) {
            Artifact artifact = asArtifact(obj);
            if (artifact != null && !artifact.isDeleted()) {
               artifacts.add(artifact);
            }
         }
      }
      return artifacts;
   }

   public Collection<Artifact> getByStaticId(String staticId, IOseeBranch branch) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<Object> cachedArts = staticIdArtifactCache.getValues(staticId);
      if (cachedArts != null) {
         for (Object obj : cachedArts) {
            Artifact artifact = asArtifact(obj);
            if (artifact != null && !artifact.isDeleted() && artifact.getBranch().equals(branch)) {
               artifacts.add(artifact);
            }
         }
      }
      return artifacts;
   }

   /**
    * @returns the previous value associated with keys, or null if there was no mapping for key. (A null return can also
    * indicate that the map previously associated null with key, if the implementation supports null values.)
    */
   public Artifact cacheByText(String key, Artifact artifact) {
      Object object = cache(artifact);
      return asArtifact(keyedArtifactCache.put(key, artifact.getBranch(), object));
   }

   /**
    * @returns the previous value associated with keys, or null if there was no mapping for key. (A null return can also
    * indicate that the map previously associated null with key, if the implementation supports null values.)
    */
   public Artifact cacheByStaticId(String staticId, Artifact artifact) {
      Object object = cache(artifact);
      return asArtifact(staticIdArtifactCache.put(staticId, object));
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(super.toString());
      builder.append(String.format("ByType:     [%s]\n", byArtifactTypeCache.size()));
      builder.append(String.format("ByText:     [%s]\n", keyedArtifactCache.size()));
      builder.append(String.format("ByStaticId: [%s]\n", staticIdArtifactCache.size()));
      return builder.toString();
   }
}