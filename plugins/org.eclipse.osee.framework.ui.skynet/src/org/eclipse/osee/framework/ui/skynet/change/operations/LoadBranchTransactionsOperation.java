/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
      Collections.sort(transactions, new Comparator<TransactionRecord>() {
         @Override
         public int compare(TransactionRecord o1, TransactionRecord o2) {
            return (int) (o1.getId().longValue() - o2.getId().longValue());
         }
      });

      List<Object> items = null;
      if (transactions != null) {
         items = org.eclipse.osee.framework.jdk.core.util.Collections.getAggregateTree(
            new ArrayList<Object>(transactions), 500);
      } else {
         items = Collections.emptyList();
      }
      branchTransactionData.setTransactions(items.toArray());
   }

}
