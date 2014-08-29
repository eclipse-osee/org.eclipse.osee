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

import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
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
import org.eclipse.osee.orcs.db.internal.types.TypesModule;
import org.eclipse.osee.orcs.db.internal.util.IdentityManagerImpl;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OrcsDataStoreImpl implements OrcsDataStore {

   private Log logger;
   private IOseeDatabaseService dbService;
   private SystemPreferences preferences;
   private ExecutorAdmin executorAdmin;
   private IResourceManager resourceManager;
   private DataProxyFactoryProvider proxyProvider;
   private EventService eventService;

   private OrcsTypesDataStore typesDataStore;
   private DataModuleFactory dataModuleFactory;
   private QueryModule queryModule;
   private IdentityManager idManager;
   private SqlProvider sqlProvider;

   public void setLogger(Log logger) {
      this.logger = logger;
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

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   public void start(BundleContext context) throws Exception {
      String id = String.format("orcs_datastore_system_%s", GUID.create());
      OrcsSession session = new DatastoreSession(id);

      sqlProvider = createSqlProvider();

      idManager = new IdentityManagerImpl(dbService);

      TypesModule typesModule = new TypesModule(logger, dbService, idManager, resourceManager);
      typesDataStore = typesModule.createTypesDataStore();

      LoaderModule loaderModule = new LoaderModule(logger, dbService, idManager, sqlProvider, proxyProvider);

      queryModule = new QueryModule(logger, executorAdmin, dbService, idManager, sqlProvider);
      queryModule.startIndexer(resourceManager);

      BranchModule branchModule =
         new BranchModule(logger, dbService, idManager, preferences, executorAdmin, resourceManager);

      TxModule txModule = new TxModule(logger, dbService, idManager);

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
      return dataModuleFactory.createDataModule(artifactTypes, attributeTypes);
   }

   @Override
   public QueryEngineIndexer getQueryEngineIndexer() {
      return queryModule.getQueryIndexer();
   }

   private static final class DatastoreSession extends BaseIdentity<String> implements OrcsSession {
      public DatastoreSession(String id) {
         super(id);
      }
   }
}
