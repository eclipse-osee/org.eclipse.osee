/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.ui.skynet.markedit.edit;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.core.operation.EmptyOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.AttributeFormPart;
import org.eclipse.osee.framework.ui.skynet.markedit.HorizontalAction;
import org.eclipse.osee.framework.ui.skynet.markedit.OmeAbstractTab;
import org.eclipse.osee.framework.ui.skynet.markedit.html.OmeHtmlComposite;
import org.eclipse.osee.framework.ui.skynet.markedit.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.markedit.model.ArtOmeData;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class OmeEditTab extends OmeAbstractTab {

   private OmeEditComposite editComposite;
   private IManagedForm managedForm;
   private final AbstractOmeData omeData;
   private boolean horizontal = false;
   private OmeHtmlComposite htmlComposite;
   private SashForm sashForm;

   public OmeEditTab(FormEditor editor, AbstractOmeData omeData) {
      super(editor, "ome.editor.edit", omeData, "Markdown Edit");
      this.omeData = omeData;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;

      try {
         updateTitleBar(managedForm);

         bodyComp = managedForm.getForm().getBody();
         GridLayout gridLayout = new GridLayout(1, false);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, false);
         gd.widthHint = 300;
         bodyComp.setLayoutData(gd);

         setLoading(true);

         List<IOperation> ops = new ArrayList<>();
         ops.add(new EmptyOperation());
         IOperation operation = Operations.createBuilder("Load Edit Tab").addAll(ops).build();
         Operations.executeAsJob(operation, false, Job.LONG, new ReloadJobChangeAdapter(editor));

      } catch (Exception ex) {
         handleException(ex);
      }
   }

   @Override
   public FormEditor getEditor() {
      return editor;
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {
      private final FormEditor editor;

      private ReloadJobChangeAdapter(FormEditor editor) {
         this.editor = editor;
         showBusy(true);
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         Job job = new UIJob("Draw Edit Tab") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
                     updateTitleBar(managedForm);
                     createToolbar(managedForm);
                     setLoading(false);
                     createBody();
                     XWidgetUtility.addMessageDecoration(managedForm, managedForm.getForm());
                     FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);
                     ((IDirtiableEditor) editor).onDirtied();
                  }
               } catch (OseeCoreException ex) {
                  handleException(ex);
               } finally {
                  showBusy(false);
               }

               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   protected void createBody() {
      OmeEditSubToolbar subToolbar = new OmeEditSubToolbar(bodyComp, this);
      subToolbar.create();
      getManagedForm().getToolkit().adapt(subToolbar);

      createBodyFull();
   }

   private void createBodyFull() {
      if (!horizontal) {
         createEditComposite(bodyComp);
      } else {
         sashForm = new SashForm(bodyComp, SWT.HORIZONTAL);
         createEditComposite(sashForm);

         htmlComposite = new OmeHtmlComposite(sashForm, SWT.BORDER, omeData);
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.widthHint = 100;
         htmlComposite.setLayoutData(gd);
      }

      if (editor instanceof ArtifactEditor) {
         ArtifactEditor artEditor = (ArtifactEditor) editor;
         artEditor.getAttributeFormSection().getAttributeFormPart().addXWidgetValidationListener(
            editComposite.getText());
      }
      AttributeFormPart.computeXTextSize(editComposite.getText());
      bodyComp.requestLayout();
      if (Widgets.isAccessible(sashForm)) {
         sashForm.redraw();
      }
      managedForm.reflow(true);
   }

   private void createEditComposite(Composite bodyComp) {
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.widthHint = 100;
      editComposite = new OmeEditComposite(bodyComp, SWT.BORDER, this, omeData);
      editComposite.setLayoutData(gd);
      if (omeData instanceof ArtOmeData) {
         new OmeEditTabDrop(this, (ArtOmeData) omeData).setDropTarget();
      }
   }

   @Override
   public String getTabName() {
      return "MD Edit";
   }

   @Override
   public boolean isDirty() {
      if (Widgets.isAccessible(editComposite)) {
         return editComposite.isDirty();
      }
      return false;
   }

   public void doSave() {
      if (Widgets.isAccessible(editComposite)) {
         editComposite.doSave();
      }
   }

   public OmeEditComposite getEditComposite() {
      return editComposite;
   }

   public void appendText(String text) {
      editComposite.appendText(text);
   }

   @Override
   public IToolBarManager createToolbar(IManagedForm managedForm) {
      IToolBarManager toolBarMgr = super.createToolbar(managedForm);
      toolBarMgr.add(new HorizontalAction(this));
      managedForm.getForm().updateToolBar();
      return toolBarMgr;
   }

   public void toggleHorizontal() {
      horizontal = !horizontal;
      if (Widgets.isAccessible(editComposite)) {
         editComposite.dispose();
      }
      if (Widgets.isAccessible(sashForm)) {
         sashForm.dispose();
      }
      createBodyFull();
      if (Widgets.isAccessible(htmlComposite)) {
         htmlComposite.handleRefreshAction(omeData);
      }
      managedForm.reflow(true);
   }

}
