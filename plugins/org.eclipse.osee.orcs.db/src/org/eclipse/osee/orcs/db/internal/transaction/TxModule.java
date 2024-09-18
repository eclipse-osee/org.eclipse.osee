/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.transaction;

import static org.eclipse.osee.framework.jdk.core.util.Conditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.callable.LoadArtifactHistory;
import org.eclipse.osee.orcs.db.internal.callable.PurgeTransactionTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.SetTransactionTxCallable;
import org.eclipse.osee.orcs.db.internal.health.PurgeUnusedBackingDataAndTransactions;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class TxModule {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory sqlJoinFactory;
   private final IdentityManager idManager;
   private final ActivityLog activityLog;
   private static final String UPDATE_TRANSACTION_COMMIT_ART_ID =
      "UPDATE osee_tx_details SET commit_art_id = ? WHERE transaction_id = ?";

   public TxModule(Log logger, JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory, IdentityManager identityService, ActivityLog activityLog) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
      this.idManager = identityService;
      this.activityLog = activityLog;
   }

   public TxDataStore createTransactionStore(DataLoaderFactory dataLoaderFactory, QueryEngineIndexer indexer,
      OrcsTokenService tokenService) {
      final TransactionProcessorProviderImpl processors = new TransactionProcessorProviderImpl();
      processors.add(TxWritePhaseEnum.BEFORE_TX_WRITE, new ComodificationCheck(dataLoaderFactory));
      processors.add(TxWritePhaseEnum.AFTER_TX_WRITE, new TransactionIndexer(logger, indexer, tokenService));
      return new TxDataStore() {

         @Override
         public Callable<TransactionResult> commitTransaction(OrcsSession session, TransactionData data) {
            TxSqlBuilderImpl builder = new TxSqlBuilderImpl(sqlJoinFactory, idManager, jdbcClient);
            TransactionWriter writer = new TransactionWriter(logger, jdbcClient, builder);
            return new CommitTransactionDatabaseTxCallable(logger, session, jdbcClient, idManager, processors, writer,
               data);
         }

         @Override
         public Callable<Integer> purgeTransactions(OrcsSession session,
            Collection<? extends TransactionId> transactionsToPurge) {
            return new PurgeTransactionTxCallable(activityLog, session, jdbcClient, sqlJoinFactory,
               transactionsToPurge);
         }

         @Override
         public Callable<Void> setTransactionComment(OrcsSession session, TransactionId transaction, String comment) {
            return new SetTransactionTxCallable(logger, session, jdbcClient, transaction, comment);
         }

         @Override
         public int[] purgeUnusedBackingDataAndTransactions() {
            PurgeUnusedBackingDataAndTransactions op;
            op = new PurgeUnusedBackingDataAndTransactions(jdbcClient);
            return op.purgeUnused();
         }

         @Override
         public int[] purgeUnusedBackingDataAndTransactions(List<Long> gammasToPurge, List<String> additionalStatements,
            String prefixRecoveryFile) {
            PurgeUnusedBackingDataAndTransactions op;
            op = new PurgeUnusedBackingDataAndTransactions(jdbcClient);
            return op.purgeListOfGammas(gammasToPurge, additionalStatements, prefixRecoveryFile);
         }

         @Override
         public void setTransactionCommitArtifact(OrcsSession session, TransactionId transaction,
            ArtifactId commitArt) {
            checkNotNull(transaction, "transaction");
            checkNotNull(commitArt, "commitArt");
            jdbcClient.runPreparedUpdate(UPDATE_TRANSACTION_COMMIT_ART_ID, commitArt, transaction);
         }

         @Override
         public Callable<List<ChangeItem>> getArtifactHistory(OrcsSession session, QueryFactory queryFactory,
            ArtifactId artifact, BranchId branch) {
            return new LoadArtifactHistory(logger, session, queryFactory, tokenService, jdbcClient, artifact, branch);
         }
      };
   }
}