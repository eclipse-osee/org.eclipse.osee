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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
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
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * Manages a cache of <code>TransactionId</code>.
 * 
 * @author Jeff C. Phillips
 */
public class TransactionIdManager {

   private static final String INSERT_INTO_TRANSACTION_DETAIL =
         "INSERT INTO osee_tx_details (transaction_id, osee_comment, time, author, branch_id, tx_type) VALUES (?, ?, ?, ?, ?, ?)";

   private final Map<Integer, TransactionId> nonEditableTransactionIdCache = new HashMap<Integer, TransactionId>();
   private static final TransactionIdManager instance = new TransactionIdManager();

   public static TransactionIdManager getInstance() {
      return instance;
   }

   private TransactionIdManager() {
   }

   /**
    * Returns the transaction corresponding to current head of the given branch
    * 
    * @param branch
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    * @throws OseeDataStoreException
    */
   public TransactionId getEditableTransactionId(Branch branch) throws OseeCoreException {
      return getTransactionId(getlatestTransactionForBranch(branch));
   }

   private int getlatestTransactionForBranch(Branch branch) throws OseeCoreException {
      int transactionNumber =
            ConnectionHandler.runPreparedQueryFetchInt(-1,
                  ClientSessionManager.getSQL(OseeSql.Transaction.SELECT_MAX_AS_LARGEST_TX), branch.getBranchId());
      if (transactionNumber == -1) {
         throw new TransactionDoesNotExist("No transactions where found in the database for branch: " + branch);
      }
      return transactionNumber;
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

      instance.nonEditableTransactionIdCache.put(transactionNumber, transactionId);
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
    * @param time
    * @param branch
    * @return The prior transactionId, or null if there is no prior.
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    * @throws OseeDataStoreException
    */
   public TransactionId getPriorTransaction(Timestamp time, Branch branch) throws OseeCoreException {
      TransactionId priorTransactionId = null;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(
               "SELECT max(transaction_id) as prior_id FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("time") + " < ?",
               branch.getBranchId(), time);

         if (chStmt.next()) {
            int priorId = chStmt.getInt("prior_id");
            if (!chStmt.wasNull()) {
               priorTransactionId = getTransactionId(priorId);
            }
         }
      } finally {
         chStmt.close();
      }
      return priorTransactionId;
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
   public static Checksum getTransactionRangeChecksum(TransactionId startTransactionId, TransactionId endTransactionId) throws OseeCoreException {
      if (startTransactionId == null) throw new IllegalArgumentException("startTransactionId can not be null");
      if (endTransactionId == null) throw new IllegalArgumentException("endTransactionId can not be null");
      if (startTransactionId.getBranch() != endTransactionId.getBranch()) throw new IllegalArgumentException(
            "transactions must be on the same branch");
      if (startTransactionId.getTransactionNumber() > endTransactionId.getTransactionNumber()) throw new IllegalArgumentException(
            "startTransactionId can not be after endTransactionId");

      Checksum checksum = new Adler32();

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         if (startTransactionId.getTransactionNumber() == endTransactionId.getTransactionNumber()) {
            chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Transaction.SELECT_TX_GAMMAS),
                  startTransactionId.getBranchId(), startTransactionId.getTransactionNumber());
         } else {
            chStmt.runPreparedQuery(ClientSessionManager.getSQL(OseeSql.Transaction.SELECT_TX_GAMMAS_RANGE),
                  startTransactionId.getBranchId(), startTransactionId.getTransactionNumber(),
                  endTransactionId.getTransactionNumber());
         }
         while (chStmt.next()) {
            checksum.update(toBytes(chStmt.getLong("transaction_id")), 0, 8);
            checksum.update(toBytes(chStmt.getLong("gamma_id")), 0, 8);
         }
      } finally {
         chStmt.close();
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

   public static TransactionId getTransactionId(int transactionNumber) throws OseeCoreException {
      return getTransactionId(transactionNumber, null);
   }

   public static TransactionId getTransactionId(ConnectionHandlerStatement chStmt) throws OseeCoreException {
      return getTransactionId(chStmt.getInt("transaction_id"), chStmt);
   }

   private static TransactionId getTransactionId(int transactionNumber, ConnectionHandlerStatement chStmt) throws OseeCoreException {
      TransactionId transactionId = instance.nonEditableTransactionIdCache.get(transactionNumber);
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
            instance.nonEditableTransactionIdCache.put(transactionNumber, transactionId);
         } finally {
            if (useLocalConnection) {
               chStmt.close();
            }
         }
      }
      return transactionId;
   }
}