package org.eclipse.osee.framework.skynet.core.internal.accessors;

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.Date;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseBranchAccessor implements IOseeDataAccessor<Branch> {
   private static final String SELECT_BRANCHES =
      "with%s recurse (id, branch_level) as (select branch_id, 1 from osee_branch where branch_id = 1 UNION ALL select branch_id, branch_level + 1 from recurse, osee_branch where parent_branch_id = recurse.id) select br.*, parTx.tx_type as p_tx_type, parTx.author as p_author, parTx.time as p_time, parTx.osee_comment as p_osee_comment, parTx.commit_art_id as p_commit_art_id, baseTx.tx_type as b_tx_type, baseTx.author as b_author, baseTx.time as b_time, baseTx.osee_comment as b_osee_comment, baseTx.commit_art_id as b_commit_art_id, source_branch_id, dest_branch_id from recurse, osee_branch br left outer join osee_merge on merge_branch_id = branch_id, osee_tx_details baseTx, osee_tx_details parTx where parent_transaction_id = parTx.transaction_id and baseline_transaction_id = baseTx.transaction_id and br.branch_id = recurse.id order by branch_level";
   private static final String SELECT_BRANCH =
      "select br.*, parTx.tx_type as p_tx_type, parTx.author as p_author, parTx.time as p_time, parTx.osee_comment as p_osee_comment, parTx.commit_art_id as p_commit_art_id, baseTx.tx_type as b_tx_type, baseTx.author as b_author, baseTx.time as b_time, baseTx.osee_comment as b_osee_comment, baseTx.commit_art_id as b_commit_art_id, source_branch_id, dest_branch_id from osee_branch br left outer join osee_merge on merge_branch_id = branch_id, osee_tx_details baseTx, osee_tx_details parTx where parent_transaction_id = parTx.transaction_id and baseline_transaction_id = baseTx.transaction_id and br.branch_id = ?";

   private final JdbcClient jdbcClient;

   public DatabaseBranchAccessor(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   @Override
   public void load(IOseeCache<Branch> cache) throws OseeCoreException {
      String sql = String.format(SELECT_BRANCHES, jdbcClient.getDbType().getRecursiveWithSql());
      jdbcClient.runQuery(stmt -> cache.cache(load(cache, stmt)), JDBC__MAX_FETCH_SIZE, sql);
   }

   public static Branch loadBranch(IOseeCache<Branch> cache, Long branchId) {
      return ConnectionHandler.getJdbcClient().fetchOrException(
         () -> new BranchDoesNotExist("Branch could not be acquired for branch id %d", branchId),
         stmt -> load(cache, stmt), SELECT_BRANCH, branchId);
   }

   private static Branch load(IOseeCache<Branch> cache, JdbcStatement stmt) {
      Branch branch = createOrUpdate(cache, stmt);

      Branch parentBranch;
      Branch sourceTxBranch;
      if (branch.equals(SYSTEM_ROOT)) {
         sourceTxBranch = branch;
      } else {
         parentBranch = cache.getById(stmt.getLong("parent_branch_id"));
         sourceTxBranch = parentBranch;
         branch.setParentBranch(parentBranch);
      }
      branch.setBaseTransaction(createTx(true, branch, stmt));
      branch.setSourceTransaction(createTx(false, sourceTxBranch, stmt));

      return branch;
   }

   private static Branch createOrUpdate(IOseeCache<Branch> cache, JdbcStatement stmt) {
      Long branchId = stmt.getLong("branch_id");
      String name = stmt.getString("branch_name");
      BranchType branchType = BranchType.valueOf(stmt.getInt("branch_type"));
      BranchState branchState = BranchState.getBranchState(stmt.getInt("branch_state"));
      boolean isArchived = BranchArchivedState.valueOf(stmt.getInt("archived")).isArchived();
      boolean inheritAccessControl = stmt.getInt("inherit_access_control") == 1;
      Integer artifactId = stmt.getInt("associated_art_id");

      Branch branch = cache.getById(branchId);
      if (branch == null) {
         if (branchType.isMergeBranch()) {
            MergeBranch mergeBranch =
               new MergeBranch(branchId, name, branchType, branchState, isArchived, inheritAccessControl);
            branch = mergeBranch;
            Branch sourceBranch = cache.getById(stmt.getLong("source_branch_id"));
            Branch destBranch = cache.getById(stmt.getLong("dest_branch_id"));

            mergeBranch.setSourceBranch(sourceBranch);
            mergeBranch.setDestinationBranch(destBranch);
         } else {
            branch = new Branch(branchId, name, branchType, branchState, isArchived, inheritAccessControl);
         }
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

   private static TransactionRecord createTx(boolean base, Branch branch, JdbcStatement stmt) {
      Long transactionId = stmt.getLong(base ? "baseline_transaction_id" : "parent_transaction_id");
      String comment = stmt.getString(base ? "b_osee_comment" : "p_osee_comment");
      Date timestamp = stmt.getTimestamp(base ? "b_time" : "p_time");
      Integer authorArtId = stmt.getInt(base ? "b_author" : "p_author");
      Integer commitArtId = stmt.getInt(base ? "b_commit_art_id" : "p_commit_art_id");
      TransactionDetailsType txType = TransactionDetailsType.toEnum(stmt.getInt(base ? "b_tx_type" : "p_tx_type"));
      return new TransactionRecord(transactionId, branch, comment, timestamp, authorArtId, commitArtId, txType);
   }
}