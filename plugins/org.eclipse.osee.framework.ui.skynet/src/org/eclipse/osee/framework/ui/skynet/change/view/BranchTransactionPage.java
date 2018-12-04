/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change.view;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.ui.skynet.action.EditTransactionComment;
import org.eclipse.osee.framework.ui.skynet.action.ITransactionRecordSelectionProvider;
import org.eclipse.osee.framework.ui.skynet.change.BranchTransactionUiData;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInput;
import org.eclipse.osee.framework.ui.skynet.change.actions.ShowChangeReportAction;
import org.eclipse.osee.framework.ui.skynet.change.actions.ShowChangeReportSinceAction;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadAssociatedArtifactOperation;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadBranchTransactionsOperation;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.XBranchWidget.IBranchWidgetMenuListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class BranchTransactionPage extends FormPage implements IBranchWidgetMenuListener, ITransactionEventListener, ITransactionRecordSelectionProvider, IOseeTreeReportProvider {

   private XBranchWidget xBranchWidget;
   private final BranchId branch;
   private final AtomicBoolean refreshNeeded = new AtomicBoolean(false);
   private final AtomicBoolean processEvents = new AtomicBoolean(false);
   private BranchTransactionUiData branchTransactionData;

   public BranchTransactionPage(ChangeReportEditor editor, BranchId branch) {
      super(editor, "transaction", "Transactions");
      this.branch = branch;
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm form = managedForm.getForm();
      final FormToolkit toolkit = managedForm.getToolkit();

      Composite parent = form.getBody();

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

         xBranchWidget = new XBranchWidget(new BranchTransactionXViewerFactory(this), this);
         xBranchWidget.setDisplayLabel(false);
         xBranchWidget.createWidgets(parent, 1);
         xBranchWidget.adaptControls(toolkit);
         xBranchWidget.getXViewer().setColumnMultiEditEnabled(false);

         getSite().setSelectionProvider(xBranchWidget.getXViewer());
         OseeEventManager.addListener(this);
      }

      updateTitle(form);
      updateImage(form);

      managedForm.getMessageManager().setAutoUpdate(false);

      addToolBar(toolkit, form, true);
      form.reflow(true);

      if (getEditorInput().isNotLoaded()) {
         xBranchWidget.setExtraInfoLabel("Cleared on shut down. Press refresh to reload.");
      } else {
         recomputeBranchTransactions(true);
      }

   }

   @Override
   public void updateMenuActionsForTable(MenuManager mm) {
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new ShowChangeReportSinceAction(getEditorInput().getBranch(), this));
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new ShowChangeReportAction(this));
      if (SystemGroup.OseeAdmin.isCurrentUserMember()) {
         mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         mm.insertBefore(XViewer.MENU_GROUP_PRE, new EditTransactionComment(this));
      }
   }

   public void recomputeBranchTransactions(boolean isReloadAllowed) {
      OperationBuilder builder = Operations.createBuilder("Load Branch Transactions");
      branchTransactionData = new BranchTransactionUiData(getEditorInput().getBranch(),
         (XBranchContentProvider) xBranchWidget.getXViewer().getContentProvider());
      builder.addOp(new LoadBranchTransactionsOperation(branchTransactionData));
      builder.addOp(new LoadAssociatedArtifactOperation(getEditor().getChanges()));
      Operations.executeAsJob(builder.build(), true, Job.LONG, new ReloadJobChangeAdapter());
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {
      private long startTime = 0;

      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
         getEditor().getBranchTransactionActionBarContributor().getReloadAction().setEnabled(false);
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
         String message = String.format("Branch Transactions Load completed in [%s]", Lib.getElapseString(startTime));
         final XBranchWidget fXBranchWidget = xBranchWidget;
         OseeLog.log(Activator.class, Level.INFO, message);
         Job job = new UIJob("Reload Branch Transactions") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               if (Widgets.isAccessible(getManagedForm().getForm())) {
                  fXBranchWidget.loadData(branchTransactionData);
                  getEditor().getBranchTransactionActionBarContributor().getReloadAction().setEnabled(true);
                  getEditor().getBranchTransactionActionBarContributor().getOpenAssociatedArtifactAction().updateEnablement();
                  getManagedForm().getForm().getBody().layout(true, true);
                  getManagedForm().getForm().reflow(true);
                  getManagedForm().refresh();
                  showBusy(false);
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   private void updateTitle(ScrolledForm form) {
      form.setText(Strings.escapeAmpersands(getEditorInput().getBranchTransactionName()));
   }

   private void updateImage(ScrolledForm form) {
      form.setImage(getEditor().getEditorInput().getBranchTransactionImage());
   }

   private void addToolBar(FormToolkit toolkit, ScrolledForm form, boolean add) {
      IToolBarManager manager = form.getToolBarManager();
      if (add) {
         getEditor().getBranchTransactionActionBarContributor().contributeToToolBar(manager);
         manager.add(xBranchWidget.getXViewer().getCustomizeAction());
         manager.update(true);
      } else {
         manager.removeAll();
      }
      form.reflow(true);
   }

   @Override
   public ChangeReportEditor getEditor() {
      return (ChangeReportEditor) super.getEditor();
   }

   @Override
   public ChangeReportEditorInput getEditorInput() {
      return (ChangeReportEditorInput) super.getEditorInput();
   }

   private boolean isInitialized() {
      return xBranchWidget != null;
   }

   @Override
   public List<TransactionId> getSelectedTransactionRecords() {
      return isInitialized() ? xBranchWidget.getSelectedTransactionRecords() : Collections.emptyList();
   }

   @Override
   public void refreshUI(List<TransactionId> records) {
      if (isInitialized()) {
         xBranchWidget.loadData(branch);
      }
   }

   @Override
   public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
      refreshNeeded.set(true);
      if (isInitialized() && processEvents.get()) {
         refreshIfNeeded();
      }
   }

   private void refreshIfNeeded() {
      if (refreshNeeded.compareAndSet(true, false)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  xBranchWidget.loadData(branch);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      }
   }

   public void setSelectionProvider() {
      getSite().setSelectionProvider(xBranchWidget.getXViewer());
   }

   @Override
   public String getEditorTitle() {
      try {
         return String.format("Branch Transactions - %s", branch);
      } catch (Exception ex) {
         // do nothing
      }
      return "Table Report - Branch Transactions";
   }

   @Override
   public String getReportTitle() {
      return getEditorTitle();
   }

}