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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.VALID_ATTRIBUTES_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.VALID_RELATIONS_TABLE;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkDeletedBranchEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.event.LocalDeletedBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.remoteEvent.RemoteEventManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.LocalAliasTable;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.Table;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
class DeleteBranchJob extends Job {
   private static final String COUNT_CHILD_BRANCHES =
         "SELECT " + Table.alias("count(branch_id)", "child_branches") + " FROM " + BRANCH_TABLE + " WHERE " + BRANCH_TABLE.column("parent_branch_id") + "=?";
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
   private static final String DELETE_ARTIFACT_TYPE =
         "delete from " + ARTIFACT_TYPE_TABLE + DELETE_GAMMAS_RIGHT_HAND_SIDE + ARTIFACT_TYPE_TABLE.column("gamma_id") + ")";
   private static final String DELETE_ATTRIBUTE_TYPE =
         "delete from " + ATTRIBUTE_TYPE_TABLE + DELETE_GAMMAS_RIGHT_HAND_SIDE + ATTRIBUTE_TYPE_TABLE.column("gamma_id") + ")";
   private static final String DELETE_RELATION_TYPE =
         "delete from " + RELATION_LINK_TYPE_TABLE + DELETE_GAMMAS_RIGHT_HAND_SIDE + RELATION_LINK_TYPE_TABLE.column("gamma_id") + ")";
   private static final String DELETE_VLAID_RELATION =
         "delete from " + VALID_RELATIONS_TABLE + DELETE_GAMMAS_RIGHT_HAND_SIDE + VALID_RELATIONS_TABLE.column("gamma_id") + ")";
   private static final String DELETE_VALID_ATTRIBUTE =
         "delete from " + VALID_ATTRIBUTES_TABLE + DELETE_GAMMAS_RIGHT_HAND_SIDE + VALID_ATTRIBUTES_TABLE.column("gamma_id") + ")";
   private static final String DELETE_FROM_BRANCH_TABLE =
         "DELETE FROM " + BRANCH_TABLE + " WHERE " + BRANCH_TABLE.column("branch_id") + " = ?";
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(DeleteBranchJob.class);

   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final RemoteEventManager remoteEventManager = RemoteEventManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

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
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return toReturn;
   }

   private final class DeleteBranchTx extends AbstractDbTxTemplate {
      private Branch branch;
      private IProgressMonitor monitor;
      private ConnectionHandlerStatement chStmt;
      private IStatus txResult;

      public DeleteBranchTx(Branch branch, IProgressMonitor monitor) {
         this.branch = branch;
         this.monitor = monitor;
         this.chStmt = null;
         this.txResult = Status.CANCEL_STATUS;
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
      protected void handleTxWork() throws Exception {
         if (Query.getInt("child_branches", COUNT_CHILD_BRANCHES, SQL3DataType.INTEGER, branch.getBranchId()) > 0) throw new IllegalArgumentException(
               "Can not delete a branch that has children");

         monitor.beginTask("Delete Branch: " + branch, 10);
         chStmt =
               ConnectionHandler.runPreparedQuery(SEARCH_FOR_DELETABLE_GAMMAS, SQL3DataType.INTEGER,
                     branch.getBranchId(), SQL3DataType.INTEGER, branch.getBranchId(), SQL3DataType.INTEGER,
                     branch.getBranchId());

         if (chStmt.getRset().next()) {// checking to see if there are any gammas to delete
            // before inserting into delete table
            ConnectionHandler.runPreparedUpdate(POPULATE_BRANCH_DELETE_HELPER_WITH_GAMMAS, SQL3DataType.INTEGER,
                  branch.getBranchId(), SQL3DataType.INTEGER, branch.getBranchId(), SQL3DataType.INTEGER,
                  branch.getBranchId());
            monitor.worked(1);
         }
         deleteAttributeVersions();
         deleteRelationVersions();
         deleteArtifactVersions();
         deleteArtifactTypes();
         deleteAttributeTypes();
         deleteRelationTypes();
         deleteValidAttributes();
         deleteValidRelations();
         deleteBranch();
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxFinally()
       */
      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
         DbUtil.close(chStmt);
         monitor.done();
         if (getResult().equals(Status.OK_STATUS)) {
            eventManager.kick(new LocalDeletedBranchEvent(this, branch.getBranchId()));
            remoteEventManager.kick(new NetworkDeletedBranchEvent(branch.getBranchId(),
                  SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));
         }
      }

      private void deleteAttributeVersions() throws SQLException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete attribute versions");
            ConnectionHandler.runPreparedUpdate(DELETE_ATTRIBUTE_VERSIONS, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteRelationVersions() throws SQLException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete relation versions");
            ConnectionHandler.runPreparedUpdate(DELETE_RELATION_VERSIONS, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteArtifactVersions() throws SQLException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete artifact versions");
            ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_VERSIONS, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteArtifactTypes() throws SQLException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete artifact types");
            ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_TYPE, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteAttributeTypes() throws SQLException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete attribute types");
            ConnectionHandler.runPreparedUpdate(DELETE_ATTRIBUTE_TYPE, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteRelationTypes() throws SQLException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete relation types");
            ConnectionHandler.runPreparedUpdate(DELETE_RELATION_TYPE, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteValidAttributes() throws SQLException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete valid attributes");
            ConnectionHandler.runPreparedUpdate(DELETE_VALID_ATTRIBUTE, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteValidRelations() throws SQLException {
         if (true != isCanceled()) {
            monitor.setTaskName("Delete valid relations");
            ConnectionHandler.runPreparedUpdate(DELETE_VLAID_RELATION, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
         }
      }

      private void deleteBranch() throws SQLException {
         if (true != isCanceled()) {
            monitor.subTask("Delete Branch");
            ConnectionHandler.runPreparedUpdate(DELETE_FROM_BRANCH_TABLE, SQL3DataType.INTEGER, branch.getBranchId());
            monitor.worked(1);
            branchManager.removeBranchFromCache(branch.getBranchId());
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