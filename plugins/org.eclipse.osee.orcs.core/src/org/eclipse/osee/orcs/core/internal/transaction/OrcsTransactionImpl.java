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
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTransactionImpl implements OrcsTransaction {

   @SuppressWarnings("unused")
   private final Log logger;
   private final OrcsSession session;
   private final TxDataStore txDataStore;
   private final ArtifactProxyFactory factory;

   private final IOseeBranch branch;
   private String comment;
   private ArtifactReadable authorArtifact;

   private final TxDataManager manager;

   private volatile boolean isCommitInProgress;

   public OrcsTransactionImpl(Log logger, OrcsSession session, TxDataStore txDataStore, ArtifactProxyFactory factory, TxDataManager manager, IOseeBranch branch) {
      super();
      this.logger = logger;
      this.session = session;
      this.txDataStore = txDataStore;
      this.factory = factory;
      this.manager = manager;
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

   protected void startCommit() throws OseeCoreException {
      isCommitInProgress = true;
      manager.onCommitStart();
   }

   private void rollback() throws OseeCoreException {
      manager.onCommitRollback();
   }

   private void commitSuccess(TransactionResult result) throws OseeCoreException {
      manager.onCommitSuccess(result);
   }

   private void endCommit() throws OseeCoreException {
      isCommitInProgress = false;
      manager.onCommitEnd();
   }

   @Override
   public boolean isCommitInProgress() {
      return isCommitInProgress;
   }

   private Callable<TransactionResult> createCommit() throws OseeCoreException {
      List<ArtifactTransactionData> changes = manager.getChanges();
      TransactionData data = new TransactionDataImpl(getBranch(), getAuthor(), getComment(), changes);
      return txDataStore.commitTransaction(session, data);
   }

   @Override
   public TransactionRecord commit() throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(isCommitInProgress(), "Commit is already in progress");
      TransactionRecord transaction = null;
      try {
         startCommit();
         Callable<TransactionResult> callable = createCommit();
         TransactionResult result = callable.call();
         commitSuccess(result);
         transaction = result.getTransaction();
      } catch (Exception ex) {
         Exception toThrow = ex;
         try {
            rollback();
         } catch (Exception ex2) {
            toThrow = new OseeCoreException("Exception during rollback and commit", ex);
         } finally {
            OseeExceptions.wrapAndThrow(toThrow);
         }
      } finally {
         endCommit();
      }
      return transaction;
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

   private void addWriteable(ArtifactWriteable writeable) throws OseeCoreException {
      manager.addWrite(writeable);
   }

   @Override
   public ArtifactWriteable asWriteable(ArtifactReadable readable) throws OseeCoreException {
      return manager.getOrAddWrite(readable);
   }

   @Override
   public List<ArtifactWriteable> asWriteable(Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException {
      List<ArtifactWriteable> toReturn = new ArrayList<ArtifactWriteable>();
      for (ArtifactReadable readable : artifacts) {
         toReturn.add(asWriteable(readable));
      }
      return toReturn;
   }

   @Override
   public ArtifactWriteable createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException {
      ArtifactWriteable artifact = factory.create(getBranch(), artifactType, guid, name);
      addWriteable(artifact);
      return artifact;
   }

   @Override
   public ArtifactWriteable duplicateArtifact(ArtifactReadable source, Collection<? extends IAttributeType> types) throws OseeCoreException {
      ArtifactWriteable toReturn = factory.copy(source, types, getBranch());
      addWriteable(toReturn);
      return toReturn;
   }

   @Override
   public ArtifactWriteable introduceArtifact(ArtifactReadable source) throws OseeCoreException {
      ArtifactWriteable toReturn = factory.introduce(source, getBranch());
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

   @SuppressWarnings("unused")
   @Override
   public void unrelate(ArtifactReadable side1, IRelationTypeSide typeSide, ArtifactReadable side2) throws OseeCoreException {
      // do nothing
   }

}
