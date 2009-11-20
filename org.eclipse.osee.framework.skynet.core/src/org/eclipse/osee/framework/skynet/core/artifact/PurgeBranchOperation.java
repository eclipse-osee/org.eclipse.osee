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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class PurgeBranchOperation extends AbstractDbTxOperation {
   private static final String COUNT_CHILD_BRANCHES = "select count(1) from osee_branch WHERE parent_branch_id = ?";

   private static final String SELECT_DELETABLE_GAMMAS =
         "select %s from osee_tx_details txd1, %s txs1 where txd1.branch_id = ? AND txd1.tx_type = %d AND txd1.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 1 FROM osee_txs txs2, osee_tx_details txd2 WHERE txs1.%s = txs2.gamma_id AND txd2.transaction_id = txs2.transaction_id AND txd1.branch_id <> txd2.branch_id)AND NOT EXISTS (SELECT 1 FROM osee_txs_archived txs3, osee_tx_details txd3 WHERE txs1.%s = txs3.gamma_id AND txd3.transaction_id = txs3.transaction_id AND txd1.branch_id <> txd3.branch_id)";

   public static final String TEST_TXS =
         "select count(1) from osee_tx_details txd where txd.branch_id = ? AND txd.tx_type = 1 AND exists (select 1 from osee_txs txs where txd.transaction_id = txs.transaction_id)";

   private static final String PURGE_GAMMAS = "delete from %s where gamma_id = ?";

   private static final String DELETE_FROM_ARTIFACT =
         "delete from osee_artifact art where not exists (select 1 from osee_artifact_version arv where arv.art_id = art.art_id)";

   private static final String DELETE_FROM_TX_DETAILS = "delete from osee_tx_details where branch_id = ?";
   private static final String DELETE_FROM_BRANCH_TABLE = "delete from osee_branch where branch_id = ?";

   public static final String SELECT_ADDRESSING_BY_BRANCH =
         "select txd.transaction_id, gamma_id from %s txs, osee_tx_details txd where txs.transaction_id = txd.transaction_id and txd.branch_id = ?";

   private final Branch branch;
   private final List<Object[]> deleteableGammas = new ArrayList<Object[]>();
   private OseeConnection connection;
   private IProgressMonitor monitor;
   private String sourceTableName;

   /**
    * @param name
    * @param branch
    */
   public PurgeBranchOperation(Branch branch) {
      super(null, String.format("Purge Branch: [(%s)-%s]", branch.getId(), branch.getShortName()), Activator.PLUGIN_ID);
      this.branch = branch;
      this.sourceTableName = branch.getArchiveState().isArchived() ? "osee_txs_archived" : "osee_txs";
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      this.connection = connection;
      this.monitor = monitor;
      int numberOfChildren =
            ConnectionHandler.runPreparedQueryFetchInt(connection, 0, COUNT_CHILD_BRANCHES, branch.getId());
      if (numberOfChildren > 0) {
         throw new OseeArgumentException("Unable to purge a branch containing children");
      }

      if (ConnectionHandler.runPreparedQueryFetchInt(connection, 0, TEST_TXS, branch.getId()) == 1) {
         sourceTableName = "osee_txs";
      } else {
         sourceTableName = "osee_txs_archived";
      }
      monitor.worked(calculateWork(0.05));

      findDeleteableGammas("osee_removed_txs", "rem_gamma_id", 0.10);
      findDeleteableGammas(sourceTableName, "gamma_id", 0.10);

      purgeGammas("osee_artifact_version", 0.10);
      purgeGammas("osee_attribute", 0.10);
      purgeGammas("osee_relation_link", 0.10);

      purgeFromTable("Artifact", DELETE_FROM_ARTIFACT, 0.10);

      purgeAddressing(0.20);

      purgeFromTable("Tx Details", DELETE_FROM_TX_DETAILS, 0.09, branch.getId());
      purgeFromTable("Branch", DELETE_FROM_BRANCH_TABLE, 0.01, branch.getId());
   }

   @Override
   protected void doFinally(IProgressMonitor monitor) {
      super.doFinally(monitor);
      if (getStatus().isOK()) {
         try {
            BranchManager.decache(branch);
            try {
               OseeEventManager.kickBranchEvent(this, BranchEventType.Purged, branch.getId());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      monitor.worked(calculateWork(0.05));
   }

   private void purgeGammas(String tableName, double percentage) throws OseeDataStoreException {
      if (!deleteableGammas.isEmpty()) {
         monitor.setTaskName(String.format("Purge from %s", tableName));
         checkForCancelledStatus(monitor);
         String sql = String.format(PURGE_GAMMAS, tableName);
         ConnectionHandler.runBatchUpdate(connection, sql, deleteableGammas);
      }
      monitor.worked(calculateWork(percentage));
   }

   private void purgeFromTable(String tableName, String sql, double percentage, Object... data) throws OseeDataStoreException {
      monitor.setTaskName(String.format("Purge from %s", tableName));
      checkForCancelledStatus(monitor);
      ConnectionHandler.runPreparedUpdate(connection, sql, data);
      monitor.worked(calculateWork(percentage));
   }

   private void findDeleteableGammas(String sourceTableName, String columnName, double percentage) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      String sql =
            String.format(SELECT_DELETABLE_GAMMAS, columnName, sourceTableName,
                  TransactionDetailsType.NonBaselined.getId(), columnName, columnName);

      try {
         chStmt.runPreparedQuery(10000, sql, branch.getId());
         while (chStmt.next()) {
            deleteableGammas.add(new Object[] {chStmt.getLong(columnName)});
         }
      } finally {
         chStmt.close();
         monitor.worked(calculateWork(percentage));
      }
   }

   private void purgeAddressing(double percentage) throws OseeDataStoreException {
      monitor.setTaskName("Purge txs addressing");
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      List<Object[]> addressing = new ArrayList<Object[]>();
      String sql = String.format(SELECT_ADDRESSING_BY_BRANCH, sourceTableName);

      try {
         chStmt.runPreparedQuery(10000, sql, branch.getId());
         while (chStmt.next()) {
            addressing.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }

      sql = String.format("delete from %s where transaction_id = ? and gamma_id = ?", sourceTableName);
      ConnectionHandler.runBatchUpdate(connection, sql, addressing);
      monitor.worked(calculateWork(percentage));
   }
}