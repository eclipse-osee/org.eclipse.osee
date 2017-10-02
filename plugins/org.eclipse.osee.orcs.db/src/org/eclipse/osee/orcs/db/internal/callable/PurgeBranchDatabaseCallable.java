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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
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
   private static final String DELETE_FROM_MERGE = "DELETE FROM osee_merge WHERE merge_branch_id = ?";
   private static final String DELETE_FROM_CONFLICT = "DELETE FROM osee_conflict WHERE merge_branch_id = ?";
   private static final String DELETE_FROM_TX_DETAILS = "DELETE FROM osee_tx_details WHERE branch_id = ?";
   private final String DELETE_ARTIFACT_ACL_FROM_BRANCH = "DELETE FROM OSEE_ARTIFACT_ACL WHERE  branch_id =?";
   private final String SELECT_MERGE_BRANCHES =
      "SELECT merge_branch_id, archived FROM osee_merge, osee_branch where merge_branch_id = branch_id and (source_branch_id = ? or dest_branch_id = ?)";
   private static final String TEMPORARY_BRANCH_UPDATE =
      "UPDATE osee_branch SET baseline_transaction_id = 1 WHERE branch_id = ?";
   private final BranchReadable toDelete;

   public PurgeBranchDatabaseCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, BranchReadable toDelete) {
      super(logger, session, jdbcClient);
      this.toDelete = toDelete;
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection)  {
      List<Pair<BranchId, Boolean>> branches = findMergeBranches(connection);
      branches.add(new Pair<BranchId, Boolean>(toDelete, toDelete.getArchiveState().isArchived()));
      for (Pair<BranchId, Boolean> toPurge : branches) {
         purgeBranch(connection, toPurge.getFirst(), toPurge.getSecond());
      }
      return null;
   }

   private void purgeBranch(JdbcConnection connection, BranchId branch, boolean isArchived) {
      String sourceTableName = isArchived ? "osee_txs_archived" : "osee_txs";
      String sql = String.format("DELETE FROM %s WHERE branch_id = ?", sourceTableName);
      purgeFromTable(connection, sql, 0.20, branch);

      if (getJdbcClient().getDbType().equals(JdbcDbType.hsql) || getJdbcClient().getDbType().equals(JdbcDbType.mysql)) {
         // update the branch table so that the baseline transaction ID of the given branch is '1'
         purgeFromTable(connection, TEMPORARY_BRANCH_UPDATE, 0.01, branch);
      }

      purgeFromTable(connection, DELETE_FROM_TX_DETAILS, 0.09, branch);
      purgeFromTable(connection, DELETE_FROM_CONFLICT, 0.01, branch);
      purgeFromTable(connection, DELETE_FROM_MERGE, 0.01, branch);
      purgeFromTable(connection, DELETE_FROM_BRANCH_TABLE, 0.01, branch);
      purgeFromTable(connection, DELETE_ARTIFACT_ACL_FROM_BRANCH, 0.01, branch);
   }

   private List<Pair<BranchId, Boolean>> findMergeBranches(JdbcConnection connection) {
      List<Pair<BranchId, Boolean>> toReturn = new LinkedList<>();
      getJdbcClient().runQuery(connection,
         stmt -> toReturn.add(
            new Pair<>(BranchId.valueOf(stmt.getLong("merge_branch_id")), stmt.getBoolean("archived"))),
         SELECT_MERGE_BRANCHES, toDelete, toDelete);
      return toReturn;
   }

   private void purgeFromTable(JdbcConnection connection, String sql, double percentage, Object... data)  {
      checkForCancelled();
      getJdbcClient().runPreparedUpdate(connection, sql, data);
   }

}