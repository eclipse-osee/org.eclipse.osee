/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class ArtifactCacheQuery {

   /**
    * Return non-deleted artifacts stored in ArtifactCache. If queryIfNotFound, query artifacts and cacheByText results
    * before returning.
    */
   public static Set<Artifact> getArtifactsFromArtifactByText(IArtifactType artifactType, IAttributeType attributeType, String text, IOseeBranch branch, boolean queryIfNotFound) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<>();
      // Retrieve cached artifacts first
      for (Artifact artifact : ArtifactCache.getListByTextId(text, branch)) {
         if (artifact.isOfType(artifactType) && !artifact.isDeleted()) {
            artifacts.add(artifact);
         }
      }
      if (artifacts.size() > 0) {
         OseeLog.log(Activator.class, Level.FINE, "CacheByText Load: [" + text + "][" + artifactType + "]");
      }
      if (queryIfNotFound && artifacts.isEmpty()) {
         artifacts.clear();
         for (Artifact artifact : ArtifactQuery.getArtifactListFromTypeAndAttribute(artifactType, attributeType, text,
            branch)) {
            artifacts.add(artifact);
            ArtifactCache.cacheByTextId(text, artifact);
         }
      }
      return artifacts;
   }

   /**
    * Get artifact, query if not already found and cacheByText.
    */
   public static Artifact getSingletonArtifactByTextOrException(IArtifactType artifactType, IAttributeType attributeType, String text, IOseeBranch branch) throws OseeCoreException {
      Set<Artifact> artifacts = getArtifactsFromArtifactByText(artifactType, attributeType, text, branch, true);
      // Exception on problems
      if (artifacts.isEmpty()) {
         throw new ArtifactDoesNotExist("No artifact with [%s] [%s] found.", attributeType, text);
      } else if (artifacts.size() > 1) {
         throw new MultipleArtifactsExist("Expected 1 artifact with [%s] [%s] not %d", attributeType, text,
            artifacts.size());
      }
      return artifacts.iterator().next();
   }

   /**
    * Return singleton artifact from ArtifactCache. If queryIfNotFound, perform query and cacheByText result.
    */
   public static Artifact getSingletonArtifactByText(IArtifactType artifactType, IAttributeType attributeType, String text, IOseeBranch branch, boolean queryIfNotFound) throws OseeCoreException {
      if (queryIfNotFound) {
         return getOrCreateSingletonArtifactHelper(artifactType, attributeType, text, branch, false);
      } else {
         Set<Artifact> artifacts = getArtifactsFromArtifactByText(artifactType, attributeType, text, branch, false);
         if (artifacts.size() > 0) {
            return artifacts.iterator().next();
         }
         return null;
      }
   }

   /**
    * Return first artifact with attribute (multiples may exist) or create one if non exist
    */
   public static Artifact getOrCreateSingletonArtifactByText(IArtifactType artifactType, IAttributeType attributeType, String text, IOseeBranch branch) throws OseeCoreException {
      return getOrCreateSingletonArtifactHelper(artifactType, attributeType, text, branch, true);
   }

   /**
    * Searches for singleton artifact in cache, if not found, queries for existence.
    * 
    * @param create will create artifact and add attribute value not found
    */
   private static Artifact getOrCreateSingletonArtifactHelper(IArtifactType artifactType, IAttributeType attributeType, String text, IOseeBranch branch, boolean create) throws OseeCoreException {
      Set<Artifact> artifacts = getArtifactsFromArtifactByText(artifactType, attributeType, text, branch, true);
      if (artifacts.isEmpty() && create) {
         Artifact artifact = ArtifactTypeManager.addArtifact(artifactType, branch);
         artifact.setSingletonAttributeValue(attributeType, text);
         ArtifactCache.cacheByTextId(text, artifact);
         return artifact;
      }
      if (artifacts.size() > 0) {
         return artifacts.iterator().next();
      }
      return null;
   }

}
