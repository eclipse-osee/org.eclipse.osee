/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.CompareType;

public class LoadCompareBranchToBranch extends AbstractOperation {

   private final ChangeUiData uiData;
   private final IBranchProvider branchProvider;

   public LoadCompareBranchToBranch(ChangeUiData uiData, IBranchProvider branchProvider) {
      super("Load data to compare different branches", SkynetGuiPlugin.PLUGIN_ID);
      this.uiData = uiData;
      this.branchProvider = branchProvider;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Branch startBranch = uiData.getTxDelta().getStartTx().getBranch();
      Branch otherBranch = branchProvider.getBranch(monitor);
      checkForCancelledStatus(monitor);

      Conditions.checkNotNull(otherBranch, "other branch to compare to");

      TransactionRecord startTx = TransactionManager.getHeadTransaction(startBranch);
      TransactionRecord endTx = TransactionManager.getHeadTransaction(otherBranch);
      TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
      uiData.setTxDelta(txDelta);

      Branch mergeBranch = BranchManager.getMergeBranch(startBranch, otherBranch, true);
      uiData.setMergeBranch(mergeBranch);

      if (otherBranch.equals(startBranch.getParentBranch())) {
         uiData.setCompareType(CompareType.COMPARE_CURRENTS_AGAINST_PARENT);
      } else {
         uiData.setCompareType(CompareType.COMPARE_CURRENTS_AGAINST_OTHER_BRANCH);
      }
   }
}
