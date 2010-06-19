package org.eclipse.osee.framework.ui.skynet.change;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

public final class UiSelectBetweenDeltasBranchProvider implements IBranchProvider {
   private final ChangeUiData uiData;

   public UiSelectBetweenDeltasBranchProvider(ChangeUiData uiData) {
      this.uiData = uiData;
   }

   @Override
   public Branch getBranch(IProgressMonitor monitor) throws OseeCoreException {
      final Branch[] selectedBranch = new Branch[1];

      TransactionDelta txDelta = uiData.getTxDelta();
      if (txDelta.areOnTheSameBranch()) {
         selectedBranch[0] = txDelta.getStartTx().getBranch();
      } else {
         final Collection<Branch> selectable = new ArrayList<Branch>();
         selectable.add(uiData.getTxDelta().getStartTx().getBranch());
         selectable.add(uiData.getTxDelta().getEndTx().getBranch());
         IStatus status = executeInUiThread(selectable, selectedBranch);
         monitor.setCanceled(status.getSeverity() == IStatus.CANCEL);
      }
      return selectedBranch[0];
   }

   private IStatus executeInUiThread(final Collection<Branch> selectable, final Branch[] selectedBranch) throws OseeCoreException {
      IStatus status = null;
      Display display = PlatformUI.getWorkbench().getDisplay();
      if (display.getThread().equals(Thread.currentThread())) {
         status = getUserSelection(selectable, selectedBranch);
      } else {
         Job job = new UIJob("Select Branch") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               return getUserSelection(selectable, selectedBranch);
            }
         };
         try {
            Jobs.startJob(job).join();
         } catch (InterruptedException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
         status = job.getResult();
      }
      return status;
   }

   private IStatus getUserSelection(Collection<Branch> selectable, Branch[] selectedBranch) {
      IStatus status = Status.OK_STATUS;
      BranchSelectionDialog dialog = new BranchSelectionDialog("Select branch to compare against", selectable);
      int result = dialog.open();
      if (result == Window.OK) {
         selectedBranch[0] = dialog.getSelected();
      } else {
         status = Status.CANCEL_STATUS;
      }
      return status;
   }

}