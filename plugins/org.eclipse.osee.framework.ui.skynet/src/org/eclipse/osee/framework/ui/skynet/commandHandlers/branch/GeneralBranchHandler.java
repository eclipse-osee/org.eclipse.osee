/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Karol M. Wilk
 */
public abstract class GeneralBranchHandler extends CommandHandler {
   public enum OpTypeEnum {
      DELETE("delete", "Delete Branch"),
      PURGE("purge", "Purge Branch");

      private final String dialogType;
      private final String dialogTitle;

      private OpTypeEnum(String type, String title) {
         dialogType = type;
         dialogTitle = title;
      }
   };
   private final OpTypeEnum type;

   public GeneralBranchHandler(OpTypeEnum type) {
      this.type = type;
   }

   public abstract void performOperation(final List<BranchToken> branches);

   @Override
   public Object executeWithException(ExecutionEvent arg0, IStructuredSelection selection) {

      XResultData errorResults = new XResultData();
      errorResults.log(type.dialogTitle + "\n\n");

      XResultData confirmDialogResults = new XResultData();
      confirmDialogResults.logf("Are you sure you want to %s branch(es):\n\n", type.dialogType);

      List<BranchToken> selectedBranches = Handlers.getBranchesFromStructuredSelection(selection);
      for (BranchToken branch : selectedBranches) {
         confirmDialogResults.logf("Branch: %s\n", branch.toStringWithId());
         if (!AccessControlManager.hasPermission(branch, PermissionEnum.WRITE)) {
            errorResults.errorf("No write permission for Branch %s.\n\n", branch.toStringWithId());
         }
         if (BranchManager.hasChildren(branch)) {
            errorResults.errorf("Branch %s has children.\n\n", branch.toStringWithId());
         }
      }

      if (errorResults.isErrors()) {
         MessageDialog.openError(Displays.getActiveShell(), type.dialogTitle, errorResults.toString());
      } else {
         if (MessageDialog.openQuestion(Displays.getActiveShell(), type.dialogTitle, confirmDialogResults.toString())) {
            performOperation(selectedBranches);
         }
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<? extends BranchId> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return !branches.isEmpty() && (AccessControlManager.isOseeAdmin() || canEnableBranches(branches));
   }

   private boolean canEnableBranches(List<? extends BranchId> branches) {
      boolean canBeDeleted = true;
      for (BranchId branch : branches) {
         if (!isEnableAllowed(branch)) {
            canBeDeleted = false;
            break;
         }
      }
      return canBeDeleted;
   }

   private boolean isEnableAllowed(BranchId branch) {
      return !BranchManager.isChangeManaged(branch) && BranchManager.getType(branch).isWorkingBranch();
   }
}