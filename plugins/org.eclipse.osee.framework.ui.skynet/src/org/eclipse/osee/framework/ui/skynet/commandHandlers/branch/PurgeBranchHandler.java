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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class PurgeBranchHandler extends CommandHandler {

   @Override
   public Object execute(ExecutionEvent arg0) {
      IStructuredSelection selection =
         (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      Branch selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();

      MessageDialog dialog =
         new MessageDialog(Displays.getActiveShell(), "Purge Branch", null,
            "Are you sure you want to purge the branch: " + selectedBranch.getName(), MessageDialog.QUESTION,
            new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);

      if (dialog.open() == 0) {
         Operations.executeAsJob(new PurgeBranchHttpRequestOperation(selectedBranch), true);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) throws OseeCoreException {
      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      return branches.size() == 1 && AccessControlManager.isOseeAdmin();
   }
}