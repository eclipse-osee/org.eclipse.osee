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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.util.widgets.commit.CommitStatus;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.AtsAddPeerToPeerReviewRule;
import org.eclipse.osee.ats.workflow.item.StateEventType;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule.DecisionRuleOption;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit.CommitHandler;
import org.eclipse.osee.framework.ui.skynet.util.TransactionIdLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * BranchManager contains methods necessary for ATS objects to interact with creation, view and commit of branches.
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchManager {
   private final SMAManager smaMgr;
   private static Set<Branch> branchesInCommit = new HashSet<Branch>();

   public AtsBranchManager(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
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
            MergeView.openView(getWorkingBranch(), branch,
                  TransactionManager.getStartEndPoint(getWorkingBranch()).getFirst());

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
      return isMergeBranchExists(getWorkingBranch(true, false), destinationBranch);
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
      ConflictManagerExternal conflictManager =
            new ConflictManagerExternal(destinationBranch, getWorkingBranch(true, false));
      return !conflictManager.remainingConflictsExist();
   }

   public CommitStatus getCommitStatus(ICommitConfigArtifact configArt) throws OseeCoreException {
      Branch branch = configArt.getParentBranch();
      if (branch == null) {
         return CommitStatus.Branch_Not_Configured;
      }

      Set<Branch> branches = BranchManager.getAssociatedArtifactBranches(smaMgr.getSma(), false, false);
      if (branches.contains(branch)) {
         return CommitStatus.Committed;
      }
      Collection<TransactionRecord> transactions =
            TransactionManager.getCommittedArtifactTransactionIds(smaMgr.getSma());
      for (TransactionRecord transId : transactions) {
         if (transId.getBranchId() == branch.getId()) {
            if (smaMgr.getBranchMgr().isMergeBranchExists(branch)) {
               return CommitStatus.Committed_With_Merge;
            } else {
               return CommitStatus.Committed;
            }
         }
      }

      Result result = smaMgr.getBranchMgr().isCommitBranchAllowed(configArt);
      if (result.isFalse()) {
         return CommitStatus.Branch_Commit_Disabled;
      }
      if (smaMgr.getBranchMgr().getWorkingBranch(true, false) == null) {
         return CommitStatus.Working_Branch_Not_Created;
      }
      if (smaMgr.getBranchMgr().isMergeBranchExists(branch)) {
         return CommitStatus.Merge_In_Progress;
      }
      return CommitStatus.Commit_Needed;
   }

   public void showMergeManager(Branch destinationBranch) throws OseeCoreException {
      if (isWorkingBranchInWork()) {
         MergeView.openView(getWorkingBranch(), destinationBranch, TransactionManager.getStartEndPoint(
               getWorkingBranch()).getFirst());
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
      if (getWorkingBranch() == null) {
         return null;
      }
      return getWorkingBranch().getId();
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

   public Collection<TransactionRecord> getTransactionIdsForBaslineBranches() throws OseeCoreException {
      Collection<TransactionRecord> transactionIds = new ArrayList<TransactionRecord>();
      Collection<TransactionRecord> committedTransactions =
            TransactionManager.getCommittedArtifactTransactionIds(smaMgr.getSma());
      for (TransactionRecord transactionId : committedTransactions) {
         // exclude working branches including branch states that are re-baselined 
         if (transactionId.getBranch().getBranchType().isBaselineBranch()) {
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
         for (TransactionRecord transactionId : getTransactionIdsForBaslineBranches()) {
            if (isMergeBranchExists(workingBranch, transactionId.getBranch())) {
               transactionIds.add(transactionId);
            }
         }
         return transactionIds;
      } else {
         return getTransactionIdsForBaslineBranches();
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
    * 
    * @param title
    * @param showMergeManager
    * @return
    * @throws OseeCoreException
    */
   private TransactionRecord getTransactionIdOrPopupChoose(String title, boolean showMergeManager) throws OseeCoreException {
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
      SimpleCheckFilteredTreeDialog ld = new SimpleCheckFilteredTreeDialog(title, "Select Commit Branch", new ArrayTreeContentProvider(),new TransactionIdLabelProvider(), sorter,  0, Integer.MAX_VALUE);
      ld.setInput(transactionIds);
      
      if (ld.open() == 0) {
         return (TransactionRecord) ld.getResult()[0];
      }
      return null;
   }

   public Result isCreateBranchAllowed() throws OseeCoreException {
      if (!(smaMgr.getSma() instanceof TeamWorkFlowArtifact)) {
         return Result.FalseResult;
      }
      TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) smaMgr.getSma();

      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (smaMgr.getTargetedForVersion() == null) {
            return new Result(false, "Workflow not targeted for Version");
         }
         Result result = smaMgr.getTargetedForVersion().isCreateBranchAllowed();
         if (result.isFalse()) {
            return result;
         }

         if (smaMgr.getTargetedForVersion().getParentBranch() == null) {
            return new Result(false,
                  "Parent Branch not configured for Version [" + smaMgr.getTargetedForVersion() + "]");
         }
         if (!smaMgr.getTargetedForVersion().getParentBranch().getBranchType().isBaselineBranch()) {
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
      if (!(smaMgr.getSma() instanceof TeamWorkFlowArtifact)) {
         return Result.FalseResult;
      }
      TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) smaMgr.getSma();

      if (teamArt.getTeamDefinition().isTeamUsesVersions()) {
         if (smaMgr.getTargetedForVersion() == null) {
            return new Result(false, "Workflow not targeted for Version");
         }
         Result result = smaMgr.getTargetedForVersion().isCommitBranchAllowed();
         if (result.isFalse()) {
            return result;
         }

         if (smaMgr.getTargetedForVersion().getParentBranch() == null) {
            return new Result(false,
                  "Parent Branch not configured for Version [" + smaMgr.getTargetedForVersion() + "]");
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
            ChangeView.open(getWorkingBranch());
         } else if (isCommittedBranchExists()) {
            TransactionRecord transactionId = getTransactionIdOrPopupChoose("Show Change Report", false);
            if (transactionId == null) {
               return;
            }
            ChangeView.open(transactionId);
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
               ChangeView.open(transactionId);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't show change report.", ex);
      }
   }

   /**
    * Return working branch associated with SMA; This data is cached across all workflows with the cache being updated
    * by local and remote events.
    * 
    * @return Branch
    */
   public Branch getWorkingBranch() throws OseeCoreException {
      return getWorkingBranch(false, false);
   }

   /**
    * Return working branch associated with SMA, even if it's been archived; This data is cached across all workflows
    * with the cache being updated by local and remote events. Filters out rebaseline branches (which are working
    * branches also).
    * 
    * @param includeDeleted
    * @return Branch
    */
   public Branch getWorkingBranch(boolean includeArchived, boolean includeDeleted) throws OseeCoreException {
      Set<Branch> branches = new HashSet<Branch>();
      for (Branch branch : BranchManager.getAssociatedArtifactBranches(smaMgr.getSma(), includeArchived, includeDeleted)) {
         if (!branch.getBranchState().isRebaselined()) {
            branches.add(branch);
         }
      }
      if (branches.size() == 0) {
         return null;
      } else if (branches.size() > 1) {
         throw new MultipleBranchesExist(
               "Unexpected multiple associated un-deleted working branches found for workflow " + smaMgr.getSma().getHumanReadableId());
      } else {
         return branches.iterator().next();
      }
   }

   /**
    * Returns true if there were ever a working branch. This could be if there is an inWork branch or an archived
    * branch. It also handles the case where the archived branch was deleted, in which case it looks at the committedTo
    * branches and returns true.
    * 
    * @return result
    * @throws OseeCoreException
    */
   public boolean isWorkingBranchEverCreated() throws OseeCoreException {
      return isWorkingBranchArchived() || isWorkingBranchEverCommitted();
   }

   /**
    * Returns true if there is a working branch that is not archived
    * 
    * @return result
    * @throws OseeCoreException
    */
   public boolean isWorkingBranchInWork() throws OseeCoreException {
      return getWorkingBranch(false, false) != null;
   }

   /**
    * Returns true if there is an archived working branch. Note, this method does not necessarily mean that there was
    * ever a working branch cause archived branches can be deleted. Use isWorkingBranchEverCreated or
    * isWorkingBranchEverCommitted.
    * 
    * @return result
    * @throws OseeCoreException
    */
   public boolean isWorkingBranchArchived() throws OseeCoreException {
      Branch branch = getWorkingBranch(true, false);
      return branch != null && branch.getArchiveState().isArchived();
   }

   /**
    * Returns true if there was ever a commit of a working branch regardless of whether the working branch is archived
    * or not.
    * 
    * @return result
    * @throws OseeCoreException
    */
   public boolean isWorkingBranchEverCommitted() throws OseeCoreException {
      return getBranchesCommittedTo().size() > 0;
   }

   public Collection<ICommitConfigArtifact> getConfigArtifactsConfiguredToCommitTo() throws OseeCoreException {
      Set<ICommitConfigArtifact> configObjects = new HashSet<ICommitConfigArtifact>();
      if (smaMgr.isTeamUsesVersions()) {
         if (smaMgr.getTargetedForVersion() != null) {
            smaMgr.getTargetedForVersion().getParallelVersions(configObjects);
         }
      } else {
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact && ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getParentBranch() != null) {
            configObjects.add(((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition());
         }
      }
      return configObjects;
   }

   public ICommitConfigArtifact getParentBranchConfigArtifactConfiguredToCommitTo() throws OseeCoreException {
      if (smaMgr.isTeamUsesVersions()) {
         if (smaMgr.getTargetedForVersion() != null) {
            return smaMgr.getTargetedForVersion();
         }
      } else {
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact && ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getParentBranch() != null) {
            return ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition();
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
    * 
    * @return true
    * @throws OseeCoreException
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
               Display.getCurrent().getActiveShell(),
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

   public static void createNecessaryBranchEventReviews(StateEventType stateEventType, SMAManager smaMgr, SkynetTransaction transaction) throws OseeCoreException {
      if (stateEventType != StateEventType.CommitBranch && stateEventType != StateEventType.CreateBranch) {
         throw new OseeStateException("Invalid stateEventType = " + stateEventType);
      }
      // Create any decision and peerToPeer reviews for createBranch and commitBranch
      for (String ruleId : Arrays.asList(AtsAddDecisionReviewRule.ID, AtsAddPeerToPeerReviewRule.ID)) {
         for (WorkRuleDefinition workRuleDef : smaMgr.getWorkRulesStartsWith(ruleId)) {
            StateEventType eventType = AtsAddDecisionReviewRule.getStateEventType(smaMgr, workRuleDef);
            if (eventType != null && eventType == stateEventType) {
               if (ruleId.equals(AtsAddDecisionReviewRule.ID)) {
                  DecisionReviewArtifact decArt =
                        AtsAddDecisionReviewRule.createNewDecisionReview(workRuleDef, transaction, smaMgr,
                              DecisionRuleOption.TransitionToDecision);
                  if (decArt != null) {
                     decArt.persist(transaction);
                  }
               } else if (ruleId.equals(AtsAddPeerToPeerReviewRule.ID)) {
                  PeerToPeerReviewArtifact peerArt =
                        AtsAddPeerToPeerReviewRule.createNewPeerToPeerReview(workRuleDef, smaMgr, transaction);
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
      if (smaMgr.isTeamUsesVersions()) {
         VersionArtifact verArt = smaMgr.getTargetedForVersion();
         if (verArt != null) {
            parentBranch = verArt.getParentBranch();
         }
      }

      // If not defined in version, check for parent branch from team definition
      if (parentBranch == null && smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
         parentBranch = ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getParentBranch();
      }

      // If not defined, return null
      return parentBranch;
   }

   /**
    * Create a working branch associated with this state machine artifact. This should NOT be called by applications
    * except in test cases or automated tools. Use createWorkingBranchWithPopups
    * 
    * @param pageId
    * @param parentBranch
    * @throws Exception
    */
   public void createWorkingBranch(String pageId, final IOseeBranch parentBranch) throws OseeCoreException {
      final Artifact stateMachineArtifact = smaMgr.getSma();
      String smaTitle = stateMachineArtifact.getName();
      if (smaTitle.length() > 40) {
         smaTitle = smaTitle.substring(0, 39) + "...";
      }
      final String branchName = String.format("%s - %s", stateMachineArtifact.getHumanReadableId(), smaTitle);

      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public IStatus run(IProgressMonitor monitor) throws OseeCoreException {
            BranchManager.createWorkingBranch(parentBranch, branchName, stateMachineArtifact);
            // Create reviews as necessary
            SkynetTransaction transaction =
                  new SkynetTransaction(AtsUtil.getAtsBranch(), "Create Reviews upon Transition");
            createNecessaryBranchEventReviews(StateEventType.CreateBranch, smaMgr, transaction);
            transaction.execute();
            return Status.OK_STATUS;
         }
      };

      Jobs.runInJob("Create Branch", runnable, AtsPlugin.class, AtsPlugin.PLUGIN_ID);
   }

   private final class AtsCommitJob extends Job {
      private final boolean commitPopup;
      private final boolean overrideStateValidation;
      private final Branch destinationBranch;
      private final boolean archiveWorkingBranch;

      /**
       * @param name
       * @param commitPopup
       * @param overrideStateValidation
       */
      public AtsCommitJob(boolean commitPopup, boolean overrideStateValidation, Branch destinationBranch, boolean archiveWorkingBranch) {
         super("Commit Branch");
         this.commitPopup = commitPopup;
         this.overrideStateValidation = overrideStateValidation;
         this.destinationBranch = destinationBranch;
         this.archiveWorkingBranch = archiveWorkingBranch;
      }

      private boolean adminOverride;

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Branch workflowWorkingBranch = null;
         try {
            workflowWorkingBranch = getWorkingBranch(true, false);
            branchesInCommit.add(workflowWorkingBranch);
            if (workflowWorkingBranch == null) {
               return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID,
                     "Commit Branch Failed: Can not locate branch for workflow " + smaMgr.getSma().getHumanReadableId());
            }

            // Confirm that all blocking reviews are completed
            // Loop through this state's blocking reviews to confirm complete
            for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviewsFromCurrentState()) {
               if (reviewArt.getReviewBlockType() == ReviewBlockType.Commit && !reviewArt.getSmaMgr().isCancelledOrCompleted()) {
                  return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID,
                        "Blocking Review must be completed before commit.");
               }
            }

            if (!overrideStateValidation) {
               adminOverride = false;
               // Check extension points for valid commit
               for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(smaMgr.getWorkPageDefinition().getId())) {
                  final Result tempResult = item.committing(smaMgr);
                  if (tempResult.isFalse()) {
                     // Allow Admin to override state validation
                     if (AtsUtil.isAtsAdmin()) {
                        Displays.ensureInDisplayThread(new Runnable() {
                           @Override
                           public void run() {
                              if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                                    "Override State Validation",
                                    tempResult.getText() + "\n\nYou are set as Admin, OVERRIDE this?")) {
                                 adminOverride = true;
                              } else {
                                 adminOverride = false;
                              }
                           }
                        }, true);
                     }
                     if (!adminOverride) {
                        return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, tempResult.getText());
                     }
                  }
               }
            }

            commit(commitPopup, workflowWorkingBranch, destinationBranch, archiveWorkingBranch);
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, ex.getLocalizedMessage(), ex);
         } finally {
            if (workflowWorkingBranch != null) {
               branchesInCommit.remove(workflowWorkingBranch);
            }
         }
         return Status.OK_STATUS;
      }
   }

   private void commit(boolean commitPopup, Branch sourceBranch, Branch destinationBranch, boolean archiveWorkingBranch) throws OseeCoreException {
      boolean branchCommitted = false;
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(destinationBranch, sourceBranch);

      if (commitPopup) {
         branchCommitted = CommitHandler.commitBranch(conflictManager, archiveWorkingBranch);
      } else {
         BranchManager.commitBranch(null, conflictManager, archiveWorkingBranch, true);
         branchCommitted = true;
      }
      if (branchCommitted) {
         // Create reviews as necessary
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Create Reviews upon Commit");
         createNecessaryBranchEventReviews(StateEventType.CommitBranch, smaMgr, transaction);
         transaction.execute();
      }
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
    *           used for developmental testing or automation
    */
   public void commitWorkingBranch(final boolean commitPopup, final boolean overrideStateValidation, Branch destinationBranch, boolean archiveWorkingBranch) throws OseeCoreException {
      commitWorkingBranch(commitPopup, overrideStateValidation, destinationBranch, archiveWorkingBranch, false);
   }

   public void commitWorkingBranch(final boolean commitPopup, final boolean overrideStateValidation, Branch destinationBranch, boolean archiveWorkingBranch, boolean forcePend) throws OseeCoreException {
      if (isBranchInCommit()) {
         throw new OseeCoreException("Branch is currently being committed.");
      }
      Job job = new AtsCommitJob(commitPopup, overrideStateValidation, destinationBranch, archiveWorkingBranch);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
      if (forcePend) {
         try {
            job.join();
         } catch (InterruptedException ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
   }

   /**
    * Since change data for a committed branch is not going to change, cache it per run instance of OSEE
    */
   private static final Map<TransactionRecord, ChangeData> changeDataCacheForCommittedBranch =
         new HashMap<TransactionRecord, ChangeData>();

   public ChangeData getChangeDataFromEarliestTransactionId() throws OseeCoreException {
      return getChangeData(null);
   }

   /**
    * Return ChangeData represented by commit to commitConfigArt or earliest commit if commitConfigArt == null
    * 
    * @param commitConfigArt that configures commit or null
    * @return ChangeData
    * @throws OseeCoreException
    */
   public ChangeData getChangeData(ICommitConfigArtifact commitConfigArt) throws OseeCoreException {
      if (commitConfigArt != null && commitConfigArt.getParentBranch() == null) {
         throw new OseeArgumentException("Parent Branch not configured for " + commitConfigArt);
      }
      ChangeData changeData = null;
      if (smaMgr.getBranchMgr().isWorkingBranchInWork()) {
         changeData = new ChangeData(ChangeManager.getChangesPerBranch(getWorkingBranch(), new NullProgressMonitor()));
      } else if (smaMgr.getBranchMgr().isCommittedBranchExists()) {
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
         changeData = null;//shangeDataCacheForCommittedBranch.get(transactionId);
         if (changeData == null) {
            changeData =
                  new ChangeData(ChangeManager.getChangesPerTransaction(transactionId, new NullProgressMonitor()));
            changeDataCacheForCommittedBranch.put(transactionId, changeData);
         }
      } else {
         changeData = new ChangeData(new ArrayList<Change>());
      }
      return changeData;
   }

   public Boolean isWorkingBranchHaveChanges() throws OseeCoreException {
      Branch branch = getWorkingBranch();
      return branch != null && BranchManager.hasChanges(branch);
   }

   public SMAManager getSmaMgr() {
      return smaMgr;
   }

}