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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceMemo;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactToLoadDescription;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

public abstract class ArtifactFactory<A extends Artifact> implements IArtifactFactory {
   public static ArtifactFactory<?> factory = null;
   private static final ConfigurationPersistenceManager configurationManger =
         ConfigurationPersistenceManager.getInstance();

   private final int factoryId;

   // The keys for this are <artId, transactionId>
   protected DoubleKeyHashMap<Integer, TransactionId, A> artifactIdCache;
   protected DoubleKeyHashMap<String, TransactionId, A> artifactGuidCache;
   protected DoubleKeyHashMap<Integer, Integer, A> artifactBranchCache;

   protected static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   protected ArtifactFactory(int factoryId) {
      super();
      this.artifactIdCache = new DoubleKeyHashMap<Integer, TransactionId, A>();
      this.artifactGuidCache = new DoubleKeyHashMap<String, TransactionId, A>();
      this.artifactBranchCache = new DoubleKeyHashMap<Integer, Integer, A>();

      if (factoryId < 1) {
         throw new IllegalStateException(this + " has not (yet) been registered");
      }
      this.factoryId = factoryId;
   }

   public A makeNewArtifact(ArtifactSubtypeDescriptor descriptor) throws SQLException {
      return makeNewArtifact(descriptor, null, null);
   }

   protected boolean compatibleWith(ArtifactSubtypeDescriptor descriptor) {
      return descriptor.getFactory().getFactoryId() == this.factoryId;
   }

   public A makeNewArtifact(ArtifactSubtypeDescriptor descriptor, String guid, String humandReadableId) throws SQLException {
      if (!compatibleWith(descriptor)) {
         throw new IllegalArgumentException("The supplied descriptor is not appropriate for this factory");
      }

      A artifact =
            getNewArtifact(guid, humandReadableId, descriptor.getFactoryKey(),
                  descriptor.getTransactionId().getBranch());

      // For now, the only difference we make is the ID, all other initialization is the same
      artifact.setDescriptor(descriptor);
      artifact.onBirth();
      artifact.onInitializationComplete();

      return artifact;
   }

   /**
    * @param artifact
    */
   public void reSetCache(Artifact artifact) {
      deCache(artifact);
      cache(artifact);
   }

   /**
    * Make the factory aware of an artifact. This is necessary when a new Artifact is persisted.
    * 
    * @param artifact
    */
   @SuppressWarnings("unchecked")
   public void cache(Artifact artifact) {
      artifactIdCache.put(artifact.getArtId(), artifact.getPersistenceMemo().getTransactionId(), (A) artifact);
      artifactGuidCache.put(artifact.getGuid(), artifact.getPersistenceMemo().getTransactionId(), (A) artifact);
      artifactBranchCache.put(artifact.getArtId(), artifact.getBranch().getBranchId(), null);
   }

   @SuppressWarnings("unchecked")
   public void deCache(Artifact artifact) {
      artifactIdCache.remove(artifact.getArtId(), artifact.getPersistenceMemo().getTransactionId());
      artifactGuidCache.remove(artifact.getGuid(), artifact.getPersistenceMemo().getTransactionId());
      artifactBranchCache.remove(artifact.getArtId(), artifact.getBranch().getBranchId());
   }

   /**
    * Request the factory to create a new instance of the type. The implementation of this method should not result in a
    * call to the persistence manager to acquire the <code>Artifact</code> or else an infinite loop will occur since
    * this method is used by the persistence manager when it needs a new instance of the class to work with and can not
    * come up with it on its own.
    * 
    * @param branch TODO
    * @return Return artifact reference
    * @throws SQLException
    */
   public abstract A getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException;

   public A getArtifact(int artId, String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      TransactionId transactionId = transactionIdManager.getEditableTransactionId(branch);
      if (transactionId == null) return null;

      return getArtifact(artId, guid, humandReadableId, factoryKey, branch, transactionId);
   }

   public A getArtifact(int artId, String guid, String humandReadableId, String factoryKey, Branch branch, TransactionId transactionId) throws SQLException {
      // First try to acquire the artifact from cache
      A artifact = artifactIdCache.get(artId, transactionId);

      // If it wasn't found, then it must be acquired from the database or created
      if (artifact == null) {
         artifact = getNewArtifact(guid, humandReadableId, factoryKey, branch);

         artifactManager.initializeArtifact(artId, artifact, transactionId);
         cache(artifact);
         artifact.onInitializationComplete();
      }

      return artifact;
   }

   public Collection<Artifact> getArtifacts(Collection<ArtifactToLoadDescription> artifactsToGet, TransactionId transactionId) throws SQLException {
      Collection<Artifact> artifacts = new ArrayList<Artifact>(artifactsToGet.size());
      Collection<Artifact> artifactsToInit = new LinkedList<Artifact>();

      A artifact;
      ArtifactToLoadDescription desc;

      // Determine which ones must be loaded since some may already be cached
      Iterator<ArtifactToLoadDescription> iter = artifactsToGet.iterator();
      while (iter.hasNext()) {
         desc = iter.next();

         artifact = artifactIdCache.get(desc.getArtId(), transactionId);
         if (artifact == null) {
            artifact =
                  getNewArtifact(desc.getGuid(), desc.getHumandReadableId(), desc.getFactoryKey(),
                        transactionId.getBranch());
            artifact.setPersistenceMemo(new ArtifactPersistenceMemo(transactionId, desc.getArtId(), desc.getGammaId()));
            artifact.setDescriptor(configurationManger.getArtifactSubtypeDescriptor(desc.getArtTypeId(), transactionId));
            cache(artifact);
            artifactsToInit.add(artifact);
         }
         artifacts.add(artifact);
      }
      artifactManager.initializeArtifacts(artifactsToInit, transactionId);

      return artifacts;
   }

   public A getArtifact(int artId, TransactionId transactionId) {
      return artifactIdCache.get(artId, transactionId);
   }

   public A getArtifact(String guid, TransactionId transactionId) {
      return artifactGuidCache.get(guid, transactionId);
   }

   public int getFactoryId() {
      return factoryId;
   }

   public boolean containsArtifact(int artId, int branchId) {
      return artifactBranchCache.containsKey(artId, branchId);
   }

   public String toString() {
      return getClass().getName();
   }
}
