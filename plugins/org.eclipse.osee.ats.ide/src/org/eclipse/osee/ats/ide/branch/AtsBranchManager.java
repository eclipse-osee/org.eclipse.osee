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

package org.eclipse.osee.ats.ide.branch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.branch.internal.AtsBranchCommitOperation;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
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
public final class AtsBranchManager {

   private AtsBranchManager() {
      // Utility class
   }

   public static void showMergeManager(TeamWorkFlowArtifact teamArt) {
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

   public static void showMergeManager(TeamWorkFlowArtifact teamArt, BranchId destinationBranch) {
      if (AtsClientService.get().getBranchService().isWorkingBranchInWork(teamArt)) {
         IOseeBranch workingBranch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt);
         MergeView.openView(workingBranch, destinationBranch, BranchManager.getBaseTransaction(workingBranch));
      } else if (AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt)) {
         for (TransactionRecord transactionId : AtsClientService.get().getBranchService().getTransactionIds(teamArt,
            true)) {
            if (transactionId.isOnBranch(destinationBranch)) {
               MergeView.openView(transactionId);
            }
         }
      }
   }

   /**
    * If working branch has no changes, allow for deletion.
    */
   public static boolean deleteWorkingBranch(TeamWorkFlowArtifact teamWf, boolean promptUser, boolean pend) {
      boolean isExecutionAllowed = !promptUser;
      try {
         BranchId branch = AtsClientService.get().getBranchService().getWorkingBranch(teamWf);
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
               result = AtsBranchUtil.deleteWorkingBranch(teamWf, pend);
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
   public static TransactionToken getTransactionIdOrPopupChoose(IAtsTeamWorkflow teamWf, String title, boolean showMergeManager) {
      Collection<TransactionRecord> transactions =
         AtsClientService.get().getBranchService().getTransactionIds(teamWf, showMergeManager);
      final Map<IOseeBranch, TransactionId> branchToTx = new LinkedHashMap<>();

      if (transactions.size() == 1) {
         return transactions.iterator().next();
      }
      for (TransactionRecord id : transactions) {
         // ignore working branches that have been committed or re-baselined (e.g. update form parent branch)
         boolean workingBranch = BranchManager.getType(id).isWorkingBranch();
         BranchState state = BranchManager.getState(id.getBranch());
         if (!workingBranch || !(state.isRebaselined() && state.isCommitted())) {
            IOseeBranch branch = BranchManager.getBranchToken(id.getBranch());
            branchToTx.put(branch, id);
         }
      }

      ViewerComparator comparator = new ViewerComparator() {
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 == null || e2 == null) {
               return 0;
            }
            Long b1 = ((IOseeBranch) e1).getId();
            Long b2 = ((IOseeBranch) e1).getId();
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
      if (dialog.open() == 0) {
         IOseeBranch branch = dialog.getSelectedFirst();
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
   public static void showChangeReport(IAtsTeamWorkflow teamArt) {
      try {
         if (AtsClientService.get().getBranchService().isWorkingBranchInWork(teamArt)) {
            BranchId parentBranch = AtsClientService.get().getBranchService().getConfiguredBranchForWorkflow(teamArt);
            Conditions.assertNotNull(parentBranch,
               "Parent Branch can not be null. Set Targeted Version or configure Team for Parent Branch");
            IOseeBranch workingBranch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt);
            ChangeUiUtil.open(workingBranch, parentBranch, true);
         } else if (AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt)) {
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
   public static void showChangeReportForBranch(TeamWorkFlowArtifact teamArt, BranchId destinationBranch) {
      try {
         for (TransactionToken transactionId : AtsClientService.get().getBranchService().getTransactionIds(teamArt,
            false)) {
            if (transactionId.isOnBranch(destinationBranch)) {
               ChangeUiUtil.open(transactionId);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't show change report.", ex);
      }
   }

   /**
    * @param commitPopup if true, pop-up errors associated with results
    * @param overrideStateValidation if true, don't do checks to see if commit can be performed. This should only be
    * used for developmental testing or automation
    */
   public static IOperation commitWorkingBranch(final TeamWorkFlowArtifact teamArt, final boolean commitPopup, final boolean overrideStateValidation, BranchId destinationBranch, boolean archiveWorkingBranch) {
      if (AtsClientService.get().getBranchService().isBranchInCommit(teamArt)) {
         throw new OseeCoreException("Branch is currently being committed.");
      }
      return new AtsBranchCommitOperation(teamArt, commitPopup, overrideStateValidation, destinationBranch,
         archiveWorkingBranch);
   }

   public static ChangeData getChangeDataFromEarliestTransactionId(IAtsTeamWorkflow teamWf) {
      return getChangeData(teamWf, null);
   }

   /**
    * Return ChangeData represented by commit to commitConfigArt or earliest commit if commitConfigArt == null
    *
    * @param commitConfigArt that configures commit or null
    */
   public static ChangeData getChangeData(IAtsTeamWorkflow teamWf, ICommitConfigItem commitConfigArt) {
      if (commitConfigArt != null && !isBaselinBranchConfigured(commitConfigArt)) {
         throw new OseeArgumentException("Parent Branch not configured for [%s]", commitConfigArt);
      }
      Collection<Change> changes = new ArrayList<>();

      IOperation operation = null;
      if (AtsClientService.get().getBranchService().isWorkingBranchInWork(teamWf)) {
         operation =
            ChangeManager.comparedToParent(AtsClientService.get().getBranchService().getWorkingBranch(teamWf), changes);
         Operations.executeWorkAndCheckStatus(operation);
      } else {
         if (AtsClientService.get().getBranchService().isCommittedBranchExists(teamWf)) {
            TransactionToken transactionId = null;
            if (commitConfigArt == null) {
               transactionId = AtsClientService.get().getBranchService().getEarliestTransactionId(teamWf);
            } else {
               Collection<TransactionRecord> transIds =
                  AtsClientService.get().getBranchService().getTransactionIds(teamWf, false);
               if (transIds.size() == 1) {
                  transactionId = transIds.iterator().next();
               } else {
                  /*
                   * First, attempt to compare the currently configured commitConfigArt parent branch with transaction
                   * id's branch.
                   */
                  for (TransactionRecord transId : transIds) {
                     if (transId.isOnBranch(commitConfigArt.getBaselineBranchId())) {
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
               throw new OseeStateException("Unable to determine transaction id for [%s]", commitConfigArt);
            }
            operation = ChangeManager.comparedToPreviousTx(transactionId, changes);
            Operations.executeWorkAndCheckStatus(operation);
         }
      }
      return new ChangeData(changes);
   }

   private static boolean isBaselinBranchConfigured(ICommitConfigItem commitConfigArt) {
      return commitConfigArt.getBaselineBranchId().isValid();
   }
}