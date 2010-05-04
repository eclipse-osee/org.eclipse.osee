package org.eclipse.osee.framework.ui.skynet.change.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

public class OpenQuickSearchAction extends Action {
   private final ChangeUiData changeData;

   public OpenQuickSearchAction(ChangeUiData changeData) {
      super("Open Quick Search", Action.AS_PUSH_BUTTON);
      this.changeData = changeData;
      setId("open.quick.search.change.report");
      setToolTipText("Open Quick Search");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_SEARCH));
   }

   @Override
   public void run() {
      Job job = new UIJob("Open Quick Search") {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status = Status.OK_STATUS;
            try {
               Branch branch = null;
               if (changeData.isBranchValid()) {
                  branch = changeData.getBranch();
               } else if (changeData.isTransactionValid()) {
                  branch = changeData.getTransaction().getBranch();
               }
               if (branch != null) {
                  IViewPart viewPart =
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                              QuickSearchView.VIEW_ID);
                  if (viewPart != null) {
                     ((QuickSearchView) viewPart).setBranch(branch);
                  }
               }
            } catch (Exception ex) {
               status = new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, "Error opening quick search view", ex);
            }
            return status;
         }
      };
      Jobs.startJob(job, true);
   }
}