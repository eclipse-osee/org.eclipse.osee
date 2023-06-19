/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.core.review.DecisionReviewOnTransitionToHook;
import org.eclipse.osee.ats.core.review.PeerReviewOnTransitionToHook;
import org.eclipse.osee.ats.ide.branch.AtsBranchServiceIde;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.CatchAndReleaseJob;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.change.WordChangeUtil;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.util.NameLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeBranchDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.ui.PlatformUI;

/**
 * BranchManager contains methods necessary for ATS objects to interact with creation, view and commit of branches.
 *
 * @author Donald G. Dunne
 */
public final class AtsBranchServiceIdeImpl implements AtsBranchServiceIde {

   private final AtsApi atsApi;

   public AtsBranchServiceIdeImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public void showMergeManager(TeamWorkFlowArtifact teamArt) {
      try {
         BranchId workingBranch = teamArt.getWorkingBranch();
         List<BranchId> destinationBranches = new ArrayList<>();

         if (workingBranch.isValid()) {
            List<MergeBranch> mergeBranches = BranchManager.getMergeBranches(workingBranch);
            BranchId selectedBranch = null;

            if (!mergeBranches.isEmpty()) {
               if (!BranchManager.getState(workingBranch).isRebaselineInProgress()) {
                  for (MergeBranch mergeBranch : mergeBranches) {
                     destinationBranches.add(mergeBranch.getDestinationBranch());
                  }
                  if (mergeBranches.size() > 1) {
                     FilteredTreeBranchDialog dialog = new FilteredTreeBranchDialog("Select Destination Branch",
                        "Select The Destination Branch for which you want to open the Merge Manager",
                        destinationBranches);
                     int result = dialog.open();
                     if (result == 0) {
                        selectedBranch = (BranchId) dialog.getSelectedFirst();
                     }
                  } else {
                     MergeBranch updateFromParentMergeBranch = BranchManager.getFirstMergeBranch(workingBranch);
                     selectedBranch = updateFromParentMergeBranch.getDestinationBranch();
                  }
               } else {
                  // the only merge branch is the Update from parent merge branch
                  MergeBranch updateFromParentMergeBranch = BranchManager.getFirstMergeBranch(workingBranch);
                  selectedBranch = updateFromParentMergeBranch.getDestinationBranch();
               }

               if (selectedBranch != null) {
                  MergeView.openView(workingBranch, selectedBranch, BranchManager.getBaseTransaction(workingBranch));
               }
            } else {
               MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
                  "There are no Merge Branches to view");
            }
         } else {
            MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
               "This Artifact does not have a working branch");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void showMergeManager(TeamWorkFlowArtifact teamArt, BranchId destinationBranch) {
      if (atsApi.getBranchService().isWorkingBranchInWork(teamArt)) {
         BranchToken workingBranch = atsApi.getBranchService().getWorkingBranch(teamArt);
         MergeView.openView(workingBranch, destinationBranch, BranchManager.getBaseTransaction(workingBranch));
      } else if (atsApi.getBranchService().isCommittedBranchExists(teamArt)) {
         for (TransactionRecord transactionId : atsApi.getBranchService().getTransactionIds(teamArt, true)) {
            if (transactionId.isOnBranch(destinationBranch)) {
               MergeView.openView(transactionId);
            }
         }
      }
   }

   /**
    * If working branch has no changes, allow for deletion.
    */
   @Override
   public boolean deleteWorkingBranch(TeamWorkFlowArtifact teamWf, boolean promptUser, boolean pend) {
      boolean isExecutionAllowed = !promptUser;
      try {
         BranchId branch = atsApi.getBranchService().getWorkingBranch(teamWf);
         if (promptUser) {
            StringBuilder message = new StringBuilder();
            if (BranchManager.hasChanges(branch)) {
               message.append("Warning: Changes have been made on this branch.\n\n");
            }
            message.append("Are you sure you want to delete the branch? \n\n");
            message.append("BRANCH NAME: \n\"" + branch + "\"");

            isExecutionAllowed =
               MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Delete Working Branch", message.toString());
         }

         if (isExecutionAllowed) {
            Exception exception = null;
            Result result = Result.FalseResult;
            try {
               result = deleteWorkingBranch(teamWf, pend);
            } catch (Exception ex) {
               exception = ex;
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Problem deleting branch.", ex);
            }
            if (result != null) {
               if (promptUser) {
                  AWorkbench.popup("Delete Complete",
                     result.isTrue() ? "Branch delete was successful." : "Branch delete failed.\n" + result.getText());
               } else if (result.isFalse()) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, result.getText(), exception);
               }
            }
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Problem deleting branch.", ex);
      }
      return false;
   }

   /**
    * Either return a single commit transaction or user must choose from a list of valid commit transactions
    */
   @Override
   public TransactionToken getTransactionIdOrPopupChoose(IAtsTeamWorkflow teamWf, String title,
      boolean showMergeManager) {
      Collection<TransactionRecord> transactions =
         atsApi.getBranchService().getTransactionIds(teamWf, showMergeManager);
      final Map<BranchToken, TransactionId> branchToTx = new LinkedHashMap<>();

      if (transactions.size() == 1) {
         return transactions.iterator().next();
      }
      for (TransactionRecord id : transactions) {
         // ignore working branches that have been committed or re-baselined (e.g. update form parent branch)
         boolean workingBranch = BranchManager.getType(id).isWorkingBranch();
         BranchState state = BranchManager.getState(id.getBranch());
         if (!workingBranch || !(state.isRebaselined() && state.isCommitted())) {
            BranchToken branch = BranchManager.getBranchToken(id.getBranch());
            branchToTx.put(branch, id);
         }
      }

      ViewerComparator comparator = new ViewerComparator() {
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 == null || e2 == null) {
               return 0;
            }
            Long b1 = ((BranchToken) e1).getId();
            Long b2 = ((BranchToken) e1).getId();
            if (b1 > b2) {
               return -1;
            }
            if (b2 > b1) {
               return 1;
            }
            return 0;
         }
      };
      FilteredTreeDialog dialog = new FilteredTreeDialog(title, "Select Commit Branch", new ArrayTreeContentProvider(),
         new NameLabelProvider(), comparator);

      dialog.setInput(branchToTx.keySet());
      if (dialog.open() == Window.OK) {
         BranchToken branch = dialog.getSelectedFirst();
         if (branch != null) {
            TransactionId id = branchToTx.get(branch);
            return TransactionToken.valueOf(id, branch);
         }
      }
      return TransactionToken.SENTINEL;
   }

   /**
    * Display change report associated with the branch, if exists, or transaction, if branch has been committed.
    */
   @Override
   public void showChangeReport(IAtsTeamWorkflow teamArt) {
      try {
         if (atsApi.getBranchService().isWorkingBranchInWork(teamArt)) {
            BranchId parentBranch = atsApi.getBranchService().getConfiguredBranchForWorkflow(teamArt);
            if (parentBranch.isInvalid()) {
               AWorkbench.popup("Parent Branch Error",
                  "Parent Branch can not be null. Set Targeted Version or configure Team for Parent Branch");
               return;
            }
            BranchToken workingBranch = atsApi.getBranchService().getWorkingBranch(teamArt);
            ChangeUiUtil.open(workingBranch, parentBranch, true);
         } else if (atsApi.getBranchService().isCommittedBranchExists(teamArt)) {
            TransactionToken transactionId = getTransactionIdOrPopupChoose(teamArt, "Show Change Report", false);
            if (TransactionToken.SENTINEL.equals(transactionId)) {
               return;
            }
            ChangeUiUtil.open(transactionId);
         } else {
            AWorkbench.popup("ERROR", "No Branch or Committed Transaction Found.");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't show change report.", ex);
      }
   }

   /**
    * Grab the change report for the indicated branch
    */
   @Override
   public void showChangeReportForBranch(TeamWorkFlowArtifact teamArt, BranchId destinationBranch) {
      try {
         for (TransactionToken transactionId : atsApi.getBranchService().getTransactionIds(teamArt, false)) {
            if (transactionId.isOnBranch(destinationBranch)) {
               ChangeUiUtil.open(transactionId);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't show change report.", ex);
      }
   }

   @Override
   public void generateWordChangeReport(IAtsTeamWorkflow teamArt) {
      List<Change> localChanges = (List<Change>) getChangeData(teamArt, null).getChanges();
      WordChangeUtil.generateWordTemplateChangeReport(localChanges, PresentationType.DIFF, false, false);
   }

   @Override
   public void generateContextChangeReport(IAtsTeamWorkflow teamArt) {
      List<Change> localChanges = (List<Change>) getChangeData(teamArt, null).getChanges();
      WordChangeUtil.generateWordTemplateChangeReport(localChanges, PresentationType.DIFF_NO_ATTRIBUTES, true, true);
   }

   /**
    * @param commitPopup if true, pop-up errors associated with results
    * @param overrideStateValidation if true, don't do checks to see if commit can be performed. This should only be
    * used for developmental testing or automation
    */
   @Override
   public XResultData commitWorkingBranch(final TeamWorkFlowArtifact teamArt, final boolean commitPopup,
      final boolean overrideStateValidation, BranchId destinationBranch, boolean archiveWorkingBranch, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      if (atsApi.getBranchService().isBranchInCommit(teamArt)) {
         rd.error("Branch is currently being committed.");
      }
      try {
         new AtsBranchCommitOperation(teamArt, commitPopup, overrideStateValidation, destinationBranch,
            archiveWorkingBranch, rd).run();
      } catch (Exception ex) {
         rd.errorf("Exception Committing Branch %s", Lib.exceptionToString(ex));
      }
      return rd;
   }

   @Override
   public ChangeData getChangeDataFromEarliestTransactionId(IAtsTeamWorkflow teamWf) {
      return getChangeData(teamWf, null);
   }

   /**
    * Return ChangeItemData represented by commit to commitConfigArt or earliest commit if commitConfigArt == null
    *
    * @param commitConfigItem that configures commit or null
    */
   @Override
   public ChangeData getChangeData(IAtsTeamWorkflow teamWf, CommitConfigItem commitConfigItem) {
      if (commitConfigItem != null && !isBaselinBranchConfigured(commitConfigItem)) {
         throw new OseeArgumentException("Parent Branch not configured for [%s]", commitConfigItem);
      }
      Collection<Change> changes = new ArrayList<>();

      IOperation operation = null;
      if (atsApi.getBranchService().isWorkingBranchInWork(teamWf)) {
         operation = ChangeManager.comparedToParent(atsApi.getBranchService().getWorkingBranch(teamWf), changes);
         Operations.executeWorkAndCheckStatus(operation);
      } else {
         if (atsApi.getBranchService().isCommittedBranchExists(teamWf)) {
            TransactionToken transactionId = null;
            if (commitConfigItem == null) {
               transactionId = atsApi.getBranchService().getEarliestTransactionId(teamWf);
            } else {
               Collection<TransactionRecord> transIds = atsApi.getBranchService().getTransactionIds(teamWf, false);
               if (transIds.size() == 1) {
                  transactionId = transIds.iterator().next();
               } else {
                  /*
                   * First, attempt to compare the currently configured commitConfigArt parent branch with transaction
                   * id's branch.
                   */
                  for (TransactionRecord transId : transIds) {
                     if (transId.isOnBranch(commitConfigItem.getBaselineBranchId())) {
                        transactionId = transId;
                     }
                  }
                  /*
                   * Otherwise, fallback to getting the lowest transaction id number. This could happen if branches were
                   * rebaselined cause previous transId branch would not match currently configured parent branch. This
                   * could also happen if workflow not targeted for version and yet commits happened
                   */
                  if (transactionId == null) {
                     TransactionRecord transactionRecord = null;
                     for (TransactionRecord transId : transIds) {
                        if (transactionRecord == null || transId.getId() < transactionRecord.getId()) {
                           transactionRecord = transId;
                        }
                     }
                     transactionId = transactionRecord;
                  }
               }
            }
            if (transactionId == null) {
               throw new OseeStateException("Unable to determine transaction id for [%s]", commitConfigItem);
            }
            operation = ChangeManager.comparedToPreviousTx(transactionId, changes);
            Operations.executeWorkAndCheckStatus(operation);
         }
      }
      return new ChangeData(changes);
   }

   private static boolean isBaselinBranchConfigured(CommitConfigItem commitConfigItem) {
      return commitConfigItem.getBaselineBranchId().isValid();
   }

   /**
    * @return true if one or more reviews were created
    */
   @Override
   public boolean createNecessaryBranchEventReviews(StateEventType stateEventType, IAtsTeamWorkflow teamWf,
      Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      Conditions.checkNotNull(teamWf, "Team Workflow");
      boolean created = false;
      if (stateEventType != StateEventType.CommitBranch && stateEventType != StateEventType.CreateBranch) {
         throw new OseeStateException("Invalid stateEventType [%s]", stateEventType);
      }
      TeamWorkFlowArtifact teamWfArt = (TeamWorkFlowArtifact) atsApi.getQueryService().getArtifact(teamWf);
      // Create any decision and peerToPeer reviews for createBranch and commitBranch
      for (IAtsDecisionReviewDefinition decRevDef : teamWf.getStateDefinition().getDecisionReviews()) {
         if (decRevDef.getStateEventType() != null && decRevDef.getStateEventType().equals(stateEventType)) {
            IAtsDecisionReview decRev = DecisionReviewOnTransitionToHook.createNewDecisionReview(decRevDef, changes,
               teamWfArt, createdDate, createdBy);
            if (decRev != null) {
               created = true;
               changes.add(decRev);
            }
         }
      }
      for (IAtsPeerReviewDefinition peerRevDef : teamWf.getStateDefinition().getPeerReviews()) {
         if (peerRevDef.getStateEventType() != null && peerRevDef.getStateEventType().equals(stateEventType)) {
            IAtsPeerToPeerReview peerRev = PeerReviewOnTransitionToHook.createNewPeerToPeerReview(peerRevDef, changes,
               teamWfArt, createdDate, createdBy);
            if (peerRev != null) {
               created = true;
               changes.add(peerRev);
            }
         }
      }
      return created;
   }

   /**
    * Perform error checks and popup confirmation dialogs associated with creating a working branch.
    *
    * @param popup if true, errors are popped up to user; otherwise sent silently in Results
    * @return Result return of status
    */
   @Override
   public Result createWorkingBranch_Validate(IAtsTeamWorkflow teamWf) {
      try {
         if (atsApi.getBranchService().isCommittedBranchExists(teamWf)) {
            return new Result(
               "Committed branch already exists. Can not create another working branch once changes have been committed.");
         }
         if (atsApi.getBranchService().isWorkingBranchInWork(teamWf)) {
            return new Result("Cannot create another branch while the current branch is in work.");
         }
         BranchId parentBranch = atsApi.getBranchService().getConfiguredBranchForWorkflow(teamWf);
         if (parentBranch == null || parentBranch.isInvalid()) {
            return new Result(PARENT_BRANCH_CAN_NOT_BE_DETERMINED);
         }

         if (atsApi.getBranchService().getBranch(parentBranch) == null) {
            return new Result(PARENT_BRANCH_CAN_NOT_BE_DETERMINED);
         }

         Result result = atsApi.getBranchService().isCreateBranchAllowed(teamWf);
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
   @Override
   public Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt) {
      return createWorkingBranch_Create(teamArt, false);
   }

   /**
    * Create a working branch associated with this state machine artifact. This should NOT be called by applications
    * except in test cases or automated tools. Use createWorkingBranchWithPopups
    */
   @Override
   public Job createWorkingBranch_Create(final IAtsTeamWorkflow teamWf, boolean pend) {
      final BranchId parentBranch = atsApi.getBranchService().getConfiguredBranchForWorkflow(teamWf);
      return createWorkingBranch_Create(teamWf, parentBranch, pend);
   }

   @Override
   public Job createWorkingBranch_Create(final TeamWorkFlowArtifact teamArt, final BranchId parentBranch) {
      return createWorkingBranch_Create(teamArt, parentBranch, false);
   }

   @Override
   public Job createWorkingBranch_Create(final IAtsTeamWorkflow teamWf, final BranchId parentBranch, boolean pend) {
      Conditions.checkNotNull(teamWf, "Parent Team Workflow");
      Conditions.checkNotNull(parentBranch, "Parent Branch");
      Conditions.checkValid(parentBranch, "Parent Branch");
      TransactionToken parentTransactionId = TransactionManager.getHeadTransaction(parentBranch);
      return createWorkingBranch(teamWf, parentTransactionId, pend);
   }

   @Override
   public Job createWorkingBranch(final IAtsTeamWorkflow teamWf, final TransactionToken parentTransactionId,
      boolean pend) {
      final String branchName = atsApi.getBranchService().getBranchName(teamWf);
      Conditions.checkNotNull(teamWf, "Parent Team Workflow");
      Conditions.checkNotNull(parentTransactionId, "Parent Branch");

      IExceptionableRunnable runnable = new IExceptionableRunnable() {

         @Override
         public IStatus run(IProgressMonitor monitor) {
            XResultData rd = new XResultData();
            for (IAtsWorkItemHook item : atsApi.getWorkItemService().getWorkItemHooks()) {
               rd = item.creatingBranch(teamWf, rd);
            }
            if (rd.isErrors()) {
               throw new OseeCoreException("Can not create branch.  Reason: [%s]", rd.toString());
            }
            atsApi.getBranchService().setWorkingBranchCreationInProgress(teamWf, true);
            BranchToken branch = BranchManager.createWorkingBranch(parentTransactionId, branchName,
               atsApi.getQueryService().getArtifact(teamWf));
            atsApi.getBranchService().setWorkingBranchCreationInProgress(teamWf, false);
            Conditions.assertTrue(branch.isValid(), "Working Branch creation failed.");
            performPostBranchCreationTasks(teamWf);
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
            throw OseeCoreException.wrap(ex);
         }
      }
      return job;
   }

   private void performPostBranchCreationTasks(final IAtsTeamWorkflow teamWf) {
      // Create reviews as necessary
      IAtsChangeSet changes = atsApi.createChangeSet("Create Reviews upon Transition");
      boolean created = createNecessaryBranchEventReviews(StateEventType.CreateBranch, teamWf, new Date(),
         AtsCoreUsers.SYSTEM_USER, changes);
      if (created) {
         changes.execute();
      }

      // Notify extensions of branch creation
      XResultData rd = new XResultData();
      for (IAtsWorkItemHook item : atsApi.getWorkItemService().getWorkItemHooks()) {
         item.workingBranchCreated(teamWf, rd);
      }
      if (rd.isErrors()) {
         ResultsEditor.open("Branch Creation Tasks - Error", rd);
      }
   }

   @Override
   public Result deleteWorkingBranch(TeamWorkFlowArtifact teamArt, boolean pend) {
      BranchId branch = atsApi.getBranchService().getWorkingBranch(teamArt);
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
               throw OseeCoreException.wrap(ex);
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