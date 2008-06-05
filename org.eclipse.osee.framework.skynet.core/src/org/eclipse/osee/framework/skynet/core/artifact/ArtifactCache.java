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

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;

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

   private static final ArtifactCache instance = new ArtifactCache();

   private ArtifactCache() {
   }

   /**
    * Cache the artifact so that we can avoid creating duplicate instances of an artifact
    * 
    * @param artifact
    */
   static void cache(Artifact artifact) {
      if (artifact.isLive()) {
         instance.artifactIdCache.put(artifact.getArtId(), artifact.getBranch().getBranchId(), artifact);
         instance.artifactGuidCache.put(artifact.getGuid(), artifact.getBranch().getBranchId(), artifact);
      } else {
         instance.historicalArtifactIdCache.put(artifact.getArtId(), artifact.getTransactionNumber(), artifact);
         instance.historicalArtifactGuidCache.put(artifact.getGuid(), artifact.getTransactionNumber(), artifact);
      }
   }

   static void deCache(Artifact artifact) {
      instance.historicalArtifactIdCache.remove(artifact.getArtId(), artifact.getTransactionNumber());
      instance.historicalArtifactGuidCache.remove(artifact.getGuid(), artifact.getTransactionNumber());
      instance.artifactIdCache.remove(artifact.getArtId(), artifact.getBranch().getBranchId());
      instance.artifactGuidCache.remove(artifact.getGuid(), artifact.getBranch().getBranchId());
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
