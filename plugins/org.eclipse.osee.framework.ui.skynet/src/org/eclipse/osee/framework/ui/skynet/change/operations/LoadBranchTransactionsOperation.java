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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
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
      branchTransactionData.setTransactions(branchTransactionData.getBranchContentProvider().getBranchChildren(
         branchTransactionData.getBranch()));
   }

}
