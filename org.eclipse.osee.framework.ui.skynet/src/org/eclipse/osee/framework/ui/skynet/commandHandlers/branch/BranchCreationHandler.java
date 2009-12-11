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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class BranchCreationHandler extends CommandHandler {

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      Object backingData = selection.getFirstElement();

      final TransactionRecord parentTransactionId;
      try {
         if (backingData instanceof Branch) {
            Branch branch = (Branch) backingData;
            parentTransactionId = TransactionManager.getLastTransaction(branch);
         } else if (backingData instanceof TransactionRecord) {

            parentTransactionId = (TransactionRecord) backingData;

         } else {
            throw new OseeStateException(
                  "Backing data for the jobbed node in the branchview was not of the expected type");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return null;
      }
      final EntryDialog dialog =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Branch", null, "Enter the name of the new Branch:",
                  MessageDialog.INFORMATION, new String[] {"OK", "Cancel"}, 0);
      int result = dialog.open();

      if (result == 0 && dialog.getEntry() != null) {

         IExceptionableRunnable runnable = new IExceptionableRunnable() {
            public IStatus run(IProgressMonitor monitor) throws Exception {
               Branch branch = parentTransactionId.getBranch();
               if (branch.equals(CoreBranches.SYSTEM_ROOT)) {
                  BranchManager.createTopLevelBranch(dialog.getEntry());
               } else {
                  BranchManager.createWorkingBranch(parentTransactionId, dialog.getEntry(), null, null);
               }
               return Status.OK_STATUS;
            }
         };

         Jobs.runInJob("Create Branch", runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      if (AWorkbench.getActivePage() == null) {
         return false;
      }
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      boolean enabled;
      if (selection.size() != 1) {
         return false;
      }

      Object object = selection.getFirstElement();
      Branch branch = null;

      if (object instanceof Branch) {
         branch = (Branch) object;
      } else if (object instanceof TransactionRecord) {
         branch = ((TransactionRecord) object).getBranch();
      }

      if (branch == null) {
         return false;
      }

      enabled = AccessControlManager.hasPermission(branch, PermissionEnum.READ);
      return enabled;
   }
}