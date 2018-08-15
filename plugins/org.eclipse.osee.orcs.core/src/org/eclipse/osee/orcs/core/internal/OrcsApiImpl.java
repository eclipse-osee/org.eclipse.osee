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

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsPerformance;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.internal.applicability.OrcsApplicabilityOps;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
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
import org.eclipse.osee.orcs.core.internal.search.QueryModule.QueryModuleProvider;
import org.eclipse.osee.orcs.core.internal.session.OrcsSessionImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TransactionFactoryImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TxCallableFactory;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataLoaderImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataLoaderImpl.TransactionProvider;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;
import org.eclipse.osee.orcs.core.internal.types.BranchHierarchyProvider;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesModule;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public class OrcsApiImpl implements OrcsApi {

   private Log logger;
   private OrcsDataStore dataStore;
   private ExecutorAdmin executorAdmin;
   private SystemPreferences preferences;
   private EventAdmin eventAdmin;

   private QueryModule queryModule;
   private IndexerModule indexerModule;
   private OrcsTypesModule typesModule;
   private OrcsSession systemSession;
   private DataModule module;

   private TxDataManager txDataManager;
   private TxCallableFactory txCallableFactory;
   private ScriptEngineManager manager;
   private OrcsApplicabilityOps applicability;

   ExternalArtifactManager proxyManager;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsDataStore(OrcsDataStore dataStore) {
      this.dataStore = dataStore;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   public void start() {
      systemSession = createSession();

      BranchHierarchyProvider hierarchyProvider = new BranchHierarchyProvider() {

         private final ThreadLocal<Iterable<? extends BranchId>> cache =
            new ThreadLocal<Iterable<? extends BranchId>>();

         @Override
         public Iterable<? extends BranchId> getParentHierarchy(BranchId branch) {
            Iterable<? extends BranchId> toReturn = cache.get();
            if (toReturn == null) {
               Set<BranchId> branches = Sets.newLinkedHashSet();
               BranchQuery branchQuery = getQueryFactory().branchQuery();
               branchQuery.andIsAncestorOf(branch);
               branches.add(branch);

               for (BranchId parent : branchQuery.getResults()) {
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

      module = dataStore.createDataModule(orcsTypes);

      AttributeFactory attributeFactory = new AttributeFactory(module.getDataFactory(), orcsTypes.getAttributeTypes());

      ArtifactFactory artifactFactory =
         new ArtifactFactory(module.getDataFactory(), attributeFactory, orcsTypes.getArtifactTypes());

      RelationFactory relationFactory = new RelationFactory(module.getDataFactory());

      final GraphFactory graphFactory = new GraphFactoryImpl();
      GraphBuilderFactory graphBuilderFactory =
         new GraphBuilderFactory(logger, artifactFactory, attributeFactory, relationFactory);

      QueryModuleProvider queryModuleProvider = new QueryModuleProvider() {

         @Override
         public QueryFactory getQueryFactory(OrcsSession session) {
            return queryModule.createQueryFactory(session);
         }

      };

      RelationNodeLoader nodeLoader = new RelationNodeLoaderImpl(module.getDataLoaderFactory(), graphBuilderFactory);
      RelationManager relationManager = RelationManagerFactory.createRelationManager(logger,
         orcsTypes.getRelationTypes(), relationFactory, nodeLoader, queryModuleProvider);

      GraphProvider graphProvider = new GraphProvider() {

         @Override
         public GraphData getGraph(OrcsSession session, BranchId branch, TransactionId transactionId) {
            return graphFactory.createGraph(session, branch, transactionId);
         }
      };

      proxyManager = new ExternalArtifactManagerImpl(relationManager, orcsTypes.getArtifactTypes());

      TransactionProvider txProvider = new TransactionProvider() {

         @Override
         public TransactionId getHeadTransaction(OrcsSession session, BranchId branch) {
            QueryFactory queryFactory = queryModule.createQueryFactory(session);
            return queryFactory.transactionQuery().andIsHead(branch).getResults().getExactlyOne();
         }
      };

      TxDataLoader txDataLoader = new TxDataLoaderImpl(module.getDataLoaderFactory(), graphFactory, graphBuilderFactory,
         graphProvider, txProvider);
      txDataManager =
         new TxDataManager(proxyManager, artifactFactory, relationManager, module.getDataFactory(), txDataLoader);
      txCallableFactory = new TxCallableFactory(logger, module.getTxDataStore(), txDataManager);

      queryModule = new QueryModule(logger, module.getQueryEngine(), graphBuilderFactory, graphProvider,
         orcsTypes.getArtifactTypes(), orcsTypes.getAttributeTypes(), proxyManager);

      indexerModule = new IndexerModule(logger, preferences, executorAdmin, dataStore.getQueryEngineIndexer());
      indexerModule.start(getSystemSession(), orcsTypes.getAttributeTypes());

      OrcsScriptCompiler compiler = new OrcsScriptCompilerImpl(getSystemSession(), module, orcsTypes);
      manager = ScriptEngines.newScriptEngineManager(compiler);

      applicability = new OrcsApplicabilityOps(this);
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
   public QueryFactory getQueryFactory() {
      OrcsSession session = getSession();
      return queryModule.createQueryFactory(session);
   }

   @Override
   public OrcsBranch getBranchOps() {
      OrcsSession session = getSession();
      QueryFactory queryFactory = getQueryFactory();
      return new OrcsBranchImpl(this, logger, session, module.getBranchDataStore(), queryFactory, getOrcsTypes());
   }

   @Override
   public KeyValueOps getKeyValueOps() {
      return new KeyValueOpsImpl(module.getKeyValueStore());
   }

   @Override
   public TransactionFactory getTransactionFactory() {
      OrcsSession session = getSession();
      return new TransactionFactoryImpl(session, txDataManager, txCallableFactory, this, getBranchOps(),
         getKeyValueOps(), module.getTxDataStore());
   }

   @Override
   public OrcsAdmin getAdminOps() {
      OrcsSession session = getSession();
      return new OrcsAdminImpl(this, logger, session, module.getDataStoreAdmin(), eventAdmin);
   }

   @Override
   public OrcsPerformance getOrcsPerformance() {
      OrcsSession session = getSession();
      return new OrcsPerformanceImpl(logger, session, queryModule, indexerModule);
   }

   @Override
   public QueryIndexer getQueryIndexer() {
      OrcsSession session = getSession();
      return indexerModule.createQueryIndexer(session, getOrcsTypes().getAttributeTypes());
   }

   private OrcsSession getSystemSession() {
      return systemSession;
   }

   private OrcsSession getSession() {
      // TODO get sessions from a session context cache - improve this
      String sessionId = GUID.create();
      return new OrcsSessionImpl(sessionId);
   }

   @Override
   public OrcsTypes getOrcsTypes() {
      OrcsSession session = getSession();
      return typesModule.createOrcsTypes(session);
   }

   private OrcsSession createSession() {
      String sessionId = GUID.create();
      return new OrcsSessionImpl(sessionId);
   }

   @Override
   public ScriptEngine getScriptEngine() {
      return manager.getEngineByName(ScriptEngines.ORCS_SCRIPT_ENGINE_ID);
   }

   @Override
   public SystemPreferences getSystemPreferences() {
      return preferences;
   }

   @Override
   public OrcsApplicability getApplicabilityOps() {
      return applicability;
   }
}
