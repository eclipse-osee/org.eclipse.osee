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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperationWithListener;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class PurgeTransactionHandler extends CommandHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
      List<TransactionRecord> transactions = Handlers.getTransactionsFromStructuredSelection(selection);

      if (MessageDialog.openConfirm(Displays.getActiveShell(), "Purge Transaction",
         "Are you sure you want to purge " + getTransactionListStr(transactions))) {

         IJobChangeListener jobChangeListener = new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               if (event.getResult().getSeverity() == IStatus.OK) {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           BranchManager.refreshBranches();
                        } catch (OseeCoreException ex) {
                           OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  });
               }
            }

         };

         IOperation op = PurgeTransactionOperationWithListener.getPurgeTransactionOperation(transactions);
         return Operations.executeAsJob(op, true, Job.LONG, jobChangeListener);
      }

      return null;
   }

   private String getTransactionListStr(List<TransactionRecord> transactions) {
      if (transactions.size() == 1) {
         return "the transaction: " + transactions.iterator().next().toString();
      }
      return transactions.size() + " transactions:\n\n " + Collections.toString(", ", transactions);
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) throws OseeCoreException {
      List<TransactionRecord> transactions = Handlers.getTransactionsFromStructuredSelection(structuredSelection);
      return transactions.size() > 0 && AccessControlManager.isOseeAdmin();
   }
}