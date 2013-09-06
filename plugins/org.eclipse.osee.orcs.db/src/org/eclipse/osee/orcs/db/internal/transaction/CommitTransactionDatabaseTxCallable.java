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
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.Date;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public final class CommitTransactionDatabaseTxCallable extends AbstractDatastoreTxCallable<TransactionResult> {

   private final BranchCache branchCache;
   private final TransactionRecordFactory factory;
   private final TransactionCache transactionCache;
   private final TransactionData transactionData;

   private final TransactionProcessorProvider provider;
   private final TransactionWriter writer;

   public CommitTransactionDatabaseTxCallable(Log logger, OrcsSession session, IOseeDatabaseService dbService, BranchCache branchCache, TransactionCache transactionCache, TransactionRecordFactory factory, TransactionProcessorProvider provider, TransactionWriter writer, TransactionData transactionData) {
      super(logger, session, dbService, String.format("Committing Transaction: [%s] for branch [%s]",
         transactionData.getComment(), transactionData.getBranch()));
      this.branchCache = branchCache;
      this.factory = factory;
      this.transactionCache = transactionCache;
      this.provider = provider;

      this.writer = writer;
      this.transactionData = transactionData;
   }

   private int getNextTransactionId() throws OseeDataStoreException, OseeCoreException {
      return getDatabaseService().getSequence().getNextTransactionId();
   }

   private void process(TxWritePhaseEnum phase) throws OseeCoreException {
      Iterable<TransactionProcessor> processors = provider.getProcessor(phase);
      for (TransactionProcessor processor : processors) {
         processor.process(this, getSession(), transactionData);
      }
   }

   @Override
   protected TransactionResult handleTxWork(OseeConnection connection) throws OseeCoreException {
      ///// 
      // TODO:
      // 1. Make this whole method a critical region on a per branch basis - can only write to a branch on one thread at time
      String comment = transactionData.getComment();
      Branch branch = branchCache.get(transactionData.getBranch());
      ArtifactReadable author = transactionData.getAuthor();
      OrcsChangeSet changeSet = transactionData.getChangeSet();

      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(author, "transaction author");
      Conditions.checkNotNullOrEmpty(comment, "transaction comment");
      Conditions.checkExpressionFailOnTrue(changeSet.isEmpty(), "No data was modified");

      process(TxWritePhaseEnum.BEFORE_TX_WRITE);

      TransactionRecord txRecord = createTransactionRecord(branch, author, comment, getNextTransactionId());
      writer.write(connection, txRecord, changeSet);

      if (branch.getBranchState() == BranchState.CREATED) {
         branch.setBranchState(BranchState.MODIFIED);
         branchCache.storeItems(branch);
      }
      transactionCache.cache(txRecord);
      return new TransactionResultImpl(txRecord, changeSet);
   }

   @Override
   protected void handleTxException(Exception ex) {
      super.handleTxException(ex);
      writer.rollback();
   }

   @Override
   protected void handleTxFinally() throws OseeCoreException {
      super.handleTxFinally();
      process(TxWritePhaseEnum.AFTER_TX_WRITE);
   }

   private TransactionRecord createTransactionRecord(Branch branch, ArtifactReadable author, String comment, int transactionNumber) throws OseeCoreException {
      int authorArtId = author.getLocalId();
      TransactionDetailsType txType = TransactionDetailsType.NonBaselined;
      Date transactionTime = GlobalTime.GreenwichMeanTimestamp();

      int branchId = branchCache.getLocalId(branch);
      return factory.create(transactionNumber, branchId, comment, transactionTime, authorArtId,
         RelationalConstants.ART_ID_SENTINEL, txType, branchCache);
   }

   private static final class TransactionResultImpl implements TransactionResult {

      private final TransactionRecord tx;
      private final OrcsChangeSet data;

      public TransactionResultImpl(TransactionRecord tx, OrcsChangeSet data) {
         super();
         this.tx = tx;
         this.data = data;
      }

      @Override
      public TransactionRecord getTransaction() {
         return tx;
      }

      @Override
      public OrcsChangeSet getChangeSet() {
         return data;
      }

   }

   //////////// EVENT STUFF //////////////////////////
   //   private void updateModifiedCachedObject() throws OseeCoreException {
   //      ArtifactEvent artifactEvent = new ArtifactEvent(transactionRecord.getBranch());
   //      artifactEvent.setTransactionId(getTransactionNumber());
   //
   //      // Update all transaction items before collecting events
   //      for (BaseTransactionData transactionData : txDatas) {
   //         transactionData.internalUpdate(transactionRecord);
   //      }
   //
   //      // Collect events before clearing any dirty flags
   //      for (BaseTransactionData transactionData : txDatas) {
   //         transactionData.internalAddToEvents(artifactEvent);
   //      }
   //
   //      // Collect attribute events
   //      for (Artifact artifact : artifactReferences) {
   //         if (artifact.hasDirtyAttributes()) {
   //            EventModifiedBasicGuidArtifact guidArt =
   //               new EventModifiedBasicGuidArtifact(artifact.getBranch().getGuid(), artifact.getArtifactType().getGuid(),
   //                  artifact.getGuid(), artifact.getDirtyFrameworkAttributeChanges());
   //            artifactEvent.getArtifacts().add(guidArt);
   //
   //            // Collection relation reorder records for events
   //            if (!artifact.getRelationOrderRecords().isEmpty()) {
   //               artifactEvent.getRelationOrderRecords().addAll(artifact.getRelationOrderRecords());
   //            }
   //         }
   //      }
   //
   //      // Clear all dirty flags
   //      for (BaseTransactionData transactionData : txDatas) {
   //         transactionData.internalClearDirtyState();
   //      }
   //
   //      // Clear all relation order records
   //      for (Artifact artifact : artifactReferences) {
   //         artifact.getRelationOrderRecords().clear();
   //      }
   //
   //      if (!artifactEvent.getArtifacts().isEmpty() || !artifactEvent.getRelations().isEmpty()) {
   //         OseeEventManager.kickPersistEvent(this, artifactEvent);
   //      }
   //   }
   //
   //   protected static int getNewAttributeId(Artifact artifact, Attribute<?> attribute) throws OseeCoreException {
   //      return ConnectionHandler.getSequence().getNextAttributeId();
   //   }

}
