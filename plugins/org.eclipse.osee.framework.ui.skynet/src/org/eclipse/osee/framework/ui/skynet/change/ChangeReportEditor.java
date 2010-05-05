/*
 * Created on Apr 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.ITransactionsDeletedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadAssociatedArtifactOperation;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadChangesOperation;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.progress.UIJob;

public class ChangeReportEditor extends FormEditor implements IChangeReportView {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditor";

   private ChangeReportPage changeReportPage;
   private ChangeReportActionBarContributor actionBarContributor;
   private final EventRelay eventRelay;
   private final ChangeReportEditorPreferences preferences;

   public ChangeReportEditor() {
      super();
      this.eventRelay = new EventRelay();
      this.preferences = new ChangeReportEditorPreferences(EDITOR_ID);
   }

   public IChangeReportPreferences getPreferences() {
      return preferences;
   }

   public ChangeReportActionBarContributor getActionBarContributor() {
      if (actionBarContributor == null) {
         actionBarContributor = new ChangeReportActionBarContributor(this);
      }
      return actionBarContributor;
   }

   @Override
   public ChangeReportEditorInput getEditorInput() {
      return (ChangeReportEditorInput) super.getEditorInput();
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (changeReportPage != null) {
         changeReportPage.showBusy(busy);
      }
   }

   @Override
   protected void addPages() {
      OseeContributionItem.addTo(this, true);
      setPartName(getEditorInput().getName());
      setTitleImage(getEditorInput().getImage());
      try {
         changeReportPage = new ChangeReportPage(this);
         addPage(changeReportPage);
      } catch (PartInitException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      OseeContributionItem.addTo(this, true);
      OseeEventManager.addListener(eventRelay);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == IActionable.class) {
         return new IActionable() {
            @Override
            public String getActionDescription() {
               StringBuilder sb = new StringBuilder();
               ChangeUiData changeData = getEditorInput().getChangeData();
               if (changeData.isBranchValid()) {
                  sb.append(String.format("\nBranch: [%s]", changeData.getBranch()));
               }
               if (changeData.isTransactionValid()) {
                  sb.append(String.format("\nTransaction Id: [%s]", changeData.getTransaction().getId()));
               }
               return sb.toString();
            }
         };
      }
      return super.getAdapter(adapter);
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
   }

   @Override
   public void doSaveAs() {
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void recomputeChangeReport() {
      refreshData(true);
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(eventRelay);
      getPreferences().saveState();
      super.dispose();
   }

   public void refreshData(boolean isReloadAllowed) {
      List<IOperation> ops = new ArrayList<IOperation>();
      ChangeUiData changeData = getEditorInput().getChangeData();
      if (isReloadAllowed) {
         changeData.reset();
         changeReportPage.onLoad();
         ops.add(new LoadChangesOperation(changeData));
      }
      ops.add(new LoadAssociatedArtifactOperation(changeData));
      IOperation operation = new CompositeOperation("Load Change Report Data", SkynetGuiPlugin.PLUGIN_ID, ops);
      Operations.executeAsJob(operation, true, Job.LONG, new ReloadJobChangeAdapter());
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {
      private long startTime = 0;

      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
         getActionBarContributor().getReloadAction().setEnabled(false);
         showBusy(true);
      }

      @Override
      public void aboutToRun(IJobChangeEvent event) {
         super.aboutToRun(event);
         startTime = System.currentTimeMillis();
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         String message = String.format("Change Report Load completed in [%s]", Lib.getElapseString(startTime));
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, message);

         Job job = new UIJob("Refresh Change Report") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               changeReportPage.refresh();
               showBusy(false);
               getActionBarContributor().getReloadAction().setEnabled(true);
               getActionBarContributor().getOpenAssociatedArtifactAction().updateEnablement();
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   private final class EventRelay implements IBranchEventListener, ITransactionsDeletedEventListener {

      @Override
      public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) throws OseeCoreException {
      }

      @Override
      public void handleBranchEvent(Sender sender, BranchEventType branchModType, final int branchId) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         if (changeUiData.isBranchValid()) {
            Branch branch = changeUiData.getBranch();
            if (branch.getId() == branchId) {
               if (branchModType == BranchEventType.Deleted && branchModType == BranchEventType.Purged) {
                  close(false);
               } else if (branchModType == BranchEventType.Committed) {
                  recomputeChangeReport();
               }
            }
         }
      }

      @Override
      public void handleTransactionsDeletedEvent(Sender sender, int[] transactionIds) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         if (changeUiData.isTransactionValid()) {
            int transactionIdToMatch = changeUiData.getTransaction().getId();
            for (int txId : transactionIds) {
               if (transactionIdToMatch == txId) {
                  close(false);
                  break;
               }
            }
         }
      }
   }
}
