/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.blam.AbstractCoverageBlam;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamInputSection;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamOutputSection;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamUsageSection;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorImportTab extends FormPage {

   private final CoverageEditor coverageEditor;
   private final ICoverageEditorProvider provider;
   private XComboViewer combo;
   private BlamUsageSection blamUsageSection;
   private BlamInputSection blamInputSection;
   private BlamOutputSection blamOutputSection;
   private CoverageImport coverageImport;

   public CoverageEditorImportTab(CoverageEditor coverageEditor) {
      super(coverageEditor, "Import", "Import");
      this.coverageEditor = coverageEditor;
      this.provider = coverageEditor.getCoverageEditorProvider();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm scrolledForm = managedForm.getForm();
      scrolledForm.setText(provider.getName());
      scrolledForm.setImage(ImageManager.getImage(provider.getTitleImage()));

      scrolledForm.getBody().setLayout(ALayout.getZeroMarginLayout());
      CoverageEditor.addToToolBar(scrolledForm.getToolBarManager(), coverageEditor);
      Composite composite = scrolledForm.getBody();
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.marginHeight = 10;
      layout.marginWidth = 6;
      layout.horizontalSpacing = 20;
      scrolledForm.getBody().setLayout(layout);
      scrolledForm.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

      managedForm.getMessageManager().setAutoUpdate(false);

      combo = new XComboViewer("Select Import Blam");
      combo.setLabelProvider(labelProvider);
      combo.setContentProvider(new ArrayTreeContentProvider());
      combo.createWidgets(managedForm, scrolledForm.getBody(), 1);
      combo.setInput(Collections.castAll(CoverageManager.getCoverageBlams()));
      combo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            createBlamSections();
         }
      });
   }

   private AbstractCoverageBlam getBlam() {
      if (combo.getSelected() != null) {
         return (AbstractCoverageBlam) combo.getSelected();
      }
      return null;
   }

   private void createBlamSections() {
      if (blamUsageSection != null) {
         blamUsageSection.dispose();
         blamInputSection.dispose();
         blamOutputSection.dispose();
      }
      int sectionStyle = Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE;
      blamUsageSection =
            new BlamUsageSection(getEditor(), getBlam(), getManagedForm().getForm().getBody(),
                  getManagedForm().getToolkit(), sectionStyle);
      blamInputSection =
            new BlamInputSection(getEditor(), getBlam(), getManagedForm().getForm().getBody(),
                  getManagedForm().getToolkit(), sectionStyle);
      blamOutputSection =
            new BlamOutputSection(getEditor(), getBlam(), getManagedForm().getForm().getBody(),
                  getManagedForm().getToolkit(), sectionStyle, new ExecuteBlamAction());

      getManagedForm().addPart(blamUsageSection);
      getManagedForm().addPart(blamInputSection);
      getManagedForm().addPart(blamOutputSection);
      getManagedForm().reflow(true);
   }

   private final class BlamEditorExecutionAdapter extends JobChangeAdapter {
      private long startTime = 0;

      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
         showBusy(true);
      }

      @Override
      public void aboutToRun(IJobChangeEvent event) {
         super.aboutToRun(event);
         startTime = System.currentTimeMillis();
         blamOutputSection.setText(String.format("Starting BLAM at [%s]\n", Lib.getElapseString(startTime)));
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         blamOutputSection.appendText(String.format("BLAM completed in [%s]\n", Lib.getElapseString(startTime)));
         showBusy(false);
      }
   }

   public final class ExecuteBlamAction extends Action {

      public ExecuteBlamAction() {
         super("Run BLAM in Job", Action.AS_PUSH_BUTTON);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.RUN_EXC));
         setToolTipText("Executes the BLAM Operation");
      }

      @Override
      public void run() {
         try {
            getBlam().execute(getBlam().getName(), blamOutputSection.getOutput(), blamInputSection.getData(),
                  new BlamEditorExecutionAdapter());
            coverageImport = getBlam().getCoverageImport();
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   @Override
   public FormEditor getEditor() {
      return super.getEditor();
   }

   static ILabelProvider labelProvider = new ILabelProvider() {

      public Image getImage(Object element) {
         return null;
      }

      public String getText(Object element) {
         if (element instanceof AbstractBlam) {
            return ((AbstractBlam) element).getName();
         }
         return "Unknown";
      }

      public void addListener(ILabelProviderListener listener) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      public void removeListener(ILabelProviderListener listener) {
      }

   };

}
