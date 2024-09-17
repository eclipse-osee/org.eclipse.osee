/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.db.internal.callable;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.data.ArchiveOperation;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class ArchiveUnarchiveBranchOperation {

   private static final String INSERT_ADDRESSING =
      "insert into %s (transaction_id, gamma_id, tx_current, mod_type, branch_id, app_id) select transaction_id, gamma_id, tx_current, mod_type, branch_id, app_id from %s where branch_id = ?";
   private static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";
   private static final String UPDATE_BRANCH = "UPDATE osee_branch SET archived = ? WHERE branch_id = ?";

   private final BranchId branch;
   private final ArchiveOperation op;
   private final JdbcClient jdbcClient;

   public ArchiveUnarchiveBranchOperation(JdbcClient jdbcClient, BranchId branch, ArchiveOperation op) {
      this.jdbcClient = jdbcClient;
      this.branch = branch;
      this.op = op;
   }

   public XResultData run() {
      boolean archive = op == ArchiveOperation.ARCHIVE;
      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

      String sql = String.format(INSERT_ADDRESSING, destinationTableName, sourceTableName);
      jdbcClient.runPreparedUpdate(sql, branch);

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      jdbcClient.runPreparedUpdate(sql, branch);

      BranchArchivedState newState = archive ? BranchArchivedState.ARCHIVED : BranchArchivedState.UNARCHIVED;
      Object[] params = new Object[] {newState, branch};
      jdbcClient.runPreparedUpdate(UPDATE_BRANCH, params);
      return XResultData.OK_STATUS;
   }
}
