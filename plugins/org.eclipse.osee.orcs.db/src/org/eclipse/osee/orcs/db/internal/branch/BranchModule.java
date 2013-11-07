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
package org.eclipse.osee.orcs.db.internal.branch;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.callable.BranchCopyTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.CheckBranchExchangeIntegrityCallable;
import org.eclipse.osee.orcs.db.internal.callable.CommitBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompareDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CreateBranchDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ExportBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.ImportBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactoryImpl;
import org.eclipse.osee.orcs.db.internal.exchange.ExportItemFactory;

/**
 * @author Roberto E. Escobar
 */
public class BranchModule {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityLocator identityService;
   private final TempCachingService cachingService;
   private final SystemPreferences preferences;
   private final ExecutorAdmin executorAdmin;
   private final IResourceManager resourceManager;

   private final IOseeModelFactoryService modelFactory;

   public BranchModule(Log logger, IOseeDatabaseService dbService, IdentityLocator identityService, TempCachingService cachingService, SystemPreferences preferences, ExecutorAdmin executorAdmin, IResourceManager resourceManager, IOseeModelFactoryService modelFactory) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.cachingService = cachingService;
      this.preferences = preferences;
      this.executorAdmin = executorAdmin;
      this.resourceManager = resourceManager;
      this.modelFactory = modelFactory;
   }

   public BranchDataStore createBranchDataStore(final DataLoaderFactory dataLoaderFactory) {
      final MissingChangeItemFactory missingChangeItemFactory =
         new MissingChangeItemFactoryImpl(identityService, dataLoaderFactory);
      return new BranchDataStore() {
         @Override
         public Callable<Branch> createBranch(OrcsSession session, CreateBranchData branchData) {
            return new CreateBranchDatabaseTxCallable(logger, session, dbService, cachingService.getBranchCache(),
               cachingService.getTransactionCache(), modelFactory.getBranchFactory(),
               modelFactory.getTransactionFactory(), branchData);
         }

         @Override
         public Callable<Branch> createBranchCopyTx(OrcsSession session, CreateBranchData branchData) {
            return new BranchCopyTxCallable(logger, session, dbService, cachingService.getBranchCache(),
               cachingService.getTransactionCache(), modelFactory.getBranchFactory(),
               modelFactory.getTransactionFactory(), branchData);
         }

         @Override
         public Callable<TransactionRecord> commitBranch(OrcsSession session, ArtifactReadable committer, Branch source, Branch destination) {
            return new CommitBranchDatabaseCallable(logger, session, dbService, cachingService.getBranchCache(),
               cachingService.getTransactionCache(), modelFactory.getTransactionFactory(), committer, source,
               destination, missingChangeItemFactory);
         }

         @Override
         public Callable<Branch> purgeBranch(OrcsSession session, Branch branch) {
            return new PurgeBranchDatabaseCallable(logger, session, dbService, cachingService.getBranchCache(), branch);
         }

         @Override
         public Callable<List<ChangeItem>> compareBranch(OrcsSession session, TransactionRecord sourceTx, TransactionRecord destinationTx) {
            return new CompareDatabaseCallable(logger, session, dbService, cachingService.getBranchCache(),
               cachingService.getTransactionCache(), sourceTx, destinationTx, missingChangeItemFactory);
         }

         @Override
         public Callable<URI> exportBranch(OrcsSession session, OrcsTypes orcsTypes, List<IOseeBranch> branches, PropertyStore options, String exportName) {
            ExportItemFactory factory =
               new ExportItemFactory(logger, dbService, identityService, resourceManager, orcsTypes);
            return new ExportBranchDatabaseCallable(session, factory, preferences, executorAdmin, identityService,
               branches, options, exportName);
         }

         @Override
         public Callable<URI> importBranch(OrcsSession session, OrcsTypes orcsTypes, URI fileToImport, List<IOseeBranch> branches, PropertyStore options) {
            ImportBranchDatabaseCallable callable =
               new ImportBranchDatabaseCallable(logger, session, dbService, preferences, resourceManager,
                  identityService, orcsTypes, fileToImport, branches, options);
            return callable;
         }

         @Override
         public Callable<URI> checkBranchExchangeIntegrity(OrcsSession session, URI fileToCheck) {
            return new CheckBranchExchangeIntegrityCallable(logger, session, dbService, preferences, resourceManager,
               fileToCheck);
         }

      };
   }
}
