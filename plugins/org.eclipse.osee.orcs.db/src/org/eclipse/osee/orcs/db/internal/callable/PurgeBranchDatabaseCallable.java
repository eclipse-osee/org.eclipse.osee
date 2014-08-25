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

import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.BranchReadable;

/**
 * @author Megumi Telles
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class PurgeBranchDatabaseCallable extends AbstractDatastoreTxCallable<Void> {
   private static final String DELETE_FROM_BRANCH_TABLE = "DELETE FROM osee_branch WHERE branch_id = ?";
   private static final String DELETE_FROM_MERGE =
      "DELETE FROM osee_merge WHERE merge_branch_id = ? AND source_branch_id = ?";
   private static final String DELETE_FROM_CONFLICT = "DELETE FROM osee_conflict WHERE merge_branch_id = ?";
   private static final String DELETE_FROM_TX_DETAILS = "DELETE FROM osee_tx_details WHERE branch_id = ?";
   private final String DELETE_ARTIFACT_ACL_FROM_BRANCH = "DELETE FROM OSEE_ARTIFACT_ACL WHERE  branch_id =?";
   private final String DELETE_BRANCH_ACL_FROM_BRANCH = "DELETE FROM OSEE_BRANCH_ACL WHERE branch_id =?";

   private final BranchReadable toDelete;

   public PurgeBranchDatabaseCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, BranchReadable toDelete) {
      super(logger, session, databaseService, String.format("Purge Branch: [(%s)-%s]", toDelete.getUuid(),
         toDelete.getName()));
      this.toDelete = toDelete;
   }

   @Override
   protected Void handleTxWork(OseeConnection connection) throws OseeCoreException {
      String sourceTableName = toDelete.getArchiveState().isArchived() ? "osee_txs_archived" : "osee_txs";
      long branchUuid = toDelete.getUuid();
      String sql = String.format("DELETE FROM %s WHERE branch_id = ?", sourceTableName);
      purgeFromTable(connection, sql, 0.20, branchUuid);

      purgeFromTable(connection, DELETE_FROM_TX_DETAILS, 0.09, branchUuid);
      purgeFromTable(connection, DELETE_FROM_CONFLICT, 0.01, branchUuid);
      Long parentUuid = toDelete.getParentBranch();
      if (parentUuid != null) {
         purgeFromTable(connection, DELETE_FROM_MERGE, 0.01, branchUuid, parentUuid);
      }
      purgeFromTable(connection, DELETE_FROM_BRANCH_TABLE, 0.01, branchUuid);

      purgeAccessControlTables(branchUuid);
      return null;
   }

   private void purgeAccessControlTables(long branchUuid) throws OseeCoreException {
      getDatabaseService().runPreparedUpdate(DELETE_ARTIFACT_ACL_FROM_BRANCH, branchUuid);
      checkForCancelled();
      getDatabaseService().runPreparedUpdate(DELETE_BRANCH_ACL_FROM_BRANCH, branchUuid);
      checkForCancelled();
   }

   private void purgeFromTable(OseeConnection connection, String sql, double percentage, Object... data) throws OseeCoreException {
      checkForCancelled();
      getDatabaseService().runPreparedUpdate(connection, sql, data);
   }

}