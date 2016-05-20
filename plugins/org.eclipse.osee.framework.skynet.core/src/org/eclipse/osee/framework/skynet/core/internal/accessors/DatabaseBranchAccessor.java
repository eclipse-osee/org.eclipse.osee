package org.eclipse.osee.framework.skynet.core.internal.accessors;

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseBranchAccessor implements IOseeDataAccessor<Branch> {
   private static final String SELECT_BRANCHES = "SELECT * FROM osee_branch";
   private static final String SELECT_BRANCH = SELECT_BRANCHES + " where branch_id = ?";
   private static final String SELECT_MERGE_BRANCHES = "SELECT * FROM osee_merge";

   private final JdbcClient jdbcClient;
   private final TransactionCache txCache;

   public DatabaseBranchAccessor(JdbcClient jdbcClient, TransactionCache txCache) {
      this.jdbcClient = jdbcClient;
      this.txCache = txCache;
   }

   private JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   @Override
   public void load(IOseeCache<Branch> cache) throws OseeCoreException {
      Map<Branch, Long> childToParent = new HashMap<>();
      Map<Branch, Long> branchToBaseTx = new HashMap<>();
      Map<Branch, Long> branchToSourceTx = new HashMap<>();

      loadBranches(cache, childToParent, branchToBaseTx, branchToSourceTx);
      loadBranchHierarchy(cache, childToParent);
      loadMergeBranches(cache);
      loadBranchRelatedTransactions(branchToBaseTx, branchToSourceTx);
   }

   private void loadBranches(IOseeCache<Branch> cache, Map<Branch, Long> childToParent, Map<Branch, Long> branchToBaseTx, Map<Branch, Long> branchToSourceTx) {
      getJdbcClient().runQuery(stmt -> {
         Branch branch = load(cache, stmt);
         cache.cache(branch);
         if (!SYSTEM_ROOT.equals(branch)) {
            childToParent.put(branch, stmt.getLong("parent_branch_id"));
         }
         branchToSourceTx.put(branch, stmt.getLong("parent_transaction_id"));
         branchToBaseTx.put(branch, stmt.getLong("baseline_transaction_id"));
      }, JDBC__MAX_FETCH_SIZE, SELECT_BRANCHES);
   }

   private static Branch create(Long branchId, String name, BranchType branchType, BranchState branchState, boolean isArchived, boolean inheritAccessControl) throws OseeCoreException {
      Branch toReturn;
      if (branchType.isMergeBranch()) {
         toReturn = new MergeBranch(branchId, name, branchType, branchState, isArchived, inheritAccessControl);
      } else {
         toReturn = new Branch(branchId, name, branchType, branchState, isArchived, inheritAccessControl);
      }
      return toReturn;
   }

   private static Branch createOrUpdate(IOseeCache<Branch> cache, Long branchId, String name, BranchType branchType, BranchState branchState, boolean isArchived, StorageState storageState, boolean inheritAccessControl, Integer artifactId) throws OseeCoreException {
      Branch branch = cache.getById(branchId);
      if (branch == null) {
         branch = create(branchId, name, branchType, branchState, isArchived, inheritAccessControl);
      } else {
         branch.setName(name);
         branch.setBranchType(branchType);
         branch.setBranchState(branchState);
         branch.setArchived(isArchived);
         branch.setInheritAccessControl(inheritAccessControl);
      }
      branch.setAssociatedArtifactId(artifactId);
      return branch;
   }

   private static Branch load(IOseeCache<Branch> cache, JdbcStatement stmt) {
      Long branchId = stmt.getLong("branch_id");
      String branchName = stmt.getString("branch_name");
      BranchType branchType = BranchType.valueOf(stmt.getInt("branch_type"));
      BranchState branchState = BranchState.getBranchState(stmt.getInt("branch_state"));
      boolean isArchived = BranchArchivedState.valueOf(stmt.getInt("archived")).isArchived();
      int inheritAccessControl = stmt.getInt("inherit_access_control");
      Integer artifactId = stmt.getInt("associated_art_id");

      Branch branch = createOrUpdate(cache, branchId, branchName, branchType, branchState, isArchived,
         StorageState.LOADED, inheritAccessControl == 1, artifactId);

      return branch;
   }

   public static Branch loadBranch(IOseeCache<Branch> cache, Long branchId) {
      return ConnectionHandler.getJdbcClient().fetchOrException(
         () -> new BranchDoesNotExist("Branch could not be acquired for branch id %d", branchId),
         stmt -> fetchBranch(cache, stmt), SELECT_BRANCH, branchId);
   }

   private static Branch fetchBranch(IOseeCache<Branch> cache, JdbcStatement stmt) {
      Branch branch = load(cache, stmt);
      branch.setBaseTransaction(TransactionManager.getTransactionRecord(stmt.getLong("baseline_transaction_id")));
      branch.setSourceTransaction(TransactionManager.getTransactionRecord(stmt.getLong("parent_transaction_id")));
      if (!SYSTEM_ROOT.equals(branch)) {
         branch.setParentBranch(BranchManager.getBranch(stmt.getLong("parent_branch_id")));
      }
      return branch;
   }

   private void loadBranchRelatedTransactions(Map<Branch, Long> branchToBaseTx, Map<Branch, Long> branchToSourceTx) throws OseeCoreException {
      Set<Long> transactionIds = new HashSet<>();
      transactionIds.addAll(branchToSourceTx.values());
      transactionIds.addAll(branchToBaseTx.values());
      txCache.loadTransactions(transactionIds);

      for (Entry<Branch, Long> entry : branchToBaseTx.entrySet()) {
         Branch branch = entry.getKey();
         if (branch.getBaseTransaction() == null) {
            TransactionRecord baseTx = txCache.getById(entry.getValue());
            branch.setBaseTransaction(baseTx);
         }
      }

      for (Entry<Branch, Long> entry : branchToSourceTx.entrySet()) {
         Branch branch = entry.getKey();
         if (BranchManager.getSourceTransaction(branch) == null) {
            TransactionRecord srcTx = txCache.getById(entry.getValue());
            branch.setSourceTransaction(srcTx);
         }
      }
   }

   private void loadBranchHierarchy(IOseeCache<Branch> cache, Map<Branch, Long> childToParent) throws OseeCoreException {
      for (Entry<Branch, Long> entry : childToParent.entrySet()) {
         Branch childBranch = entry.getKey();
         Branch parentBranch = cache.getById(entry.getValue());
         if (parentBranch == null) {
            throw new BranchDoesNotExist("Parent Branch uuid:[%s] does not exist for child branch [%s]",
               entry.getValue(), entry.getKey());
         }
         childBranch.setParentBranch(parentBranch);
      }
   }

   private void loadMergeBranches(IOseeCache<Branch> cache) throws OseeCoreException {
      Consumer<JdbcStatement> consumer = stmt -> {
         Branch sourceBranch = cache.getById(stmt.getLong("source_branch_id"));
         Branch destBranch = cache.getById(stmt.getLong("dest_branch_id"));
         MergeBranch mergeBranch = (MergeBranch) cache.getById(stmt.getLong("merge_branch_id"));

         mergeBranch.setSourceBranch(sourceBranch);
         mergeBranch.setDestinationBranch(destBranch);
      };
      getJdbcClient().runQuery(consumer, 1000, SELECT_MERGE_BRANCHES);
   }
}