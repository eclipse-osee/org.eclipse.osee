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
import java.util.Date;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsBranchService {

   boolean isBranchInCommit(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   /**
    * @return whether there is a working branch that is not committed
    */
   boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IOseeBranch getBranch(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IOseeBranch getBranch(IAtsConfigObject configObject);

   IOseeBranch getBranch(ICommitConfigItem configObject);

   String getBranchShortName(ICommitConfigItem commitConfigArt);

   boolean isBranchValid(ICommitConfigItem configArt);

   IOseeBranch getBranchInherited(IAtsVersion version);

   boolean isAllObjectsToCommitToConfigured(IAtsTeamWorkflow teamWf);

   boolean isCommittedBranchExists(IAtsTeamWorkflow teamWf);

   boolean isBranchesAllCommitted(IAtsTeamWorkflow teamWf);

   IOseeBranch getWorkingBranch(IAtsTeamWorkflow teamWf);

   IOseeBranch getCommittedWorkingBranch(IAtsTeamWorkflow teamWf);

   Collection<ICommitConfigItem> getConfigArtifactsConfiguredToCommitTo(IAtsTeamWorkflow teamWf);

   ITransaction getEarliestTransactionId(IAtsTeamWorkflow teamWf);

   Collection<ITransaction> getTransactionIds(IAtsTeamWorkflow teamWf, boolean forMergeBranches);

   boolean isBranchesAllCommittedExcept(IAtsTeamWorkflow teamWf, IOseeBranch branchToExclude);

   Collection<IOseeBranch> getBranchesCommittedTo(IAtsTeamWorkflow teamWf);

   Collection<IOseeBranch> getBranchesLeftToCommit(IAtsTeamWorkflow teamWf);

   CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, ICommitConfigItem configArt);

   ICommitConfigItem getParentBranchConfigArtifactConfiguredToCommitTo(IAtsTeamWorkflow teamWf);

   CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, IOseeBranch destinationBranch, ICommitConfigItem configArt);

   IOseeBranch getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates);

   CommitStatus getCommitStatus(IAtsTeamWorkflow teamWf, IOseeBranch destinationBranch);

   Collection<Object> getCommitTransactionsAndConfigItemsForTeamWf(IAtsTeamWorkflow teamWf);

   IOseeBranch getConfiguredBranchForWorkflow(IAtsTeamWorkflow teamWf);

   IOseeBranch getWorkingBranch(IAtsTeamWorkflow teamWf, boolean force);

   boolean isWorkingBranchEverCommitted(IAtsTeamWorkflow teamWf);

   Collection<Object> combineCommitTransactionsAndConfigItems(Collection<ICommitConfigItem> configArtSet, Collection<ITransaction> commitTxs);

   Collection<ITransaction> getCommitTransactionsToUnarchivedBaselineBranchs(IAtsTeamWorkflow teamWf);

   BranchType getBranchType(IOseeBranch branch);

   BranchState getBranchState(IOseeBranch branch);

   Collection<ITransaction> getCommittedArtifactTransactionIds(IAtsTeamWorkflow teamWf);

   boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, IOseeBranch destinationBranch);

   boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, IOseeBranch workingBranch, IOseeBranch destinationBranch);

   Result isCommitBranchAllowed(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   Result isCreateBranchAllowed(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IOseeBranch getBranchByUuid(long branchUuid);

   boolean branchExists(long branchUuid);

   BranchArchivedState getArchiveState(IOseeBranch branch);

   IOseeBranch getBranch(ITransaction transactionId);

   ITransaction getCommitTransactionRecord(IAtsTeamWorkflow teamWf, IOseeBranch branch);

   Date getTimeStamp(ITransaction committedToParentTransRecord);

   Collection<IOseeBranch> getBranchesToCommitTo(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   Collection<IOseeBranch> getBranchesInCommit();

   boolean workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(IAtsTeamWorkflow teamWf, IOseeBranch destinationBranch, Collection<ITransaction> commitTransactionIds) throws OseeCoreException;

   IOseeBranch getParentBranch(IOseeBranch branch);

   ITransaction getBaseTransaction(IOseeBranch branch);

}
