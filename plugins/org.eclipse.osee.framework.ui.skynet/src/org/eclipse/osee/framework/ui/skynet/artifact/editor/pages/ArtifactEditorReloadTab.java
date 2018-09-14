/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor.pages;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEditorReloadTab extends FormPage {
   private IManagedForm managedForm;
   private Composite bodyComp;
   private Composite atsBody;
   public final static String ID = "artifact.editor.reload.tab";
   private final ArtifactEditor editor;
   private final String title;
   private final ArtifactId artifactId;
   private final BranchId branchId;

   public ArtifactEditorReloadTab(ArtifactEditor editor) {
      super(editor, ID, "Reload");
      this.editor = editor;
      this.artifactId = editor.getEditorInput().getSavedArtUuid();
      this.title = editor.getEditorInput().getSavedTitle();
      this.branchId = editor.getEditorInput().getSavedBranchId();
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

         FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);
      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void handleException(Exception ex) {
      if (Widgets.isAccessible(atsBody)) {
         atsBody.dispose();
      }
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   private void updateTitleBar() throws OseeCoreException {
      String displayableTitle = Strings.escapeAmpersands(title);
      if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
         managedForm.getForm().setText(displayableTitle);
         managedForm.getForm().setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EDITOR));
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
                     editor.getEditorInput().setArtifact(artifact);
                     bodyComp.dispose();
                     page.dispose();
                     editor.performLoad();
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
         if (artifactId.isValid()) {
            try {
               artifact = ArtifactQuery.getArtifactFromId(artifactId, branchId, DeletionFlag.INCLUDE_DELETED);
            } catch (ArtifactDoesNotExist ex) {
               // do nothing
            }
         }
         return Status.OK_STATUS;
      }

      public Artifact getArtifact() {
         return artifact;
      }
   };

}
