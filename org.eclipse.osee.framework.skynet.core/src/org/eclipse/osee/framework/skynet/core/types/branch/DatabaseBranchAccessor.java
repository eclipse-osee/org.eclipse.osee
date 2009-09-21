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
package org.eclipse.osee.framework.skynet.core.types.branch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchArchivedState;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseBranchAccessor implements IOseeTypeDataAccessor<Branch> {
   private static final int NULL_PARENT_BRANCH_ID = -1;
   private static final String SELECT_BRANCHES = "SELECT * FROM osee_branch";
   private static final String INSERT_BRANCH =
         "INSERT INTO osee_branch (branch_id, branch_guid, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state) VALUES (?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE_BRANCH =
         "UPDATE osee_branch SET branch_name = ?, parent_branch_id = ?, parent_transaction_id = ?, archived = ?, associated_art_id = ?, branch_type = ?, branch_state = ? where branch_id = ?";
   private static final String DELETE_BRANCH = "DELETE from osee_branch where branch_id = ?";

   private static final String SELECT_MERGE_BRANCHES = "SELECT m1.* FROM osee_merge m1";
   private static final String SELECT_MAPPED_BRANCH_INFO = "SELECT * FROM osee_branch_definitions";

   //   , osee_tx_details txd1 WHERE m1.merge_branch_id = txd1.branch_id and txd1.tx_type = " + TransactionDetailsType.Baselined.getId();

   private BranchCache getCastedObject(AbstractOseeCache<Branch> cache) {
      return (BranchCache) cache;
   }

   @Override
   public void load(AbstractOseeCache<Branch> cache, IOseeTypeFactory factory) throws OseeCoreException {
      Map<Branch, Integer> childToParent = new HashMap<Branch, Integer>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(2000, SELECT_BRANCHES);
         while (chStmt.next()) {
            try {
               int branchId = chStmt.getInt("branch_id");

               String branchName = chStmt.getString("branch_name");
               BranchState branchState = BranchState.getBranchState(chStmt.getInt("branch_state"));
               BranchType branchType = BranchType.getBranchType(chStmt.getInt("branch_type"));
               boolean isArchived = BranchArchivedState.valueOf(chStmt.getInt("archived")).isArchived();
               boolean isChangeManaged = false;

               Branch branch = cache.getTypeById(branchId);
               if (branch == null) {
                  branch =
                        factory.createBranch(cache, chStmt.getString("branch_guid"), branchName,
                              chStmt.getInt("parent_transaction_id"), chStmt.getInt("associated_art_id"), branchType,
                              branchState, isArchived, isChangeManaged);
                  branch.setId(branchId);
                  branch.setModificationType(ModificationType.MODIFIED);
                  branch.clearDirty();
                  cache.cacheType(branch);
               } else {
                  branch.setName(branchName);
                  branch.setArchived(isArchived);
                  branch.setBranchType(branchType);
                  branch.setBranchState(branchState);
                  branch.setChangeManaged(isChangeManaged);
                  branch.setModificationType(ModificationType.MODIFIED);
               }
               Integer parentBranchId = chStmt.getInt("parent_branch_id");
               if (parentBranchId != NULL_PARENT_BRANCH_ID) {
                  childToParent.put(branch, parentBranchId);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
      BranchCache branchCache = getCastedObject(cache);
      updateBranchHierarchy(branchCache, childToParent);
      loadMergeBranches(branchCache);
      loadBranchAliases(branchCache);
   }

   private void updateBranchHierarchy(BranchCache branchCache, Map<Branch, Integer> childToParent) throws OseeCoreException {
      for (Entry<Branch, Integer> entry : childToParent.entrySet()) {
         Branch childBranch = entry.getKey();
         Branch parentBranch = branchCache.getTypeById(entry.getValue());
         if (parentBranch == null) {
            throw new BranchDoesNotExist(String.format("Parent Branch id:[%s] does not exist for child branch [%s]",
                  entry.getValue(), entry.getKey()));
         }
         branchCache.setBranchParent(childBranch, parentBranch);
      }
   }

   private void loadMergeBranches(BranchCache branchCache) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(1000, SELECT_MERGE_BRANCHES);
         while (chStmt.next()) {
            Branch sourceBranch = branchCache.getTypeById(chStmt.getInt("source_branch_id"));
            Branch destBranch = branchCache.getTypeById(chStmt.getInt("dest_branch_id"));
            Branch mergeBranch = branchCache.getTypeById(chStmt.getInt("merge_branch_id"));
            branchCache.addMergeBranch(mergeBranch, sourceBranch, destBranch);
         }
      } finally {
         chStmt.close();
      }

   }

   private void loadBranchAliases(BranchCache branchCache) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_MAPPED_BRANCH_INFO);

         while (chStmt.next()) {
            String alias = chStmt.getString("static_branch_name").toLowerCase();
            Branch branch = branchCache.getTypeById(chStmt.getInt("mapped_branch_id"));
            branchCache.addBranchAlias(branch, alias);
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void store(AbstractOseeCache<Branch> cache, Collection<Branch> branches) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();

      for (Branch branch : branches) {
         switch (branch.getModificationType()) {
            case NEW:
               branch.setId(SequenceManager.getNextBranchId());
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
      ConnectionHandler.runBatchUpdate(INSERT_BRANCH, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_BRANCH, updateData);
      ConnectionHandler.runBatchUpdate(DELETE_BRANCH, deleteData);
      for (Branch branch : branches) {
         branch.clearDirty();
      }
   }

   private Object[] toInsertValues(Branch type) throws OseeCoreException {
      Branch parentBranch = type.getParentBranch();
      return new Object[] {type.getId(), type.getGuid(), type.getName(),
            parentBranch != null ? parentBranch.getId() : NULL_PARENT_BRANCH_ID, type.getParentTxNumber(),
            type.getArchiveState().getValue(), type.getAssociatedArtifactId(), type.getBranchType().getValue(),
            type.getBranchState().getValue()};
   }

   private Object[] toUpdateValues(Branch type) throws OseeCoreException {
      Branch parentBranch = type.getParentBranch();
      return new Object[] {type.getName(), parentBranch != null ? parentBranch.getId() : NULL_PARENT_BRANCH_ID,
            type.getParentTxNumber(), type.getArchiveState().getValue(), type.getAssociatedArtifactId(),
            type.getBranchType().getValue(), type.getBranchState().getValue()};
   }

   private Object[] toDeleteValues(Branch branch) throws OseeDataStoreException {
      return new Object[] {branch.getId()};
   }
}
