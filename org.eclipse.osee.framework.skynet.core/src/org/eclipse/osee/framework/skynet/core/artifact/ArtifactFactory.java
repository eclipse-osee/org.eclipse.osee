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

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

public abstract class ArtifactFactory {
   private final int factoryId;

   protected ArtifactFactory(int factoryId) {
      super();

      if (factoryId < 1) {
         throw new IllegalStateException(this + " has not (yet) been registered");
      }
      this.factoryId = factoryId;
   }

   protected boolean compatibleWith(ArtifactType descriptor) {
      return descriptor.getFactory().getFactoryId() == this.factoryId;
   }

   /**
    * Used to create a new artifact (one that has never been saved into the datastore)
    * 
    * @param branch
    * @param artifactType
    * @param guid
    * @param humandReadableId
    * @param earlyArtifactInitialization TODO
    * @return the new artifact instance
    */
   public Artifact makeNewArtifact(Branch branch, ArtifactType artifactType, String guid, String humandReadableId, ArtifactProcessor earlyArtifactInitialization) throws OseeCoreException {
      if (!compatibleWith(artifactType)) {
         throw new IllegalArgumentException("The supplied descriptor is not appropriate for this factory");
      }

      Artifact artifact =
            getArtifactInstance(guid, humandReadableId, artifactType.getFactoryKey(), branch, artifactType);

      artifact.setArtId(SequenceManager.getNextArtifactId());
      if (earlyArtifactInitialization != null) {
         earlyArtifactInitialization.run(artifact);
      }
      artifact.meetMinimumAttributeCounts(true);
      ArtifactCache.cache(artifact);
      artifact.setLinksLoaded();
      artifact.onBirth();
      artifact.onInitializationComplete();

      return artifact;
   }

   public synchronized Artifact loadExisitingArtifact(int artId, String guid, String humandReadableId, ArtifactType artifactType, int gammaId, TransactionId transactionId, ModificationType modType, boolean historical) throws OseeCoreException {
      Artifact artifact =
            getArtifactInstance(guid, humandReadableId, artifactType.getFactoryKey(), transactionId.getBranch(),
                  artifactType);

      artifact.setArtId(artId);
      artifact.internalSetPersistenceData(gammaId, transactionId, modType, historical);

      ArtifactCache.cache(artifact);
      return artifact;
   }

   /**
    * Request the factory to create a new instance of the type. The implementation of this method should not result in a
    * call to the persistence manager to acquire the <code>Artifact</code> or else an infinite loop will occur since
    * this method is used by the persistence manager when it needs a new instance of the class to work with and can not
    * come up with it on its own.
    * 
    * @param branch branch on which this instance of this artifact will be associated
    * @return Return artifact reference
    */
   protected abstract Artifact getArtifactInstance(String guid, String humandReadableId, String factoryKey, Branch branch, ArtifactType artifactType);

   public int getFactoryId() {
      return factoryId;
   }

   public String toString() {
      return getClass().getName();
   }
}
