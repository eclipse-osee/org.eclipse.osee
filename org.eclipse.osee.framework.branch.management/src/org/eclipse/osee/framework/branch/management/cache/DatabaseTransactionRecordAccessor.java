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
package org.eclipse.osee.framework.branch.management.cache;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.ITransactionDataAccessor;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseTransactionRecordAccessor implements ITransactionDataAccessor {

   private static final String SELECT_BASE_TRANSACTION =
         "select * from osee_tx_details WHERE branch_id = ? AND tx_type = " + TransactionDetailsType.Baselined;

   private static final String SELECT_BY_TRANSACTION = "select * from osee_tx_details WHERE transaction_id = ?";

   private static final String SELECT_BRANCH_TRANSACTIONS =
         "select * from osee_tx_details where branch_id = ? order by transaction_id DESC";

   private static final String SELECT_TRANSACTIONS_BY_QUERY_ID =
         "select * from osee_tx_details txd, osee_join_id oji where txd.transaction_id = oji.id and oji.query_id = ?";

   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;
   private final BranchCache branchCache;

   public DatabaseTransactionRecordAccessor(IOseeDatabaseServiceProvider oseeDatabaseProvider, BranchCache branchCache) {
      this.oseeDatabaseProvider = oseeDatabaseProvider;
      this.branchCache = branchCache;
   }

   public void loadTransactionRecord(TransactionCache cache, Collection<Integer> transactionIds) throws OseeCoreException {
      if (transactionIds == null || transactionIds.isEmpty()) {
         throw new OseeCoreException("transaction ids cannot be null or empty");
      }
      if (transactionIds.size() > 1) {
         IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery();
         try {
            for (Integer txNumber : transactionIds) {
               joinQuery.add(txNumber);
            }
            joinQuery.store();

            loadFromTransaction(cache, null, 5000, false, SELECT_TRANSACTIONS_BY_QUERY_ID, joinQuery.getQueryId());

         } finally {
            joinQuery.delete();
         }
      } else {
         loadFromTransaction(cache, null, 1, SELECT_BY_TRANSACTION, transactionIds.iterator().next());
      }
   }

   public void loadTransactionRecord(TransactionCache cache, Branch branch) throws OseeCoreException {
      loadFromTransaction(cache, branch, 1000, SELECT_BRANCH_TRANSACTIONS, branch.getId());
   }

   public void loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) throws OseeCoreException {
      switch (transactionType) {
         case BASE:
            loadFirstTransactionRecord(cache, branch, SELECT_BASE_TRANSACTION, branch.getId());
            break;
         case HEAD:
            loadFirstTransactionRecord(cache, branch, SELECT_BRANCH_TRANSACTIONS, branch.getId());
            break;
         default:
            throw new OseeStateException(String.format("Transaction Type [%s] is not supported", transactionType));
      }
   }

   private void loadFirstTransactionRecord(TransactionCache cache, Branch branch, String query, Object... parameters) throws OseeCoreException {
      loadFromTransaction(cache, branch, 1, true, query, parameters);
   }

   private void loadFromTransaction(TransactionCache cache, Branch branch, int fetchSize, String query, Object... parameters) throws OseeCoreException {
      loadFromTransaction(cache, branch, fetchSize, false, query, parameters);
   }

   private void loadFromTransaction(TransactionCache cache, Branch branch, int fetchSize, boolean isOnlyReadFirstResult, String query, Object... parameters) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(fetchSize, query, parameters);
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
      //      TransactionRecord record = cache.getById(transactionNumber);
      //      if (record == null) {
      //         record = new TransactionRecord(transactionNumber, branch, comment, time, authorArtId, commitArtId, txType);
      //         cache.cache(record);
      //      } else {
      //         record.setAuthor(authorArtId);
      //         record.setComment(comment);
      //         record.setCommit(commitArtId);
      //         record.setTime(time);
      //      }
   }

}
