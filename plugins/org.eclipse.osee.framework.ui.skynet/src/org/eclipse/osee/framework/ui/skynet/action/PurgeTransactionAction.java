/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.action;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperation;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class PurgeTransactionAction extends Action {

   private static final String NAME = "Purge Transaction";
   private final List<TransactionToken> transactions;

   public PurgeTransactionAction() {
      this(new ArrayList<TransactionToken>());
   }

   public PurgeTransactionAction(List<TransactionToken> transactions) {
      super(NAME);
      this.transactions = transactions;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.PURGE));
      setToolTipText(NAME);
   }

   @Override
   public void run() {
      if (transactions.isEmpty()) {
         if (!getTransactions()) {
            return;
         }
      }
      if (MessageDialog.openConfirm(Displays.getActiveShell(), NAME,
         "Are you sure you want to purge\n\n" + getTransactionListStr())) {

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

         IOperation op = PurgeTransactionOperation.getPurgeTransactionOperation(transactions);
         transactions.clear();
         Operations.executeAsJob(op, true, Job.LONG, jobChangeListener);
      }
   }

   private boolean getTransactions() {
      EntryDialog dialog = new EntryDialog(NAME, "Enter Transaction(s), comma delimited");
      boolean success = false;
      if (dialog.open() == 0) {

         transactions.addAll(
            Collections.fromString(dialog.getEntry(), tx -> TransactionManager.getTransaction(Long.valueOf(tx))));
         success = !transactions.isEmpty();
      }
      return success;
   }

   private String getTransactionListStr() {
      StringBuilder transStrs = new StringBuilder();
      int count = 1;
      for (TransactionId transactionId : transactions) {
         transStrs.append(String.format("Tranaction Id [%s] Comment [%s]\n", transactionId.getIdString(),
            TransactionManager.getComment(transactionId)));
         if (count++ > 30) {
            transStrs.append("(truncated at 25)...");
            break;
         }
      }
      return transStrs.toString();
   }
}