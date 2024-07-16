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

package org.eclipse.osee.ats.ide.branch.internal;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchServiceImpl extends AbstractAtsBranchService {

   public AtsBranchServiceImpl(AtsApi atsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsApi, teamWorkflowProvidersLazy);
   }

   /**
    * Return working branch associated with SMA, even if it's been archived; This data is cached across all workflows
    * with the cache being updated by local and remote events. Filters out rebaseline branches (which are working
    * branches also).
    */
   @Override
   public BranchToken getWorkingBranchExcludeStates(IAtsTeamWorkflow teamWf, BranchState... negatedBranchStates) {
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING, BranchType.BASELINE);
      branchFilter.setNegatedBranchStates(negatedBranchStates);
      branchFilter.setAssociatedArtifact(teamWf.getStoreObject());

      return BranchManager.getBranch(branchFilter);
   }

   @Override
   public BranchToken getCommittedWorkingBranch(IAtsTeamWorkflow teamWf) {
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING);
      branchFilter.setBranchStates(BranchState.COMMITTED);
      branchFilter.setAssociatedArtifact(teamWf.getStoreObject());
      return BranchManager.getBranch(branchFilter);
   }

   @Override
   public BranchType getBranchType(BranchId branch) {
      return BranchManager.getType(branch);
   }

   @Override
   public BranchState getBranchState(BranchId branch) {
      return BranchManager.getState(branch);
   }

   @Override
   public Collection<TransactionRecord> getCommittedArtifactTransactionIds(IAtsTeamWorkflow teamWf) {
      return TransactionManager.getCommittedArtifactTransactionIds(teamWf.getStoreObject());
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
      return BranchManager.doesMergeBranchExist(workingBranch, destinationBranch);
   }

   @Override
   public boolean isArchived(BranchId branch) {
      return BranchManager.isArchived(branch);
   }

   @Override
   public void archiveBranch(BranchId branch) {
      BranchManager.archiveUnArchiveBranch(branch, BranchArchivedState.ARCHIVED);
   }

   @Override
   public BranchId getParentBranch(BranchId branch) {
      return BranchManager.getParentBranch(branch);
   }

   @Override
   public TransactionToken getBaseTransaction(BranchId branch) {
      return BranchManager.getBaseTransaction(branch);
   }

   @Override
   public void setBranchName(BranchToken branch, String name) {
      BranchManager.setName(branch, name);
   }

   @Override
   public String getBranchName(BranchId branch) {
      return BranchManager.getBranchName(branch);
   }

   @Override
   public boolean branchExists(BranchId branch) {
      return BranchManager.branchExists(branch);
   }

   @Override
   public Result moveWorkingBranch(IAtsTeamWorkflow fromTeamWf, IAtsTeamWorkflow toTeamWf, String newBranchName) {
      if (isCommittedBranchExists(fromTeamWf)) {
         return new Result(false, "Can not move a branch that has commits");
      }
      BranchToken workingBranch = getWorkingBranch(fromTeamWf);
      if (workingBranch == null) {
         return new Result(false, "Working Branch does not exist for workflow " + toTeamWf.toStringWithId());
      }
      if (getWorkingBranch(toTeamWf).isValid()) {
         return new Result(false, String.format(
            "Can not move Working Branch to workflow %s; It already has a working branch.", toTeamWf.toStringWithId()));
      }
      try {
         BranchManager.setAssociatedArtifactId(workingBranch, toTeamWf.getStoreObject());
      } catch (Exception ex) {
         return new Result(false, String.format("Failure setting new associated artifact %s for branch [%s] ",
            toTeamWf.toStringWithId(), workingBranch));
      }
      String log = String.format("Working Branch [%s] moved from %s to %s.", workingBranch, fromTeamWf.toStringWithId(),
         toTeamWf.toStringWithId());
      try {
         BranchManager.setName(workingBranch, newBranchName);
      } catch (Exception ex) {
         return new Result(false,
            String.format("Failure setting new branch name [%s] for branch [%s] ", newBranchName, workingBranch));
      }
      IAtsChangeSet changes = atsApi.createChangeSet(log);
      fromTeamWf.getLog().addLog(LogType.Note, fromTeamWf.getCurrentStateName(), log,
         atsApi.getUserService().getCurrentUserId());
      changes.add(fromTeamWf);
      toTeamWf.getLog().addLog(LogType.Note, toTeamWf.getCurrentStateName(), log,
         atsApi.getUserService().getCurrentUserId());
      changes.add(toTeamWf);
      changes.execute();
      return Result.TrueResult;
   }

   @Override
   public Collection<BranchId> getBranches(BranchArchivedState archivedState, BranchType branchTypes) {
      return Collections.castAll(BranchManager.getBranches(archivedState, branchTypes));
   }

   @Override
   public ArtifactId getAssociatedArtifactId(BranchId branch) {
      return BranchManager.getAssociatedArtifactId(branch);
   }

   @Override
   public List<ChangeItem> getChangeData(BranchId branch) {
      return atsApi.getServerEndpoints().getActionEndpoint().getBranchChangeData(branch);
   }

   @Override
   public List<ChangeItem> getChangeData(TransactionId transaction) {
      return atsApi.getServerEndpoints().getActionEndpoint().getTransactionChangeData(transaction);
   }

   @Override
   public void setAssociatedArtId(BranchId branch, ArtifactId artifact) {
      BranchManager.setAssociatedArtifactId(branch, artifact);
   }

   @Override
   public boolean isBaselinBranchConfigured(CommitConfigItem commitConfigItem) {
      return false;
   }

   @Override
   public BranchToken getBranch(BranchId branch) {
      return BranchManager.getBranchToken(branch);
   }

   @Override
   public XResultData deleteBranch(BranchId branch) {
      XResultData rd = new XResultData();
      try {
         BranchManager.deleteBranch(branch);
      } catch (Exception ex) {
         rd.errorf("Exception deleting branch %s", branch.toString());
      }
      return rd;
   }

   @Override
   public BranchData createBranch(BranchData branchData) {
      return atsApi.getServerEndpoints().getConfigEndpoint().createBranch(branchData);
   }

   @Override
   public XResultData commitBranch(IAtsTeamWorkflow teamWf, BranchId destinationBranch, AtsUser user, XResultData rd) {
      throw new UnsupportedOperationException("Not supported on client");
   }

   @Override
   public XResultData commitWorkingBranch(IAtsTeamWorkflow teamWf, boolean commitPopup, boolean overrideStateValidation,
      BranchId destinationBranch, boolean archiveWorkingBranch, XResultData rd) {
      throw new UnsupportedOperationException("Not supported on client");
   }

}