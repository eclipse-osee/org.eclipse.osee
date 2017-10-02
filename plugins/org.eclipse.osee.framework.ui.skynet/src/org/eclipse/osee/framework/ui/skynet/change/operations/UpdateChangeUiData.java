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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.CompareType;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

public class UpdateChangeUiData extends AbstractOperation {
   private final ChangeUiData changeData;

   public UpdateChangeUiData(ChangeUiData changeData) {
      super("Update Change Ui Data", Activator.PLUGIN_ID);
      this.changeData = changeData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      TransactionDelta txDelta = changeData.getTxDelta();

      CompareType compareType = changeData.getCompareType();
      if (!compareType.areSpecificTxs()) {
         TransactionToken startTx = txDelta.getStartTx();
         if (!compareType.isBaselineTxIncluded()) {
            startTx = TransactionManager.getHeadTransaction(startTx.getBranch());
         }
         TransactionToken endTx = TransactionManager.getHeadTransaction(txDelta.getEndTx().getBranch());

         txDelta = new TransactionDelta(startTx, endTx);
         changeData.setTxDelta(txDelta);
      }

      if (!txDelta.areOnTheSameBranch()) {
         IOseeBranch mergeBranch =
            BranchManager.getMergeBranch(txDelta.getStartTx().getBranch(), txDelta.getEndTx().getBranch());
         changeData.setMergeBranch(mergeBranch);
      }
      boolean areBranchesValid = !hasBeenRebaselined(txDelta.getStartTx()) && !hasBeenRebaselined(txDelta.getEndTx());
      changeData.setAreBranchesValid(areBranchesValid);
   }

   private boolean hasBeenRebaselined(TransactionToken tx)  {
      return BranchManager.getState(tx.getBranch()).isRebaselined();
   }
}
