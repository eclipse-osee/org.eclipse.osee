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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
class PurgeBranchJob extends Job {
   private static final String COUNT_CHILD_BRANCHES =
         "SELECT count(branch_id) as child_branches FROM OSEE_BRANCH WHERE parent_branch_id = ?";
   private static final String SEARCH_FOR_DELETABLE_GAMMAS =
         " SELECT ?, gamma_id FROM OSEE_TX_DETAILS det1, OSEE_TXS txs1 WHERE det1.branch_id = ? AND det1.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 'not_matter' FROM OSEE_TX_DETAILS det2, OSEE_TXS txs2 WHERE txs1.gamma_id = txs2.gamma_id AND det2.transaction_id = txs2.transaction_id AND det1.branch_id <> det2.branch_id)";
   private static final String POPULATE_BRANCH_DELETE_HELPER_WITH_GAMMAS =
         " INSERT INTO OSEE_BRANCH_DELETE_HELPER (branch_id, gamma_id) " + SEARCH_FOR_DELETABLE_GAMMAS;
   private static final String SEARCH_FOR_REMOVED_DELETABLE_GAMMAS =
         " SELECT ?, rem_gamma_id AS gamma_id FROM OSEE_TX_DETAILS det1, OSEE_REMOVED_TXS txs1  WHERE det1.branch_id = ? AND det1.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 'not_matter' FROM OSEE_TX_DETAILS det2, OSEE_TXS txs2 WHERE txs1.rem_gamma_id = txs2.gamma_id AND det2.transaction_id = txs2.transaction_id AND det1.branch_id <> det2.branch_id)";
   private static final String POPULATE_BRANCH_DELETE_HELPER_WITH_REMOVED_GAMMAS =
         " INSERT INTO OSEE_BRANCH_DELETE_HELPER (branch_id, gamma_id) " + SEARCH_FOR_REMOVED_DELETABLE_GAMMAS;
   private static final String DELETE_GAMMAS_RIGHT_HAND_SIDE =
         " where exists ( select 'x' from OSEE_BRANCH_DELETE_HELPER t2 where t2.branch_id = ? and t2.gamma_id = ";
   private static final String PURGE_ATTRIBUTE_VERSIONS =
         "delete from OSEE_ATTRIBUTE attr " + DELETE_GAMMAS_RIGHT_HAND_SIDE + "attr.gamma_id)";
   private static final String PURGE_RELATION_VERSIONS =
         "delete from OSEE_RELATION_LINK rel " + DELETE_GAMMAS_RIGHT_HAND_SIDE + "rel.gamma_id)";
   private static final String PURGE_ARTIFACT_VERSIONS =
         "delete from OSEE_ARTIFACT_VERSION art" + DELETE_GAMMAS_RIGHT_HAND_SIDE + "art.gamma_id)";
   private static final String DELETE_FROM_BRANCH_TABLE = "DELETE FROM OSEE_BRANCH WHERE branch_id = ?";

   private final Branch branch;

   /**
    * @param name
    * @param branch
    */
   public PurgeBranchJob(Branch branch) {
      super("Purge Branch: " + branch);
      this.branch = branch;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         PurgeBranchTx purgeBranchTx = new PurgeBranchTx(branch, monitor);
         purgeBranchTx.execute();
         toReturn = purgeBranchTx.getResult();
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

   private final class PurgeBranchTx extends DbTransaction {
      private final Branch branch;
      private final IProgressMonitor monitor;
      private IStatus txResult = Status.CANCEL_STATUS;

      public PurgeBranchTx(Branch branch, IProgressMonitor monitor) throws OseeCoreException {
         this.branch = branch;
         this.monitor = monitor;
      }

      public IStatus getResult() {
         return txResult;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         if (ConnectionHandler.runPreparedQueryFetchInt(connection, 0, COUNT_CHILD_BRANCHES, branch.getBranchId()) > 0) {
            throw new OseeCoreException("Can not purge a branch that has children");
         }

         monitor.beginTask("Purge Branch: " + branch, 10);
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
         try {
            ConnectionHandler.runPreparedUpdate(POPULATE_BRANCH_DELETE_HELPER_WITH_GAMMAS, branch.getBranchId(),
                  branch.getBranchId());
            monitor.worked(1);
            ConnectionHandler.runPreparedUpdate(POPULATE_BRANCH_DELETE_HELPER_WITH_REMOVED_GAMMAS,
                  branch.getBranchId(), branch.getBranchId());
            monitor.worked(1);

            purgeAttributeVersions();
            purgeRelationVersions();
            purgeArtifactVersions();
            purgeBranch();
            txResult = Status.OK_STATUS;
         } finally {
            chStmt.close();
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxFinally()
       */
      @Override
      protected void handleTxFinally() throws OseeCoreException {
         monitor.done();
         if (getResult().isOK()) {
            BranchManager.handleBranchDeletion(branch.getBranchId());
         }
      }

      private void purgeAttributeVersions() throws OseeDataStoreException {
         if (true != isCanceled()) {
            monitor.setTaskName("Purge attribute versions");
            ConnectionHandler.runPreparedUpdate(PURGE_ATTRIBUTE_VERSIONS, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void purgeRelationVersions() throws OseeDataStoreException {
         if (true != isCanceled()) {
            monitor.setTaskName("Purge relation versions");
            ConnectionHandler.runPreparedUpdate(PURGE_RELATION_VERSIONS, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void purgeArtifactVersions() throws OseeDataStoreException {
         if (true != isCanceled()) {
            monitor.setTaskName("Purge artifact versions");
            ConnectionHandler.runPreparedUpdate(PURGE_ARTIFACT_VERSIONS, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void purgeBranch() throws OseeDataStoreException {
         if (true != isCanceled()) {
            monitor.subTask("Purge Branch");
            ConnectionHandler.runPreparedUpdate(DELETE_FROM_BRANCH_TABLE, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private boolean isCanceled() {
         boolean isCanceled = monitor.isCanceled();
         return isCanceled;
      }
   }
}