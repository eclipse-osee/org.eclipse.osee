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
import org.eclipse.osee.framework.database.core.OseeConnection;

/**
 * @author Megumi Telles
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class PurgeBranchOperation extends AbstractDbTxOperation {
   private static final String DELETE_FROM_BRANCH_TABLE = "DELETE FROM osee_branch WHERE branch_id = ?";
   private static final String DELETE_FROM_MERGE =
      "DELETE FROM osee_merge WHERE merge_branch_id = ? AND source_branch_id = ?";
   private static final String DELETE_FROM_CONFLICT = "DELETE FROM osee_conflict WHERE merge_branch_id = ?";
   private static final String DELETE_FROM_TX_DETAILS = "DELETE FROM osee_tx_details WHERE branch_id = ?";
   private final String DELETE_ARTIFACT_ACL_FROM_BRANCH = "DELETE FROM OSEE_ARTIFACT_ACL WHERE  branch_id =?";
   private final String DELETE_BRANCH_ACL_FROM_BRANCH = "DELETE FROM OSEE_BRANCH_ACL WHERE branch_id =?";

   private final Branch branch;
   private final int branchId;
   private OseeConnection connection;
   private IProgressMonitor monitor;
   private final String sourceTableName;
   private final BranchCache branchCache;
   private final IOseeDatabaseService databaseService;

   public PurgeBranchOperation(OperationLogger logger, Branch branch, BranchCache branchCache, IOseeDatabaseService databaseService) {
      super(databaseService, String.format("Purge Branch: [(%s)-%s]", branch.getId(), branch.getShortName()),
         Activator.PLUGIN_ID, logger);
      this.branch = branch;
      branchId = branch.getId();
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

      String sql = String.format("DELETE FROM %s WHERE branch_id = ?", sourceTableName);
      purgeFromTable(sourceTableName, sql, 0.20, branchId);
      purgeFromTable("Tx Details", DELETE_FROM_TX_DETAILS, 0.09, branchId);
      purgeFromTable("Conflict", DELETE_FROM_CONFLICT, 0.01, branchId);
      purgeFromTable("Merge", DELETE_FROM_MERGE, 0.01, branchId, branch.getParentBranch().getId());
      purgeFromTable("Branch", DELETE_FROM_BRANCH_TABLE, 0.01, branchId);
      purgeAccessControlTables(branch);

      branch.setStorageState(StorageState.PURGED);
      branchCache.storeItems(branch);
      branch.internalRemovePurgedBranchFromParent();
   }

   private void purgeAccessControlTables(Branch branch) throws OseeCoreException {
      databaseService.runPreparedUpdate(DELETE_ARTIFACT_ACL_FROM_BRANCH, branchId);
      databaseService.runPreparedUpdate(DELETE_BRANCH_ACL_FROM_BRANCH, branchId);
   }

   private void purgeFromTable(String tableName, String sql, double percentage, Object... data) throws OseeCoreException {
      monitor.setTaskName(String.format("Purge from %s", tableName));
      checkForCancelledStatus(monitor);
      databaseService.runPreparedUpdate(connection, sql, data);
      monitor.worked(calculateWork(percentage));
   }
}