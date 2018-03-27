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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.loader.data.TransactionDataImpl;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public final class CommitTransactionDatabaseTxCallable extends AbstractDatastoreTxCallable<TransactionResult> {

   private final TransactionData transactionData;
   private final IdentityManager identityManager;
   private final TransactionProcessorProvider provider;
   private final TransactionWriter writer;
   private static final String UPDATE_BRANCH_STATE =
      "UPDATE osee_branch SET branch_state = ? WHERE branch_id = ? and branch_state = ?";

   public CommitTransactionDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, IdentityManager identityManager, TransactionProcessorProvider provider, TransactionWriter writer, TransactionData transactionData) {
      super(logger, session, jdbcClient);
      this.identityManager = identityManager;
      this.provider = provider;
      this.writer = writer;
      this.transactionData = transactionData;
   }

   private TransactionId getNextTransactionId() {
      return identityManager.getNextTransactionId();
   }

   private void process(TxWritePhaseEnum phase) {
      Iterable<TransactionProcessor> processors = provider.getProcessor(phase);
      for (TransactionProcessor processor : processors) {
         processor.process(this, getSession(), transactionData);
      }
   }

   /**
    * Persist changes to database.
    *
    * @return TransactionResult or null if no data was modified
    */
   @Override
   protected TransactionResult handleTxWork(JdbcConnection connection) {
      /////
      // TODO:
      // 1. Make this whole method a critical region on a per branch basis - can only write to a branch on one thread at time
      String comment = transactionData.getComment();
      BranchId branch = transactionData.getBranch();
      UserId author = transactionData.getAuthor();
      OrcsChangeSet changeSet = transactionData.getChangeSet();

      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(author, "transaction author");
      Conditions.checkNotNullOrEmpty(comment, "transaction comment");
      TransactionResult result = null;
      if (!changeSet.isEmpty()) {

         process(TxWritePhaseEnum.BEFORE_TX_WRITE);

         TransactionReadable txRecord = createTransactionRecord(branch, author, comment, getNextTransactionId());
         writer.write(connection, txRecord, changeSet);

         Object[] params = new Object[] {BranchState.MODIFIED, branch, BranchState.CREATED};
         getJdbcClient().runPreparedUpdate(connection, UPDATE_BRANCH_STATE, params);

         result = new TransactionResultImpl(txRecord, changeSet);
      }
      return result;
   }

   @Override
   protected void handleTxException(Exception ex) {
      super.handleTxException(ex);
      writer.rollback();
   }

   @Override
   protected void handleTxFinally() {
      super.handleTxFinally();
      process(TxWritePhaseEnum.AFTER_TX_WRITE);
   }

   private TransactionReadable createTransactionRecord(BranchId branch, UserId author, String comment, TransactionId transaction) {

      TransactionDetailsType txType = TransactionDetailsType.NonBaselined;
      Date transactionTime = GlobalTime.GreenwichMeanTimestamp();

      TransactionDataImpl created = new TransactionDataImpl(transaction.getId());
      created.setAuthor(author);
      created.setBranch(branch);
      created.setComment(comment);
      created.setCommitArt(ArtifactId.SENTINEL);
      created.setDate(transactionTime);
      created.setTxType(txType);

      return created;
   }

   private static final class TransactionResultImpl implements TransactionResult {

      private final TransactionReadable tx;
      private final OrcsChangeSet data;

      public TransactionResultImpl(TransactionReadable tx, OrcsChangeSet data) {
         super();
         this.tx = tx;
         this.data = data;
      }

      @Override
      public TransactionReadable getTransaction() {
         return tx;
      }

      @Override
      public OrcsChangeSet getChangeSet() {
         return data;
      }

   }

   //////////// EVENT STUFF //////////////////////////
   //   private void updateModifiedCachedObject()  {
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
   //   protected static int getNewAttributeId(Artifact artifact, Attribute<?> attribute)  {
   //      return ConnectionHandler.getSequence().getNextAttributeId();
   //   }

}
