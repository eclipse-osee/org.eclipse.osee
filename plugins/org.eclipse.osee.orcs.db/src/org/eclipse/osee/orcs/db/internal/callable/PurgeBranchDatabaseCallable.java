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

package org.eclipse.osee.orcs.db.internal.callable;

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Megumi Telles
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class PurgeBranchDatabaseCallable extends AbstractDatastoreTxCallable<Branch> {
   private static final String DELETE_FROM_BRANCH_TABLE = "DELETE FROM osee_branch WHERE branch_id = ?";
   private static final String DELETE_FROM_MERGE =
      "DELETE FROM osee_merge WHERE merge_branch_id = ? AND source_branch_id = ?";
   private static final String DELETE_FROM_CONFLICT = "DELETE FROM osee_conflict WHERE merge_branch_id = ?";
   private static final String DELETE_FROM_TX_DETAILS = "DELETE FROM osee_tx_details WHERE branch_id = ?";
   private final String DELETE_ARTIFACT_ACL_FROM_BRANCH = "DELETE FROM OSEE_ARTIFACT_ACL WHERE  branch_id =?";
   private final String DELETE_BRANCH_ACL_FROM_BRANCH = "DELETE FROM OSEE_BRANCH_ACL WHERE branch_id =?";

   private final Branch branch;
   private final String sourceTableName;
   private final BranchCache branchCache;

   public PurgeBranchDatabaseCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, BranchCache branchCache, Branch branch) {
      super(logger, session, databaseService, String.format("Purge Branch: [(%s)-%s]", branch.getId(),
         branch.getShortName()));
      this.branch = branch;
      this.sourceTableName = branch.getArchiveState().isArchived() ? "osee_txs_archived" : "osee_txs";
      this.branchCache = branchCache;
   }

   @Override
   protected Branch handleTxWork(OseeConnection connection) throws OseeCoreException {
      if (branch.getStorageState() != StorageState.PURGED) {
         if (!branch.getAllChildBranches(false).isEmpty()) {
            throw new OseeArgumentException(
               "Unable to purge a branch containing children: branchGuid[%s] branchType[%s]", branch.getGuid(),
               branch.getBranchType());
         }

         int branchId = branch.getId();
         String sql = String.format("DELETE FROM %s WHERE branch_id = ?", sourceTableName);
         purgeFromTable(connection, sql, 0.20, branchId);

         purgeFromTable(connection, DELETE_FROM_TX_DETAILS, 0.09, branchId);
         purgeFromTable(connection, DELETE_FROM_CONFLICT, 0.01, branchId);
         if (branch.hasParentBranch()) {
            purgeFromTable(connection, DELETE_FROM_MERGE, 0.01, branchId, branch.getParentBranch().getId());
         }
         purgeFromTable(connection, DELETE_FROM_BRANCH_TABLE, 0.01, branchId);

         purgeAccessControlTables(branchId);

         branch.setStorageState(StorageState.PURGED);
         branchCache.storeItems(branch);
         branch.setBranchState(BranchState.PURGED);
         branch.internalRemovePurgedBranchFromParent();
      }
      return branch;
   }

   private void purgeAccessControlTables(int branchId) throws OseeCoreException {
      getDatabaseService().runPreparedUpdate(DELETE_ARTIFACT_ACL_FROM_BRANCH, branchId);
      checkForCancelled();
      getDatabaseService().runPreparedUpdate(DELETE_BRANCH_ACL_FROM_BRANCH, branchId);
      checkForCancelled();
   }

   private void purgeFromTable(OseeConnection connection, String sql, double percentage, Object... data) throws OseeCoreException {
      checkForCancelled();
      getDatabaseService().runPreparedUpdate(connection, sql, data);
   }

}