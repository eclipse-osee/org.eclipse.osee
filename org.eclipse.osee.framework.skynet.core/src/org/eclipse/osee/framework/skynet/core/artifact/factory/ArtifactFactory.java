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
package org.eclipse.osee.framework.skynet.core.artifact.factory;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;

public abstract class ArtifactFactory<A extends Artifact> implements IArtifactFactory {
   private final int factoryId;

   protected ArtifactFactory(int factoryId) {
      super();

      if (factoryId < 1) {
         throw new IllegalStateException(this + " has not (yet) been registered");
      }
      this.factoryId = factoryId;
   }

   public A makeNewArtifact(Branch branch, ArtifactSubtypeDescriptor descriptor) throws SQLException {
      return makeNewArtifact(branch, descriptor, null, null);
   }

   protected boolean compatibleWith(ArtifactSubtypeDescriptor descriptor) {
      return descriptor.getFactory().getFactoryId() == this.factoryId;
   }

   public A makeNewArtifact(Branch branch, ArtifactSubtypeDescriptor artifactType, String guid, String humandReadableId) throws SQLException {
      if (!compatibleWith(artifactType)) {
         throw new IllegalArgumentException("The supplied descriptor is not appropriate for this factory");
      }

      A artifact = getNewArtifact(guid, humandReadableId, artifactType.getFactoryKey(), branch, artifactType);
      AttributeToTransactionOperation.meetMinimumAttributeCounts(artifact);
      artifact.onBirth();
      artifact.onInitializationComplete();

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
    * @throws SQLException
    */
   public abstract A getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch, ArtifactSubtypeDescriptor artifactType) throws SQLException;

   public int getFactoryId() {
      return factoryId;
   }

   public String toString() {
      return getClass().getName();
   }
}
