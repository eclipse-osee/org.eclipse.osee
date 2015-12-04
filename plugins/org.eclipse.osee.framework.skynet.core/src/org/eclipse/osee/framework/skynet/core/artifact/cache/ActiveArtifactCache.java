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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactKey;

/**
 * @author Roberto E. Escobar
 */
public class ActiveArtifactCache extends AbstractArtifactCache {

   private final CompositeKeyHashMap<IArtifactType, ArtifactKey, Object> byArtifactTypeCache;
   private final CompositeKeyHashMap<String, Long, Set<Object>> keyedArtifactCache;
   private final Map<Long, ArtifactKey> uuidToArtifactKey;
   private final CompositeKeyHashMap<Long, Long, String> uuidBranchUuidToGuid;

   public ActiveArtifactCache(int initialCapacity) {
      super(initialCapacity);
      byArtifactTypeCache = new CompositeKeyHashMap<>(initialCapacity, true);
      keyedArtifactCache = new CompositeKeyHashMap<>(200, true);
      uuidToArtifactKey = new HashMap<>(200);
      uuidBranchUuidToGuid = new CompositeKeyHashMap<>(200, true);
   }

   @Override
   public Object cache(Artifact artifact) {
      Object object = super.cache(artifact);
      ArtifactKey artifactKey = new ArtifactKey(artifact);
      byArtifactTypeCache.put(artifact.getArtifactType(), artifactKey, object);
      uuidToArtifactKey.put(artifact.getUuid(), artifactKey);
      uuidBranchUuidToGuid.put(artifact.getUuid(), artifact.getBranchId(), artifact.getGuid());
      return object;
   }

   @Override
   public void deCache(Artifact artifact) {
      super.deCache(artifact);
      byArtifactTypeCache.removeAndGet(artifact.getArtifactType(), new ArtifactKey(artifact));
      List<String> guid = uuidBranchUuidToGuid.getValues(artifact.getUuid());
      if (guid != null && !guid.isEmpty()) {
         keyedArtifactCache.remove(guid.iterator().next(), artifact.getBranch().getUuid());
      }
      uuidToArtifactKey.remove(artifact.getUuid());
      uuidBranchUuidToGuid.remove(artifact.getUuid());
   }

   @Override
   protected Long getKey2(Artifact artifact) {
      try {
         return artifact.getBranchId();
      } catch (OseeCoreException ex) {
         return -1L;
      }
   }

   public Artifact getById(Integer artId, Long branchUuid) {
      return asArtifact(getObjectById(artId, branchUuid));
   }

   public Artifact getByGuid(String artGuid, Long branchUuid) {
      return asArtifact(getObjectByGuid(artGuid, branchUuid));
   }

   public Artifact getByUuid(Long uuid, Long branchUuid) {
      return asArtifact(getObjectById(uuid.intValue(), branchUuid));
   }

   public List<Artifact> getByType(IArtifactType artifactType) {
      List<Artifact> items = new ArrayList<>();
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

   public void deCacheByText(String text, IOseeBranch branch, Artifact artifact) {
      super.deCache(artifact);
      Set<Object> objects = keyedArtifactCache.get(text, branch.getUuid());
      objects.remove(artifact);
   }

   /**
    * Return single artifact stored by text and branch or null if none.
    *
    * @throws OseeStateException if more than one artifact stored.
    */
   public Artifact getByText(String text, IOseeBranch branch) throws OseeCoreException {
      Set<Object> objects = keyedArtifactCache.get(text, branch.getUuid());
      if (objects != null) {
         if (objects.size() > 1) {
            throw new OseeStateException(
               String.format("Expected only one value for [%s]; found [%d]", text, objects.size()));
         } else if (objects.size() == 1) {
            return asArtifact(objects.iterator().next());
         }
      }
      return null;
   }

   public Collection<Artifact> getListByText(String text, IOseeBranch branch) {
      Set<Object> objects = keyedArtifactCache.get(text, branch.getUuid());
      if (objects == null) {
         return Collections.emptyList();
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(objects);
   }

   /**
    * @returns the previous value associated with keys, or null if there was no mapping for key. (A null return can also
    * indicate that the map previously associated null with key, if the implementation supports null values.)
    */
   public Artifact cacheByText(String key, Artifact artifact) {
      cache(artifact);
      Set<Object> objects = keyedArtifactCache.get(key, artifact.getBranch().getUuid());
      if (objects == null) {
         objects = new HashSet<>();
         keyedArtifactCache.put(key, artifact.getBranch().getUuid(), objects);
      }
      if (objects.contains(artifact)) {
         return artifact;
      }
      objects.add(artifact);
      return null;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(super.toString());
      builder.append(String.format("ByType:     [%s]\n", byArtifactTypeCache.size()));
      builder.append(String.format("ByText:     [%s]\n", keyedArtifactCache.size()));
      return builder.toString();
   }

   public void deCache(Long uuid, Long branchUuid) {
      ArtifactKey artifactKey = uuidToArtifactKey.get(uuid);
      if (artifactKey != null) {
         IOseeBranch branch = artifactKey.getBranch();
         keyedArtifactCache.remove(uuid, branch);
      }
      String guid = uuidBranchUuidToGuid.get(uuid, branchUuid);
      if (Strings.isValid(guid)) {
         keyedArtifactCache.removeAndGet(guid, branchUuid);
      }
      uuidToArtifactKey.remove(uuid);
      uuidBranchUuidToGuid.remove(uuid, branchUuid);
   }
}