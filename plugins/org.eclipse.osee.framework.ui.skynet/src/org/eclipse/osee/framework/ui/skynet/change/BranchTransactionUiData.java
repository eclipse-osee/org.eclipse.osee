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
package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchContentProvider;

/**
 * @author Donald G. Dunne
 */
public class BranchTransactionUiData {

   Object[] transactions;
   private final Branch branch;
   private final XBranchContentProvider branchContentProvider;

   public BranchTransactionUiData(Branch branch, XBranchContentProvider branchContentProvider) {
      this.branch = branch;
      this.branchContentProvider = branchContentProvider;
   }

   public Object[] getTransactions() {
      return transactions;
   }

   public void setTransactions(Object[] transactions) {
      this.transactions = transactions;
   }

   public Branch getBranch() {
      return branch;
   }

   public XBranchContentProvider getBranchContentProvider() {
      return branchContentProvider;
   }

   public void reset() {
      transactions = null;
   }

}
