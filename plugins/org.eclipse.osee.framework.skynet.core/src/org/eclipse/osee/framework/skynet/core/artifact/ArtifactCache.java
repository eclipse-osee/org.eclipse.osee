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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.cache.ActiveArtifactCache;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactCache {

   private static final ActiveArtifactCache ACTIVE_CACHE = new ActiveArtifactCache(2000);

   private ArtifactCache() {
      super();
   }

   /**
    * Cache the artifact so that we can avoid creating duplicate instances of an artifact
    */
   static void cache(Artifact artifact) {
      if (!artifact.isHistorical()) {
         ACTIVE_CACHE.cache(artifact);
      }
   }

   public static void deCache(Artifact artifact) {
      if (!artifact.isHistorical()) {
         ACTIVE_CACHE.deCache(artifact);
      }
   }

   /**
    * <p>
    * De-caches all artifacts from <code>HISTORICAL_CACHE</code> and <code>ACTIVE_CACHE</code> for a specific branch.
    * This method is usually called by a purge operation or at the end of a unit test/suite.
    * </p>
    * 
    * @param branch of which artifacts (all) will be de-cache'ed.
    */
   public static void deCache(IOseeBranch branch) {
      for (Artifact artifact : ACTIVE_CACHE.getAll()) {
         if (artifact.getBranch().equals(branch)) {
            ACTIVE_CACHE.deCache(artifact);
         }
      }
   }

   public static List<Artifact> getArtifactsByName(IArtifactType artifactType, String name) {
      List<Artifact> arts = new ArrayList<Artifact>();
      for (Artifact artifact : getArtifactsByType(artifactType)) {
         if (artifact.getName().equals(name)) {
            arts.add(artifact);
         }
      }
      return arts;
   }

   public static String report() {
      StringBuilder sb = new StringBuilder();
      sb.append("Active:");
      sb.append(ACTIVE_CACHE.toString());
      return sb.toString();
   }

   public static Collection<Artifact> getDirtyArtifacts() {
      return ACTIVE_CACHE.getAllDirties();
   }

   /**
    * This method is called by attributes and relations when their dirty state changes. This way, when an artifact is
    * dirty we can hold onto a strong reference and when it is not dirty we can have a weak reference.
    */
   public static void updateCachedArtifact(int artId, long branchId) {
      ACTIVE_CACHE.updateReferenceType(artId, branchId);
   }

   /**
    * @returns the previous value associated with keys, or null if there was no mapping for key. (A null return can also
    * indicate that the map previously associated null with key, if the implementation supports null values.)
    */
   public static Artifact cacheByTextId(String key, Artifact artifact) throws OseeCoreException {
      if (artifact.isHistorical()) {
         throw new OseeArgumentException("historical artifact cannot be cached by text [%s]", key);
      }
      return ACTIVE_CACHE.cacheByText(key, artifact);
   }

   public static List<Artifact> getArtifactsByType(IArtifactType artifactType) {
      return ACTIVE_CACHE.getByType(artifactType);
   }

   public static Artifact getActive(IBasicGuidArtifact basicGuidArtifact) throws OseeCoreException {
      return ACTIVE_CACHE.getByGuid(basicGuidArtifact.getGuid(),
         BranchManager.getBranchByGuid(BranchManager.getBranchGuidLegacy(basicGuidArtifact.getBranchUuid())).getId());
   }

   public static Collection<Artifact> getActive(Collection<? extends IBasicGuidArtifact> basicGuidArtifacts) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      for (IBasicGuidArtifact guidArt : basicGuidArtifacts) {
         Artifact art = getActive(guidArt);
         if (art != null) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   public static Artifact getActive(DefaultBasicGuidArtifact guidArt) throws OseeCoreException {
      return getActive(guidArt.getGuid(), BranchManager.getBranch(guidArt));
   }

   private static Artifact getActiveA(IBasicGuidRelation guidRel) throws OseeCoreException {
      return getActive(guidRel.getArtA().getGuid(), BranchManager.getBranch(guidRel.getArtA()));
   }

   private static Artifact getActiveB(IBasicGuidRelation guidRel) throws OseeCoreException {
      return getActive(guidRel.getArtB().getGuid(), BranchManager.getBranch(guidRel.getArtB()));
   }

   /**
    * Returns loaded artifacts from either side of the relation
    */
   public static Collection<Artifact> getActive(IBasicGuidRelation guidRel) throws OseeCoreException {
      return getActive(guidRel, null);
   }

   /**
    * Returns loaded artifacts from either side of the relation of type clazz
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> Collection<A> getActive(IBasicGuidRelation guidRel, Class<A> clazz) throws OseeCoreException {
      List<A> arts = new ArrayList<A>();
      Artifact artA = getActiveA(guidRel);
      if (artA != null) {
         if (clazz == null || clazz.isInstance(artA)) {
            arts.add((A) artA);
         }
      }
      Artifact artB = getActiveB(guidRel);
      if (artB != null) {
         if (clazz == null || clazz.isInstance(artB)) {
            arts.add((A) artB);
         }
      }
      return arts;
   }

   public static Artifact getActive(Integer artId, Branch branch) {
      return getActive(artId, branch.getId());
   }

   public static Artifact getActive(Integer artId, IOseeBranch branch) throws OseeCoreException {
      return getActive(artId, BranchManager.getBranchId(branch));
   }

   public static Artifact getActive(Integer artId, Long branchId) {
      return ACTIVE_CACHE.getById(artId, branchId);
   }

   public static Artifact getActive(String artGuid, IOseeBranch branch) throws OseeCoreException {
      return ACTIVE_CACHE.getByGuid(artGuid, BranchManager.getBranchId(branch));
   }

   /**
    * Return single active artifact stored by text and branch or null if none.
    * 
    * @throws OseeStateException if more than one artifact stored.
    */
   public static Artifact getByTextId(String key, IOseeBranch branch) throws OseeCoreException {
      Artifact artifact = ACTIVE_CACHE.getByText(key, BranchManager.getBranch(branch));
      // decache if deleted
      if (artifact != null && artifact.isDeleted()) {
         ACTIVE_CACHE.deCacheByText(key, branch, artifact);
      }
      return artifact;
   }

   public static Artifact deCacheByTextId(String key, IOseeBranch branch) throws OseeCoreException {
      Artifact artifact = ACTIVE_CACHE.getByText(key, BranchManager.getBranch(branch));
      ACTIVE_CACHE.deCacheByText(key, branch, artifact);
      return artifact;
   }

   public static Collection<Artifact> getListByTextId(String key, IOseeBranch branch) throws OseeCoreException {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      Collection<Artifact> cached = ACTIVE_CACHE.getListByText(key, BranchManager.getBranch(branch));
      // decache any deleted artifacts
      for (Artifact artifact : cached) {
         if (artifact.isDeleted()) {
            ACTIVE_CACHE.deCacheByText(key, branch, artifact);
         } else {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

}