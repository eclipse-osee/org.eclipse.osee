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
package org.eclipse.osee.framework.ui.skynet.branch;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.SkynetSelections;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class BranchCreationHandler extends AbstractSelectionEnabledHandler {
   private TreeViewer branchTable;

   /**
    * @param branchTable
    */
   public BranchCreationHandler(MenuManager menuManager, TreeViewer branchTable) {
      super(menuManager);
      this.branchTable = branchTable;
   }

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();
      Object backingData = ((JobbedNode) selection.getFirstElement()).getBackingData();

      final TransactionId parentTransactionId;
      try {
         if (backingData instanceof Branch) {
            Branch branch = (Branch) backingData;
            parentTransactionId = TransactionIdManager.getlatestTransactionForBranch(branch);
         } else if (backingData instanceof TransactionId) {

            parentTransactionId = ((TransactionId) backingData);

         } else {
            throw new OseeStateException(
                  "Backing data for the jobbed node in the branchview was not of the expected type");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return null;
      }
      final EntryDialog dialog =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Branch", null, "Enter the name of the new Branch",
                  MessageDialog.INFORMATION, new String[] {"OK", "Cancel"}, 0);
      int result = dialog.open();

      if (result == 0 && dialog.getEntry() != null) {

         IExceptionableRunnable runnable = new IExceptionableRunnable() {
            public void run(IProgressMonitor monitor) throws Exception {
               BranchManager.createWorkingBranch(parentTransactionId, null, dialog.getEntry(), null);
            }
         };

         Jobs.run("Create Branch", runnable, SkynetGuiPlugin.class, SkynetGuiPlugin.PLUGIN_ID);
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      IStructuredSelection selection = (IStructuredSelection) branchTable.getSelection();

      return (SkynetSelections.oneBranchSelected(selection) && AccessControlManager.checkObjectPermission(
            SkynetSelections.boilDownObject(selection.getFirstElement()), PermissionEnum.READ)) || (SkynetSelections.oneTransactionSelected(selection) && AccessControlManager.checkObjectPermission(
            ((TransactionData) SkynetSelections.boilDownObject(selection.getFirstElement())).getTransactionId().getBranch(),
            PermissionEnum.READ));
   }
}