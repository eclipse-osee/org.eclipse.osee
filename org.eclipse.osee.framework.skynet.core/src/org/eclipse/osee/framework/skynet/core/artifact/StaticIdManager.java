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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class StaticIdManager {

   public static String STATIC_ID_ATTRIBUTE = "Static Id";

   /**
    * Will add the single static id value if it does not already exist. Will also cleanup if more than one exists with
    * same staticId.
    * 
    * @param artifact
    * @param staticId
    * @throws OseeCoreException
    */
   public static void setSingletonAttributeValue(Artifact artifact, String staticId) throws OseeCoreException {
      List<Attribute<String>> attributes = artifact.getAttributes(STATIC_ID_ATTRIBUTE, staticId);
      if (attributes.size() == 0) {
         artifact.addAttribute(STATIC_ID_ATTRIBUTE, staticId);
      } else if (attributes.size() > 1) {
         // keep one of the attributes
         for (int x = 1; x < attributes.size(); x++) {
            Attribute<String> attr = attributes.get(x);
            attr.delete();
         }
      }
      ArtifactCache.cachePostAttributeLoad(artifact);
   }

   public static boolean hasValue(Artifact artifact, String staticId) throws OseeCoreException {
      return artifact.getAttributesToStringList(STATIC_ID_ATTRIBUTE).contains(staticId);
   }

   /**
    * Return non-deleted artifacts with staticId
    * 
    * @param artifactTypeName
    * @param staticId
    * @param branch
    * @return artifacts
    * @throws OseeCoreException
    */
   public static Set<Artifact> getArtifacts(String artifactTypeName, String staticId, Branch branch) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      // Retrieve cached artifacts first
      for (Artifact artifact : ArtifactCache.getArtifactsByStaticId(staticId, branch)) {
         if (artifact.getArtifactTypeName().equals(artifactTypeName) && !artifact.isDeleted()) {
            artifacts.add(artifact);
         }
      }
      if (artifacts.size() > 0) {
         OseeLog.log(Activator.class, Level.FINE, "StaticId Load: [" + staticId + "][" + artifactTypeName + "]");
      }
      if (artifacts.size() == 0) {
         // Retrieve database artifacts if cache has none
         artifacts.addAll(ArtifactQuery.getArtifactsFromTypeAndAttribute(artifactTypeName, STATIC_ID_ATTRIBUTE,
               staticId, branch));
      }

      // Store results in cache
      for (Artifact artifact : artifacts) {
         if (!artifact.isDeleted()) {
            ArtifactCache.cachePostAttributeLoad(artifact);
         }
      }
      return artifacts;
   }

   public static Artifact getSingletonArtifactOrException(String artifactType, String staticId, Branch branch) throws OseeCoreException {
      Set<Artifact> artifacts = getArtifacts(artifactType, staticId, branch);
      // Exception on problems
      if (artifacts.size() == 0) {
         throw new ArtifactDoesNotExist("Can't find requested artifact \"" + staticId + "\"");
      } else if (artifacts.size() > 1) {
         throw new MultipleArtifactsExist("Expected 1 \"" + staticId + "\" artifact, retrieved " + artifacts.size());
      }
      return artifacts.iterator().next();
   }

   public static Artifact getSingletonArtifact(String artifactTypeName, String staticId, Branch branch) throws OseeCoreException {
      return getOrCreateSingletonArtifactHelper(artifactTypeName, staticId, branch, false);
   }

   /**
    * Return first artifact with staticId (multiples may exist) or create one if non exist
    * 
    * @param artifactTypeName
    * @param staticId
    * @param branch
    * @param create
    * @return artifact
    * @throws OseeCoreException
    */
   public static Artifact getOrCreateSingletonArtifact(String artifactTypeName, String staticId, Branch branch) throws OseeCoreException {
      return getOrCreateSingletonArtifactHelper(artifactTypeName, staticId, branch, true);
   }

   private static Artifact getOrCreateSingletonArtifactHelper(String artifactTypeName, String staticId, Branch branch, boolean create) throws OseeCoreException {
      Set<Artifact> artifacts = getArtifacts(artifactTypeName, staticId, branch);
      if (artifacts.size() == 0 && create) {
         Artifact artifact = ArtifactTypeManager.addArtifact(artifactTypeName, branch);
         setSingletonAttributeValue(artifact, staticId);
         return artifact;
      }
      if (artifacts.size() > 0) {
         return artifacts.iterator().next();
      }
      return null;
   }
}