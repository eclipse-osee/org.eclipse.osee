/*
 * Created on Oct 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.types.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeType;

/**
 * @author Ryan D. Brooks
 */
public class BranchStoreOperation extends AbstractDbTxOperation {
   private static final String INSERT_BRANCH_ALIASES =
         "insert into osee_branch_definitions (mapped_branch_id, static_branch_name) VALUES (?, ?)";
   private static final String DELETE_BRANCH_ALIASES = "delete from osee_branch_definitions where mapped_branch_id = ?";

   private static final String UPDATE_BRANCH =
         "update osee_branch SET branch_name = ?, parent_branch_id = ?, parent_transaction_id = ?, archived = ?, associated_art_id = ?, branch_type = ?, branch_state = ? where branch_id = ?";

   private static final String SELECT_ADDRESSING_BY_BRANCH =
         "select * from %s txs, osee_tx_details txd where txs.transaction_id = txd.transaction_id and txd.branch_id = ?";
   private static final String INSERT_ARCHIVED_ADDRESSING =
         "insert into %s (transaction_id, gamma_id, mod_type, tx_current) VALUES (?,?,?,?)";

   public static final String DELETE_ADDRESSING = "delete from %s where transaction_id = ? and gamma_id = ?";
   private final Collection<Branch> branches;

   public BranchStoreOperation(AbstractOseeCache<Branch> cache, Collection<Branch> branches) {
      super("Branch Archive Operation", Activator.PLUGIN_ID);
      this.branches = branches;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      Collection<Branch> dirtyAliases = new HashSet<Branch>();

      //      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();

      for (Branch branch : branches) {
         if (isDataDirty(branch)) {
            switch (branch.getModificationType()) {
               case NEW:
                  throw new UnsupportedOperationException(
                        "Branch Object Creation should only be performed by app server");
                  // TODO remove this exception once this class is only useb by the app server.
                  //               branch.setId(SequenceManager.getNextBranchId());
                  //               insertData.add(toInsertValues(branch));
                  //               break;
               case MODIFIED:
                  updateData.add(toUpdateValues(branch));
                  break;
               case DELETED:
                  deleteData.add(toDeleteValues(branch));
                  break;
               default:
                  break;
            }
         }
         if (branch.isFieldDirty(Branch.BRANCH_ALIASES_FIELD_KEY)) {
            dirtyAliases.add(branch);
         }
         if (branch.isFieldDirty(Branch.BRANCH_ARCHIVED_STATE_FIELD_KEY)) {
            moveBranchAddressing(connection, branch, branch.getArchiveState().isArchived());
         }

      }
      //      ConnectionHandler.runBatchUpdate(INSERT_BRANCH, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_BRANCH, updateData);
      //      ConnectionHandler.runBatchUpdate(DELETE_BRANCH, deleteData);

      storeAliases(dirtyAliases);
      sendChangeEvents(branches);

      for (Branch branch : branches) {
         branch.clearDirty();
      }
   }

   private void moveBranchAddressing(OseeConnection connection, Branch branch, boolean archive) throws OseeDataStoreException {
      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      List<Object[]> addressing = new ArrayList<Object[]>();
      List<Object[]> deleteAddressing = new ArrayList<Object[]>();
      String sql = String.format(SELECT_ADDRESSING_BY_BRANCH, sourceTableName);

      try {
         chStmt.runPreparedQuery(10000, sql, branch.getBranchId());
         while (chStmt.next()) {
            addressing.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id"),
                  chStmt.getInt("mod_type"), chStmt.getInt("tx_current")});
            deleteAddressing.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }
      sql = String.format(INSERT_ARCHIVED_ADDRESSING, destinationTableName);
      ConnectionHandler.runBatchUpdate(connection, sql, addressing);

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      ConnectionHandler.runBatchUpdate(connection, sql, deleteAddressing);

   }

   private boolean isDataDirty(Branch type) throws OseeCoreException {
      return type.areFieldsDirty(//
            AbstractOseeType.NAME_FIELD_KEY, //
            AbstractOseeType.UNIQUE_ID_FIELD_KEY, //
            Branch.BRANCH_ARCHIVED_STATE_FIELD_KEY, //
            Branch.BRANCH_STATE_FIELD_KEY, //
            Branch.BRANCH_TYPE_FIELD_KEY, //
            Branch.BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY);
   }

   private void storeAliases(Collection<Branch> branches) throws OseeCoreException {
      List<Object[]> deleteData = new ArrayList<Object[]>();
      List<Object[]> insertData = new ArrayList<Object[]>();
      for (Branch branch : branches) {
         deleteData.add(new Object[] {branch.getId()});
         for (String alias : branch.getAliases()) {
            insertData.add(new Object[] {branch.getId(), alias});
         }
      }
      ConnectionHandler.runBatchUpdate(DELETE_BRANCH_ALIASES, deleteData);
      ConnectionHandler.runBatchUpdate(INSERT_BRANCH_ALIASES, insertData);
   }

   private void sendChangeEvents(Collection<Branch> branches) {
      for (Branch branch : branches) {
         if (branch.getBranchState().isDeleted()) {
            try {
               OseeEventManager.kickBranchEvent(this, BranchEventType.Deleted, branch.getId());
            } catch (Exception ex) {
               // Do Nothing
            }
         }

         try {
            if (branch.isFieldDirty(AbstractOseeType.NAME_FIELD_KEY)) {
               OseeEventManager.kickBranchEvent(this, BranchEventType.Renamed, branch.getId());
            }
         } catch (Exception ex) {
            // Do Nothing
         }
      }
   }

   private Object[] toUpdateValues(Branch type) throws OseeCoreException {
      Branch parentBranch = type.getParentBranch();
      int parentBranchId = parentBranch != null ? parentBranch.getId() : DatabaseBranchAccessor.NULL_PARENT_BRANCH_ID;
      return new Object[] {type.getName(), parentBranchId, type.getBaseTransaction().getId(),
            type.getArchiveState().getValue(), type.getAssociatedArtifact().getArtId(),
            type.getBranchType().getValue(), type.getBranchState().getValue(), type.getId()};
   }

   private Object[] toDeleteValues(Branch branch) throws OseeDataStoreException {
      return new Object[] {branch.getId()};
   }

}
