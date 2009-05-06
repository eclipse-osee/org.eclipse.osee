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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * Manages a cache of <code>TransactionId</code>.
 * 
 * @author Jeff C. Phillips
 */
public final class TransactionIdManager {

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
         "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";
   private static final String SELECT_TRANSACTIONS =
         "SELECT " + TRANSACTION_DETAIL_TABLE.columns("transaction_id", "commit_art_id", TXD_COMMENT, "time", "author",
               "branch_id", "tx_type") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " ORDER BY transaction_id DESC";

   private static final String SELECT_COMMIT_TRANSACTIONS =
         "SELECT transaction_id from osee_tx_details where commit_art_id = ?";

   private final Map<Integer, TransactionId> transactionIdCache = new HashMap<Integer, TransactionId>();
   private static final TransactionIdManager instance = new TransactionIdManager();
   private static final HashMap<Artifact, List<TransactionId>> commitArtifactMap =
         new HashMap<Artifact, List<TransactionId>>();

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

   public synchronized static Collection<TransactionId> getCommittedArtifactTransactionIds(Artifact artifact) throws OseeCoreException {
      List<TransactionId> transactionIds = commitArtifactMap.get(artifact);
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

   public synchronized static void cacheCommittedArtifactTransaction(Artifact artifact, TransactionId transactionId) throws OseeCoreException {
      Collection<TransactionId> transactionIds = getCommittedArtifactTransactionIds(artifact);
      if (!transactionIds.contains(transactionId)) {
         transactionIds.add(transactionId);
      }
   }

   /**
    * @param branch
    * @return the largest (most recent) transaction on the given branch
    * @throws OseeCoreException
    */
   public static TransactionId getlatestTransactionForBranch(Branch branch) throws OseeCoreException {
      int transactionNumber =
            ConnectionHandler.runPreparedQueryFetchInt(-1,
                  ClientSessionManager.getSQL(OseeSql.Transaction.SELECT_MAX_AS_LARGEST_TX), branch.getBranchId());
      if (transactionNumber == -1) {
         throw new TransactionDoesNotExist("No transactions where found in the database for branch: " + branch);
      }
      return getTransactionId(transactionNumber);
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
         chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Transaction.SELECT_MAX_AND_MIN_TX),
               branch.getBranchId());

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
         chStmt.runPreparedQuery(
               "SELECT max(transaction_id) as prior_id FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " < ?",
               transactionId.getBranch().getBranchId(), transactionId.getTransactionNumber());

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
               chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Transaction.SELECT_ALL_TRANSACTIONS),
                     transactionNumber);
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