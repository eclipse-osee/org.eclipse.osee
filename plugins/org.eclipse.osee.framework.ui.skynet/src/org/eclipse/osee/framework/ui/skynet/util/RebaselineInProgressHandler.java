/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

/**
 * @author Angel Avila
 */

import org.apache.commons.lang.mutable.MutableBoolean;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

public class RebaselineInProgressHandler {

   private final static String TITLE = "Update In Progress";
   private final static String DIALOG =
      "This working branch is already being updated from parent, conflicts were detected.\n\nWhat would you like to do?";
   private final static String[] CHOICES = new String[] {"Finish Update", "Abort Update", "Cancel"};

   public static void handleRebaselineInProgress(Branch branch) throws OseeCoreException {
      MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), TITLE,
         null, DIALOG, MessageDialog.QUESTION, CHOICES, 0);
      int result = dialog.open();
      if (result == 2) { // Cancel
         return;
      } else if (result == 0) {
         openMergeViewForCurrentUpdate(branch);
      } else {
         cancelCurrentUpdate(branch, false);
      }
   }

   private static void openMergeViewForCurrentUpdate(Branch branch) throws OseeCoreException {
      MergeBranch mergeBranch = BranchManager.getFirstMergeBranch(branch);
      MergeView.openView(branch, mergeBranch.getDestinationBranch(), branch.getBaseTransaction());
   }

   public static void cancelCurrentUpdate(Branch branch, boolean isSkipPrompt) throws OseeCoreException {
      if (isSkipPrompt || promptUser(branch)) {
         MergeBranch mergeBranch = BranchManager.getFirstMergeBranch(branch);
         if (branch.getBranchState() == BranchState.REBASELINE_IN_PROGRESS) {
            BranchManager.purgeBranch(mergeBranch.getDestinationBranch());
            branch.setBranchState(BranchState.MODIFIED);
            BranchManager.persist(branch);
         }
      }
   }

   private static boolean promptUser(Branch sourceBranch) {
      final MutableBoolean isUserSure = new MutableBoolean(false);
      final String message = String.format(
         "Are you sure you want to Abondon the Update for [%s].  Any resolved conflicts will be lost.", sourceBranch);

      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            isUserSure.setValue(MessageDialog.openConfirm(
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Abondon Update", message));
         }
      });

      return isUserSure.booleanValue();
   }
}
