/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core;

import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Simple creation and access point for artifact instances required by the framework
 * 
 * @author Ryan D. Brooks
 */
public final class OseeSystemArtifacts {
   public static final String DEFAULT_HIERARCHY_ROOT_NAME = "Default Hierarchy Root";
   public static final String ROOT_ARTIFACT_TYPE_NAME = "Root Artifact";

   public static Artifact getGlobalPreferenceArtifact() throws OseeCoreException {
      return getCachedArtifact("Global Preferences", "Global Preferences", BranchManager.getCommonBranch());
   }

   public static Artifact getDefaultHierarchyRootArtifact(Branch branch) throws OseeCoreException {
      return getCachedArtifact(ROOT_ARTIFACT_TYPE_NAME, DEFAULT_HIERARCHY_ROOT_NAME, branch);
   }

   public static Artifact createGlobalPreferenceArtifact() throws OseeCoreException {
      return ArtifactTypeManager.addArtifact("Global Preferences", BranchManager.getCommonBranch(),
            "Global Preferences");
   }

   /**
    * @param artifactTypeName
    * @param artifactName
    * @param branch
    * @return the artifact specified by type, name, and branch from the cache if available otherwise the datastore is
    *         accessed, and finally a new artifact is created if it can not be found
    * @throws OseeCoreException
    */
   public static Artifact getOrCreateArtifact(String artifactTypeName, String artifactName, Branch branch) throws OseeCoreException {
      return getOrCreateCachedArtifact(artifactTypeName, artifactName, branch, true);
   }

   public static Artifact getCachedArtifact(String artifactTypeName, String artifactName, Branch branch) throws OseeCoreException {
      return getOrCreateCachedArtifact(artifactTypeName, artifactName, branch, false);
   }

   private static Artifact getOrCreateCachedArtifact(String artifactTypeName, String artifactName, Branch branch, boolean create) throws OseeCoreException {
      Artifact artifact = ArtifactCache.getByTextId(artifactTypeName + "." + artifactName, branch);
      if (artifact == null) {
         artifact = ArtifactQuery.checkArtifactFromTypeAndName(artifactTypeName, artifactName, branch);
         if (artifact == null && create) {
            artifact = ArtifactTypeManager.addArtifact(artifactTypeName, branch, artifactName);
         }
         if (artifact == null) {
            throw new ArtifactDoesNotExist(
                  "Artifact of type " + artifactTypeName + "with name " + artifactName + " does not exist on branch" + branch);
         }
         ArtifactCache.cacheByTextId(artifactTypeName + "." + artifactName, artifact);
      }
      return artifact;
   }
}