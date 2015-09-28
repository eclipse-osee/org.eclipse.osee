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
package org.eclipse.osee.framework.skynet.core.artifact.cache;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractArtifactCache {
   private final CompositeKeyHashMap<Integer, Long, Object> idCache;
   private final CompositeKeyHashMap<String, Long, Object> guidCache;

   private static enum FilterType {
      ONLY_DIRTIES,
      NONE;
   }

   protected AbstractArtifactCache(int initialCapacity) {
      idCache = new CompositeKeyHashMap<>(initialCapacity, true);
      guidCache = new CompositeKeyHashMap<>(initialCapacity, true);
   }

   public Object cache(Artifact artifact) {
      Object object = asCacheObject(artifact);
      Long key2 = getKey2(artifact);
      idCache.put(artifact.getArtId(), key2, object);
      guidCache.put(artifact.getGuid(), key2, object);
      return object;
   }

   public void deCache(Artifact artifact) {
      Long key2 = getKey2(artifact);
      idCache.removeAndGet(artifact.getArtId(), key2);
      guidCache.removeAndGet(artifact.getGuid(), key2);
   }

   public Collection<Artifact> getAll() {
      return getItems(FilterType.NONE);
   }

   public Collection<Artifact> getAllDirties() {
      return getItems(FilterType.ONLY_DIRTIES);
   }

   private boolean isFiltered(Artifact artifact, FilterType filter) {
      boolean isFiltered = artifact == null;
      if (!isFiltered) {
         if (FilterType.ONLY_DIRTIES == filter) {
            isFiltered = !artifact.isDirty();
         }
      }
      return isFiltered;
   }

   private Collection<Artifact> getItems(FilterType filterType) {
      Collection<Artifact> artifacts = new HashSet<>();
      for (Entry<Pair<String, Long>, Object> entry : guidCache.entrySet()) {
         Artifact art = asArtifact(entry.getValue());
         if (!isFiltered(art, filterType)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   protected abstract Long getKey2(Artifact artifact);

   protected Object getObjectByGuid(String guid, Long key2) {
      return guidCache.get(guid, key2);
   }

   protected Object getObjectById(Integer uniqueId, Long key2) {
      return idCache.get(uniqueId, key2);
   }

   private Object asCacheObject(Artifact artifact) {
      if (ArtifactFactoryManager.getEternalArtifactTypes().contains(artifact.getArtifactType())) {
         return artifact;
      } else if (artifact.isDirty()) {
         return artifact;
      } else {
         return new WeakReference<Artifact>(artifact);
      }
   }

   @SuppressWarnings("unchecked")
   protected Artifact asArtifact(Object obj) {
      if (obj != null) {
         if (obj instanceof Artifact) {
            return (Artifact) obj;
         } else if (obj instanceof WeakReference<?>) {
            WeakReference<Artifact> art = (WeakReference<Artifact>) obj;
            return art.get();
         }
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public void updateReferenceType(int artId, long branchUuid) {
      Object obj = idCache.get(artId, branchUuid);
      if (obj != null) {
         if (obj instanceof Artifact) {
            Artifact artifact = (Artifact) obj;
            if (!artifact.isDirty()) {
               cache(artifact);
            }
         } else if (obj instanceof WeakReference<?>) {
            WeakReference<Artifact> art = (WeakReference<Artifact>) obj;
            Artifact artifact = art.get();
            if (artifact != null && artifact.isDirty()) {
               cache(artifact);
            }
         }
      }
   }

   public int size() {
      return getAll().size();
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format("Items:   [%s]\n", size()));
      builder.append(String.format("Dirtied: [%s]\n", getAllDirties()));
      return builder.toString();
   }

}