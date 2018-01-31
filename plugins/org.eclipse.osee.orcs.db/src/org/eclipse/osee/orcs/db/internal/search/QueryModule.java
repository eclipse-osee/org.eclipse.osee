/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search;

import static org.eclipse.osee.orcs.db.internal.search.Engines.newArtifactQueryEngine;
import static org.eclipse.osee.orcs.db.internal.search.Engines.newIndexingEngine;
import static org.eclipse.osee.orcs.db.internal.search.Engines.newQueryEngine;
import static org.eclipse.osee.orcs.db.internal.search.Engines.newTaggingEngine;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
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
   private final IdentityLocator idService;
   private final SqlJoinFactory sqlJoinFactory;

   private TaggingEngine taggingEngine;
   private QueryEngineIndexer queryIndexer;

   public QueryModule(Log logger, ExecutorAdmin executorAdmin, JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory, IdentityLocator idService) {
      this.logger = logger;
      this.executorAdmin = executorAdmin;
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
      this.idService = idService;
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

   public QueryEngine createQueryEngine(DataLoaderFactory loaderFactory, AttributeTypes attrTypes, SqlObjectLoader sqlObjectLoader) {
      QuerySqlContextFactory artifactSqlContextFactory =
         Engines.createArtifactSqlContext(logger, sqlJoinFactory, idService, jdbcClient, taggingEngine);
      QueryCallableFactory factory1 = newArtifactQueryEngine(artifactSqlContextFactory, logger, taggingEngine,
         executorAdmin, loaderFactory, attrTypes);
      QuerySqlContextFactory branchSqlContextFactory =
         Engines.newBranchSqlContextFactory(logger, sqlJoinFactory, idService, jdbcClient);
      QuerySqlContextFactory txSqlContextFactory =
         Engines.newTxSqlContextFactory(logger, sqlJoinFactory, idService, jdbcClient);

      QueryCallableFactory factory4 = newQueryEngine(logger, sqlJoinFactory, idService, jdbcClient, taggingEngine,
         executorAdmin, loaderFactory, attrTypes);
      return new QueryEngineImpl(factory1, branchSqlContextFactory, txSqlContextFactory, factory4, jdbcClient,
         sqlJoinFactory, artifactSqlContextFactory, sqlObjectLoader);
   }
}