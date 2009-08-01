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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 */
public class PurgeTransactionHandler extends CommandHandler {

   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<TransactionId> transactions = Handlers.getTransactionsFromStructuredSelection(selection);
      TransactionId selectedTransaction = transactions.iterator().next();

      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Purge Transaction",
            "Are you sure you want to purge the transaction: " + selectedTransaction.getTransactionNumber())) {
         BranchManager.purgeTransactions(new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               if (event.getResult().getSeverity() == IStatus.OK) {
                  Display.getDefault().asyncExec(new Runnable() {
                     public void run() {
                        try {
                           BranchManager.refreshBranches();
                        } catch (OseeCoreException ex) {
                           OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  });
               }
            }

         }, selectedTransaction.getTransactionNumber());
      }

      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      if (AWorkbench.getActivePage() == null) return false;
      IStructuredSelection selection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<TransactionId> transactions = Handlers.getTransactionsFromStructuredSelection(selection);
      return transactions.size() == 1 && AccessControlManager.isOseeAdmin();
   }
}