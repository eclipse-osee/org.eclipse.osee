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
import org.eclipse.osee.framework.core.operation.OperationLogger;
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

   //@formatter:off
   private static final String SELECT_DELETABLE_GAMMAS =
      "SELECT txs1.gamma_id FROM %s txs1 WHERE txs1.branch_id = ? AND txs1.transaction_id <> ? AND NOT EXISTS " +
      "(SELECT txs2.branch_id FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs1.branch_id <> txs2.branch_id) AND NOT EXISTS " +
      "(SELECT txs3.branch_id FROM osee_txs_archived txs3 WHERE txs1.gamma_id = txs3.gamma_id AND txs1.branch_id <> txs3.branch_id)";
   //@formatter:on

   private static final String PURGE_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_FROM_BRANCH_TABLE = "DELETE FROM osee_branch WHERE branch_id = ?";
   private static final String DELETE_FROM_MERGE =
      "DELETE FROM osee_merge WHERE merge_branch_id = ? AND source_branch_id = ?";
   private static final String DELETE_FROM_CONFLICT = "DELETE FROM osee_conflict WHERE merge_branch_id = ?";
   private static final String DELETE_FROM_TX_DETAILS = "DELETE FROM osee_tx_details WHERE branch_id = ?";
   private final String DELETE_ARTIFACT_ACL_FROM_BRANCH = "DELETE FROM OSEE_ARTIFACT_ACL WHERE  branch_id =?";
   private final String DELETE_BRANCH_ACL_FROM_BRANCH = "DELETE FROM OSEE_BRANCH_ACL WHERE branch_id =?";

   private final Branch branch;
   private final List<Object[]> deleteableGammas = new ArrayList<Object[]>();
   private OseeConnection connection;
   private IProgressMonitor monitor;
   private final String sourceTableName;
   private final BranchCache branchCache;
   private final IOseeDatabaseService databaseService;

   public PurgeBranchOperation(OperationLogger logger, Branch branch, BranchCache branchCache, IOseeDatabaseService databaseService) {
      super(databaseService, String.format("Purge Branch: [(%s)-%s]", branch.getId(), branch.getShortName()),
         Activator.PLUGIN_ID, logger);
      this.branch = branch;
      this.sourceTableName = branch.getArchiveState().isArchived() ? "osee_txs_archived" : "osee_txs";
      this.branchCache = branchCache;
      this.databaseService = databaseService;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      this.connection = connection;
      this.monitor = monitor;

      if (branch.getStorageState() == StorageState.PURGED) {
         return;
      }

      if (!branch.getAllChildBranches(false).isEmpty()) {
         throw new OseeArgumentException("Unable to purge a branch containing children: branchGuid[%s] branchType[%s]",
            branch.getGuid(), branch.getBranchType());
      }

      monitor.worked(calculateWork(0.05));

      findDeleteableGammas(String.format(SELECT_DELETABLE_GAMMAS, sourceTableName), 0.10);

      purgeGammas("osee_artifact", 0.10);
      purgeGammas("osee_attribute", 0.10);
      purgeGammas("osee_relation_link", 0.10);

      String sql = String.format("DELETE FROM %s WHERE branch_id = ?", sourceTableName);
      purgeFromTable(sourceTableName, sql, 0.20, branch.getId());
      purgeFromTable("Tx Details", DELETE_FROM_TX_DETAILS, 0.09, branch.getId());
      purgeFromTable("Conflict", DELETE_FROM_CONFLICT, 0.01, branch.getId());
      purgeFromTable("Merge", DELETE_FROM_MERGE, 0.01, branch.getId(), branch.getParentBranch().getId());
      purgeFromTable("Branch", DELETE_FROM_BRANCH_TABLE, 0.01, branch.getId());

      branch.setStorageState(StorageState.PURGED);
      branchCache.storeItems(branch);
      branch.internalRemovePurgedBranchFromParent();

      purgeAccessControlTables(branch);
   }

   private void purgeAccessControlTables(Branch branch) throws OseeCoreException {
      databaseService.runPreparedUpdate(DELETE_ARTIFACT_ACL_FROM_BRANCH, branch.getId());
      databaseService.runPreparedUpdate(DELETE_BRANCH_ACL_FROM_BRANCH, branch.getId());
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
