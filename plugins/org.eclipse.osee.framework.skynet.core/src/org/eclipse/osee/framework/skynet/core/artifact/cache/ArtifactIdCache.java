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
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactIdCache {
   private final ConcurrentHashMap<ArtifactToken, Object> idCache;
   private final CompositeKeyHashMap<String, BranchId, Object> guidCache;

   private static enum FilterType {
      ONLY_DIRTIES,
      NONE;
   }

   public ArtifactIdCache(int initialCapacity) {
      idCache = new ConcurrentHashMap<>(initialCapacity);
      guidCache = new CompositeKeyHashMap<>(initialCapacity, true);
   }

   public Artifact getById(ArtifactToken artifact) {
      return asArtifact(idCache.get(artifact));
   }

   public Artifact getByGuid(String artGuid, BranchId branch) {
      return asArtifact(guidCache.get(artGuid, branch));
   }

   public Object cache(Artifact artifact) {
      Object object = asCacheObject(artifact);
      idCache.put(artifact, object);
      guidCache.put(artifact.getGuid(), artifact.getBranch(), object);
      return object;
   }

   public void deCache(Artifact artifact) {
      idCache.remove(artifact);
      guidCache.removeAndGet(artifact.getGuid(), artifact.getBranch());
   }

   public Collection<Artifact> getAll() {
      return getItems(FilterType.NONE);
   }

   public Collection<Artifact> getAllDirties() {
      return getItems(FilterType.ONLY_DIRTIES);
   }

   private boolean isFiltered(Artifact artifact, FilterType filter) {
      boolean isFiltered = true;

      if (artifact != null) {
         isFiltered = false;
         if (FilterType.ONLY_DIRTIES == filter) {
            isFiltered = !artifact.isDirty();
         }
      }

      return isFiltered;
   }

   private Collection<Artifact> getItems(FilterType filterType) {
      Collection<Artifact> artifacts = new HashSet<>();
      for (Entry<Pair<String, BranchId>, Object> entry : guidCache.entrySet()) {
         Artifact art = asArtifact(entry.getValue());
         if (!isFiltered(art, filterType)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   private Object asCacheObject(Artifact artifact) {
      if (ArtifactFactoryManager.getEternalArtifactTypes().contains(artifact.getArtifactType())) {
         return artifact;
      } else if (artifact.isDirty()) {
         return artifact;
      } else {
         return new WeakReference<>(artifact);
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
   public void updateReferenceType(ArtifactToken token) {
      Object obj = idCache.get(token);
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