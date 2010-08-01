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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractArtifactCache {
   private final CompositeKeyHashMap<Integer, Integer, Object> idCache;
   private final CompositeKeyHashMap<String, Integer, Object> guidCache;

   private static enum FilterType {
      ONLY_DIRTIES,
      NONE;
   }

   protected AbstractArtifactCache(int initialCapacity) {
      idCache = new CompositeKeyHashMap<Integer, Integer, Object>(initialCapacity, true);
      guidCache = new CompositeKeyHashMap<String, Integer, Object>(initialCapacity, true);
   }

   public Object cache(Artifact artifact) {
      Object object = asCacheObject(artifact);
      Integer key2 = getKey2(artifact);
      idCache.put(artifact.getArtId(), key2, object);
      guidCache.put(artifact.getGuid(), key2, object);
      return object;
   }

   public void deCache(Artifact artifact) {
      Integer key2 = getKey2(artifact);
      idCache.remove(artifact.getArtId(), key2);
      guidCache.remove(artifact.getGuid(), key2);
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
      Collection<Artifact> artifacts = new HashSet<Artifact>();
      for (Entry<Pair<String, Integer>, Object> entry : guidCache.entrySet()) {
         Artifact art = asArtifact(entry.getValue());
         if (!isFiltered(art, filterType)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   protected abstract Integer getKey2(Artifact artifact);

   protected Object getObjectByGuid(String guid, Integer key2) {
      return guidCache.get(guid, key2);
   }

   protected Object getObjectById(Integer uniqueId, Integer key2) {
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
   public void updateReferenceType(int artId, int branchId) throws OseeCoreException {
      Object obj = idCache.get(artId, branchId);
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