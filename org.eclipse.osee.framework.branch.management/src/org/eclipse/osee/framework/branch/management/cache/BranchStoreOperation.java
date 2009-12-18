/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.IBranchUpdateEvent;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchField;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.logging.OseeLog;

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

   private final IBranchUpdateEvent eventSender;
   private final Collection<Branch> branches;

   public BranchStoreOperation(IOseeDatabaseServiceProvider provider, IBranchUpdateEvent eventSender, Collection<Branch> branches) {
      super(provider, "Branch Archive Operation", Activator.PLUGIN_ID);
      this.eventSender = eventSender;
      this.branches = branches;
   }

   private Object[] toInsertValues(Branch branch) throws OseeCoreException {
      Branch parentBranch = branch.getParentBranch();
      int parentBranchId = parentBranch != null ? parentBranch.getId() : NULL_PARENT_BRANCH_ID;
      return new Object[] {branch.getId(), branch.getGuid(), branch.getName(), parentBranchId,
            branch.getSourceTransaction().getId(), branch.getArchiveState().getValue(),
            branch.getAssociatedArtifact().getArtId(), branch.getBranchType().getValue(),
            branch.getBranchState().getValue()};
   }

   private Object[] toUpdateValues(Branch branch) throws OseeCoreException {
      Branch parentBranch = branch.getParentBranch();
      int parentBranchId = parentBranch != null ? parentBranch.getId() : NULL_PARENT_BRANCH_ID;
      return new Object[] {branch.getName(), parentBranchId, branch.getSourceTransaction().getId(),
            branch.getArchiveState().getValue(), branch.getAssociatedArtifact().getArtId(),
            branch.getBranchType().getValue(), branch.getBranchState().getValue(), branch.getId()};
   }

   private Object[] toDeleteValues(Branch branch) throws OseeDataStoreException {
      return new Object[] {branch.getId()};
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
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
                  break;
               default:
                  break;
            }
         }
         if (branch.isFieldDirty(BranchField.BRANCH_ARCHIVED_STATE_FIELD_KEY)) {
            Operations.executeAsJob(new BranchMoveOperation(getDatabaseServiceProvider(),
                  branch.getArchiveState().isArchived(), branch), false);
         }

      }
      getDatabaseService().runBatchUpdate(connection, INSERT_BRANCH, insertData);
      getDatabaseService().runBatchUpdate(connection, UPDATE_BRANCH, updateData);
      getDatabaseService().runBatchUpdate(connection, DELETE_BRANCH, deleteData);

      for (Branch branch : branches) {
         if (branch.getModificationType() == ModificationType.NEW) {
            branch.setModificationType(ModificationType.MODIFIED);
         }
         branch.clearDirty();
      }
      try {
         eventSender.send(branches);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error creating branch update relay", ex);
      }
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
}
