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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class CommitIntoHandler extends CommitHandler {

   private static boolean USE_PARENT_BRANCH = false;

   public CommitIntoHandler() {
      super(USE_PARENT_BRANCH);
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      BranchId sourceBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      BranchType[] allowedTypes;
      if (AccessControlManager.isOseeAdmin()) {
         allowedTypes = new BranchType[] {BranchType.WORKING, BranchType.BASELINE};
      } else {
         allowedTypes = new BranchType[] {BranchType.WORKING};
      }

      List<? extends IOseeBranch> branches = BranchManager.getBranches(BranchArchivedState.UNARCHIVED, allowedTypes);
      Collections.sort(branches);
      branches.remove(sourceBranch);

      if (!branches.isEmpty()) {
         BranchSelectionDialog branchSelection = new BranchSelectionDialog(
            String.format("Source Branch [%s]\n\nSelect Destination Branch", sourceBranch), branches);
         int result = branchSelection.open();
         if (result == Window.OK) {
            CheckBoxDialog dialog = new CheckBoxDialog("Commit Into",
               String.format("Commit from\n\nSource Branch: [%s]\n\ninto\n\nDestination Branch: [%s]", sourceBranch,
                  branchSelection.getSelection()),
               "Archive Source Branch");
            if (dialog.open() == 0) {
               Jobs.startJob(new CommitJob(sourceBranch, branchSelection.getSelection(), dialog.isChecked()));
            }
         }
      } else {
         MessageDialog.openWarning(Displays.getActiveShell(), "Commit Into",
            "No valid destination branches found.\n\nPlease contact an OSEE Admin from more information.");
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return branches.size() == 1;
   }

}
