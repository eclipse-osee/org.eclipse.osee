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
package org.eclipse.osee.orcs.db.internal.search;

import java.util.List;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.search.SqlBuilder.QueryType;
import org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerCallableFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumer;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumerImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.QueryEngineIndexerImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.QueueToAttributeLoader;
import org.eclipse.osee.orcs.db.internal.search.indexer.data.QueueToAttributeLoaderImpl;
import org.eclipse.osee.orcs.db.internal.search.language.EnglishLanguage;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagEncoder;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {

   private SqlHandlerFactory handlerFactory;
   private SqlBuilder builder;
   private QueryEngineIndexerImpl queryIndexer;

   private SqlProvider sqlProvider;
   private IOseeDatabaseService dbService;
   private IdentityService identityService;
   private IOseeCachingService cacheService;
   private DataStoreTypeCache cache;
   private ExecutorAdmin executorAdmin;
   private Log logger;
   private IResourceManager resourceManager;
   private IResourceLocatorManager locatorService;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setSqlProvider(SqlProvider sqlProvider) {
      this.sqlProvider = sqlProvider;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   //TODO fix these two services
   public void setCachingService(IOseeCachingService cacheService) {
      this.cacheService = cacheService;
   }

   //TODO other
   public void setDataStoreTypeCache(DataStoreTypeCache cache) {
      this.cache = cache;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setResourceManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void setLocatorService(IResourceLocatorManager locatorService) {
      this.locatorService = locatorService;
   }

   public void start() {
      TagProcessor tagProcessor = new TagProcessor(new EnglishLanguage(logger), new TagEncoder());
      TaggingEngine taggingEngine = new TaggingEngine(tagProcessor, cache.getAttributeTypeCache());

      QueueToAttributeLoader attributeLoader =
         new QueueToAttributeLoaderImpl(logger, dbService, cache.getAttributeTypeCache(), resourceManager,
            locatorService);
      IndexerCallableFactory callableFactory =
         new IndexerCallableFactoryImpl(logger, dbService, taggingEngine, attributeLoader);

      IndexingTaskConsumer indexConsumer = new IndexingTaskConsumerImpl(executorAdmin, callableFactory);

      queryIndexer = new QueryEngineIndexerImpl(logger, dbService, cache.getAttributeTypeCache(), indexConsumer);
      handlerFactory = new SqlHandlerFactoryImpl(logger, executorAdmin, identityService, taggingEngine, cache);
      builder = new SqlBuilder(sqlProvider, dbService);
   }

   public void stop() {
      queryIndexer = null;
      handlerFactory = null;
      builder = null;
   }

   public SqlContext createContext(String sessionId, QueryOptions options) {
      return new SqlContext(sessionId, options);
   }

   @Override
   public QueryContext createCount(String sessionId, QueryData queryData) throws OseeCoreException {
      return createQuery(sessionId, queryData, QueryType.COUNT_ARTIFACTS);
   }

   @Override
   public QueryContext create(String sessionId, QueryData queryData) throws OseeCoreException {
      return createQuery(sessionId, queryData, QueryType.SELECT_ARTIFACTS);
   }

   private SqlContext createQuery(String sessionId, QueryData queryData, QueryType queryType) throws OseeCoreException {
      IOseeBranch branch = queryData.getCriteriaSet().getBranch();
      int branchId = cacheService.getBranchCache().getLocalId(branch);

      List<SqlHandler> handlers = handlerFactory.createHandlers(queryData.getCriteriaSet());
      SqlContext context = createContext(sessionId, queryData.getOptions());
      builder.generateSql(context, branchId, handlers, queryType);

      if (logger.isTraceEnabled()) {
         logger.trace("SessionId:[%s] Query:[%s] Parameters:[%s]", sessionId, context.getSql(), context.getParameters());
      }

      return context;
   }

   @Override
   public QueryEngineIndexer getQueryIndexer() {
      return queryIndexer;
   }

}
