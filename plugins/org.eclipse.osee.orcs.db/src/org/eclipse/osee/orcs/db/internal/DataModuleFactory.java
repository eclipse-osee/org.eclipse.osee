/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.KeyValueStore;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.db.internal.branch.BranchModule;
import org.eclipse.osee.orcs.db.internal.branch.KeyValueModule;
import org.eclipse.osee.orcs.db.internal.loader.LoaderModule;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsObjectFactoryImpl;
import org.eclipse.osee.orcs.db.internal.loader.processor.DynamicLoadProcessor;
import org.eclipse.osee.orcs.db.internal.proxy.AttributeDataProxyFactory;
import org.eclipse.osee.orcs.db.internal.search.QueryModule;
import org.eclipse.osee.orcs.db.internal.transaction.TxModule;

/**
 * @author Roberto E. Escobar
 */
public class DataModuleFactory {

   private final Log logger;
   private final LoaderModule loaderModule;
   private final QueryModule queryModule;
   private final BranchModule branchModule;
   private final KeyValueModule keyValueModule;
   private final TxModule txModule;
   private final DataStoreAdmin dataStoreAdmin;
   private final IResourceManager resourceManager;

   public DataModuleFactory(Log logger, LoaderModule loaderModule, QueryModule queryModule, BranchModule branchModule, KeyValueModule keyValueModule, TxModule txModule, DataStoreAdmin dataStoreAdmin, IResourceManager resourceManager) {
      this.logger = logger;
      this.loaderModule = loaderModule;
      this.queryModule = queryModule;
      this.branchModule = branchModule;
      this.keyValueModule = keyValueModule;
      this.txModule = txModule;
      this.dataStoreAdmin = dataStoreAdmin;
      this.resourceManager = resourceManager;
   }

   public DataModule createDataModule(OrcsTokenService tokenService) {
      logger.debug("Creating DataModule");

      final QueryEngineIndexer indexer = queryModule.getQueryIndexer();
      final AttributeDataProxyFactory proxyFactory = loaderModule.createProxyDataFactory();
      final OrcsObjectFactory objectFactory = new OrcsObjectFactoryImpl(proxyFactory, tokenService);
      final DataFactory dataFactory = loaderModule.createDataFactory(objectFactory);
      final DynamicLoadProcessor loadProcessor = loaderModule.createDynamicLoadProcessor(tokenService, proxyFactory);
      SqlObjectLoader sqlObjectLoader =
         loaderModule.createSqlObjectLoader(objectFactory, loadProcessor, tokenService);
      final DataLoaderFactory dataLoaderFactory = loaderModule.createDataLoaderFactory(sqlObjectLoader);
      final KeyValueStore keyValueStore = keyValueModule.createKeyValueStore();
      final QueryEngine queryEngine = queryModule.createQueryEngine(dataLoaderFactory, tokenService, sqlObjectLoader,
         keyValueStore, resourceManager);
      final BranchDataStore branchDataStore = branchModule.createBranchDataStore(dataLoaderFactory);
      final TxDataStore txDataStore = txModule.createTransactionStore(dataLoaderFactory, indexer, tokenService);
      return new DataModule() {
         @Override
         public DataFactory getDataFactory() {
            return dataFactory;
         }

         @Override
         public DataLoaderFactory getDataLoaderFactory() {
            return dataLoaderFactory;
         }

         @Override
         public QueryEngine getQueryEngine() {
            return queryEngine;
         }

         @Override
         public BranchDataStore getBranchDataStore() {
            return branchDataStore;
         }

         @Override
         public KeyValueStore getKeyValueStore() {
            return keyValueStore;
         }

         @Override
         public TxDataStore getTxDataStore() {
            return txDataStore;
         }

         @Override
         public DataStoreAdmin getDataStoreAdmin() {
            return dataStoreAdmin;
         }
      };
   }
}
