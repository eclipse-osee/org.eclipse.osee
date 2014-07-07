package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchField;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public class StoreBranchDatabaseCallable {
   protected static final int NULL_PARENT_BRANCH_ID = -1;

   private static final String INSERT_BRANCH =
      "INSERT INTO osee_branch (branch_id, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state, baseline_transaction_id, inherit_access_control) VALUES (?,?,?,?,?,?,?,?,?,?)";

   private static final String UPDATE_BRANCH =
      "UPDATE osee_branch SET branch_name = ?, parent_branch_id = ?, parent_transaction_id = ?, archived = ?, associated_art_id = ?, branch_type = ?, branch_state = ?, baseline_transaction_id = ?, inherit_access_control = ? WHERE branch_id = ?";

   private static final String DELETE_BRANCH = "DELETE FROM osee_branch WHERE branch_id = ?";

   private final Collection<Branch> branches;
   private final IOseeDatabaseService dbService;

   public StoreBranchDatabaseCallable(IOseeDatabaseService dbService, Collection<Branch> branches) {
      this.branches = branches;
      this.dbService = dbService;
   }

   public IStatus handleTxWork() throws OseeCoreException {
      OseeConnection connection = dbService.getConnection();
      try {
         List<Object[]> insertData = new ArrayList<Object[]>();
         List<Object[]> updateData = new ArrayList<Object[]>();
         List<Object[]> deleteData = new ArrayList<Object[]>();

         for (Branch branch : branches) {
            if (isDataDirty(branch)) {
               switch (branch.getStorageState()) {
                  case CREATED:
                     insertData.add(toInsertValues(branch));
                     break;
                  case MODIFIED:
                     updateData.add(toUpdateValues(branch));
                     break;
                  case PURGED:
                     deleteData.add(toDeleteValues(branch));
                     break;
                  default:
                     break;
               }
            }
            if (branch.isFieldDirty(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY)) {
               MoveBranchDatabaseCallable task =
                  new MoveBranchDatabaseCallable(dbService, branch.getArchiveState().isArchived(), branch);
               try {
                  task.handleTxWork();
               } catch (Exception ex) {
                  OseeExceptions.wrapAndThrow(ex);
               }
            }
         }
         dbService.runBatchUpdate(connection, INSERT_BRANCH, insertData);
         dbService.runBatchUpdate(connection, UPDATE_BRANCH, updateData);
         dbService.runBatchUpdate(connection, DELETE_BRANCH, deleteData);
      } finally {
         connection.close();
      }

      return Status.OK_STATUS;
   }

   private Object[] toInsertValues(Branch branch) throws OseeCoreException {
      Branch parentBranch = branch.getParentBranch();
      TransactionRecord baseTxRecord = branch.getBaseTransaction();
      long parentBranchId = parentBranch != null ? parentBranch.getUuid() : NULL_PARENT_BRANCH_ID;
      int baselineTransaction = baseTxRecord != null ? baseTxRecord.getId() : NULL_PARENT_BRANCH_ID;
      int inheritAccessControl = branch.isInheritAccessControl() ? 1 : 0;

      return new Object[] {
         branch.getUuid(),
         branch.getName(),
         parentBranchId,
         branch.getSourceTransaction().getId(),
         branch.getArchiveState().getValue(),
         branch.getAssociatedArtifactId(),
         branch.getBranchType().getValue(),
         branch.getBranchState().getValue(),
         baselineTransaction,
         inheritAccessControl};
   }

   private Object[] toUpdateValues(Branch branch) throws OseeCoreException {
      Branch parentBranch = branch.getParentBranch();
      TransactionRecord baseTxRecord = branch.getBaseTransaction();
      long parentBranchId = parentBranch != null ? parentBranch.getUuid() : NULL_PARENT_BRANCH_ID;
      int baselineTransaction = baseTxRecord != null ? baseTxRecord.getId() : NULL_PARENT_BRANCH_ID;
      int inheritAccessControl = branch.isInheritAccessControl() ? 1 : 0;

      return new Object[] {
         branch.getName(),
         parentBranchId,
         branch.getSourceTransaction().getId(),
         branch.getArchiveState().getValue(),
         branch.getAssociatedArtifactId(),
         branch.getBranchType().getValue(),
         branch.getBranchState().getValue(),
         baselineTransaction,
         inheritAccessControl,
         branch.getUuid()};
   }

   private Object[] toDeleteValues(Branch branch) {
      return new Object[] {branch.getUuid()};
   }

   private boolean isDataDirty(Branch type) throws OseeCoreException {
      return type.areFieldsDirty(//
         AbstractOseeType.NAME_FIELD_KEY, //
         AbstractOseeType.UNIQUE_ID_FIELD_KEY, //
         BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY, //
         BranchField.BRANCH_STATE_FIELD_KEY, //
         BranchField.BRANCH_TYPE_FIELD_KEY, //
         BranchField.BRANCH_ASSOCIATED_ARTIFACT_ID_FIELD_KEY, //
         BranchField.BRANCH_BASE_TRANSACTION, //
         BranchField.BRANCH_INHERIT_ACCESS_CONTROL);
   }

}
