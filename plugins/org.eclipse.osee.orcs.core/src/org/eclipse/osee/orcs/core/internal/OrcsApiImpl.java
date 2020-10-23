/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.OseeApiBase;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsPerformance;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.internal.access.AccessControlServiceImpl;
import org.eclipse.osee.orcs.core.internal.access.UserGroupService;
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
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.core.internal.search.QueryModule.QueryModuleProvider;
import org.eclipse.osee.orcs.core.internal.session.OrcsSessionImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TransactionFactoryImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TxCallableFactory;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataLoaderImpl;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataLoaderImpl.TransactionProvider;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;
import org.eclipse.osee.orcs.core.internal.types.impl.OrcsTypesImpl;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsApiImpl extends OseeApiBase implements OrcsApi {

   private Log logger;
   private OrcsDataStore dataStore;
   private ExecutorAdmin executorAdmin;
   private SystemProperties properties;

   private QueryModule queryModule;
   private IndexerModule indexerModule;
   private OrcsSession systemSession;
   private DataModule module;

   private TxDataManager txDataManager;
   private TxCallableFactory txCallableFactory;
   private OrcsApplicabilityOps applicability;
   private UserGroupService userGroupService;
   private IAccessControlService accessControlService;
   private ActivityLog activityLog;
   private OrcsTypes orcsTypes;

   ExternalArtifactManager proxyManager;

   // for ReviewOsgiXml public void setOrcsTokenService(OrcsTokenService tokenService) {
   // for ReviewOsgiXml public void setJaxRsApi(JaxRsApi jaxRsApi) {

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsDataStore(OrcsDataStore dataStore) {
      this.dataStore = dataStore;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setSystemProperties(SystemProperties properties) {
      this.properties = properties;
   }

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void start() {
      systemSession = createSession();
      module = dataStore.createDataModule(tokenService());

      orcsTypes = new OrcsTypesImpl(getSession(), dataStore.getTypesDataStore());

      AttributeFactory attributeFactory = new AttributeFactory(module.getDataFactory(), tokenService());

      ArtifactFactory artifactFactory = new ArtifactFactory(module.getDataFactory(), attributeFactory);

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
      RelationManager relationManager = RelationManagerFactory.createRelationManager(logger, tokenService(),
         relationFactory, nodeLoader, queryModuleProvider);

      GraphProvider graphProvider = new GraphProvider() {

         @Override
         public GraphData getGraph(OrcsSession session, BranchId branch, TransactionId transactionId) {
            return graphFactory.createGraph(session, branch, transactionId);
         }
      };

      proxyManager = new ExternalArtifactManagerImpl(relationManager, tokenService());
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

      queryModule = new QueryModule(this, logger, module.getQueryEngine(), graphBuilderFactory, graphProvider,
         tokenService(), proxyManager);

      indexerModule = new IndexerModule(logger, properties, executorAdmin, dataStore.getQueryEngineIndexer());
      indexerModule.start(getSystemSession(), tokenService());

      applicability = new OrcsApplicabilityOps(this);
      accessControlService = new AccessControlServiceImpl();
   }

   public void stop() {
      if (indexerModule != null) {
         indexerModule.stop();
      }
      queryModule = null;
      txDataManager = null;
      txCallableFactory = null;
      module = null;
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
      return new OrcsBranchImpl(this, logger, session, module.getBranchDataStore(), queryFactory);
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
      return new OrcsAdminImpl(this, logger, session, module.getDataStoreAdmin());
   }

   @Override
   public OrcsPerformance getOrcsPerformance() {
      OrcsSession session = getSession();
      return new OrcsPerformanceImpl(logger, session, queryModule, indexerModule);
   }

   @Override
   public QueryIndexer getQueryIndexer() {
      OrcsSession session = getSession();
      return indexerModule.createQueryIndexer(session, tokenService());
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
      return orcsTypes;
   }

   private OrcsSession createSession() {
      String sessionId = GUID.create();
      return new OrcsSessionImpl(sessionId);
   }

   @Override
   public SystemProperties getSystemProperties() {
      return properties;
   }

   @Override
   public OrcsApplicability getApplicabilityOps() {
      return applicability;
   }

   @Override
   public UserGroupService getUserGroupService() {
      if (userGroupService == null) {
         userGroupService = new UserGroupService();
         userGroupService.setOrcsApi(this);
      }
      return userGroupService;
   }

   @Override
   public IAccessControlService getAccessControlService() {
      return accessControlService;
   }

   @Override
   public JdbcService getJdbcService() {
      return dataStore.getJdbcService();
   }

   @Override
   public ActivityLog getActivityLog() {
      return activityLog;
   }
}