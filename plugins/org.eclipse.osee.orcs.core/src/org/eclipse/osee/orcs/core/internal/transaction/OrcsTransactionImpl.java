/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.data.GraphWriteable;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTransactionImpl implements OrcsTransaction, TransactionData {

   private final Log logger;
   private final IOseeBranch branch;
   private final BranchDataStore dataStore;
   private final ArtifactFactory artifactFactory;

   private String comment;
   private ArtifactReadable authorArtifact;
   private final Map<String, ArtifactWriteable> writeableArtifacts = new ConcurrentHashMap<String, ArtifactWriteable>();

   public OrcsTransactionImpl(Log logger, SessionContext sessionContext, BranchDataStore dataStore, ArtifactFactory artifactFactory, IOseeBranch branch) {
      super();
      this.logger = logger;
      this.dataStore = dataStore;
      this.artifactFactory = artifactFactory;
      this.branch = branch;
   }

   @Override
   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setAuthor(ArtifactReadable authorArtifact) {
      this.authorArtifact = authorArtifact;
   }

   @Override
   public ArtifactReadable getAuthor() {
      return authorArtifact;
   }

   @Override
   public String getComment() {
      return comment;
   }

   @Override
   public Collection<ArtifactWriteable> getWriteables() {
      return writeableArtifacts.values();
   }

   @Override
   public void rollback() {
      // TODO
      // ? 
   }

   private void startCommit() {
      // TODO
   }

   private void closeCommit() {
      // TODO
   }

   @Override
   public ITransaction commit() throws OseeCoreException {
      ITransaction transaction = null;
      try {
         startCommit();
         Callable<ITransaction> callable = dataStore.commitTransaction(this);
         transaction = callable.call();
      } catch (Exception ex) {
         rollback();
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         closeCommit();
      }
      return transaction;
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

   private synchronized void addWriteable(ArtifactWriteable writeable) {
      writeableArtifacts.put(writeable.getGuid(), writeable);
   }

   @Override
   public synchronized ArtifactWriteable asWritable(ArtifactReadable readable) throws OseeCoreException {
      String guid = readable.getGuid();
      ArtifactWriteable toReturn = writeableArtifacts.get(guid);
      if (toReturn == null) {
         toReturn = artifactFactory.asWriteableArtifact(readable);
         addWriteable(toReturn);
      }
      return toReturn;
   }

   @Override
   public List<ArtifactWriteable> asWritable(Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException {
      List<ArtifactWriteable> toReturn = new ArrayList<ArtifactWriteable>();
      for (ArtifactReadable readable : artifacts) {
         toReturn.add(asWritable(readable));
      }
      return toReturn;
   }

   @Override
   public ArtifactWriteable createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException {
      ArtifactWriteable artifact = artifactFactory.createWriteableArtifact(getBranch(), artifactType, guid);
      artifact.setName(name);
      addWriteable(artifact);
      return artifact;
   }

   @Override
   public ArtifactWriteable duplicateArtifact(ArtifactReadable source, Collection<? extends IAttributeType> types) throws OseeCoreException {
      ArtifactWriteable toReturn = artifactFactory.copyArtifact(source, types, getBranch());
      addWriteable(toReturn);
      return toReturn;
   }

   @Override
   public ArtifactWriteable introduceArtifact(ArtifactReadable source) throws OseeCoreException {
      ArtifactWriteable toReturn = artifactFactory.introduceArtifact(source, getBranch());
      addWriteable(toReturn);
      return toReturn;
   }

   @Override
   public ArtifactWriteable createArtifact(IArtifactType artifactType, String name) throws OseeCoreException {
      return createArtifact(artifactType, name, null);
   }

   @Override
   public ArtifactWriteable createArtifact(IArtifactToken artifactToken) throws OseeCoreException {
      return createArtifact(artifactToken.getArtifactType(), artifactToken.getName(), artifactToken.getGuid());
   }

   @Override
   public ArtifactWriteable duplicateArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException {
      return duplicateArtifact(sourceArtifact, sourceArtifact.getExistingAttributeTypes());
   }

   @Override
   public GraphWriteable asWriteableGraph(GraphReadable readableGraph) throws OseeCoreException {
      //TX_TODO
      return null;
   }

   @Override
   public void deleteArtifact(ArtifactWriteable artifact) throws OseeCoreException {
      //TX_TODO
      //      public static void deleteArtifact(SkynetTransaction transaction, boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException {
      //         deleteArtifactCollection(transaction, overrideDeleteCheck, Arrays.asList(artifacts));
      //      }
      //
      //      public static void deleteArtifactCollection(SkynetTransaction transaction, boolean overrideDeleteCheck, final Collection<Artifact> artifacts) throws OseeCoreException {
      //         if (artifacts.isEmpty()) {
      //            return;
      //         }
      //
      //         if (!overrideDeleteCheck) {
      //            performDeleteChecks(artifacts);
      //         }
      //
      //         bulkLoadRelatives(artifacts);
      //
      //         boolean reorderRelations = true;
      //         for (Artifact artifact : artifacts) {
      //            deleteTrace(artifact, transaction, reorderRelations);
      //         }
      //      }
      //      private static void deleteTrace(Artifact artifact, SkynetTransaction transaction, boolean reorderRelations) throws OseeCoreException {
      //         if (!artifact.isDeleted()) {
      //            // This must be done first since the the actual deletion of an
      //            // artifact clears out the link manager
      //            for (Artifact childArtifact : artifact.getChildren()) {
      //               deleteTrace(childArtifact, transaction, false);
      //            }
      //            try {
      //               // calling deCache here creates a race condition when the handleRelationModifiedEvent listeners fire - RS
      //               //          ArtifactCache.deCache(artifact);
      //               artifact.internalSetDeleted();
      //               RelationManager.deleteRelationsAll(artifact, reorderRelations, transaction);
      //               if (transaction != null) {
      //                  artifact.persist(transaction);
      //               }
      //            } catch (OseeCoreException ex) {
      //               artifact.resetToPreviousModType();
      //               throw ex;
      //            }
      //         }
      //      }
   }

}
