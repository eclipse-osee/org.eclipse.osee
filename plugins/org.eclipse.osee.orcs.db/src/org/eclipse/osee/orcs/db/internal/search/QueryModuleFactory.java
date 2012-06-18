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

import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
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
public class QueryModuleFactory {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final ExecutorAdmin executorAdmin;

   public QueryModuleFactory(Log logger, IOseeDatabaseService dbService, IdentityService identityService, ExecutorAdmin executorAdmin) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.executorAdmin = executorAdmin;
   }

   public TaggingEngine createTaggingEngine(AttributeTypeCache attributeTypeCache) {
      TagProcessor tagProcessor = new TagProcessor(new EnglishLanguage(logger), new TagEncoder());
      return new TaggingEngine(tagProcessor, attributeTypeCache);
   }

   public QueryEngine createQueryEngine(TaggingEngine taggingEngine, SqlProvider sqlProvider, DataStoreTypeCache cache, BranchCache branchCache) {
      SqlHandlerFactory handlerFactory =
         new SqlHandlerFactoryImpl(logger, executorAdmin, identityService, taggingEngine, cache);
      SqlBuilder sqlBuilder = new SqlBuilder(sqlProvider, dbService);
      return new QueryEngineImpl(logger, branchCache, handlerFactory, sqlBuilder);
   }

   public QueryEngineIndexer createQueryEngineIndexer(TaggingEngine taggingEngine, IResourceManager resourceManager, AttributeTypeCache attributeTypeCache) {
      QueueToAttributeLoader attributeLoader =
         new QueueToAttributeLoaderImpl(logger, dbService, attributeTypeCache, resourceManager);
      IndexerCallableFactory callableFactory =
         new IndexerCallableFactoryImpl(logger, dbService, taggingEngine, attributeLoader);
      IndexingTaskConsumer indexConsumer = new IndexingTaskConsumerImpl(executorAdmin, callableFactory);
      return new QueryEngineIndexerImpl(logger, dbService, attributeTypeCache, indexConsumer);
   }
}
