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
import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * Manages a cache of <code>TransactionId</code>.
 * 
 * @author Jeff C. Phillips
 */
public final class TransactionIdManager {

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
         "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String SELECT_TRANSACTIONS =
         "SELECT * from osee_tx_details where branch_id = ? order by transaction_id DESC";

   private static final String GET_PRIOR_TRANSACTION =
         "SELECT max(transaction_id) as prior_id FROM osee_tx_details WHERE branch_id = ? AND transaction_id < ?";

   private static final String SELECT_COMMIT_TRANSACTIONS =
         "SELECT transaction_id from osee_tx_details where commit_art_id = ?";

   private final Map<Integer, TransactionId> transactionIdCache = new HashMap<Integer, TransactionId>();
   private static final TransactionIdManager instance = new TransactionIdManager();
   private static final HashMap<IArtifact, List<TransactionId>> commitArtifactMap =
         new HashMap<IArtifact, List<TransactionId>>();

   private TransactionIdManager() {
   }

   public static List<TransactionId> getTransactionsForBranch(Branch branch) throws OseeCoreException {
      ArrayList<TransactionId> transactions = new ArrayList<TransactionId>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_TRANSACTIONS, branch.getBranchId());

         while (chStmt.next()) {
            transactions.add(getTransactionId(chStmt.getInt("transaction_id"), chStmt));
         }
      } finally {
         chStmt.close();
      }
      return transactions;
   }

   public synchronized static Collection<TransactionId> getCommittedArtifactTransactionIds(IArtifact artifact) throws OseeCoreException {
      List<TransactionId> transactionIds = commitArtifactMap.get(artifact);
      // Cache the transactionIds first time through.  Other commits will be added to cache as they
      // happen in this client or as remote commit events come through
      if (transactionIds == null) {
         transactionIds = new ArrayList<TransactionId>(5);
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
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

   public synchronized static void cacheCommittedArtifactTransaction(IArtifact artifact, TransactionId transactionId) throws OseeCoreException {
      Collection<TransactionId> transactionIds = getCommittedArtifactTransactionIds(artifact);
      if (!transactionIds.contains(transactionId)) {
         transactionIds.add(transactionId);
      }
   }

   public synchronized static void cacheTransaction(TransactionId transaction) throws OseeCoreException {
      instance.transactionIdCache.put(transaction.getTransactionNumber(), transaction);
   }

   public synchronized static TransactionId getTransactionFromCache(int txNumber) throws OseeCoreException {
      return instance.transactionIdCache.get(txNumber);
   }

   /**
    * @param branch
    * @return the largest (most recent) transaction on the given branch
    * @throws OseeCoreException
    */
   public static TransactionId getlatestTransactionForBranch(int branchId) throws OseeCoreException {
      int transactionNumber =
            ConnectionHandler.runPreparedQueryFetchInt(-1,
                  ClientSessionManager.getSql(OseeSql.TX_GET_MAX_AS_LARGEST_TX), branchId);
      if (transactionNumber == -1) {
         throw new TransactionDoesNotExist("No transactions where found in the database for branch: " + branchId);
      }
      return getTransactionId(transactionNumber);
   }

   /**
    * @param branch
    * @return the largest (most recent) transaction on the given branch
    * @throws OseeCoreException
    */
   public static TransactionId getlatestTransactionForBranch(Branch branch) throws OseeCoreException {
      return getlatestTransactionForBranch(branch.getBranchId());
   }

   public static synchronized TransactionId createNextTransactionId(Branch branch, User userToBlame, String comment) throws OseeDataStoreException {
      Integer transactionNumber = SequenceManager.getNextTransactionId();
      if (comment == null) {
         comment = "";
      }
      int authorArtId = userToBlame.getArtId();

      Date transactionTime = GlobalTime.GreenwichMeanTimestamp();
      ConnectionHandler.runPreparedUpdate(INSERT_INTO_TRANSACTION_DETAIL, transactionNumber, comment, transactionTime,
            authorArtId, branch.getBranchId(), TransactionDetailsType.NonBaselined.getId());

      TransactionId transactionId =
            new TransactionId(transactionNumber, branch, comment, transactionTime, authorArtId, -1,
                  TransactionDetailsType.NonBaselined);

      instance.transactionIdCache.put(transactionNumber, transactionId);
      return transactionId;
   }

   public static Pair<TransactionId, TransactionId> getStartEndPoint(Branch branch) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.TX_GET_MAX_AND_MIN_TX), branch.getBranchId());

         // the max, min query will return exactly 1 row by definition (even if there is no max or min)
         chStmt.next();

         int minId = chStmt.getInt("min_id");
         int maxId = chStmt.getInt("max_id");

         if (chStmt.wasNull()) {
            throw new TransactionDoesNotExist("Branch " + branch + " has no transactions");
         }

         return new Pair<TransactionId, TransactionId>(getTransactionId(minId), getTransactionId(maxId));
      } finally {
         chStmt.close();
      }
   }

   /**
    * @param transactionId
    * @return The prior transactionId, or null if there is no prior.
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    * @throws OseeDataStoreException
    */
   public static TransactionId getPriorTransaction(TransactionId transactionId) throws OseeCoreException {
      TransactionId priorTransactionId = null;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(GET_PRIOR_TRANSACTION, transactionId.getBranch().getBranchId(),
               transactionId.getTransactionNumber());

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

   public static TransactionId getTransactionId(int transactionNumber) throws OseeCoreException {
      return getTransactionId(transactionNumber, null);
   }

   public static TransactionId getTransactionId(ConnectionHandlerStatement chStmt) throws OseeCoreException {
      return getTransactionId(chStmt.getInt("transaction_id"), chStmt);
   }

   private synchronized static TransactionId getTransactionId(int transactionNumber, ConnectionHandlerStatement chStmt) throws OseeCoreException {
      TransactionId transactionId = instance.transactionIdCache.get(transactionNumber);
      boolean useLocalConnection = chStmt == null;
      if (transactionId == null) {
         try {
            if (useLocalConnection) {
               chStmt = new ConnectionHandlerStatement();
               chStmt.runPreparedQuery(ClientSessionManager.getSql(OseeSql.TX_GET_ALL_TRANSACTIONS), transactionNumber);
               if (!chStmt.next()) {
                  throw new TransactionDoesNotExist(
                        "The transaction id " + transactionNumber + " does not exist in the databse.");
               }
            }
            Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id"));
            TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));

            transactionId =
                  new TransactionId(transactionNumber, branch, chStmt.getString("osee_comment"),
                        chStmt.getTimestamp("time"), chStmt.getInt("author"), chStmt.getInt("commit_art_id"), txType);
            instance.transactionIdCache.put(transactionNumber, transactionId);
         } finally {
            if (useLocalConnection) {
               chStmt.close();
            }
         }
      }
      return transactionId;
   }
}