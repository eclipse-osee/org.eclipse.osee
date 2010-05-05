/*
 * Created on Apr 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

public final class ChangeUiUtil {

   private ChangeUiUtil() {
   }

   public static void open(Branch branch) throws OseeCoreException {
      Conditions.checkNotNull(branch, "Branch");
      if (EditorsPreferencePage.isUseLegacyChangeReportView()) {
         ChangeView.open(branch); // Legacy Change Report
      } else {
         ChangeUiData uiData = new ChangeUiData();
         uiData.setBranch(branch);
         open(new ChangeReportEditorInput(uiData), true);
      }
   }

   public static void open(TransactionRecord transactionId) throws OseeCoreException {
      Conditions.checkNotNull(transactionId, "TransactionId");
      if (EditorsPreferencePage.isUseLegacyChangeReportView()) {
         ChangeView.open(transactionId); // Legacy Change Report
      } else {
         ChangeUiData uiData = new ChangeUiData();
         uiData.setTransaction(transactionId);
         open(new ChangeReportEditorInput(uiData), true);
      }
   }

   private static void open(final ChangeReportEditorInput editorInput, final boolean loadChanges) {
      Job job = new UIJob("Open Change Report") {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status = Status.OK_STATUS;
            try {
               IEditorPart part = AWorkbench.getActivePage().openEditor(editorInput, ChangeReportEditor.EDITOR_ID);
               if (loadChanges) {
                  ChangeReportEditor editor = (ChangeReportEditor) part;
                  editor.recomputeChangeReport();
               }
            } catch (PartInitException ex) {
               status = new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, "Error opening change report", ex);
            }
            return status;
         }
      };
      Jobs.startJob(job, true);
   }
}
