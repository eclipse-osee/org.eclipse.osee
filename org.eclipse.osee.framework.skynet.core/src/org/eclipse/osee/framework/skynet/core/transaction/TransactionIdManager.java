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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;

/**
 * Manages a cache of <code>TransactionId</code>.
 * 
 * @author Jeff C. Phillips
 */
public class TransactionIdManager {
   private static final String largestTransIdSql =
         "SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "largest_transaction_id") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?";
   private static final String SELECT_MAX_MIN_TX =
         "SELECT max(transaction_id) AS max_id, min(transaction_id) AS min_id FROM osee_define_tx_details WHERE branch_id = ?";
   private static final String SELECT_TX_GAMMAS =
         "SELECT " + TRANSACTIONS_TABLE.columns("transaction_id", "gamma_id") + " FROM " + TRANSACTION_DETAIL_TABLE + "," + TRANSACTIONS_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.join(
               TRANSACTIONS_TABLE, "transaction_id") + " ORDER BY " + TRANSACTIONS_TABLE.columns("transaction_id",
               "gamma_id");
   private static final String SELECT_TX_GAMMAS_RANGE =
         "SELECT " + TRANSACTIONS_TABLE.columns("transaction_id", "gamma_id") + " FROM " + TRANSACTION_DETAIL_TABLE + "," + TRANSACTIONS_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ">? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=? AND " + TRANSACTION_DETAIL_TABLE.join(
               TRANSACTIONS_TABLE, "transaction_id") + " ORDER BY " + TRANSACTIONS_TABLE.columns("transaction_id",
               "gamma_id");

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
         "INSERT INTO osee_define_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";
   private final Map<Integer, TransactionId> nonEditableTransactionIdCache = new HashMap<Integer, TransactionId>();
   private static final TransactionIdManager instance = new TransactionIdManager();

   private static final String SELECT_TRANSACTION = "SELECT * FROM osee_define_tx_details WHERE transaction_id = ?";

   public static TransactionIdManager getInstance() {
      return instance;
   }

   private TransactionIdManager() {
   }

   /**
    * Returns the transaction corresponding to current head of the given branch
    * 
    * @param branch
    * @throws SQLException
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    */
   public TransactionId getEditableTransactionId(Branch branch) throws SQLException, TransactionDoesNotExist, BranchDoesNotExist {
      return getTransactionId(getlatestTransactionForBranch(branch));
   }

   private int getlatestTransactionForBranch(Branch branch) throws SQLException, TransactionDoesNotExist {
      int transactionNumber = -1;
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(largestTransIdSql, branch.getBranchId());
         if (chStmt.next()) {
            transactionNumber = chStmt.getRset().getInt("largest_transaction_id");
         } else {
            throw new TransactionDoesNotExist("No transactions where found in the database for branch: " + branch);
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return transactionNumber;
   }

   public static synchronized TransactionId createNextTransactionId(Branch branch, User userToBlame, String comment) throws SQLException {
      Integer transactionNumber = SequenceManager.getNextTransactionId();
      if (comment == null) {
         comment = "";
      }
      int authorArtId = -1;
      if (userToBlame == null || !userToBlame.isInDb()) {
         if (SkynetAuthentication.isBasicUsersCreated()) {
            authorArtId = SkynetAuthentication.getNoOneArtifactId();
         }
      } else {
         authorArtId = userToBlame.getArtId();
      }

      Date transactionTime = GlobalTime.GreenwichMeanTimestamp();
      ConnectionHandler.runPreparedUpdate(INSERT_INTO_TRANSACTION_DETAIL, transactionNumber, comment, transactionTime,
            authorArtId, branch.getBranchId(), TransactionDetailsType.NonBaselined.getId());

      TransactionId transactionId =
            new TransactionId(transactionNumber, branch, comment, transactionTime, authorArtId, -1,
                  TransactionDetailsType.NonBaselined);

      instance.nonEditableTransactionIdCache.put(transactionNumber, transactionId);
      return transactionId;
   }

   public static Pair<TransactionId, TransactionId> getStartEndPoint(Branch branch) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_MAX_MIN_TX, branch.getBranchId());

         ResultSet rset = chStmt.getRset();
         // the max, min query will return exactly 1 row by definition (even if there is no max or min)
         rset.next();

         int minId = rset.getInt("min_id");
         int maxId = rset.getInt("max_id");

         if (rset.wasNull()) {
            throw new TransactionDoesNotExist("Branch " + branch + " has no transactions");
         }

         return new Pair<TransactionId, TransactionId>(getTransactionId(minId), getTransactionId(maxId));
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * @param time
    * @param branch
    * @return The prior transactionId, or null if there is no prior.
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    * @throws SQLException
    */
   public TransactionId getPriorTransaction(Timestamp time, Branch branch) throws BranchDoesNotExist, TransactionDoesNotExist, SQLException {
      TransactionId priorTransactionId = null;
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(
                     "SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "prior_id") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("time") + " < ?",
                     branch.getBranchId(), time);

         ResultSet rset = chStmt.getRset();
         if (rset.next()) {
            int priorId = rset.getInt("prior_id");
            if (!rset.wasNull()) priorTransactionId = getTransactionId(priorId);
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return priorTransactionId;
   }

   /**
    * @param transactionId
    * @return The prior transactionId, or null if there is no prior.
    * @throws SQLException
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    */
   public static TransactionId getPriorTransaction(TransactionId transactionId) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      TransactionId priorTransactionId = null;
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(
                     "SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "prior_id") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " < ?",
                     transactionId.getBranch().getBranchId(), transactionId.getTransactionNumber());

         ResultSet rset = chStmt.getRset();
         if (rset.next()) {
            int priorId = rset.getInt("prior_id");
            if (rset.wasNull()) {
               throw new TransactionDoesNotExist("the prior transation id was null");
            }
            priorTransactionId = getTransactionId(priorId);
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return priorTransactionId;
   }

   public static TransactionId getParentBaseTransaction(Branch branch) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      if (branch == null) throw new IllegalArgumentException("branch can not be null.");
      if (branch.getParentBranch() == null) throw new IllegalArgumentException("branch must have a parent branch.");

      TransactionId baseParentTransactionId = null;
      TransactionId baseTransaction = getStartEndPoint(branch).getKey();

      String baseComment = baseTransaction.getComment();
      int baseParentTransactionNumber = getParentBaseTransactionNumber(baseComment);
      if (baseParentTransactionNumber != -1) {
         baseParentTransactionId = getTransactionId(baseParentTransactionNumber);
      }

      return baseParentTransactionId;
   }

   public static int getParentBaseTransactionNumber(String comment) {
      if (comment != null) {
         String[] vals = comment.split("(\\(|\\))", 3);
         if (vals.length == 3) {
            try {
               return Integer.parseInt(vals[1]);
            } catch (NumberFormatException ex) {
               // not able to get the parent transaction number
            }
         }
      }
      return -1;
   }

   @Deprecated
   public static Checksum getTransactionRangeChecksum(TransactionId startTransactionId, TransactionId endTransactionId) throws SQLException {
      if (startTransactionId == null) throw new IllegalArgumentException("startTransactionId can not be null");
      if (endTransactionId == null) throw new IllegalArgumentException("endTransactionId can not be null");
      if (startTransactionId.getBranch() != endTransactionId.getBranch()) throw new IllegalArgumentException(
            "transactions must be on the same branch");
      if (startTransactionId.getTransactionNumber() > endTransactionId.getTransactionNumber()) throw new IllegalArgumentException(
            "startTransactionId can not be after endTransactionId");

      Checksum checksum = new Adler32();

      ConnectionHandlerStatement chStmt = null;

      try {
         if (startTransactionId.getTransactionNumber() == endTransactionId.getTransactionNumber()) {
            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_TX_GAMMAS, startTransactionId.getBranch().getBranchId(),
                        startTransactionId.getTransactionNumber());
         } else {
            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_TX_GAMMAS_RANGE,
                        startTransactionId.getBranch().getBranchId(), startTransactionId.getTransactionNumber(),
                        endTransactionId.getTransactionNumber());
         }

         ResultSet rset = chStmt.getRset();
         while (rset.next()) {
            checksum.update(toBytes(rset.getLong("transaction_id")), 0, 8);
            checksum.update(toBytes(rset.getLong("gamma_id")), 0, 8);
         }
      } finally {
         DbUtil.close(chStmt);
      }

      return checksum;
   }

   @Deprecated
   private static byte[] toBytes(long val) {
      byte[] bytes = new byte[8];
      bytes[7] = (byte) (val);
      val >>>= 8;
      bytes[6] = (byte) (val);
      val >>>= 8;
      bytes[5] = (byte) (val);
      val >>>= 8;
      bytes[4] = (byte) (val);
      val >>>= 8;
      bytes[3] = (byte) (val);
      val >>>= 8;
      bytes[2] = (byte) (val);
      val >>>= 8;
      bytes[1] = (byte) (val);
      val >>>= 8;
      bytes[0] = (byte) (val);

      return bytes;
   }

   public static TransactionId getTransactionId(int transactionNumber) throws SQLException, TransactionDoesNotExist, BranchDoesNotExist {
      return getTransactionId(transactionNumber, null);
   }

   public static TransactionId getTransactionId(ResultSet rSet) throws SQLException, TransactionDoesNotExist, BranchDoesNotExist {
      return getTransactionId(rSet.getInt("transaction_id"), rSet);
   }

   private static TransactionId getTransactionId(int transactionNumber, ResultSet rSet) throws SQLException, TransactionDoesNotExist, BranchDoesNotExist {
      TransactionId transactionId = instance.nonEditableTransactionIdCache.get(transactionNumber);
      if (transactionId == null) {
         ConnectionHandlerStatement chStmt = null;
         try {
            if (rSet == null) {
               chStmt = ConnectionHandler.runPreparedQuery(SELECT_TRANSACTION, transactionNumber);
               rSet = chStmt.getRset();
               if (!rSet.next()) {
                  throw new TransactionDoesNotExist(
                        "The transaction id " + transactionNumber + " does not exist in the databse.");
               }
            }
            Branch branch = BranchPersistenceManager.getBranch(rSet.getInt("branch_id"));
            TransactionDetailsType txType = TransactionDetailsType.toEnum(rSet.getInt("tx_type"));

            transactionId =
                  new TransactionId(transactionNumber, branch, rSet.getString("osee_comment"),
                        rSet.getTimestamp("time"), rSet.getInt("author"), rSet.getInt("commit_art_id"), txType);
            instance.nonEditableTransactionIdCache.put(transactionNumber, transactionId);
         } finally {
            DbUtil.close(chStmt);
         }
      }
      return transactionId;
   }
}