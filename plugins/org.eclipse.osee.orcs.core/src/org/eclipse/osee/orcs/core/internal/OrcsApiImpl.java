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
package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsPerformance;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactBuilderFactoryImpl;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.indexer.IndexerModule;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationGraphImpl;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.core.internal.session.SessionContextImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TransactionFactoryImpl;
import org.eclipse.osee.orcs.core.internal.transaction.handler.TxDataHandlerFactoryImpl;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.search.QueryFacade;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsApiImpl implements OrcsApi {

   private Log logger;
   private OrcsDataStore dataStore;
   private AttributeClassResolver resolver;
   private IOseeCachingService cacheService;
   private DataStoreTypeCache dataStoreTypeCache;

   private ExecutorAdmin executorAdmin;
   private SystemPreferences preferences;

   private ArtifactProxyFactory proxyFactory;
   private OrcsObjectLoader objectLoader;
   private QueryModule queryModule;
   private IndexerModule indexerModule;
   private TxDataHandlerFactoryImpl txUpdateFactory;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsDataStore(OrcsDataStore dataStore) {
      this.dataStore = dataStore;
   }

   public void setAttributeClassResolver(AttributeClassResolver resolver) {
      this.resolver = resolver;
   }

   public void setCacheService(IOseeCachingService cacheService) {
      this.cacheService = cacheService;
   }

   public void setDataStoreTypeCache(DataStoreTypeCache dataStoreTypeCache) {
      this.dataStoreTypeCache = dataStoreTypeCache;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void start() {
      RelationFactory relationFactory = new RelationFactory(dataStoreTypeCache.getRelationTypeCache());

      AttributeFactory attributeFactory =
         new AttributeFactory(resolver, dataStoreTypeCache.getAttributeTypeCache(), dataStore.getDataFactory());

      ArtifactFactory artifactFactory =
         new ArtifactFactory(dataStore.getDataFactory(), attributeFactory, relationFactory,
            cacheService.getArtifactTypeCache(), cacheService.getBranchCache());

      proxyFactory = new ArtifactProxyFactory(artifactFactory);

      txUpdateFactory = new TxDataHandlerFactoryImpl(dataStore.getDataFactory(), proxyFactory);

      ArtifactBuilderFactory builderFactory =
         new ArtifactBuilderFactoryImpl(logger, proxyFactory, artifactFactory, attributeFactory);

      objectLoader = new OrcsObjectLoader(logger, dataStore.getDataLoader(), builderFactory);

      queryModule =
         new QueryModule(logger, dataStore.getQueryEngine(), objectLoader, dataStoreTypeCache.getAttributeTypeCache());

      indexerModule = new IndexerModule(logger, preferences, executorAdmin, dataStore.getQueryEngineIndexer());
      indexerModule.start();
   }

   public void stop() {
      if (indexerModule != null) {
         indexerModule.stop();
      }
      queryModule = null;
      objectLoader = null;
      txUpdateFactory = null;
      proxyFactory = null;
   }

   @Override
   public QueryFactory getQueryFactory(ApplicationContext context) {
      SessionContext sessionContext = getSessionContext(context);
      return queryModule.createQueryFactory(sessionContext);
   }

   @Override
   public TransactionCache getTxsCache() {
      return cacheService.getTransactionCache();
   }

   @Override
   public BranchCache getBranchCache() {
      return cacheService.getBranchCache();
   }

   @Override
   public QueryFacade getQueryFacade(ApplicationContext context) {
      throw new UnsupportedOperationException();
   }

   @Override
   public GraphReadable getGraph(ApplicationContext context) {
      SessionContext sessionContext = getSessionContext(context);
      return new RelationGraphImpl(sessionContext, objectLoader, dataStoreTypeCache.getArtifactTypeCache(),
         dataStoreTypeCache.getRelationTypeCache());
   }

   @Override
   public OrcsBranch getBranchOps(ApplicationContext context) {
      SessionContext sessionContext = getSessionContext(context);
      return new OrcsBranchImpl(logger, sessionContext, dataStore.getBranchDataStore(), cacheService.getBranchCache(),
         cacheService.getTransactionCache());
   }

   @Override
   public TransactionFactory getTransactionFactory(ApplicationContext context) {
      SessionContext sessionContext = getSessionContext(context);
      return new TransactionFactoryImpl(logger, sessionContext, dataStore.getBranchDataStore(), proxyFactory,
         txUpdateFactory);
   }

   @Override
   public OrcsAdmin getAdminOps(ApplicationContext context) {
      SessionContext sessionContext = getSessionContext(context);
      return new OrcsAdminImpl(logger, sessionContext, dataStore.getDataStoreAdmin());
   }

   @Override
   public OrcsPerformance getOrcsPerformance(ApplicationContext context) {
      SessionContext sessionContext = getSessionContext(context);
      return new OrcsPerformanceImpl(logger, sessionContext, queryModule, indexerModule);
   }

   @Override
   public QueryIndexer getQueryIndexer(ApplicationContext context) {
      SessionContext sessionContext = getSessionContext(context);
      return indexerModule.createQueryIndexer(sessionContext);
   }

   private SessionContext getSessionContext(ApplicationContext context) {
      // TODO get sessions from a session context cache
      String sessionId = GUID.create(); // TODO context.getSessionId() attach to application context
      return new SessionContextImpl(sessionId);
   }

}
