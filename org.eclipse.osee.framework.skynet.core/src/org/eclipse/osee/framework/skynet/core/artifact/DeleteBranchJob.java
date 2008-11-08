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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.Connection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
class DeleteBranchJob extends Job {
   private static final String COUNT_CHILD_BRANCHES =
         "SELECT count(branch_id) as child_branches FROM " + BRANCH_TABLE + " WHERE " + BRANCH_TABLE.column("parent_branch_id") + "=?";
   private static final LocalAliasTable T1 = new LocalAliasTable(TRANSACTION_DETAIL_TABLE, "t1");
   private static final LocalAliasTable T2 = new LocalAliasTable(TRANSACTIONS_TABLE, "t2");
   private static final String SEARCH_FOR_DELETABLE_GAMMAS =
         " SELECT ?, gamma_id FROM " + T1 + ", " + T2 + " WHERE " + T1.column("branch_id") + " = ? AND " + T1.column("transaction_id") + " = " + T2.column("transaction_id") + " AND " + T1.column("transaction_id") + " <> (SELECT MIN(transaction_id) FROM " + TRANSACTION_DETAIL_TABLE + " WHERE branch_id = ?) and NOT EXISTS (SELECT 'not_matter' FROM " + TRANSACTION_DETAIL_TABLE + ", " + TRANSACTIONS_TABLE + " WHERE " + T2.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " = " + TRANSACTIONS_TABLE.column("transaction_id") + " AND " + T1.column("transaction_id") + " <> " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ")";
   private static final String POPULATE_BRANCH_DELETE_HELPER_WITH_GAMMAS =
         " INSERT INTO " + SkynetDatabase.BRANCH_DELETE_HELPER + "(branch_id, gamma_id) " + SEARCH_FOR_DELETABLE_GAMMAS;
   private static final LocalAliasTable T2_BDH = new LocalAliasTable(SkynetDatabase.BRANCH_DELETE_HELPER, "t2");
   private static final String DELETE_GAMMAS_RIGHT_HAND_SIDE =
         " where exists ( select 'x' from " + T2_BDH + " where t2.branch_id = ? and t2.gamma_id = ";
   private static final String DELETE_ATTRIBUTE_VERSIONS =
         "delete from " + ATTRIBUTE_VERSION_TABLE + DELETE_GAMMAS_RIGHT_HAND_SIDE + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + ")";
   private static final String DELETE_RELATION_VERSIONS =
         "delete from " + RELATION_LINK_VERSION_TABLE + DELETE_GAMMAS_RIGHT_HAND_SIDE + RELATION_LINK_VERSION_TABLE.column("gamma_id") + ")";
   private static final String DELETE_ARTIFACT_VERSIONS =
         "delete from " + ARTIFACT_VERSION_TABLE + DELETE_GAMMAS_RIGHT_HAND_SIDE + ARTIFACT_VERSION_TABLE.column("gamma_id") + ")";
   private static final String DELETE_FROM_BRANCH_TABLE =
         "DELETE FROM " + BRANCH_TABLE + " WHERE " + BRANCH_TABLE.column("branch_id") + " = ?";

   private final Branch branch;

   /**
    * @param name
    * @param branch
    */
   public DeleteBranchJob(Branch branch) {
      super("Delete Branch: " + branch);
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
         DeleteBranchTx deleteBranchTx = new DeleteBranchTx(branch, monitor);
         deleteBranchTx.execute();
         toReturn = deleteBranchTx.getResult();
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

   private final class DeleteBranchTx extends DbTransaction {
      private final Branch branch;
      private final IProgressMonitor monitor;
      private IStatus txResult = Status.CANCEL_STATUS;

      public DeleteBranchTx(Branch branch, IProgressMonitor monitor) throws OseeCoreException {
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
      protected void handleTxWork(Connection connection) throws OseeCoreException {
         if (ConnectionHandler.runPreparedQueryFetchInt(connection, 0, COUNT_CHILD_BRANCHES, branch.getBranchId()) > 0) {
            throw new OseeCoreException("Can not delete a branch that has children");
         }

         monitor.beginTask("Delete Branch: " + branch, 10);
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
         try {
            chStmt.runPreparedQuery(SEARCH_FOR_DELETABLE_GAMMAS, branch.getBranchId(), branch.getBranchId(),
                  branch.getBranchId());

            if (chStmt.next()) {// checking to see if there are any gammas to delete
               // before inserting into delete table
               ConnectionHandler.runPreparedUpdate(POPULATE_BRANCH_DELETE_HELPER_WITH_GAMMAS, branch.getBranchId(),
                     branch.getBranchId(), branch.getBranchId());
               monitor.worked(1);
            }
            deleteAttributeVersions();
            deleteRelationVersions();
            deleteArtifactVersions();
            deleteBranch();
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
         if (getResult().equals(Status.OK_STATUS)) {
            OseeEventManager.kickBranchEvent(this, BranchEventType.Deleted, branch.getBranchId());
         }
      }

      private void deleteAttributeVersions() throws OseeDataStoreException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete attribute versions");
            ConnectionHandler.runPreparedUpdate(DELETE_ATTRIBUTE_VERSIONS, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteRelationVersions() throws OseeDataStoreException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete relation versions");
            ConnectionHandler.runPreparedUpdate(DELETE_RELATION_VERSIONS, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteArtifactVersions() throws OseeDataStoreException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete artifact versions");
            ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_VERSIONS, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteBranch() throws OseeDataStoreException {
         if (true != isCanceled()) {
            monitor.subTask("Delete Branch");
            ConnectionHandler.runPreparedUpdate(DELETE_FROM_BRANCH_TABLE, branch.getBranchId());
            monitor.worked(1);
            BranchManager.handleBranchDeletion(branch.getBranchId());
         }
      }

      private boolean isCanceled() {
         boolean isCanceled = monitor.isCanceled();
         if (!isCanceled) {
            txResult = Status.OK_STATUS;
         }
         return isCanceled;
      }
   }
}