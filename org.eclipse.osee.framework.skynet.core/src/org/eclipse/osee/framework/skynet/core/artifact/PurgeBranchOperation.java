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

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.AbstractDbTxOperation;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class PurgeBranchOperation extends AbstractDbTxOperation {
   private static final String COUNT_CHILD_BRANCHES =
         "select count(branch_id) as child_branches from osee_branch WHERE parent_branch_id = ?";

   private static final String SEARCH_FOR_DELETABLE_GAMMAS =
         "select ?, gamma_id from osee_tx_details det1, osee_txs txs1 where det1.branch_id = ? AND det1.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 'not_matter' FROM OSEE_TX_DETAILS det2, OSEE_TXS txs2 WHERE txs1.gamma_id = txs2.gamma_id AND det2.transaction_id = txs2.transaction_id AND det1.branch_id <> det2.branch_id)";

   private static final String POPULATE_BRANCH_DELETE_HELPER_WITH_GAMMAS =
         "insert into osee_branch_delete_helper (branch_id, gamma_id) " + SEARCH_FOR_DELETABLE_GAMMAS;

   private static final String SEARCH_FOR_REMOVED_DELETABLE_GAMMAS =
         "select ?, rem_gamma_id AS gamma_id from osee_tx_details det1, OSEE_REMOVED_TXS txs1 WHERE det1.branch_id = ? AND det1.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 'not_matter' FROM OSEE_TX_DETAILS det2, OSEE_TXS txs2 WHERE txs1.rem_gamma_id = txs2.gamma_id AND det2.transaction_id = txs2.transaction_id AND det1.branch_id <> det2.branch_id)";

   private static final String POPULATE_BRANCH_DELETE_HELPER_WITH_REMOVED_GAMMAS =
         "insert into osee_branch_delete_helper (branch_id, gamma_id) " + SEARCH_FOR_REMOVED_DELETABLE_GAMMAS;

   private static final String IN_HELPER_TABLE =
         " where exists (select obdh.gamma_id from osee_branch_delete_helper obdh where obdh.gamma_id = item.gamma_id and obdh.branch_id = ?)";

   private static final String PURGE_ATTRIBUTE_VERSIONS = "delete from osee_attribute item " + IN_HELPER_TABLE;

   private static final String PURGE_RELATION_VERSIONS = "delete from osee_relation_link item " + IN_HELPER_TABLE;

   private static final String PURGE_ARTIFACT_VERSIONS = "delete from osee_artifact_version item" + IN_HELPER_TABLE;

   private static final String DELETE_FROM_BRANCH_TABLE = "delete from osee_branch where branch_id = ?";

   private static final String DELETE_FROM_ARTIFACT =
         "delete from osee_artifact item where exists (select oa.art_id from osee_artifact oa where oa.art_id = item.art_id %s select oav.art_id from osee_artifact_version oav)";

   private static final String DELETE_FROM_BRANCH_DELETE_HELPER =
         "delete from osee_branch_delete_helper where branch_id = ?";

   private final Branch branch;

   /**
    * @param name
    * @param branch
    */
   public PurgeBranchOperation(Branch branch) {
      super(String.format("Purge Branch: [(%s)-%s]", branch.getBranchId(), branch.getBranchName()), Activator.PLUGIN_ID);
      this.branch = branch;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractDbTxOperation#handleTxWork(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.db.connection.OseeConnection)
    */
   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      int numberOfChildren =
            ConnectionHandler.runPreparedQueryFetchInt(connection, 0, COUNT_CHILD_BRANCHES, branch.getBranchId());
      if (numberOfChildren > 0) {
         throw new OseeArgumentException("Unable to purge a branch containing children");
      }

      ConnectionHandler.runPreparedUpdate(POPULATE_BRANCH_DELETE_HELPER_WITH_GAMMAS, branch.getBranchId(),
            branch.getBranchId());
      monitor.worked(calculateWork(0.10));

      ConnectionHandler.runPreparedUpdate(POPULATE_BRANCH_DELETE_HELPER_WITH_REMOVED_GAMMAS, branch.getBranchId(),
            branch.getBranchId());
      monitor.worked(calculateWork(0.10));

      purgeHelper(monitor, connection, "Attribute Versions", PURGE_ATTRIBUTE_VERSIONS, 0.15, branch.getBranchId());
      purgeHelper(monitor, connection, "Relation Versions", PURGE_RELATION_VERSIONS, 0.15, branch.getBranchId());
      purgeHelper(monitor, connection, "Artifact Versions", PURGE_ARTIFACT_VERSIONS, 0.15, branch.getBranchId());

      String exceptSyntax = SupportedDatabase.isDatabaseType(SupportedDatabase.oracle) ? "MINUS" : "EXCEPT";

      String deleteFromArtifactTable = String.format(DELETE_FROM_ARTIFACT, exceptSyntax);
      //TODO review sql      purgeHelper(monitor, connection, "Artifact", deleteFromArtifactTable, 0.15);
      monitor.worked(calculateWork(0.15));

      purgeHelper(monitor, connection, "Branch", DELETE_FROM_BRANCH_TABLE, 0.10, branch.getBranchId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doFinally(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doFinally(IProgressMonitor monitor) {
      super.doFinally(monitor);
      if (getStatus().isOK()) {
         BranchManager.handleBranchDeletion(branch.getBranchId());
      }
      monitor.worked(calculateWork(0.10));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#createErrorStatus(java.lang.Throwable)
    */
   @Override
   protected IStatus createErrorStatus(Throwable error) {
      IStatus status = super.createErrorStatus(error);
      if (!status.matches(IStatus.CANCEL)) {
         try {
            ConnectionHandler.runPreparedUpdate(DELETE_FROM_BRANCH_DELETE_HELPER, branch.getBranchId());
         } catch (OseeDataStoreException ex1) {
            OseeLog.log(Activator.class, Level.SEVERE, ex1);
         }
      }
      return status;
   }

   private void purgeHelper(IProgressMonitor monitor, OseeConnection connection, String type, String updateSql, double workPercentage, Object... data) throws OseeDataStoreException {
      monitor.setTaskName(String.format("Purge %s", type));
      checkForCancelledStatus(monitor);
      ConnectionHandler.runPreparedUpdate(connection, updateSql, data);
      monitor.worked(calculateWork(workPercentage));
   }
}