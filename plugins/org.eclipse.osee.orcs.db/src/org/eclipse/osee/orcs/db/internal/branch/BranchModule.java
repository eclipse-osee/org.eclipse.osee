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

package org.eclipse.osee.orcs.db.internal.branch;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ArchiveUnarchiveBranchCallable;
import org.eclipse.osee.orcs.db.internal.callable.BranchCopyTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.BranchInheritACLCallable;
import org.eclipse.osee.orcs.db.internal.callable.ChangeBranchFieldCallable;
import org.eclipse.osee.orcs.db.internal.callable.CommitBranchDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompositeDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.CreateBranchDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ExportBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.ImportBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.change.LoadDeltasBetweenTxsOnTheSameBranch;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactoryImpl;
import org.eclipse.osee.orcs.db.internal.exchange.ExportItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public class BranchModule {
   private final String UPDATE_BRANCH_ACL =
      "UPDATE OSEE_BRANCH_ACL SET permission_id = ? WHERE privilege_entity_id = ? AND branch_id = ?";
   private final String GET_BRANCH_PERMISSION =
      "SELECT permission_id FROM osee_branch_acl WHERE privilege_entity_id = ? AND branch_id = ?";
   private static final String COPY_APPLIC =
      "INSERT INTO osee_txs (branch_id, gamma_id, transaction_id, tx_current, mod_type, app_id)\n" + "with cte as (select branch_id as chid, baseline_transaction_id as chtx, parent_branch_id as pid from osee_branch where branch_id = ?)\n" + "select chid, txsP.gamma_id, chtx, tx_current, mod_type, app_id from cte, osee_tuple2 t2, osee_txs txsP where tuple_type = 2 and t2.gamma_id = txsP.gamma_id and txsP.branch_id = pid and txsP.tx_current =1 and not exists (select 1 from osee_txs txsC where txsC.branch_id = chid and txsC.gamma_id = txsP.gamma_id)";

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;
   private final IdentityManager idManager;
   private final SystemProperties preferences;
   private final ExecutorAdmin executorAdmin;
   private final IResourceManager resourceManager;

   public BranchModule(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IdentityManager idManager, SystemProperties preferences, ExecutorAdmin executorAdmin, IResourceManager resourceManager) {
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
         public void addMissingApplicabilityFromParentBranch(BranchId branch) {
            jdbcClient.runPreparedUpdate(COPY_APPLIC, branch);
         }

         @Override
         public void createBranch(CreateBranchData branchData, UserService userService) {
            jdbcClient.runTransaction(new CreateBranchDatabaseTxCallable(jdbcClient, idManager, userService, branchData,
               OseeCodeVersion.getVersionId()));
            if (branchData.isInheritAccess()) {
               jdbcClient.runTransaction(new BranchInheritACLCallable(jdbcClient, branchData));
            }
         }

         @Override
         public XResultData createBranchValidation(CreateBranchData branchData, UserService userService) {
            return new CreateBranchDatabaseTxCallable(jdbcClient, idManager, userService, branchData,
               OseeCodeVersion.getVersionId()).checkPreconditions(jdbcClient.getConnection());
         }

         @Override
         public void createBranchCopyTx(CreateBranchData branchData, UserService userService) {
            jdbcClient.runTransaction(new BranchCopyTxCallable(jdbcClient, idManager, userService, branchData,
               OseeCodeVersion.getVersionId()));
         }

         @Override
         public TransactionId commitBranch(OrcsSession session, ArtifactId committer, OrcsTokenService tokenService,
            Branch source, TransactionToken sourceTx, Branch destination, TransactionToken destinationTx,
            OrcsApi orcsApi) {
            BranchId mergeBranch = getMergeBranchId(orcsApi.getQueryFactory().branchQuery(), source, destination);

            try {
               return new CommitBranchDatabaseTxCallable(idManager, committer, jdbcClient, joinFactory, tokenService,
                  source, destination, sourceTx, destinationTx, mergeBranch, orcsApi, missingChangeItemFactory).call();
            } catch (Exception ex) {
               throw OseeCoreException.wrap(ex);
            }
         }

         @Override
         public Callable<Void> purgeBranch(OrcsSession session, Branch toDelete) {
            return new PurgeBranchDatabaseCallable(logger, session, jdbcClient, toDelete);
         }

         @Override
         public List<ChangeItem> compareBranch(OrcsSession session, OrcsTokenService tokenService,
            TransactionToken sourceTx, TransactionToken destinationTx, OrcsApi orcsApi) {
            BranchId mergeBranch = getMergeBranchId(orcsApi.getQueryFactory().branchQuery(), sourceTx.getBranch(),
               destinationTx.getBranch());
            return new LoadDeltasBetweenTxsOnTheSameBranch(jdbcClient, joinFactory, tokenService, sourceTx,
               destinationTx, mergeBranch, orcsApi, missingChangeItemFactory).compareTransactions();
         }

         private BranchId getMergeBranchId(BranchQuery branchQuery, BranchId source, BranchId destination) {
            if (branchQuery.andIsMergeFor(source, destination) == null) {
               return BranchId.SENTINEL;
            }
            BranchId mergeBranch =
               branchQuery.andIsMergeFor(source, destination).getResults().getAtMostOneOrDefault(Branch.SENTINEL);

            if (mergeBranch.isInvalid()) {
               mergeBranch = BranchId.SENTINEL;
            }
            return mergeBranch;
         }

         @Override
         public Callable<URI> exportBranch(OrcsSession session, List<? extends BranchId> branches,
            PropertyStore options, String exportName) {
            ExportItemFactory factory = new ExportItemFactory(logger, preferences, jdbcClient, resourceManager);
            return new ExportBranchDatabaseCallable(session, factory, joinFactory, preferences, executorAdmin, branches,
               options, exportName);
         }

         @Override
         public Callable<URI> importBranch(OrcsSession session, URI fileToImport, List<? extends BranchId> branches,
            PropertyStore options) {
            ImportBranchDatabaseCallable callable = new ImportBranchDatabaseCallable(logger, session, jdbcClient,
               preferences, resourceManager, fileToImport, branches, options);
            return callable;
         }

         @Override
         public Callable<Void> changeBranchState(OrcsSession session, BranchId branch, BranchState branchState) {
            return ChangeBranchFieldCallable.newBranchState(logger, session, jdbcClient, branch, branchState);
         }

         @Override
         public Callable<Void> changeBranchType(OrcsSession session, BranchId branch, BranchType branchType) {
            return ChangeBranchFieldCallable.newBranchType(logger, session, jdbcClient, branch, branchType);
         }

         @Override
         public Callable<Void> changeBranchName(OrcsSession session, BranchId branch, String branchName) {
            return ChangeBranchFieldCallable.newBranchName(logger, session, jdbcClient, branch, branchName);
         }

         @Override
         public Callable<Void> changeBranchAssociatedArt(OrcsSession session, BranchId branch, ArtifactId assocArt) {
            return ChangeBranchFieldCallable.newAssocArtId(logger, session, jdbcClient, branch, assocArt);
         }

         @Override
         public Callable<Void> archiveUnArchiveBranch(OrcsSession session, BranchId branch, ArchiveOperation op) {
            return new ArchiveUnarchiveBranchCallable(logger, session, jdbcClient, branch, op);
         }

         @Override
         public Callable<Void> deleteBranch(OrcsSession session, BranchId branch) {
            AbstractDatastoreTxCallable<?> deleteBranch =
               (AbstractDatastoreTxCallable<?>) changeBranchState(session, branch, BranchState.DELETED);
            AbstractDatastoreTxCallable<?> archiveBranch =
               (AbstractDatastoreTxCallable<?>) archiveUnArchiveBranch(session, branch, ArchiveOperation.ARCHIVE);
            CompositeDatastoreTxCallable composite =
               new CompositeDatastoreTxCallable(logger, session, jdbcClient, deleteBranch, archiveBranch);
            return composite;
         }

         @Override
         public void setBranchPermission(ArtifactId subject, BranchId branch, PermissionEnum permission) {
            int existingPermission = jdbcClient.fetch(-1, GET_BRANCH_PERMISSION, subject, branch);
            if (existingPermission == -1) {
               jdbcClient.runPreparedUpdate(OseeDb.OSEE_BRANCH_ACL_TABLE.getInsertSql(), branch, subject,
                  permission.getPermId());
            } else {
               jdbcClient.runPreparedUpdate(UPDATE_BRANCH_ACL, permission.getPermId(), subject, branch);
            }
         }
      };
   }
}