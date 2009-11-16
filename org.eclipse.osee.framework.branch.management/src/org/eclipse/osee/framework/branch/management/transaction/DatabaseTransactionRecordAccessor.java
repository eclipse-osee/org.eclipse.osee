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
package org.eclipse.osee.framework.branch.management.transaction;

import java.util.Date;
import org.eclipse.osee.framework.branch.management.ITransactionService.TransactionVersion;
import org.eclipse.osee.framework.core.data.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseTransactionRecordAccessor implements ITransactionDataAccessor {

   private static final String SELECT_BASE_TRANSACTION =
         "SELECT * FROM osee_tx_details WHERE branch_id = ? AND tx_type = " + TransactionDetailsType.Baselined;

   private static final String SELECT_BY_TRANSACTION = "SELECT * FROM osee_tx_details WHERE transaction_id = ?";

   private static final String SELECT_BRANCH_TRANSACTIONS =
         "SELECT * from osee_tx_details where branch_id = ? order by transaction_id DESC";

   private final IOseeDatabaseService oseeDatabase;
   private final AbstractOseeCache<Branch> branchCache;

   public DatabaseTransactionRecordAccessor(IOseeDatabaseService oseeDatabase, AbstractOseeCache<Branch> branchCache) {
      this.oseeDatabase = oseeDatabase;
      this.branchCache = branchCache;
   }

   public void loadTransactionRecord(TransactionCache cache, int transactionId) throws OseeCoreException {
      loadFromTransaction(cache, null, SELECT_BY_TRANSACTION, transactionId);
   }

   public void loadTransactionRecord(TransactionCache cache, Branch branch) throws OseeCoreException {
      loadFromTransaction(cache, branch, SELECT_BRANCH_TRANSACTIONS, branch.getId());
   }

   public void loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) throws OseeCoreException {
      switch (transactionType) {
         case BASE:
            loadFromTransaction(cache, branch, SELECT_BASE_TRANSACTION, branch.getId());
            break;
         case HEAD:
            loadFirstTransactionRecord(cache, branch, SELECT_BRANCH_TRANSACTIONS, branch.getId());
            break;
         default:
            throw new OseeStateException(String.format("Transaction Type [%s] is not supported", transactionType));
      }
   }

   private void loadFirstTransactionRecord(TransactionCache cache, Branch branch, String query, Object... parameters) throws OseeCoreException {
      loadFromTransaction(cache, branch, true, query, parameters);
   }

   private void loadFromTransaction(TransactionCache cache, Branch branch, String query, Object... parameters) throws OseeCoreException {
      loadFromTransaction(cache, branch, false, query, parameters);
   }

   private void loadFromTransaction(TransactionCache cache, Branch branch, boolean isOnlyReadFirstResult, String query, Object... parameters) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = oseeDatabase.getStatement();
      try {
         chStmt.runPreparedQuery(query, parameters);
         while (chStmt.next()) {
            if (branch == null) {
               branch = branchCache.getById(chStmt.getInt("branch_id"));
            }
            int transactionNumber = chStmt.getInt("transaction_id");
            String comment = chStmt.getString("osee_comment");
            Date time = chStmt.getTimestamp("time");
            int authorArtId = chStmt.getInt("author");
            int commitArtId = chStmt.getInt("commit_art_id");
            TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));

            cacheAndUpdate(cache, transactionNumber, branch, comment, time, authorArtId, commitArtId, txType);
            if (isOnlyReadFirstResult) {
               break;
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void cacheAndUpdate(TransactionCache cache, int transactionNumber, Branch branch, String comment, Date time, int authorArtId, int commitArtId, TransactionDetailsType txType) {
      TransactionRecord record = cache.getById(transactionNumber);
      if (record == null) {
         record = new TransactionRecord(transactionNumber, branch, comment, time, authorArtId, commitArtId, txType);
         cache.cache(record);
      } else {
         record.setAuthor(authorArtId);
         record.setComment(comment);
         record.setCommit(commitArtId);
         record.setTime(time);
      }
   }

}
