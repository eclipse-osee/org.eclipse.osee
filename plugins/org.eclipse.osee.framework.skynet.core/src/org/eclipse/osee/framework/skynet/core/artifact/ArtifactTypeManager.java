/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * Contains methods specific to artifact types. All artifact methods will eventually be moved from the
 * ConfigurationPersistenceManager to here.
 *
 * @author Donald G. Dunne
 */
public class ArtifactTypeManager {

   private final static ArtifactFactoryManager factoryManager = new ArtifactFactoryManager();

   public static Artifact addArtifact(ArtifactToken artifactToken) {
      Conditions.assertTrue(artifactToken.getBranch().isValid(), "Branch must be specified.");
      return addArtifact(artifactToken, artifactToken.getBranch());
   }

   /**
    * Get a new instance of type artifactTypeName
    */
   public static Artifact addArtifact(ArtifactTypeToken artifactType, BranchToken branch) {
      return addArtifact(artifactType, branch, null, null, null);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    */
   public static Artifact addArtifact(ArtifactTypeToken artifactType, BranchToken branch, String name) {
      return addArtifact(artifactType, branch, name, null, null);
   }

   public static Artifact addArtifact(ArtifactTypeToken artifactType, BranchToken branch, String name, Long artifactId) {
      return addArtifact(artifactType, branch, name, null, artifactId);
   }

   public static Artifact addArtifact(ArtifactTypeToken artifactType, BranchToken branch, String name, ArtifactId artifact) {
      return addArtifact(artifactType, branch, name, null, artifact.getId());
   }

   public static Artifact addArtifact(ArtifactTypeToken artifactType, BranchToken branch, String name, String guid) {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, name, guid);
   }

   public static Artifact addArtifact(ArtifactTypeToken artifactType, BranchToken branch, String name, String guid, Long uuid) {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, name, guid, uuid);
   }

   public static Artifact addArtifact(ArtifactToken artifactToken, BranchToken branch) {
      return addArtifact(artifactToken.getArtifactType(), branch, artifactToken.getName(), artifactToken.getGuid(),
         artifactToken.getId());
   }

   private static final String COUNT_ARTIFACT_OCCURRENCE =
      "select count(1) from (select DISTINCT(art_id) FROM osee_artifact where art_type_id = ?) t1";

   public static void purgeArtifactType(ArtifactTypeId artifactType) {
      int artifactCount = ConnectionHandler.getJdbcClient().fetch(0, COUNT_ARTIFACT_OCCURRENCE, artifactType);

      if (artifactCount != 0) {
         throw new OseeArgumentException(
            "Can not delete artifact type [%s] because there are %d existing artifacts of this type.", artifactType,
            artifactCount);
      }
   }

   /**
    * Given a set of artifact types, they will be converted to the new artifact type and the old artifact types will be
    * purged
    *
    * @param purgeArtifactTypes types to be converted and purged
    * @param newArtifactType new type to convert any existing artifacts of the old type
    */
   public static void purgeArtifactTypesWithCheck(Collection<? extends ArtifactTypeId> purgeArtifactTypes, ArtifactTypeToken newArtifactType) {
      for (ArtifactTypeId purgeArtifactType : purgeArtifactTypes) {
         // find all artifact of this type on all branches and make a unique list for type change (since it is not by branch)
         Set<Artifact> artifacts = new LinkedHashSet<>();
         for (BranchId branch : BranchManager.getBranches(new BranchFilter())) {
            artifacts.addAll(ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SoftwareRequirementMsWord, branch,
               DeletionFlag.INCLUDE_DELETED));
         }
         if (artifacts.size() > 0) {
            if (newArtifactType == null) {
               HashSet<BranchId> branches = new HashSet<>();
               for (Artifact artifact : artifacts) {
                  branches.add(artifact.getBranch());
               }
               throw new OseeStateException("Found %d artifact references of type [%s] on branches [%s]",
                  artifacts.size(), purgeArtifactType, branches);
            } else {
               ChangeArtifactType.changeArtifactType(artifacts, newArtifactType, true);
            }
         }
         purgeArtifactType(purgeArtifactType);
      }
   }

   public static ArtifactFactory getFactory(ArtifactTypeToken artifactType) {
      if (artifactType == null) {
         throw new OseeArgumentException("Artifact Type cannot be null");
      }
      return factoryManager.getFactory(artifactType);
   }

   public static boolean isUserCreationAllowed(ArtifactTypeToken artifactType) {
      boolean userCreationoAllowed = false;
      ArtifactFactory factory = factoryManager.getFactory(artifactType);
      if (factory != null && factory.isUserCreationEnabled(artifactType)) {
         userCreationoAllowed = true;
      } else if (factory == null) {
         userCreationoAllowed = true;
      }
      return userCreationoAllowed;
   }
}