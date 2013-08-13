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

import static org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryUtil.createArtifactSqlHandlerFactory;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataPostProcessorFactory;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerCallableFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerConstants;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumer;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumerImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.QueryEngineIndexerImpl;
import org.eclipse.osee.orcs.db.internal.search.indexer.QueueToAttributeLoader;
import org.eclipse.osee.orcs.db.internal.search.indexer.data.QueueToAttributeLoaderImpl;
import org.eclipse.osee.orcs.db.internal.search.language.EnglishLanguage;
import org.eclipse.osee.orcs.db.internal.search.tagger.DefaultAttributeTagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.StreamMatcher;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagEncoder;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.db.internal.search.tagger.XmlAttributeTagger;
import org.eclipse.osee.orcs.db.internal.search.util.DataPostProcessorFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.util.MatcherFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;

/**
 * @author Roberto E. Escobar
 */
public class QueryModule {

   private final Log logger;
   private final ExecutorAdmin executorAdmin;
   private final IOseeDatabaseService dbService;
   private final IdentityService idService;
   private final SqlProvider sqlProvider;

   private TaggingEngine taggingEngine;
   private QueryEngineIndexer queryIndexer;

   public QueryModule(Log logger, ExecutorAdmin executorAdmin, IOseeDatabaseService dbService, IdentityService idService, SqlProvider sqlProvider) {
      super();
      this.logger = logger;
      this.executorAdmin = executorAdmin;
      this.dbService = dbService;
      this.idService = idService;
      this.sqlProvider = sqlProvider;
   }

   public void startIndexer(IResourceManager resourceManager) throws Exception {
      taggingEngine = createTaggingEngine();
      executorAdmin.createFixedPoolExecutor(IndexerConstants.INDEXING_CONSUMER_EXECUTOR_ID, 4);
      queryIndexer = createQueryEngineIndexer(taggingEngine, resourceManager);
   }

   public void stopIndexer() throws Exception {
      queryIndexer = null;
      taggingEngine = null;
      executorAdmin.shutdown(IndexerConstants.INDEXING_CONSUMER_EXECUTOR_ID);
   }

   public QueryEngineIndexer getQueryIndexer() {
      return queryIndexer;
   }

   public QueryEngineImpl createQueryEngine(BranchCache branchCache) {
      DataPostProcessorFactory<CriteriaAttributeKeywords> postProcessor =
         createAttributeKeywordPostProcessor(taggingEngine);
      SqlHandlerFactory handlerFactory =
         createArtifactSqlHandlerFactory(logger, idService, taggingEngine.getTagProcessor(), postProcessor);
      return new QueryEngineImpl(logger, dbService, sqlProvider, branchCache, handlerFactory);
   }

   protected TaggingEngine createTaggingEngine() {
      TagProcessor tagProcessor = new TagProcessor(new EnglishLanguage(logger), new TagEncoder());
      Map<String, Tagger> taggers = new HashMap<String, Tagger>();

      StreamMatcher matcher = MatcherFactory.createMatcher();
      taggers.put("DefaultAttributeTaggerProvider", new DefaultAttributeTagger(tagProcessor, matcher));
      taggers.put("XmlAttributeTaggerProvider", new XmlAttributeTagger(tagProcessor, matcher));

      return new TaggingEngine(taggers, tagProcessor);
   }

   protected QueryEngineIndexer createQueryEngineIndexer(TaggingEngine taggingEngine, IResourceManager resourceManager) {
      QueueToAttributeLoader resourceLoader =
         new QueueToAttributeLoaderImpl(logger, dbService, idService, resourceManager);
      IndexerCallableFactory callableFactory =
         new IndexerCallableFactoryImpl(logger, dbService, taggingEngine, resourceLoader);
      IndexingTaskConsumer indexConsumer = new IndexingTaskConsumerImpl(executorAdmin, callableFactory);
      return new QueryEngineIndexerImpl(logger, dbService, idService, indexConsumer);
   }

   protected DataPostProcessorFactory<CriteriaAttributeKeywords> createAttributeKeywordPostProcessor(TaggingEngine taggingEngine) {
      return new DataPostProcessorFactoryImpl(logger, taggingEngine, executorAdmin);
   }

}
