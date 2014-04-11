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
package org.eclipse.osee.orcs.db.internal.accessor;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.ITransactionDataAccessor;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseTransactionRecordAccessor implements ITransactionDataAccessor {

   private static final String SELECT_BASE_TRANSACTION =
      "select * from osee_tx_details where branch_id = ? and tx_type = ?";

   private static final String SELECT_BY_TRANSACTION = "select * from osee_tx_details WHERE transaction_id = ?";

   private static final String SELECT_HEAD_TRANSACTION =
      "select * from osee_tx_details where transaction_id = (select max(transaction_id) from osee_tx_details where branch_id = ?) and branch_id = ?";

   private static final String SELECT_TRANSACTIONS_BY_QUERY_ID =
      "select * from osee_join_id oji, osee_tx_details txd where oji.query_id = ? and txd.transaction_id = oji.id";

   private static final String SELECT_NON_EXISTING_TRANSACTIONS_BY_QUERY_ID =
      "select oji.id from osee_join_id oji where oji.query_id = ? and not exists (select 1 from osee_tx_details txd where txd.transaction_id = oji.id)";

   private static final String GET_PRIOR_TRANSACTION =
      "select max(transaction_id) FROM osee_tx_details where branch_id = ? and transaction_id < ?";

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

            loadTransactions(cache, transactionIds.size(), SELECT_TRANSACTIONS_BY_QUERY_ID, joinQuery.getQueryId());

         } finally {
            joinQuery.delete();
         }
      } else {
         loadTransaction(cache, SELECT_BY_TRANSACTION, transactionIds.iterator().next());
      }
   }

   @Override
   public TransactionRecord loadTransactionRecord(TransactionCache cache, Branch branch, TransactionVersion transactionType) throws OseeCoreException {
      ensureDependantCachePopulated();
      TransactionRecord toReturn = null;
      switch (transactionType) {
         case BASE:
            toReturn =
               loadTransaction(cache, SELECT_BASE_TRANSACTION, branch.getUuid(), TransactionDetailsType.Baselined);
            break;
         case HEAD:
            toReturn = loadTransaction(cache, SELECT_HEAD_TRANSACTION, branch.getUuid(), branch.getUuid());
            break;
         default:
            throw new OseeStateException("Transaction Type [%s] is not supported", transactionType);
      }
      return toReturn;
   }

   private void loadTransactions(TransactionCache cache, int expectedCount, String query, int queryId) throws OseeCoreException {
      MutableInteger numberLoaded = new MutableInteger(-1);
      loadFromTransaction(cache, expectedCount, numberLoaded, query, queryId);

      if (numberLoaded.getValue() != expectedCount) {
         IOseeStatement chStmt = oseeDatabaseService.getStatement();
         try {
            chStmt.runPreparedQuery(expectedCount, SELECT_NON_EXISTING_TRANSACTIONS_BY_QUERY_ID, queryId);
            while (chStmt.next()) {
               int transactionNumber = chStmt.getInt("id");
               factory.getOrCreate(cache, transactionNumber, branchCache);
            }
         } finally {
            chStmt.close();
         }
      }
   }

   private TransactionRecord loadTransaction(TransactionCache cache, String query, Object... parameters) throws OseeCoreException {
      return loadFromTransaction(cache, 1, new MutableInteger(0), query, parameters);
   }

   private TransactionRecord loadFromTransaction(TransactionCache cache, int expectedCount, MutableInteger numberLoaded, String query, Object... parameters) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseService.getStatement();
      TransactionRecord record = null;
      int count = 0;
      try {
         chStmt.runPreparedQuery(expectedCount, query, parameters);
         while (chStmt.next()) {
            count++;
            long branchUuid = chStmt.getLong("branch_id");
            int transactionNumber = chStmt.getInt("transaction_id");
            String comment = chStmt.getString("osee_comment");
            Date timestamp = chStmt.getTimestamp("time");
            int authorArtId = chStmt.getInt("author");
            int commitArtId = chStmt.getInt("commit_art_id");
            TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));

            record =
               prepareTransactionRecord(cache, transactionNumber, branchUuid, comment, timestamp, authorArtId,
                  commitArtId, txType);
         }
         numberLoaded.setValue(count);
      } finally {
         chStmt.close();
      }
      return record;
   }

   private TransactionRecord prepareTransactionRecord(TransactionCache cache, int transactionNumber, long branchUuid, String comment, Date timestamp, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException {
      TransactionRecord record =
         factory.createOrUpdate(cache, transactionNumber, branchUuid, comment, timestamp, authorArtId, commitArtId,
            txType, branchCache);
      record.clearDirty();
      return record;
   }

   @Override
   public void load(TransactionCache transactionCache) throws OseeCoreException {
      // Not implemented
   }

   @Override
   public TransactionRecord getOrLoadPriorTransaction(TransactionCache cache, int transactionNumber, long branchUuid) throws OseeCoreException {
      int priorTransactionId =
         oseeDatabaseService.runPreparedQueryFetchObject(-1, GET_PRIOR_TRANSACTION, branchUuid, transactionNumber);
      return cache.getOrLoad(priorTransactionId);
   }

   @Override
   public TransactionRecord getHeadTransaction(TransactionCache cache, Branch branch) throws OseeCoreException {
      return cache.getOrLoad(oseeDatabaseService.runPreparedQueryFetchObject(-1,
         OseeSql.TX_GET_MAX_AS_LARGEST_TX.getSql(), branch.getUuid()));
   }
}
