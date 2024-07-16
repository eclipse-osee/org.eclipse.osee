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

package org.eclipse.osee.ats.ide.util.widgets.commit;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.commit.CommitOverride;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.commit.menu.CommitOverrideAction;
import org.eclipse.osee.ats.ide.util.widgets.commit.menu.RemoveCommitOverrideAction;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
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
         BranchToken branch = null;
         CommitConfigItem configItem = null;
         if (firstSelectedArt instanceof CommitConfigItem) {
            configItem = (CommitConfigItem) firstSelectedArt;
            branch = BranchManager.getBranchToken(AtsApiService.get().getBranchService().getBranch(configItem));
         }
         CommitOverride override = null;
         if (branch != null) {
            override =
               atsApi.getBranchService().getCommitOverrideOps().getCommitOverride(xCommitManager.getTeamArt(), branch);
         }
         if (override == null) {
            if (branch != null) {
               mm.insertAfter(MENU_GROUP_PRE, new CommitOverrideAction(teamWf, branch, atsApi));
            }
         } else if (branch != null) {
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
         CommitConfigItem configItem = null;
         if (firstSelectedArt instanceof CommitConfigItem) {
            configItem = (CommitConfigItem) firstSelectedArt;
            branch = AtsApiService.get().getBranchService().getBranch(configItem);
            displayName = configItem.toString();
         } else if (firstSelectedArt instanceof TransactionToken) {
            TransactionToken txRecord = (TransactionToken) firstSelectedArt;
            branch = txRecord.getBranch();
            displayName = txRecord.toString();
         } else {
            throw new OseeArgumentException("Unhandled element type [%s]", firstSelectedArt.getClass().toString());
         }

         CommitStatus commitStatus =
            AtsApiService.get().getBranchService().getCommitStatus(xCommitManager.getTeamArt(), branch, configItem);
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
            XResultData rd = new XResultData();
            AtsApiService.get().getBranchServiceIde().commitWorkingBranch(xCommitManager.getTeamArt(), true, false,
               branch, false, rd);
            if (rd.isErrors()) {
               ResultsEditor.open("Commit Failure", rd);
            }
         } else if (commitStatus == CommitStatus.Committed) {
            AtsApiService.get().getBranchServiceIde().showChangeReportForBranch(xCommitManager.getTeamArt(), branch);
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
         AtsApiService.get().getBranchServiceIde().showChangeReportForBranch(xCommitManager.getTeamArt(), branch);
      }
      // merge manager
      else {
         AtsApiService.get().getBranchServiceIde().showMergeManager(xCommitManager.getTeamArt());
      }
   }

}
