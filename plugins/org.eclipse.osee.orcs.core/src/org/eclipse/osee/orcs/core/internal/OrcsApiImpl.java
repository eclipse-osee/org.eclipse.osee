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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.LazyObject;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsPerformance;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassRegistry;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.indexer.IndexerModule;
import org.eclipse.osee.orcs.core.internal.loader.ArtifactBuilderFactoryImpl;
import org.eclipse.osee.orcs.core.internal.loader.ArtifactLoaderFactoryImpl;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.proxy.impl.ExternalArtifactManagerImpl;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationGraphImpl;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.core.internal.session.OrcsSessionImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TransactionFactoryImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TxCallableFactory;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataLoaderImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;
import org.eclipse.osee.orcs.core.internal.types.BranchHierarchyProvider;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesModule;
import org.eclipse.osee.orcs.core.internal.util.ValueProviderFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import com.google.common.collect.Sets;

/**
 * @author Roberto E. Escobar
 */
public class OrcsApiImpl implements OrcsApi {

   private Log logger;
   private OrcsDataStore dataStore;
   private AttributeClassRegistry registry;
   private TempCachingService cacheService;

   private ExecutorAdmin executorAdmin;
   private SystemPreferences preferences;

   private ExternalArtifactManager proxyManager;
   private ArtifactLoaderFactory loaderFactory;
   private QueryModule queryModule;
   private IndexerModule indexerModule;
   private OrcsTypesModule typesModule;
   private OrcsSession systemSession;
   private DataModule module;

   private TxDataManager txDataManager;
   private TxCallableFactory txCallableFactory;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsDataStore(OrcsDataStore dataStore) {
      this.dataStore = dataStore;
   }

   public void setAttributeClassRegistry(AttributeClassRegistry registry) {
      this.registry = registry;
   }

   public void setCacheService(TempCachingService cacheService) {
      this.cacheService = cacheService;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void start() {
      systemSession = createSession();

      BranchHierarchyProvider hierarchyProvider = new BranchHierarchyProvider() {

         @Override
         public Iterable<? extends IOseeBranch> getParentHierarchy(IOseeBranch branch) throws OseeCoreException {
            Set<IOseeBranch> branches = Sets.newLinkedHashSet();
            BranchCache branchCache = cacheService.getBranchCache();

            Branch branchPtr = branchCache.get(branch);
            while (branchPtr != null) {
               if (!branches.add(branchPtr)) {
                  logger.error("Cycle detected with branch: [%s]", branchPtr);
                  return Collections.emptyList();
               }
               branchPtr = branchPtr.getParentBranch();
            }

            return branches;
         }
      };

      typesModule = new OrcsTypesModule(logger, dataStore.getTypesDataStore(), hierarchyProvider);
      typesModule.start(getSystemSession());

      OrcsTypes orcsTypes = typesModule.createOrcsTypes(getSystemSession());

      module = dataStore.createDataModule(orcsTypes.getArtifactTypes(), orcsTypes.getAttributeTypes());

      AttributeClassResolver resolver = new AttributeClassResolver(registry, orcsTypes.getAttributeTypes());
      AttributeFactory attributeFactory =
         new AttributeFactory(resolver, module.getDataFactory(), orcsTypes.getAttributeTypes());

      ValueProviderFactory providerFactory = new ValueProviderFactory(cacheService.getBranchCache());

      RelationFactory relationFactory =
         new RelationFactory(orcsTypes.getRelationTypes(), module.getDataFactory(), providerFactory);

      ArtifactFactory artifactFactory =
         new ArtifactFactory(module.getDataFactory(), attributeFactory, relationFactory, orcsTypes.getArtifactTypes(),
            providerFactory);

      proxyManager = new ExternalArtifactManagerImpl();

      ArtifactBuilderFactory builderFactory =
         new ArtifactBuilderFactoryImpl(logger, proxyManager, artifactFactory, attributeFactory, relationFactory);

      loaderFactory = new ArtifactLoaderFactoryImpl(module.getDataLoaderFactory(), builderFactory);

      TxDataLoader txDataLoader = new TxDataLoaderImpl();
      txDataManager = new TxDataManager(proxyManager, artifactFactory, txDataLoader);
      txCallableFactory = new TxCallableFactory(logger, module.getTxDataStore(), txDataManager);

      queryModule =
         new QueryModule(logger, module.getQueryEngine(), builderFactory, orcsTypes.getArtifactTypes(),
            orcsTypes.getAttributeTypes());

      indexerModule = new IndexerModule(logger, preferences, executorAdmin, dataStore.getQueryEngineIndexer());
      indexerModule.start(getSystemSession(), orcsTypes.getAttributeTypes());
   }

   public void stop() {
      if (indexerModule != null) {
         indexerModule.stop();
      }
      queryModule = null;
      loaderFactory = null;
      proxyManager = null;
      systemSession = null;
   }

   @Override
   public QueryFactory getQueryFactory(ApplicationContext context) {
      OrcsSession session = getSession(context);
      return queryModule.createQueryFactory(session);
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
   public GraphReadable getGraph(ApplicationContext context) {
      OrcsSession session = getSession(context);
      return new RelationGraphImpl(session, loaderFactory, getOrcsTypes(context).getRelationTypes(), proxyManager);
   }

   @Override
   public OrcsBranch getBranchOps(final ApplicationContext context) {
      OrcsSession session = getSession(context);
      LazyObject<ArtifactReadable> systemUser = new LazyObject<ArtifactReadable>() {

         @Override
         protected final FutureTask<ArtifactReadable> createLoaderTask() {
            Callable<ArtifactReadable> callable = new Callable<ArtifactReadable>() {
               @Override
               public ArtifactReadable call() throws Exception {
                  return getQueryFactory(context).fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();

               }
            };
            return new FutureTask<ArtifactReadable>(callable);
         }
      };
      return new OrcsBranchImpl(logger, session, module.getBranchDataStore(), cacheService.getBranchCache(),
         cacheService.getTransactionCache(), systemUser, getOrcsTypes(context));
   }

   @Override
   public TransactionFactory getTransactionFactory(ApplicationContext context) {
      OrcsSession session = getSession(context);
      return new TransactionFactoryImpl(session, txDataManager, txCallableFactory);
   }

   @Override
   public OrcsAdmin getAdminOps(ApplicationContext context) {
      OrcsSession session = getSession(context);
      return new OrcsAdminImpl(logger, session, module.getDataStoreAdmin());
   }

   @Override
   public OrcsPerformance getOrcsPerformance(ApplicationContext context) {
      OrcsSession session = getSession(context);
      return new OrcsPerformanceImpl(logger, session, queryModule, indexerModule);
   }

   @Override
   public QueryIndexer getQueryIndexer(ApplicationContext context) {
      OrcsSession session = getSession(context);
      return indexerModule.createQueryIndexer(session, getOrcsTypes(context).getAttributeTypes());
   }

   private OrcsSession getSystemSession() {
      return systemSession;
   }

   private OrcsSession getSession(ApplicationContext context) {
      // TODO get sessions from a session context cache - improve this
      String sessionId = null;
      if (context != null) {
         sessionId = context.getSessionId();
      }
      if (!Strings.isValid(sessionId)) {
         sessionId = GUID.create();
      }
      return new OrcsSessionImpl(sessionId);
   }

   @Override
   public OrcsTypes getOrcsTypes(ApplicationContext context) {
      OrcsSession session = getSession(context);
      return typesModule.createOrcsTypes(session);
   }

   private OrcsSession createSession() {
      String sessionId = GUID.create();
      return new OrcsSessionImpl(sessionId);
   }
}
