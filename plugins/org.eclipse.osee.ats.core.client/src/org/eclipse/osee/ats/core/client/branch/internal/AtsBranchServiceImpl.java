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

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.model.change.CompareResults;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
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
      branchFilter.setAssociatedArtifact(teamWf.getStoreObject());

      return BranchManager.getBranch(branchFilter);
   }

   @Override
   public IOseeBranch getCommittedWorkingBranch(IAtsTeamWorkflow teamWf) {
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
   public boolean isMergeBranchExists(IAtsTeamWorkflow teamWf, BranchId workingBranch, BranchId destinationBranch)  {
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
   public BranchId getParentBranch(BranchId branch) {
      return BranchManager.getParentBranch(branch);
   }

   @Override
   public TransactionToken getBaseTransaction(BranchId branch) {
      return BranchManager.getBaseTransaction(branch);
   }

   @Override
   public void setBranchName(IOseeBranch branch, String name) {
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
      IOseeBranch workingBranch = getWorkingBranch(fromTeamWf);
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
      IAtsChangeSet changes = services.createChangeSet(log);
      fromTeamWf.getLog().addLog(LogType.Note, fromTeamWf.getStateMgr().getCurrentStateName(), log,
         services.getUserService().getCurrentUserId());
      changes.add(fromTeamWf);
      toTeamWf.getLog().addLog(LogType.Note, toTeamWf.getStateMgr().getCurrentStateName(), log,
         services.getUserService().getCurrentUserId());
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
   public CompareResults getChangeData(BranchId branch) {
      throw new UnsupportedOperationException("Not supported on client");
   }

   @Override
   public CompareResults getChangeData(TransactionToken transaction) {
      throw new UnsupportedOperationException("Not supported on client");
   }

   @Override
   public void setAssociatedArtId(BranchId branch, ArtifactId artifact) {
      BranchManager.setAssociatedArtifactId(branch, artifact);
   }

}