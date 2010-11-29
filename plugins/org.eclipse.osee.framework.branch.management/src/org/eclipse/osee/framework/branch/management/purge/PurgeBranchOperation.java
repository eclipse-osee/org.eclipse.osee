/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.branch.management.purge;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;

/**
 * @author Megumi Telles
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class PurgeBranchOperation extends AbstractDbTxOperation {
   private static final String COUNT_CHILD_BRANCHES = "select count(1) from osee_branch WHERE parent_branch_id = ?";
   private static final String SELECT_DELETABLE_GAMMAS =
      "select txs1.gamma_id from %s txs1 where txs1.branch_id = ? AND txs1.transaction_id <> ? AND NOT EXISTS (SELECT 1 FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs1.branch_id <> txs2.branch_id) AND NOT EXISTS (SELECT 1 FROM osee_txs_archived txs3 WHERE txs1.gamma_id = txs3.gamma_id AND txs1.branch_id <> txs3.branch_id)";
   private static final String SELECT_DELETABLE_TXS_REMOVED_GAMMAS =
      "select txs1.rem_gamma_id from osee_removed_txs txs1, osee_tx_details txd1 where txd1.branch_id = ? AND txs1.transaction_id <> ? AND txs1.transaction_id = txd1.transaction_id AND NOT EXISTS (SELECT 1 FROM osee_txs txs2 WHERE txs1.rem_gamma_id = txs2.gamma_id AND txd1.branch_id <> txs2.branch_id) AND NOT EXISTS (SELECT 1 FROM osee_txs_archived txs3 WHERE txs1.rem_gamma_id = txs3.gamma_id AND txd1.branch_id <> txs3.branch_id)";
   private static final String PURGE_GAMMAS = "delete from %s where gamma_id = ?";
   private static final String DELETE_FROM_BRANCH_TABLE = "delete from osee_branch where branch_id = ?";
   private static final String DELETE_FROM_MERGE =
      "delete from osee_merge where merge_branch_id = ? and source_branch_id=?";
   private static final String DELETE_FROM_CONFLICT = "delete from osee_conflict where merge_branch_id = ?";
   private static final String DELETE_FROM_TX_DETAILS = "delete from osee_tx_details where branch_id = ?";

   private final Branch branch;
   private final List<Object[]> deleteableGammas = new ArrayList<Object[]>();
   private OseeConnection connection;
   private IProgressMonitor monitor;
   private final String sourceTableName;
   private final IOseeCachingService cachingService;
   private final IOseeDatabaseService databaseService;

   public PurgeBranchOperation(Branch branch, IOseeCachingService cachingService, IOseeDatabaseService databaseService) {
      super(databaseService, String.format("Purge Branch: [(%s)-%s]", branch.getId(), branch.getShortName()),
         Activator.PLUGIN_ID);
      this.branch = branch;
      this.sourceTableName = branch.getArchiveState().isArchived() ? "osee_txs_archived" : "osee_txs";
      this.cachingService = cachingService;
      this.databaseService = databaseService;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      this.connection = connection;
      this.monitor = monitor;
      int numberOfChildren =
         databaseService.runPreparedQueryFetchObject(connection, 0, COUNT_CHILD_BRANCHES, branch.getId());
      if (numberOfChildren > 0) {
         throw new OseeArgumentException("Unable to purge a branch containing children: branchId[%s]", branch.getId());
      }

      monitor.worked(calculateWork(0.05));

      findDeleteableGammas(SELECT_DELETABLE_TXS_REMOVED_GAMMAS, 0.10);
      findDeleteableGammas(String.format(SELECT_DELETABLE_GAMMAS, sourceTableName), 0.10);

      purgeGammas("osee_artifact", 0.10);
      purgeGammas("osee_attribute", 0.10);
      purgeGammas("osee_relation_link", 0.10);

      String sql = String.format("delete from %s where branch_id = ?", sourceTableName);
      purgeFromTable(sourceTableName, sql, 0.20, branch.getId());
      purgeFromTable("Tx Details", DELETE_FROM_TX_DETAILS, 0.09, branch.getId());
      purgeFromTable("Conflict", DELETE_FROM_CONFLICT, 0.01, branch.getId());
      purgeFromTable("Merge", DELETE_FROM_MERGE, 0.01, branch.getId(), branch.getParentBranch().getId());
      purgeFromTable("Branch", DELETE_FROM_BRANCH_TABLE, 0.01, branch.getId());

      BranchCache branchCache = cachingService.getBranchCache();
      branch.setStorageState(StorageState.PURGED);
      branchCache.storeItems(branch);
      branchCache.decache(branch);
   }

   private void purgeGammas(String tableName, double percentage) throws OseeCoreException {
      if (!deleteableGammas.isEmpty()) {
         monitor.setTaskName(String.format("Purge from %s", tableName));
         checkForCancelledStatus(monitor);
         String sql = String.format(PURGE_GAMMAS, tableName);
         databaseService.runBatchUpdate(connection, sql, deleteableGammas);
      }
      monitor.worked(calculateWork(percentage));
   }

   private void purgeFromTable(String tableName, String sql, double percentage, Object... data) throws OseeCoreException {
      monitor.setTaskName(String.format("Purge from %s", tableName));
      checkForCancelledStatus(monitor);
      databaseService.runPreparedUpdate(connection, sql, data);
      monitor.worked(calculateWork(percentage));
   }

   private void findDeleteableGammas(String sql, double percentage) throws OseeCoreException {
      IOseeStatement chStmt = databaseService.getStatement(connection);
      try {
         chStmt.runPreparedQuery(10000, sql, branch.getId(), branch.getBaseTransaction().getId());
         while (chStmt.next()) {
            deleteableGammas.add(new Object[] {chStmt.getLong(1)});
         }
      } finally {
         chStmt.close();
         monitor.worked(calculateWork(percentage));
      }
   }
}