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

import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.OseeEnumTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
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
import org.eclipse.osee.orcs.db.internal.callable.PurgeArtifactTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeAttributeTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeRelationTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactoryImpl;
import org.eclipse.osee.orcs.db.internal.loader.DataModuleFactory;
import org.eclipse.osee.orcs.db.internal.loader.DataProxyFactoryProvider;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.IdFactoryImpl;
import org.eclipse.osee.orcs.db.internal.search.QueryModuleFactory;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;
import org.eclipse.osee.orcs.db.internal.types.OseeModelingServiceImpl;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OrcsDataStoreImpl implements OrcsDataStore, IOseeCachingService {

   private Log logger;
   private IOseeDatabaseService dbService;
   private IdentityService identityService;
   private SystemPreferences preferences;
   private ExecutorAdmin executorAdmin;
   private IResourceManager resourceManager;
   private DataProxyFactoryProvider proxyProvider;
   private IOseeModelFactoryService modelFactory;
   private EventService eventService;

   private DataStoreAdmin dataStoreAdmin;
   private BranchDataStore branchStore;
   private DataModuleFactory dataModuleFactory;
   private QueryModuleFactory queryModule;

   private OseeModelingServiceImpl modelingService;
   private IOseeCachingService cacheService;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
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

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   public void start(BundleContext context) {

      modelingService =
         new OseeModelingServiceImpl(logger, dbService, identityService, executorAdmin, resourceManager, modelFactory,
            eventService, this);

      cacheService = modelingService.createCachingService(true);

      StaticSqlProvider sqlProvider = new StaticSqlProvider();
      sqlProvider.setLogger(logger);
      sqlProvider.setPreferences(preferences);

      IdFactory idFactory = new IdFactoryImpl(dbService, cacheService.getBranchCache());

      dataModuleFactory = new DataModuleFactory(logger);
      dataModuleFactory.create(dbService, idFactory, identityService, sqlProvider, proxyProvider,
         cacheService.getBranchCache(), cacheService.getArtifactTypeCache(), cacheService.getAttributeTypeCache());

      MissingChangeItemFactory missingChangeItemFactory =
         new MissingChangeItemFactoryImpl(dataModuleFactory.getDataLoaderFactory(), identityService);

      branchStore =
         new BranchDataStoreImpl(logger, dbService, identityService, cacheService, preferences, executorAdmin,
            resourceManager, modelFactory, modelingService, idFactory, dataModuleFactory.getDataLoaderFactory(),
            missingChangeItemFactory);

      dataStoreAdmin = new DataStoreAdminImpl(logger, dbService, identityService, branchStore, preferences);

      queryModule = new QueryModuleFactory(logger);
      queryModule.create(executorAdmin, dbService, identityService, sqlProvider, resourceManager,
         cacheService.getBranchCache(), cacheService.getAttributeTypeCache());
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

   @Override
   public void importOseeTypes(boolean isInitializing, OseeImportModelRequest request, OseeImportModelResponse response) throws OseeCoreException {
      modelingService.importOseeTypes(isInitializing, request, response);
   }

   @Override
   public void exportOseeTypes(OutputStream outputStream) throws OseeCoreException {
      modelingService.exportOseeTypes(outputStream);
   }

   private IOseeCachingService getProxied() {
      return cacheService;
   }

   @Override
   public BranchCache getBranchCache() {
      return getProxied().getBranchCache();
   }

   @Override
   public TransactionCache getTransactionCache() {
      return getProxied().getTransactionCache();
   }

   @Override
   public ArtifactTypeCache getArtifactTypeCache() {
      return getProxied().getArtifactTypeCache();
   }

   @Override
   public AttributeTypeCache getAttributeTypeCache() {
      return getProxied().getAttributeTypeCache();
   }

   @Override
   public RelationTypeCache getRelationTypeCache() {
      return getProxied().getRelationTypeCache();
   }

   @Override
   public OseeEnumTypeCache getEnumTypeCache() {
      return getProxied().getEnumTypeCache();
   }

   @Override
   public IdentityService getIdentityService() {
      return getProxied().getIdentityService();
   }

   @Override
   public Collection<?> getCaches() {
      return getProxied().getCaches();
   }

   @Override
   public IOseeCache<?, ?> getCache(OseeCacheEnum cacheId) throws OseeCoreException {
      return getProxied().getCache(cacheId);
   }

   @Override
   public void reloadAll() throws OseeCoreException {
      getProxied().reloadAll();
   }

   @Override
   public void clearAll() {
      getProxied().clearAll();
   }

   @Override
   public Callable<?> purgeArtifactType(Collection<? extends IArtifactType> typesToPurge) {
      return new PurgeArtifactTypeDatabaseTxCallable(logger, dbService, identityService, typesToPurge);
   }

   @Override
   public Callable<?> purgeAttributeType(Collection<? extends IAttributeType> typesToPurge) {
      return new PurgeAttributeTypeDatabaseTxCallable(logger, dbService, identityService, typesToPurge);
   }

   @Override
   public Callable<?> purgeRelationType(Collection<? extends IRelationType> typesToPurge) {
      return new PurgeRelationTypeDatabaseTxCallable(logger, dbService, identityService, typesToPurge);
   }

}
