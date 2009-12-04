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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class PurgeBranchHandler extends CommandHandler {

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      Branch selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      MessageDialog dialog =
            new MessageDialog(Display.getCurrent().getActiveShell(), "Purge Branch", null,
                  "Are you sure you want to purge the branch: " + selectedBranch.getName(), MessageDialog.QUESTION,
                  new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);

      if (dialog.open() == 0) {
         try {
            BranchManager.purgeBranch(selectedBranch);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            return null;
         }
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      if (AWorkbench.getActivePage() == null) return false;
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection);
      return branches.size() == 1; //&& AccessControlManager.isOseeAdmin();
   }
}