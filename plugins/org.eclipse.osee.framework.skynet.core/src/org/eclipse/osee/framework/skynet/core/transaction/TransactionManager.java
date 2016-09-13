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
package org.eclipse.osee.framework.skynet.core.transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Manages a cache of <code>TransactionId</code>.
 *
 * @author Jeff C. Phillips
 */
public final class TransactionManager {

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
      "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String SELECT_TRANSACTIONS =
      "SELECT * FROM osee_tx_details WHERE branch_id = ? ORDER BY transaction_id DESC";

   private static final String SELECT_TRANSACTIONS_BY_IDS =
      "SELECT * FROM osee_tx_details WHERE transaction_id in (%s)";

   private static final String SELECT_COMMIT_TRANSACTIONS = "SELECT * FROM osee_tx_details WHERE commit_art_id = ?";

   private static final String UPDATE_TRANSACTION_COMMENTS =
      "UPDATE osee_tx_details SET osee_comment = ? WHERE transaction_id = ?";

   private static final String SELECT_TRANSACTION_COMMENTS = "SELECT * FROM osee_tx_details WHERE osee_comment LIKE ?";

   private static final String SELECT_BRANCH_TRANSACTION_BY_DATE =
      "SELECT * FROM osee_tx_details WHERE branch_id = ? AND time < ? ORDER BY time DESC";

   private static final String SELECT_HEAD_TRANSACTION =
      "select * from osee_tx_details where transaction_id = (select max(transaction_id) from osee_tx_details where branch_id = ?) and branch_id = ?";

   private static final String SELECT_PRIOR_TRANSACTION =
      "select * from osee_tx_details where transaction_id = (select max(transaction_id) from osee_tx_details where branch_id = ? and transaction_id < ?) and branch_id = ?";

   private static final String TX_GET_TRANSACTION_BY_ID = "SELECT * FROM osee_tx_details WHERE transaction_id = ?";

   private static final TxMonitorImpl<BranchId> txMonitor = new TxMonitorImpl<>(new TxMonitorCache<>());
   private static final HashCollection<ArtifactId, TransactionRecord> commitArtifactIdMap =
      new HashCollection<>(true, HashSet.class);

   public static SkynetTransaction createTransaction(BranchId branch, String comment) throws OseeCoreException {
      SkynetTransaction tx = new SkynetTransaction(txMonitor, branch, comment);
      txMonitor.createTx(branch, tx);
      return tx;
   }

   public static List<TransactionRecord> getTransaction(String comment) throws OseeCoreException {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      ArrayList<TransactionRecord> transactions = new ArrayList<>();
      jdbcClient.runQuery(stmt -> transactions.add(loadTransaction(stmt)), SELECT_TRANSACTION_COMMENTS, comment);
      return transactions;
   }

   public static void setTransactionComment(TransactionId transaction, String comment) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(UPDATE_TRANSACTION_COMMENTS, comment, transaction);
   }

   public static List<TransactionRecord> getTransactionsForBranch(BranchId branch) throws OseeCoreException {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      ArrayList<TransactionRecord> transactions = new ArrayList<>();
      jdbcClient.runQuery(stmt -> transactions.add(loadTransaction(branch, stmt)), JdbcConstants.JDBC__MAX_FETCH_SIZE,
         SELECT_TRANSACTIONS, branch);
      return transactions;
   }

   public static Long getCommitArtId(TransactionId tx) {
      return (long) getTransaction(tx).getCommit();
   }

   public synchronized static Collection<TransactionRecord> getCommittedArtifactTransactionIds(ArtifactId artifact) throws OseeCoreException {
      if (!commitArtifactIdMap.containsKey(artifact)) {
         ConnectionHandler.getJdbcClient().runQuery(stmt -> commitArtifactIdMap.put(artifact, loadTransaction(stmt)),
            SELECT_COMMIT_TRANSACTIONS, artifact);
      }
      Collection<TransactionRecord> transactions = commitArtifactIdMap.getValues(artifact);
      return transactions == null ? Collections.emptyList() : transactions;
   }

   /**
    * Allow commitArtifactIdMap cache to be cleared for a given associatedArtifact. This will force a refresh of the
    * cache the next time it's accessed. This is provided for remote event commits. All other updates to cache should be
    * performed through cacheCommittedArtifactTransaction.
    */
   public static void clearCommitArtifactCacheForAssociatedArtifact(IArtifact associatedArtifact) {
      commitArtifactIdMap.removeValues(associatedArtifact);
   }

   public synchronized static void cacheCommittedArtifactTransaction(IArtifact artifact, TransactionToken transactionId) throws OseeCoreException {
      commitArtifactIdMap.put(artifact, getTransactionRecord(transactionId.getId()));
   }

   /**
    * @return the largest (most recent) transaction on the given branch
    */
   public static TransactionToken getHeadTransaction(BranchId branch) throws OseeCoreException {
      return getTransaction(branch, SELECT_HEAD_TRANSACTION, branch, branch);
   }

   public static TransactionToken getPriorTransaction(TransactionToken tx) throws OseeCoreException {
      BranchId branch = tx.getBranch();
      return getTransaction(branch, SELECT_PRIOR_TRANSACTION, branch, tx.getId(), branch);
   }

   private static TransactionRecord getTransaction(BranchId branch, String sql, Object... data) throws OseeCoreException {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      return jdbcClient.fetchOrException(
         () -> new TransactionDoesNotExist("No transactions where found in the database for branch: %d",
            branch.getId()),
         stmt -> loadTransaction(branch, stmt), sql, data);
   }

   private static TransactionRecord loadTransaction(JdbcStatement stmt) {
      return loadTransaction(TokenFactory.createBranch(stmt.getLong("branch_id")), stmt);
   }

   private static TransactionRecord loadTransaction(BranchId branch, JdbcStatement stmt) {
      Long transactionNumber = stmt.getLong("transaction_id");
      String comment = stmt.getString("osee_comment");
      Date timestamp = stmt.getTimestamp("time");
      Integer authorArtId = stmt.getInt("author");
      Integer commitArtId = stmt.getInt("commit_art_id");
      TransactionDetailsType txType = TransactionDetailsType.toEnum(stmt.getInt("tx_type"));
      return new TransactionRecord(transactionNumber, branch, comment, timestamp, authorArtId, commitArtId, txType);
   }

   public static synchronized void internalPersist(JdbcConnection connection, TransactionRecord transactionRecord) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(connection, INSERT_INTO_TRANSACTION_DETAIL, transactionRecord.getId(),
         transactionRecord.getComment(), transactionRecord.getTimeStamp(), transactionRecord.getAuthor(),
         transactionRecord.getBranchId(), transactionRecord.getTxType().getId());
   }

   public static TransactionToken getTransactionAtDate(BranchId branch, Date maxDateExclusive) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(maxDateExclusive, "max date exclusive");

      TransactionRecord txRecord = null;

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_BRANCH_TRANSACTION_BY_DATE, branch, new Timestamp(maxDateExclusive.getTime()));
         if (chStmt.next()) {
            txRecord = loadTransaction(chStmt);
         }
      } finally {
         chStmt.close();
      }
      return txRecord;
   }

   public static TransactionRecord getTransaction(TransactionId tx) {
      if (tx instanceof TransactionRecord) {
         return (TransactionRecord) tx;
      }
      return getTransactionRecord(tx.getId());
   }

   public static TransactionToken getTransaction(long txId) {
      return getTransactionRecord(txId);
   }

   public static String getComment(long txId) {
      return getTransactionRecord(txId).getComment();
   }

   public static String getComment(TransactionId tx) {
      return getComment(tx.getId());
   }

   private static TransactionRecord getTransactionRecord(long txId) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      return jdbcClient.fetchOrException(
         () -> new TransactionDoesNotExist("A transaction with id %d was not found.", txId),
         stmt -> loadTransaction(stmt), TX_GET_TRANSACTION_BY_ID, txId);
   }

   public static Collection<TransactionRecord> getTransactions(Set<Long> ids) {
      List<TransactionRecord> transactions = new LinkedList<TransactionRecord>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         String query = String.format(SELECT_TRANSACTIONS_BY_IDS,
            org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", ids));
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            transactions.add(loadTransaction(chStmt));
         }
      } finally {
         chStmt.close();
      }
      return transactions;
   }
}