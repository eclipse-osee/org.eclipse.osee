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
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.db.internal.branch.BranchDataStoreImpl;
import org.eclipse.osee.orcs.db.internal.loader.DataModuleFactory;
import org.eclipse.osee.orcs.db.internal.loader.DataProxyFactoryProvider;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.loader.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.IdFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.QueryModuleFactory;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;

/**
 * @author Roberto E. Escobar
 */
public class OrcsDataStoreImpl implements OrcsDataStore {

   private Log logger;
   private IOseeDatabaseService dbService;
   private IdentityService identityService;
   private IOseeCachingService cacheService;
   private DataStoreTypeCache cache;
   private SystemPreferences preferences;
   private ExecutorAdmin executorAdmin;
   private IResourceManager resourceManager;
   private DataProxyFactoryProvider proxyProvider;
   private IOseeModelFactoryService modelFactory;
   private IOseeModelingService typeModelService;

   private DataStoreAdmin dataStoreAdmin;
   private BranchDataStore branchStore;
   private DataFactory dataFactory;
   private DataLoader dataLoader;
   private QueryEngine queryEngine;
   private QueryEngineIndexer queryIndexer;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   //TODO fix these two services
   public void setCachingService(IOseeCachingService cacheService) {
      this.cacheService = cacheService;
   }

   //TODO other
   public void setDataStoreTypeCache(DataStoreTypeCache cache) {
      this.cache = cache;
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

      branchStore =
         new BranchDataStoreImpl(logger, dbService, identityService, cacheService, preferences, executorAdmin,
            resourceManager, modelFactory, typeModelService, sqlProvider, idFactory);

      dataStoreAdmin = new DataStoreAdminImpl(logger, dbService, identityService, branchStore, preferences);

      DataModuleFactory dataModuleFactory = new DataModuleFactory(logger, dbService, identityService);
      OrcsObjectFactory rowDataFactory =
         dataModuleFactory.createOrcsObjectFactory(proxyProvider, cacheService.getAttributeTypeCache());
      dataFactory = dataModuleFactory.createDataFactory(rowDataFactory, idFactory, cacheService.getArtifactTypeCache());
      dataLoader = dataModuleFactory.createDataLoader(sqlProvider, rowDataFactory);

      QueryModuleFactory factory = new QueryModuleFactory(logger, dbService, identityService, executorAdmin);
      TaggingEngine taggingEngine = factory.createTaggingEngine(cacheService.getAttributeTypeCache());

      queryEngine = factory.createQueryEngine(taggingEngine, sqlProvider, cache, cacheService.getBranchCache());
      queryIndexer =
         factory.createQueryEngineIndexer(taggingEngine, resourceManager, cacheService.getAttributeTypeCache());
   }

   public void stop() {
      queryIndexer = null;
      queryEngine = null;
      dataFactory = null;
      dataLoader = null;
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
      return dataFactory;
   }

   @Override
   public DataLoader getDataLoader() {
      return dataLoader;
   }

   @Override
   public QueryEngine getQueryEngine() {
      return queryEngine;
   }

   @Override
   public QueryEngineIndexer getQueryEngineIndexer() {
      return queryIndexer;
   }
}
