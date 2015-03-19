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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.OseeSql;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * Manages a cache of <code>TransactionId</code>.
 * 
 * @author Jeff C. Phillips
 */
public final class TransactionManager {

   private static final String TRANSACTION_ID_SEQ = "SKYNET_TRANSACTION_ID_SEQ";

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
      "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String SELECT_TRANSACTIONS =
      "SELECT * FROM osee_tx_details WHERE branch_id = ? ORDER BY transaction_id DESC";

   private static final String SELECT_COMMIT_TRANSACTIONS =
      "SELECT transaction_id FROM osee_tx_details WHERE commit_art_id = ?";

   private static final String UPDATE_TRANSACTION_COMMENTS =
      "UPDATE osee_tx_details SET osee_comment = ? WHERE transaction_id = ?";

   private static final String SELECT_TRANSACTION_COMMENTS =
      "SELECT transaction_id FROM osee_tx_details WHERE osee_comment LIKE ?";

   private static final String SELECT_BRANCH_TRANSACTION_BY_DATE =
      "SELECT * FROM osee_tx_details WHERE branch_id = ? AND time < ? ORDER BY time DESC";

   private static final TransactionRecordFactory factory = new TransactionRecordFactory();

   private static final HashMap<Integer, List<TransactionRecord>> commitArtifactIdMap =
      new HashMap<Integer, List<TransactionRecord>>();

   private static final TxMonitorImpl<Branch> txMonitor = new TxMonitorImpl<Branch>(new TxMonitorCache<Branch>());

   public static SkynetTransaction createTransaction(IOseeBranch branch, String comment) throws OseeCoreException {
      Branch actualBranch = BranchManager.getBranch(branch);
      SkynetTransaction tx = new SkynetTransaction(txMonitor, actualBranch, GUID.create(), comment);
      txMonitor.createTx(actualBranch, tx);
      return tx;
   }

   public static List<TransactionRecord> getTransaction(String comment) throws OseeCoreException {
      ArrayList<TransactionRecord> transactions = new ArrayList<TransactionRecord>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_TRANSACTION_COMMENTS, comment);
         while (chStmt.next()) {
            transactions.add(getTransactionId(chStmt.getInt("transaction_id"), chStmt));
         }
      } finally {
         chStmt.close();
      }
      return transactions;
   }

   public static void setTransactionComment(TransactionRecord transaction, String comment) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(UPDATE_TRANSACTION_COMMENTS, comment, transaction.getId());
   }

   private static IOseeCachingService getCacheService() throws OseeCoreException {
      return ServiceUtil.getOseeCacheService();
   }

   private static TransactionCache getTransactionCache() throws OseeCoreException {
      return getCacheService().getTransactionCache();
   }

   private static BranchCache getBranchCache() throws OseeCoreException {
      return getCacheService().getBranchCache();
   }

   public static List<TransactionRecord> getTransactionsForBranch(Branch branch) throws OseeCoreException {
      ArrayList<TransactionRecord> transactions = new ArrayList<TransactionRecord>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();

      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, SELECT_TRANSACTIONS, branch.getUuid());

         while (chStmt.next()) {
            transactions.add(getTransactionId(chStmt.getInt("transaction_id"), chStmt));
         }
      } finally {
         chStmt.close();
      }
      return transactions;
   }

   public synchronized static Collection<TransactionRecord> getCommittedArtifactTransactionIds(IArtifact artifact) throws OseeCoreException {
      List<TransactionRecord> transactionIds = commitArtifactIdMap.get(artifact.getArtId());
      // Cache the transactionIds first time through.  Other commits will be added to cache as they
      // happen in this client or as remote commit events come through
      if (transactionIds == null) {
         transactionIds = new ArrayList<TransactionRecord>(5);
         JdbcStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(SELECT_COMMIT_TRANSACTIONS, artifact.getArtId());
            while (chStmt.next()) {
               transactionIds.add(getTransactionId(chStmt.getInt("transaction_id")));
            }

            commitArtifactIdMap.put(artifact.getArtId(), transactionIds);
         } finally {
            chStmt.close();
         }
      }
      return transactionIds;
   }

   /**
    * Allow commitArtifactIdMap cache to be cleared for a given associatedArtifact. This will force a refresh of the
    * cache the next time it's accessed. This is provided for remote event commits. All other updates to cache should be
    * performed through cacheCommittedArtifactTransaction.
    */
   public static void clearCommitArtifactCacheForAssociatedArtifact(IArtifact associatedArtifact) {
      if (associatedArtifact != null) {
         commitArtifactIdMap.remove(associatedArtifact.getArtId());
      }
   }

   public synchronized static void cacheCommittedArtifactTransaction(IArtifact artifact, TransactionRecord transactionId) throws OseeCoreException {
      Collection<TransactionRecord> transactionIds = getCommittedArtifactTransactionIds(artifact);
      if (!transactionIds.contains(transactionId)) {
         transactionIds.add(transactionId);
         getTransactionCache().cache(transactionId);
      }
   }

   /**
    * @return the largest (most recent) transaction on the given branch
    */
   public static TransactionRecord getHeadTransaction(IOseeBranch branch) throws OseeCoreException {
      long branchUuid = branch.getUuid();
      int transactionNumber =
         ConnectionHandler.runPreparedQueryFetchInt(-1, ServiceUtil.getSql(OseeSql.TX_GET_MAX_AS_LARGEST_TX),
            branchUuid);
      if (transactionNumber == -1) {
         throw new TransactionDoesNotExist("No transactions where found in the database for branch: %d", branchUuid);
      }
      return getTransactionId(transactionNumber);
   }

   public static synchronized TransactionRecord createNextTransactionId(JdbcConnection connection, Branch branch, User userToBlame, String comment) throws OseeCoreException {
      TransactionRecord transactionId = internalCreateTransactionRecord(branch, userToBlame, comment);
      internalPersist(connection, transactionId);
      return transactionId;
   }

   private static int getNextTransactionId() {
      return (int) ConnectionHandler.getNextSequence(TRANSACTION_ID_SEQ);
   }

   public static synchronized TransactionRecord internalCreateTransactionRecord(IOseeBranch branch, User userToBlame, String comment) throws OseeCoreException {
      if (comment == null) {
         comment = "";
      }
      Integer transactionNumber = getNextTransactionId();
      int authorArtId = userToBlame.getArtId();
      TransactionDetailsType txType = TransactionDetailsType.NonBaselined;
      Date transactionTime = GlobalTime.GreenwichMeanTimestamp();
      TransactionRecord transactionId =
         factory.createOrUpdate(getTransactionCache(), transactionNumber, branch.getUuid(), comment, transactionTime,
            authorArtId, 0, txType, getBranchCache());
      return transactionId;
   }

   public static synchronized void internalPersist(JdbcConnection connection, TransactionRecord transactionRecord) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(connection, INSERT_INTO_TRANSACTION_DETAIL, transactionRecord.getId(),
         transactionRecord.getComment(), transactionRecord.getTimeStamp(), transactionRecord.getAuthor(),
         transactionRecord.getBranchId(), transactionRecord.getTxType().getId());
   }

   public static TransactionRecord getTransactionAtDate(IOseeBranch branch, Date maxDateExclusive) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(maxDateExclusive, "max date exclusive");
      long branchUuid = branch.getUuid();

      TransactionRecord txRecord = null;

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_BRANCH_TRANSACTION_BY_DATE, branchUuid,
            new Timestamp(maxDateExclusive.getTime()));
         if (chStmt.next()) {
            int transactionId = chStmt.getInt("transaction_id");
            if (chStmt.wasNull()) {
               DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
               throw new TransactionDoesNotExist("Cannot find transaction for [%s] - the transation id was null",
                  dateFormat.format(maxDateExclusive));
            }
            txRecord = getTransactionId(transactionId, chStmt);
         }
      } finally {
         chStmt.close();
      }
      return txRecord;
   }

   public static TransactionRecord getTransactionId(int transactionNumber) throws OseeCoreException {
      return getTransactionId(transactionNumber, null);
   }

   public static TransactionRecord getTransactionId(JdbcStatement chStmt) throws OseeCoreException {
      return getTransactionId(chStmt.getInt("transaction_id"), chStmt);
   }

   public static void deCache(int txId) throws OseeCoreException {
      TransactionCache txCache = getTransactionCache();
      TransactionRecord transactionRecord = txCache.getById(txId);
      if (transactionRecord != null) {
         txCache.decache(transactionRecord);
      }
   }

   private synchronized static TransactionRecord getTransactionId(int txId, JdbcStatement chStmt) throws OseeCoreException {
      TransactionCache txCache = getTransactionCache();
      TransactionRecord transactionRecord = txCache.getById(txId);

      boolean useLocalConnection = chStmt == null;
      if (transactionRecord == null) {
         try {
            if (useLocalConnection) {
               chStmt = ConnectionHandler.getStatement();
               chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.TX_GET_ALL_TRANSACTIONS), txId);
               if (!chStmt.next()) {
                  throw new TransactionDoesNotExist("The transaction id %d does not exist in the databse.", txId);
               }
            }
            TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));

            BranchCache branchCache = getBranchCache();
            transactionRecord =
               factory.createOrUpdate(txCache, txId, chStmt.getLong("branch_id"), chStmt.getString("osee_comment"),
                  chStmt.getTimestamp("time"), chStmt.getInt("author"), chStmt.getInt("commit_art_id"), txType,
                  branchCache);

         } finally {
            if (useLocalConnection) {
               chStmt.close();
            }
         }
      }
      return transactionRecord;
   }

   public static TransactionRecord getPriorTransaction(TransactionRecord transactionId) throws OseeCoreException {
      TransactionCache txCache = getTransactionCache();
      return txCache.getPriorTransaction(transactionId);
   }

}