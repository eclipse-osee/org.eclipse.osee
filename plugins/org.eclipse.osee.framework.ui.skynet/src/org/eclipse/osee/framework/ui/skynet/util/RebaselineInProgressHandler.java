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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchState;
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

   public static void handleRebaselineInProgress(BranchId branch)  {
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

   private static void openMergeViewForCurrentUpdate(BranchId branch)  {
      MergeBranch mergeBranch = BranchManager.getFirstMergeBranch(branch);
      MergeView.openView(branch, mergeBranch.getDestinationBranch(), BranchManager.getBaseTransaction(branch));
   }

   public static void cancelCurrentUpdate(BranchId branch, boolean isSkipPrompt)  {
      if (isSkipPrompt || promptUser(branch)) {
         MergeBranch mergeBranch = BranchManager.getFirstMergeBranch(branch);
         if (BranchManager.getState(branch).isRebaselineInProgress()) {
            BranchManager.purgeBranch(mergeBranch.getDestinationBranch());
            BranchManager.setState(branch, BranchState.MODIFIED);
         }
      }
   }

   private static boolean promptUser(BranchId sourceBranch) {
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
