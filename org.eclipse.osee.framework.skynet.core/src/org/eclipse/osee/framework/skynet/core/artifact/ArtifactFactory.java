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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public abstract class ArtifactFactory {

   private final Collection<String> artifactTypeNames;

   protected ArtifactFactory(Collection<String> artifactTypeNames) {
      this.artifactTypeNames = artifactTypeNames;
   }

   protected ArtifactFactory(String artifactTypeName) {
      this(Arrays.asList(artifactTypeName));
   }

   protected ArtifactFactory() {
      super();
      this.artifactTypeNames = null;
   }

   /**
    * Used to create a new artifact (one that has never been saved into the datastore)
    */
   public Artifact makeNewArtifact(Branch branch, ArtifactType artifactType, String guid, String humandReadableId, ArtifactProcessor earlyArtifactInitialization) throws OseeCoreException {
      if (artifactType.isAbstract()) {
         throw new OseeArgumentException(String.format("Cannot create an instance of abstract type [%s]",
               artifactType.getName()));
      }
      Artifact artifact = getArtifactInstance(guid, humandReadableId, branch, artifactType);

      artifact.setArtId(SequenceManager.getNextArtifactId());
      if (earlyArtifactInitialization != null) {
         earlyArtifactInitialization.run(artifact);
      }
      artifact.meetMinimumAttributeCounts(true);
      ArtifactCache.cache(artifact);
      artifact.setLinksLoaded(true);
      artifact.onBirth();
      artifact.onInitializationComplete();

      return artifact;
   }

   public synchronized Artifact reflectExisitingArtifact(int artId, String guid, String humandReadableId, ArtifactType artifactType, int gammaId, Branch branch, ModificationType modificationType) throws OseeCoreException {
      return internalExistingArtifact(artId, guid, humandReadableId, artifactType, gammaId, branch, modificationType,
            false, null);
   }

   private Artifact internalExistingArtifact(int artId, String guid, String humandReadableId, ArtifactType artifactType, int gammaId, Branch branch, ModificationType modType, boolean historical, TransactionId transactionId) throws OseeCoreException {
      Artifact artifact = getArtifactInstance(guid, humandReadableId, branch, artifactType);

      artifact.setArtId(artId);
      artifact.internalSetPersistenceData(gammaId, transactionId, modType, historical);

      ArtifactCache.cache(artifact);
      return artifact;
   }

   public synchronized Artifact loadExisitingArtifact(int artId, String guid, String humandReadableId, ArtifactType artifactType, int gammaId, TransactionId transactionId, ModificationType modType, boolean historical) throws OseeCoreException {
      return internalExistingArtifact(artId, guid, humandReadableId, artifactType, gammaId, transactionId.getBranch(),
            modType, historical, transactionId);
   }

   /**
    * Request the factory to create a new instance of the type. The implementation of this method should not result in a
    * call to the persistence manager to acquire the <code>Artifact</code> or else an infinite loop will occur since
    * this method is used by the persistence manager when it needs a new instance of the class to work with and can not
    * come up with it on its own.
    * 
    * @param branch branch on which this instance of this artifact will be associated
    */
   protected abstract Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException;

   @Override
   public String toString() {
      return getClass().getName();
   }

   /**
    * Return true if this artifact factory is responsible for creating artifactType.
    */
   public boolean isResponsibleFor(String artifactTypeName) throws OseeDataStoreException {
      return artifactTypeNames != null && artifactTypeNames.contains(artifactTypeName);
   }

   /**
    * Return any artifact types of artifacts that should never be garbage collected. This includes artifacts like user
    * artifacts and config artifacts that should always stay loaded for performance reasons.
    */
   public Collection<ArtifactType> getEternalArtifactTypes() throws OseeCoreException {
      return Collections.emptyList();
   }
}
