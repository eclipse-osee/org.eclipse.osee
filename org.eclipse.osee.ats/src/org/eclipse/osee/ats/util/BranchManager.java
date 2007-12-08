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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.editor.IAtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.skynet.core.IActionBranchStateChange;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactInTransactionSearch;
import org.eclipse.osee.framework.skynet.core.event.AtsBranchCommittedEvent;
import org.eclipse.osee.framework.skynet.core.event.AtsBranchCreatedEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * BranchManager contains methods necessary for ATS objects to interact with creation, view and commit of branches.
 * 
 * @author Donald G. Dunne
 */
public class BranchManager implements IActionBranchStateChange {

   private boolean commitPopup;
   private final SMAManager smaMgr;

   public BranchManager(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   /**
    * Opens the branch currently associated with this state machine artifact.
    */
   public void showWorkingBranch() {
      if (!isWorkingBranch()) {
         AWorkbench.popup("ERROR", "No Current Working Branch");
         return;
      }
      try {
         BranchView.revealBranch(getBranch());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /**
    * If working branch has no changes, allow for deletion.
    */
   public void deleteEmptyWorkingBranch() {
      try {
         Branch branch = getBranch();
         if (branch.hasChanges()) {
            AWorkbench.popup("ERROR", "Working branch has changes.  Can't delete through Action.");
            return;
         }
         if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Delete Branch", "Are you sure you want to delete the branch: " + branch)) {
            BranchPersistenceManager.getInstance().deleteBranch(branch);

            clearBranchId();
            smaMgr.getSma().saveSMA();
            AWorkbench.popup("Delete Complete", "Deleted Branch Successfully");
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't delete change report.", ex, true);
      }

   }

   /**
    * @return TransactionId associated with this state machine artifact
    * @throws SQLException
    */
   public TransactionId getTransactionId() throws SQLException {
      if ((getTransactionIdInt() != null) && (getTransactionIdInt() != 0)) return TransactionIdManager.getInstance().getPossiblyEditableTransactionId(
            getTransactionIdInt());
      return null;

   }

   /**
    * Display change report associated with the branch, if exists, or transaction, if branch has been committed.
    */
   public void showChangeReport() {
      try {
         if (isWorkingBranch()) {
            ChangeReportView.openViewUpon(getBranch());
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
    * @return branch id or null if there is no stored branch id
    */
   public Integer getBranchId() {
      return smaMgr.getSma().getSoleIntegerAttributeValue(ATSAttributes.BRANCH_ID_ATTRIBUTE.getStoreName());
   }

   /**
    * @return true if there is a current working branch
    */
   public boolean isWorkingBranch() {
      Integer id = smaMgr.getBranchMgr().getBranchId();
      if (id != null && id > 0) return true;
      return false;
   }

   /**
    * @return true if there are committed changes associated with this state machine artifact
    */
   public boolean isCommittedBranch() {
      Integer id = smaMgr.getBranchMgr().getTransactionIdInt();
      if (id != null && id > 0) return true;
      return false;
   }

   /**
    * Set the current branch id to be associated with this state machine artifact
    * 
    * @param branchId
    * @throws SQLException
    * @throws IllegalStateException
    */
   public void setBranchId(int branchId) throws IllegalStateException, SQLException {
      smaMgr.getSma().setSoleAttributeValue(ATSAttributes.BRANCH_ID_ATTRIBUTE.getStoreName(), String.valueOf(branchId));
   }

   /**
    * Set parent branch id associated with this state machine artifact
    * 
    * @param branchId
    * @throws SQLException
    * @throws IllegalStateException
    */
   public void setParentBranchId(int branchId) throws IllegalStateException, SQLException {
      smaMgr.getSma().setSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(),
            String.valueOf(branchId));
   }

   /**
    * @return integer transaction id associated with this state machine artifact
    */
   public Integer getTransactionIdInt() {
      return smaMgr.getSma().getSoleIntegerAttributeValue(ATSAttributes.TRANSACTION_ID_ATTRIBUTE.getStoreName());
   }

   /**
    * Set transaction id associated with this state machine artifact
    * 
    * @param id
    * @throws SQLException
    * @throws IllegalStateException
    */
   public void setTransactionId(int id) throws IllegalStateException, SQLException {
      smaMgr.getSma().setSoleAttributeValue(ATSAttributes.TRANSACTION_ID_ATTRIBUTE.getStoreName(), String.valueOf(id));
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
         if (getTransactionIdInt() != null && getTransactionIdInt() != 0) {
            if (popup) AWorkbench.popup("ERROR",
                  "Can not create another working branch once changes have been committed.");
            return new Result("Committed branch already exists.");
         }
         Branch parentBranch = getParentBranchForWorkingBranchCreation();
         if (parentBranch == null) {
            String errorStr = "Parent Branch can not be determined.\n\nPlease specify " + "parent branch through Version Artifact or Team Definition Artifact.\n\n" + "Contact your team lead to configure this.";
            if (popup) AWorkbench.popup("ERROR", errorStr);
            return new Result(errorStr);
         }
         // Retrieve parent branch to create working branch from
         if (popup && !MessageDialog.openConfirm(
               Display.getCurrent().getActiveShell(),
               "Create Working Branch",
               "Create a working branch from parent branch\n\n\"" + parentBranch.getBranchName() + "\"?\n\n" + "NOTE: Working branches are necessary when OSEE Artifact changes " + "are made during implementation.")) return Result.FalseResult;
         createWorkingBranch(pageId, parentBranch);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return new Result("Exception occurred: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   /**
    * @return Branch that is the configured branch to create working branch from.
    * @throws SQLException
    */
   private Branch getParentBranchForWorkingBranchCreation() throws SQLException {
      Branch parentBranch = null;

      // Check for parent branch id in Version artifact
      if (smaMgr.isTeamUsesVersions()) {
         VersionArtifact verArt = smaMgr.getTargetedForVersion();
         if (verArt != null) {
            Integer branchId = verArt.getSoleIntegerAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName());
            if (branchId != null && branchId > 0) {
               parentBranch = BranchPersistenceManager.getInstance().getBranch(branchId);
            }
         }
      }

      // If not defined in version, check for parent branch from team definition
      if (parentBranch == null && (smaMgr.getSma() instanceof TeamWorkFlowArtifact)) {
         Integer branchId = ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getSoleIntegerAttributeValue(
               ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName());
         if (branchId != null && branchId > 0) {
            parentBranch = BranchPersistenceManager.getInstance().getBranch(branchId);
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
    * @throws SQLException
    */
   public void createWorkingBranch(String pageId, Branch parentBranch) throws SQLException {
      final Artifact stateMachineArtifact = smaMgr.getSma();
      final IActionBranchStateChange callback = this;
      String title = stateMachineArtifact.getDescriptiveName();
      if (title.length() > 40) title = title.substring(0, 39) + "...";
      final String branchName = String.format("%s - %s - %s", stateMachineArtifact.getHumanReadableId(),
            stateMachineArtifact.getDescriptiveName(), title);
      String branchShortName = "";
      if (pageId != null && !pageId.equals("")) {
         List<IAtsStateItem> stateItems = smaMgr.getStateItems().getStateItems(pageId);
         if (stateItems.size() > 0) {
            branchShortName = (stateItems.iterator().next().getBranchShortName(smaMgr));
         }
      }
      final String finalBranchShortName = branchShortName;
      final TransactionId parentTransactionId = TransactionIdManager.getInstance().getEditableTransactionId(
            parentBranch);

      IExceptionableRunnable runnable = new IExceptionableRunnable() {
         public void run(IProgressMonitor monitor) throws Exception {
            BranchPersistenceManager.getInstance().createWorkingBranch(parentTransactionId, finalBranchShortName,
                  branchName, callback, stateMachineArtifact);
         }
      };

      Jobs.run("Create Branch", runnable, AtsPlugin.getLogger(), AtsPlugin.PLUGIN_ID);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.IActionBranchStateChange#branchCommited()
    */
   public void branchCommited(int transactionNumber) {

      if (transactionNumber == 0) {
         OSEELog.logSevere(AtsPlugin.class, "Commit Branch Failed: Commit returned a 0 transId for branchId ",
               commitPopup);
         return;
      }

      try {
         clearBranchId();
         setTransactionId(transactionNumber);
      } catch (IllegalStateException ex) {
         OSEELog.logException(AtsPlugin.class, ex, commitPopup);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, commitPopup);
      }
      smaMgr.getSma().saveSMA();

      SkynetEventManager.getInstance().kick(new AtsBranchCommittedEvent(this));

      // Notify extenstion points of commit
      for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(smaMgr.getWorkPage().getId())) {
         item.committed(smaMgr);
      }
      synchronized (smaMgr.getBranchMgr()) {
         smaMgr.getBranchMgr().notify();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.IActionBranchStateChange#branchCreated()
    */
   public void branchCreated(Branch branch) {
      try {
         setBranchId(branch.getBranchId());
         smaMgr.getSma().persist();

         updateBranchAccessControl();
         SkynetEventManager.getInstance().kick(new AtsBranchCreatedEvent(this));

         synchronized (smaMgr.getBranchMgr()) {
            smaMgr.getBranchMgr().notify();
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, "Can't create branch", ex, true);
      }
   }

   public void updateBranchAccessControl() throws SQLException {
      // Only set/update branch access control if state item is configured to accept
      for (IAtsStateItem stateItem : smaMgr.getStateItems().getCurrentPageStateItems(smaMgr)) {
         if (stateItem.isAccessControlViaAssigneesEnabledForBranching()) {
            Branch branch = getBranch();
            if (branch != null) {
               for (AccessControlData acd : AccessControlManager.getInstance().getAccessControlList(branch)) {
                  // If subject is NOT an assignee, remove access control
                  if (!smaMgr.getAssignees().contains(acd.getSubject())) {
                     AccessControlManager.getInstance().removeAccessControlData(acd);
                  }
               }
               // If subject doesn't have access, add it
               for (User user : smaMgr.getAssignees())
                  AccessControlManager.getInstance().setPermission(user, branch, PermissionEnum.FULLACCESS);
            }
         }
      }
   }

   /**
    * @param popup if true, popup errors associated with results
    * @return Result
    */
   public Result commitWorkingBranch(boolean popup) {
      return commitWorkingBranch(popup, false);
   }

   /**
    * @param popup if true, popup errors associated with results
    * @param overrideStateValidation if true, don't do checks to see if commit can be performed. This should only be
    *           used for developmental testing or automation
    * @return Result
    */
   public Result commitWorkingBranch(boolean popup, boolean overrideStateValidation) {
      commitPopup = popup;

      try {
         Branch branch = getBranch();
         if (branch == null) {
            OSEELog.logSevere(AtsPlugin.class, "Commit Branch Failed: Can not locate branch for id " + getBranchId(),
                  popup);
            return new Result("Commit Branch Failed: Can not locate branch.");
         }

         if (!overrideStateValidation) {
            // Check extenstion points for valid commit
            for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(smaMgr.getWorkPage().getId())) {
               Result result = item.committing(smaMgr);
               if (result.isFalse()) return result;
            }
         }

         try {
            if (commitPopup && Display.getCurrent() != null) {
               CheckBoxDialog cd = BranchView.createCommitDialog();

               if (cd.open() != Window.OK) {
                  return new Result("Operation Cancelled");
               }
            }

            BranchPersistenceManager.getInstance().commitBranch(branch, true, this);
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, "Commit Branch Failed", ex, popup);
            return new Result("Commit Branch Failed: " + ex.getLocalizedMessage());
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Commit Branch Failed", ex, popup);
         return new Result("Commit Branch Failed" + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   /**
    * Clear out branch id attribute DON this needs to delete the attribute, not just set to ""
    * 
    * @throws SQLException
    */
   public void clearBranchId() throws SQLException {
      smaMgr.getSma().getAttributeManager(ATSAttributes.BRANCH_ID_ATTRIBUTE.getStoreName()).setSoleAttributeValue("");
   }

   /**
    * @return Branch if one exists
    */
   public Branch getBranch() throws SQLException {
      if (getBranchId() != null && getBranchId() > 0) return BranchPersistenceManager.getInstance().getBranch(
            getBranchId());
      return null;
   }

   /**
    * Return the artifacts modifed via transaction of branch commit during implementation state. This includes artifacts
    * that only had relation changes. NOTE: The returned artifacts are the old versions at the time of the commit. They
    * can't be used for editing or relating. NOTE: This is a VERY expensive operation as each artifact must be loaded.
    * Retrieving data through change report with snapshotting is cheaper.
    * 
    * @return artifacts modifed
    */
   public Collection<Artifact> getArtifactsModified(boolean includeRelationOnlyChanges) {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      Integer transId = getTransactionIdInt();
      if (transId == null || transId == 0) return arts;

      try {
         TransactionId txId = getTransactionId();
         return RevisionManager.getInstance().getNewAndModifiedArtifacts(txId, txId, includeRelationOnlyChanges);
      } catch (SQLException ex) {
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
   public Collection<Artifact> getArtifactsRelChanged() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      Integer transId = getTransactionIdInt();
      if (transId == null || transId == 0) return arts;

      try {
         TransactionId txId = getTransactionId();
         return RevisionManager.getInstance().getRelationChangedArtifacts(txId, txId);
      } catch (SQLException ex) {
         OSEELog.logSevere(AtsPlugin.class, "Error getting relation changed artifacts", true);
      }
      return arts;
   }

   /**
    * Since deleted artifacts don't exist, this method will return the artifact object just prior to it's deletion.
    * NOTE: This is a VERY expensive operation as each artifact must be loaded. Retrieving data through change report
    * with snapshotting is cheaper.
    * 
    * @return artifacts that were deleted
    */
   public Collection<Artifact> getArtifactsDeleted() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      Integer transId = getTransactionIdInt();
      if ((transId == null) || (transId == 0)) return arts;

      try {
         for (ArtifactChange artChange : RevisionManager.getInstance().getDeletedArtifactChanges(getTransactionId())) {
            if (artChange.getModType() == ModificationType.DELETE) arts.add(artChange.getArtifact());
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class,
               "Error getting deleted artifacts " + smaMgr.getSma().getHumanReadableId(), ex, true);
      }
      return arts;
   }

   /**
    * Return the head of artifacts modifed via transaction of branch commit during implementation state. NOTE: The
    * returned artifacts are NOT the old versions at the time of the commit. They are the head versions of the artifacts
    * and CAN be used for relating
    * 
    * @return head of artifacts modified
    * @throws SQLException
    */
   public Collection<Artifact> getArtifactsModifiedHead() throws SQLException {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      Integer transId = getTransactionIdInt();
      if ((transId == null) || (transId == 0)) return arts;
      TransactionId trans = getTransactionId();
      Collection<Artifact> transArts = ArtifactPersistenceManager.getInstance().getArtifacts(
            new ArtifactInTransactionSearch(trans), trans.getBranch());
      return transArts;
   }
}