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
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
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
import org.eclipse.osee.orcs.db.internal.IdentityManager;
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
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class BranchModule {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;
   private final IdentityManager idManager;
   private final SystemPreferences preferences;
   private final ExecutorAdmin executorAdmin;
   private final IResourceManager resourceManager;

   public BranchModule(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IdentityManager idManager, SystemPreferences preferences, ExecutorAdmin executorAdmin, IResourceManager resourceManager) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.idManager = idManager;
      this.preferences = preferences;
      this.executorAdmin = executorAdmin;
      this.resourceManager = resourceManager;
   }

   public BranchDataStore createBranchDataStore(final DataLoaderFactory dataLoaderFactory) {
      final MissingChangeItemFactory missingChangeItemFactory = new MissingChangeItemFactoryImpl(dataLoaderFactory);
      return new BranchDataStore() {
         @Override
         public Callable<Void> createBranch(OrcsSession session, CreateBranchData branchData) {
            return new CreateBranchDatabaseTxCallable(logger, session, jdbcClient, idManager, branchData);
         }

         @Override
         public Callable<Void> createBranchCopyTx(OrcsSession session, CreateBranchData branchData) {
            return new BranchCopyTxCallable(logger, session, jdbcClient, joinFactory, idManager, branchData);
         }

         @Override
         public Callable<Integer> commitBranch(OrcsSession session, ArtifactReadable committer, BranchReadable source, TransactionReadable sourceHead, BranchReadable destination, TransactionReadable destinationHead) {
            return new CommitBranchDatabaseCallable(logger, session, jdbcClient, joinFactory, idManager, committer,
               source, sourceHead, destination, destinationHead, missingChangeItemFactory);
         }

         @Override
         public Callable<Void> purgeBranch(OrcsSession session, BranchReadable toDelete) {
            return new PurgeBranchDatabaseCallable(logger, session, jdbcClient, toDelete);
         }

         @Override
         public Callable<List<ChangeItem>> compareBranch(OrcsSession session, TransactionReadable sourceTx, TransactionReadable destinationTx) {
            return new CompareDatabaseCallable(logger, session, jdbcClient, joinFactory, sourceTx, destinationTx,
               missingChangeItemFactory);
         }

         @Override
         public Callable<URI> exportBranch(OrcsSession session, OrcsTypes orcsTypes, List<IOseeBranch> branches, PropertyStore options, String exportName) {
            ExportItemFactory factory =
               new ExportItemFactory(logger, preferences, jdbcClient, resourceManager, orcsTypes);
            return new ExportBranchDatabaseCallable(session, factory, joinFactory, preferences, executorAdmin,
               branches, options, exportName);
         }

         @Override
         public Callable<URI> importBranch(OrcsSession session, OrcsTypes orcsTypes, URI fileToImport, List<IOseeBranch> branches, PropertyStore options) {
            ImportBranchDatabaseCallable callable =
               new ImportBranchDatabaseCallable(logger, session, jdbcClient, preferences, resourceManager, idManager,
                  orcsTypes, fileToImport, branches, options);
            return callable;
         }

         @Override
         public Callable<URI> checkBranchExchangeIntegrity(OrcsSession session, URI fileToCheck) {
            return new CheckBranchExchangeIntegrityCallable(logger, session, jdbcClient, preferences, resourceManager,
               fileToCheck);
         }

         @Override
         public Callable<Void> changeBranchState(OrcsSession session, IOseeBranch branch, BranchState newState) {
            return new ChangeBranchStateCallable(logger, session, jdbcClient, branch, newState);
         }

         @Override
         public Callable<Void> changeBranchType(OrcsSession session, IOseeBranch branch, BranchType newType) {
            return new ChangeBranchTypeCallable(logger, session, jdbcClient, branch, newType);
         }

         @Override
         public Callable<Void> archiveUnArchiveBranch(OrcsSession session, IOseeBranch branch, ArchiveOperation op) {
            return new ArchiveUnarchiveBranchCallable(logger, session, jdbcClient, branch, op);
         }

         @Override
         public Callable<Void> deleteBranch(OrcsSession session, IOseeBranch branch) {
            AbstractDatastoreTxCallable<?> deleteBranch =
               (AbstractDatastoreTxCallable<?>) changeBranchState(session, branch, BranchState.DELETED);
            AbstractDatastoreTxCallable<?> archiveBranch =
               (AbstractDatastoreTxCallable<?>) archiveUnArchiveBranch(session, branch, ArchiveOperation.ARCHIVE);
            CompositeDatastoreTxCallable composite =
               new CompositeDatastoreTxCallable(logger, session, jdbcClient, deleteBranch, archiveBranch);
            return composite;
         }

      };
   }
}
