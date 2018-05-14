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
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.db.internal.branch.BranchModule;
import org.eclipse.osee.orcs.db.internal.branch.KeyValueModule;
import org.eclipse.osee.orcs.db.internal.loader.LoaderModule;
import org.eclipse.osee.orcs.db.internal.search.QueryModule;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.internal.transaction.TxModule;
import org.eclipse.osee.orcs.db.internal.types.TypesModule;
import org.eclipse.osee.orcs.db.internal.util.IdentityManagerImpl;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OrcsDataStoreImpl implements OrcsDataStore {

   private Log logger;
   private JdbcService jdbcService;
   private SystemPreferences preferences;
   private ExecutorAdmin executorAdmin;
   private IResourceManager resourceManager;

   private OrcsTypesDataStore typesDataStore;
   private DataModuleFactory dataModuleFactory;
   private QueryModule queryModule;
   private IdentityManager idManager;
   private SqlJoinFactory joinFactory;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setResourceManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void setSqlJoinFactory(SqlJoinFactory joinFactory) {
      this.joinFactory = joinFactory;
   }

   public void start(BundleContext context) throws Exception {
      JdbcClient jdbcClient = jdbcService.getClient();

      idManager = new IdentityManagerImpl(jdbcClient);

      TypesModule typesModule = new TypesModule(logger, jdbcClient, joinFactory, resourceManager);
      typesDataStore = typesModule.createTypesDataStore();

      LoaderModule loaderModule = new LoaderModule(logger, jdbcClient, idManager, joinFactory, resourceManager);

      queryModule = new QueryModule(logger, executorAdmin, jdbcClient, joinFactory, idManager);
      queryModule.startIndexer(resourceManager);

      BranchModule branchModule =
         new BranchModule(logger, jdbcClient, joinFactory, idManager, preferences, executorAdmin, resourceManager);

      TxModule txModule = new TxModule(logger, jdbcClient, joinFactory, idManager);

      AdminModule adminModule = new AdminModule(logger, jdbcClient, idManager, preferences, typesDataStore);

      KeyValueModule keyValueModule = new KeyValueModule(jdbcClient);

      dataModuleFactory =
         new DataModuleFactory(logger, loaderModule, queryModule, branchModule, keyValueModule, txModule, adminModule);
   }

   public void stop() throws Exception {
      queryModule.stopIndexer();
      queryModule = null;
   }

   @Override
   public OrcsTypesDataStore getTypesDataStore() {
      return typesDataStore;
   }

   @Override
   public DataModule createDataModule(OrcsTypes orcsTypes) {
      return dataModuleFactory.createDataModule(orcsTypes);
   }

   @Override
   public QueryEngineIndexer getQueryEngineIndexer() {
      return queryModule.getQueryIndexer();
   }

}
