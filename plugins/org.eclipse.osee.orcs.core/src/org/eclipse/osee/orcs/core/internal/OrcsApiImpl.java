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
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory.BranchProviderFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl.BranchProvider;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassRegistry;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilderFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.graph.GraphFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.graph.impl.GraphFactoryImpl;
import org.eclipse.osee.orcs.core.internal.indexer.IndexerModule;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.proxy.impl.ExternalArtifactManagerImpl;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationManagerFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationNodeLoader;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeLoaderImpl;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptCompiler;
import org.eclipse.osee.orcs.core.internal.script.ScriptEngines;
import org.eclipse.osee.orcs.core.internal.script.impl.OrcsScriptCompilerImpl;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.core.internal.session.OrcsSessionImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TransactionFactoryImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TxCallableFactory;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataLoaderImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataLoaderImpl.TransactionProvider;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;
import org.eclipse.osee.orcs.core.internal.types.BranchHierarchyProvider;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesModule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
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
   private ExecutorAdmin executorAdmin;
   private SystemPreferences preferences;

   private QueryModule queryModule;
   private IndexerModule indexerModule;
   private OrcsTypesModule typesModule;
   private OrcsSession systemSession;
   private DataModule module;

   private TxDataManager txDataManager;
   private TxCallableFactory txCallableFactory;
   private ScriptEngineManager manager;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsDataStore(OrcsDataStore dataStore) {
      this.dataStore = dataStore;
   }

   public void setAttributeClassRegistry(AttributeClassRegistry registry) {
      this.registry = registry;
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

         private final ThreadLocal<Iterable<? extends IOseeBranch>> cache =
            new ThreadLocal<Iterable<? extends IOseeBranch>>();

         @Override
         public Iterable<? extends IOseeBranch> getParentHierarchy(IOseeBranch branch) throws OseeCoreException {
            Iterable<? extends IOseeBranch> toReturn = cache.get();
            if (toReturn == null) {
               Set<IOseeBranch> branches = Sets.newLinkedHashSet();
               BranchQuery branchQuery = getQueryFactory(null).branchQuery();
               branchQuery.andIsAncestorOf(branch);
               branches.add(branch);

               for (IOseeBranch parent : branchQuery.getResults()) {
                  if (!branches.add(parent)) {
                     logger.error("Cycle detected with branch: [%s]", parent);
                     return Collections.emptyList();
                  }
               }
               cache.set(branches);
               toReturn = branches;
            }
            return toReturn;
         }
      };

      typesModule = new OrcsTypesModule(logger, dataStore.getTypesDataStore(), hierarchyProvider);
      typesModule.start(getSystemSession());

      OrcsTypes orcsTypes = typesModule.createOrcsTypes(getSystemSession());

      module = dataStore.createDataModule(orcsTypes.getArtifactTypes(), orcsTypes.getAttributeTypes());

      AttributeClassResolver resolver = new AttributeClassResolver(registry, orcsTypes.getAttributeTypes());
      AttributeFactory attributeFactory =
         new AttributeFactory(resolver, module.getDataFactory(), orcsTypes.getAttributeTypes());

      ArtifactFactory artifactFactory =
         new ArtifactFactory(module.getDataFactory(), attributeFactory, orcsTypes.getArtifactTypes(),
            new BranchProviderFactoryImpl());

      RelationFactory relationFactory = new RelationFactory(orcsTypes.getRelationTypes(), module.getDataFactory());

      final GraphFactory graphFactory = new GraphFactoryImpl();
      GraphBuilderFactory graphBuilderFactory =
         new GraphBuilderFactory(logger, artifactFactory, attributeFactory, relationFactory);

      RelationNodeLoader nodeLoader = new RelationNodeLoaderImpl(module.getDataLoaderFactory(), graphBuilderFactory);
      RelationManager relationManager =
         RelationManagerFactory.createRelationManager(logger, orcsTypes.getRelationTypes(), relationFactory, nodeLoader);

      GraphProvider graphProvider = new GraphProvider() {

         @Override
         public GraphData getGraph(OrcsSession session, IOseeBranch branch, int transactionId) throws OseeCoreException {
            return graphFactory.createGraph(session, branch, transactionId);
         }
      };

      ExternalArtifactManager proxyManager = new ExternalArtifactManagerImpl(relationManager);

      TransactionProvider txProvider = new TransactionProvider() {

         @Override
         public int getHeadTransaction(OrcsSession session, IOseeBranch branch) {
            QueryFactory queryFactory = queryModule.createQueryFactory(session);
            return queryFactory.transactionQuery().andIsHead(branch).getResultsAsIds().getExactlyOne();
         }
      };

      TxDataLoader txDataLoader =
         new TxDataLoaderImpl(module.getDataLoaderFactory(), graphFactory, graphBuilderFactory, graphProvider,
            txProvider);
      txDataManager = new TxDataManager(proxyManager, artifactFactory, relationManager, txDataLoader);
      txCallableFactory = new TxCallableFactory(logger, module.getTxDataStore(), txDataManager);

      queryModule =
         new QueryModule(logger, module.getQueryEngine(), graphBuilderFactory, graphProvider,
            orcsTypes.getArtifactTypes(), orcsTypes.getAttributeTypes(), proxyManager);

      indexerModule = new IndexerModule(logger, preferences, executorAdmin, dataStore.getQueryEngineIndexer());
      indexerModule.start(getSystemSession(), orcsTypes.getAttributeTypes());

      OrcsScriptCompiler compiler = new OrcsScriptCompilerImpl(getSystemSession(), module, orcsTypes);
      manager = ScriptEngines.newScriptEngineManager(compiler);
   }

   public void stop() {
      if (indexerModule != null) {
         indexerModule.stop();
      }
      queryModule = null;
      txDataManager = null;
      txCallableFactory = null;
      module = null;
      if (typesModule != null) {
         typesModule.stop();
      }
      systemSession = null;
   }

   @Override
   public QueryFactory getQueryFactory(ApplicationContext context) {
      OrcsSession session = getSession(context);
      return queryModule.createQueryFactory(session);
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
      QueryFactory queryFactory = getQueryFactory(context);
      return new OrcsBranchImpl(logger, session, module.getBranchDataStore(), queryFactory, systemUser,
         getOrcsTypes(context));
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

   private class BranchProviderFactoryImpl implements BranchProviderFactory {

      @Override
      public BranchProvider createBranchProvider(final OrcsSession session) {
         BranchProvider provider = new BranchProvider() {

            BranchReadable cached = null;

            @Override
            public BranchReadable getBranch(Long branchUuid) {
               if (cached == null || !cached.getUuid().equals(branchUuid)) {
                  BranchQuery query = queryModule.createQueryFactory(session).branchQuery();
                  cached = query.andUuids(branchUuid).getResults().getExactlyOne();
               }
               return cached;
            }

         };
         return provider;
      }

   }

   @Override
   public ScriptEngine getScriptEngine() {
      return manager.getEngineByName(ScriptEngines.ORCS_SCRIPT_ENGINE_ID);
   }

}
