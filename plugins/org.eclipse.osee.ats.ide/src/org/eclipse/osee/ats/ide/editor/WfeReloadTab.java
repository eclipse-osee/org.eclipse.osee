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
package org.eclipse.osee.ats.ide.editor;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.config.AtsBulkLoad;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
            final FormPage page = this;
            reloadButton.addSelectionListener(new SelectionAdapter() {

               @Override
               public void widgetSelected(SelectionEvent e) {
                  loadEditor(page);
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

   private void loadEditor(final FormPage page) {
      LoadAndRefreshJob loadAndRefresh = new LoadAndRefreshJob(title);
      Jobs.startJob(loadAndRefresh, false, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            final Artifact artifact = ((LoadAndRefreshJob) event.getJob()).getArtifact();
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  if (artifact == null) {
                     AWorkbench.popup("Saved item is not valid.  Unable to reload.");
                  } else {
                     ((WfeInput) editor.getEditorInput()).setArtifact(artifact);
                     bodyComp.dispose();
                     page.dispose();
                     editor.addPages();
                     editor.removePage(0);
                  }
               }
            });
         }

      });
   }

   private class LoadAndRefreshJob extends Job {

      public LoadAndRefreshJob(String name) {
         super(name);
      }

      private Artifact artifact;

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         if (artId.isValid() && AtsClientService.get().getAtsBranch().equals(branch)) {
            try {
               artifact = ArtifactQuery.getArtifactFromId(artId, AtsClientService.get().getAtsBranch());
            } catch (ArtifactDoesNotExist ex) {
               // do nothing
            }
         }
         if (artifact != null) {
            AtsBulkLoad.bulkLoadArtifacts(Collections.singleton(artifact));
         }
         return Status.OK_STATUS;
      }

      public Artifact getArtifact() {
         return artifact;
      }
   };

}
