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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.cache.AbstractArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.cache.ActiveArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.cache.HistoricalArtifactCache;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactCache {

   private static final HistoricalArtifactCache HISTORICAL_CACHE = new HistoricalArtifactCache(50);
   private static final ActiveArtifactCache ACTIVE_CACHE = new ActiveArtifactCache(2000);

   private ArtifactCache() {
      super();
   }

   private static AbstractArtifactCache getCache(Artifact artifact) throws OseeCoreException {
      Conditions.checkNotNull(artifact, "Artifact");
      return artifact.isHistorical() ? HISTORICAL_CACHE : ACTIVE_CACHE;
   }

   /**
    * Cache the artifact so that we can avoid creating duplicate instances of an artifact
    */
   static void cache(Artifact artifact) throws OseeCoreException {
      AbstractArtifactCache cache = getCache(artifact);
      cache.cache(artifact);
   }

   public static void deCache(Artifact artifact) throws OseeCoreException {
      AbstractArtifactCache cache = getCache(artifact);
      cache.deCache(artifact);
   }

   public static List<Artifact> getArtifactsByName(IArtifactType artifactType, String name) throws OseeCoreException {
      List<Artifact> arts = new ArrayList<Artifact>();
      for (Artifact artifact : getArtifactsByType(artifactType)) {
         if (artifact.getName().equals(name)) {
            arts.add(artifact);
         }
      }
      return arts;
   }

   public static String report() throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      sb.append("Active:");
      sb.append(ACTIVE_CACHE.toString());
      sb.append("\n");
      sb.append("Historical:");
      sb.append(HISTORICAL_CACHE.toString());
      return sb.toString();
   }

   public static Collection<Artifact> getDirtyArtifacts() throws OseeCoreException {
      return ACTIVE_CACHE.getAllDirties();
   }

   /**
    * This method is called by attributes and relations when their dirty state changes. This way, when an artifact is
    * dirty we can hold onto a strong reference and when it is not dirty we can have a weak reference.
    */
   public static void updateCachedArtifact(int artId, int branchId) throws OseeCoreException {
      ACTIVE_CACHE.updateReferenceType(artId, branchId);
   }

   static void cachePostAttributeLoad(Artifact artifact) throws OseeCoreException {
      if (!artifact.isHistorical()) {
         for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.STATIC_ID)) {
            cacheByStaticId(staticId, artifact);
         }
      }
   }

   /**
    * @returns the previous value associated with keys, or null if there was no mapping for key. (A null return can also
    *          indicate that the map previously associated null with key, if the implementation supports null values.)
    */
   public static Artifact cacheByTextId(String key, Artifact artifact) throws OseeCoreException {
      if (artifact.isHistorical()) {
         throw new OseeArgumentException(String.format("historical artifact cannot be cached by text [%s]", key));
      }
      return ACTIVE_CACHE.cacheByText(key, artifact);
   }

   public static void cacheByStaticId(String staticId, Artifact artifact) throws OseeCoreException {
      if (artifact.isHistorical()) {
         throw new OseeArgumentException(String.format("historical artifact cannot be cached by staticId [%s]",
               staticId));
      }
      ACTIVE_CACHE.cacheByStaticId(staticId, artifact);
   }

   public static void cacheByStaticId(Artifact artifact) throws OseeCoreException {
      for (String staticId : artifact.getAttributesToStringList(CoreAttributeTypes.STATIC_ID)) {
         ACTIVE_CACHE.cacheByStaticId(staticId, artifact);
      }
   }

   public static Collection<Artifact> getArtifactsByStaticId(String staticId) {
      return ACTIVE_CACHE.getByStaticId(staticId);
   }

   public static Collection<Artifact> getArtifactsByStaticId(String staticId, IOseeBranch branch) {
      return ACTIVE_CACHE.getByStaticId(staticId, branch);
   }

   public static List<Artifact> getArtifactsByType(IArtifactType artifactType) throws OseeCoreException {
      return ACTIVE_CACHE.getByType(ArtifactTypeManager.getType(artifactType));
   }

   public static Artifact getHistorical(Integer artId, Integer transactionNumber) {
      return HISTORICAL_CACHE.getById(artId, transactionNumber);
   }

   public static Artifact getHistorical(String guid, Integer transactionNumber) {
      return HISTORICAL_CACHE.getByGuid(guid, transactionNumber);
   }

   public static Artifact getActive(IBasicGuidArtifact basicGuidArtifact) throws OseeCoreException {
      return ACTIVE_CACHE.getByGuid(basicGuidArtifact.getGuid(),
            BranchManager.getBranchByGuid(basicGuidArtifact.getBranchGuid()));
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

   public static Artifact getActive(Integer artId, Branch branch) {
      return getActive(artId, branch.getId());
   }

   public static Artifact getActive(DefaultBasicGuidArtifact guidArt) throws OseeCoreException {
      return getActive(guidArt.getGuid(), BranchManager.getBranch(guidArt));
   }

   public static Artifact getActiveA(IBasicGuidRelation guidRel) throws OseeCoreException {
      return getActive(guidRel.getArtA().getGuid(), BranchManager.getBranch(guidRel.getArtA()));
   }

   public static Artifact getActiveB(IBasicGuidRelation guidRel) throws OseeCoreException {
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
         if (clazz == null || clazz.isInstance(artA)) arts.add((A) artA);
      }
      Artifact artB = getActiveB(guidRel);
      if (artB != null) {
         if (clazz == null || clazz.isInstance(artB)) arts.add((A) artB);
      }
      return arts;
   }

   public static Artifact getActive(Integer artId, IOseeBranch branch) throws OseeCoreException {
      return getActive(artId, BranchManager.getBranchId(branch));
   }

   public static Artifact getActive(Integer artId, Integer branchId) {
      return ACTIVE_CACHE.getById(artId, branchId);
   }

   public static Artifact getActive(String artGuid, Integer branchId) {
      return ACTIVE_CACHE.getByGuid(artGuid, branchId);
   }

   public static Artifact getActive(String artGuid, IOseeBranch branch) throws OseeCoreException {
      return ACTIVE_CACHE.getByGuid(artGuid, BranchManager.getBranch(branch));
   }

   /**
    * @returns the active artifact based on the previously provided text key and branch
    */
   public static Artifact getByTextId(String key, IOseeBranch branch) throws OseeCoreException {
      return ACTIVE_CACHE.getByText(key, BranchManager.getBranch(branch));
   }

}