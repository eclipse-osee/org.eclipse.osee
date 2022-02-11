/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.branch.AtsBranchServiceIde;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeTargetedVersionHeader;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Shawn F. Cook
 */
public class XWorkingBranchButtonCreate extends XWorkingBranchButtonAbstract {

   @Override
   protected void initButton(final Button button) {
      button.setToolTipText("Create Working Branch");
      button.setImage(ImageManager.getImage(FrameworkImage.BRANCH));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            disableAll = true;
            refreshEnablement(button);
            // Create working branch
            Result result = AtsApiService.get().getBranchServiceIde().createWorkingBranch_Validate(getTeamArt());
            boolean appropriate = selectTargetedVersionOrConfigureParentBranchIfAppropriate(result, button);
            if (appropriate) {
               disableAll = false;
               refreshEnablement(button);
               return;
            }
            if (result.isFalse()) {
               AWorkbench.popup(result);
               disableAll = false;
               refreshEnablement(button);
               return;
            }
            try {
               String workingBranchName = AtsApiService.get().getBranchService().getBranchName(getTeamArt());
               final BranchId parentBranch =
                  AtsApiService.get().getBranchService().getConfiguredBranchForWorkflow(getTeamArt());
               if (parentBranch.isValid()) {
                  String pBranchName = AtsApiService.get().getBranchService().getBranchName(parentBranch);
                  // Retrieve parent branch to create working branch from
                  if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Create Working Branch",
                     "Creating working branch:\n\n\"" + workingBranchName + //
                  "\"\n\nfrom parent branch: \n\n\"" + //
                  pBranchName + "\"\n\nIs that correct?\n\n" + //
                  "---\nNOTE: Working branches are necessary when OSEE Artifact changes " + //
                  "are made during implementation.")) {
                     disableAll = false;
                     refreshEnablement(button);
                     return;
                  }
                  button.setText("Creating Branch...");
                  button.redraw();
                  button.getParent().layout();
                  AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(getTeamArt(), false);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               disableAll = false;
               refreshEnablement(button);
            }
         }
      });
   }

   private boolean selectTargetedVersionOrConfigureParentBranchIfAppropriate(Result result, Button button) {
      boolean returnVal = false;
      if (result.getText().equals(AtsBranchServiceIde.PARENT_BRANCH_CAN_NOT_BE_DETERMINED)) {
         returnVal = true;
         IAtsVersion version = AtsApiService.get().getVersionService().getTargetedVersion(getTeamArt());
         if (version == null) {
            MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Create Working Branch", null,
               AtsBranchServiceIde.PARENT_BRANCH_CAN_NOT_BE_DETERMINED, MessageDialog.ERROR,
               new String[] {"Select Targeted Version", "Cancel"}, 0);
            if (dialog.open() == Window.OK) {
               WfeTargetedVersionHeader.promptChangeVersion(getTeamArt());
            }
         } else {
            MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Create Working Branch", null,
               AtsBranchServiceIde.PARENT_BRANCH_CAN_NOT_BE_DETERMINED, MessageDialog.ERROR,
               new String[] {"Ok", "Cancel"}, 0);
            dialog.open();
         }
      }
      return returnVal;
   }

   @Override
   protected void refreshEnablement(Button button) {
      boolean enabled;
      enabled = !disableAll && (!isWorkingBranchCreationInProgress() && //
         !isWorkingBranchCommitInProgress() && //
         !isWorkingBranchInWork() && //
         !isCommittedBranchExists()) && //
         isWidgetAllowedInCurrentState();
      button.setText("");
      button.getParent().layout();
      button.setEnabled(enabled);
   }

   @Override
   protected boolean isWidgetAllowedInCurrentState() {
      return isWidgetInState(XWorkingBranchButtonCreate.class.getSimpleName());
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return null;
   }
}
