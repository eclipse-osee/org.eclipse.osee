/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.transaction;

import static org.eclipse.osee.framework.jdk.core.util.Conditions.checkNotNull;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.callable.PurgeTransactionTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeUnusedBackingDataAndTransactions;
import org.eclipse.osee.orcs.db.internal.callable.SetTransactionTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class TxModule {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory sqlJoinFactory;
   private final IdentityManager idManager;
   private static final String UPDATE_TRANSACTION_COMMIT_ART_ID =
      "UPDATE osee_tx_details SET commit_art_id = ? WHERE transaction_id = ?";

   public TxModule(Log logger, JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory, IdentityManager identityService) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
      this.idManager = identityService;
   }

   public TxDataStore createTransactionStore(DataLoaderFactory dataLoaderFactory, QueryEngineIndexer indexer, AttributeTypes types) {
      final TransactionProcessorProviderImpl processors = new TransactionProcessorProviderImpl();
      processors.add(TxWritePhaseEnum.BEFORE_TX_WRITE, new ComodificationCheck(dataLoaderFactory));
      processors.add(TxWritePhaseEnum.AFTER_TX_WRITE, new TransactionIndexer(logger, indexer, types));
      return new TxDataStore() {

         @Override
         public Callable<TransactionResult> commitTransaction(OrcsSession session, TransactionData data) {
            TxSqlBuilderImpl builder = new TxSqlBuilderImpl(sqlJoinFactory, idManager, jdbcClient);
            TransactionWriter writer = new TransactionWriter(logger, jdbcClient, builder);
            return new CommitTransactionDatabaseTxCallable(logger, session, jdbcClient, idManager, processors, writer,
               data);
         }

         @Override
         public Callable<Integer> purgeTransactions(OrcsSession session, Collection<? extends TransactionId> transactionsToPurge) {
            return new PurgeTransactionTxCallable(logger, session, jdbcClient, sqlJoinFactory, transactionsToPurge);
         }

         @Override
         public Callable<Void> setTransactionComment(OrcsSession session, TransactionId transaction, String comment) {
            return new SetTransactionTxCallable(logger, session, jdbcClient, transaction, comment);
         }

         @Override
         public int[] purgeUnusedBackingDataAndTransactions() {
            PurgeUnusedBackingDataAndTransactions op = new PurgeUnusedBackingDataAndTransactions(jdbcClient);
            return op.purge();
         }

         @Override
         public void setTransactionCommitArtifact(OrcsSession session, TransactionId transaction, ArtifactToken commitArt) {
            checkNotNull(transaction, "transaction");
            checkNotNull(commitArt, "commitArt");

            jdbcClient.runPreparedUpdate(UPDATE_TRANSACTION_COMMIT_ART_ID, commitArt.getId().toString(),
               transaction.getIdString());
         }
      };
   }
}
