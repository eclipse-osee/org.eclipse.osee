/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.branch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.core.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.version.VersionArtifact;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchManagerCore {

   private static Map<String, Branch> hridToWorkingBranchCache = new HashMap<String, Branch>();
   private static Map<String, Long> hridToWorkingBranchCacheUpdated = new HashMap<String, Long>(50);
   public static Set<Branch> branchesInCommit = new HashSet<Branch>();

   /**
    * Return working branch associated with SMA whether it is committed or not; This data is cached across all workflows
    * with the cache being updated by local and remote events.
    */
   public static Branch getWorkingBranch(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      long now = new Date().getTime();
      boolean notSet = hridToWorkingBranchCacheUpdated.get(teamArt.getHumanReadableId()) == null;
      if (notSet || (now - hridToWorkingBranchCacheUpdated.get(teamArt.getHumanReadableId()) > 1000)) {
         hridToWorkingBranchCache.put(teamArt.getHumanReadableId(),
            getWorkingBranchExcludeStates(teamArt, BranchState.REBASELINED, BranchState.DELETED));
         hridToWorkingBranchCacheUpdated.put(teamArt.getHumanReadableId(), now);
      }
      return hridToWorkingBranchCache.get(teamArt.getHumanReadableId());
   }

   /**
    * Return working branch associated with SMA, even if it's been archived; This data is cached across all workflows
    * with the cache being updated by local and remote events. Filters out rebaseline branches (which are working
    * branches also).
    */
   public static Branch getWorkingBranchExcludeStates(TeamWorkFlowArtifact teamArt, BranchState... negatedBranchStates) throws OseeCoreException {
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING, BranchType.BASELINE);
      branchFilter.setNegatedBranchStates(negatedBranchStates);
      branchFilter.setAssociatedArtifact(teamArt);

      List<Branch> branches = BranchManager.getBranches(branchFilter);

      if (branches.isEmpty()) {
         return null;
      } else if (branches.size() > 1) {
         throw new MultipleBranchesExist(
            "Unexpected multiple associated un-deleted working branches found for workflow [%s]",
            teamArt.getHumanReadableId());
      } else {
         return branches.get(0);
      }
   }

   /**
    * @return whether there is a working branch that is not committed
    */
   public static boolean isWorkingBranchInWork(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Branch branch = getWorkingBranch(teamArt);
      return branch != null && !branch.getBranchState().isCommitted();
   }

   /**
    * Return true if merge branch exists in DB (whether archived or not)
    */
   public static boolean isMergeBranchExists(TeamWorkFlowArtifact teamArt, Branch destinationBranch) throws OseeCoreException {
      return isMergeBranchExists(teamArt, getWorkingBranch(teamArt), destinationBranch);
   }

   /**
    * Method available for optimized checking of merge branches so don't have to re-acquire working branch if already
    * have
    */
   public static boolean isMergeBranchExists(TeamWorkFlowArtifact teamArt, Branch workingBranch, Branch destinationBranch) throws OseeCoreException {
      if (workingBranch == null) {
         return false;
      }
      return BranchManager.doesMergeBranchExist(workingBranch, destinationBranch);
   }

   public static boolean isMergeCompleted(TeamWorkFlowArtifact teamArt, Branch destinationBranch) throws OseeCoreException {
      ConflictManagerExternal conflictManager =
         new ConflictManagerExternal(destinationBranch, getWorkingBranch(teamArt));
      return !conflictManager.remainingConflictsExist();
   }

   public static TransactionRecord getCommitTransactionRecord(TeamWorkFlowArtifact teamArt, ICommitConfigArtifact configArt) throws OseeCoreException {
      Branch branch = configArt.getParentBranch();
      if (branch == null) {
         return null;
      }

      Collection<TransactionRecord> transactions = TransactionManager.getCommittedArtifactTransactionIds(teamArt);
      for (TransactionRecord transId : transactions) {
         if (transId.getBranchId() == branch.getId()) {
            return transId;
         }
      }
      return null;
   }

   public static CommitStatus getCommitStatus(TeamWorkFlowArtifact teamArt, ICommitConfigArtifact configArt) throws OseeCoreException {
      Branch desinationBranch = configArt.getParentBranch();
      if (desinationBranch == null) {
         return CommitStatus.Branch_Not_Configured;
      }

      Collection<TransactionRecord> transactions = TransactionManager.getCommittedArtifactTransactionIds(teamArt);
      boolean mergeBranchExists = AtsBranchManagerCore.isMergeBranchExists(teamArt, desinationBranch);

      for (TransactionRecord transId : transactions) {
         if (desinationBranch.equals(transId.getBranch())) {
            if (mergeBranchExists) {
               return CommitStatus.Committed_With_Merge;
            } else {
               return CommitStatus.Committed;
            }
         }
      }

      Result result = AtsBranchManagerCore.isCommitBranchAllowed(teamArt, configArt);
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

   public static Result isCommitBranchAllowed(TeamWorkFlowArtifact teamArt, ICommitConfigArtifact configArt) throws OseeCoreException {
      if (!teamArt.isTeamWorkflow()) {
         return Result.FalseResult;
      }
      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (teamArt.getTargetedVersion() == null) {
            return new Result(false, "Workflow not targeted for Version");
         }
         Result result = teamArt.getTargetedVersion().isCommitBranchAllowed();
         if (result.isFalse()) {
            return result;
         }

         if (teamArt.getTargetedVersion().getParentBranch() == null) {
            return new Result(false, "Parent Branch not configured for Version [" + teamArt.getTargetedVersion() + "]");
         }
         return Result.TrueResult;

      } else {
         Result result = teamArt.getTeamDefinition().isCommitBranchAllowed();
         if (result.isFalse()) {
            return result;
         }

         if (teamArt.getTeamDefinition().getParentBranch() == null) {
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
         if (teamArt.getTargetedVersion() == null) {
            return new Result(false, "Workflow not targeted for Version");
         }
         Result result = teamArt.getTargetedVersion().isCreateBranchAllowed();
         if (result.isFalse()) {
            return result;
         }

         if (teamArt.getTargetedVersion().getParentBranch() == null) {
            return new Result(false, "Parent Branch not configured for Version [" + teamArt.getTargetedVersion() + "]");
         }
         if (!teamArt.getTargetedVersion().getParentBranch().getBranchType().isBaselineBranch()) {
            return new Result(false, "Parent Branch must be of Baseline branch type.  See Admin for configuration.");
         }
         return Result.TrueResult;

      } else {
         Result result = teamArt.getTeamDefinition().isCreateBranchAllowed();
         if (result.isFalse()) {
            return result;
         }

         if (teamArt.getTeamDefinition().getParentBranch() == null) {
            return new Result(false,
               "Parent Branch not configured for Team Definition [" + teamArt.getTeamDefinition() + "]");
         }
         if (!teamArt.getTeamDefinition().getParentBranch().getBranchType().isBaselineBranch()) {
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

   public static Collection<ICommitConfigArtifact> getConfigArtifactsConfiguredToCommitTo(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<ICommitConfigArtifact> configObjects = new HashSet<ICommitConfigArtifact>();
      if (teamArt.isTeamUsesVersions()) {
         if (teamArt.getTargetedVersion() != null) {
            teamArt.getTargetedVersion().getParallelVersions(configObjects);
         }
      } else {
         if (teamArt.isTeamWorkflow() && teamArt.getTeamDefinition().getParentBranch() != null) {
            configObjects.add(teamArt.getTeamDefinition());
         }
      }
      return configObjects;
   }

   public static ICommitConfigArtifact getParentBranchConfigArtifactConfiguredToCommitTo(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      if (teamArt.isTeamUsesVersions()) {
         if (teamArt.getTargetedVersion() != null) {
            return teamArt.getTargetedVersion();
         }
      } else {
         if (teamArt.isTeamWorkflow() && teamArt.getTeamDefinition().getParentBranch() != null) {
            return teamArt.getTeamDefinition();
         }
      }
      return null;
   }

   public static boolean isAllObjectsToCommitToConfigured(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return getConfigArtifactsConfiguredToCommitTo(teamArt).size() == getBranchesToCommitTo(teamArt).size();
   }

   public static Collection<Branch> getBranchesLeftToCommit(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<Branch> branchesLeft = new HashSet<Branch>();
      Collection<Branch> committedTo = getBranchesCommittedTo(teamArt);
      for (Branch branchToCommit : getBranchesToCommitTo(teamArt)) {
         if (!committedTo.contains(branchToCommit)) {
            branchesLeft.add(branchToCommit);
         }
      }
      return branchesLeft;
   }

   public static Collection<Branch> getBranchesToCommitTo(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<Branch> branches = new HashSet<Branch>();
      for (Object obj : getConfigArtifactsConfiguredToCommitTo(teamArt)) {
         if (obj instanceof VersionArtifact && ((VersionArtifact) obj).getParentBranch() != null) {
            branches.add(((VersionArtifact) obj).getParentBranch());
         } else if (obj instanceof TeamDefinitionArtifact && ((TeamDefinitionArtifact) obj).getParentBranch() != null) {
            branches.add(((TeamDefinitionArtifact) obj).getParentBranch());
         }
      }
      return branches;
   }

   public static Collection<Branch> getBranchesCommittedTo(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<Branch> branches = new HashSet<Branch>();
      for (TransactionRecord transId : getTransactionIds(teamArt, false)) {
         branches.add(transId.getBranch());
      }
      return branches;
   }

   /**
    * @return true if there is at least one destination branch committed to
    */
   public static boolean isCommittedBranchExists(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return isAllObjectsToCommitToConfigured(teamArt) && !getBranchesCommittedTo(teamArt).isEmpty();
   }

   /**
    * Return true if all commit destination branches are configured and have been committed to
    */
   public static boolean isBranchesAllCommitted(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<Branch> committedTo = getBranchesCommittedTo(teamArt);
      for (Branch destBranch : getBranchesToCommitTo(teamArt)) {
         if (!committedTo.contains(destBranch)) {
            return false;
         }
      }
      return true;
   }

   public static boolean isBranchesAllCommittedExcept(TeamWorkFlowArtifact teamArt, Branch branchToExclude) throws OseeCoreException {
      Collection<Branch> committedTo = getBranchesCommittedTo(teamArt);
      for (Branch destBranch : getBranchesToCommitTo(teamArt)) {
         if (!destBranch.equals(branchToExclude) && !committedTo.contains(destBranch)) {
            return false;
         }
      }
      return true;
   }

   /**
    * @return Branch that is the configured branch to create working branch from.
    */
   public static Branch getConfiguredBranchForWorkflow(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Branch parentBranch = null;

      // Check for parent branch id in Version artifact
      if (teamArt.isTeamUsesVersions()) {
         VersionArtifact verArt = teamArt.getTargetedVersion();
         if (verArt != null) {
            parentBranch = verArt.getParentBranch();
         }
      }

      // If not defined in version, check for parent branch from team definition
      if (parentBranch == null && teamArt.isTeamWorkflow()) {
         parentBranch = teamArt.getTeamDefinition().getParentBranch();
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

   public static Integer getId(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Branch branch = getWorkingBranch(teamArt);
      if (branch == null) {
         return null;
      }
      return branch.getId();
   }

   private static Collection<TransactionRecord> getCommitTransactionsToUnarchivedBaslineBranchs(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
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
         Branch workingBranch = getWorkingBranch(teamArt);
         // grab only the transaction that had merge conflicts
         Collection<TransactionRecord> transactionIds = new ArrayList<TransactionRecord>();
         for (TransactionRecord transactionId : getCommitTransactionsToUnarchivedBaslineBranchs(teamArt)) {
            if (isMergeBranchExists(teamArt, workingBranch, transactionId.getBranch())) {
               transactionIds.add(transactionId);
            }
         }
         return transactionIds;
      } else {
         return getCommitTransactionsToUnarchivedBaslineBranchs(teamArt);
      }
   }

   public static TransactionRecord getEarliestTransactionId(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<TransactionRecord> transactionIds = getTransactionIds(teamArt, false);
      if (transactionIds.size() == 1) {
         return transactionIds.iterator().next();
      }
      TransactionRecord earliestTransactionId = transactionIds.iterator().next();
      for (TransactionRecord transactionId : transactionIds) {
         if (transactionId.getId() < earliestTransactionId.getId()) {
            earliestTransactionId = transactionId;
         }
      }
      return earliestTransactionId;
   }

}
