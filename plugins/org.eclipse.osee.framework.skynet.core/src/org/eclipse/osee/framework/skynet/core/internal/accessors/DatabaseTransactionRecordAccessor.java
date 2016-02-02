package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TransactionVersion;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.ITransactionDataAccessor;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.IdJoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

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

   private final JdbcClient jdbcClient;
   private final TransactionRecordFactory factory;
   private final TransactionCache cache;

   public DatabaseTransactionRecordAccessor(JdbcClient jdbcClient, TransactionCache cache, TransactionRecordFactory factory) {
      this.cache = cache;
      this.jdbcClient = jdbcClient;
      this.factory = factory;
   }

   @Override
   public void loadTransactionRecords(TransactionCache cache, Collection<Integer> transactionIds) throws OseeCoreException {
      if (transactionIds.isEmpty()) {
         return;
      }
      if (transactionIds.size() > 1) {
         IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery(jdbcClient);
         try {
            for (Integer txNumber : transactionIds) {
               joinQuery.add(txNumber);
            }
            joinQuery.store();

            loadTransactions(transactionIds.size(), SELECT_TRANSACTIONS_BY_QUERY_ID, joinQuery.getQueryId());

         } finally {
            joinQuery.delete();
         }
      } else {
         loadTransaction(SELECT_BY_TRANSACTION, transactionIds.iterator().next());
      }
   }

   @Override
   public TransactionRecord loadTransactionRecord(TransactionCache cache, BranchId branch, TransactionVersion transactionType) throws OseeCoreException {
      TransactionRecord toReturn = null;
      switch (transactionType) {
         case BASE:
            toReturn = loadTransaction(SELECT_BASE_TRANSACTION, branch.getUuid(), TransactionDetailsType.Baselined);
            break;
         case HEAD:
            toReturn = loadTransaction(SELECT_HEAD_TRANSACTION, branch.getUuid(), branch.getUuid());
            break;
         default:
            throw new OseeStateException("Transaction Type [%s] is not supported", transactionType);
      }
      return toReturn;
   }

   private TransactionRecord loadInternalTransaction(JdbcStatement stmt) {
      IOseeBranch branch = BranchManager.getBranch(stmt.getLong("branch_id"));
      int transactionNumber = stmt.getInt("transaction_id");
      String comment = stmt.getString("osee_comment");
      Date timestamp = stmt.getTimestamp("time");
      int authorArtId = stmt.getInt("author");
      int commitArtId = stmt.getInt("commit_art_id");
      TransactionDetailsType txType = TransactionDetailsType.toEnum(stmt.getInt("tx_type"));
      return factory.createOrUpdate(cache, transactionNumber, branch, comment, timestamp, authorArtId, commitArtId,
         txType);
   }

   private void loadTransactions(int expectedCount, String query, int queryId) throws OseeCoreException {
      jdbcClient.runQuery(this::loadInternalTransaction, query, queryId);
   }

   private TransactionRecord loadTransaction(String query, Object... parameters) throws OseeCoreException {
      return jdbcClient.fetchObject(null, this::loadInternalTransaction, query, parameters);
   }

   @Override
   public void load(TransactionCache transactionCache) throws OseeCoreException {
      // Not implemented
   }

   @Override
   public TransactionRecord getOrLoadPriorTransaction(TransactionCache cache, int transactionNumber, long branchUuid) throws OseeCoreException {
      int priorTransactionId =
         jdbcClient.runPreparedQueryFetchObject(-1, GET_PRIOR_TRANSACTION, branchUuid, transactionNumber);
      return cache.getOrLoad(priorTransactionId);
   }

   @Override
   public TransactionRecord getHeadTransaction(TransactionCache cache, BranchId branch) throws OseeCoreException {
      String query = ServiceUtil.getSql(OseeSql.TX_GET_MAX_AS_LARGEST_TX);
      return cache.getOrLoad(jdbcClient.runPreparedQueryFetchObject(-1, query, branch.getId()));
   }
}
