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
package org.eclipse.osee.framework.skynet.core.types.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Branch.DirtyStateDetails;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.types.IOseeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.ShallowArtifact;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseBranchAccessor implements IOseeDataAccessor<Branch> {
   private static final int NULL_PARENT_BRANCH_ID = -1;
   private static final String SELECT_BRANCHES =
         "SELECT * FROM osee_branch ob, osee_tx_details txd WHERE ob.branch_id = txd.branch_id and txd.tx_type = " + TransactionDetailsType.Baselined.getId();
   private static final String INSERT_BRANCH =
         "INSERT INTO osee_branch (branch_id, branch_guid, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state) VALUES (?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE_BRANCH =
         "UPDATE osee_branch SET branch_name = ?, parent_branch_id = ?, parent_transaction_id = ?, archived = ?, associated_art_id = ?, branch_type = ?, branch_state = ? where branch_id = ?";
   private static final String DELETE_BRANCH = "DELETE from osee_branch where branch_id = ?";

   private static final String SELECT_MERGE_BRANCHES = "SELECT * FROM osee_merge";

   private static final String SELECT_BRANCH_ALIASES = "select * from osee_branch_definitions";
   private static final String INSERT_BRANCH_ALIASES =
         "insert into osee_branch_definitions (mapped_branch_id, static_branch_name) VALUES (?, ?)";
   private static final String DELETE_BRANCH_ALIASES = "delete from osee_branch_definitions where mapped_branch_id = ?";

   private BranchCache getCastedObject(AbstractOseeCache<Branch> cache) {
      return (BranchCache) cache;
   }

   //   private Object[] toInsertValues(Branch type) throws OseeCoreException {
   //      Branch parentBranch = type.getParentBranch();
   //      int parentBranchId = parentBranch != null ? parentBranch.getId() : NULL_PARENT_BRANCH_ID;
   //      return new Object[] {type.getId(), type.getGuid(), type.getName(),
   //            parentBranchId, type.getBaseTransaction().getTransactionNumber(), type.getArchiveState().getValue(),
   //            type.getAssociatedArtifactId(), type.getBranchType().getValue(), type.getBranchState().getValue()};
   //   }

   private Object[] toUpdateValues(Branch type) throws OseeCoreException {
      Branch parentBranch = type.getParentBranch();
      int parentBranchId = parentBranch != null ? parentBranch.getId() : NULL_PARENT_BRANCH_ID;
      return new Object[] {type.getName(), parentBranchId, type.getBaseTransaction().getTransactionNumber(),
            type.getArchiveState().getValue(), type.getAssociatedArtifact().getArtId(),
            type.getBranchType().getValue(), type.getBranchState().getValue(), type.getId()};
   }

   private Object[] toDeleteValues(Branch branch) throws OseeDataStoreException {
      return new Object[] {branch.getId()};
   }

   @Override
   public void load(AbstractOseeCache<Branch> cache, IOseeTypeFactory factory) throws OseeCoreException {
      Map<Branch, Integer> childToParent = new HashMap<Branch, Integer>();

      BranchCache branchCache = getCastedObject(cache);
      loadBranches(branchCache, factory, childToParent);
      loadBranchHierarchy(branchCache, childToParent);
      loadMergeBranches(branchCache);
      loadBranchAliases(branchCache);
      for (Branch branch : cache.getAllTypes()) {
         branch.clearDirty();
      }
   }

   private void loadBranches(BranchCache cache, IOseeTypeFactory factory, Map<Branch, Integer> childToParent) throws OseeCoreException {
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
               IArtifact artifact = new ShallowArtifact(chStmt.getInt("associated_art_id"));

               Branch branch = cache.getTypeById(branchId);
               if (branch == null) {

                  branch =
                        factory.createBranch(cache, chStmt.getString("branch_guid"), branchName,
                              chStmt.getInt("parent_transaction_id"), branchType, branchState, isArchived);
                  branch.setId(branchId);
                  branch.setModificationType(ModificationType.MODIFIED);
                  branch.setAssociatedArtifact(artifact);

                  //TODO Move to its own Cache
                  TransactionId baseTransaction = createBaselineTx(branch, chStmt);

                  branch.clearDirty();
                  cache.cacheType(branch);
                  cache.cacheTransaction(branch, baseTransaction);
               } else {
                  branch.setName(branchName);
                  branch.setArchived(isArchived);
                  branch.setBranchType(branchType);
                  branch.setBranchState(branchState);
                  branch.setAssociatedArtifact(artifact);
                  branch.setModificationType(ModificationType.MODIFIED);
                  updateBaselineTx(branch, chStmt);
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
   }

   private void updateBaselineTx(Branch branch, ConnectionHandlerStatement chStmt) throws OseeCoreException {
      String comment = chStmt.getString("osee_comment");
      Date timeStamp = chStmt.getTimestamp("time");
      int authorId = chStmt.getInt("author");
      int commitArtId = chStmt.getInt("commit_art_id");

      TransactionId transactionId = branch.getBaseTransaction();
      transactionId.setComment(comment);
      transactionId.setAuthorArtId(authorId);
      transactionId.setCommitArtId(commitArtId);
      transactionId.setTime(timeStamp);
   }

   private TransactionId createBaselineTx(Branch branch, ConnectionHandlerStatement chStmt) throws OseeCoreException {
      int transactionNumber = chStmt.getInt("transaction_id");
      String comment = chStmt.getString("osee_comment");
      Date timeStamp = chStmt.getTimestamp("time");
      int authorId = chStmt.getInt("author");
      int commitArtId = chStmt.getInt("commit_art_id");
      TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));
      TransactionId baseTransaction =
            new TransactionId(transactionNumber, branch, comment, timeStamp, authorId, commitArtId, txType);
      TransactionIdManager.cacheTransaction(baseTransaction);
      return baseTransaction;
   }

   private void loadBranchHierarchy(BranchCache branchCache, Map<Branch, Integer> childToParent) throws OseeCoreException {
      for (Entry<Branch, Integer> entry : childToParent.entrySet()) {
         Branch childBranch = entry.getKey();
         Branch parentBranch = branchCache.getTypeById(entry.getValue());
         if (parentBranch == null) {
            throw new BranchDoesNotExist(String.format("Parent Branch id:[%s] does not exist for child branch [%s]",
                  entry.getValue(), entry.getKey()));
         }
         branchCache.setBranchParent(parentBranch, childBranch);
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
         chStmt.runPreparedQuery(SELECT_BRANCH_ALIASES);

         while (chStmt.next()) {
            String alias = chStmt.getString("static_branch_name").toLowerCase();
            Branch branch = branchCache.getTypeById(chStmt.getInt("mapped_branch_id"));
            branch.setAliases(alias);
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void store(AbstractOseeCache<Branch> cache, Collection<Branch> branches) throws OseeCoreException {
      Collection<Branch> dirtyAliases = new HashSet<Branch>();

      //      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();

      for (Branch branch : branches) {
         DirtyStateDetails dirtyDetails = branch.getDirtyDetails();
         if (dirtyDetails.isDataDirty()) {
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
         if (dirtyDetails.areAliasesDirty()) {
            dirtyAliases.add(branch);
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
         if (branch.getDirtyDetails().isNameDirty()) {
            try {
               OseeEventManager.kickBranchEvent(this, BranchEventType.Renamed, branch.getId());
            } catch (Exception ex) {
               // Do Nothing
            }
         }
      }
   }
}
