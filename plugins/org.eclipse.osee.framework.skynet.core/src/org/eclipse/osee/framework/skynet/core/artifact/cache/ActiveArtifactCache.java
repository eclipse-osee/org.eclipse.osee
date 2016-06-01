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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactKey;

/**
 * @author Roberto E. Escobar
 */
public class ActiveArtifactCache extends AbstractArtifactCache {

   private final CompositeKeyHashMap<IArtifactType, ArtifactKey, Object> byArtifactTypeCache;
   private final Map<Long, ArtifactKey> uuidToArtifactKey;
   private final CompositeKeyHashMap<Long, Long, String> uuidBranchUuidToGuid;

   public ActiveArtifactCache(int initialCapacity) {
      super(initialCapacity);
      byArtifactTypeCache = new CompositeKeyHashMap<>(initialCapacity, true);
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

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(super.toString());
      builder.append(String.format("ByType:     [%s]\n", byArtifactTypeCache.size()));
      return builder.toString();
   }

}