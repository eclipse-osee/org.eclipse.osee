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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.CompareType;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

public class LoadCompareBaseToHead extends AbstractOperation {

   private final ChangeUiData uiData;

   public LoadCompareBaseToHead(ChangeUiData uiData) {
      super("Load Data to compare from base to head transaction", Activator.PLUGIN_ID);
      this.uiData = uiData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      BranchId branch = uiData.getTxDelta().getStartTx().getBranch();
      TransactionRecord startTx = BranchManager.getBaseTransaction(branch);
      TransactionRecord endTx = TransactionManager.getHeadTransaction(branch);
      TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
      uiData.setTxDelta(txDelta);
      uiData.setCompareType(CompareType.COMPARE_BASE_TO_HEAD);
   }
}
