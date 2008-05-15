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
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactCache {
   // The keys for this are <artId, transactionId>
   private final DoubleKeyHashMap<Integer, Integer, Artifact> artifactIdCache =
         new DoubleKeyHashMap<Integer, Integer, Artifact>();
   private final DoubleKeyHashMap<String, Integer, Artifact> artifactGuidCache =
         new DoubleKeyHashMap<String, Integer, Artifact>();

   private final CompositeKeyHashMap<Integer, Branch, Artifact> artifactIdBranchCache =
         new CompositeKeyHashMap<Integer, Branch, Artifact>(2000);

   private final CompositeKeyHashMap<String, Branch, Artifact> guidBranchCache =
         new CompositeKeyHashMap<String, Branch, Artifact>(2000);

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
         instance.artifactIdBranchCache.put(artifact.getArtId(), artifact.getBranch(), artifact);
         instance.guidBranchCache.put(artifact.getGuid(), artifact.getBranch(), artifact);
      } else {
         instance.artifactIdCache.put(artifact.getArtId(), artifact.getTransactionNumber(), artifact);
         instance.artifactGuidCache.put(artifact.getGuid(), artifact.getTransactionNumber(), artifact);
      }
   }

   static void deCache(Artifact artifact) {
      instance.artifactIdCache.remove(artifact.getArtId(), artifact.getTransactionNumber());
      instance.artifactGuidCache.remove(artifact.getGuid(), artifact.getTransactionNumber());
      instance.artifactIdBranchCache.remove(artifact.getArtId(), artifact.getBranch());
      instance.guidBranchCache.remove(artifact.getGuid(), artifact.getBranch());
   }

   public static Artifact get(Integer artId, Integer transactionNumber) {
      return instance.artifactIdCache.get(artId, transactionNumber);
   }

   public static Artifact get(String guid, Integer transactionNumber) {
      return instance.artifactGuidCache.get(guid, transactionNumber);
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branch
    * @return
    */
   public static Artifact get(Integer artId, Branch branch) {
      return instance.artifactIdBranchCache.get(artId, branch);
   }

   /**
    * returns the active artifact with the given guid from the given branch if it is in the cache and null
    * 
    * @param guid
    * @param branch
    * @return
    */
   public static Artifact get(String guid, Branch branch) {
      return instance.guidBranchCache.get(guid, branch);
   }
}
