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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TXD_COMMENT;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * Manages a cache of <code>TransactionId</code>.
 * 
 * @author Jeff C. Phillips
 */
public class TransactionIdManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(TransactionIdManager.class);
   public static final String NO_TRANSACTIONS_MESSAGE = "No transactions where found in the database for branch: ";
   private static final String largestTransIdSql =
         "SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "largest_transaction_id") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?";
   private static final String SELECT_COMMENT =
         "SELECT " + TRANSACTION_DETAIL_TABLE.column(TXD_COMMENT) + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " = ?";
   private static final String SELECT_MAX_MIN_TX =
         "SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "max_id") + "," + TRANSACTION_DETAIL_TABLE.min(
               "transaction_id", "min_id") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE branch_id = ?";
   private static final String SELECT_TX_GAMMAS =
         "SELECT " + TRANSACTIONS_TABLE.columns("transaction_id", "gamma_id") + " FROM " + TRANSACTION_DETAIL_TABLE + "," + TRANSACTIONS_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.join(
               TRANSACTIONS_TABLE, "transaction_id") + " ORDER BY " + TRANSACTIONS_TABLE.columns("transaction_id",
               "gamma_id");
   private static final String SELECT_TX_GAMMAS_RANGE =
         "SELECT " + TRANSACTIONS_TABLE.columns("transaction_id", "gamma_id") + " FROM " + TRANSACTION_DETAIL_TABLE + "," + TRANSACTIONS_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ">? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=? AND " + TRANSACTION_DETAIL_TABLE.join(
               TRANSACTIONS_TABLE, "transaction_id") + " ORDER BY " + TRANSACTIONS_TABLE.columns("transaction_id",
               "gamma_id");
   private final Map<Branch, TransactionId> editableTransactionCache;
   private final Map<Integer, TransactionId> nonEditableTransactionIdCache;
   private static final TransactionIdManager reference = new TransactionIdManager();

   public static TransactionIdManager getInstance() {
      return reference;
   }

   private TransactionIdManager() {
      this.editableTransactionCache = new HashMap<Branch, TransactionId>();
      this.nonEditableTransactionIdCache = new HashMap<Integer, TransactionId>();
   }

   /**
    * Returns the transaction corresponding to current head of the given branch
    * 
    * @param branch
    */
   public TransactionId getEditableTransactionId(Branch branch) {
      TransactionId transactionId = editableTransactionCache.get(branch);

      if (transactionId == null) {
         transactionId = createTransactionId(getlatestTransactionForBranch(branch), branch, true);
      }
      return transactionId;
   }

   public TransactionId getPossiblyEditableTransactionId(Integer transactionNumber) throws SQLException {
      return getPossiblyEditableTransactionId(transactionNumber, true);
   }

   /**
    * Returns a transactionId. If an editable transaction is cached with the number it will be returned, otherwise a
    * non-editable transaction will be created if necessary and returned.
    * 
    * @param transactionNumber
    */
   public TransactionId getPossiblyEditableTransactionIfFromCache(Integer transactionNumber) throws SQLException {
      return getPossiblyEditableTransactionId(transactionNumber, false);
   }

   private TransactionId getPossiblyEditableTransactionId(Integer transactionNumber, boolean aggresive) throws SQLException {
      TransactionId transactionId;
      Branch branch = BranchPersistenceManager.getInstance().getBranchForTransactionNumber(transactionNumber);

      if (aggresive) {
         // force the editable cache to be loaded for the branch
         getEditableTransactionId(branch);
      }

      transactionId = editableTransactionCache.get(branch);
      if (transactionId != null && transactionId.getTransactionNumber() == transactionNumber) {
         return transactionId;
      }

      if (nonEditableTransactionIdCache.containsKey(transactionNumber)) {
         transactionId = nonEditableTransactionIdCache.get(transactionNumber);
      } else {
         transactionId = createTransactionId(transactionNumber, branch, false);
      }

      if (transactionId.getTransactionNumber() != transactionNumber) {
         throw new IllegalStateException("TransactionId " + transactionId + " found for number " + transactionNumber);
      }

      return transactionId;
   }

   /**
    * Returns a non-editable transactionId. If the transactionId is located in cache it will be returned. Otherwise a
    * new transactionId will be created, cached and then returned.
    * 
    * @param transactionNumber
    */
   public TransactionId getNonEditableTransactionId(Integer transactionNumber) throws SQLException {
      TransactionId transactionId;

      if (nonEditableTransactionIdCache.containsKey(transactionNumber)) {
         transactionId = nonEditableTransactionIdCache.get(transactionNumber);
      } else {
         Branch branch = BranchPersistenceManager.getInstance().getBranchForTransactionNumber(transactionNumber);
         transactionId = createTransactionId(transactionNumber, branch, false);
      }

      if (transactionId.getTransactionNumber() != transactionNumber) {
         throw new IllegalStateException("TransactionId " + transactionId + " found for number " + transactionNumber);
      }

      return transactionId;
   }

   /**
    * Replaces the current editable transactionId object with a new transactionId object created with the
    * newTransactionNumber. Note: the old transactionId will be set to uneditable and moved from the editable cache to
    * the non-editable cache. However, the old transactionId will still remain in the transactionCache.
    * 
    * @param newTransactionNumber
    * @param branch
    */
   public void resetEditableTransactionId(Integer newTransactionNumber, Branch branch) {
      TransactionId oldEditabletransactionId = getEditableTransactionId(branch);
      oldEditabletransactionId.setHead(false);
      editableTransactionCache.remove(oldEditabletransactionId);
      nonEditableTransactionIdCache.put(oldEditabletransactionId.getTransactionNumber(), oldEditabletransactionId);

      createTransactionId(newTransactionNumber, branch, true);
   }

   /**
    * Returns the editable transactionId object with the newTransactionNumber.
    * 
    * @param newTransactionNumber
    * @param branch
    */
   public synchronized TransactionId updateEditableTransactionId(Integer newTransactionNumber, Branch branch) {
      TransactionId transactionId = getEditableTransactionId(branch);

      if (transactionId == null) {
         // should only happen when there are no transactions in the db (only on bootstrapping)
         transactionId = createTransactionId(newTransactionNumber, branch, true);
      } else {
         transactionId.setTransactionNumber(newTransactionNumber);
      }

      return transactionId;
   }

   private int getlatestTransactionForBranch(Branch branch) {
      int transactionNumber = -1;
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(largestTransIdSql, SQL3DataType.INTEGER, branch.getBranchId());
         if (chStmt.next()) {
            transactionNumber = chStmt.getRset().getInt("largest_transaction_id");
         } else {
            throw new IllegalArgumentException(NO_TRANSACTIONS_MESSAGE + branch);
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      } finally {
         DbUtil.close(chStmt);
      }
      return transactionNumber;
   }

   private TransactionId createTransactionId(int transactionNumber, Branch branch, boolean head) {
      TransactionId transactionId =
            new TransactionId(transactionNumber, branch, head, getTransactionComment(transactionNumber));

      if (head) {
         editableTransactionCache.put(branch, transactionId);
      } else {
         nonEditableTransactionIdCache.put(transactionNumber, transactionId);
      }
      return transactionId;
   }

   private String getTransactionComment(int transactionNumber) {
      ConnectionHandlerStatement chStmt = null;
      String comment = "";
      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_COMMENT, SQL3DataType.INTEGER, transactionNumber);

         ResultSet rset = chStmt.getRset();
         if (rset.next()) {
            comment = rset.getString(TXD_COMMENT);
         }
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
      } finally {
         DbUtil.close(chStmt);
      }

      return comment;
   }

   /**
    * Called when transaction is orginated from a remote event.
    * 
    * @param branch
    * @throws SQLException
    */
   public TransactionSwitch switchTransaction(Branch branch) throws SQLException {
      int priorEditableTransactionNumber = getEditableTransactionId(branch).getTransactionNumber();
      int editableTransactionNumber = getlatestTransactionForBranch(branch);
      TransactionId editableTransactionId;
      TransactionId priorEditableTransactionId;

      if ((editableTransactionNumber == priorEditableTransactionNumber)) {
         priorEditableTransactionId = getPossiblyEditableTransactionIfFromCache(priorEditableTransactionNumber);
         editableTransactionId = priorEditableTransactionId;
      } else {
         editableTransactionId = updateEditableFromDb(branch);
         priorEditableTransactionId = getPossiblyEditableTransactionIfFromCache(priorEditableTransactionNumber);
      }
      return new TransactionSwitch(editableTransactionId, priorEditableTransactionId);
   }

   private TransactionId updateEditableFromDb(Branch branch) {
      int transactionNumber = getlatestTransactionForBranch(branch);
      return updateEditableTransactionId(transactionNumber, branch);
   }

   public synchronized Pair<Integer, TransactionId> createNextTransactionId(Branch branch) throws SQLException {
      Integer transactionNumber = SkynetDatabase.getNextTransactionId();

      return new Pair<Integer, TransactionId>(transactionNumber, updateEditableTransactionId(transactionNumber, branch));
   }

   public Pair<TransactionId, TransactionId> getStartEndPoint(Branch branch) throws IllegalStateException, SQLException {
      return getStartEndPoint(branch, false);
   }

   /**
    * @throws SQLException
    * @throws IllegalStateException occurs when the datastore query returns no min or max
    */
   public Pair<TransactionId, TransactionId> getNonEditableStartEndPoint(Branch branch) throws SQLException, IllegalStateException {
      return getStartEndPoint(branch, true);
   }

   private Pair<TransactionId, TransactionId> getStartEndPoint(Branch branch, boolean nonEditable) throws SQLException, IllegalStateException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_MAX_MIN_TX, SQL3DataType.INTEGER, branch.getBranchId());

         ResultSet rset = chStmt.getRset();
         // the max, min query will return exactly 1 row by definition (even if there is no max or min)
         rset.next();

         TransactionId minId;
         TransactionId maxId;

         if (nonEditable) {
            minId = getNonEditableTransactionId(rset.getInt("min_id"));
            maxId = getNonEditableTransactionId(rset.getInt("max_id"));
         } else {
            minId = getPossiblyEditableTransactionIfFromCache(rset.getInt("min_id"));
            maxId = getPossiblyEditableTransactionIfFromCache(rset.getInt("max_id"));
         }

         if (rset.wasNull()) {
            throw new IllegalStateException("Branch " + branch + " has no transactions");
         }

         return new Pair<TransactionId, TransactionId>(minId, maxId);
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public class TransactionSwitch {
      private final TransactionId editableTransactionId;
      private final TransactionId nonEditableTransactionId;

      /**
       * @param editableTransactionId
       * @param nonEditableTransactionId
       */
      public TransactionSwitch(TransactionId editableTransactionId, TransactionId nonEditableTransactionId) {
         super();
         this.editableTransactionId = editableTransactionId;
         this.nonEditableTransactionId = nonEditableTransactionId;
      }

      /**
       * @return Returns the editableTransactionId.
       */
      public TransactionId getEditableTransactionId() {
         return editableTransactionId;
      }

      /**
       * @return Returns the nonEditableTransactionId.
       */
      public TransactionId getNonEditableTransactionId() {
         return nonEditableTransactionId;
      }
   }

   /**
    * @param time
    * @param branch
    * @return The prior transactionId, or null if there is no prior.
    */
   public TransactionId getPriorTransaction(Timestamp time, Branch branch) {
      TransactionId priorTransactionId = null;
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(
                     "SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "prior_id") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("time") + " < ?",
                     SQL3DataType.INTEGER, branch.getBranchId(), SQL3DataType.TIMESTAMP, time);

         ResultSet rset = chStmt.getRset();
         if (rset.next()) {
            int priorId = rset.getInt("prior_id");
            if (!rset.wasNull()) priorTransactionId = getPossiblyEditableTransactionIfFromCache(priorId);
         }
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
      } finally {
         DbUtil.close(chStmt);
      }
      return priorTransactionId;
   }

   /**
    * @param transactionId
    * @return The prior transactionId, or null if there is no prior.
    * @throws SQLException
    */
   public TransactionId getPriorTransaction(TransactionId transactionId) throws SQLException {
      TransactionId priorTransactionId = null;
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(
                     "SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id", "prior_id") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " < ?",
                     SQL3DataType.INTEGER, transactionId.getBranch().getBranchId(), SQL3DataType.INTEGER,
                     transactionId.getTransactionNumber());

         ResultSet rset = chStmt.getRset();
         if (rset.next()) {
            int priorId = rset.getInt("prior_id");
            if (!rset.wasNull()) priorTransactionId = getPossiblyEditableTransactionIfFromCache(priorId);
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return priorTransactionId;
   }

   public TransactionId getNextTransaction(TransactionId transactionId) {
      TransactionId nextTransactionId = null;
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(
                     "SELECT MIN(transaction_id) next_id " + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ? " + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " > ?",
                     SQL3DataType.INTEGER, transactionId.getBranch().getBranchId(), SQL3DataType.INTEGER,
                     transactionId.getTransactionNumber());

         ResultSet rset = chStmt.getRset();
         if (rset.next()) {
            int nextId = rset.getInt("next_id");
            if (!rset.wasNull()) nextTransactionId = getPossiblyEditableTransactionIfFromCache(nextId);
         }
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
      } finally {
         DbUtil.close(chStmt);
      }
      return nextTransactionId;
   }

   public TransactionId getParentBaseTransaction(Branch branch) throws SQLException {
      if (branch == null) throw new IllegalArgumentException("branch can not be null.");
      if (branch.getParentBranch() == null) throw new IllegalArgumentException("branch must have a parent branch.");

      TransactionId baseParentTransactionId = null;
      TransactionId baseTransaction = getStartEndPoint(branch).getKey();

      String baseComment = baseTransaction.getComment();
      int baseParentTransactionNumber = getParentBaseTransactionNumber(baseComment);
      if (baseParentTransactionNumber != -1) {
         baseParentTransactionId = getPossiblyEditableTransactionIfFromCache(baseParentTransactionNumber);
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

   public Checksum getTransactionRangeChecksum(TransactionId startTransactionId, TransactionId endTransactionId) throws SQLException {
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
                  ConnectionHandler.runPreparedQuery(SELECT_TX_GAMMAS, SQL3DataType.INTEGER,
                        startTransactionId.getBranch().getBranchId(), SQL3DataType.INTEGER,
                        startTransactionId.getTransactionNumber());
         } else {
            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_TX_GAMMAS_RANGE, SQL3DataType.INTEGER,
                        startTransactionId.getBranch().getBranchId(), SQL3DataType.INTEGER,
                        startTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
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

   public byte[] toBytes(long val) {
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
}