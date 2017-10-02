package org.eclipse.osee.framework.skynet.core.internal.accessors;

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseBranchAccessor implements IOseeDataAccessor<Branch> {
   private static final String SELECT_BRANCHES =
      "with%s recurse (id, branch_level) as (select branch_id, 1 from osee_branch where branch_id = 1 UNION ALL select branch_id, branch_level + 1 from recurse, osee_branch where parent_branch_id = recurse.id) select br.*, parTx.build_id as p_tx_build_id, parTx.tx_type as p_tx_type, parTx.author as p_author, parTx.time as p_time, parTx.osee_comment as p_osee_comment, parTx.commit_art_id as p_commit_art_id, baseTx.build_id as b_tx_build_id, baseTx.tx_type as b_tx_type, baseTx.author as b_author, baseTx.time as b_time, baseTx.osee_comment as b_osee_comment, baseTx.commit_art_id as b_commit_art_id, source_branch_id, dest_branch_id from recurse, osee_branch br left outer join osee_merge on merge_branch_id = branch_id, osee_tx_details baseTx, osee_tx_details parTx where parent_transaction_id = parTx.transaction_id and baseline_transaction_id = baseTx.transaction_id and br.branch_id = recurse.id order by branch_level";
   private static final String SELECT_BRANCH =
      "select br.*, parTx.build_id as p_tx_build_id, parTx.tx_type as p_tx_type, parTx.author as p_author, parTx.time as p_time, parTx.osee_comment as p_osee_comment, parTx.commit_art_id as p_commit_art_id, baseTx.build_id as b_tx_build_id, baseTx.tx_type as b_tx_type, baseTx.author as b_author, baseTx.time as b_time, baseTx.osee_comment as b_osee_comment, baseTx.commit_art_id as b_commit_art_id, source_branch_id, dest_branch_id from osee_branch br left outer join osee_merge on merge_branch_id = branch_id, osee_tx_details baseTx, osee_tx_details parTx where parent_transaction_id = parTx.transaction_id and baseline_transaction_id = baseTx.transaction_id and br.branch_id = ?";

   private final JdbcClient jdbcClient;
   private final OseeClient oseeClient;

   public DatabaseBranchAccessor(JdbcClient jdbcClient, OseeClient oseeClient) {
      this.jdbcClient = jdbcClient;
      this.oseeClient = oseeClient;
   }

   @Override
   public void load(IOseeCache<Branch> cache)  {
      String sql = String.format(SELECT_BRANCHES, jdbcClient.getDbType().getRecursiveWithSql());
      Map<MergeBranch, Long> mergeIdMap = new HashMap<>();
      jdbcClient.runQuery(stmt -> cache.cache(load(cache, stmt, mergeIdMap)), JDBC__MAX_FETCH_SIZE, sql);

      for (MergeBranch branch : mergeIdMap.keySet()) {
         Long destId = mergeIdMap.get(branch);
         Branch destBranch = cache.getById(destId);
         branch.setDestinationBranch(destBranch);
      }
      List<BranchViewData> branchAndViews = oseeClient.getApplicabilityEndpoint(CoreBranches.COMMON).getViews();
      List<Branch> branchViews = new ArrayList<>();

      for (BranchViewData data : branchAndViews) {
         BranchId branchId = data.getBranch();
         for (ArtifactId viewId : data.getBranchViews()) {
            Artifact viewArtifact = ArtifactLoader.loadArtifacts(Collections.singletonList(viewId), branchId,
               LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA, LoadType.RELOAD_CACHE, DeletionFlag.EXCLUDE_DELETED).get(0);

            Branch branch = cache.getById(branchId.getId());
            Branch viewBranch =
               Branch.createBranchView(branch, viewId, branch.getName() + " " + viewArtifact.getName());
            branchViews.add(viewBranch);
         }
      }

      ((BranchCache) cache).setBranchViews(branchViews);
   }

   /**
    * @return Branch or BranchDoesNotExist exception
    */
   public static Branch loadBranch(IOseeCache<Branch> cache, BranchId branchId) {
      Conditions.assertTrue(branchId.isValid(), "Invalid Branch %s", branchId);
      return ConnectionHandler.getJdbcClient().fetchOrException(
         () -> new BranchDoesNotExist("Branch could not be acquired for id [%s]", branchId.getIdString()),
         stmt -> load(cache, stmt, null), SELECT_BRANCH, branchId);
   }

   /**
    * @return Branch if it exists or null
    */
   public static Branch loadBranchOrNull(IOseeCache<Branch> cache, BranchId branchId) {
      return ConnectionHandler.getJdbcClient().fetch((Branch) null, stmt -> load(cache, stmt, null), SELECT_BRANCH,
         branchId);
   }

   private static Branch load(IOseeCache<Branch> cache, JdbcStatement stmt, Map<MergeBranch, Long> mergeIdMap) {
      Branch branch = createOrUpdate(cache, stmt, mergeIdMap);

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

   private static Branch createOrUpdate(IOseeCache<Branch> cache, JdbcStatement stmt, Map<MergeBranch, Long> mergeIdMap) {
      Long branchId = stmt.getLong("branch_id");
      String name = stmt.getString("branch_name");
      BranchType branchType = BranchType.valueOf(stmt.getInt("branch_type"));
      BranchState branchState = BranchState.getBranchState(stmt.getInt("branch_state"));
      boolean isArchived = BranchArchivedState.valueOf(stmt.getInt("archived")).isArchived();
      boolean inheritAccessControl = stmt.getInt("inherit_access_control") == 1;
      ArtifactId artifactId = ArtifactId.valueOf(stmt.getLong("associated_art_id"));

      Branch branch = cache.getById(branchId);
      if (branch == null) {
         if (branchType.isMergeBranch()) {
            MergeBranch mergeBranch =
               new MergeBranch(branchId, name, branchType, branchState, isArchived, inheritAccessControl);
            branch = mergeBranch;
            Branch sourceBranch = cache.getById(stmt.getLong("source_branch_id"));
            Branch destBranch = cache.getById(stmt.getLong("dest_branch_id"));

            if (destBranch == null) {
               mergeIdMap.put(mergeBranch, stmt.getLong("dest_branch_id"));
            }

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
      branch.setAssociatedArtifact(artifactId);
      return branch;
   }

   private static TransactionRecord createTx(boolean base, Branch branch, JdbcStatement stmt) {
      Long transactionId = stmt.getLong(base ? "baseline_transaction_id" : "parent_transaction_id");
      String comment = stmt.getString(base ? "b_osee_comment" : "p_osee_comment");
      Date timestamp = stmt.getTimestamp(base ? "b_time" : "p_time");
      UserId authorArtId = UserId.valueOf(stmt.getLong(base ? "b_author" : "p_author"));
      Integer commitArtId = stmt.getInt(base ? "b_commit_art_id" : "p_commit_art_id");
      TransactionDetailsType txType = TransactionDetailsType.toEnum(stmt.getInt(base ? "b_tx_type" : "p_tx_type"));
      Long txBuildId = stmt.getLong(base ? "b_tx_build_id" : "p_tx_build_id");
      return new TransactionRecord(transactionId, branch, comment, timestamp, authorArtId, commitArtId, txType,
         txBuildId);
   }
}