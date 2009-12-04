/*
 * Created on Oct 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchField;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public class BranchStoreOperation extends AbstractDbTxOperation {
   protected static final int NULL_PARENT_BRANCH_ID = -1;

   private static final String INSERT_BRANCH =
         "INSERT INTO osee_branch (branch_id, branch_guid, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state) VALUES (?,?,?,?,?,?,?,?,?)";

   private static final String UPDATE_BRANCH =
         "update osee_branch SET branch_name = ?, parent_branch_id = ?, parent_transaction_id = ?, archived = ?, associated_art_id = ?, branch_type = ?, branch_state = ? where branch_id = ?";

   private static final String DELETE_BRANCH = "DELETE from osee_branch where branch_id = ?";

   private static final String INSERT_BRANCH_ALIASES =
         "insert into osee_branch_definitions (mapped_branch_id, static_branch_name) VALUES (?, ?)";

   private static final String DELETE_BRANCH_ALIASES = "delete from osee_branch_definitions where mapped_branch_id = ?";

   private static final String INSERT_ADDRESSING =
         "insert into %s (transaction_id, gamma_id, tx_current, mod_type, branch_id) select transaction_id, gamma_id, tx_current, mod_type, branch_id from osee_txs where branch_id = ?";

   public static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";

   private final Collection<Branch> branches;

   public BranchStoreOperation(IOseeDatabaseServiceProvider provider, Collection<Branch> branches) {
      super(provider, "Branch Archive Operation", Activator.PLUGIN_ID);
      this.branches = branches;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      Collection<Branch> dirtyAliases = new HashSet<Branch>();

      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();

      for (Branch branch : branches) {
         if (isDataDirty(branch)) {
            switch (branch.getModificationType()) {
               case NEW:
                  branch.setId(getDatabaseService().getSequence().getNextBranchId());
                  insertData.add(toInsertValues(branch));
                  break;
               case MODIFIED:
                  updateData.add(toUpdateValues(branch));
                  break;
               case DELETED:
                  deleteData.add(toDeleteValues(branch));
                  dirtyAliases.add(branch);
                  break;
               default:
                  break;
            }
         }
         if (branch.isFieldDirty(BranchField.BRANCH_ALIASES_FIELD_KEY)) {
            dirtyAliases.add(branch);
         }
         if (branch.isFieldDirty(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY)) {
            moveBranchAddressing(connection, branch, branch.getArchiveState().isArchived());
         }

      }
      getDatabaseService().runBatchUpdate(connection, INSERT_BRANCH, insertData);
      getDatabaseService().runBatchUpdate(connection, UPDATE_BRANCH, updateData);
      getDatabaseService().runBatchUpdate(connection, DELETE_BRANCH, deleteData);

      storeAliases(connection, dirtyAliases);
      sendChangeEvents(branches);

      for (Branch branch : branches) {
         if (branch.getModificationType() == ModificationType.NEW) {
            branch.setModificationType(ModificationType.MODIFIED);
         }
         branch.clearDirty();
      }
   }

   private Object[] toInsertValues(Branch type) throws OseeCoreException {
      Branch parentBranch = type.getParentBranch();
      int parentBranchId = parentBranch != null ? parentBranch.getId() : NULL_PARENT_BRANCH_ID;
      return new Object[] {type.getId(), type.getGuid(), type.getName(), parentBranchId,
            type.getSourceTransaction().getId(), type.getArchiveState().getValue(),
            type.getAssociatedArtifact().getArtId(), type.getBranchType().getValue(), type.getBranchState().getValue()};
   }

   public void moveBranchAddressing(OseeConnection connection, Branch branch, boolean archive) throws OseeDataStoreException {
      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

      String sql = String.format(INSERT_ADDRESSING, destinationTableName);
      getDatabaseService().runPreparedUpdate(connection, sql, branch.getId());

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      getDatabaseService().runPreparedUpdate(connection, sql, branch.getId());
   }

   private boolean isDataDirty(Branch type) throws OseeCoreException {
      return type.areFieldsDirty(//
            AbstractOseeType.NAME_FIELD_KEY, //
            AbstractOseeType.UNIQUE_ID_FIELD_KEY, //
            BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY, //
            BranchField.BRANCH_STATE_FIELD_KEY, //
            BranchField.BRANCH_TYPE_FIELD_KEY, //
            BranchField.BRANCH_ASSOCIATED_ARTIFACT_FIELD_KEY);
   }

   private void storeAliases(OseeConnection connection, Collection<Branch> branches) throws OseeCoreException {
      List<Object[]> deleteData = new ArrayList<Object[]>();
      List<Object[]> insertData = new ArrayList<Object[]>();
      for (Branch branch : branches) {
         deleteData.add(new Object[] {branch.getId()});
         if (!branch.getModificationType().isDeleted()) {
            for (String alias : branch.getAliases()) {
               if (Strings.isValid(alias)) {
               insertData.add(new Object[] {branch.getId(), alias});
            }
         }
      }
      }
      getDatabaseService().runBatchUpdate(connection, DELETE_BRANCH_ALIASES, deleteData);
      if (!insertData.isEmpty()) {
      getDatabaseService().runBatchUpdate(connection, INSERT_BRANCH_ALIASES, insertData);
   }
   }

   private void sendChangeEvents(Collection<Branch> branches) {
      // TODO send Branch Events
      //      for (Branch branch : branches) {
      //         if (branch.getBranchState().isDeleted()) {
      //            try {
      //               OseeEventManager.kickBranchEvent(this, BranchEventType.Deleted, branch.getId());
      //            } catch (Exception ex) {
      //               // Do Nothing
      //            }
      //         }
      //
      //         try {
      //            if (branch.isFieldDirty(AbstractOseeType.NAME_FIELD_KEY)) {
      //               OseeEventManager.kickBranchEvent(this, BranchEventType.Renamed, branch.getId());
      //            }
      //         } catch (Exception ex) {
      //            // Do Nothing
      //         }
      //      }
   }

   private Object[] toUpdateValues(Branch type) throws OseeCoreException {
      Branch parentBranch = type.getParentBranch();
      int parentBranchId = parentBranch != null ? parentBranch.getId() : NULL_PARENT_BRANCH_ID;
      return new Object[] {type.getName(), parentBranchId, type.getBaseTransaction().getId(),
            type.getArchiveState().getValue(), type.getAssociatedArtifact().getArtId(),
            type.getBranchType().getValue(), type.getBranchState().getValue(), type.getId()};
   }

   private Object[] toDeleteValues(Branch branch) throws OseeDataStoreException {
      return new Object[] {branch.getId()};
   }

}
