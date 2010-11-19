package org.eclipse.osee.ats.editor.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.editor.history.operations.LoadChangesOperation;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.ui.progress.UIJob;

public final class HistoryRefreshAction extends Action {

   private final XHistoryViewer xHistoryViewer;

   public HistoryRefreshAction(String text, int style, XHistoryViewer xHistoryViewer) {
      super(text, style);
      this.xHistoryViewer = xHistoryViewer;
      setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      setToolTipText("Refresh History");
   }

   @Override
   public void run() {
      setEnabled(false);
      setToolTipText("Already Loading, Please Wait");
      this.xHistoryViewer.getXViewer().setInput(Arrays.asList("Loading..."));

      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new LoadChangesOperation(xHistoryViewer.awa, xHistoryViewer.changes));
      IOperation operation = new CompositeOperation("Load History Viewer", AtsPlugin.PLUGIN_ID, ops);
      Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            IStatus result = event.getResult();
            if (!result.isOK()) {
               setEnabled(true);
            } else {
               Job job = new UIJob("Load History Table") {

                  @Override
                  public IStatus runInUIThread(IProgressMonitor monitor) {
                     try {
                        if (Widgets.isAccessible(xHistoryViewer.getXViewer().getTree())) {
                           xHistoryViewer.getXViewer().setInput(xHistoryViewer.changes);
                        }
                     } catch (Exception ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                     } finally {
                        setEnabled(true);
                        setToolTipText("Refresh History");
                     }
                     return Status.OK_STATUS;
                  }
               };
               Operations.scheduleJob(job, true, Job.SHORT, null);
            }
         }
      });
   }
}
