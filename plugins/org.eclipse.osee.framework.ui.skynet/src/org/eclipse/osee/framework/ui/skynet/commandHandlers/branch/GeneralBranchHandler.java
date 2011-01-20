/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
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
         dialogTitle = type;
      }
   };
   private final OpTypeEnum type;

   public GeneralBranchHandler(OpTypeEnum type) {
      this.type = type;
   }

   public abstract void performOperation(final List<Branch> branches);

   @Override
   public Object executeWithException(ExecutionEvent arg0) {
      IStructuredSelection selections =
         (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<Branch> selectedBranches = Handlers.getBranchesFromStructuredSelection(selections);

      MessageDialog dialog =
         new MessageDialog(Displays.getActiveShell(), type.dialogTitle, null, buildDialogMessage(selectedBranches,
            type.dialogType), MessageDialog.QUESTION, new String[] {
            IDialogConstants.YES_LABEL,
            IDialogConstants.NO_LABEL}, 1);

      if (dialog.open() == 0) {
         performOperation(selectedBranches);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) throws OseeCoreException {
      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return branches.size() > 0 && AccessControlManager.isOseeAdmin();
   }

   private String buildDialogMessage(List<Branch> selectedBranches, String actionDesc) {
      StringBuilder branchesStatement = new StringBuilder();
      branchesStatement.append(String.format("Are you sure you want to %s branch(es): ", actionDesc));
      branchesStatement.append(Strings.buildItemizedStatment(selectedBranches));
      branchesStatement.append(" \u003F");
      return branchesStatement.toString();
   }
}