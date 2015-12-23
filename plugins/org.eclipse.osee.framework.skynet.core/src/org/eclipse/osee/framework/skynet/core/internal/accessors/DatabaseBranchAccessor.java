package org.eclipse.osee.framework.skynet.core.internal.accessors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseBranchAccessor implements IOseeDataAccessor<Long, Branch> {
   private static final int NULL_PARENT_BRANCH_ID = -1;

   private static final String SELECT_BRANCHES = "SELECT * FROM osee_branch";
   private static final String SELECT_MERGE_BRANCHES = "SELECT * FROM osee_merge";

   private final JdbcClient jdbcClient;

   private final TransactionCache txCache;
   private final BranchFactory branchFactory;

   public DatabaseBranchAccessor(JdbcClient jdbcClient, TransactionCache txCache, BranchFactory branchFactory) {
      this.jdbcClient = jdbcClient;
      this.txCache = txCache;
      this.branchFactory = branchFactory;
   }

   private JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   @Override
   public void load(IOseeCache<Long, Branch> cache) throws OseeCoreException {
      Map<Branch, Long> childToParent = new HashMap<>();
      Map<Branch, Integer> branchToBaseTx = new HashMap<>();
      Map<Branch, Integer> branchToSourceTx = new HashMap<>();
      Map<Branch, Integer> associatedArtifact = new HashMap<>();

      BranchCache brCache = (BranchCache) cache;
      loadBranches(brCache, childToParent, branchToBaseTx, branchToSourceTx, associatedArtifact);
      loadBranchHierarchy(brCache, childToParent);
      loadMergeBranches(brCache);
      loadAssociatedArtifacts(brCache, associatedArtifact);
      loadBranchRelatedTransactions(brCache, branchToBaseTx, branchToSourceTx);

      for (Branch branch : cache.getAll()) {
         branch.clearDirty();
      }
   }

   private void loadBranches(BranchCache cache, Map<Branch, Long> childToParent, Map<Branch, Integer> branchToBaseTx, Map<Branch, Integer> branchToSourceTx, Map<Branch, Integer> associatedArtifact) throws OseeCoreException {
      JdbcStatement chStmt = getJdbcClient().getStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_BRANCHES);
         while (chStmt.next()) {
            try {
               String branchName = chStmt.getString("branch_name");
               BranchState branchState = BranchState.getBranchState(chStmt.getInt("branch_state"));
               BranchType branchType = BranchType.valueOf(chStmt.getInt("branch_type"));
               boolean isArchived = BranchArchivedState.valueOf(chStmt.getInt("archived")).isArchived();
               long branchUuid = chStmt.getLong("branch_id");
               int inheritAccessControl = chStmt.getInt("inherit_access_control");
               Branch branch = branchFactory.createOrUpdate(cache, branchUuid, branchName, branchType, branchState,
                  isArchived, StorageState.LOADED, inheritAccessControl == 1);

               Long parentBranchId = chStmt.getLong("parent_branch_id");
               if (parentBranchId != NULL_PARENT_BRANCH_ID) {
                  childToParent.put(branch, parentBranchId);
               }
               branchToSourceTx.put(branch, chStmt.getInt("parent_transaction_id"));
               branchToBaseTx.put(branch, chStmt.getInt("baseline_transaction_id"));
               associatedArtifact.put(branch, chStmt.getInt("associated_art_id"));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Error loading branches", ex);
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
      Set<Integer> transactionIds = new HashSet<>();
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

   private void loadBranchHierarchy(BranchCache branchCache, Map<Branch, Long> childToParent) throws OseeCoreException {
      for (Entry<Branch, Long> entry : childToParent.entrySet()) {
         Branch childBranch = entry.getKey();
         Branch parentBranch = branchCache.getByUuid(entry.getValue());
         if (parentBranch == null) {
            throw new BranchDoesNotExist("Parent Branch uuid:[%s] does not exist for child branch [%s]",
               entry.getValue(), entry.getKey());
         }
         childBranch.setParentBranch(parentBranch);
      }
   }

   private void loadMergeBranches(BranchCache branchCache) throws OseeCoreException {
      JdbcStatement chStmt = getJdbcClient().getStatement();
      try {
         chStmt.runPreparedQuery(1000, SELECT_MERGE_BRANCHES);
         while (chStmt.next()) {
            Branch sourceBranch = branchCache.getByUuid(chStmt.getLong("source_branch_id"));
            Branch destBranch = branchCache.getByUuid(chStmt.getLong("dest_branch_id"));

            MergeBranch mergeBranch = (MergeBranch) branchCache.getByUuid(chStmt.getLong("merge_branch_id"));
            mergeBranch.setSourceBranch(sourceBranch);
            mergeBranch.setDestinationBranch(destBranch);
         }
      } finally {
         chStmt.close();
      }

   }

   @Override
   public void store(Collection<Branch> branches) throws OseeCoreException {
      StoreBranchDatabaseCallable task = new StoreBranchDatabaseCallable(jdbcClient, branches);
      try {
         IStatus status = task.handleTxWork();
         if (status.isOK()) {
            sendChangeEvents(branches);
            for (Branch branch : branches) {
               branch.clearDirty();
            }
         } else {
            throw new OseeStateException("Error storing branches");
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private void sendChangeEvents(Collection<Branch> branches) {
      for (Branch branch : branches) {
         if (branch.getBranchState().isDeleted()) {
            try {
               OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Deleted, branch.getUuid()));
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

         try {
            if (branch.isFieldDirty(AbstractOseeType.NAME_FIELD_KEY)) {
               OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.Renamed, branch.getUuid()));
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }
}
