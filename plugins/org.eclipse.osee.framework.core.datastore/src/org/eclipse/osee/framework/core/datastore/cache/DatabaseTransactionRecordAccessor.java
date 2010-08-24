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
package org.eclipse.osee.framework.core.datastore.cache;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.ITransactionDataAccessor;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseTransactionRecordAccessor implements ITransactionDataAccessor {

   private static final String SELECT_BASE_TRANSACTION =
      "select txd.* from osee_branch ob, osee_tx_details txd WHERE ob.branch_id = ? AND ob.baseline_transaction_id = txd.transaction_id";

   private static final String SELECT_BY_TRANSACTION = "select * from osee_tx_details WHERE transaction_id = ?";

   private static final String SELECT_BRANCH_TRANSACTIONS =
      "select * from osee_tx_details where branch_id = ? order by transaction_id DESC";

   private static final String SELECT_TRANSACTIONS_BY_QUERY_ID =
      "select * from osee_tx_details txd, osee_join_id oji where txd.transaction_id = oji.id and oji.query_id = ?";

   private final IOseeDatabaseService oseeDatabaseService;
   private final BranchCache branchCache;
   private final TransactionRecordFactory factory;

   public DatabaseTransactionRecordAccessor(IOseeDatabaseService oseeDatabaseService, BranchCache branchCache, TransactionRecordFactory factory) {
      this.oseeDatabaseService = oseeDatabaseService;
      this.branchCache = branchCache;
      this.factory = factory;
   }

   private synchronized void ensureDependantCachePopulated() throws OseeCoreException {
      branchCache.ensurePopulated();
   }

   @Override
   public void loadTransactionRecord(TransactionCache cache, Collection<Integer> transactionIds) throws OseeCoreException {
      if (transactionIds.isEmpty()) {
         return;
      }
      ensureDependantCachePopulated();
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

   @Override
   public void loadTransactionRecord(TransactionCache cache, Branch branch) throws OseeCoreException {
      ensureDependantCachePopulated();
      loadFromTransaction(cache, branch, 1000, SELECT_BRANCH_TRANSACTIONS, branch.getId());
   }

   @Override
   public TransactionRecord loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) throws OseeCoreException {
      ensureDependantCachePopulated();
      TransactionRecord toReturn = null;
      switch (transactionType) {
         case BASE:
            toReturn = loadFirstTransactionRecord(cache, branch, SELECT_BASE_TRANSACTION, branch.getId());
            break;
         case HEAD:
            toReturn = loadFirstTransactionRecord(cache, branch, SELECT_BRANCH_TRANSACTIONS, branch.getId());
            break;
         default:
            throw new OseeStateException(String.format("Transaction Type [%s] is not supported", transactionType));
      }
      return toReturn;
   }

   private TransactionRecord loadFirstTransactionRecord(TransactionCache cache, Branch branch, String query, Object... parameters) throws OseeCoreException {
      ensureDependantCachePopulated();
      return loadFromTransaction(cache, branch, 1, true, query, parameters);
   }

   private void loadFromTransaction(TransactionCache cache, Branch branch, int fetchSize, String query, Object... parameters) throws OseeCoreException {
      ensureDependantCachePopulated();
      loadFromTransaction(cache, branch, fetchSize, false, query, parameters);
   }

   private TransactionRecord loadFromTransaction(TransactionCache cache, Branch branch, int fetchSize, boolean isOnlyReadFirstResult, String query, Object... parameters) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseService.getStatement();
      TransactionRecord record = null;
      try {
         chStmt.runPreparedQuery(fetchSize, query, parameters);
         while (chStmt.next()) {
            int branchId = chStmt.getInt("branch_id");
            int transactionNumber = chStmt.getInt("transaction_id");
            String comment = chStmt.getString("osee_comment");
            Date timestamp = chStmt.getTimestamp("time");
            int authorArtId = chStmt.getInt("author");
            int commitArtId = chStmt.getInt("commit_art_id");
            TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));

            record =
               prepareTransactionRecord(cache, transactionNumber, branchId, comment, timestamp, authorArtId,
                  commitArtId, txType);
            if (isOnlyReadFirstResult) {
               break;
            }
         }
      } finally {
         chStmt.close();
      }
      return record;
   }

   private TransactionRecord prepareTransactionRecord(TransactionCache cache, int transactionNumber, int branchId, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException {
      TransactionRecord record =
         factory.createOrUpdate(cache, transactionNumber, branchId, comment, timestamp, authorArtId, commitArtId,
            txType);
      record.setBranchCache(branchCache);
      record.clearDirty();
      return record;
   }

   @SuppressWarnings("unused")
   @Override
   public void load(TransactionCache transactionCache) throws OseeCoreException {
      // Not implemented
   }
}
