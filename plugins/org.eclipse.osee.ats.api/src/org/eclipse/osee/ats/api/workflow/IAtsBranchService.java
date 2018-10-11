/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.CommitOverrideOperations;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.CompareResults;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsBranchService {

   boolean isBranchInCommit(IAtsTeamWorkflow teamWf);

   /**
    * @return whether there is a working branch that is not committed
    */
   boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf);

   IOseeBranch getBranch(IAtsTeamWorkflow teamWf);

   BranchId getBranch(IAtsConfigObject configObject);

   BranchId getBranch(ICommitConfigItem configObject);

   String getBranchShortName(ICommitConfigItem commitConfigArt);

   boolean isBranchValid(ICommitConfigItem configArt);

   boolean isAllObjectsToCommitToConfigured(IAtsTeamWorkflow teamWf);

   boolean isCommittedBranchExists(IAtsTeamWorkflow teamWf);

   boolean isBranchesAllCommitted(IAtsTeamWorkflow teamWf);

   IOseeBranch getWorkingBranch(IAtsTeamWorkflow teamWf);

   IOseeBranch getCommittedWorkingBranch(IAtsTeamWorkflow teamWf);

   Collection<ICommitConfigItem> getConfigArtifactsConfiguredToCommitTo(IAtsTeamWorkflow teamWf);

   TransactionToken getEarliestTransactionId(IAtsTeamWorkflow teamWf);

   Collection<TransactionRecord> getTransactionIds(IAtsTeamWorkflow teamWf, boolean forMergeBranches);

   boolean isBranchesAllCommittedExcept(IAtsTeamWorkflow teamWf, BranchId branchToExclude);

   Collection<BranchId> getBranchesCommittedTo(IAtsTeamWorkflow teamWf);

   Collection<BranchId> getBranchesLeftToCommit(IAtsTeamWorkflow teamWf);

   CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, ICommitConfigItem configArt);

   ICommitConfigItem getParentBranchConfigArtifactConfiguredToCommitTo(IAtsTeamWorkflow teamWf);

   CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, BranchId destinationBranch, ICommitConfigItem configArt);

   IOseeBranch getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates);

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
   IOseeBranch getWorkingBranch(IAtsTeamWorkflow teamWf, boolean force);

   boolean isWorkingBranchEverCommitted(IAtsTeamWorkflow teamWf);

   Collection<Object> combineCommitTransactionsAndConfigItems(Collection<ICommitConfigItem> configArtSet, Collection<TransactionRecord> commitTxs);

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

   TransactionRecord getCommitTransactionRecord(IAtsTeamWorkflow teamWf, BranchId branch);

   Collection<BranchId> getBranchesToCommitTo(IAtsTeamWorkflow teamWf);

   Collection<BranchId> getBranchesInCommit();

   boolean workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(IAtsTeamWorkflow teamWf, BranchId destinationBranch, Collection<? extends TransactionToken> commitTransactionIds);

   BranchId getParentBranch(BranchId branch);

   TransactionToken getBaseTransaction(BranchId branch);

   String getBranchName(IAtsTeamWorkflow teamWf);

   String getBranchName(BranchId branchId);

   void setBranchName(IOseeBranch branch, String name);

   Result moveWorkingBranch(IAtsTeamWorkflow fromTeamWf, IAtsTeamWorkflow toTeamWf, String newBranchName);

   Collection<BranchId> getBranches(BranchArchivedState unarchived, BranchType working);

   ArtifactId getAssociatedArtifactId(BranchId branch);

   BranchId getWorkingBranchInWork(IAtsTeamWorkflow teamWf);

   CompareResults getChangeData(BranchId branch);

   CompareResults getChangeData(TransactionToken transaction);

   void setAssociatedArtId(BranchId branch, ArtifactId artifact);

   CommitOverrideOperations getCommitOverrideOps();

   boolean isBaselinBranchConfigured(ICommitConfigItem commitConfigArt);

   void setWorkingBranchCreationInProgress(IAtsTeamWorkflow teamWf, boolean inProgress);

   boolean isWorkingBranchCreationInProgress(IAtsTeamWorkflow teamWf);

   void setWorkingBranchCommitInProgress(IAtsTeamWorkflow teamWf, boolean inProgress);

   boolean isWorkingBranchCommitInProgress(IAtsTeamWorkflow teamWf);

}