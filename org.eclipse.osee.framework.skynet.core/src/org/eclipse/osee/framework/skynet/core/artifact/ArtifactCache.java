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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;

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

   private static final ArtifactCache instance = new ArtifactCache();

   private ArtifactCache() {
   }

   /**
    * Cache the artifact so that we can avoid creating duplicate instances of an artifact
    * 
    * @param artifact
    */
   static void cache(Artifact artifact) {
      if (artifact.isHistorical()) {
         instance.historicalArtifactIdCache.put(artifact.getArtId(), artifact.getTransactionNumber(), artifact);
         instance.historicalArtifactGuidCache.put(artifact.getGuid(), artifact.getTransactionNumber(), artifact);
      } else {
         instance.artifactIdCache.put(artifact.getArtId(), artifact.getBranch().getBranchId(), artifact);
         instance.artifactGuidCache.put(artifact.getGuid(), artifact.getBranch().getBranchId(), artifact);
      }
   }

   static void cachePostAttributeLoad(Artifact artifact) throws OseeDataStoreException {
      try {
         for (String staticId : artifact.getAttributesToStringList(StaticIdQuery.STATIC_ID_ATTRIBUTE)) {
            instance.staticIdArtifactCache.put(staticId, artifact);
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   static void deCache(Artifact artifact) throws OseeCoreException {
      try {
         instance.historicalArtifactIdCache.remove(artifact.getArtId(), artifact.getTransactionNumber());
         instance.historicalArtifactGuidCache.remove(artifact.getGuid(), artifact.getTransactionNumber());
         instance.artifactIdCache.remove(artifact.getArtId(), artifact.getBranch().getBranchId());
         instance.artifactGuidCache.remove(artifact.getGuid(), artifact.getBranch().getBranchId());
         for (String staticId : artifact.getAttributesToStringList(StaticIdQuery.STATIC_ID_ATTRIBUTE)) {
            instance.staticIdArtifactCache.removeValue(staticId, artifact);
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static Collection<Artifact> getArtifactsByStaticId(String staticId) {
      return instance.staticIdArtifactCache.getValues(staticId);
   }

   public static Collection<Artifact> getArtifactsByStaticId(String staticId, Branch branch) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<Artifact> cachedArts = instance.staticIdArtifactCache.getValues(staticId);
      if (cachedArts == null) return artifacts;
      for (Artifact artifact : cachedArts) {
         if (artifact.getBranch().equals(branch)) artifacts.add(artifact);
      }
      return artifacts;
   }

   public static Artifact getHistorical(Integer artId, Integer transactionNumber) {
      return instance.historicalArtifactIdCache.get(artId, transactionNumber);
   }

   public static Artifact getHistorical(String guid, Integer transactionNumber) {
      return instance.historicalArtifactGuidCache.get(guid, transactionNumber);
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branch
    * @return
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
    * @return
    */
   public static Artifact getActive(Integer artId, Integer branchId) {
      return instance.artifactIdCache.get(artId, branchId);
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branchId
    * @return
    */
   public static Artifact getActive(String artGuid, Integer branchId) {
      return instance.artifactGuidCache.get(artGuid, branchId);
   }

   /**
    * returns the active artifact based on the previously provided text key and branch
    * 
    * @param key
    * @param branch
    * @return
    */
   public static Artifact getByTextId(String key, Branch branch) {
      return instance.keyedArtifactCache.get(key, branch);
   }

   /**
    * used to cache an artifact based on a text identifier and its branch
    * 
    * @param key
    * @param branch
    * @param artifact
    */
   public static void putByTextId(String key, Artifact artifact) {
      instance.keyedArtifactCache.put(key, artifact.getBranch(), artifact);
   }
}
