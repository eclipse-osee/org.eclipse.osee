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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ATSBranchMetrics;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.editor.IAtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.branch.BranchContentProvider;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
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

   private boolean commitPopup;
   private final SMAManager smaMgr;
   private final ATSBranchMetrics atsBranchMetrics;
   public static String BRANCH_CATEGORY = "Branch Changes";

   public AtsBranchManager(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
      atsBranchMetrics = new ATSBranchMetrics(this);
   }

   public void setAsDefaultBranch() {
      try {
         if (!isWorkingBranch()) {
            AWorkbench.popup("ERROR", "No Current Working Branch");
            return;
         }
         BranchPersistenceManager.getInstance().setDefaultBranch(getWorkingBranch());
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
            MergeView.openViewUpon(getWorkingBranch(), branch, TransactionIdManager.getInstance().getStartEndPoint(
                  getWorkingBranch()).getKey());
         } else {
            AWorkbench.popup("ERROR", "Showing Read-Only Merge View for Committed Branch\n\nNot Implemented Yet");
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

   public Integer getBranchId() throws SQLException, MultipleBranchesExist {
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
         Job job = BranchPersistenceManager.getInstance().deleteBranch(branch);
         job.join();

         AWorkbench.popup("Delete Complete", "Deleted Branch Successfully");

      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't delete change report.", ex, true);
      }
   }

   /**
    * @return TransactionId associated with this state machine artifact
    * @throws SQLException
    */
   public TransactionId getTransactionId() throws SQLException {
      Set<Integer> tranSet = RevisionManager.getInstance().getTransactionDataPerCommitArtifact(smaMgr.getSma());
      // Cache null transactionId so don't re-search for every call
      if (tranSet.size() == 0) {
         return null;
      } else if (tranSet.size() > 1) {
         OSEELog.logWarning(AtsPlugin.class,
               "Unexpected multiple transactions per committed artifact id " + smaMgr.getSma().getArtId(), false);
      }
      try {
         return TransactionIdManager.getInstance().getPossiblyEditableTransactionId(tranSet.iterator().next());
      } catch (Exception ex) {
         // there may be times where the transaction id cache is not up-to-date yet; don't throw error
      }
      return null;
   }

   /**
    * Display change report associated with the branch, if exists, or transaction, if branch has been committed.
    */
   public void showChangeReportOld() {
      try {
         if (isWorkingBranch()) {
            ChangeReportView.openViewUpon(getWorkingBranch());
         } else if (isCommittedBranch()) {
            ChangeReportView.openViewUpon(new ChangeReportInput(smaMgr.getSma().getDescriptiveName(),
                  getTransactionId()));
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
   public void showChangeReport() {
      try {
         if (isWorkingBranch()) {
            ChangeView.open(getWorkingBranch());
         } else if (isCommittedBranch()) {
            ChangeView.open(getTransactionId().getTransactionNumber());
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
    * @return
    * @throws SQLException
    */
   public Branch getWorkingBranch() throws SQLException, MultipleBranchesExist {
      Set<Branch> branches = BranchPersistenceManager.getInstance().getAssociatedArtifactBranches(smaMgr.getSma());
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
    * @throws SQLException
    */
   public boolean isWorkingBranch() throws SQLException, MultipleBranchesExist {
      return getWorkingBranch() != null;
   }

   /**
    * @return true if there are committed changes associated with this state machine artifact
    */
   public boolean isCommittedBranch() throws SQLException {
      return (getTransactionId() != null);
   }

   /**
    * Set parent branch id associated with this state machine artifact
    * 
    * @param branchId
    * @throws SQLException
    * @throws MultipleAttributesExist
    */
   public void setParentBranchId(int branchId) throws SQLException, MultipleAttributesExist {
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
         OSEELog.logException(AtsPlugin.class, ex, false);
         return new Result("Exception occurred: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   /**
    * @return Branch that is the configured branch to create working branch from.
    * @throws SQLException
    */
   private Branch getParentBranchForWorkingBranchCreation() throws SQLException, MultipleAttributesExist {
      Branch parentBranch = null;

      // Check for parent branch id in Version artifact
      if (smaMgr.isTeamUsesVersions()) {
         VersionArtifact verArt = smaMgr.getTargetedForVersion();
         if (verArt != null) {
            try {
               Integer branchId =
                     verArt.getSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(), 0);
               if (branchId != null && branchId > 0) {
                  parentBranch = BranchPersistenceManager.getInstance().getBranch(branchId);
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
               parentBranch = BranchPersistenceManager.getInstance().getBranch(branchId);
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
   public void createWorkingBranch(String pageId, Branch parentBranch) throws OseeCoreException, SQLException {
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
      final TransactionId parentTransactionId =
            TransactionIdManager.getInstance().getEditableTransactionId(parentBranch);

      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws OseeCoreException, SQLException {
            BranchPersistenceManager.getInstance().createWorkingBranch(parentTransactionId, finalBranchShortName,
                  branchName, stateMachineArtifact);
         }
      };

      Jobs.run("Create Branch", runnable, AtsPlugin.getLogger(), AtsPlugin.PLUGIN_ID);
   }

   public void updateBranchAccessControl() throws OseeCoreException, SQLException {
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

   /**
    * @param popup if true, popup errors associated with results
    * @return Result
    */
   public Result commitWorkingBranch(boolean popup) throws OseeCoreException, SQLException {
      return commitWorkingBranch(popup, false);
   }

   /**
    * @param popup if true, popup errors associated with results
    * @param overrideStateValidation if true, don't do checks to see if commit can be performed. This should only be
    *           used for developmental testing or automation
    * @return Result
    */
   public Result commitWorkingBranch(boolean popup, boolean overrideStateValidation) throws OseeCoreException, SQLException {
      commitPopup = popup;

      Branch branch = getWorkingBranch();
      if (branch == null) {
         OSEELog.logSevere(AtsPlugin.class,
               "Commit Branch Failed: Can not locate branch for workflow " + smaMgr.getSma().getHumanReadableId(),
               popup);
         return new Result("Commit Branch Failed: Can not locate branch.");
      }

      // If team uses versions, then validate that the parent branch id is specified by either
      // the team definition's attribute or the related version's attribute
      if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact team = (TeamWorkFlowArtifact) smaMgr.getSma();
         // Only perform checks if team definition uses ATS versions
         if (team.getTeamDefinition().isTeamUsesVersions()) {
            // Validate that a parent branch is specified in ATS configuration
            Branch parentBranch = getParentBranchForWorkingBranchCreation();
            if (parentBranch == null) {
               return new Result(
                     String.format(
                           "Commit Branch Failed: Workflow \"%s\" can't access parent branch to commit to.\n\nSince the configured Team Definition uses versions, the parent branch must be specified in either the targeted Version Artifact or the Team Definition Artifact.",
                           smaMgr.getSma().getHumanReadableId()));
            }

            // Validate that the configured parentBranch is the same as the working branch's
            // parent branch.
            Integer targetedVersionBranchId = parentBranch.getBranchId();
            Integer workflowWorkingBranchParentBranchId = smaMgr.getBranchMgr().getWorkingBranch().getParentBranchId();
            if (!targetedVersionBranchId.equals(workflowWorkingBranchParentBranchId)) {
               return new Result(
                     String.format(
                           "Commit Branch Failed: Workflow \"%s\" targeted version \"%s\" branch id \"%s\" does not match branch's " + "parent branch id \"%s\"",
                           smaMgr.getSma().getHumanReadableId(), team.getTargetedForVersion().getDescriptiveName(),
                           String.valueOf(targetedVersionBranchId), String.valueOf(workflowWorkingBranchParentBranchId)));
            }
         }
      }

      if (!overrideStateValidation) {
         // Check extenstion points for valid commit
         for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(smaMgr.getWorkPageDefinition().getId())) {
            Result result = item.committing(smaMgr);
            if (result.isFalse()) return result;
         }
      }
      ConflictManager conflictManager = new ConflictManager(branch.getParentBranch(), branch);
      if (!popup) {
         BranchPersistenceManager.getInstance().commitBranch(branch, true, true);
      } else if (conflictManager.getRemainingConflicts().size() > 0) {

         MessageDialog dialog;
         if (OseeProperties.isDeveloper()) {
            dialog =
                  new MessageDialog(
                        Display.getCurrent().getActiveShell(),
                        "Commit Failed",
                        null,
                        "Commit Failed Due To Unresolved Conflicts\n\nPossible Resolutions:\n  Cancel commit and resolve at a later time\n  Launch the Merge Manger to resolve conflicts\n  Force the commit",
                        MessageDialog.QUESTION, new String[] {"Cancel", "Launch Merge Manager", "Force Commit"}, 0);
         } else {
            dialog =
                  new MessageDialog(
                        Display.getCurrent().getActiveShell(),
                        "Commit Failed",
                        null,
                        "Commit Failed Due To Unresolved Conflicts\n\nPossible Resolutions:\n  Cancel commit and resolve at a later time\n  Launch the Merge Manger to resolve conflicts",
                        MessageDialog.QUESTION, new String[] {"Cancel", "Launch Merge Manager"}, 0);

         }

         int result = dialog.open();
         if (commitPopup && result == 1) {
            MergeView.openViewUpon(branch, branch.getParentBranch(),
                  TransactionIdManager.getInstance().getStartEndPoint(branch).getKey());
         } else if (result == 2) {
            BranchPersistenceManager.getInstance().commitBranch(branch, true, true);
         }
      } else {
         StringBuffer sb =
               new StringBuffer(
                     "Commit branch\n\n\"" + branch + "\"\n\nto parent branch\n\n\"" + conflictManager.getToBranch() + "\"\n");
         if (conflictManager.getOriginalConflicts().size() > 0) {
            sb.append("\nwith " + conflictManager.getOriginalConflicts().size() + " conflicts resolved.\n");
         } else {
            sb.append("\n(no conflicts found)\n");
         }
         sb.append("\nCommit?");
         MessageDialog dialog =
               new MessageDialog(Display.getCurrent().getActiveShell(), "Commit Branch", null, sb.toString(),
                     MessageDialog.QUESTION, new String[] {"Ok", "Launch Merge Manager", "Cancel"}, 0);
         int result = dialog.open();
         if (result == 0) {
            BranchPersistenceManager.getInstance().commitBranch(branch, true, true);
         } else if (result == 1) {
            MergeView.openViewUpon(branch, branch.getParentBranch(),
                  TransactionIdManager.getInstance().getStartEndPoint(branch).getKey());
         }
      }

      return Result.TrueResult;
   }

   public ArtifactChange getArtifactChange(String artifactName) throws OseeCoreException, SQLException {
      for (ArtifactChange artChange : getArtifactChanges()) {
         if (artChange.getName().equals(artifactName)) return artChange;
      }
      return null;
   }

   public Collection<ArtifactChange> getArtifactChanges() throws OseeCoreException, SQLException {
      ArrayList<ArtifactChange> artChanges = new ArrayList<ArtifactChange>();
      if (smaMgr.getBranchMgr().isWorkingBranch()) {
         Branch workingBranch = smaMgr.getBranchMgr().getWorkingBranch();
         if (workingBranch != null) {
            try {
               for (Object obj : BranchContentProvider.getArtifactChanges(new ChangeReportInput(workingBranch))) {
                  if (obj instanceof ArtifactChange) artChanges.add((ArtifactChange) obj);
               }
            } catch (SQLException ex) {
               OSEELog.logSevere(AtsPlugin.class,
                     "Error getting branch artifact changes - " + ex.getLocalizedMessage(), true);
            }
         }
      } else if (smaMgr.getBranchMgr().isCommittedBranch()) {
         TransactionId transactionId = getTransactionId();
         if (transactionId != null) {
            try {
               for (Object obj : BranchContentProvider.getArtifactChanges(new ChangeReportInput("", transactionId))) {
                  if (obj instanceof ArtifactChange) artChanges.add((ArtifactChange) obj);
               }
            } catch (SQLException ex) {
               OSEELog.logSevere(AtsPlugin.class,
                     "Error getting transaction artifact changes - " + ex.getLocalizedMessage(), true);
            }
         }
      }
      return artChanges;
   }

   /**
    * Return the artifacts modified via transaction of branch commit during implementation state. This includes
    * artifacts that only had relation changes. NOTE: The returned artifacts are the old versions at the time of the
    * commit. They can't be used for editing or relating. NOTE: This is a VERY expensive operation as each artifact must
    * be loaded. Retrieving data through change report snapshot is cheaper.
    * 
    * @return artifacts modified
    */
   public Collection<Artifact> getArtifactsModified(boolean includeRelationOnlyChanges) {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      try {
         if (isWorkingBranch() && !isChangesOnWorkingBranch()) return arts;
         if (smaMgr.getBranchMgr().isWorkingBranch()) {
            try {
               Branch workingBranch = smaMgr.getBranchMgr().getWorkingBranch();
               return RevisionManager.getInstance().getNewAndModifiedArtifacts(workingBranch,
                     includeRelationOnlyChanges);
            } catch (SQLException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         } else if (smaMgr.getBranchMgr().isCommittedBranch()) {
            TransactionId transactionId = getTransactionId();
            if (transactionId != null) {

               return RevisionManager.getInstance().getNewAndModifiedArtifacts(transactionId, transactionId,
                     includeRelationOnlyChanges);

            }
         }
      } catch (Exception ex) {
         OSEELog.logSevere(AtsPlugin.class, "Error getting modified artifacts", true);
      }
      return arts;
   }

   /**
    * Return All artifacts who had a relation change. This includes relation only artifacts and those who had other
    * attribute changes.
    * 
    * @return artifacts
    */
   public Collection<Artifact> getArtifactsRelChanged() throws SQLException {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      try {
         if (isWorkingBranch() && !isChangesOnWorkingBranch()) return arts;
         TransactionId transactionId = getTransactionId();
         if (transactionId == null) return arts;

         return RevisionManager.getInstance().getRelationChangedArtifacts(transactionId, transactionId);
      } catch (Exception ex) {
         OSEELog.logSevere(AtsPlugin.class, "Error getting relation changed artifacts", true);
      }
      return arts;
   }

   /**
    * @return true if isWorkingBranch() and changes exist else false
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   public Boolean isChangesOnWorkingBranch() throws SQLException, MultipleBranchesExist, BranchDoesNotExist, TransactionDoesNotExist {
      if (isWorkingBranch()) {
         Pair<TransactionId, TransactionId> transactionToFrom =
               TransactionIdManager.getInstance().getStartEndPoint(getWorkingBranch());
         if (transactionToFrom.getKey().equals(transactionToFrom.getValue())) {
            return false;
         }
         return true;
      }
      return false;
   }

   /**
    * Since deleted artifacts don't exist, this method will return the artifact object just prior to it's deletion.
    * NOTE: This is a VERY expensive operation as each artifact must be loaded. Retrieving data through change report
    * snapshot is cheaper.
    * 
    * @return artifacts that were deleted
    */
   public Collection<Artifact> getArtifactsDeleted() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      try {
         if (isWorkingBranch() && !isChangesOnWorkingBranch()) return arts;
         TransactionId transactionId = getTransactionId();
         if (transactionId == null) return arts;

         for (ArtifactChange artChange : RevisionManager.getInstance().getDeletedArtifactChanges(transactionId)) {
            if (artChange.getModType() == ModificationType.DELETED) {
               arts.add(artChange.getArtifact());
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class,
               "Error getting deleted artifacts " + smaMgr.getSma().getHumanReadableId(), ex, true);
      }
      return arts;
   }

   /**
    * @return the atsBranchMetrics
    */
   public ATSBranchMetrics getAtsBranchMetrics(boolean cache) throws OseeCoreException, SQLException {
      if (cache) atsBranchMetrics.persist();
      return atsBranchMetrics;
   }

   /**
    * @return the smaMgr
    */
   public SMAManager getSmaMgr() {
      return smaMgr;
   }

}