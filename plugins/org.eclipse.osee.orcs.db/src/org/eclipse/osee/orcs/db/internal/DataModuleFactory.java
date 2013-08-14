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

import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.DataStoreAdmin;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryEngineIndexer;
import org.eclipse.osee.orcs.core.ds.TxDataStore;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.branch.BranchModule;
import org.eclipse.osee.orcs.db.internal.loader.LoaderModule;
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
   private final TxModule txModule;
   private final AdminModule adminModule;

   public DataModuleFactory(Log logger, LoaderModule loaderModule, QueryModule queryModule, BranchModule branchModule, TxModule txModule, AdminModule adminModule) {
      super();
      this.logger = logger;
      this.loaderModule = loaderModule;
      this.queryModule = queryModule;
      this.branchModule = branchModule;
      this.txModule = txModule;
      this.adminModule = adminModule;
   }

   public DataModule createDataModule(BranchCache branchCache, ArtifactTypes artifactTypes, AttributeTypes attributeTypes) {
      logger.debug("Creating DataModule");
      QueryEngineIndexer indexer = queryModule.getQueryIndexer();
      OrcsObjectFactory objectFactory = loaderModule.createOrcsObjectFactory(attributeTypes);
      final DataFactory dataFactory = loaderModule.createDataFactory(objectFactory, artifactTypes);
      final DataLoaderFactory dataLoaderFactory = loaderModule.createDataLoaderFactory(objectFactory, branchCache);
      final QueryEngine queryEngine = queryModule.createQueryEngine(branchCache);
      final BranchDataStore branchDataStore = branchModule.createBranchDataStore(dataLoaderFactory);
      final TxDataStore txDataStore = txModule.createTransactionStore(dataLoaderFactory, indexer, attributeTypes);
      final DataStoreAdmin dataStoreAdmin = adminModule.createDataStoreAdmin(branchDataStore);
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
