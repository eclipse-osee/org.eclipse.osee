/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.branch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewDefinitionManager;
import org.eclipse.osee.ats.core.client.review.PeerReviewDefinitionManager;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.workflow.stateitem.AtsStateItemCoreManager;
import org.eclipse.osee.ats.core.client.workflow.stateitem.IAtsStateItemCore;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.CatchAndReleaseJob;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchManagerCore {

   private static Map<String, IOseeBranch> idToWorkingBranchCache = new HashMap<String, IOseeBranch>();
   private static Map<String, Long> idToWorkingBranchCacheUpdated = new HashMap<String, Long>(50);
   public static Set<IOseeBranch> branchesInCommit = new HashSet<IOseeBranch>();

   /**
    * Return working branch associated with SMA whether it is committed or not; This data is cached across all workflows
    * with the cache being updated by local and remote events.
    */
   public static IOseeBranch getWorkingBranch(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return getWorkingBranch(teamArt, false);
   }

   /**
    * Return working branch associated with SMA whether it is committed or not; This data is cached across all workflows
    * with the cache being updated by local and remote events.
    * 
    * @param force == true does not used cached value
    */
   public static IOseeBranch getWorkingBranch(TeamWorkFlowArtifact teamArt, boolean force) throws OseeCoreException {
      long now = new Date().getTime();
      boolean notSet = idToWorkingBranchCacheUpdated.get(teamArt.getAtsId()) == null;
      if (AtsUtilCore.isInTest() || notSet || force || (now - idToWorkingBranchCacheUpdated.get(teamArt.getAtsId()) > 1000)) {
         idToWorkingBranchCache.put(
            teamArt.getAtsId(),
            getWorkingBranchExcludeStates(teamArt, BranchState.REBASELINED, BranchState.DELETED, BranchState.PURGED,
               BranchState.COMMIT_IN_PROGRESS, BranchState.CREATION_IN_PROGRESS, BranchState.DELETE_IN_PROGRESS,
               BranchState.PURGE_IN_PROGRESS));
         idToWorkingBranchCacheUpdated.put(teamArt.getAtsId(), now);
      }
      return idToWorkingBranchCache.get(teamArt.getAtsId());
   }

   public static IOseeBranch getCommittedWorkingBranch(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING);
      branchFilter.setBranchStates(BranchState.COMMITTED);
      branchFilter.setAssociatedArtifact(teamArt);
      List<Branch> branches = BranchManager.getBranches(branchFilter);
      if (branches.isEmpty()) {
         return null;
      } else if (branches.size() > 1) {
         throw new MultipleBranchesExist(
            "Unexpected multiple associated un-deleted working branches found for workflow [%s]", teamArt.getAtsId());
      } else {
         return branches.get(0);
      }
   }

   /**
    * Return working branch associated with SMA, even if it's been archived; This data is cached across all workflows
    * with the cache being updated by local and remote events. Filters out rebaseline branches (which are working
    * branches also).
    */
   public static IOseeBranch getWorkingBranchExcludeStates(TeamWorkFlowArtifact teamArt, BranchState... negatedBranchStates) throws OseeCoreException {
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING, BranchType.BASELINE);
      branchFilter.setNegatedBranchStates(negatedBranchStates);
      branchFilter.setAssociatedArtifact(teamArt);

      List<Branch> branches = BranchManager.getBranches(branchFilter);

      if (branches.isEmpty()) {
         return null;
      } else if (branches.size() > 1) {
         throw new MultipleBranchesExist(
            "Unexpected multiple associated un-deleted working branches found for workflow [%s]", teamArt.getAtsId());
      } else {
         return branches.get(0);
      }
   }

   /**
    * @return whether there is a working branch that is not committed
    */
   public static boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) AtsClientService.get().getArtifact(teamWf);
      IOseeBranch branch = getWorkingBranch(teamArt);
      return branch != null && !BranchManager.getState(branch).isCommitted();
   }

   public static boolean isWorkingBranchInWork(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      IOseeBranch branch = getWorkingBranch(teamArt);
      return branch != null && !BranchManager.getState(branch).isCommitted();
   }

   /**
    * Return true if merge branch exists in DB (whether archived or not)
    */
   public static boolean isMergeBranchExists(TeamWorkFlowArtifact teamArt, IOseeBranch destinationBranch) throws OseeCoreException {
      return isMergeBranchExists(teamArt, getWorkingBranch(teamArt), destinationBranch);
   }

   /**
    * Method available for optimized checking of merge branches so don't have to re-acquire working branch if already
    * have
    */
   public static boolean isMergeBranchExists(TeamWorkFlowArtifact teamArt, IOseeBranch workingBranch, IOseeBranch destinationBranch) throws OseeCoreException {
      if (workingBranch == null) {
         return false;
      }
      return BranchManager.doesMergeBranchExist(workingBranch, destinationBranch);
   }

   public static boolean isMergeCompleted(TeamWorkFlowArtifact teamArt, IOseeBranch destinationBranch) throws OseeCoreException {
      ConflictManagerExternal conflictManager =
         new ConflictManagerExternal(destinationBranch, getWorkingBranch(teamArt));
      return !conflictManager.remainingConflictsExist();
   }

   public static TransactionRecord getCommitTransactionRecord(TeamWorkFlowArtifact teamArt, IOseeBranch branch) throws OseeCoreException {
      if (branch == null) {
         return null;
      }

      Collection<TransactionRecord> transactions = TransactionManager.getCommittedArtifactTransactionIds(teamArt);
      for (TransactionRecord transId : transactions) {
         if (transId.getBranchId() == branch.getUuid()) {
            return transId;
         }
      }
      return null;
   }

   public static TransactionRecord getCommitTransactionRecord(TeamWorkFlowArtifact teamArt, ICommitConfigItem configArt) throws OseeCoreException {
      IOseeBranch branch = AtsClientService.get().getBranchService().getBranch(configArt);
      return getCommitTransactionRecord(teamArt, branch);
   }

   public static CommitStatus getCommitStatus(TeamWorkFlowArtifact teamArt, ICommitConfigItem configArt) throws OseeCoreException {
      IOseeBranch destinationBranch = AtsClientService.get().getBranchService().getBranch(configArt);
      return getCommitStatus(teamArt, destinationBranch, null);
   }

   public static CommitStatus getCommitStatus(TeamWorkFlowArtifact teamArt, IOseeBranch destinationBranch, ICommitConfigItem configArt) throws OseeCoreException {
      Branch workingBranch = teamArt.getWorkingBranch();
      if (workingBranch != null) {
         if (workingBranch.getBranchState().isRebaselineInProgress()) {
            return CommitStatus.Rebaseline_In_Progress;
         }
      }

      if (destinationBranch == null) {
         return CommitStatus.Branch_Not_Configured;
      }

      Collection<TransactionRecord> transactions = TransactionManager.getCommittedArtifactTransactionIds(teamArt);
      boolean mergeBranchExists = AtsBranchManagerCore.isMergeBranchExists(teamArt, destinationBranch);

      for (TransactionRecord transId : transactions) {
         if (destinationBranch.equals(transId.getBranch())) {
            if (mergeBranchExists) {
               return CommitStatus.Committed_With_Merge;
            } else {
               return CommitStatus.Committed;
            }
         }
      }

      if (workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(teamArt, destinationBranch,
         transactions)) {
         return CommitStatus.No_Commit_Needed;
      }

      Result result = new Result(false);
      if (configArt == null) {
         result = AtsBranchManagerCore.isCommitBranchAllowed(teamArt);
      } else {
         result = configArt.isAllowCommitBranchInherited();
      }
      if (result.isFalse()) {
         return CommitStatus.Branch_Commit_Disabled;
      }
      if (AtsBranchManagerCore.getWorkingBranch(teamArt) == null) {
         return CommitStatus.Working_Branch_Not_Created;
      }
      if (mergeBranchExists) {
         return CommitStatus.Merge_In_Progress;
      }

      return CommitStatus.Commit_Needed;
   }

   public static CommitStatus getCommitStatus(TeamWorkFlowArtifact teamArt, IOseeBranch destinationBranch) throws OseeCoreException {
      CommitStatus commitStatus = getCommitStatus(teamArt, destinationBranch, null);
      return commitStatus;
   }

   private static boolean workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(TeamWorkFlowArtifact teamArt, IOseeBranch destinationBranch, Collection<TransactionRecord> commitTransactionIds) throws OseeCoreException {
      Branch destinationBranchParent = BranchManager.getParentBranch(destinationBranch);
      if (destinationBranchParent.getBranchType() == BranchType.SYSTEM_ROOT) {
         return false;
      }

      TransactionRecord committedToParentTransRecord = null;
      for (TransactionRecord transId : commitTransactionIds) {
         if (transId.getBranch().equals(destinationBranchParent)) {
            committedToParentTransRecord = transId;
            break;
         }
      }
      if (committedToParentTransRecord != null) {
         if (BranchManager.getBaseTransaction(destinationBranch).getTimeStamp().after(
            committedToParentTransRecord.getTimeStamp())) {
            return true;
         }
      }
      return workingBranchCommittedToDestinationBranchParentPriorToDestinationBranchCreation(teamArt,
         destinationBranchParent, commitTransactionIds);
   }

   public static Result isCommitBranchAllowed(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      if (!teamArt.isTeamWorkflow()) {
         return Result.FalseResult;
      }
      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (!AtsVersionService.get().hasTargetedVersion(teamArt)) {
            return new Result(false, "Workflow not targeted for Version");
         }
         IAtsVersion targetedVersion = AtsVersionService.get().getTargetedVersion(teamArt);
         Result result = targetedVersion.isAllowCommitBranchInherited();
         if (result.isFalse()) {
            return result;
         }

         if (!AtsClientService.get().getBranchService().isBranchValid(targetedVersion)) {
            return new Result(false, "Parent Branch not configured for Version [" + targetedVersion + "]");
         }
         return Result.TrueResult;

      } else {
         Result result = teamArt.getTeamDefinition().isAllowCommitBranchInherited();
         if (result.isFalse()) {
            return result;
         }

         if (!AtsClientService.get().getBranchService().isBranchValid(teamArt.getTeamDefinition())) {
            return new Result(false,
               "Parent Branch not configured for Team Definition [" + teamArt.getTeamDefinition() + "]");
         }
         return Result.TrueResult;
      }
   }

   public static Result isCreateBranchAllowed(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      if (!teamArt.isTeamWorkflow()) {
         return Result.FalseResult;
      }

      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (!AtsVersionService.get().hasTargetedVersion(teamArt)) {
            return new Result(false, "Workflow not targeted for Version");
         }
         IAtsVersion targetedVersion = AtsVersionService.get().getTargetedVersion(teamArt);
         Result result = targetedVersion.isAllowCreateBranchInherited();
         if (result.isFalse()) {
            return result;
         }

         if (!AtsClientService.get().getBranchService().isBranchValid(targetedVersion)) {
            return new Result(false, "Parent Branch not configured for Version [" + targetedVersion + "]");
         }
         IOseeBranch baselineBranch =
            AtsClientService.get().getBranchService().getBranch((IAtsConfigObject) targetedVersion);
         if (!BranchManager.getBranchType(baselineBranch).isBaselineBranch()) {
            return new Result(false, "Parent Branch must be of Baseline branch type.  See Admin for configuration.");
         }
         return Result.TrueResult;

      } else {
         Result result = teamArt.getTeamDefinition().isAllowCreateBranchInherited();
         if (result.isFalse()) {
            return result;
         }

         if (!AtsClientService.get().getBranchService().isBranchValid(teamArt.getTeamDefinition())) {
            return new Result(false,
               "Parent Branch not configured for Team Definition [" + teamArt.getTeamDefinition() + "]");
         }
         IOseeBranch baselineBranch =
            AtsClientService.get().getBranchService().getBranch((IAtsConfigObject) teamArt.getTeamDefinition());
         if (!BranchManager.getBranchType(baselineBranch).isBaselineBranch()) {
            return new Result(false, "Parent Branch must be of Baseline branch type.  See Admin for configuration.");
         }
         return Result.TrueResult;
      }
   }

   /**
    * Returns true if there was ever a commit of a working branch regardless of whether the working branch is archived
    * or not.
    */
   public static boolean isWorkingBranchEverCommitted(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return getBranchesCommittedTo(teamArt).size() > 0;
   }

   public static Collection<ICommitConfigItem> getConfigArtifactsConfiguredToCommitTo(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<ICommitConfigItem> configObjects = new HashSet<ICommitConfigItem>();
      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (AtsVersionService.get().hasTargetedVersion(teamArt)) {
            AtsVersionService.get().getTargetedVersion(teamArt).getParallelVersions(configObjects);
         }
      } else {
         if (teamArt.isTeamWorkflow() && AtsClientService.get().getBranchService().isBranchValid(
            teamArt.getTeamDefinition())) {
            configObjects.add(teamArt.getTeamDefinition());
         }
      }
      return configObjects;
   }

   public static ICommitConfigItem getParentBranchConfigArtifactConfiguredToCommitTo(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (AtsVersionService.get().hasTargetedVersion(teamArt)) {
            return AtsVersionService.get().getTargetedVersion(teamArt);
         }
      } else {
         if (teamArt.isTeamWorkflow() && AtsClientService.get().getBranchService().isBranchValid(
            teamArt.getTeamDefinition())) {
            return teamArt.getTeamDefinition();
         }
      }
      return null;
   }

   /**
    * @return false if any object in parallel configuration is not configured with a valid branch
    */
   public static boolean isAllObjectsToCommitToConfigured(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<ICommitConfigItem> configs = getConfigArtifactsConfiguredToCommitTo(teamArt);
      for (ICommitConfigItem config : configs) {
         if (!AtsClientService.get().getBranchService().isBranchValid(config)) {
            return false;
         }
         if (!BranchManager.branchExists(config.getBaselineBranchUuid())) {
            return false;
         }
      }
      return true;
   }

   public static Collection<IOseeBranch> getBranchesLeftToCommit(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<IOseeBranch> branchesLeft = new HashSet<IOseeBranch>();
      Collection<IOseeBranch> committedTo = getBranchesCommittedTo(teamArt);
      for (IOseeBranch branchToCommit : getBranchesToCommitTo(teamArt)) {
         if (!committedTo.contains(branchToCommit) && !isNoCommitNeeded(teamArt, branchToCommit)) {
            branchesLeft.add(branchToCommit);
         }
      }
      return branchesLeft;
   }

   public static Collection<IOseeBranch> getBranchesToCommitTo(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<IOseeBranch> branches = new HashSet<IOseeBranch>();
      for (ICommitConfigItem obj : getConfigArtifactsConfiguredToCommitTo(teamArt)) {
         if (AtsClientService.get().getBranchService().isBranchValid(obj)) {
            branches.add(AtsClientService.get().getBranchService().getBranch(obj));
         }
      }
      return branches;
   }

   public static Collection<IOseeBranch> getBranchesCommittedTo(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<IOseeBranch> branches = new HashSet<IOseeBranch>();
      for (TransactionRecord transId : getTransactionIds(teamArt, false)) {
         branches.add(transId.getBranch());
      }
      return branches;
   }

   /**
    * @return true if at least one destination branch committed to
    */
   public static boolean isCommittedBranchExists(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return !getBranchesCommittedTo(teamArt).isEmpty();
   }

   public static boolean isNoCommitNeeded(TeamWorkFlowArtifact teamArt, IOseeBranch destinationBranch) throws OseeCoreException {
      return getCommitStatus(teamArt, destinationBranch) == CommitStatus.No_Commit_Needed;
   }

   /**
    * Return true if all commit destination branches are configured and have been committed to
    */
   public static boolean isBranchesAllCommitted(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<IOseeBranch> committedTo = getBranchesCommittedTo(teamArt);
      for (IOseeBranch destBranch : getBranchesToCommitTo(teamArt)) {
         if (!committedTo.contains(destBranch) && !isNoCommitNeeded(teamArt, destBranch)) {
            return false;
         }
      }
      return true;
   }

   public static boolean isBranchesAllCommittedExcept(TeamWorkFlowArtifact teamArt, IOseeBranch branchToExclude) throws OseeCoreException {
      Collection<IOseeBranch> committedTo = getBranchesCommittedTo(teamArt);
      for (IOseeBranch destBranch : getBranchesToCommitTo(teamArt)) {
         if (!destBranch.equals(branchToExclude) && !committedTo.contains(destBranch) && !isNoCommitNeeded(teamArt,
            destBranch)) {
            return false;
         }
      }
      return true;
   }

   /**
    * @return Branch that is the configured branch to create working branch from.
    */
   public static IOseeBranch getConfiguredBranchForWorkflow(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      IOseeBranch parentBranch = null;

      // Check for parent branch uuid in Version artifact
      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         IAtsVersion verArt = AtsVersionService.get().getTargetedVersion(teamArt);
         if (verArt != null) {
            parentBranch = AtsClientService.get().getBranchService().getBranch((IAtsConfigObject) verArt);
         }
      }

      // If not defined in version, check for parent branch from team definition
      if (parentBranch == null && teamArt.isTeamWorkflow() && AtsClientService.get().getBranchService().isBranchValid(
         teamArt.getTeamDefinition())) {
         parentBranch =
            AtsClientService.get().getBranchService().getBranch((IAtsConfigObject) teamArt.getTeamDefinition());
      }

      // If not defined, return null
      return parentBranch;
   }

   public static boolean isBranchInCommit(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      if (!isWorkingBranchInWork(teamArt)) {
         return false;
      }
      return branchesInCommit.contains(getWorkingBranch(teamArt));
   }

   public static Long getId(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      IOseeBranch branch = getWorkingBranch(teamArt);
      if (branch == null) {
         return null;
      }
      return branch.getUuid();
   }

   /**
    * @return Logically combines the results from getConfigArtifactsConfiguredToCommitTo() and
    * getCommitTransactionsToUnarchivedBaselineBranchs() into a single Collection of Objects. Objects are selected from
    * getConfigArtifactsConfiguredToCommitTo() first. Then compared to the branches in the Collection of TxRecords from
    * getCommitTransactionsToUnarchivedBaselineBranchs(). The TxRecords take LESS priority than the ICommitConfigArts
    * from getConfigArtifactsConfiguredToCommitTo()
    */
   public static Collection<Object> getCommitTransactionsAndConfigItemsForTeamWf(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<ICommitConfigItem> configArtSet = getConfigArtifactsConfiguredToCommitTo(teamArt);
      Collection<TransactionRecord> commitTxs = getCommitTransactionsToUnarchivedBaselineBranchs(teamArt);
      Collection<Object> commitMgrInputObjs = combineCommitTransactionsAndConfigItems(configArtSet, commitTxs);
      return commitMgrInputObjs;
   }

   /**
    * This method was refactored from above so it could be tested independently
    */
   public static Collection<Object> combineCommitTransactionsAndConfigItems(Collection<ICommitConfigItem> configArtSet, Collection<TransactionRecord> commitTxs) throws OseeCoreException {
      // commitMgrInputObjs will hold a union of all commits from configArtSet and commitTxs.
      // - first, we addAll configArtSet
      // - next, we loop through commitTxs and for any tx that has the same branch as ANY pre-existing commit
      //    in configArtSet we do NOT add it to commitMgrInputObjs.
      Collection<Object> commitMgrInputObjs = new HashSet<Object>();
      commitMgrInputObjs.addAll(configArtSet);
      //for each tx commit...
      for (TransactionRecord txRecord : commitTxs) {
         Branch txBranch = txRecord.getBranch();
         boolean isCommitAlreadyPresent = false;
         // ... compare the branch of the tx commit to all the parent branches in configArtSet and do NOT add the tx
         // commit if it is already represented.
         for (ICommitConfigItem configArt : configArtSet) {
            IOseeBranch configArtBranch = AtsClientService.get().getBranchService().getBranch(configArt);
            if (txBranch == configArtBranch) {
               isCommitAlreadyPresent = true;
               break;
            }
         }
         if (!isCommitAlreadyPresent) {
            commitMgrInputObjs.add(txRecord);
         }
      }
      return commitMgrInputObjs;
   }

   private static Collection<TransactionRecord> getCommitTransactionsToUnarchivedBaselineBranchs(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<TransactionRecord> committedTransactions =
         TransactionManager.getCommittedArtifactTransactionIds(teamArt);

      Collection<TransactionRecord> transactionIds = new ArrayList<TransactionRecord>();
      for (TransactionRecord transactionId : committedTransactions) {
         // exclude working branches including branch states that are re-baselined
         Branch branch = transactionId.getBranch();
         if (branch.getBranchType().isBaselineBranch() && branch.getArchiveState().isUnArchived()) {
            transactionIds.add(transactionId);
         }
      }
      return transactionIds;
   }

   /**
    * @return TransactionId associated with this state machine artifact
    */
   public static Collection<TransactionRecord> getTransactionIds(TeamWorkFlowArtifact teamArt, boolean forMergeBranches) throws OseeCoreException {
      if (forMergeBranches) {
         IOseeBranch workingBranch = getWorkingBranch(teamArt);
         // grab only the transaction that had merge conflicts
         Collection<TransactionRecord> transactionIds = new ArrayList<TransactionRecord>();
         for (TransactionRecord transactionId : getCommitTransactionsToUnarchivedBaselineBranchs(teamArt)) {
            if (isMergeBranchExists(teamArt, workingBranch, transactionId.getBranch())) {
               transactionIds.add(transactionId);
            }
         }
         return transactionIds;
      } else {
         return getCommitTransactionsToUnarchivedBaselineBranchs(teamArt);
      }
   }

   public static TransactionRecord getEarliestTransactionId(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<TransactionRecord> transactionIds = getTransactionIds(teamArt, false);
      TransactionRecord earliestTransactionId;
      if (transactionIds.isEmpty()) {
         earliestTransactionId = null;
      } else {
         earliestTransactionId = transactionIds.iterator().next();
         for (TransactionRecord transactionId : transactionIds) {
            if (transactionId.getId() < earliestTransactionId.getId()) {
               earliestTransactionId = transactionId;
            }
         }
      }
      return earliestTransactionId;
   }

   /**
    * Perform error checks and popup confirmation dialogs associated with creating a working branch.
    * 
    * @param popup if true, errors are popped up to user; otherwise sent silently in Results
    * @return Result return of status
    */
   public static Result createWorkingBranch_Validate(TeamWorkFlowArtifact teamArt) {
      try {
         if (AtsBranchManagerCore.isCommittedBranchExists(teamArt)) {
            return new Result(
               "Committed branch already exists. Can not create another working branch once changes have been committed.");
         }
         IOseeBranch parentBranch = AtsBranchManagerCore.getConfiguredBranchForWorkflow(teamArt);
         if (parentBranch == null) {
            return new Result(
               "Parent Branch can not be determined.\n\nPlease specify " + "parent branch through Version Artifact or Team Definition Artifact.\n\n" + "Contact your team lead to configure this.");
         }
         Result result = AtsBranchManagerCore.isCreateBranchAllowed(teamArt);
         if (result.isFalse()) {
            return result;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result("Exception occurred: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   /**
    * Create a working branch associated with this Team Workflow. Call createWorkingBranch_Validate first to validate
    * that branch can be created.
    */
   public static Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return createWorkingBranch_Create(teamArt, false);
   }

   /**
    * Create a working branch associated with this state machine artifact. This should NOT be called by applications
    * except in test cases or automated tools. Use createWorkingBranchWithPopups
    */
   public static Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt, boolean pend) throws OseeCoreException {
      final IOseeBranch parentBranch = AtsBranchManagerCore.getConfiguredBranchForWorkflow(teamArt);
      return createWorkingBranch_Create(teamArt, parentBranch, pend);
   }

   public static Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt, final IOseeBranch parentBranch) throws OseeCoreException {
      return createWorkingBranch_Create(teamArt, parentBranch, false);
   }

   public static Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt, final IOseeBranch parentBranch, boolean pend) throws OseeCoreException {
      Conditions.checkNotNull(teamArt, "Parent Team Workflow");
      Conditions.checkNotNull(parentBranch, "Parent Branch");
      TransactionRecord parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      return createWorkingBranch(teamArt, parentTransactionId, pend);
   }

   public static Job createWorkingBranch(final TeamWorkFlowArtifact teamArt, final TransactionRecord parentTransactionId, boolean pend) throws OseeCoreException {
      final String branchName = Strings.truncate(TeamWorkFlowManager.getBranchName(teamArt), 195, true);
      Conditions.checkNotNull(teamArt, "Parent Team Workflow");
      Conditions.checkNotNull(parentTransactionId, "Parent Branch");

      IExceptionableRunnable runnable = new IExceptionableRunnable() {

         @Override
         public IStatus run(IProgressMonitor monitor) throws OseeCoreException {
            teamArt.setWorkingBranchCreationInProgress(true);
            BranchManager.createWorkingBranch(parentTransactionId, branchName, teamArt);
            teamArt.setWorkingBranchCreationInProgress(false);
            performPostBranchCreationTasks(teamArt);
            return Status.OK_STATUS;
         }

      };

      //            Jobs.runInJob("Create Branch", runnable, Activator.class, Activator.PLUGIN_ID);
      Job job =
         Jobs.startJob(new CatchAndReleaseJob("Create Branch", runnable, Activator.class, Activator.PLUGIN_ID), true);
      if (pend) {
         try {
            job.join();
         } catch (InterruptedException ex) {
            throw new OseeWrappedException(ex);
         }
      }
      return job;
   }

   private static void performPostBranchCreationTasks(final TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Create reviews as necessary
      AtsChangeSet changes = new AtsChangeSet("Create Reviews upon Transition");
      boolean created =
         createNecessaryBranchEventReviews(StateEventType.CreateBranch, teamArt, new Date(), AtsCoreUsers.SYSTEM_USER,
            changes);
      if (created) {
         changes.execute();
      }

      // Notify extensions of branch creation 
      for (IAtsStateItemCore item : AtsStateItemCoreManager.getStateItems()) {
         item.workingBranchCreated(teamArt);
      }
   }

   /**
    * @return true if one or more reviews were created
    */
   public static boolean createNecessaryBranchEventReviews(StateEventType stateEventType, TeamWorkFlowArtifact teamArt, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) throws OseeCoreException {
      boolean created = false;
      if (stateEventType != StateEventType.CommitBranch && stateEventType != StateEventType.CreateBranch) {
         throw new OseeStateException("Invalid stateEventType [%s]", stateEventType);
      }
      // Create any decision and peerToPeer reviews for createBranch and commitBranch
      for (IAtsDecisionReviewDefinition decRevDef : teamArt.getStateDefinition().getDecisionReviews()) {
         if (decRevDef.getStateEventType() != null && decRevDef.getStateEventType().equals(stateEventType)) {
            DecisionReviewArtifact decArt =
               DecisionReviewDefinitionManager.createNewDecisionReview(decRevDef, changes, teamArt, createdDate,
                  createdBy);
            if (decArt != null) {
               created = true;
               changes.add(decArt);
            }
         }
      }
      for (IAtsPeerReviewDefinition peerRevDef : teamArt.getStateDefinition().getPeerReviews()) {
         if (peerRevDef.getStateEventType() != null && peerRevDef.getStateEventType().equals(stateEventType)) {
            PeerToPeerReviewArtifact peerArt =
               PeerReviewDefinitionManager.createNewPeerToPeerReview(peerRevDef, changes, teamArt, createdDate,
                  createdBy);
            if (peerArt != null) {
               created = true;
               changes.add(peerArt);
            }
         }
      }
      return created;
   }

   public static Result deleteWorkingBranch(TeamWorkFlowArtifact teamArt, boolean pend) throws OseeCoreException {
      IOseeBranch branch = AtsBranchManagerCore.getWorkingBranch(teamArt);
      if (branch != null) {
         IStatus status = null;
         if (pend) {
            status = BranchManager.deleteBranchAndPend(branch);
         } else {
            Job job = BranchManager.deleteBranch(branch);
            job.schedule();
            try {
               job.join();
            } catch (InterruptedException ex) {
               throw new OseeWrappedException(ex);
            }
            status = job.getResult();
         }
         if (status.isOK()) {
            return Result.TrueResult;
         }
         return new Result(status.getMessage());
      }
      return Result.TrueResult;
   }

}
