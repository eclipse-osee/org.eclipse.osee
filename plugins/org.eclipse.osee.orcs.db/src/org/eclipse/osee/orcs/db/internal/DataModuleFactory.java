/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.KeyValueStore;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.RelationTypes;
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

   public DataModule createDataModule(OrcsTypes orcsTypes, OrcsTokenService tokenService) {
      logger.debug("Creating DataModule");

      ArtifactTypes artifactTypes = orcsTypes.getArtifactTypes();
      AttributeTypes attributeTypes = orcsTypes.getAttributeTypes();
      RelationTypes relationTypes = orcsTypes.getRelationTypes();

      final QueryEngineIndexer indexer = queryModule.getQueryIndexer();
      final AttributeDataProxyFactory proxyFactory = loaderModule.createProxyDataFactory(attributeTypes);
      final OrcsObjectFactory objectFactory = new OrcsObjectFactoryImpl(proxyFactory, relationTypes, artifactTypes);
      final DataFactory dataFactory = loaderModule.createDataFactory(objectFactory);
      final DynamicLoadProcessor loadProcessor = loaderModule.createDynamicLoadProcessor(orcsTypes, proxyFactory);
      SqlObjectLoader sqlObjectLoader = loaderModule.createSqlObjectLoader(objectFactory, loadProcessor, orcsTypes);
      final DataLoaderFactory dataLoaderFactory = loaderModule.createDataLoaderFactory(sqlObjectLoader);
      final KeyValueStore keyValueStore = keyValueModule.createKeyValueStore();
      final QueryEngine queryEngine = queryModule.createQueryEngine(dataLoaderFactory, orcsTypes, tokenService,
         sqlObjectLoader, keyValueStore, resourceManager);
      final BranchDataStore branchDataStore = branchModule.createBranchDataStore(dataLoaderFactory);
      final TxDataStore txDataStore = txModule.createTransactionStore(dataLoaderFactory, indexer, attributeTypes);
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