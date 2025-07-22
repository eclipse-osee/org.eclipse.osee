/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.mim.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.mim.MimPeerReviewApi;
import org.eclipse.osee.mim.types.ApplyResult;
import org.eclipse.osee.mim.types.PeerReviewApplyData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;

public class MimPeerReviewApiImpl implements MimPeerReviewApi {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;

   MimPeerReviewApiImpl(OrcsApi orcsApi, AtsApi atsApi) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
   }

   @Override
   public BranchId resetPeerReviewBranch(BranchId prBranchId) {
      //Purge all transactions from committing working MIM branches
      Branch prBranch =
         orcsApi.getQueryFactory().branchQuery().andId(prBranchId).getResults().getAtMostOneOrDefault(Branch.SENTINEL);
      List<TransactionReadable> list =
         orcsApi.getQueryFactory().transactionQuery().andBranch(prBranch).getResults().getList();
      List<TransactionId> transactionsToPurge =
         list.stream().filter(a -> a.getComment().startsWith("Commit Branch")).map(
            a -> TransactionId.valueOf(a.getId())).collect(Collectors.toList());
      Integer call = 1;
      if (transactionsToPurge.isEmpty()) {
         return prBranchId;
      }
      try {
         call = orcsApi.getTransactionFactory().purgeTransaction(transactionsToPurge).call();
      } catch (Exception ex) {
         throw new OseeCoreException("Error purging transactions");
      }
      if (call == 0) {
         return BranchId.SENTINEL;
      } else {
         orcsApi.getBranchOps().setBranchState(prBranchId, BranchState.CREATED);
      }
      return prBranchId;
   }

   @Override
   public List<BranchId> getAppliedBranches(BranchId prBranchId) {
      List<BranchId> appliedBranches = new ArrayList<>();
      String query = "select wb.branch_id, wb.branch_name " + //
         "from osee_branch b, osee_tx_details txd, osee_branch wb " + //
         "where b.branch_id = ? " + //
         "and b.branch_id = txd.branch_id and txd.transaction_id > b.baseline_transaction_id and txd.commit_art_id > ? and wb.associated_art_id = txd.commit_art_id and wb.archived = 0 and wb.branch_state in (1,0)";
      orcsApi.getJdbcService().getClient().runQuery(
         chStmt -> appliedBranches.add(BranchId.valueOf(chStmt.getLong("branch_id"))), query, prBranchId, 0);
      return appliedBranches;
   }

   @Override
   public ApplyResult applyWorkingBranches(BranchId prBranchId, PeerReviewApplyData data) {
      ApplyResult applyResult = new ApplyResult(true, "Apply log...\n");
      Branch prBranch =
         orcsApi.getQueryFactory().branchQuery().andId(prBranchId).getResults().getAtMostOneOrDefault(Branch.SENTINEL);
      List<BranchId> currentAppliedBranches = getAppliedBranches(prBranchId);
      List<ArtifactId> associatedWorkflows =
         orcsApi.getQueryFactory().branchQuery().andIds(currentAppliedBranches).getResults().getList().stream().map(
            a -> a.getAssociatedArtifact()).collect(Collectors.toList());
      if (prBranch.isInvalid()) {
         return applyResult;
      }
      if (!currentAppliedBranches.isEmpty() && !data.getRemoveBranches().isEmpty()) {
         currentAppliedBranches.removeIf(a -> data.getRemoveBranches().contains(a));
      }
      if (!data.getAddBranches().isEmpty()) {
         currentAppliedBranches.addAll(data.getAddBranches());
      }
      if (prBranch.getBranchState().isModified()) {
         prBranchId = resetPeerReviewBranch(prBranchId);
      }
      List<BranchId> branchesToApply = currentAppliedBranches.stream().distinct().collect(Collectors.toList());
      for (BranchId wBranch : branchesToApply) {
         IAtsTeamWorkflow wbWf =
            atsApi.getQueryService().getTeamWf(atsApi.getBranchService().getAssociatedArtifactId(wBranch));
         TransactionToken call = TransactionToken.SENTINEL;
         try {
            call = orcsApi.getBranchOps().commitBranch(orcsApi.userService().getUser(), wBranch, prBranchId).call();
         } catch (Exception ex) {
            throw new OseeCoreException(
               "Error applying branch:" + wBranch.getIdString() + " to branch: " + prBranch.getIdString() + " Exception: " + ex.getMessage());
         }
         if (call.isInvalid()) {
            applyResult.setSuccess(false);
            applyResult.setStatusText(applyResult.getStatusText() + "Error applying: " + wbWf.getName() + "\n");
            break;
         } else {
            applyResult.setStatusText(applyResult.getStatusText() + "Applied: " + wbWf.getName() + "\n");
         }
      }
      return applyResult;

   }
}
