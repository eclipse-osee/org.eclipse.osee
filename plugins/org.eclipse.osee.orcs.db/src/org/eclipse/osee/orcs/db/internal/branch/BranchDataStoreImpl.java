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
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.TransactionData;
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
import org.eclipse.osee.orcs.db.internal.exchange.ExportItemFactory;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataStoreImpl implements BranchDataStore {

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final IdentityService identityService;
   private final IOseeCachingService cachingService;
   private final SystemPreferences preferences;
   private final ExecutorAdmin executorAdmin;
   private final IResourceManager resourceManager;

   private final IOseeModelFactoryService modelFactory;
   private final IOseeModelingService typeModelService;

   public BranchDataStoreImpl(Log logger, IOseeDatabaseService dbService, IdentityService identityService, IOseeCachingService cachingService, SystemPreferences preferences, ExecutorAdmin executorAdmin, IResourceManager resourceManager, IOseeModelFactoryService modelFactory, IOseeModelingService typeModelService) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.cachingService = cachingService;
      this.preferences = preferences;
      this.executorAdmin = executorAdmin;
      this.resourceManager = resourceManager;
      this.modelFactory = modelFactory;
      this.typeModelService = typeModelService;
   }

   @Override
   public Callable<Branch> createBranch(String sessionId, CreateBranchData branchData) {
      return new CreateBranchDatabaseTxCallable(logger, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getBranchFactory(), modelFactory.getTransactionFactory(),
         branchData);
   }

   @Override
   public Callable<Branch> createBranchCopyTx(String sessionId, CreateBranchData branchData) {
      return new BranchCopyTxCallable(logger, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getBranchFactory(), modelFactory.getTransactionFactory(),
         branchData);
   }

   @Override
   public Callable<TransactionRecord> commitBranch(String sessionId, ArtifactReadable committer, Branch source, Branch destination) {
      return new CommitBranchDatabaseCallable(logger, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), modelFactory.getTransactionFactory(), committer, source, destination);
   }

   @Override
   public Callable<Branch> purgeBranch(String sessionId, Branch branch) {
      return new PurgeBranchDatabaseCallable(logger, dbService, cachingService.getBranchCache(), branch);
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(String sessionId, TransactionRecord sourceTx, TransactionRecord destinationTx) {
      return new CompareDatabaseCallable(logger, dbService, cachingService.getBranchCache(),
         cachingService.getTransactionCache(), sourceTx, destinationTx);
   }

   @Override
   public Callable<List<ChangeItem>> compareBranch(String sessionId, Branch branch) throws OseeCoreException {
      TransactionCache txCache = cachingService.getTransactionCache();
      return new CompareDatabaseCallable(logger, dbService, cachingService.getBranchCache(), txCache,
         branch.getBaseTransaction(), txCache.getHeadTransaction(branch));
   }

   @Override
   public Callable<URI> exportBranch(List<IOseeBranch> branches, PropertyStore options, String exportName) {
      ExportItemFactory factory =
         new ExportItemFactory(logger, dbService, cachingService, typeModelService, resourceManager);
      return new ExportBranchDatabaseCallable(factory, preferences, executorAdmin, branches, options, exportName);
   }

   @Override
   public Callable<URI> importBranch(URI fileToImport, List<IOseeBranch> branches, PropertyStore options) {
      ImportBranchDatabaseCallable callable =
         new ImportBranchDatabaseCallable(logger, dbService, preferences, cachingService, typeModelService,
            resourceManager, identityService, fileToImport, branches, options);
      return callable;
   }

   @Override
   public Callable<URI> checkBranchExchangeIntegrity(URI fileToCheck) {
      return new CheckBranchExchangeIntegrityCallable(logger, dbService, preferences, resourceManager, fileToCheck);
   }

   @Override
   public Callable<ITransaction> commitTransaction(TransactionData transaction) {
      ///// 
      // TODO:
      // 1. Make this whole method a critical region on a per branch basis - can only write to a branch on one thread at time
      // 2. This is where we will eventually check that the gammaIds have not changed from under us for: attributes, artifacts and relations
      // 3. Don't burn transaction ID until now
      // 4.
      // delete  new DeleteRelationDatabaseCallable(logger, dbService, identityService, cachingService.getBranchCache(),    branch, relationType, aArtId, bArtId, artUserId, comment);
      ////
      return null;
   }

}
