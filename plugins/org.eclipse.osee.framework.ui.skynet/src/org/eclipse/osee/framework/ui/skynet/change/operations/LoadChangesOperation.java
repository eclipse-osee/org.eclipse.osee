package org.eclipse.osee.framework.ui.skynet.change.operations;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeDataLoader;
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
      Collection<Change> changes = changeData.getChanges();
      changes.clear();
      monitor.worked(calculateWork(0.10));

      IOperation subOp = new ChangeDataLoader(changes, changeData.getTxDelta());
      doSubWork(subOp, monitor, 0.80);

      changeData.setIsLoaded(true);
      monitor.worked(calculateWork(0.10));
   }
};