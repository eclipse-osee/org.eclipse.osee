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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.core.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.core.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.review.DecisionReviewDefinitionManager;
import org.eclipse.osee.ats.core.review.PeerReviewDefinitionManager;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.core.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.util.TransactionIdLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * BranchManager contains methods necessary for ATS objects to interact with creation, view and commit of branches.
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchManager implements IBranchEventListener {

   private static AtsBranchManager instance;

   public static void showMergeManager(TeamWorkFlowArtifact teamArt) {
      try {
         if (!AtsBranchManagerCore.isWorkingBranchInWork(teamArt) && !AtsBranchManagerCore.isCommittedBranchExists(teamArt)) {
            AWorkbench.popup("ERROR", "No Current Working or Committed Branch");
            return;
         }
         if (AtsBranchManagerCore.isWorkingBranchInWork(teamArt)) {
            Branch branch = AtsBranchManagerCore.getConfiguredBranchForWorkflow(teamArt);
            if (branch == null) {
               AWorkbench.popup("ERROR", "Can't access parent branch");
               return;
            }
            Branch workBranch = AtsBranchManagerCore.getWorkingBranch(teamArt);
            MergeView.openView(workBranch, branch, workBranch.getBaseTransaction());

         } else if (AtsBranchManagerCore.isCommittedBranchExists(teamArt)) {
            TransactionRecord transactionId = getTransactionIdOrPopupChoose(teamArt, "Show Merge Manager", true);
            if (transactionId == null) {
               return;
            }
            MergeView.openView(transactionId);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void showMergeManager(TeamWorkFlowArtifact teamArt, Branch destinationBranch) throws OseeCoreException {
      if (AtsBranchManagerCore.isWorkingBranchInWork(teamArt)) {
         MergeView.openView(AtsBranchManagerCore.getWorkingBranch(teamArt), destinationBranch,
            AtsBranchManagerCore.getWorkingBranch(teamArt).getBaseTransaction());
      } else if (AtsBranchManagerCore.isCommittedBranchExists(teamArt)) {
         for (TransactionRecord transactionId : AtsBranchManagerCore.getTransactionIds(teamArt, true)) {
            if (transactionId.getBranchId() == destinationBranch.getId()) {
               MergeView.openView(transactionId);

            }
         }
      }
   }

   /**
    * Opens the branch currently associated with this state machine artifact.
    */
   public static void showWorkingBranch(TeamWorkFlowArtifact teamArt) {
      try {
         if (!AtsBranchManagerCore.isWorkingBranchInWork(teamArt)) {
            AWorkbench.popup("ERROR", "No Current Working Branch");
            return;
         }
         BranchView.revealBranch(AtsBranchManagerCore.getWorkingBranch(teamArt));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * If working branch has no changes, allow for deletion.
    */
   public static void deleteWorkingBranch(TeamWorkFlowArtifact teamArt, boolean promptUser) {
      boolean isExecutionAllowed = !promptUser;
      try {
         Branch branch = AtsBranchManagerCore.getWorkingBranch(teamArt);
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

   /**
    * Either return a single commit transaction or user must choose from a list of valid commit transactions
    */
   public static TransactionRecord getTransactionIdOrPopupChoose(TeamWorkFlowArtifact teamArt, String title, boolean showMergeManager) throws OseeCoreException {
      Collection<TransactionRecord> transactionIds = new HashSet<TransactionRecord>();
      for (TransactionRecord id : AtsBranchManagerCore.getTransactionIds(teamArt, showMergeManager)) {
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

   /**
    * Display change report associated with the branch, if exists, or transaction, if branch has been committed.
    */
   public static void showChangeReport(TeamWorkFlowArtifact teamArt) {
      try {
         if (AtsBranchManagerCore.isWorkingBranchInWork(teamArt)) {
            ChangeUiUtil.open(AtsBranchManagerCore.getWorkingBranch(teamArt));
         } else if (AtsBranchManagerCore.isCommittedBranchExists(teamArt)) {
            TransactionRecord transactionId = getTransactionIdOrPopupChoose(teamArt, "Show Change Report", false);
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
   public static void showChangeReportForBranch(TeamWorkFlowArtifact teamArt, Branch destinationBranch) {
      try {
         for (TransactionRecord transactionId : AtsBranchManagerCore.getTransactionIds(teamArt, false)) {
            if (transactionId.getBranch() == destinationBranch) {
               ChangeUiUtil.open(transactionId);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't show change report.", ex);
      }
   }

   /**
    * Perform error checks and popup confirmation dialogs associated with creating a working branch.
    * 
    * @param pageId if specified, WorkPage gets callback to provide confirmation that branch can be created
    * @param popup if true, errors are popped up to user; otherwise sent silently in Results
    * @return Result return of status
    */
   public static Result createWorkingBranch(TeamWorkFlowArtifact teamArt, String pageId, boolean popup) {
      try {
         if (AtsBranchManagerCore.isCommittedBranchExists(teamArt)) {
            if (popup) {
               AWorkbench.popup("ERROR", "Can not create another working branch once changes have been committed.");
            }
            return new Result("Committed branch already exists.");
         }
         Branch parentBranch = AtsBranchManagerCore.getConfiguredBranchForWorkflow(teamArt);
         if (parentBranch == null) {
            String errorStr =
               "Parent Branch can not be determined.\n\nPlease specify " + "parent branch through Version Artifact or Team Definition Artifact.\n\n" + "Contact your team lead to configure this.";
            if (popup) {
               AWorkbench.popup("ERROR", errorStr);
            }
            return new Result(errorStr);
         }
         Result result = AtsBranchManagerCore.isCreateBranchAllowed(teamArt);
         if (result.isFalse()) {
            if (popup) {
               AWorkbench.popup(result);
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
         createWorkingBranch(teamArt, pageId, parentBranch);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result("Exception occurred: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   public static void createNecessaryBranchEventReviews(StateEventType stateEventType, TeamWorkFlowArtifact teamArt, Date createdDate, User createdBy, SkynetTransaction transaction) throws OseeCoreException {
      if (stateEventType != StateEventType.CommitBranch && stateEventType != StateEventType.CreateBranch) {
         throw new OseeStateException("Invalid stateEventType [%s]", stateEventType);
      }
      // Create any decision and peerToPeer reviews for createBranch and commitBranch
      for (DecisionReviewDefinition decRevDef : teamArt.getStateDefinition().getDecisionReviews()) {
         if (decRevDef.getStateEventType() != null && decRevDef.getStateEventType().equals(stateEventType)) {
            DecisionReviewArtifact decArt =
               DecisionReviewDefinitionManager.createNewDecisionReview(decRevDef, transaction, teamArt, createdDate,
                  createdBy);
            if (decArt != null) {
               decArt.persist(transaction);
            }
         }
      }
      for (PeerReviewDefinition peerRevDef : teamArt.getStateDefinition().getPeerReviews()) {
         if (peerRevDef.getStateEventType() != null && peerRevDef.getStateEventType().equals(stateEventType)) {
            PeerToPeerReviewArtifact peerArt =
               PeerReviewDefinitionManager.createNewPeerToPeerReview(peerRevDef, transaction, teamArt, createdDate,
                  createdBy);
            if (peerArt != null) {
               peerArt.persist(transaction);
            }
         }
      }
   }

   /**
    * Create a working branch associated with this state machine artifact. This should NOT be called by applications
    * except in test cases or automated tools. Use createWorkingBranchWithPopups
    */
   public static void createWorkingBranch(final TeamWorkFlowArtifact teamArt, String pageId, final IOseeBranch parentBranch) {
      final String branchName = Strings.truncate(teamArt.getBranchName(), 195, true);

      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         @Override
         public IStatus run(IProgressMonitor monitor) throws OseeCoreException {
            teamArt.setWorkingBranchCreationInProgress(true);
            BranchManager.createWorkingBranch(parentBranch, branchName, teamArt);
            teamArt.setWorkingBranchCreationInProgress(false);
            // Create reviews as necessary
            SkynetTransaction transaction =
               new SkynetTransaction(AtsUtil.getAtsBranch(), "Create Reviews upon Transition");
            createNecessaryBranchEventReviews(StateEventType.CreateBranch, teamArt, new Date(),
               UserManager.getUser(SystemUser.OseeSystem), transaction);
            transaction.execute();
            return Status.OK_STATUS;
         }
      };

      Jobs.runInJob("Create Branch", runnable, AtsPlugin.class, AtsPlugin.PLUGIN_ID);
   }

   /**
    * @param commitPopup if true, pop-up errors associated with results
    * @param overrideStateValidation if true, don't do checks to see if commit can be performed. This should only be
    * used for developmental testing or automation
    */
   public static Job commitWorkingBranch(final TeamWorkFlowArtifact teamArt, final boolean commitPopup, final boolean overrideStateValidation, Branch destinationBranch, boolean archiveWorkingBranch) throws OseeCoreException {
      if (AtsBranchManagerCore.isBranchInCommit(teamArt)) {
         throw new OseeCoreException("Branch is currently being committed.");
      }
      Job job =
         new AtsBranchCommitJob(teamArt, commitPopup, overrideStateValidation, destinationBranch, archiveWorkingBranch);
      Operations.scheduleJob(job, true, Job.LONG, null);
      return job;
   }

   public static ChangeData getChangeDataFromEarliestTransactionId(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return getChangeData(teamArt, null);
   }

   /**
    * Return ChangeData represented by commit to commitConfigArt or earliest commit if commitConfigArt == null
    * 
    * @param commitConfigArt that configures commit or null
    */
   public static ChangeData getChangeData(TeamWorkFlowArtifact teamArt, ICommitConfigArtifact commitConfigArt) throws OseeCoreException {
      if (commitConfigArt != null && commitConfigArt.getParentBranch() == null) {
         throw new OseeArgumentException("Parent Branch not configured for [%s]", commitConfigArt);
      }
      Collection<Change> changes = new ArrayList<Change>();

      IOperation operation = null;
      if (AtsBranchManagerCore.isWorkingBranchInWork(teamArt)) {
         operation = ChangeManager.comparedToParent(AtsBranchManagerCore.getWorkingBranch(teamArt), changes);
         Operations.executeWorkAndCheckStatus(operation);
      } else {
         if (AtsBranchManagerCore.isCommittedBranchExists(teamArt)) {
            TransactionRecord transactionId = null;
            if (commitConfigArt == null) {
               transactionId = AtsBranchManagerCore.getEarliestTransactionId(teamArt);
            } else {
               Collection<TransactionRecord> transIds = AtsBranchManagerCore.getTransactionIds(teamArt, false);
               if (transIds.size() == 1) {
                  transactionId = transIds.iterator().next();
               } else {
                  /*
                   * First, attempt to compare the currently configured commitConfigArt parent branch with transaction
                   * id's branch.
                   */
                  for (TransactionRecord transId : transIds) {
                     if (transId.getBranch() == commitConfigArt.getParentBranch()) {
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

   public static void start() {
      instance = new AtsBranchManager();
   }

   public static void stop() {
      OseeEventManager.removeListener(instance);
   }

   public AtsBranchManager() {
      OseeEventManager.addListener(this);
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      try {
         Branch branch = BranchManager.getBranchByGuid(branchEvent.getBranchGuid());
         if (branch != null) {
            Artifact assocArtInCache =
               ArtifactCache.getActive(branch.getAssociatedArtifactId(), BranchManager.getCommonBranch());
            if (assocArtInCache != null && assocArtInCache instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) assocArtInCache;
               if (branchEvent.getEventType() == BranchEventType.Added) {
                  teamArt.setWorkingBranchCreationInProgress(false);
               } else if (branchEvent.getEventType() == BranchEventType.Committing) {
                  teamArt.setWorkingBranchCommitInProgress(true);
               } else if (branchEvent.getEventType() == BranchEventType.Committed || branchEvent.getEventType() == BranchEventType.CommitFailed) {
                  teamArt.setWorkingBranchCommitInProgress(false);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }
}