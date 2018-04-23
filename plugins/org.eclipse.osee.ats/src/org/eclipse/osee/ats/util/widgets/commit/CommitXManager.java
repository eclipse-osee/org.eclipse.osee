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
package org.eclipse.osee.ats.util.widgets.commit;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.commit.CommitOverride;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.branch.AtsBranchManager;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.commit.menu.CommitOverrideAction;
import org.eclipse.osee.ats.util.widgets.commit.menu.RemoveCommitOverrideAction;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.RebaselineInProgressHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CommitXManager extends XViewer {

   private final XCommitManager xCommitManager;
   private final AtsApi atsApi;
   private final IAtsTeamWorkflow teamWf;

   public CommitXManager(Composite parent, int style, XCommitManager xRoleViewer, IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      super(parent, style, new CommitXManagerFactory(new OseeTreeReportAdapter("Table Report - Commit Manager")));
      this.xCommitManager = xRoleViewer;
      this.teamWf = teamWf;
      this.atsApi = atsApi;
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());

      if (!getSelectedArtifacts().isEmpty()) {
         Object firstSelectedArt = getSelectedArtifacts().iterator().next();
         IOseeBranch branch = null;
         ICommitConfigItem configArt = null;
         if (firstSelectedArt instanceof ICommitConfigItem) {
            configArt = (ICommitConfigItem) firstSelectedArt;
            branch = BranchManager.getBranch(AtsClientService.get().getBranchService().getBranch(configArt));
         }

         CommitOverride override =
            atsApi.getBranchService().getCommitOverrideOps().getCommitOverride(xCommitManager.getTeamArt(), branch);
         if (override == null) {
            mm.insertAfter(MENU_GROUP_PRE, new CommitOverrideAction(teamWf, branch, atsApi));
         } else {
            mm.insertAfter(MENU_GROUP_PRE, new RemoveCommitOverrideAction(teamWf, branch, atsApi));
         }
      }
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      getLabelProvider().dispose();
   }

   public List<Object> getSelectedArtifacts() {
      List<Object> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add(item.getData());
         }
      }
      return arts;
   }

   /**
    * @return the xUserRoleViewer
    */
   public XCommitManager getXCommitViewer() {
      return xCommitManager;
   }

   @Override
   public void handleDoubleClick() {
      try {
         Object firstSelectedArt = getSelectedArtifacts().iterator().next();
         BranchId branch = null;
         String displayName = "";
         ICommitConfigItem configArt = null;
         if (firstSelectedArt instanceof ICommitConfigItem) {
            configArt = (ICommitConfigItem) firstSelectedArt;
            branch = AtsClientService.get().getBranchService().getBranch(configArt);
            displayName = configArt.toString();
         } else if (firstSelectedArt instanceof TransactionToken) {
            TransactionToken txRecord = (TransactionToken) firstSelectedArt;
            branch = txRecord.getBranch();
            displayName = txRecord.toString();
         } else {
            throw new OseeArgumentException("Unhandled element type [%s]", firstSelectedArt.getClass().toString());
         }

         CommitStatus commitStatus =
            AtsClientService.get().getBranchService().getCommitStatus(xCommitManager.getTeamArt(), branch, configArt);
         if (commitStatus == CommitStatus.Commit_Overridden) {
            AWorkbench.popup("Commit Overridden.  Right-click remove override to continue.");
         } else if (commitStatus == CommitStatus.Rebaseline_In_Progress) {
            RebaselineInProgressHandler.handleRebaselineInProgress(xCommitManager.getTeamArt().getWorkingBranch());
         } else if (commitStatus == CommitStatus.Working_Branch_Not_Created) {
            AWorkbench.popup(commitStatus.getDisplayName(), "Need to create a working branch");
         } else if (commitStatus == CommitStatus.No_Commit_Needed) {
            AWorkbench.popup(commitStatus.getDisplayName(),
               "Destination Branch creation date is after commit to Parent Destination Branch; No Action Needed");
         } else if (commitStatus == CommitStatus.Branch_Not_Configured) {
            AWorkbench.popup(commitStatus.getDisplayName(),
               "Talk to project lead to configure branch for version [" + displayName + "]");
         } else if (commitStatus == CommitStatus.Branch_Commit_Disabled) {
            AWorkbench.popup(commitStatus.getDisplayName(),
               "Talk to project lead as to why commit disabled for version [" + displayName + "]");
         } else if (commitStatus == CommitStatus.Commit_Needed || commitStatus == CommitStatus.Merge_In_Progress) {
            IOperation operation =
               AtsBranchManager.commitWorkingBranch(xCommitManager.getTeamArt(), true, false, branch,
                  AtsClientService.get().getBranchService().isBranchesAllCommittedExcept(xCommitManager.getTeamArt(),
                     branch));
            Operations.executeAsJob(operation, true);
         } else if (commitStatus == CommitStatus.Committed) {
            AtsBranchManager.showChangeReportForBranch(xCommitManager.getTeamArt(), branch);
         } else if (commitStatus == CommitStatus.Committed_With_Merge) {
            handleCommittedWithMerge(branch);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void handleCommittedWithMerge(BranchId branch) {
      MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Select Report", null,
         "Both Change Report and Merge Manager exist.\n\nSelect to open.", MessageDialog.QUESTION,
         new String[] {"Show Change Report", "Show Merge Manager", "Cancel"}, 0);
      int result = dialog.open();
      if (result == 2) {
         return;
      }
      // change report
      if (result == 0) {
         AtsBranchManager.showChangeReportForBranch(xCommitManager.getTeamArt(), branch);
      }
      // merge manager
      else {
         AtsBranchManager.showMergeManager(xCommitManager.getTeamArt());
      }
   }

}
