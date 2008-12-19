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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.editor.IAtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.AtsAddPeerToPeerReviewRule;
import org.eclipse.osee.ats.workflow.item.StateEventType;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.branch.CommitHandler;
import org.eclipse.osee.framework.ui.skynet.dialogs.ListDialogSortable;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.TransactionIdLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.osee.framework.ui.skynet.widgets.xcommit.CommitManagerView;
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
   public static String BRANCH_CATEGORY = "Branch Changes";

   public AtsBranchManager(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   public void setAsDefaultBranch() {
      try {
         if (!isWorkingBranch()) {
            AWorkbench.popup("ERROR", "No Current Working Branch");
            return;
         }
         BranchManager.setDefaultBranch(getWorkingBranch());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public void showMergeManager() {
      try {
         if (!isWorkingBranch() && !isCommittedBranch()) {
            AWorkbench.popup("ERROR", "No Current Working or Committed Branch");
            return;
         }
         if (isWorkingBranch()) {
            Branch branch = getParentBranchForWorkingBranchCreation();
            if (branch == null) {
               AWorkbench.popup("ERROR", "Can't access parent branch");
               return;
            }
            MergeView.openView(getWorkingBranch(), branch,
                  TransactionIdManager.getStartEndPoint(getWorkingBranch()).getKey());

         } else if (isCommittedBranch()) {
            TransactionId transactionId = getTransactionIdOrPopupChoose();
            if (transactionId == null) return;
            MergeView.openView(transactionId);
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /**
    * Opens the branch currently associated with this state machine artifact.
    */
   public void showWorkingBranch() {
      try {
         if (!isWorkingBranch()) {
            AWorkbench.popup("ERROR", "No Current Working Branch");
            return;
         }
         BranchView.revealBranch(getWorkingBranch());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public Integer getBranchId() throws OseeCoreException {
      if (getWorkingBranch() == null) return null;
      return getWorkingBranch().getBranchId();
   }

   /**
    * If working branch has no changes, allow for deletion.
    */
   public void deleteEmptyWorkingBranch() {
      try {
         Branch branch = getWorkingBranch();
         if (branch.hasChanges()) {
            if (!MessageDialog.openQuestion(
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Delete Branch with Changes",
                  "Warning: Changes have been made on this branch.\n\nAre you sure you want to delete the branch: " + branch)) return;
         } else if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Delete Branch", "Are you sure you want to delete the branch: " + branch)) {
         }
         Job job = BranchManager.deleteBranch(branch);
         job.join();

         AWorkbench.popup("Delete Complete", "Deleted Branch Successfully");

      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Problem deleting branch.", ex, true);
      }
   }

   /**
    * @return TransactionId associated with this state machine artifact
    */
   public Collection<TransactionId> getTransactionIds() throws OseeCoreException {
      List<TransactionId> transactionIds = new ArrayList<TransactionId>();
      try {
         for (Integer transIdInt : RevisionManager.getInstance().getTransactionDataPerCommitArtifact(smaMgr.getSma())) {
            transactionIds.add(TransactionIdManager.getTransactionId(transIdInt));
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         // there may be times where the transaction id cache is not up-to-date yet; don't throw error
      }
      return transactionIds;
   }

   public TransactionId getEarliestTransactionId() throws OseeCoreException {
      Collection<TransactionId> transactionIds = getTransactionIds();
      if (transactionIds.size() == 1) return transactionIds.iterator().next();
      TransactionId earliestTransactionId = transactionIds.iterator().next();
      for (TransactionId transactionId : transactionIds) {
         if (transactionId.getTransactionNumber() < earliestTransactionId.getTransactionNumber()) {
            earliestTransactionId = transactionId;
         }
      }
      return earliestTransactionId;
   }

   public TransactionId getTransactionIdOrPopupChoose() throws OseeCoreException {
      Collection<TransactionId> transactionIds = getTransactionIds();
      if (transactionIds.size() == 1) {
         return transactionIds.iterator().next();
      }
      ListDialogSortable ld = new ListDialogSortable(Display.getCurrent().getActiveShell());
      ld.setContentProvider(new ArrayContentProvider());
      ld.setLabelProvider(new TransactionIdLabelProvider());
      ld.setSorter(new ViewerSorter() {
         /* (non-Javadoc)
          * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
          */
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            if (((TransactionId) e1).getTransactionNumber() < ((TransactionId) e2).getTransactionNumber()) {
               return -1;
            } else if (((TransactionId) e1).getTransactionNumber() > ((TransactionId) e2).getTransactionNumber()) {
               return 1;
            }
            return 0;
         }
      });
      ld.setTitle("Select Transaction");
      ld.setMessage("Select Transaction");
      ld.setInput(transactionIds);
      if (ld.open() == 0) {
         return (TransactionId) ld.getResult()[0];
      }
      return null;
   }

   /**
    * Display change report associated with the branch, if exists, or transaction, if branch has been committed.
    */
   public void showChangeReport() {
      try {
         if (isWorkingBranch()) {
            ChangeView.open(getWorkingBranch());
         } else if (isCommittedBranch()) {
            TransactionId transactionId = getTransactionIdOrPopupChoose();
            if (transactionId == null) return;
            ChangeView.open(transactionId);
         } else {
            AWorkbench.popup("ERROR", "No Branch or Committed Transaction Found.");
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't show change report.", ex, true);
      }
   }

   /**
    * Display change report associated with the branch, if exists, or transaction, if branch has been committed.
    */
   public void showCommitManager() {
      try {
         if (smaMgr.getBranchMgr().getWorkingBranch() == null) {
            AWorkbench.popup("ERROR", "No working branch");
         } else if (!(smaMgr.getSma() instanceof IBranchArtifact)) {
            AWorkbench.popup("ERROR", "Not IBranchArtifact");
         } else
            CommitManagerView.openViewUpon((IBranchArtifact) smaMgr.getSma());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /**
    * Return working branch associated with SMA; This data is cached across all workflows with the cache being updated
    * by local and remote events.
    * 
    * @return Branch
    */
   public Branch getWorkingBranch() throws OseeCoreException {
      Set<Branch> branches = BranchManager.getAssociatedArtifactBranches(smaMgr.getSma());
      if (branches.size() == 0) {
         return null;
      } else if (branches.size() > 1) {
         throw new MultipleBranchesExist(
               "Unexpected multiple associated working branches found for workflow " + smaMgr.getSma().getHumanReadableId());
      } else {
         return branches.iterator().next();
      }
   }

   /**
    * @return true if there is a current working branch
    */
   public boolean isWorkingBranch() throws OseeCoreException {
      return getWorkingBranch() != null;
   }

   /**
    * @return true if there are committed changes associated with this state machine artifact
    */
   public boolean isCommittedBranch() throws OseeCoreException {
      return (getTransactionIds().size() > 0);
   }

   /**
    * Set parent branch id associated with this state machine artifact
    * 
    * @param branchId
    * @throws MultipleAttributesExist
    */
   public void setParentBranchId(int branchId) throws OseeCoreException {
      smaMgr.getSma().setSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(),
            String.valueOf(branchId));
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
         if (isCommittedBranch()) {
            if (popup) AWorkbench.popup("ERROR",
                  "Can not create another working branch once changes have been committed.");
            return new Result("Committed branch already exists.");
         }
         Branch parentBranch = getParentBranchForWorkingBranchCreation();
         if (parentBranch == null) {
            String errorStr =
                  "Parent Branch can not be determined.\n\nPlease specify " + "parent branch through Version Artifact or Team Definition Artifact.\n\n" + "Contact your team lead to configure this.";
            if (popup) AWorkbench.popup("ERROR", errorStr);
            return new Result(errorStr);
         }
         // Retrieve parent branch to create working branch from
         if (popup && !MessageDialog.openConfirm(
               Display.getCurrent().getActiveShell(),
               "Create Working Branch",
               "Create a working branch from parent branch\n\n\"" + parentBranch.getBranchName() + "\"?\n\n" + "NOTE: Working branches are necessary when OSEE Artifact changes " + "are made during implementation.")) return Result.FalseResult;
         createWorkingBranch(pageId, parentBranch);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result("Exception occurred: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   private void createNecessaryBranchEventReviews(StateEventType stateEventType, SMAManager smaMgr, SkynetTransaction transaction) throws OseeCoreException {
      if (stateEventType != StateEventType.CommitBranch && stateEventType != StateEventType.CreateBranch) {
         throw new OseeStateException("Invalid stateEventType = " + stateEventType);
      }
      // Create any decision and peerToPeer reviews for createBranch and commitBranch
      for (String ruleId : Arrays.asList(AtsAddDecisionReviewRule.ID, AtsAddPeerToPeerReviewRule.ID)) {
         for (WorkRuleDefinition workRuleDef : smaMgr.getWorkRulesStartsWith(ruleId)) {
            StateEventType eventType = AtsAddDecisionReviewRule.getStateEventType(smaMgr, workRuleDef);
            if (eventType != null && eventType == stateEventType) {
               if (ruleId.equals(AtsAddDecisionReviewRule.ID)) {
                  DecisionReviewArtifact decArt = AtsAddDecisionReviewRule.createNewDecisionReview(workRuleDef, smaMgr);
                  if (decArt != null) decArt.persistAttributesAndRelations(transaction);
               } else if (ruleId.equals(AtsAddPeerToPeerReviewRule.ID)) {
                  PeerToPeerReviewArtifact peerArt =
                        AtsAddPeerToPeerReviewRule.createNewPeerToPeerReview(workRuleDef, smaMgr, transaction);
                  if (peerArt != null) peerArt.persistAttributesAndRelations(transaction);
               }
            }
         }
      }
   }

   /**
    * @return Branch that is the configured branch to create working branch from.
    */
   private Branch getParentBranchForWorkingBranchCreation() throws OseeCoreException {
      Branch parentBranch = null;

      // Check for parent branch id in Version artifact
      if (smaMgr.isTeamUsesVersions()) {
         VersionArtifact verArt = smaMgr.getTargetedForVersion();
         if (verArt != null) {
            try {
               Integer branchId =
                     verArt.getSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(), 0);
               if (branchId != null && branchId > 0) {
                  parentBranch = BranchManager.getBranch(branchId);
               }
            } catch (BranchDoesNotExist ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      }

      // If not defined in version, check for parent branch from team definition
      if (parentBranch == null && (smaMgr.getSma() instanceof TeamWorkFlowArtifact)) {
         try {
            Integer branchId =
                  ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getSoleAttributeValue(
                        ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName());
            if (branchId != null && branchId > 0) {
               parentBranch = BranchManager.getBranch(branchId);
            }
         } catch (OseeCoreException ex) {
            // do nothing
         }
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
   public void createWorkingBranch(String pageId, final Branch parentBranch) throws OseeCoreException {
      final Artifact stateMachineArtifact = smaMgr.getSma();
      String title = stateMachineArtifact.getDescriptiveName();
      if (title.length() > 40) title = title.substring(0, 39) + "...";
      final String branchName =
            String.format("%s - %s - %s", stateMachineArtifact.getHumanReadableId(),
                  stateMachineArtifact.getDescriptiveName(), title);
      String branchShortName = "";
      if (pageId != null && !pageId.equals("")) {
         List<IAtsStateItem> stateItems = smaMgr.getStateItems().getStateItems(pageId);
         if (stateItems.size() > 0) {
            branchShortName = (stateItems.iterator().next().getBranchShortName(smaMgr));
         }
      }
      final String finalBranchShortName = branchShortName;

      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws OseeCoreException {
            BranchManager.createWorkingBranch(parentBranch, finalBranchShortName, branchName, stateMachineArtifact);
            // Create reviews as necessary
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            createNecessaryBranchEventReviews(StateEventType.CreateBranch, smaMgr, transaction);
            transaction.execute();
         }
      };

      Jobs.run("Create Branch", runnable, AtsPlugin.class, AtsPlugin.PLUGIN_ID);
   }

   public void updateBranchAccessControl() throws OseeCoreException {
      // Only set/update branch access control if state item is configured to accept
      for (IAtsStateItem stateItem : smaMgr.getStateItems().getCurrentPageStateItems(smaMgr)) {
         if (stateItem.isAccessControlViaAssigneesEnabledForBranching()) {
            Branch branch = getWorkingBranch();
            if (branch != null) {
               for (AccessControlData acd : AccessControlManager.getInstance().getAccessControlList(branch)) {
                  // If subject is NOT an assignee, remove access control
                  if (!smaMgr.getStateMgr().getAssignees().contains(acd.getSubject())) {
                     AccessControlManager.getInstance().removeAccessControlData(acd);
                  }
               }
               // If subject doesn't have access, add it
               for (User user : smaMgr.getStateMgr().getAssignees())
                  AccessControlManager.getInstance().setPermission(user, branch, PermissionEnum.FULLACCESS);
            }
         }
      }
   }

   private final class AtsCommitJob extends Job {
      private final boolean commitPopup;
      private final boolean overrideStateValidation;

      /**
       * @param name
       * @param commitPopup
       * @param overrideStateValidation
       */
      public AtsCommitJob(boolean commitPopup, boolean overrideStateValidation) {
         super("Commit Branch");
         this.commitPopup = commitPopup;
         this.overrideStateValidation = overrideStateValidation;
      }

      /* (non-Javadoc)
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            Branch branch = getWorkingBranch();
            if (branch == null) {
               return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID,
                     "Commit Branch Failed: Can not locate branch for workflow " + smaMgr.getSma().getHumanReadableId());
            }

            // If team uses versions, then validate that the parent branch id is specified by either
            // the team definition's attribute or the related version's attribute
            if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
               // Only perform checks if team definition uses ATS versions
               TeamWorkFlowArtifact team = (TeamWorkFlowArtifact) smaMgr.getSma();
               if (team.getTeamDefinition().isTeamUsesVersions()) {

                  // Confirm that team is targeted for version
                  if (team.getWorldViewTargetedVersion() == null) {
                     return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, String.format(
                           "Commit Branch Failed: Workflow \"%s\" must be targeted for a version.",
                           smaMgr.getSma().getHumanReadableId()));
                  }

                  // Validate that a parent branch is specified in ATS configuration
                  Branch parentBranch = getParentBranchForWorkingBranchCreation();
                  if (parentBranch == null) {
                     return new Status(
                           Status.ERROR,
                           AtsPlugin.PLUGIN_ID,
                           String.format(
                                 "Commit Branch Failed: Workflow \"%s\" can't access parent branch to commit to.\n\nSince the configured Team Definition uses versions, the parent branch must be specified in either the targeted Version Artifact or the Team Definition Artifact.",
                                 smaMgr.getSma().getHumanReadableId()));
                  }

                  // Validate that the configured parentBranch is the same as the working branch's
                  // parent branch.
                  Integer targetedVersionBranchId = parentBranch.getBranchId();
                  Integer workflowWorkingBranchParentBranchId =
                        smaMgr.getBranchMgr().getWorkingBranch().getParentBranchId();
                  if (!targetedVersionBranchId.equals(workflowWorkingBranchParentBranchId)) {
                     return new Status(
                           Status.ERROR,
                           AtsPlugin.PLUGIN_ID,
                           String.format(
                                 "Commit Branch Failed: Workflow \"%s\" targeted version \"%s\" branch id \"%s\" does not match branch's " + "parent branch id \"%s\"",
                                 smaMgr.getSma().getHumanReadableId(),
                                 team.getWorldViewTargetedVersion().getDescriptiveName(),
                                 String.valueOf(targetedVersionBranchId),
                                 String.valueOf(workflowWorkingBranchParentBranchId)));
                  }
               }
            }

            // Confirm that all blocking reviews are completed
            // Loop through this state's blocking reviews to confirm complete
            for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviewsFromCurrentState()) {
               if (reviewArt.getReviewBlockType() == ReviewBlockType.Commit && !reviewArt.getSmaMgr().isCancelledOrCompleted()) {
                  return new Status(
                        Status.ERROR,
                        AtsPlugin.PLUGIN_ID,
                        "Blocking Review must be completed before commit.\n\nReview Title: \"" + reviewArt.getDescriptiveName() + "\"\nHRID: " + reviewArt.getHumanReadableId());
               }
            }

            if (!overrideStateValidation) {
               // Check extenstion points for valid commit
               for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(smaMgr.getWorkPageDefinition().getId())) {
                  Result tempResult = item.committing(smaMgr);
                  if (tempResult.isFalse()) {
                     return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, tempResult.getText());
                  }
               }
            }

            commit(commitPopup, branch);
         } catch (OseeCoreException ex) {
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, ex.getLocalizedMessage(), ex);
         }
         return Status.OK_STATUS;
      }

      private void commit(boolean commitPopup, Branch branch) throws OseeCoreException {
         boolean branchCommitted = false;
         ConflictManagerExternal conflictManager = new ConflictManagerExternal(branch.getParentBranch(), branch);

         if (commitPopup) {
            branchCommitted = CommitHandler.commitBranch(conflictManager, true);
         } else {
            BranchManager.commitBranch(conflictManager, true, true);
            branchCommitted = true;
         }
         if (branchCommitted) {
            // Create reviews as necessary
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            createNecessaryBranchEventReviews(StateEventType.CommitBranch, smaMgr, transaction);
            transaction.execute();
         }
      }
   }

   public boolean isBranchInCommit() throws OseeCoreException {
      if (!isWorkingBranch()) return false;
      return BranchManager.isBranchInCommit(getWorkingBranch());
   }

   /**
    * @param commitPopup if true, popup errors associated with results
    * @param overrideStateValidation if true, don't do checks to see if commit can be performed. This should only be
    *           used for developmental testing or automation
    */
   public void commitWorkingBranch(final boolean commitPopup, final boolean overrideStateValidation) throws OseeCoreException {
      if (isBranchInCommit()) {
         throw new OseeCoreException("Branch is currently being committed.");
      }
      Jobs.startJob(new AtsCommitJob(commitPopup, overrideStateValidation));
   }

   /**
    * Since change data for a committed branch is not going to change, cache it per run instance of OSEE
    */
   private static final Map<TransactionId, ChangeData> changeDataCacheForCommittedBranch =
         new HashMap<TransactionId, ChangeData>();

   public ChangeData getChangeData() throws OseeCoreException {
      ChangeData changeData = null;
      if (smaMgr.getBranchMgr().isWorkingBranch()) {
         changeData = ChangeManager.getChangeDataPerBranch(getWorkingBranch(), null);
      } else if (smaMgr.getBranchMgr().isCommittedBranch()) {
         TransactionId transactionId = getEarliestTransactionId();
         if (changeDataCacheForCommittedBranch.get(transactionId) == null) {
            changeDataCacheForCommittedBranch.put(transactionId, ChangeManager.getChangeDataPerTransaction(
                  transactionId, null));
         }
         changeData = changeDataCacheForCommittedBranch.get(transactionId);
      } else {
         changeData = new ChangeData(new ArrayList<Change>());
      }
      return changeData;
   }

   /**
    * @return true if isWorkingBranch() and changes exist else false
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   public Boolean isChangesOnWorkingBranch() throws OseeCoreException {
      if (isWorkingBranch()) {
         return ChangeManager.isChangesOnWorkingBranch(getWorkingBranch());
      }
      return false;
   }

   /**
    * @return the smaMgr
    */
   public SMAManager getSmaMgr() {
      return smaMgr;
   }

}