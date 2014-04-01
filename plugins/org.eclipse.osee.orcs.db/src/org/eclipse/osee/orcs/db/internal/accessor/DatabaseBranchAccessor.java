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
package org.eclipse.osee.orcs.db.internal.accessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.StoreBranchDatabaseCallable;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseBranchAccessor implements IOseeDataAccessor<Long, Branch> {
   private static final int NULL_PARENT_BRANCH_ID = -1;

   private static final String SELECT_BRANCHES = "SELECT * FROM osee_branch";
   private static final String SELECT_MERGE_BRANCHES = "SELECT * FROM osee_merge";

   private final Log logger;
   private final OrcsSession session;
   private final IOseeDatabaseService dbService;
   private final ExecutorAdmin executorAdmin;
   private final EventService eventService;

   private final TransactionCache txCache;
   private final BranchFactory branchFactory;

   public DatabaseBranchAccessor(Log logger, OrcsSession session, ExecutorAdmin executorAdmin, EventService eventService, IOseeDatabaseService dbService, TransactionCache txCache, BranchFactory branchFactory) {
      this.logger = logger;
      this.session = session;
      this.executorAdmin = executorAdmin;
      this.eventService = eventService;
      this.dbService = dbService;
      this.txCache = txCache;
      this.branchFactory = branchFactory;
   }

   public Log getLogger() {
      return logger;
   }

   private IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   private ExecutorAdmin getExecutorAdmin() {
      return executorAdmin;
   }

   private EventService getEventService() {
      return eventService;
   }

   @Override
   public void load(IOseeCache<Long, Branch> cache) throws OseeCoreException {
      long startTime = System.currentTimeMillis();
      Map<Branch, Integer> childToParent = new HashMap<Branch, Integer>();
      Map<Branch, Integer> branchToBaseTx = new HashMap<Branch, Integer>();
      Map<Branch, Integer> branchToSourceTx = new HashMap<Branch, Integer>();
      Map<Branch, Integer> associatedArtifact = new HashMap<Branch, Integer>();

      BranchCache brCache = (BranchCache) cache;
      loadBranches(brCache, childToParent, branchToBaseTx, branchToSourceTx, associatedArtifact);
      loadBranchHierarchy(brCache, childToParent);
      loadMergeBranches(brCache);
      loadAssociatedArtifacts(brCache, associatedArtifact);
      loadBranchRelatedTransactions(brCache, branchToBaseTx, branchToSourceTx);

      for (Branch branch : cache.getAll()) {
         branch.clearDirty();
      }
      getLogger().info("Branch Cache loaded [%s]", Lib.getElapseString(startTime));
   }

   private void loadBranches(BranchCache cache, Map<Branch, Integer> childToParent, Map<Branch, Integer> branchToBaseTx, Map<Branch, Integer> branchToSourceTx, Map<Branch, Integer> associatedArtifact) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_BRANCHES);
         while (chStmt.next()) {
            try {
               String branchName = chStmt.getString("branch_name");
               BranchState branchState = BranchState.getBranchState(chStmt.getInt("branch_state"));
               BranchType branchType = BranchType.valueOf(chStmt.getInt("branch_type"));
               boolean isArchived = BranchArchivedState.valueOf(chStmt.getInt("archived")).isArchived();
               long branchUuid = chStmt.getLong("branch_id");
               Branch branch =
                  branchFactory.createOrUpdate(cache, branchUuid, branchName, branchType, branchState, isArchived,
                     StorageState.LOADED);

               Integer parentBranchId = chStmt.getInt("parent_branch_id");
               if (parentBranchId != NULL_PARENT_BRANCH_ID) {
                  childToParent.put(branch, parentBranchId);
               }
               branchToSourceTx.put(branch, chStmt.getInt("parent_transaction_id"));
               branchToBaseTx.put(branch, chStmt.getInt("baseline_transaction_id"));
               associatedArtifact.put(branch, chStmt.getInt("associated_art_id"));
            } catch (OseeCoreException ex) {
               getLogger().error(ex, "Error loading branches");
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadAssociatedArtifacts(BranchCache cache, Map<Branch, Integer> associatedArtifact) throws OseeCoreException {
      for (Entry<Branch, Integer> entry : associatedArtifact.entrySet()) {
         Branch branch = entry.getKey();
         branch.setAssociatedArtifactId(entry.getValue());
      }
   }

   private void loadBranchRelatedTransactions(BranchCache cache, Map<Branch, Integer> branchToBaseTx, Map<Branch, Integer> branchToSourceTx) throws OseeCoreException {
      Set<Integer> transactionIds = new HashSet<Integer>();
      transactionIds.addAll(branchToSourceTx.values());
      transactionIds.addAll(branchToBaseTx.values());
      txCache.loadTransactions(transactionIds);

      for (Entry<Branch, Integer> entry : branchToBaseTx.entrySet()) {
         Branch branch = entry.getKey();
         if (branch.getBaseTransaction() == null) {
            TransactionRecord baseTx = txCache.getById(entry.getValue());
            branch.setBaseTransaction(baseTx);
         }
      }

      for (Entry<Branch, Integer> entry : branchToSourceTx.entrySet()) {
         Branch branch = entry.getKey();
         if (branch.getSourceTransaction() == null) {
            TransactionRecord srcTx = txCache.getById(entry.getValue());
            branch.setSourceTransaction(srcTx);
         }
      }
   }

   private void loadBranchHierarchy(BranchCache branchCache, Map<Branch, Integer> childToParent) throws OseeCoreException {
      for (Entry<Branch, Integer> entry : childToParent.entrySet()) {
         Branch childBranch = entry.getKey();
         Branch parentBranch = branchCache.getByUuid(entry.getValue());
         if (parentBranch == null) {
            throw new BranchDoesNotExist("Parent Branch id:[%s] does not exist for child branch [%s]",
               entry.getValue(), entry.getKey());
         }
         childBranch.setParentBranch(parentBranch);
      }
   }

   private void loadMergeBranches(BranchCache branchCache) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(1000, SELECT_MERGE_BRANCHES);
         while (chStmt.next()) {
            Branch sourceBranch = branchCache.getByUuid(chStmt.getInt("source_branch_id"));
            Branch destBranch = branchCache.getByUuid(chStmt.getInt("dest_branch_id"));

            MergeBranch mergeBranch = (MergeBranch) branchCache.getByUuid(chStmt.getInt("merge_branch_id"));
            mergeBranch.setSourceBranch(sourceBranch);
            mergeBranch.setDestinationBranch(destBranch);
         }
      } finally {
         chStmt.close();
      }

   }

   @Override
   public void store(Collection<Branch> branches) throws OseeCoreException {
      Callable<IStatus> task =
         new StoreBranchDatabaseCallable(getLogger(), session, getDatabaseService(), getExecutorAdmin(),
            getEventService(), branches);
      try {
         Future<IStatus> future = getExecutorAdmin().schedule(task);
         IStatus status = future.get();
         if (!status.isOK()) {
            throw new OseeStateException("Error storing branches");
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }
}