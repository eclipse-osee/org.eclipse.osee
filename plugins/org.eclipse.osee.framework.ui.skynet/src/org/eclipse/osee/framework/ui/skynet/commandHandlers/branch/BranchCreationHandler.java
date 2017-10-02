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

import static org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class BranchCreationHandler extends CommandHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      Object backingData = selection.getFirstElement();

      final TransactionToken parentTransactionId;
      if (backingData instanceof BranchId) {
         parentTransactionId = TransactionManager.getHeadTransaction((BranchId) backingData);
      } else if (backingData instanceof TransactionToken) {
         parentTransactionId = (TransactionToken) backingData;
      } else {
         throw new OseeStateException(
            "Backing data for the jobbed node in the branchview was not of the expected type");
      }
      final EntryCheckDialog dialog = new EntryCheckDialog(Displays.getActiveShell(), "Branch", null,
         "Enter the name of the new branch:", "Include a copy of the chosen transaction on the new branch",
         MessageDialog.INFORMATION, new String[] {"OK", "Cancel"}, 0);
      int result = dialog.open();

      if (result == 0 && dialog.getEntry() != null) {

         IExceptionableRunnable runnable = new IExceptionableRunnable() {
            @Override
            public IStatus run(IProgressMonitor monitor) throws Exception {
               BranchId branch = parentTransactionId.getBranch();
               if (branch.equals(CoreBranches.SYSTEM_ROOT)) {
                  BranchManager.createTopLevelBranch(dialog.getEntry());
                  AccessControlManager.setPermission(UserManager.getUser(DemoUsers.Joe_Smith), branch,
                     PermissionEnum.FULLACCESS);
               } else {
                  if (dialog.isChecked()) {
                     BranchManager.createWorkingBranchFromTx(parentTransactionId, dialog.getEntry(), null);
                  } else {
                     BranchManager.createWorkingBranch(parentTransactionId, dialog.getEntry(), OseeSystem);
                  }

               }
               return Status.OK_STATUS;
            }
         };

         Jobs.runInJob("Create Branch", runnable, Activator.class, Activator.PLUGIN_ID);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      boolean enabled;
      if (structuredSelection.size() != 1) {
         return false;
      }

      Object object = structuredSelection.getFirstElement();
      BranchId branch = null;

      if (object instanceof BranchId) {
         branch = (BranchId) object;
      } else if (object instanceof TransactionToken) {
         branch = ((TransactionToken) object).getBranch();
      }

      if (branch == null || BranchManager.isArchived(branch)) {
         return false;
      }

      enabled = AccessControlManager.hasPermission(branch, PermissionEnum.READ);
      return enabled;
   }
}