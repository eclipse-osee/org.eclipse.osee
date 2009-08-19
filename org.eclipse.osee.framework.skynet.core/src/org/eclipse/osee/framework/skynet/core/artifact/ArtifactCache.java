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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactCache {
   // The keys for this are <artId, transactionId>
   private final CompositeKeyHashMap<Integer, Integer, Artifact> historicalArtifactIdCache =
         new CompositeKeyHashMap<Integer, Integer, Artifact>();
   private final CompositeKeyHashMap<String, Integer, Artifact> historicalArtifactGuidCache =
         new CompositeKeyHashMap<String, Integer, Artifact>();

   private final CompositeKeyHashMap<Integer, Integer, Artifact> artifactIdCache =
         new CompositeKeyHashMap<Integer, Integer, Artifact>(2000);

   private final CompositeKeyHashMap<String, Integer, Artifact> artifactGuidCache =
         new CompositeKeyHashMap<String, Integer, Artifact>(2000);

   private final CompositeKeyHashMap<String, Branch, Artifact> keyedArtifactCache =
         new CompositeKeyHashMap<String, Branch, Artifact>(10);

   private final HashCollection<String, Artifact> staticIdArtifactCache =
         new HashCollection<String, Artifact>(true, HashSet.class, 100);

   private final HashCollection<ArtifactType, Artifact> byArtifactTypeCache =
         new HashCollection<ArtifactType, Artifact>();

   private static final ArtifactCache instance = new ArtifactCache();

   private ArtifactCache() {
   }

   public static List<Artifact> getArtifactsByName(ArtifactType artifactType, String name) {
      List<Artifact> arts = new ArrayList<Artifact>();
      for (Artifact artifact : getArtifactsByType(artifactType)) {
         if (artifact.getName().equals(name)) {
            arts.add(artifact);
         }
      }
      return arts;
   }

   public static Collection<Artifact> getDirtyArtifacts() throws OseeCoreException {
      Set<Artifact> dirtyArts = new HashSet<Artifact>();
      // ArtifactIdCache is the master cache - no need to check other caches
      for (Entry<Pair<Integer, Integer>, Artifact> entry : instance.artifactIdCache.entrySet()) {
         if (entry.getValue().isDirty()) {
            dirtyArts.add(entry.getValue());
         }
      }
      return dirtyArts;
   }

   /**
    * Cache the artifact so that we can avoid creating duplicate instances of an artifact
    * 
    * @param artifact
    */
   synchronized static void cache(Artifact artifact) {
      if (artifact.isHistorical()) {
         instance.historicalArtifactIdCache.put(artifact.getArtId(), artifact.getTransactionNumber(), artifact);
         instance.historicalArtifactGuidCache.put(artifact.getGuid(), artifact.getTransactionNumber(), artifact);
      } else {
         instance.artifactIdCache.put(artifact.getArtId(), artifact.getBranch().getBranchId(), artifact);
         instance.artifactGuidCache.put(artifact.getGuid(), artifact.getBranch().getBranchId(), artifact);
         instance.byArtifactTypeCache.put(artifact.getArtifactType(), artifact);
      }

   }

   synchronized static void cachePostAttributeLoad(Artifact artifact) throws OseeCoreException {
      for (String staticId : artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)) {
         cacheByStaticId(staticId, artifact);
      }
   }

   public synchronized static void cacheByStaticId(String staticId, Artifact artifact) {
      instance.staticIdArtifactCache.put(staticId, artifact);
   }

   public synchronized static void cacheByStaticId(Artifact artifact) throws OseeCoreException {
      for (String staticId : artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)) {
         ArtifactCache.cacheByStaticId(staticId, artifact);
      }
   }

   public synchronized static void deCacheStaticIds(Artifact artifact) throws OseeCoreException {
      Set<String> staticIds = new HashSet<String>();
      for (String staticId : instance.staticIdArtifactCache.keySet()) {
         if (instance.staticIdArtifactCache.getValues(staticId).contains(artifact)) {
            staticIds.add(staticId);
         }
      }
      for (String staticId : staticIds) {
         instance.staticIdArtifactCache.removeValue(staticId, artifact);
      }
   }

   public synchronized static void deCache(Artifact artifact) throws OseeCoreException {
      if (artifact.isInDb()) {
         instance.historicalArtifactIdCache.remove(artifact.getArtId(), artifact.getTransactionNumber());
         instance.historicalArtifactGuidCache.remove(artifact.getGuid(), artifact.getTransactionNumber());
      }
      instance.artifactIdCache.remove(artifact.getArtId(), artifact.getBranch().getBranchId());
      instance.artifactGuidCache.remove(artifact.getGuid(), artifact.getBranch().getBranchId());
      instance.byArtifactTypeCache.removeValue(artifact.getArtifactType(), artifact);
      deCacheStaticIds(artifact);
   }

   public synchronized static Collection<Artifact> getArtifactsByStaticId(String staticId) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<Artifact> cachedArts = instance.staticIdArtifactCache.getValues(staticId);
      if (cachedArts == null) {
         return artifacts;
      }
      for (Artifact artifact : cachedArts) {
         if (!artifact.isDeleted()) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   public synchronized static Collection<Artifact> getArtifactsByStaticId(String staticId, Branch branch) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<Artifact> cachedArts = instance.staticIdArtifactCache.getValues(staticId);
      if (cachedArts == null) {
         return artifacts;
      }
      for (Artifact artifact : cachedArts) {
         if (!artifact.isDeleted() && artifact.getBranch().equals(branch)) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   public synchronized static Artifact getHistorical(Integer artId, Integer transactionNumber) {
      return instance.historicalArtifactIdCache.get(artId, transactionNumber);
   }

   public synchronized static Artifact getHistorical(String guid, Integer transactionNumber) {
      return instance.historicalArtifactGuidCache.get(guid, transactionNumber);
   }

   public synchronized static List<Artifact> getArtifactsByType(ArtifactType artifactType) {
      List<Artifact> items = new ArrayList<Artifact>();
      Collection<Artifact> cachedItems = instance.byArtifactTypeCache.getValues(artifactType);
      if (cachedItems != null) {
         items.addAll(cachedItems);
      }
      return items;
   }

   public static List<Artifact> getArtifactsByType(ArtifactType artifactType, Active active) throws OseeCoreException {
      return Artifacts.getActive(getArtifactsByType(artifactType), active, null);
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branch
    */
   public static Artifact getActive(Integer artId, Branch branch) {
      return getActive(artId, branch.getBranchId());
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branchId
    */
   public synchronized static Artifact getActive(Integer artId, Integer branchId) {
      return instance.artifactIdCache.get(artId, branchId);
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branchId
    */
   public synchronized static Artifact getActive(String artGuid, Integer branchId) {
      return instance.artifactGuidCache.get(artGuid, branchId);
   }

   /**
    * returns the active artifact based on the previously provided text key and branch
    * 
    * @param key
    * @param branch
    */
   public synchronized static Artifact getByTextId(String key, Branch branch) {
      return instance.keyedArtifactCache.get(key, branch);
   }

   /**
    * used to cache an artifact based on a text identifier and its branch
    * 
    * @param key
    * @param branch
    * @param artifact
    */
   public synchronized static Artifact cacheByTextId(String key, Artifact artifact) {
      return instance.keyedArtifactCache.put(key, artifact.getBranch(), artifact);
   }
}
