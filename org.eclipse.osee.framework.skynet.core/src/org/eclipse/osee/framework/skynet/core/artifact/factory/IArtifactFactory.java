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
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactToLoadDescription;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * Defines the necessary methods for being an artifact factory.
 * 
 * @author Robert A. Fisher
 */
public interface IArtifactFactory {

   public abstract void cache(Artifact artifact);

   public abstract void deCache(Artifact artifact);

   public abstract Artifact makeNewArtifact(ArtifactSubtypeDescriptor descriptor) throws SQLException;

   public abstract Artifact makeNewArtifact(ArtifactSubtypeDescriptor descriptor, String guid, String humandReadableId) throws SQLException;

   public abstract Artifact getArtifact(int artId, String guid, String humandReadableId, String factoryKey, Branch tag, TransactionId transactionId) throws SQLException;

   /**
    * Hit the cache to check for the artifact, this will return null if the artId is not cached.
    */
   public abstract Artifact getArtifact(int artId, TransactionId transactionId);

   public abstract Artifact getArtifact(String guid, TransactionId transactionId);

   public abstract Collection<Artifact> getArtifacts(Collection<ArtifactToLoadDescription> artifactsToGet, TransactionId transactionId) throws SQLException;

   public abstract int getFactoryId();

   public abstract Artifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException;
}
