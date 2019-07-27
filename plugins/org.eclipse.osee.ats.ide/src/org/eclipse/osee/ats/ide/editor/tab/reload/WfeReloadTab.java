/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.reload;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author Donald G. Dunne
 */
public class WfeReloadTab extends FormPage {
   private IManagedForm managedForm;
   private Composite bodyComp;
   public final static String ID = "ats.reload.tab";
   private final WorkflowEditor editor;
   private final String title;
   private final ArtifactId artId;
   private final BranchId branch;

   public WfeReloadTab(WorkflowEditor editor) {
      super(editor, ID, "Reload");
      this.editor = editor;
      this.artId = editor.getWfeInput().getArtId();
      this.title = editor.getWfeInput().getSavedTitle();
      this.branch = editor.getWfeInput().getBranchId();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      try {
         updateTitleBar();

         bodyComp = managedForm.getForm().getBody();
         bodyComp.setLayout(new GridLayout(1, false));
         bodyComp.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, true, false));

         if (AtsClientService.get().getAtsBranch().notEqual(branch)) {
            Label imageLabel = new Label(bodyComp, SWT.NONE);
            imageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, true));
            Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            imageLabel.setImage(image);
            imageLabel.setBackground(Displays.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            imageLabel.setText("Saved item not on currently configured ATS Branch.  Unable to reload.");
         } else {
            Button reloadButton = new Button(bodyComp, SWT.PUSH);
            reloadButton.setText("Reload");
            reloadButton.setImage(ImageManager.getImage(FrameworkImage.REFRESH));
            reloadButton.addSelectionListener(new SelectionAdapter() {

               @Override
               public void widgetSelected(SelectionEvent e) {
                  loadEditor();
               }

            });
         }

         FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);
      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void handleException(Exception ex) {
      if (Widgets.isAccessible(bodyComp)) {
         bodyComp.dispose();
      }
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   private void updateTitleBar() {
      String displayableTitle = Strings.escapeAmpersands(title);
      if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
         managedForm.getForm().setText(displayableTitle);
         managedForm.getForm().setImage(ImageManager.getImage(AtsImage.TEAM_WORKFLOW));
      }
      setPartName(displayableTitle);
   }

   @Override
   public void dispose() {
      if (editor.getToolkit() != null) {
         editor.getToolkit().dispose();
      }
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      IManagedForm managedForm = getManagedForm();
      if (managedForm != null && Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   private void loadEditor() {
      LoadAndRefreshJob loadAndRefresh = new LoadAndRefreshJob(title);
      Jobs.startJob(loadAndRefresh, true);
   }

   private class LoadAndRefreshJob extends Job {

      public LoadAndRefreshJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         IAtsWorkItem workItem = editor.getWorkItem();
         if (workItem == null) {
            workItem =
               AtsClientService.get().getWorkItemService().getWorkItem(editor.getWfeInput().getSavedArtUuid().getId());
         }
         if (workItem == null) {
            AWorkbench.popup("Can't reload editor.");
            editor.closeEditor();
         }
         final IAtsWorkItem fWorkItem = workItem;
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               WorkflowEditor workflowEditor = WorkflowEditor.getWorkflowEditor(fWorkItem);
               if (workflowEditor != null) {
                  workflowEditor.closeEditor();
               }
            }
         });
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               WorkflowEditor.edit(fWorkItem);
            }
         });
         return Status.OK_STATUS;
      }

   };

}
