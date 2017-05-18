/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change.view;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInput;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadAssociatedArtifactOperation;
import org.eclipse.osee.framework.ui.skynet.change.operations.LoadChangesOperation;
import org.eclipse.osee.framework.ui.skynet.change.operations.UpdateChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.presenter.ChangeReportInfoPresenter;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeXViewer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Ryan D. Brooks
 */
public class ChangeReportPage extends FormPage {

   private ChangeReportTable changeReportTable;
   private ChangeReportInfoPresenter infoPresenter;
   private ChangeReportPageViewApplicability viewApplicability;

   public ChangeReportPage(ChangeReportEditor editor) {
      super(editor, "change.report", "Change Report");
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

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.marginHeight = 10;
      layout.marginWidth = 6;
      layout.horizontalSpacing = 20;
      form.getBody().setLayout(layout);
      form.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      updateTitle(form);
      updateImage(form);

      managedForm.getMessageManager().setAutoUpdate(false);

      ChangeUiData uiData = getEditorInput().getChangeData();
      changeReportTable = new ChangeReportTable(uiData);
      infoPresenter = new ChangeReportInfoPresenter(new ChangeReportInfo(), uiData);
      viewApplicability = new ChangeReportPageViewApplicability(getEditor(), toolkit, form);

      int sectionStyle = ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE;

      managedForm.addPart(
         new EditorSection(infoPresenter, "Info", form.getBody(), managedForm.getToolkit(), sectionStyle, false));
      viewApplicability.create();
      // Can not place table in section or double scroll-bars and maintaining correct table size becomes an issue
      changeReportTable.onCreate(getManagedForm(), form.getBody());

      addToolBar(toolkit, form, true);
      form.reflow(true);

      bindMenu();

      recomputeChangeReport(uiData.isLoadOnOpenEnabled());
      HelpUtil.setHelp(form.getBody(), OseeHelpContext.CHANGE_REPORT_EDITOR);
   }

   private void bindMenu() {
      final ChangeXViewer xviewer = changeReportTable.getXViewer();

      MenuManager manager = xviewer.getMenuManager();
      manager.setRemoveAllWhenShown(true);
      manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      manager.addMenuListener(new ChangeReportMenuListener(xviewer));

      Control control = xviewer.getTree();
      Menu menu = manager.createContextMenu(control);
      control.setMenu(menu);

      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeView", manager,
         xviewer);
      getSite().setSelectionProvider(changeReportTable.getXViewer());
   }

   private static final class ChangeReportMenuListener implements IMenuListener {

      private final ChangeXViewer xviewer;

      private ChangeReportMenuListener(ChangeXViewer xviewer) {
         this.xviewer = xviewer;
      }

      @Override
      public void menuAboutToShow(IMenuManager manager) {
         MenuManager menuManager = xviewer.getMenuManager();
         if (menuManager.find(XViewer.MENU_GROUP_PRE) != null) {
            menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      }
   }

   private void updateTitle(ScrolledForm form) {
      form.setText(Strings.escapeAmpersands(getEditorInput().getName()));
   }

   private void updateImage(ScrolledForm form) {
      form.setImage(getEditor().getEditorInput().getImage());
   }

   private void addToolBar(FormToolkit toolkit, ScrolledForm form, boolean add) {
      IToolBarManager manager = form.getToolBarManager();
      if (add) {
         getEditor().getChangeReportActionBarContributor().contributeToToolBar(manager);
         manager.add(changeReportTable.getXViewer().getCustomizeAction());
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

   public void onLoad() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (changeReportTable != null && infoPresenter != null) {
               changeReportTable.onLoading();
               infoPresenter.onLoading();
            }
         }
      });
   }

   public void refresh() {
      final ScrolledForm sForm = getManagedForm().getForm();
      if (Widgets.isAccessible(sForm)) {
         for (IFormPart part : getManagedForm().getParts()) {
            part.refresh();
         }
         changeReportTable.onUpdate();

         updateTitle(sForm);
         updateImage(sForm);

         sForm.getBody().layout(true);
         sForm.reflow(true);
         getManagedForm().refresh();
      }
   }

   public void recomputeChangeReport(boolean isReloadAllowed) {
      OperationBuilder builder = Operations.createBuilder("Load Change Report Data");
      ChangeUiData changeData = getEditorInput().getChangeData();
      builder.addOp(new UpdateChangeUiData(changeData));
      if (isReloadAllowed) {
         changeData.reset();
         onLoad();
         builder.addOp(new LoadChangesOperation(changeData));
      }
      builder.addOp(new LoadAssociatedArtifactOperation(changeData));
      Operations.executeAsJob(builder.build(), true, Job.LONG, new ReloadJobChangeAdapter());
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {
      private long startTime = 0;

      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
         getEditor().getChangeReportActionBarContributor().getReloadAction().setEnabled(false);
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
         OseeLog.log(Activator.class, Level.INFO, message);

         Job job = new UIJob("Refresh Change Report") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               if (Widgets.isAccessible(getManagedForm().getForm())) {
                  getEditor().refresh();
                  getEditor().getChangeReportActionBarContributor().getReloadAction().setEnabled(true);
                  getEditor().getChangeReportActionBarContributor().getOpenAssociatedArtifactAction().updateEnablement();
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

   public void setSelectionProvider() {
      getSite().setSelectionProvider(changeReportTable.getXViewer());
   }

}