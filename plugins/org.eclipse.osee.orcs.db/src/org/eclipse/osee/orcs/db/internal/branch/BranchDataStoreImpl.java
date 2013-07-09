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
package org.eclipse.osee.orcs.db.internal.branch;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.core.ds.TransactionResult;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.callable.BranchCopyTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.CheckBranchExchangeIntegrityCallable;
import org.eclipse.osee.orcs.db.internal.callable.CommitBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompareDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CreateBranchDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ExportBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.ImportBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeTransactionTxCallable;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.exchange.ExportItemFactory;
import org.eclipse.osee.orcs.db.internal.loader.DataModuleFactory;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.transaction.CheckProvider;
import org.eclipse.osee.orcs.db.internal.transaction.CommitTransactionDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.transaction.ComodificationCheck;
import org.eclipse.osee.orcs.db.internal.transaction.TransactionCheck;
import org.eclipse.osee.orcs.db.internal.transaction.TransactionWriter;
import org.eclipse.osee.orcs.db.internal.transaction.TxSqlBuilderImpl;
import org.eclipse.osee.orcs.db.internal.transaction.UnsubscribeTransaction;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataStoreImpl implements BranchDataStore {

   private final CheckProviderImpl checkProvider = new CheckProviderImpl();
   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final TempCachingService cachingService;
   private final SystemPreferences preferences;
   private final ExecutorAdmin executorAdmin;
   private final IResourceManager resourceManager;

   private final IOseeModelFactoryService modelFactory;

   private final IdFactory idFactory;
   private final DataModuleFactory dataLoader;
   private final MissingChangeItemFactory missingChangeItemFactory;

   public BranchDataStoreImpl(Log logger, IOseeDatabaseService dbService, IdentityService identityService, TempCachingService cachingService, SystemPreferences preferences, ExecutorAdmin executorAdmin, IResourceManager resourceManager, IOseeModelFactoryService modelFactory, IdFactory idFactory, DataModuleFactory dataModuleFactory, MissingChangeItemFactory missingChangeItemFactory) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.cachingService = cachingService;
      this.preferences = preferences;
      this.executorAdmin = executorAdmin;
      this.resourceManager = resourceManager;
      this.modelFactory = modelFactory;
      this.idFactory = idFactory;
      this.dataLoader = dataModuleFactory;
      this.missingChangeItemFactory = missingChangeItemFactory;
   }

   @Override
   public Callable<Branch> createBranch(OrcsSession session, CreateBranchData branchData) {
      return new CreateBranchDatabaseTxCallable(logger, session, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getBranchFactory(), modelFactory.getTransactionFactory(),
         branchData);
   }

   @Override
   public Callable<Branch> createBranchCopyTx(OrcsSession session, CreateBranchData branchData) {
      return new BranchCopyTxCallable(logger, session, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getBranchFactory(), modelFactory.getTransactionFactory(),
         branchData);
   }

   @Override
   public Callable<TransactionRecord> commitBranch(OrcsSession session, ArtifactReadable committer, Branch source, Branch destination) {
      return new CommitBranchDatabaseCallable(logger, session, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getTransactionFactory(), committer, source, destination,
         missingChangeItemFactory);
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
      ExportItemFactory factory = new ExportItemFactory(logger, dbService, identityService, resourceManager, orcsTypes);
      return new ExportBranchDatabaseCallable(session, factory, preferences, executorAdmin,
         cachingService.getBranchCache(), branches, options, exportName);
   }

   @Override
   public Callable<URI> importBranch(OrcsSession session, OrcsTypes orcsTypes, URI fileToImport, List<IOseeBranch> branches, PropertyStore options) {
      ImportBranchDatabaseCallable callable =
         new ImportBranchDatabaseCallable(logger, session, dbService, preferences, resourceManager, identityService, orcsTypes,
            fileToImport, branches, options);
      return callable;
   }

   @Override
   public Callable<URI> checkBranchExchangeIntegrity(OrcsSession session, URI fileToCheck) {
      return new CheckBranchExchangeIntegrityCallable(logger, session, dbService, preferences, resourceManager,
         fileToCheck);
   }

   @Override
   public Callable<TransactionResult> commitTransaction(OrcsSession session, TransactionData data) {
      TxSqlBuilderImpl builder = new TxSqlBuilderImpl(dbService, idFactory, identityService);
      TransactionWriter writer = new TransactionWriter(logger, dbService, builder);
      return new CommitTransactionDatabaseTxCallable(logger, session, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getTransactionFactory(), checkProvider, writer, data);
   }

   private final class CheckProviderImpl implements CheckProvider {

      @Override
      public Collection<TransactionCheck> getTransactionChecks() {
         return Collections.<TransactionCheck> singletonList(new ComodificationCheck(dataLoader.getDataLoaderFactory()));
      }
   }

   @Override
   public Callable<String> createUnsubscribeTx(ArtifactReadable userArtifact, ArtifactReadable groupArtifact) {
      return new UnsubscribeTransaction(logger, dbService, identityService, userArtifact, groupArtifact);
   }

   @Override
   public Callable<?> purgeTransactions(OrcsSession session, Collection<? extends ITransaction> transactionsToPurge) {
      return new PurgeTransactionTxCallable(logger, session, dbService, transactionsToPurge);
   }
}
