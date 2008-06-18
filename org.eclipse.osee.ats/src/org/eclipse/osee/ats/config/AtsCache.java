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
package org.eclipse.osee.ats.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * Common cache storage for ATS configuration artifacts:<br>
 * TeamDefinitionArtifact<br>
 * VersionArtifact<br>
 * ActionableItemArtifact<br>
 * All other artifact types will silently not cached
 * 
 * @author Donald G. Dunne
 */
public class AtsCache {

   private final Set<Artifact> cache = new HashSet<Artifact>();
   private static final AtsCache instance = new AtsCache();
   private static List<String> cacheTypes =
         Arrays.asList(ActionableItemArtifact.ARTIFACT_NAME, TeamDefinitionArtifact.ARTIFACT_NAME,
               VersionArtifact.ARTIFACT_NAME);

   public static void cache(Artifact artifact) throws OseeCoreException {
      if (cacheTypes.contains(artifact.getArtifactTypeName())) instance.cache.add(artifact);
   }

   public static void deCache(Artifact artifact) {
      instance.cache.remove(artifact);
   }

   @SuppressWarnings("unchecked")
   public static <A> List<A> getArtifactsByActive(Active active, Class<A> clazz) {
      BulkLoadAtsCache.run(true);
      List<A> arts = new ArrayList<A>();
      for (Artifact art : instance.cache) {
         if (!art.isDeleted() && art.getClass().isInstance(clazz)) {
            arts.add((A) art);
         }
      }
      return arts;
   }

   @SuppressWarnings("unchecked")
   public static <A> List<A> getArtifactsByName(String name, Class<A> clazz) {
      BulkLoadAtsCache.run(true);
      List<A> arts = new ArrayList<A>();
      for (Artifact art : instance.cache) {
         if (!art.isDeleted() && art.getClass().isInstance(clazz) && art.getDescriptiveName().equals(name)) {
            arts.add((A) art);
         }
      }
      return arts;
   }

   public static <A> A getSoleArtifactByName(String name, Class<A> clazz) throws MultipleArtifactsExist, ArtifactDoesNotExist {
      List<A> arts = getArtifactsByName(name, clazz);
      if (arts.size() == 1) {
         return (A) arts.iterator().next();
      }
      return null;
   }
}
