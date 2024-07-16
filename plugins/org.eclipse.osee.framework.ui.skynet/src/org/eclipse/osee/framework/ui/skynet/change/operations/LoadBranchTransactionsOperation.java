/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.change.BranchTransactionUiData;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class LoadBranchTransactionsOperation extends AbstractOperation {
   private final BranchTransactionUiData branchTransactionData;

   public LoadBranchTransactionsOperation(BranchTransactionUiData branchTransactionData) {
      super("Loading Branch Transactions", Activator.PLUGIN_ID);
      this.branchTransactionData = branchTransactionData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<TransactionRecord> transactions =
         TransactionManager.getTransactionsForBranch(branchTransactionData.getBranch());
      List<TransactionRecord> subsetTxs = null;
      Integer numTransactions = branchTransactionData.getNumTransactions();
      if (numTransactions > 0) {
         Collections.sort(transactions, new Comparator<TransactionRecord>() {
            @Override
            public int compare(TransactionRecord o1, TransactionRecord o2) {
               return (int) (o2.getId().longValue() - o1.getId().longValue());
            }

         });
         subsetTxs = new ArrayList<TransactionRecord>();
         int x = 1;
         for (TransactionRecord tx : transactions) {
            subsetTxs.add(tx);
            if (x++ == numTransactions) {
               break;
            }
         }
      } else {
         subsetTxs = transactions;
      }

      Collections.sort(subsetTxs, new Comparator<TransactionRecord>() {
         @Override
         public int compare(TransactionRecord o1, TransactionRecord o2) {
            return (int) (o1.getId().longValue() - o2.getId().longValue());
         }

      });

      List<Object> items = null;
      if (transactions != null) {
         items = org.eclipse.osee.framework.jdk.core.util.Collections.getAggregateTree(new ArrayList<Object>(subsetTxs),
            500);
      } else {
         items = Collections.emptyList();
      }
      branchTransactionData.setTransactions(items.toArray());
   }

}
