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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.artifact.cache.ArtifactIdCache;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactCache {

   private static final ArtifactIdCache ID_CACHE = new ArtifactIdCache(2000);

   private ArtifactCache() {
      super();
   }

   /**
    * Cache the artifact so that we can avoid creating duplicate instances of an artifact
    */
   static void cache(Artifact artifact) {
      if (!artifact.isHistorical()) {
         ID_CACHE.cache(artifact);
      }
   }

   /**
    * <p>
    * This should NOT be called by applications unless extreme care is taken. Grabbing an artifact, then decaching, then
    * searching/loading the artifact by another operation can cause 2 current versions of the same artifact in the JVM.
    * This can cause problems changing the artifact and/or processing incoming events.
    * </p>
    */
   public static void deCache(Artifact artifact) {
      if (!artifact.isHistorical()) {
         ID_CACHE.deCache(artifact);
      }
   }

   /**
    * <p>
    * De-caches all artifacts from <code>HISTORICAL_CACHE</code> and <code>ACTIVE_CACHE</code> for a specific branch.
    * This method is usually called by a purge operation or at the end of a unit test/suite.</br>
    * </br>
    * This should NOT be called by applications unless extreme care is taken. Grabbing an artifact, then decaching, then
    * searching/loading the artifact by another operation can cause 2 current versions of the same artifact in the JVM.
    * This can cause problems changing the artifact and/or processing incoming events.
    * </p>
    *
    * @param branch of which artifacts (all) will be de-cache'ed.
    */
   public static void deCache(BranchId branch) {
      for (Artifact artifact : ID_CACHE.getAll()) {
         if (artifact.isOnBranch(branch)) {
            ID_CACHE.deCache(artifact);
         }
      }
   }

   public static String report() {
      StringBuilder sb = new StringBuilder();
      sb.append("Active:");
      sb.append(ID_CACHE.toString());
      return sb.toString();
   }

   public static Collection<Artifact> getDirtyArtifacts() {
      return ID_CACHE.getAllDirties();
   }

   /**
    * This method is called by attributes and relations when their dirty state changes. This way, when an artifact is
    * dirty we can hold onto a strong reference and when it is not dirty we can have a weak reference.
    */
   public static void updateCachedArtifact(ArtifactToken artifact) {
      ID_CACHE.updateReferenceType(artifact);
   }

   public static List<Artifact> getArtifactsByType(ArtifactTypeId artifactType) {
      List<Artifact> artifacts = new LinkedList<>();
      for (Artifact artifact : ID_CACHE.getAll()) {
         if (artifact.isOfType(artifactType)) {
            artifacts.add(artifact);
            deCache(artifact);
         }
      }
      return artifacts;
   }

   public static Collection<Artifact> getActive(Collection<? extends DefaultBasicGuidArtifact> basicGuidArtifacts) {
      Set<Artifact> artifacts = new HashSet<>();
      for (DefaultBasicGuidArtifact guidArt : basicGuidArtifacts) {
         Artifact art = ID_CACHE.getByGuid(guidArt.getGuid(), guidArt.getBranch());
         if (art != null) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   public static Artifact getActive(DefaultBasicGuidArtifact guidArt)  {
      return getActive(guidArt.getGuid(), guidArt.getBranch());
   }

   private static Artifact getActiveA(IBasicGuidRelation guidRel)  {
      return getActive(guidRel.getArtA().getGuid(), guidRel.getArtA().getBranch());
   }

   private static Artifact getActiveB(IBasicGuidRelation guidRel)  {
      return getActive(guidRel.getArtB().getGuid(), guidRel.getArtB().getBranch());
   }

   /**
    * Returns loaded artifacts from either side of the relation
    */
   public static Collection<Artifact> getActive(IBasicGuidRelation guidRel)  {
      return getActive(guidRel, null);
   }

   /**
    * Returns loaded artifacts from either side of the relation of type clazz
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> Collection<A> getActive(IBasicGuidRelation guidRel, Class<A> clazz)  {
      List<A> arts = new ArrayList<>();
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

   public static Artifact getActive(ArtifactId artId, BranchId branch) {
      return getActive(ArtifactToken.valueOf(artId, branch));
   }

   public static Artifact getActive(ArtifactToken artifact) {
      return ID_CACHE.getById(artifact);
   }

   public static Artifact getActive(String artGuid, BranchId branch)  {
      return ID_CACHE.getByGuid(artGuid, branch);
   }

   public static Artifact getActive(Long artId, BranchId branch) {
      return getActive(ArtifactToken.valueOf(artId, branch));
   }
}