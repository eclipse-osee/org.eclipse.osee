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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public abstract class ArtifactFactory {
   private final Set<ArtifactTypeToken> artifactTypes = new HashSet<>(5);

   protected ArtifactFactory(ArtifactTypeToken... artifactTypes) {
      for (ArtifactTypeToken artifactType : artifactTypes) {
         this.artifactTypes.add(artifactType);
      }
   }

   /**
    * Used to create a new artifact (one that has never been saved into the datastore)
    */
   public Artifact makeNewArtifact(BranchToken branch, ArtifactTypeToken artifactTypeId, String artifactName, String guid) {
      return makeNewArtifact(branch, artifactTypeId, artifactName, guid, null);
   }

   public Artifact makeNewArtifact(BranchToken branch, ArtifactTypeToken artifactTypeId, String artifactName, String guid, Long uuid) {
      if (guid == null) {
         guid = GUID.create();
      } else {
         Conditions.checkExpressionFailOnTrue(!GUID.isValid(guid),
            "Invalid guid [%s] during artifact creation [name: %s]", guid, artifactName);
      }

      Artifact artifact = getArtifactInstance(getNextArtifactId(uuid), guid, branch, artifactTypeId, false);

      artifact.meetMinimumAttributeCounts(true);
      ArtifactCache.cache(artifact);
      artifact.setLinksLoaded(true);

      if (Strings.isValid(artifactName)) {
         artifact.setName(artifactName);
      }

      return artifact;
   }

   public static Long getNextArtifactId(Long uuid) {
      if (ArtifactToken.USE_LONG_IDS) {
         return Lib.generateUuid();
      } else {
         return uuid == null ? ConnectionHandler.getNextSequence(OseeData.ART_ID_SEQ, true) : uuid;
      }
      
   }

   public synchronized Artifact reflectExisitingArtifact(ArtifactId artId, String guid, ArtifactTypeToken artifactType, GammaId gammaId, BranchToken branch, ModificationType modificationType, ApplicabilityId applicabilityId) {
      Artifact toReturn = internalExistingArtifact(artId, guid, artifactType, gammaId, branch, modificationType,
         applicabilityId, false, TransactionToken.SENTINEL, true);
      ArtifactCache.cache(toReturn);
      return toReturn;
   }

   /**
    * This method does not cache the artifact, ArtifactLoader will cache existing artifacts
    */
   private Artifact internalExistingArtifact(ArtifactId artId, String guid, ArtifactTypeToken artifactType, GammaId gammaId, BranchToken branch, ModificationType modType, ApplicabilityId applicabilityId, boolean historical, TransactionToken transactionId, boolean useBackingData) {
      Artifact artifact = getArtifactInstance(artId.getId(), guid, branch, artifactType, true);

      artifact.internalSetPersistenceData(gammaId, transactionId, modType, applicabilityId, historical, useBackingData);

      return artifact;
   }

   /**
    * This method does not cache the artifact, ArtifactLoader will cache existing artifacts
    */
   public synchronized Artifact loadExisitingArtifact(ArtifactId artId, String guid, ArtifactTypeToken artifactType, GammaId gammaId, BranchToken branch, TransactionToken transactionId, ModificationType modType, ApplicabilityId applicabilityId, boolean historical) {
      return internalExistingArtifact(artId, guid, artifactType, gammaId, branch, modType, applicabilityId, historical,
         transactionId, false);
   }

   /**
    * Request the factory to create a new instance of the type. The implementation of this method should not result in a
    * call to the persistence manager to acquire the <code>Artifact</code> or else an infinite loop will occur since
    * this method is used by the persistence manager when it needs a new instance of the class to work with and can not
    * come up with it on its own.
    */
   protected abstract Artifact getArtifactInstance(Long id, String guid, BranchToken branch, ArtifactTypeToken artifactType, boolean inDataStore);

   @Override
   public String toString() {
      return getClass().getName();
   }

   /**
    * Return true if this artifact factory is responsible for creating artifactType.
    */
   public boolean isResponsibleFor(ArtifactTypeToken artifactType) {
      return artifactTypes.contains(artifactType);
   }

   /**
    * Return any artifact types of artifacts that should never be garbage collected. This includes artifacts like user
    * artifacts and config artifacts that should always stay loaded for performance reasons.
    */
   public Collection<ArtifactTypeId> getEternalArtifactTypes() {
      return Collections.emptyList();
   }

   public abstract boolean isUserCreationEnabled(ArtifactTypeToken artifactType);
}
