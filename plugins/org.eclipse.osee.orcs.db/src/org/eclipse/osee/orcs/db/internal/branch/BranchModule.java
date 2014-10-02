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
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ArchiveUnarchiveBranchCallable;
import org.eclipse.osee.orcs.db.internal.callable.BranchCopyTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ChangeBranchStateCallable;
import org.eclipse.osee.orcs.db.internal.callable.ChangeBranchTypeCallable;
import org.eclipse.osee.orcs.db.internal.callable.CheckBranchExchangeIntegrityCallable;
import org.eclipse.osee.orcs.db.internal.callable.CommitBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompareDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompositeDatastoreTxCallable;
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
   private final SystemPreferences preferences;
   private final ExecutorAdmin executorAdmin;
   private final IResourceManager resourceManager;

   public BranchModule(Log logger, IOseeDatabaseService dbService, IdentityLocator identityService, SystemPreferences preferences, ExecutorAdmin executorAdmin, IResourceManager resourceManager) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.identityService = identityService;
      this.preferences = preferences;
      this.executorAdmin = executorAdmin;
      this.resourceManager = resourceManager;
   }

   public BranchDataStore createBranchDataStore(final DataLoaderFactory dataLoaderFactory) {
      final MissingChangeItemFactory missingChangeItemFactory = new MissingChangeItemFactoryImpl(dataLoaderFactory);
      return new BranchDataStore() {
         @Override
         public Callable<Void> createBranch(OrcsSession session, CreateBranchData branchData) {
            return new CreateBranchDatabaseTxCallable(logger, session, dbService, branchData);
         }

         @Override
         public Callable<Void> createBranchCopyTx(OrcsSession session, CreateBranchData branchData) {
            return new BranchCopyTxCallable(logger, session, dbService, branchData);
         }

         @Override
         public Callable<Integer> commitBranch(OrcsSession session, ArtifactReadable committer, BranchReadable source, TransactionReadable sourceHead, BranchReadable destination, TransactionReadable destinationHead) {
            return new CommitBranchDatabaseCallable(logger, session, dbService, committer, source, sourceHead,
               destination, destinationHead, missingChangeItemFactory);
         }

         @Override
         public Callable<Void> purgeBranch(OrcsSession session, BranchReadable toDelete) {
            return new PurgeBranchDatabaseCallable(logger, session, dbService, toDelete);
         }

         @Override
         public Callable<List<ChangeItem>> compareBranch(OrcsSession session, TransactionReadable sourceTx, TransactionReadable destinationTx) {
            return new CompareDatabaseCallable(logger, session, dbService, sourceTx, destinationTx,
               missingChangeItemFactory);
         }

         @Override
         public Callable<URI> exportBranch(OrcsSession session, OrcsTypes orcsTypes, List<IOseeBranch> branches, PropertyStore options, String exportName) {
            ExportItemFactory factory = new ExportItemFactory(logger, dbService, resourceManager, orcsTypes);
            return new ExportBranchDatabaseCallable(session, factory, preferences, executorAdmin, branches, options,
               exportName);
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

         @Override
         public Callable<Void> changeBranchState(OrcsSession session, IOseeBranch branch, BranchState newState) {
            return new ChangeBranchStateCallable(logger, session, dbService, branch, newState);
         }

         @Override
         public Callable<Void> changeBranchType(OrcsSession session, IOseeBranch branch, BranchType newType) {
            return new ChangeBranchTypeCallable(logger, session, dbService, branch, newType);
         }

         @Override
         public Callable<Void> archiveUnArchiveBranch(OrcsSession session, IOseeBranch branch, ArchiveOperation op) {
            return new ArchiveUnarchiveBranchCallable(logger, session, dbService, branch, op);
         }

         @Override
         public Callable<Void> deleteBranch(OrcsSession session, IOseeBranch branch) {
            AbstractDatastoreTxCallable<?> deleteBranch =
               (AbstractDatastoreTxCallable<?>) changeBranchState(session, branch, BranchState.DELETED);
            AbstractDatastoreTxCallable<?> archiveBranch =
               (AbstractDatastoreTxCallable<?>) archiveUnArchiveBranch(session, branch, ArchiveOperation.ARCHIVE);
            CompositeDatastoreTxCallable composite =
               new CompositeDatastoreTxCallable(logger, session, dbService,
                  String.format("Delete Branch [%s]", branch), deleteBranch, archiveBranch);
            return composite;
         }

      };
   }
}
