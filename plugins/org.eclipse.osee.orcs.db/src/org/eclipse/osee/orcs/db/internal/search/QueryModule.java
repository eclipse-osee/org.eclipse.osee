/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.search;

import static org.eclipse.osee.orcs.db.internal.search.Engines.newArtifactQueryEngine;
import static org.eclipse.osee.orcs.db.internal.search.Engines.newIndexingEngine;
import static org.eclipse.osee.orcs.db.internal.search.Engines.newTaggingEngine;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.KeyValueStore;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.search.engines.ArtifactQuerySqlContextFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.engines.ObjectQueryCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.engines.QueryEngineImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerConstants;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class QueryModule {

   private final Log logger;
   private final ExecutorAdmin executorAdmin;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory sqlJoinFactory;

   private TaggingEngine taggingEngine;
   private QueryEngineIndexer queryIndexer;

   public QueryModule(Log logger, ExecutorAdmin executorAdmin, JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory) {
      this.logger = logger;
      this.executorAdmin = executorAdmin;
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
   }

   public void startIndexer(IResourceManager resourceManager) throws Exception {
      taggingEngine = newTaggingEngine(logger);
      queryIndexer =
         newIndexingEngine(logger, jdbcClient, sqlJoinFactory, taggingEngine, executorAdmin, resourceManager);

      executorAdmin.createFixedPoolExecutor(IndexerConstants.INDEXING_CONSUMER_EXECUTOR_ID, 4);
   }

   public void stopIndexer() throws Exception {
      queryIndexer = null;
      taggingEngine = null;
      executorAdmin.shutdown(IndexerConstants.INDEXING_CONSUMER_EXECUTOR_ID);
   }

   public QueryEngineIndexer getQueryIndexer() {
      return queryIndexer;
   }

   public QueryEngine createQueryEngine(DataLoaderFactory loaderFactory, OrcsTokenService tokenService, SqlObjectLoader sqlObjectLoader, KeyValueStore keyValue, IResourceManager resourceManager) {
      ArtifactQuerySqlContextFactoryImpl artifactSqlContextFactory =
         Engines.createArtifactSqlContext(logger, sqlJoinFactory, jdbcClient, taggingEngine);
      ObjectQueryCallableFactory factory1 =
         newArtifactQueryEngine(artifactSqlContextFactory, logger, taggingEngine, executorAdmin, loaderFactory);
      QuerySqlContextFactory branchSqlContextFactory =
         Engines.newBranchSqlContextFactory(logger, sqlJoinFactory, jdbcClient);
      QuerySqlContextFactory txSqlContextFactory = Engines.newTxSqlContextFactory(logger, sqlJoinFactory, jdbcClient);

      return new QueryEngineImpl(factory1, branchSqlContextFactory, txSqlContextFactory, jdbcClient, sqlJoinFactory,
         artifactSqlContextFactory.getHandlerFactory(), sqlObjectLoader, tokenService, keyValue, resourceManager);
   }
}