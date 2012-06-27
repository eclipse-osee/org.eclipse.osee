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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.database.schema.DatabaseTxCallable;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public final class CommitTransactionDatabaseTxCallable extends DatabaseTxCallable<TransactionResult> {

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
      "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String UPDATE_TXS_NOT_CURRENT =
      "UPDATE osee_txs SET tx_current = " + TxChange.NOT_CURRENT.getValue() + " WHERE branch_id = ? AND transaction_id = ? AND gamma_id = ?";

   private final SqlProvider sqlProvider;
   private final IdFactory idFactory;
   private final IdentityService identityService;
   private final BranchCache branchCache;
   private final TransactionRecordFactory factory;
   private final TransactionCache transactionCache;
   private final TransactionData transactionData;

   private List<DaoToSql> binaryStores;

   public CommitTransactionDatabaseTxCallable(Log logger, IOseeDatabaseService dbService, IdentityService identityService, SqlProvider sqlProvider, IdFactory idFactory, BranchCache branchCache, TransactionCache transactionCache, TransactionRecordFactory factory, TransactionData transactionData) {
      super(logger, dbService, String.format("Committing Transaction: [%s] for branch [%s]",
         transactionData.getComment(), transactionData.getBranch()));
      this.sqlProvider = sqlProvider;
      this.idFactory = idFactory;
      this.identityService = identityService;
      this.branchCache = branchCache;
      this.factory = factory;
      this.transactionCache = transactionCache;
      this.transactionData = transactionData;
   }

   private void checkPreconditions(Collection<ArtifactTransactionData> txData) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(txData, "artifacts modified");
   }

   @Override
   protected TransactionResult handleTxWork(OseeConnection connection) throws OseeCoreException {
      ///// 
      // TODO:
      // 1. Make this whole method a critical region on a per branch basis - can only write to a branch on one thread at time
      // 2. This is where we will eventually check that the gammaIds have not changed from under us for: attributes, artifacts and relations
      // 3. Don't burn transaction ID until now
      // 4.
      ////
      List<ArtifactTransactionData> txData = transactionData.getTxData();
      checkPreconditions(txData);

      Branch branch = branchCache.get(transactionData.getBranch());
      TransactionRecord txRecord =
         createTransactionRecord(branch, transactionData.getAuthor(), transactionData.getComment());
      persistTx(connection, txRecord);

      if (!txData.isEmpty()) {
         executeTransactionDataItems(txData, connection, branch);
      }

      if (branch.getBranchState() == BranchState.CREATED) {
         branch.setBranchState(BranchState.MODIFIED);
         branchCache.storeItems(branch);
      }
      return new TransactionResultImpl(txRecord, txData);
   }

   @Override
   protected void handleTxException(Exception ex) {
      super.handleTxException(ex);
      for (DaoToSql tx : binaryStores) {
         try {
            tx.rollBack();
         } catch (OseeCoreException ex1) {
            getLogger().error(ex1, "Error during binary rollback [%s]", tx);
         }
      }
   }

   private void executeTransactionDataItems(Collection<ArtifactTransactionData> txData, OseeConnection connection, Branch branch) throws OseeCoreException {
      TxSqlBuilder builder = new TxSqlBuilder(idFactory, identityService, txData);
      builder.build();

      binaryStores = builder.getBinaryTxs();
      for (DaoToSql tx : binaryStores) {
         tx.persist();
      }

      List<Object[]> txNotCurrentData = new ArrayList<Object[]>();
      for (OseeSql sql : builder.getTxSql()) {
         for (Object[] params : builder.getTxParameters(sql)) {
            fetchTxNotCurrent(connection, branch, txNotCurrentData, sql, params);
         }
      }

      // Insert into data tables - i.e. attribute, relation and artifact version tables
      for (String sql : builder.getObjectSql()) {
         getDatabaseService().runBatchUpdate(connection, sql, builder.getObjectParameters(sql));
      }

      // Set stale tx currents in txs table
      getDatabaseService().runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, txNotCurrentData);
   }

   @SuppressWarnings("unchecked")
   private void persistTx(OseeConnection connection, TransactionRecord transactionRecord) throws OseeCoreException {
      getDatabaseService().runPreparedUpdate(connection, INSERT_INTO_TRANSACTION_DETAIL, transactionRecord.getId(),
         transactionRecord.getComment(), transactionRecord.getTimeStamp(), transactionRecord.getAuthor(),
         transactionRecord.getBranchId(), transactionRecord.getTxType().getId());
   }

   private TransactionRecord createTransactionRecord(Branch branch, ArtifactReadable author, String comment) throws OseeCoreException {
      Integer transactionNumber = getDatabaseService().getSequence().getNextTransactionId();
      if (comment == null) {
         comment = "";
      }
      int authorArtId = author.getLocalId();
      TransactionDetailsType txType = TransactionDetailsType.NonBaselined;
      Date transactionTime = GlobalTime.GreenwichMeanTimestamp();

      int branchId = branch.getId();
      return factory.createOrUpdate(transactionCache, transactionNumber, branchId, comment, transactionTime,
         authorArtId, -1, txType, branchCache);
   }

   private void fetchTxNotCurrent(OseeConnection connection, Branch branch, List<Object[]> results, OseeSql sql, Object[] params) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         String query = sqlProvider.getSql(sql);
         chStmt.runPreparedQuery(query, params);
         while (chStmt.next()) {
            results.add(new Object[] {branch.getId(), chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }
   }

   private final class TransactionResultImpl implements TransactionResult {

      private final TransactionRecord tx;
      private final List<ArtifactTransactionData> data;

      public TransactionResultImpl(TransactionRecord tx, List<ArtifactTransactionData> data) {
         super();
         this.tx = tx;
         this.data = data;
      }

      @Override
      public TransactionRecord getTransaction() {
         return tx;
      }

      @Override
      public List<ArtifactTransactionData> getData() {
         return data;
      }

   }
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
