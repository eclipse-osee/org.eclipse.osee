/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.rest.internal.branch;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.ats.rest.internal.workitem.operations.AtsBranchCommitOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchServiceImpl extends AbstractAtsBranchService {

   private final OrcsApi orcsApi;
   private final TransactionQuery txQuery;
   private final HashCollectionSet<ArtifactId, TransactionRecord> commitArtifactIdMap =
      new HashCollectionSet<>(true, HashSet::new);

   public AtsBranchServiceImpl(AtsApi atsServices, OrcsApi orcsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsServices, teamWorkflowProvidersLazy);
      this.orcsApi = orcsApi;
      txQuery = orcsApi.getQueryFactory().transactionQuery();
   }

   @Override
   public BranchToken getCommittedWorkingBranch(IAtsTeamWorkflow teamWf) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      ArtifactId artId = ArtifactId.valueOf(teamWf.getId());
      return query.andIsOfType(BranchType.WORKING).andStateIs(
         BranchState.COMMITTED).excludeArchived().andAssociatedArtId(artId).getResults().getExactlyOne();
   }

   @Override
   public BranchToken getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates) {
      BranchQuery branchQuery = orcsApi.getQueryFactory().branchQuery();
      if (negatedBranchStates.length > 0) {
         Collection<BranchState> statesToSearch = new LinkedList<>(Arrays.asList(BranchState.values()));
         statesToSearch.removeAll(Arrays.asList(negatedBranchStates));
         branchQuery.andStateIs(statesToSearch.toArray(new BranchState[statesToSearch.size()]));
      }
      branchQuery.andIsOfType(BranchType.WORKING);
      ArtifactId artId = ArtifactId.valueOf(teamWf.getId());
      branchQuery.andAssociatedArtId(artId);
      return branchQuery.getResultsAsId().getExactlyOne();
   }

   @Override
   public BranchType getBranchType(BranchId branch) {
      return getBranch(branch).getBranchType();
   }

   @Override
   public BranchState getBranchState(BranchId branch) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      Branch fullBranch = query.andId(branch).getResults().getExactlyOne();
      return fullBranch.getBranchState();
   }

   /**
    * Return true if merge branch exists in DB (whether archived or not)
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, BranchId destinationBranch) {
      return isMergeBranchExists(teamWf, getWorkingBranch(teamWf), destinationBranch);
   }

   /**
    * Method available for optimized checking of merge branches so don't have to re-acquire working branch if already
    * have
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, BranchId workingBranch, BranchId destinationBranch) {
      if (workingBranch.isInvalid()) {
         return false;
      }
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      query = query.andIsMergeFor(workingBranch, destinationBranch);
      return query.exists();
   }

   @Override
   public Branch getBranch(BranchId branch) {
      return orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getExactlyOne();
   }

   @Override
   public boolean branchExists(BranchId branch) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      return query.andId(branch).exists();
   }

   @Override
   public boolean isArchived(BranchId branch) {
      return getBranch(branch).isArchived();
   }

   @Override
   public void archiveBranch(BranchId branch) {
      try {
         orcsApi.getBranchOps().archiveUnarchiveBranch(branch, ArchiveOperation.ARCHIVE).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public Collection<TransactionRecord> getCommittedArtifactTransactionIds(IAtsTeamWorkflow teamWf) {
      ArtifactId artId = ArtifactId.valueOf(teamWf.getId());
      if (!commitArtifactIdMap.containsKey(artId)) {
         txQuery.andCommitId(teamWf.getArtifactId());
         txQuery.getResults().forEach(
            tx -> commitArtifactIdMap.put(artId, new TransactionRecord(tx.getId(), tx.getBranch(), tx.getComment(),
               tx.getDate(), tx.getAuthor(), tx.getCommitArt(), tx.getTxType(), tx.getBuildId())));
      }
      return commitArtifactIdMap.safeGetValues(artId);
   }

   @Override
   public BranchId getParentBranch(BranchId branch) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      Branch fullBranch = query.andId(branch).getResults().getExactlyOne();
      return fullBranch.getParentBranch();
   }

   @Override
   public TransactionToken getBaseTransaction(BranchId branch) {
      TransactionQuery txQuery = orcsApi.getQueryFactory().transactionQuery();
      return txQuery.andBranch(branch).andIs(TransactionDetailsType.Baselined).getResults().getExactlyOne();
   }

   @Override
   public void setBranchName(BranchToken branch, String name) {
      try {
         orcsApi.getBranchOps().changeBranchName(branch, name).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public String getBranchName(BranchId branchId) {
      return getBranch(branchId).getName();
   }

   @Override
   public Result moveWorkingBranch(IAtsTeamWorkflow fromTeamWf, IAtsTeamWorkflow toTeamWf, String newBranchName) {
      throw new UnsupportedOperationException("Not yet supported on server");
   }

   @Override
   public Collection<BranchId> getBranches(BranchArchivedState archivedState, BranchType branchTypes) {
      List<BranchId> branches = new LinkedList<>();
      for (Branch branch : orcsApi.getQueryFactory().branchQuery().andIsOfType(
         branchTypes).excludeArchived().getResults()) {
         branches.add(branch);
      }
      return branches;
   }

   @Override
   public ArtifactId getAssociatedArtifactId(BranchId branch) {
      return orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getExactlyOne().getAssociatedArtifact();
   }

   @Override
   public List<ChangeItem> getChangeData(TransactionId transaction) {
      TransactionQuery transQuery = orcsApi.getQueryFactory().transactionQuery();
      TransactionToken transTok = transQuery.andTxId(transaction).getTokens().iterator().next();
      TransactionReadable startTx =
         transQuery.andIsPriorTx(transTok).getResults().getAtMostOneOrDefault(TransactionReadable.SENTINEL);
      List<ChangeItem> results = orcsApi.getTransactionFactory().compareTxs(startTx, transaction);
      return results;
   }

   @Override
   public List<ChangeItem> getChangeData(BranchId branch) {
      TransactionQuery transactionQuery2 = orcsApi.getQueryFactory().transactionQuery();
      TransactionQuery transactionQuery3 = orcsApi.getQueryFactory().transactionQuery();
      BranchId parentBranch = atsApi.getBranchService().getParentBranch(branch);
      TransactionReadable startTx = transactionQuery2.andIsHead(branch).getResults().getExactlyOne();
      TransactionReadable endTx = transactionQuery3.andIsHead(parentBranch).getResults().getExactlyOne();
      List<ChangeItem> results = orcsApi.getTransactionFactory().compareTxs(startTx, endTx);
      return results;
   }

   @Override
   public void setAssociatedArtId(BranchId branch, ArtifactId artifact) {
      try {
         orcsApi.getBranchOps().associateBranchToArtifact(branch, artifact).call();
      } catch (Exception ex) {
         throw new OseeWrappedException(ex, "Error setting associated branch %s to artifact %s", branch, artifact);
      }
   }

   @Override
   public boolean isBaselinBranchConfigured(CommitConfigItem commitConfigArt) {
      return commitConfigArt.getBaselineBranchId().isValid();
   }

   @Override
   public XResultData deleteBranch(BranchId branch) {
      throw new UnsupportedOperationException("Not supported on server");
   }

   @Override
   public BranchData createBranch(BranchData branchData) {
      AtsBranchOperations ops = new AtsBranchOperations(atsApi, orcsApi);
      return ops.createBranch(branchData);
   }

   @Override
   public XResultData commitBranch(IAtsTeamWorkflow teamWf, BranchId destinationBranch, AtsUser asUser, XResultData rd) {

      AtsBranchCommitOperation commitOp =
         new AtsBranchCommitOperation(asUser, teamWf, atsApi, orcsApi, false, destinationBranch, rd);
      return commitOp.run();

   }

}