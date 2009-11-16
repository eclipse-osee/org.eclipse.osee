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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.ShallowArtifact;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseBranchAccessor extends AbstractDatabaseAccessor<Branch> {
   public static final int NULL_PARENT_BRANCH_ID = -1;
   private static final String SELECT_BRANCHES =
         "SELECT ob.*, txd.transaction_id FROM osee_branch ob, osee_tx_details txd WHERE ob.branch_id = txd.branch_id and txd.tx_type = " + TransactionDetailsType.Baselined.getId();
   //   private static final String INSERT_BRANCH =
   //         "INSERT INTO osee_branch (branch_id, branch_guid, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type, branch_state) VALUES (?,?,?,?,?,?,?,?,?)";
   //   private static final String DELETE_BRANCH = "DELETE from osee_branch where branch_id = ?";

   private static final String SELECT_MERGE_BRANCHES = "SELECT * FROM osee_merge";

   private static final String SELECT_BRANCH_ALIASES =
         "select * from osee_branch_definitions order by mapped_branch_id";

   private final DatabaseTransactionAccessor transactionData;

   public DatabaseBranchAccessor(IOseeTypeFactory factory) {
      super(factory);
      transactionData = new DatabaseTransactionAccessor();
   }

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

   @Override
   public void load(AbstractOseeCache<Branch> cache) throws OseeCoreException {
      long startTime = System.currentTimeMillis();
      Map<Branch, Integer> childToParent = new HashMap<Branch, Integer>();
      Map<Branch, Integer> branchToSourceTx = new HashMap<Branch, Integer>();
      Map<Branch, Integer> branchToBaseTx = new HashMap<Branch, Integer>();
      Map<Branch, Integer> associatedArtifact = new HashMap<Branch, Integer>();

      BranchCache branchCache = getCastedObject(cache);
      loadBranches(branchCache, getFactory(), childToParent, branchToBaseTx, branchToSourceTx, associatedArtifact);
      loadBranchHierarchy(branchCache, childToParent);
      loadMergeBranches(branchCache);
      loadBranchAliases(branchCache);
      loadAssociatedArtifacts(branchCache, associatedArtifact);
      loadBranchRelatedTransactions(branchCache, branchToBaseTx, branchToSourceTx);

      for (Branch branch : cache.getAll()) {
         branch.clearDirty();
      }
      OseeLog.log(Activator.class, Level.INFO,
            String.format("Branch Cache loaded [%s]", Lib.getElapseString(startTime)));
   }

   private void loadAssociatedArtifacts(BranchCache cache, Map<Branch, Integer> associatedArtifact) throws OseeCoreException {
      if (cache.getDefaultAssociatedArtifact() == null) {
         cache.setDefaultAssociatedArtifact(new ShallowArtifact(cache, -1));
      }
      for (Entry<Branch, Integer> entry : associatedArtifact.entrySet()) {
         Branch branch = entry.getKey();
         branch.setAssociatedArtifact(new ShallowArtifact(cache, entry.getValue()));
      }
   }

   @SuppressWarnings("unchecked")
   private void loadBranchRelatedTransactions(BranchCache cache, Map<Branch, Integer> branchToBaseTx, Map<Branch, Integer> branchToSourceTx) throws OseeCoreException {
      Set<Integer> transactions = Collections.setUnion(branchToBaseTx.values(), branchToSourceTx.values());
      transactionData.loadTransactions(cache, transactions);

      for (Entry<Branch, Integer> entry : branchToBaseTx.entrySet()) {
         Branch branch = entry.getKey();
         if (branch.getBaseTransaction() == null) {
            TransactionRecord baseTransaction = TransactionManager.getTransactionFromCache(entry.getValue());
            cache.cacheBaseTransaction(branch, baseTransaction);
         }
      }

      for (Entry<Branch, Integer> entry : branchToBaseTx.entrySet()) {
         Branch branch = entry.getKey();
         if (branch.getSourceTransaction() == null) {
            TransactionRecord sourceTransaction = TransactionManager.getTransactionFromCache(entry.getValue());
            cache.cacheSourceTransaction(branch, sourceTransaction);
         }
      }
   }

   private void loadBranches(BranchCache cache, IOseeTypeFactory factory, Map<Branch, Integer> childToParent, Map<Branch, Integer> branchToBaseTx, Map<Branch, Integer> branchToSourceTx, Map<Branch, Integer> associatedArtifact) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_BRANCHES);
         while (chStmt.next()) {
            try {
               int branchId = chStmt.getInt("branch_id");

               String branchName = chStmt.getString("branch_name");
               BranchState branchState = BranchState.getBranchState(chStmt.getInt("branch_state"));
               BranchType branchType = BranchType.getBranchType(chStmt.getInt("branch_type"));
               boolean isArchived = BranchArchivedState.valueOf(chStmt.getInt("archived")).isArchived();
               Branch branch = cache.getById(branchId);
               if (branch == null) {

                  branch =
                        factory.createBranch(cache, chStmt.getString("branch_guid"), branchName, branchType,
                              branchState, isArchived);
                  branch.setId(branchId);
                  branch.setModificationType(ModificationType.MODIFIED);
                  branch.clearDirty();
                  cache.cache(branch);
               } else {
                  branch.setName(branchName);
                  branch.setArchived(isArchived);
                  branch.setBranchType(branchType);
                  branch.setBranchState(branchState);
                  branch.setModificationType(ModificationType.MODIFIED);
               }
               Integer parentBranchId = chStmt.getInt("parent_branch_id");
               if (parentBranchId != NULL_PARENT_BRANCH_ID) {
                  childToParent.put(branch, parentBranchId);
               }
               branchToSourceTx.put(branch, chStmt.getInt("parent_transaction_id"));
               branchToBaseTx.put(branch, chStmt.getInt("transaction_id"));
               associatedArtifact.put(branch, chStmt.getInt("associated_art_id"));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadBranchHierarchy(BranchCache branchCache, Map<Branch, Integer> childToParent) throws OseeCoreException {
      for (Entry<Branch, Integer> entry : childToParent.entrySet()) {
         Branch childBranch = entry.getKey();
         Branch parentBranch = branchCache.getById(entry.getValue());
         if (parentBranch == null) {
            throw new BranchDoesNotExist(String.format("Parent Branch id:[%s] does not exist for child branch [%s]",
                  entry.getValue(), entry.getKey()));
         }
         branchCache.setBranchParent(parentBranch, childBranch);

      }
   }

   private void loadMergeBranches(BranchCache branchCache) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(1000, SELECT_MERGE_BRANCHES);
         while (chStmt.next()) {
            Branch sourceBranch = branchCache.getById(chStmt.getInt("source_branch_id"));
            Branch destBranch = branchCache.getById(chStmt.getInt("dest_branch_id"));
            Branch mergeBranch = branchCache.getById(chStmt.getInt("merge_branch_id"));
            branchCache.cacheMergeBranch(mergeBranch, sourceBranch, destBranch);
         }
      } finally {
         chStmt.close();
      }

   }

   private void loadBranchAliases(BranchCache branchCache) throws OseeCoreException {
      HashCollection<Integer, String> aliasMap = new HashCollection<Integer, String>();
      ConnectionHandlerStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_BRANCH_ALIASES);
         while (chStmt.next()) {
            int branchId = chStmt.getInt("mapped_branch_id");
            String alias = chStmt.getString("static_branch_name").toLowerCase();
            aliasMap.put(branchId, alias);
         }
      } finally {
         chStmt.close();
      }
      for (Integer branchId : aliasMap.keySet()) {
         Branch branch = branchCache.getById(branchId);
         Collection<String> aliases = aliasMap.getValues(branchId);
         if (aliases != null) {
            branch.setAliases(aliases.toArray(new String[aliases.size()]));
         }
      }
   }

   @Override
   public void store(AbstractOseeCache<Branch> cache, Collection<Branch> branches) throws OseeCoreException {
      Operations.executeWork(new BranchStoreOperation(cache, branches), new NullProgressMonitor(), -1);
   }

   //TODO Move to its own Cache
   private final static class DatabaseTransactionAccessor {
      private static final String SELECT_TRANSACTIONS_BY_QUERY_ID =
            "select * from osee_tx_details txd, osee_join_id oji where txd.transaction_id = oji.id and oji.query_id = ?";

      private void loadTransactions(AbstractOseeCache<Branch> branchCache, Collection<Integer> txNumbers) throws OseeCoreException {
         IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery();
         try {
            for (Integer txNumber : txNumbers) {
               joinQuery.add(txNumber);
            }
            joinQuery.store();

            ConnectionHandlerStatement chStmt = ConnectionHandler.getStatement();
            try {
               chStmt.runPreparedQuery(5000, SELECT_TRANSACTIONS_BY_QUERY_ID, joinQuery.getQueryId());
               while (chStmt.next()) {
                  int transactionNumber = chStmt.getInt("transaction_id");
                  String comment = chStmt.getString("osee_comment");
                  Date timeStamp = chStmt.getTimestamp("time");
                  int authorId = chStmt.getInt("author");
                  int commitArtId = chStmt.getInt("commit_art_id");

                  TransactionRecord transaction = TransactionManager.getTransactionFromCache(transactionNumber);
                  if (transaction == null) {
                     Branch branch = branchCache.getById(chStmt.getInt("branch_id"));
                     TransactionDetailsType txType = TransactionDetailsType.toEnum(chStmt.getInt("tx_type"));

                     transaction =
                           new TransactionRecord(transactionNumber, branch, comment, timeStamp, authorId, commitArtId,
                                 txType);
                     TransactionManager.cacheTransaction(transaction);
                  } else {
                     transaction.setComment(comment);
                     transaction.setAuthor(authorId);
                     transaction.setCommit(commitArtId);
                     transaction.setTime(timeStamp);
                  }
               }
            } finally {
               chStmt.close();
            }
         } finally {
            joinQuery.delete();
         }
      }
   }
}
