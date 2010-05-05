package org.eclipse.osee.framework.ui.skynet.change.operations;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;

public class LoadChangesOperation extends AbstractOperation {
   private final ChangeUiData changeData;

   public LoadChangesOperation(ChangeUiData changeData) {
      super("Load Change Data", SkynetGuiPlugin.PLUGIN_ID);
      this.changeData = changeData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      changeData.setIsLoaded(false);
      boolean hasBranch = changeData.isBranchValid();
      boolean isRebaselined =
            hasBranch ? changeData.getBranch().getBranchState().equals(BranchState.REBASELINED) : false;
      if (!isRebaselined) {
         Collection<Change> changes = changeData.getChanges();
         IOperation subOp;
         if (hasBranch) {
            subOp = ChangeManager.comparedToParent(changeData.getBranch(), changes);
         } else {
            subOp = ChangeManager.comparedToPreviousTx(changeData.getTransaction(), changes);
         }
         doSubWork(subOp, monitor, 0.80);
      } else {
         monitor.worked(calculateWork(0.80));
      }
      monitor.worked(calculateWork(0.20));
      changeData.setIsLoaded(true);
   }
};