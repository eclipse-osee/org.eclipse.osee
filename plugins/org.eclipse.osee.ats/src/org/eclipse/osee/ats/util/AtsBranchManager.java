/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.commit.CommitStatus;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule.DecisionRuleOption;
import org.eclipse.osee.ats.workflow.item.AtsAddPeerToPeerReviewRule;
import org.eclipse.osee.ats.workflow.item.StateEventType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.util.TransactionIdLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * BranchManager contains methods necessary for ATS objects to interact with creation, view and commit of branches.
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchManager {
   public static Set<Branch> branchesInCommit = new HashSet<Branch>();
   private final TeamWorkFlowArtifact teamArt;

   public AtsBranchManager(TeamWorkFlowArtifact teamArt) {
      this.teamArt = teamArt;
   }

   public void showMergeManager() {
      try {
         if (!isWorkingBranchInWork() && !isCommittedBranchExists()) {
            AWorkbench.popup("ERROR", "No Current Working or Committed Branch");
            return;
         }
         if (isWorkingBranchInWork()) {
            Branch branch = getConfiguredBranchForWorkflow();
            if (branch == null) {
               AWorkbench.popup("ERROR", "Can't access parent branch");
               return;
            }
            MergeView.openView(getWorkingBranch(), branch, getWorkingBranch().getBaseTransaction());

         } else if (isCommittedBranchExists()) {
            TransactionRecord transactionId = getTransactionIdOrPopupChoose("Show Merge Manager", true);
            if (transactionId == null) {
               return;
            }
            MergeView.openView(transactionId);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Return true if merge branch exists in DB (whether archived or not)
    * 
    * @param destinationBranch
    * @return true
    * @throws OseeCoreException
    */
   public boolean isMergeBranchExists(Branch destinationBranch) throws OseeCoreException {
      return isMergeBranchExists(getWorkingBranch(), destinationBranch);
   }

   /**
    * Method available for optimized checking of merge branches so don't have to re-acquire working branch if already
    * have
    */
   public boolean isMergeBranchExists(Branch workingBranch, Branch destinationBranch) throws OseeCoreException {
      if (workingBranch == null) {
         return false;
      }
      return BranchManager.doesMergeBranchExist(workingBranch, destinationBranch);
   }

   public boolean isMergeCompleted(Branch destinationBranch) throws OseeCoreException {
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(destinationBranch, getWorkingBranch());
      return !conflictManager.remainingConflictsExist();
   }

   public TransactionRecord getCommitTransactionRecord(ICommitConfigArtifact configArt) throws OseeCoreException {
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

   public CommitStatus getCommitStatus(ICommitConfigArtifact configArt) throws OseeCoreException {
      Branch branch = configArt.getParentBranch();
      if (branch == null) {
         return CommitStatus.Branch_Not_Configured;
      }

      if (branch.getBranchState().isCommitted()) {
         return CommitStatus.Committed;
      }
      Collection<TransactionRecord> transactions = TransactionManager.getCommittedArtifactTransactionIds(teamArt);
      boolean mergeBranchExists = teamArt.getBranchMgr().isMergeBranchExists(branch);

      for (TransactionRecord transId : transactions) {
         if (transId.getBranchId() == branch.getId()) {
            if (mergeBranchExists) {
               return CommitStatus.Committed_With_Merge;
            } else {
               return CommitStatus.Committed;
            }
         }
      }

      Result result = teamArt.getBranchMgr().isCommitBranchAllowed(configArt);
      if (result.isFalse()) {
         return CommitStatus.Branch_Commit_Disabled;
      }
      if (teamArt.getBranchMgr().getWorkingBranch() == null) {
         return CommitStatus.Working_Branch_Not_Created;
      }
      if (mergeBranchExists) {
         return CommitStatus.Merge_In_Progress;
      }
      return CommitStatus.Commit_Needed;
   }

   public void showMergeManager(Branch destinationBranch) throws OseeCoreException {
      if (isWorkingBranchInWork()) {
         MergeView.openView(getWorkingBranch(), destinationBranch, getWorkingBranch().getBaseTransaction());
      } else if (isCommittedBranchExists()) {
         for (TransactionRecord transactionId : getTransactionIds(true)) {
            if (transactionId.getBranchId() == destinationBranch.getId()) {
               MergeView.openView(transactionId);

            }
         }
      }
   }

   /**
    * Opens the branch currently associated with this state machine artifact.
    */
   public void showWorkingBranch() {
      try {
         if (!isWorkingBranchInWork()) {
            AWorkbench.popup("ERROR", "No Current Working Branch");
            return;
         }
         BranchView.revealBranch(getWorkingBranch());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public Integer getId() throws OseeCoreException {
      Branch branch = getWorkingBranch();
      if (branch == null) {
         return null;
      }
      return branch.getId();
   }

   /**
    * If working branch has no changes, allow for deletion.
    */
   public void deleteWorkingBranch(boolean promptUser) {
      boolean isExecutionAllowed = !promptUser;
      try {
         Branch branch = getWorkingBranch();
         if (promptUser) {
            StringBuilder message = new StringBuilder();
            if (BranchManager.hasChanges(branch)) {
               message.append("Warning: Changes have been made on this branch.\n\n");
            }
            message.append("Are you sure you want to delete the branch: ");
            message.append(branch);

            isExecutionAllowed =
               MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Delete Working Branch", message.toString());
         }

         if (isExecutionAllowed) {
            Job job = BranchManager.deleteBranch(branch);
            job.join();
            IStatus status = job.getResult();
            if (promptUser) {
               AWorkbench.popup("Delete Complete",
                  status.isOK() ? "Branch delete was successful." : "Branch delete failed.\n" + status.getMessage());
            } else {
               if (!status.isOK()) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, status.getMessage(), status.getException());
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Problem deleting branch.", ex);
      }
   }

   public Collection<TransactionRecord> getFirstCommitTransaction() throws OseeCoreException {
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
   public Collection<TransactionRecord> getTransactionIds(boolean showMergeManager) throws OseeCoreException {
      if (showMergeManager) {
         Branch workingBranch = getWorkingBranch();
         // grab only the transaction that had merge conflicts
         Collection<TransactionRecord> transactionIds = new ArrayList<TransactionRecord>();
         for (TransactionRecord transactionId : getFirstCommitTransaction()) {
            if (isMergeBranchExists(workingBranch, transactionId.getBranch())) {
               transactionIds.add(transactionId);
            }
         }
         return transactionIds;
      } else {
         return getFirstCommitTransaction();
      }
   }

   public TransactionRecord getEarliestTransactionId() throws OseeCoreException {
      Collection<TransactionRecord> transactionIds = getTransactionIds(false);
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

   /**
    * Either return a single commit transaction or user must choose from a list of valid commit transactions
    */
   public TransactionRecord getTransactionIdOrPopupChoose(String title, boolean showMergeManager) throws OseeCoreException {
      Collection<TransactionRecord> transactionIds = new HashSet<TransactionRecord>();
      for (TransactionRecord id : getTransactionIds(showMergeManager)) {
         // ignore working branches that have been committed
         if (id.getBranch().getBranchType().isWorkingBranch() && id.getBranch().getBranchState().isCommitted()) {
            continue;
         }
         // ignore working branches that have been re-baselined (e.g. update form parent branch)
         else if (id.getBranch().getBranchType().isWorkingBranch() && id.getBranch().getBranchState().isRebaselined()) {
            continue;
         } else {
            transactionIds.add(id);
         }
      }
      if (transactionIds.size() == 1) {
         return transactionIds.iterator().next();
      }

      ViewerSorter sorter = new ViewerSorter() {
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 == null || e2 == null) {
               return 0;
            }
            if (((TransactionRecord) e1).getId() < ((TransactionRecord) e2).getId()) {
               return -1;
            } else if (((TransactionRecord) e1).getId() > ((TransactionRecord) e2).getId()) {
               return 1;
            }
            return 0;
         }
      };
      SimpleCheckFilteredTreeDialog ld =
         new SimpleCheckFilteredTreeDialog(title, "Select Commit Branch", new ArrayTreeContentProvider(),
            new TransactionIdLabelProvider(), sorter, 0, Integer.MAX_VALUE);
      ld.setInput(transactionIds);

      if (ld.open() == 0) {
         return (TransactionRecord) ld.getResult()[0];
      }
      return null;
   }

   public Result isCreateBranchAllowed() throws OseeCoreException {
      if (!teamArt.isTeamWorkflow()) {
         return Result.FalseResult;
      }

      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (teamArt.getTargetedForVersion() == null) {
            return new Result(false, "Workflow not targeted for Version");
         }
         Result result = teamArt.getTargetedForVersion().isCreateBranchAllowed();
         if (result.isFalse()) {
            return result;
         }

         if (teamArt.getTargetedForVersion().getParentBranch() == null) {
            return new Result(false,
               "Parent Branch not configured for Version [" + teamArt.getTargetedForVersion() + "]");
         }
         if (!teamArt.getTargetedForVersion().getParentBranch().getBranchType().isBaselineBranch()) {
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

   public Result isCommitBranchAllowed(ICommitConfigArtifact configArt) throws OseeCoreException {
      if (!teamArt.isTeamWorkflow()) {
         return Result.FalseResult;
      }
      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (teamArt.getTargetedForVersion() == null) {
            return new Result(false, "Workflow not targeted for Version");
         }
         Result result = teamArt.getTargetedForVersion().isCommitBranchAllowed();
         if (result.isFalse()) {
            return result;
         }

         if (teamArt.getTargetedForVersion().getParentBranch() == null) {
            return new Result(false,
               "Parent Branch not configured for Version [" + teamArt.getTargetedForVersion() + "]");
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

   /**
    * Display change report associated with the branch, if exists, or transaction, if branch has been committed.
    */
   public void showChangeReport() {
      try {
         if (isWorkingBranchInWork()) {
            ChangeUiUtil.open(getWorkingBranch());
         } else if (isCommittedBranchExists()) {
            TransactionRecord transactionId = getTransactionIdOrPopupChoose("Show Change Report", false);
            if (transactionId == null) {
               return;
            }
            ChangeUiUtil.open(transactionId);
         } else {
            AWorkbench.popup("ERROR", "No Branch or Committed Transaction Found.");
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't show change report.", ex);
      }
   }

   /**
    * Grab the change report for the indicated branch
    */
   public void showChangeReportForBranch(Branch destinationBranch) {
      try {
         for (TransactionRecord transactionId : getTransactionIds(false)) {
            if (transactionId.getBranch() == destinationBranch) {
               ChangeUiUtil.open(transactionId);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't show change report.", ex);
      }
   }

   Branch workingBranchCache = null;
   long workingBranchCacheUpdated = 0;

   /**
    * Return working branch associated with SMA whether it is committed or not; This data is cached across all workflows
    * with the cache being updated by local and remote events.
    */
   public Branch getWorkingBranch() throws OseeCoreException {
      long now = new Date().getTime();
      if (now - workingBranchCacheUpdated > 1000) {
         workingBranchCache = getWorkingBranchExcludeStates(BranchState.REBASELINED, BranchState.DELETED);
         workingBranchCacheUpdated = now;
         //         System.out.println("updating cache");
      } else {
         //         System.out.println("returning cache");
      }
      return workingBranchCache;
   }

   /**
    * Return working branch associated with SMA, even if it's been archived; This data is cached across all workflows
    * with the cache being updated by local and remote events. Filters out rebaseline branches (which are working
    * branches also).
    */
   public Branch getWorkingBranchExcludeStates(BranchState... negatedBranchStates) throws OseeCoreException {
      BranchFilter branchFilter = new BranchFilter(BranchArchivedState.ALL, BranchType.WORKING, BranchType.BASELINE);
      branchFilter.setNegatedBranchStates(negatedBranchStates);
      branchFilter.setAssociatedArtifact(teamArt);

      List<Branch> branches = BranchManager.getBranches(branchFilter);

      if (branches.isEmpty()) {
         return null;
      } else if (branches.size() > 1) {
         throw new MultipleBranchesExist(
            "Unexpected multiple associated un-deleted working branches found for workflow " + teamArt.getHumanReadableId());
      } else {
         return branches.get(0);
      }
   }

   /**
    * @return whether there is a working branch that is not committed
    * @throws OseeCoreException
    */
   public boolean isWorkingBranchInWork() throws OseeCoreException {
      Branch branch = getWorkingBranch();
      return branch != null && !branch.getBranchState().isCommitted();
   }

   /**
    * Returns true if there was ever a commit of a working branch regardless of whether the working branch is archived
    * or not.
    */
   public boolean isWorkingBranchEverCommitted() throws OseeCoreException {
      return getBranchesCommittedTo().size() > 0;
   }

   public Collection<ICommitConfigArtifact> getConfigArtifactsConfiguredToCommitTo() throws OseeCoreException {
      Set<ICommitConfigArtifact> configObjects = new HashSet<ICommitConfigArtifact>();
      if (teamArt.isTeamUsesVersions()) {
         if (teamArt.getTargetedForVersion() != null) {
            teamArt.getTargetedForVersion().getParallelVersions(configObjects);
         }
      } else {
         if (teamArt.isTeamWorkflow() && teamArt.getTeamDefinition().getParentBranch() != null) {
            configObjects.add(teamArt.getTeamDefinition());
         }
      }
      return configObjects;
   }

   public ICommitConfigArtifact getParentBranchConfigArtifactConfiguredToCommitTo() throws OseeCoreException {
      if (teamArt.isTeamUsesVersions()) {
         if (teamArt.getTargetedForVersion() != null) {
            return teamArt.getTargetedForVersion();
         }
      } else {
         if (teamArt.isTeamWorkflow() && teamArt.getTeamDefinition().getParentBranch() != null) {
            return teamArt.getTeamDefinition();
         }
      }
      return null;
   }

   public boolean isAllObjectsToCommitToConfigured() throws OseeCoreException {
      return getConfigArtifactsConfiguredToCommitTo().size() == getBranchesToCommitTo().size();
   }

   public Collection<Branch> getBranchesLeftToCommit() throws OseeCoreException {
      Set<Branch> branchesLeft = new HashSet<Branch>();
      Collection<Branch> committedTo = getBranchesCommittedTo();
      for (Branch branchToCommit : getBranchesToCommitTo()) {
         if (!committedTo.contains(branchToCommit)) {
            branchesLeft.add(branchToCommit);
         }
      }
      return branchesLeft;
   }

   public Collection<Branch> getBranchesToCommitTo() throws OseeCoreException {
      Set<Branch> branches = new HashSet<Branch>();
      for (Object obj : getConfigArtifactsConfiguredToCommitTo()) {
         if (obj instanceof VersionArtifact && ((VersionArtifact) obj).getParentBranch() != null) {
            branches.add(((VersionArtifact) obj).getParentBranch());
         } else if (obj instanceof TeamDefinitionArtifact && ((TeamDefinitionArtifact) obj).getParentBranch() != null) {
            branches.add(((TeamDefinitionArtifact) obj).getParentBranch());
         }
      }
      return branches;
   }

   public Collection<Branch> getBranchesCommittedTo() throws OseeCoreException {
      Set<Branch> branches = new HashSet<Branch>();
      for (TransactionRecord transId : getTransactionIds(false)) {
         branches.add(transId.getBranch());
      }
      return branches;
   }

   /**
    * @return true if there is at least one destination branch committed to
    */
   public boolean isCommittedBranchExists() throws OseeCoreException {
      return isAllObjectsToCommitToConfigured() && !getBranchesCommittedTo().isEmpty();
   }

   /**
    * Return true if all commit destination branches are configured and have been committed to
    */
   public boolean isBranchesAllCommitted() throws OseeCoreException {
      Collection<Branch> committedTo = getBranchesCommittedTo();
      for (Branch destBranch : getBranchesToCommitTo()) {
         if (!committedTo.contains(destBranch)) {
            return false;
         }
      }
      return true;
   }

   public boolean isBranchesAllCommittedExcept(Branch branchToExclude) throws OseeCoreException {
      Collection<Branch> committedTo = getBranchesCommittedTo();
      for (Branch destBranch : getBranchesToCommitTo()) {
         if (!destBranch.equals(branchToExclude) && !committedTo.contains(destBranch)) {
            return false;
         }
      }
      return true;
   }

   /**
    * Perform error checks and popup confirmation dialogs associated with creating a working branch.
    * 
    * @param pageId if specified, WorkPage gets callback to provide confirmation that branch can be created
    * @param popup if true, errors are popped up to user; otherwise sent silently in Results
    * @return Result return of status
    */
   public Result createWorkingBranch(String pageId, boolean popup) {
      try {
         if (isCommittedBranchExists()) {
            if (popup) {
               AWorkbench.popup("ERROR", "Can not create another working branch once changes have been committed.");
            }
            return new Result("Committed branch already exists.");
         }
         Branch parentBranch = getConfiguredBranchForWorkflow();
         if (parentBranch == null) {
            String errorStr =
               "Parent Branch can not be determined.\n\nPlease specify " + "parent branch through Version Artifact or Team Definition Artifact.\n\n" + "Contact your team lead to configure this.";
            if (popup) {
               AWorkbench.popup("ERROR", errorStr);
            }
            return new Result(errorStr);
         }
         Result result = isCreateBranchAllowed();
         if (result.isFalse()) {
            if (popup) {
               result.popup();
            }
            return result;
         }
         // Retrieve parent branch to create working branch from
         if (popup && !MessageDialog.openConfirm(
            Displays.getActiveShell(),
            "Create Working Branch",
            "Create a working branch from parent branch\n\n\"" + parentBranch.getName() + "\"?\n\n" + "NOTE: Working branches are necessary when OSEE Artifact changes " + "are made during implementation.")) {
            return Result.FalseResult;
         }
         createWorkingBranch(pageId, parentBranch);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result("Exception occurred: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   public static void createNecessaryBranchEventReviews(StateEventType stateEventType, TeamWorkFlowArtifact teamArt, SkynetTransaction transaction) throws OseeCoreException {
      if (stateEventType != StateEventType.CommitBranch && stateEventType != StateEventType.CreateBranch) {
         throw new OseeStateException("Invalid stateEventType = " + stateEventType);
      }
      // Create any decision and peerToPeer reviews for createBranch and commitBranch
      for (String ruleId : Arrays.asList(AtsAddDecisionReviewRule.ID, AtsAddPeerToPeerReviewRule.ID)) {
         for (WorkRuleDefinition workRuleDef : teamArt.getWorkRulesStartsWith(ruleId)) {
            StateEventType eventType = AtsAddDecisionReviewRule.getStateEventType(teamArt, workRuleDef);
            if (eventType != null && eventType == stateEventType) {
               if (ruleId.equals(AtsAddDecisionReviewRule.ID)) {
                  DecisionReviewArtifact decArt =
                     AtsAddDecisionReviewRule.createNewDecisionReview(workRuleDef, transaction, teamArt,
                        DecisionRuleOption.TransitionToDecision);
                  if (decArt != null) {
                     decArt.persist(transaction);
                  }
               } else if (ruleId.equals(AtsAddPeerToPeerReviewRule.ID)) {
                  PeerToPeerReviewArtifact peerArt =
                     AtsAddPeerToPeerReviewRule.createNewPeerToPeerReview(workRuleDef, teamArt, transaction);
                  if (peerArt != null) {
                     peerArt.persist(transaction);
                  }
               }
            }
         }
      }
   }

   /**
    * @return Branch that is the configured branch to create working branch from.
    */
   private Branch getConfiguredBranchForWorkflow() throws OseeCoreException {
      Branch parentBranch = null;

      // Check for parent branch id in Version artifact
      if (teamArt.isTeamUsesVersions()) {
         VersionArtifact verArt = teamArt.getTargetedForVersion();
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

   /**
    * Create a working branch associated with this state machine artifact. This should NOT be called by applications
    * except in test cases or automated tools. Use createWorkingBranchWithPopups
    */
   public void createWorkingBranch(String pageId, final IOseeBranch parentBranch) throws OseeCoreException {
      final String branchName = Strings.truncate(teamArt.getBranchName(), 195, true);

      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public IStatus run(IProgressMonitor monitor) throws OseeCoreException {
            BranchManager.createWorkingBranch(parentBranch, branchName, teamArt);
            // Create reviews as necessary
            SkynetTransaction transaction =
               new SkynetTransaction(AtsUtil.getAtsBranch(), "Create Reviews upon Transition");
            createNecessaryBranchEventReviews(StateEventType.CreateBranch, teamArt, transaction);
            transaction.execute();
            return Status.OK_STATUS;
         }
      };

      Jobs.runInJob("Create Branch", runnable, AtsPlugin.class, AtsPlugin.PLUGIN_ID);
   }

   public boolean isBranchInCommit() throws OseeCoreException {
      if (!isWorkingBranchInWork()) {
         return false;
      }
      return branchesInCommit.contains(getWorkingBranch());
   }

   /**
    * @param commitPopup if true, pop-up errors associated with results
    * @param overrideStateValidation if true, don't do checks to see if commit can be performed. This should only be
    * used for developmental testing or automation
    */
   public Job commitWorkingBranch(final boolean commitPopup, final boolean overrideStateValidation, Branch destinationBranch, boolean archiveWorkingBranch) throws OseeCoreException {
      if (isBranchInCommit()) {
         throw new OseeCoreException("Branch is currently being committed.");
      }
      Job job =
         new AtsBranchCommitJob(teamArt, commitPopup, overrideStateValidation, destinationBranch, archiveWorkingBranch);
      Operations.scheduleJob(job, true, Job.LONG, null);
      return job;
   }

   public ChangeData getChangeDataFromEarliestTransactionId() throws OseeCoreException {
      return getChangeData(null);
   }

   /**
    * Return ChangeData represented by commit to commitConfigArt or earliest commit if commitConfigArt == null
    * 
    * @param commitConfigArt that configures commit or null
    */
   public ChangeData getChangeData(ICommitConfigArtifact commitConfigArt) throws OseeCoreException {
      if (commitConfigArt != null && commitConfigArt.getParentBranch() == null) {
         throw new OseeArgumentException("Parent Branch not configured for " + commitConfigArt);
      }
      Collection<Change> changes = new ArrayList<Change>();

      IOperation operation = null;
      if (teamArt.getBranchMgr().isWorkingBranchInWork()) {
         operation = ChangeManager.comparedToParent(getWorkingBranch(), changes);
         Operations.executeWorkAndCheckStatus(operation, new NullProgressMonitor(), -1.0);
      } else if (teamArt.getBranchMgr().isCommittedBranchExists()) {
         TransactionRecord transactionId = null;
         if (commitConfigArt == null) {
            transactionId = getEarliestTransactionId();
         } else {
            for (TransactionRecord transId : getTransactionIds(false)) {
               if (transId.getBranch() == commitConfigArt.getParentBranch()) {
                  transactionId = transId;
               }
            }
         }
         operation = ChangeManager.comparedToPreviousTx(transactionId, changes);
         Operations.executeWorkAndCheckStatus(operation, new NullProgressMonitor(), -1.0);
      }
      return new ChangeData(changes);
   }
}