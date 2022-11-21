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

package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.commit.CommitOverrideOperations;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsBranchService {

   boolean isBranchInCommit(IAtsTeamWorkflow teamWf);

   /**
    * @return whether there is a working branch that is not committed
    */
   boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf);

   BranchToken getBranch(IAtsTeamWorkflow teamWf);

   BranchId getBranch(IAtsConfigObject configObject);

   BranchId getBranch(CommitConfigItem configObject);

   String getBranchShortName(CommitConfigItem commitConfigArt);

   boolean isBranchValid(CommitConfigItem configArt);

   boolean isAllObjectsToCommitToConfigured(IAtsTeamWorkflow teamWf);

   boolean isCommittedBranchExists(IAtsTeamWorkflow teamWf);

   boolean isBranchesAllCommitted(IAtsTeamWorkflow teamWf);

   BranchToken getWorkingBranch(IAtsTeamWorkflow teamWf);

   BranchToken getCommittedWorkingBranch(IAtsTeamWorkflow teamWf);

   Collection<CommitConfigItem> getConfigArtifactsConfiguredToCommitTo(IAtsTeamWorkflow teamWf);

   TransactionToken getEarliestTransactionId(IAtsTeamWorkflow teamWf);

   Collection<TransactionRecord> getTransactionIds(IAtsTeamWorkflow teamWf, boolean forMergeBranches);

   boolean isBranchesAllCommittedExcept(IAtsTeamWorkflow teamWf, BranchId branchToExclude);

   Collection<BranchToken> getBranchesCommittedTo(IAtsTeamWorkflow teamWf);

   Collection<BranchId> getBranchesLeftToCommit(IAtsTeamWorkflow teamWf);

   CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, CommitConfigItem configArt);

   CommitConfigItem getParentBranchConfigArtifactConfiguredToCommitTo(IAtsTeamWorkflow teamWf);

   CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, BranchId destinationBranch, CommitConfigItem configArt);

   BranchToken getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates);

   CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, BranchId destinationBranch);

   Collection<Object> getCommitTransactionsAndConfigItemsForTeamWf(IAtsTeamWorkflow teamWf);

   /**
    * @return Branch that is the configured branch to create working branch from.
    */
   BranchId getConfiguredBranchForWorkflow(IAtsTeamWorkflow teamWf);

   /**
    * Return working branch associated with SMA whether it is committed or not; This data is cached across all workflows
    * with the cache being updated by local and remote events.
    *
    * @param force == true does not used cached value
    */
   BranchToken getWorkingBranch(IAtsTeamWorkflow teamWf, boolean force);

   boolean isWorkingBranchEverCommitted(IAtsTeamWorkflow teamWf);

   Collection<Object> combineCommitTransactionsAndConfigItems(Collection<CommitConfigItem> commitConfigItems, Collection<TransactionRecord> commitTxs);

   Collection<TransactionRecord> getCommitTransactionsToUnarchivedBaselineBranchs(IAtsTeamWorkflow teamWf);

   BranchType getBranchType(BranchId branch);

   BranchState getBranchState(BranchId branch);

   Collection<TransactionRecord> getCommittedArtifactTransactionIds(IAtsTeamWorkflow teamWf);

   boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, BranchId destinationBranch);

   boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, BranchId workingBranch, BranchId destinationBranch);

   Result isCommitBranchAllowed(IAtsTeamWorkflow teamWf);

   Result isCreateBranchAllowed(IAtsTeamWorkflow teamWf);

   boolean branchExists(BranchId branch);

   boolean isArchived(BranchId branch);

   void archiveBranch(BranchId branch);

   TransactionRecord getCommitTransactionRecord(IAtsTeamWorkflow teamWf, BranchId branch);

   Collection<BranchId> getBranchesToCommitTo(IAtsTeamWorkflow teamWf);

   Collection<BranchId> getBranchesInCommit();

   boolean workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(IAtsTeamWorkflow teamWf, BranchId destinationBranch, Collection<? extends TransactionToken> commitTransactionIds);

   BranchId getParentBranch(BranchId branch);

   TransactionToken getBaseTransaction(BranchId branch);

   String getBranchName(IAtsTeamWorkflow teamWf);

   String getBranchName(BranchId branchId);

   void setBranchName(BranchToken branch, String name);

   Result moveWorkingBranch(IAtsTeamWorkflow fromTeamWf, IAtsTeamWorkflow toTeamWf, String newBranchName);

   Collection<BranchId> getBranches(BranchArchivedState unarchived, BranchType working);

   ArtifactId getAssociatedArtifactId(BranchId branch);

   BranchToken getWorkingBranchInWork(IAtsTeamWorkflow teamWf);

   List<ChangeItem> getChangeData(BranchId branch);

   List<ChangeItem> getChangeData(TransactionId transaction);

   void setAssociatedArtId(BranchId branch, ArtifactId artifact);

   CommitOverrideOperations getCommitOverrideOps();

   boolean isBaselinBranchConfigured(CommitConfigItem commitConfigArt);

   void setWorkingBranchCreationInProgress(IAtsTeamWorkflow teamWf, boolean inProgress);

   boolean isWorkingBranchCreationInProgress(IAtsTeamWorkflow teamWf);

   void setWorkingBranchCommitInProgress(IAtsTeamWorkflow teamWf, boolean inProgress);

   boolean isWorkingBranchCommitInProgress(IAtsTeamWorkflow teamWf);

   BranchToken getBranch(BranchId branch);

   Collection<ChangeItem> getChangeData(IAtsTeamWorkflow teamWf);

   boolean isAtsBranch(BranchId branchId);

   XResultData deleteBranch(BranchId branch);

   BranchData createBranch(BranchData branchData);

   BranchData validate(BranchData branchData, AtsApi atsApi);

   XResultData commitBranch(IAtsTeamWorkflow teamWf, BranchId destinationBranch, AtsUser user, XResultData rd);

}
