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

import java.util.Collection;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.branch.BranchModule;
import org.eclipse.osee.orcs.db.internal.loader.DataProxyFactoryProvider;
import org.eclipse.osee.orcs.db.internal.loader.LoaderModule;
import org.eclipse.osee.orcs.db.internal.search.QueryModule;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;
import org.eclipse.osee.orcs.db.internal.transaction.TxModule;
import org.eclipse.osee.orcs.db.internal.types.TempCachingServiceFactory;
import org.eclipse.osee.orcs.db.internal.types.TypesModule;
import org.eclipse.osee.orcs.db.internal.util.IdentityManagerImpl;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OrcsDataStoreImpl implements OrcsDataStore, TempCachingService {

   private Log logger;
   private IOseeDatabaseService dbService;
   private IdentityService identityService;
   private SystemPreferences preferences;
   private ExecutorAdmin executorAdmin;
   private IResourceManager resourceManager;
   private DataProxyFactoryProvider proxyProvider;
   private IOseeModelFactoryService modelFactory;
   private EventService eventService;

   private OrcsTypesDataStore typesDataStore;
   private DataModuleFactory dataModuleFactory;
   private QueryModule queryModule;
   private IdentityManager idManager;
   private SqlProvider sqlProvider;

   private TempCachingService cacheService;

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

   public void start(BundleContext context) throws Exception {
      String id = String.format("orcs_datastore_system_%s", GUID.create());
      OrcsSession session = new DatastoreSession(id);

      TempCachingServiceFactory modelingService =
         new TempCachingServiceFactory(logger, dbService, executorAdmin, modelFactory, eventService);
      cacheService = modelingService.createCachingService(session, true);

      sqlProvider = createSqlProvider();

      idManager = new IdentityManagerImpl(dbService, identityService);

      TypesModule typesModule = new TypesModule(logger, dbService, idManager, resourceManager);
      typesDataStore = typesModule.createTypesDataStore();

      LoaderModule loaderModule = new LoaderModule(logger, dbService, idManager, sqlProvider, proxyProvider);

      queryModule = new QueryModule(logger, executorAdmin, dbService, idManager, sqlProvider);
      queryModule.startIndexer(resourceManager);

      BranchModule branchModule =
         new BranchModule(logger, dbService, idManager, cacheService, preferences, executorAdmin, resourceManager,
            modelFactory);

      TxModule txModule = new TxModule(logger, dbService, cacheService, modelFactory, idManager);

      AdminModule adminModule = new AdminModule(logger, dbService, idManager, preferences);

      dataModuleFactory = new DataModuleFactory(logger, loaderModule, queryModule, branchModule, txModule, adminModule);
   }

   public void stop() throws Exception {
      queryModule.stopIndexer();
      queryModule = null;
   }

   private SqlProvider createSqlProvider() {
      StaticSqlProvider toReturn = new StaticSqlProvider();
      toReturn.setLogger(logger);
      toReturn.setPreferences(preferences);
      return toReturn;
   }

   @Override
   public OrcsTypesDataStore getTypesDataStore() {
      return typesDataStore;
   }

   @Override
   public DataModule createDataModule(ArtifactTypes artifactTypes, AttributeTypes attributeTypes) {
      return dataModuleFactory.createDataModule(cacheService.getBranchCache(), artifactTypes, attributeTypes);
   }

   @Override
   public QueryEngineIndexer getQueryEngineIndexer() {
      return queryModule.getQueryIndexer();
   }

   private TempCachingService getProxied() {
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

   private static final class DatastoreSession extends BaseIdentity<String> implements OrcsSession {
      public DatastoreSession(String id) {
         super(id);
      }
   }
}
