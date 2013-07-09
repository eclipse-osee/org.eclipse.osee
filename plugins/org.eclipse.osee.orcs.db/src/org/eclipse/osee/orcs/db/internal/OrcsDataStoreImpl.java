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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.AbstractIdentity;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.branch.BranchDataStoreImpl;
import org.eclipse.osee.orcs.db.internal.callable.OrcsTypeLoaderCallable;
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
import org.eclipse.osee.orcs.db.internal.types.TempCachingServiceFactory;
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

   private DataStoreAdmin dataStoreAdmin;
   private BranchDataStore branchStore;
   private DataModuleFactory dataModuleFactory;
   private QueryModuleFactory queryModule;
   private IdFactory idFactory;
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

      idFactory = new IdFactoryImpl(dbService, cacheService.getBranchCache());

      dataModuleFactory = new DataModuleFactory(logger);

      MissingChangeItemFactory missingChangeItemFactory =
         new MissingChangeItemFactoryImpl(dataModuleFactory, identityService);

      branchStore =
         new BranchDataStoreImpl(logger, dbService, identityService, cacheService, preferences, executorAdmin,
            resourceManager, modelFactory, idFactory, dataModuleFactory, missingChangeItemFactory);

      dataStoreAdmin = new DataStoreAdminImpl(logger, dbService, identityService, branchStore, preferences);

      queryModule = new QueryModuleFactory(logger, executorAdmin);
      queryModule.start(dbService, identityService, sqlProvider, resourceManager, cacheService.getBranchCache());
   }

   public void stop() throws Exception {
      queryModule.stop();
      queryModule = null;

      dataModuleFactory.stop();
      dataModuleFactory = null;

      branchStore = null;
      dataStoreAdmin = null;
   }

   private SqlProvider createSqlProvider() {
      StaticSqlProvider toReturn = new StaticSqlProvider();
      toReturn.setLogger(logger);
      toReturn.setPreferences(preferences);
      return toReturn;
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
   public DataModule createDataModule(ArtifactTypes artifactTypes, AttributeTypes attributeTypes) {
      return dataModuleFactory.create(dbService, idFactory, identityService, sqlProvider, proxyProvider,
         cacheService.getBranchCache(), artifactTypes, attributeTypes);
   }

   @Override
   public QueryEngine getQueryEngine() {
      return queryModule.getQueryEngine();
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

   @Override
   public Callable<IResource> getOrcsTypesLoader(OrcsSession session) {
      return new OrcsTypeLoaderCallable(logger, session, dbService, identityService, resourceManager);
   }

   @Override
   public Callable<Void> purgeArtifactsByArtifactType(OrcsSession session, Collection<? extends IArtifactType> typesToPurge) {
      return new PurgeArtifactTypeDatabaseTxCallable(logger, session, dbService, identityService, typesToPurge);
   }

   @Override
   public Callable<Void> purgeAttributesByAttributeType(OrcsSession session, Collection<? extends IAttributeType> typesToPurge) {
      return new PurgeAttributeTypeDatabaseTxCallable(logger, session, dbService, identityService, typesToPurge);
   }

   @Override
   public Callable<Void> purgeRelationsByRelationType(OrcsSession session, Collection<? extends IRelationType> typesToPurge) {
      return new PurgeRelationTypeDatabaseTxCallable(logger, session, dbService, identityService, typesToPurge);
   }

   @Override
   public Callable<Void> persistTypeIdentities(OrcsSession session, final Collection<Identity<Long>> types) {
      return new Callable<Void>() {

         @Override
         public Void call() throws Exception {
            List<Long> toPersist = new LinkedList<Long>();
            for (Identity<Long> type : types) {
               toPersist.add(type.getGuid());
            }
            identityService.store(toPersist);
            return null;
         }
      };
   }

   private static final class DatastoreSession extends AbstractIdentity<String> implements OrcsSession {

      private final String id;

      public DatastoreSession(String id) {
         super();
         this.id = id;
      }

      @Override
      public String getGuid() {
         return id;
      }

   }
}
