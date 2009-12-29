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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * Manages a cache of <code>TransactionId</code>.
 * 
 * @author Jeff C. Phillips
 */
public final class TransactionManager {

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
         "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String SELECT_TRANSACTIONS =
         "SELECT * from osee_tx_details where branch_id = ? order by transaction_id DESC";

   private static final String GET_PRIOR_TRANSACTION =
         "SELECT max(transaction_id) as prior_id FROM osee_tx_details WHERE branch_id = ? AND transaction_id < ?";

   private static final String SELECT_COMMIT_TRANSACTIONS =
         "SELECT transaction_id from osee_tx_details where commit_art_id = ?";

   private static final String UPDATE_TRANSACTION_COMMENTS =
         "update osee_tx_details set osee_comment = ? where transaction_id = ?";

   private static final String SELECT_TRANSACTION_COMMENTS =
         "select transaction_id from osee_tx_details where osee_comment like ?";

   private static final HashMap<IArtifact, List<TransactionRecord>> commitArtifactMap =
         new HashMap<IArtifact, List<TransactionRecord>>();

   private TransactionManager() {
   }

   public static List<TransactionRecord> getTransaction(String comment) throws OseeCoreException {
      ArrayList<TransactionRecord> transactions = new ArrayList<TransactionRecord>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();
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

   public static void setTransactionComment(TransactionRecord transaction, String comment) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(UPDATE_TRANSACTION_COMMENTS, comment, transaction.getId());
   }

   private static TransactionCache getTransactionCache() {
      return Activator.getInstance().getOseeCacheService().getTransactionCache();
   }

   public static List<TransactionRecord> getTransactionsForBranch(Branch branch) throws OseeCoreException {
      ArrayList<TransactionRecord> transactions = new ArrayList<TransactionRecord>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();

      try {
         chStmt.runPreparedQuery(SELECT_TRANSACTIONS, branch.getId());

         while (chStmt.next()) {
            transactions.add(getTransactionId(chStmt.getInt("transaction_id"), chStmt));
         }
      } finally {
         chStmt.close();
      }
      return transactions;
   }

   public synchronized static Collection<TransactionRecord> getCommittedArtifactTransactionIds(IArtifact artifact) throws OseeCoreException {
      List<TransactionRecord> transactionIds = commitArtifactMap.get(artifact);
      // Cache the transactionIds first time through.  Other commits will be added to cache as they
      // happen in this client or as remote commit events come through
      if (transactionIds == null) {
         transactionIds = new ArrayList<TransactionRecord>(5);
         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(SELECT_COMMIT_TRANSACTIONS, artifact.getArtId());
            while (chStmt.next()) {
               transactionIds.add(getTransactionId(chStmt.getInt("transaction_id")));
            }

            commitArtifactMap.put(artifact, transactionIds);
         } finally {
            chStmt.close();
         }
      }
      return transactionIds;
   }

   /**
    * Allow commitArtifactMap cache to be cleared for a given associatedArtifact. This will force a refresh of the cache
    * the next time it's accessed. This is provided for remote event commits. All other updates to cache should be
    * performed through cacheCommittedArtifactTransaction.
    */
   public static void clearCommitArtifactCacheForAssociatedArtifact(IArtifact associatedArtifact) throws OseeCoreException {
      if (associatedArtifact != null) {
         commitArtifactMap.remove(associatedArtifact);
      }
   }

   public synchronized static void cacheCommittedArtifactTransaction(IArtifact artifact, TransactionRecord transactionId) throws OseeCoreException {
      Collection<TransactionRecord> transactionIds = getCommittedArtifactTransactionIds(artifact);
      if (!transactionIds.contains(transactionId)) {
         BranchCache branchCache = Activator.getInstance().getOseeCacheService().getBranchCache();
         transactionId.setBranchCache(branchCache);
         transactionIds.add(transactionId);
         getTransactionCache().cache(transactionId);
      }
   }

   /**
    * @param branch
    * @return the largest (most recent) transaction on the given branch
    * @throws OseeCoreException
    */
   public static TransactionRecord getLastTransaction(IOseeBranch branch) throws OseeCoreException {
      int branchId = BranchManager.getBranchId(branch);
      int transactionNumber =
            ConnectionHandler.runPreparedQueryFetchInt(-1,
                  ClientSessionManager.getSql(OseeSql.TX_GET_MAX_AS_LARGEST_TX), branchId);
      if (transactionNumber == -1) {
         throw new TransactionDoesNotExist("No transactions where found in the database for branch: " + branchId);
      }
      return getTransactionId(transactionNumber);
   }

   @SuppressWarnings("unchecked")
   public static synchronized TransactionRecord createNextTransactionId(Branch branch, User userToBlame, String comment) throws OseeCoreException {
      Integer transactionNumber = SequenceManager.getNextTransactionId();
      if (comment == null) {
         comment = "";
      }
      int authorArtId = userToBlame.getArtId();
      TransactionDetailsType txType = TransactionDetailsType.NonBaselined;
      Date transactionTime = GlobalTime.GreenwichMeanTimestamp();
      ConnectionHandler.runPreparedUpdate(INSERT_INTO_TRANSACTION_DETAIL, transactionNumber, comment, transactionTime,
            authorArtId, branch.getId(), txType.getId());

      TransactionRecordFactory factory = Activator.getInstance().getOseeFactoryService().getTransactionFactory();
      TransactionRecord transactionId =
            factory.createOrUpdate(getTransactionCache(), transactionNumber, branch.getId(), comment, transactionTime,
                  authorArtId, -1, txType);
      transactionId.setBranchCache(Activator.getInstance().getOseeCacheService().getBranchCache());
      return transactionId;
   }

   public static Pair<TransactionRecord, TransactionRecord> getStartEndPoint(Branch branch) throws OseeCoreException {
      TransactionRecord startRecord = branch.getBaseTransaction();
      TransactionRecord endRecord = getLastTransaction(branch);
      return new Pair<TransactionRecord, TransactionRecord>(startRecord, endRecord);
   }

   /**
    * @param transactionId
    * @return The prior transactionId, or null if there is no prior.
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    * @throws OseeDataStoreException
    */
   public static TransactionRecord getPriorTransaction(TransactionRecord transactionId) throws OseeCoreException {
      TransactionRecord priorTransactionId = null;
      IOseeStatement chStmt = ConnectionHandler.getStatement();

      try {
         chStmt.runPreparedQuery(GET_PRIOR_TRANSACTION, transactionId.getBranchId(), transactionId.getId());

         if (chStmt.next()) {
            int priorId = chStmt.getInt("prior_id");
            if (chStmt.wasNull()) {
               throw new TransactionDoesNotExist("the prior transation id was null");
            }
            priorTransactionId = getTransactionId(priorId);
         }
      } finally {
         chStmt.close();
      }
      return priorTransactionId;
   }

   public static TransactionRecord getTransactionId(int transactionNumber) throws OseeCoreException {
      return getTransactionId(transactionNumber, null);
   }

   public static TransactionRecord getTransactionId(IOseeStatement chStmt) throws OseeCoreException {
      return getTransactionId(chStmt.getInt("transaction_id"), chStmt);
   }

   private synchronized static TransactionRecord getTransactionId(int txId, IOseeStatement chStmt) throws OseeCoreException {
      TransactionCache txCache = getTransactionCache();
      TransactionRecord transactionId = txCache.getById(txId);

      TransactionRecordFactory factory = Activator.getInstance().getOseeFactoryService().getTransactionFactory();
      BranchCache branchCache = Activator.getInstance().getOseeCacheService().getBranchCache();

      boolean useLocalConnection = chStmt == null;
      if (transactionId == null) {
         try {
            if (useLocalConnection) {
               chStmt = ConnectionHandler.getStatement();
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.TX_GET_ALL_TRANSACTIONS), txId);
               if (!chStmt.next()) {
                  throw new TransactionDoesNotExist("The transaction id " + txId + " does not exist in the databse.");
               }
            }
            TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));

            transactionId =
                  factory.createOrUpdate(txCache, txId, chStmt.getInt("branch_id"), chStmt.getString("osee_comment"),
                        chStmt.getTimestamp("time"), chStmt.getInt("author"), chStmt.getInt("commit_art_id"), txType);
            transactionId.setBranchCache(branchCache);

         } finally {
            if (useLocalConnection) {
               chStmt.close();
            }
         }
      }
      return transactionId;
   }
}