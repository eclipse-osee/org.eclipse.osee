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
package org.eclipse.osee.ats.core.client.branch.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.ats.core.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchServiceImpl extends AbstractAtsBranchService {

   public AtsBranchServiceImpl(IAtsServices atsServices, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsServices, teamWorkflowProvidersLazy);
   }

   /**
    * Return working branch associated with SMA, even if it's been archived; This data is cached across all workflows
    * with the cache being updated by local and remote events. Filters out rebaseline branches (which are working
    * branches also).
    */
   @Override
   public IOseeBranch getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates) {
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING, BranchType.BASELINE);
      branchFilter.setNegatedBranchStates(negatedBranchStates);
      branchFilter.setAssociatedArtifact((Artifact) teamWf.getStoreObject());

      List<Branch> branches = BranchManager.getBranches(branchFilter);

      if (branches.isEmpty()) {
         return null;
      } else if (branches.size() > 1) {
         throw new MultipleBranchesExist(
            "Unexpected multiple associated un-deleted working branches found for workflow [%s]. Branches [%s].",
            teamWf.getAtsId(), getBranchesStr(branches));
      } else {
         return branches.get(0);
      }
   }

   @Override
   public IOseeBranch getCommittedWorkingBranch(IAtsTeamWorkflow teamWf) {
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING);
      branchFilter.setBranchStates(BranchState.COMMITTED);
      branchFilter.setAssociatedArtifact((Artifact) teamWf.getStoreObject());
      List<Branch> branches = BranchManager.getBranches(branchFilter);
      if (branches.isEmpty()) {
         return null;
      } else if (branches.size() > 1) {
         throw new MultipleBranchesExist(
            "Unexpected multiple associated un-deleted committed working branches found for workflow [%s]. Branches [%s].",
            teamWf.getAtsId(), getBranchesStr(branches));
      } else {
         return branches.get(0);
      }
   }

   private String getBranchesStr(List<Branch> branches) {
      StringBuilder sb = new StringBuilder();
      for (Branch branch : branches) {
         sb.append(branch.toStringWithDetails());
         sb.append(",");
      }
      return sb.toString();
   }

   @Override
   public BranchType getBranchType(IOseeBranch branch) {
      return BranchManager.getBranchType(branch);
   }

   @Override
   public BranchState getBranchState(IOseeBranch branch) {
      return BranchManager.getState(branch);
   }

   @Override
   public Collection<ITransaction> getCommittedArtifactTransactionIds(IAtsTeamWorkflow teamWf) {
      List<ITransaction> transactions = new ArrayList<>();
      for (TransactionRecord trans : TransactionManager.getCommittedArtifactTransactionIds(
         (Artifact) teamWf.getStoreObject())) {
         transactions.add(trans);
      }
      return transactions;
   }

   /**
    * Method available for optimized checking of merge branches so don't have to re-acquire working branch if already
    * have
    */
   @Override
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, IOseeBranch workingBranch, IOseeBranch destinationBranch) throws OseeCoreException {
      if (workingBranch == null) {
         return false;
      }
      return BranchManager.doesMergeBranchExist(workingBranch, destinationBranch);
   }

   public boolean isMergeCompleted(TeamWorkFlowArtifact teamWf, IOseeBranch destinationBranch) throws OseeCoreException {
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(destinationBranch,
         AtsClientService.get().getBranchService().getWorkingBranch(teamWf));
      return !conflictManager.remainingConflictsExist();
   }

   @Override
   public IOseeBranch getBranchByUuid(long branchUuid) {
      return BranchManager.getBranch(branchUuid);
   }

   @Override
   public boolean branchExists(long branchUuid) {
      return BranchManager.branchExists(branchUuid);
   }

   @Override
   public BranchArchivedState getArchiveState(IOseeBranch branch) {
      return BranchManager.getBranch(branch).getArchiveState();
   }

   @Override
   public Date getTimeStamp(ITransaction transaction) {
      return TransactionManager.getTransactionId(transaction.getGuid()).getTimeStamp();
   }

   @Override
   public IOseeBranch getParentBranch(IOseeBranch destinationBranch) {
      return BranchManager.getBranch(destinationBranch).getParentBranch();
   }

   @Override
   public ITransaction getBaseTransaction(IOseeBranch destinationBranch) {
      return BranchManager.getBranch(destinationBranch).getBaseTransaction();
   }

}
