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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public abstract class ArtifactFactory {

   private final Set<IArtifactType> artifactTypeNames = new HashSet<IArtifactType>(5);

   protected ArtifactFactory(IArtifactType... artifactTypes) {
      for (IArtifactType artifactType : artifactTypes) {
         registerAsResponsible(artifactType);
      }
   }

   public Artifact makeNewArtifact(IOseeBranch branch, IArtifactType artifactTypeToken, String guid) throws OseeCoreException {
      return makeNewArtifact(branch, artifactTypeToken, null, guid);
   }

   /**
    * Used to create a new artifact (one that has never been saved into the datastore)
    */
   public Artifact makeNewArtifact(IOseeBranch branch, IArtifactType artifactTypeToken, String artifactName, String guid) throws OseeCoreException {
      ArtifactType artifactType = ArtifactTypeManager.getType(artifactTypeToken);

      Conditions.checkExpressionFailOnTrue(artifactType.isAbstract(),
         "Cannot create an instance of abstract type [%s]", artifactType);

      if (guid == null) {
         guid = GUID.create();
      } else {
         Conditions.checkExpressionFailOnTrue(!GUID.isValid(guid),
            "Invalid guid [%s] during artifact creation [name: %s]", guid, artifactName);
      }

      Artifact artifact = getArtifactInstance(guid, BranchManager.getBranch(branch), artifactType, false);

      artifact.setArtId(ConnectionHandler.getSequence().getNextArtifactId());
      artifact.meetMinimumAttributeCounts(true);
      ArtifactCache.cache(artifact);
      artifact.setLinksLoaded(true);

      if (Strings.isValid(artifactName)) {
         artifact.setName(artifactName);
      }

      return artifact;
   }

   public synchronized Artifact reflectExisitingArtifact(int artId, String guid, IArtifactType artifactType, int gammaId, IOseeBranch branch, ModificationType modificationType) throws OseeCoreException {
      Artifact toReturn =
         internalExistingArtifact(artId, guid, artifactType, gammaId, branch, modificationType, false,
            Artifact.TRANSACTION_SENTINEL, true);
      ArtifactCache.cache(toReturn);
      return toReturn;
   }

   /**
    * This method does not cache the artifact, ArtifactLoader will cache existing artifacts
    */
   private Artifact internalExistingArtifact(int artId, String guid, IArtifactType artifactType, int gammaId, IOseeBranch branch, ModificationType modType, boolean historical, int transactionId, boolean useBackingData) throws OseeCoreException {
      Artifact artifact = getArtifactInstance(guid, BranchManager.getBranch(branch), artifactType, true);

      artifact.setArtId(artId);
      artifact.internalSetPersistenceData(gammaId, transactionId, modType, historical, useBackingData);

      return artifact;
   }

   /**
    * This method does not cache the artifact, ArtifactLoader will cache existing artifacts
    */
   public synchronized Artifact loadExisitingArtifact(int artId, String guid, IArtifactType artifactType, int gammaId, Branch branch, int transactionId, ModificationType modType, boolean historical) throws OseeCoreException {
      return internalExistingArtifact(artId, guid, artifactType, gammaId, branch, modType, historical, transactionId,
         false);
   }

   /**
    * Request the factory to create a new instance of the type. The implementation of this method should not result in a
    * call to the persistence manager to acquire the <code>Artifact</code> or else an infinite loop will occur since
    * this method is used by the persistence manager when it needs a new instance of the class to work with and can not
    * come up with it on its own.
    * 
    * @param branch branch on which this instance of this artifact will be associated
    */
   protected abstract Artifact getArtifactInstance(String guid, Branch branch, IArtifactType artifactType, boolean inDataStore) throws OseeCoreException;

   @Override
   public String toString() {
      return getClass().getName();
   }

   /**
    * Return true if this artifact factory is responsible for creating artifactType.
    */
   public boolean isResponsibleFor(IArtifactType artifactType) {
      return artifactTypeNames.contains(artifactType);
   }

   protected void registerAsResponsible(IArtifactType artifactType) {
      if (!artifactTypeNames.contains(artifactType)) {
         artifactTypeNames.add(artifactType);
      }
   }

   /**
    * Return any artifact types of artifacts that should never be garbage collected. This includes artifacts like user
    * artifacts and config artifacts that should always stay loaded for performance reasons.
    */
   public Collection<IArtifactType> getEternalArtifactTypes() {
      return Collections.emptyList();
   }
}
