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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
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
      return getCachedArtifact(CoreArtifactTypes.GlobalPreferences, CoreArtifactTypes.GlobalPreferences.getName(),
            BranchManager.getCommonBranch());
   }

   public static Artifact getDefaultHierarchyRootArtifact(IOseeBranch branch) throws OseeCoreException {
      return getCachedArtifact(CoreArtifactTypes.RootArtifact, DEFAULT_HIERARCHY_ROOT_NAME, branch);
   }

   public static Artifact createGlobalPreferenceArtifact() throws OseeCoreException {
      return ArtifactTypeManager.addArtifact(CoreArtifactTypes.GlobalPreferences, BranchManager.getCommonBranch(),
            CoreArtifactTypes.GlobalPreferences.getName());
   }

   /**
    * @param artifactTypeName
    * @param artifactName
    * @param branch
    * @return the artifact specified by type, name, and branch from the cache if available otherwise the datastore is
    *         accessed, and finally a new artifact is created if it can not be found
    * @throws OseeCoreException
    */
   public static Artifact getOrCreateArtifact(IArtifactType artifactType, String artifactName, Branch branch) throws OseeCoreException {
      return getOrCreateCachedArtifact(artifactType, artifactName, branch, true);
   }

   public static Artifact getCachedArtifact(IArtifactType artifactType, String artifactName, IOseeBranch branch) throws OseeCoreException {
      return getOrCreateCachedArtifact(artifactType, artifactName, branch, false);
   }

   private static Artifact getOrCreateCachedArtifact(IArtifactType artifactType, String artifactName, IOseeBranch branch, boolean create) throws OseeCoreException {
      Artifact artifact = ArtifactCache.getByTextId(artifactType.getName() + "." + artifactName, branch);
      if (artifact == null) {
         artifact = ArtifactQuery.checkArtifactFromTypeAndName(artifactType, artifactName, branch);
         if (artifact == null && create) {
            artifact = ArtifactTypeManager.addArtifact(artifactType, branch, artifactName);
         }
         if (artifact == null) {
            throw new ArtifactDoesNotExist(
                  "Artifact of type " + artifactType + " with name " + artifactName + " does not exist on branch " + branch);
         }
         ArtifactCache.cacheByTextId(artifactType.getName() + "." + artifactName, artifact);
      }
      return artifact;
   }
}