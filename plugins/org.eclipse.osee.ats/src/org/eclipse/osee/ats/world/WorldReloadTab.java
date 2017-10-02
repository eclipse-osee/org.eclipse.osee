package org.eclipse.osee.ats.world;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.internal.Activator;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author Donald G. Dunne
 */
public class WorldReloadTab extends FormPage {
   private IManagedForm managedForm;
   private Composite bodyComp;
   private Composite atsBody;
   public final static String ID = "ats.world.reload.tab";
   private final WorldEditor editor;
   private final WorldEditorReloadProvider provider;

   public WorldReloadTab(WorldEditor editor, WorldEditorReloadProvider provider) {
      super(editor, ID, "Reload");
      this.editor = editor;
      this.provider = provider;
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

         if (provider.getValidArtUuids().isEmpty()) {
            managedForm.getToolkit().createLabel(bodyComp, "Nothing to reload.");
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
      if (Widgets.isAccessible(atsBody)) {
         atsBody.dispose();
      }
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   private void updateTitleBar()  {
      String displayableTitle = Strings.escapeAmpersands(provider.getName());
      if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
         managedForm.getForm().setText(displayableTitle);
         managedForm.getForm().setImage(ImageManager.getImage(AtsImage.GLOBE));
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
      final LoadAndRefreshJob loadAndRefresh = new LoadAndRefreshJob(provider.getName());
      Jobs.startJob(loadAndRefresh, false, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  if (loadAndRefresh.isSuccess()) {
                     provider.setReload(false);
                     editor.addPages();
                     editor.removePage(0);
                  }
               }
            });
         }

      });
   }

   private class LoadAndRefreshJob extends Job {

      boolean success = true;

      public boolean isSuccess() {
         return success;
      }

      public LoadAndRefreshJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         if (provider.getValidArtUuids().isEmpty()) {
            AWorkbench.popup("No valid ids to reload.");
            success = false;
         } else {
            success = provider.searchAndLoad();
         }
         return Status.OK_STATUS;
      }

   };

}