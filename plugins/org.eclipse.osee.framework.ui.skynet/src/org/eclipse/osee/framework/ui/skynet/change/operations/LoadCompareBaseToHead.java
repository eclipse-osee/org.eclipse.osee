/*
 * Created on May 5, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.CompareType;

public class LoadCompareBaseToHead extends AbstractOperation {

   private final ChangeUiData uiData;

   public LoadCompareBaseToHead(ChangeUiData uiData) {
      super("Load Data to compare from base to head transaction", SkynetGuiPlugin.PLUGIN_ID);
      this.uiData = uiData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Branch branch = uiData.getTxDelta().getStartTx().getBranch();
      TransactionRecord startTx = branch.getBaseTransaction();
      TransactionRecord endTx = TransactionManager.getHeadTransaction(branch);
      TransactionDelta txDelta = new TransactionDelta(startTx, endTx);
      uiData.setTxDelta(txDelta);
      uiData.setCompareType(CompareType.COMPARE_BASE_TO_HEAD);
   }
}
