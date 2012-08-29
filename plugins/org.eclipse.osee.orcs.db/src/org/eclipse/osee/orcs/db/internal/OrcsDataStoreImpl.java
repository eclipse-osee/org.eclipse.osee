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

import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.db.internal.branch.BranchDataStoreImpl;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactoryImpl;
import org.eclipse.osee.orcs.db.internal.loader.DataModuleFactory;
import org.eclipse.osee.orcs.db.internal.loader.DataProxyFactoryProvider;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.IdFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.QueryModuleFactory;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;

/**
 * @author Roberto E. Escobar
 */
public class OrcsDataStoreImpl implements OrcsDataStore {

   private Log logger;
   private IOseeDatabaseService dbService;
   private IdentityService identityService;
   private IOseeCachingService cacheService;
   private SystemPreferences preferences;
   private ExecutorAdmin executorAdmin;
   private IResourceManager resourceManager;
   private DataProxyFactoryProvider proxyProvider;
   private IOseeModelFactoryService modelFactory;
   private IOseeModelingService typeModelService;

   private DataStoreAdmin dataStoreAdmin;
   private BranchDataStore branchStore;
   private DataModuleFactory dataModuleFactory;
   private QueryModuleFactory queryModule;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setCachingService(IOseeCachingService cacheService) {
      this.cacheService = cacheService;
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

   public void setDataProxyFactoryProvider(DataProxyFactoryProvider proxyProvider) {
      this.proxyProvider = proxyProvider;
   }

   public void setModelFactory(IOseeModelFactoryService modelFactory) {
      this.modelFactory = modelFactory;
   }

   public void setTypeModelService(IOseeModelingService typeModelService) {
      this.typeModelService = typeModelService;
   }

   public void start() {
      StaticSqlProvider sqlProvider = new StaticSqlProvider();
      sqlProvider.setLogger(logger);
      sqlProvider.setPreferences(preferences);

      IdFactory idFactory = new IdFactoryImpl(dbService, cacheService.getBranchCache());

      dataModuleFactory = new DataModuleFactory(logger);
      dataModuleFactory.create(dbService, idFactory, identityService, sqlProvider, cacheService, proxyProvider);

      MissingChangeItemFactory missingChangeItemFactory =
         new MissingChangeItemFactoryImpl(dataModuleFactory.getDataLoaderFactory(), identityService);

      branchStore =
         new BranchDataStoreImpl(logger, dbService, identityService, cacheService, preferences, executorAdmin,
            resourceManager, modelFactory, typeModelService, idFactory, dataModuleFactory.getDataLoaderFactory(),
            missingChangeItemFactory);

      dataStoreAdmin = new DataStoreAdminImpl(logger, dbService, identityService, branchStore, preferences);

      queryModule = new QueryModuleFactory(logger);
      queryModule.create(executorAdmin, dbService, identityService, sqlProvider, cacheService, resourceManager);
   }

   public void stop() {
      queryModule.stop();
      queryModule = null;

      dataModuleFactory.stop();
      dataModuleFactory = null;

      branchStore = null;
      dataStoreAdmin = null;
   }

   @Override
   public BranchDataStore getBranchDataStore() {
      return branchStore;
   }

   @Override
   public DataStoreAdmin getDataStoreAdmin() {
      return dataStoreAdmin;
   }

   @Override
   public DataFactory getDataFactory() {
      return dataModuleFactory.getDataFactory();
   }

   @Override
   public DataLoaderFactory getDataLoaderFactory() {
      return dataModuleFactory.getDataLoaderFactory();
   }

   @Override
   public QueryEngine getQueryEngine() {
      return queryModule.getQueryEngine();
   }

   @Override
   public QueryEngineIndexer getQueryEngineIndexer() {
      return queryModule.getQueryIndexer();
   }
}
