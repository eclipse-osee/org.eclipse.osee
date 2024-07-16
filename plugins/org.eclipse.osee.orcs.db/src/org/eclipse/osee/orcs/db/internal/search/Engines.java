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

package org.eclipse.osee.orcs.db.internal.search;

import static org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryUtil.createArtifactSqlHandlerFactory;
import static org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryUtil.createBranchSqlHandlerFactory;
import static org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryUtil.createObjectSqlHandlerFactory;
import static org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryUtil.createTxSqlHandlerFactory;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.SqlTable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext.ObjectQueryType;
import org.eclipse.osee.orcs.db.internal.search.engines.ArtifactQuerySqlContextFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.engines.ObjectQueryCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.engines.ObjectQuerySqlContextFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.engines.QueryFilterFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.engines.QuerySqlContextFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexedResourceLoader;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerCallableFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumer;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumerImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.QueryEngineIndexerImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.data.GammaQueueIndexerDataSourceLoader;
import org.eclipse.osee.orcs.db.internal.search.language.EnglishLanguage;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagEncoder;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.db.internal.search.util.AttributeDataMatcher;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public final class Engines {

   private Engines() {
      //
   }

   public static ArtifactQuerySqlContextFactoryImpl createArtifactSqlContext(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient, TaggingEngine taggingEngine) {
      SqlHandlerFactory handlerFactory = createArtifactSqlHandlerFactory(taggingEngine.getTagProcessor());
      return new ArtifactQuerySqlContextFactoryImpl(joinFactory, jdbcClient, handlerFactory);
   }

   public static ObjectQueryCallableFactory newArtifactQueryEngine(QuerySqlContextFactory sqlContextFactory, Log logger, TaggingEngine taggingEngine, ExecutorAdmin executorAdmin, DataLoaderFactory objectLoader) {
      AttributeDataMatcher matcher = new AttributeDataMatcher(logger, taggingEngine);
      QueryFilterFactoryImpl filterFactory = new QueryFilterFactoryImpl(logger, executorAdmin, matcher);
      return new ObjectQueryCallableFactory(logger, objectLoader, sqlContextFactory, filterFactory);
   }

   public static QueryCallableFactory newQueryEngine(Log logger, SqlJoinFactory joinFactory, //
      JdbcClient jdbcClient, TaggingEngine taggingEngine, ExecutorAdmin executorAdmin, //
      DataLoaderFactory objectLoader) {

      SqlHandlerFactory handlerFactory = createObjectSqlHandlerFactory(logger, taggingEngine.getTagProcessor());
      QuerySqlContextFactory sqlContextFactory =
         new ObjectQuerySqlContextFactoryImpl(joinFactory, jdbcClient, handlerFactory);
      AttributeDataMatcher matcher = new AttributeDataMatcher(logger, taggingEngine);
      QueryFilterFactoryImpl filterFactory = new QueryFilterFactoryImpl(logger, executorAdmin, matcher);
      return new ObjectQueryCallableFactory(logger, objectLoader, sqlContextFactory, filterFactory);
   }

   public static TaggingEngine newTaggingEngine(Log logger) {
      TagProcessor tagProcessor = new TagProcessor(new EnglishLanguage(logger), new TagEncoder());
      return new TaggingEngine(tagProcessor);
   }

   public static QuerySqlContextFactory newSqlContextFactory(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlTable table, String idColumn, SqlHandlerFactory handlerFactory, ObjectQueryType type) {
      return new QuerySqlContextFactoryImpl(logger, joinFactory, jdbcClient, handlerFactory, table, idColumn, type);
   }

   public static QuerySqlContextFactory newBranchSqlContextFactory(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient) {
      SqlHandlerFactory handlerFactory = createBranchSqlHandlerFactory(logger);
      return newSqlContextFactory(logger, joinFactory, jdbcClient, OseeDb.BRANCH_TABLE, "branch_id", handlerFactory,
         ObjectQueryType.BRANCH);
   }

   public static QuerySqlContextFactory newTxSqlContextFactory(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient) {
      SqlHandlerFactory handlerFactory = createTxSqlHandlerFactory(logger);
      return newSqlContextFactory(logger, joinFactory, jdbcClient, OseeDb.TX_DETAILS_TABLE, "transaction_id",
         handlerFactory, ObjectQueryType.TX);
   }

   public static QueryEngineIndexer newIndexingEngine(Log logger, JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory, TaggingEngine taggingEngine, ExecutorAdmin executorAdmin, IResourceManager resourceManager) {
      IndexedResourceLoader resourceLoader = new GammaQueueIndexerDataSourceLoader(logger, jdbcClient, resourceManager);
      IndexerCallableFactory callableFactory =
         new IndexerCallableFactoryImpl(logger, jdbcClient, taggingEngine, resourceLoader);
      IndexingTaskConsumer indexConsumer = new IndexingTaskConsumerImpl(executorAdmin, callableFactory);
      return new QueryEngineIndexerImpl(logger, jdbcClient, sqlJoinFactory, indexConsumer);
   }
}