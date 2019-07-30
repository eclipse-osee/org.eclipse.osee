/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.branch.AtsBranchUtil;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeTargetedVersionHeader;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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

   public static String WIDGET_NAME = "XWorkingBranchButtonCreate";

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
            Result result = AtsBranchUtil.createWorkingBranch_Validate(getTeamArt());
            boolean appropriate = selectTargetedVersionIfAppropriate(result, button);
            if (appropriate) {
               return;
            }
            if (result.isFalse()) {
               AWorkbench.popup(result);
               disableAll = false;
               refreshEnablement(button);
               return;
            }
            try {
               String workingBranchName = AtsClientService.get().getBranchService().getBranchName(getTeamArt());
               final BranchId parentBranch =
                  AtsClientService.get().getBranchService().getConfiguredBranchForWorkflow(getTeamArt());
               if (parentBranch.isValid()) {
                  String pBranchName = AtsClientService.get().getBranchService().getBranchName(parentBranch);
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
                  AtsBranchUtil.createWorkingBranch_Create(getTeamArt(), false);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               disableAll = false;
               refreshEnablement(button);
            }
         }
      });
   }

   private boolean selectTargetedVersionIfAppropriate(Result result, Button button) {
      boolean returnVal = false;
      if (result.getText().equals(AtsBranchUtil.PARENT_BRANCH_CAN_NOT_BE_DETERMINED)) {
         returnVal = true;
         IAtsVersion version = AtsClientService.get().getVersionService().getTargetedVersion(getTeamArt());
         if (version == null) {
            MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Create Working Branch", null,
               AtsBranchUtil.PARENT_BRANCH_CAN_NOT_BE_DETERMINED, MessageDialog.ERROR,
               new String[] {"Select Targeted Version", "Cancel"}, 0);
            if (dialog.open() == 0) {
               if (!(WfeTargetedVersionHeader.chooseVersion(getTeamArt()))) {
                  refreshEnablement(button);
               }
            } else {
               refreshEnablement(button);
            }
         }
      }
      return returnVal;
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(
         !disableAll && !isWorkingBranchCreationInProgress() && !isWorkingBranchCommitInProgress() && !isWorkingBranchInWork() && !isCommittedBranchExists());
   }

}
