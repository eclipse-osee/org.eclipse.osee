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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.DataPostProcessorFactory;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllArtifacts;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.search.handlers.AllArtifactsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.ArtifactGuidSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.ArtifactHridsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.ArtifactIdsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.ArtifactTypeSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.AttributeOtherSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.AttributeTokenSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.AttributeTypeExistsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.RelatedToSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.RelationTypeExistsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexerCallableFactoryImpl;
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
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public class QueryModuleFactory {

   private final Log logger;
   private QueryEngine queryEngine;
   private QueryEngineIndexer queryIndexer;

   public QueryModuleFactory(Log logger) {
      super();
      this.logger = logger;
   }

   public void start(ExecutorAdmin executorAdmin, IOseeDatabaseService dbService, IdentityService idService, SqlProvider sqlProvider, IResourceManager resourceManager, BranchCache branchCache) {
      TaggingEngine taggingEngine = createTaggingEngine();
      DataPostProcessorFactory<CriteriaAttributeKeywords> postProcessor =
         createAttributeKeywordPostProcessor(executorAdmin, taggingEngine);
      SqlHandlerFactory handlerFactory =
         createHandlerFactory(idService, postProcessor, taggingEngine.getTagProcessor());

      queryEngine = createQueryEngine(dbService, handlerFactory, sqlProvider, branchCache);
      queryIndexer = createQueryEngineIndexer(dbService, idService, executorAdmin, taggingEngine, resourceManager);
   }

   public void stop() {
      queryIndexer = null;
      queryEngine = null;
   }

   public QueryEngine getQueryEngine() {
      return queryEngine;
   }

   public QueryEngineIndexer getQueryIndexer() {
      return queryIndexer;
   }

   public TaggingEngine createTaggingEngine() {
      TagProcessor tagProcessor = new TagProcessor(new EnglishLanguage(logger), new TagEncoder());
      Map<String, Tagger> taggers = new HashMap<String, Tagger>();

      StreamMatcher matcher = MatcherFactory.createMatcher();
      taggers.put("DefaultAttributeTaggerProvider", new DefaultAttributeTagger(tagProcessor, matcher));
      taggers.put("XmlAttributeTaggerProvider", new XmlAttributeTagger(tagProcessor, matcher));

      return new TaggingEngine(taggers, tagProcessor);
   }

   protected QueryEngineImpl createQueryEngine(IOseeDatabaseService dbService, SqlHandlerFactory handlerFactory, SqlProvider sqlProvider, BranchCache branchCache) {
      return new QueryEngineImpl(logger, dbService, sqlProvider, branchCache, handlerFactory);
   }

   protected QueryEngineIndexer createQueryEngineIndexer(IOseeDatabaseService dbService, IdentityService identityService, ExecutorAdmin executorAdmin, TaggingEngine taggingEngine, IResourceManager resourceManager) {
      QueueToAttributeLoader attributeLoader =
         new QueueToAttributeLoaderImpl(logger, dbService, identityService, resourceManager);
      IndexerCallableFactory callableFactory =
         new IndexerCallableFactoryImpl(logger, dbService, taggingEngine, attributeLoader);
      IndexingTaskConsumer indexConsumer = new IndexingTaskConsumerImpl(executorAdmin, callableFactory);
      return new QueryEngineIndexerImpl(logger, dbService, identityService, indexConsumer);
   }

   protected SqlHandlerFactory createHandlerFactory(IdentityService identityService, DataPostProcessorFactory<CriteriaAttributeKeywords> postProcessorFactory, TagProcessor tagProcessor) {
      Map<Class<? extends Criteria<?>>, Class<? extends SqlHandler<?, ?>>> handleMap =
         new HashMap<Class<? extends Criteria<?>>, Class<? extends SqlHandler<?, ?>>>();

      Map<Class<? extends SqlHandler<?, ?>>, DataPostProcessorFactory<?>> factoryMap =
         new HashMap<Class<? extends SqlHandler<?, ?>>, DataPostProcessorFactory<?>>();

      // Query
      handleMap.put(CriteriaArtifactGuids.class, ArtifactGuidSqlHandler.class);
      handleMap.put(CriteriaArtifactHrids.class, ArtifactHridsSqlHandler.class);
      handleMap.put(CriteriaArtifactIds.class, ArtifactIdsSqlHandler.class);
      handleMap.put(CriteriaArtifactType.class, ArtifactTypeSqlHandler.class);
      handleMap.put(CriteriaRelatedTo.class, RelatedToSqlHandler.class);
      handleMap.put(CriteriaRelationTypeExists.class, RelationTypeExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeTypeExists.class, AttributeTypeExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeOther.class, AttributeOtherSqlHandler.class);
      handleMap.put(CriteriaAttributeKeywords.class, AttributeTokenSqlHandler.class);
      handleMap.put(CriteriaAllArtifacts.class, AllArtifactsSqlHandler.class);

      factoryMap.put(AttributeTokenSqlHandler.class, postProcessorFactory);

      return new SqlHandlerFactoryImpl(logger, identityService, tagProcessor, handleMap, factoryMap);
   }

   protected DataPostProcessorFactory<CriteriaAttributeKeywords> createAttributeKeywordPostProcessor(ExecutorAdmin executorAdmin, TaggingEngine taggingEngine) {
      return new DataPostProcessorFactoryImpl(logger, taggingEngine, executorAdmin);
   }
}
