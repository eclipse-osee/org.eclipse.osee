/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.editor;

import java.util.logging.Level;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.blam.AbstractCoverageBlam;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamInputSection;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamOutputSection;
import org.eclipse.osee.framework.ui.skynet.blam.sections.BlamUsageSection;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorImportTab extends FormPage {

   private final CoverageEditor coverageEditor;
   private final CoveragePackageBase coveragePackageBase;
   private XComboViewer combo;
   private BlamUsageSection blamUsageSection;
   private BlamInputSection blamInputSection;
   private BlamOutputSection blamOutputSection;
   private CoverageImport coverageImport;
   private CoverageEditorCoverageTab coverageImportTab;
   private CoverageEditorOverviewTab coverageImportOverviewTab;
   private CoverageEditorMergeTab coverageEditorMergeTab;
   private int coverageImportIndex, coverageImportOverviewIndex, coverageEditorMergeIndex;
   private Composite destroyableComposite;
   private boolean isSimulateImput = false;
   public static String PAGE_ID = "coverage.import";

   public CoverageEditorImportTab(CoverageEditor coverageEditor) throws OseeCoreException {
      super(coverageEditor, "Import", "Import");
      this.coverageEditor = coverageEditor;
      this.coveragePackageBase = coverageEditor.getCoveragePackageBase();
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm scrolledForm = managedForm.getForm();
      scrolledForm.setText(coveragePackageBase.getName());
      scrolledForm.setImage(ImageManager.getImage(CoverageUtil.getCoveragePackageBaseImage(coveragePackageBase)));

      scrolledForm.getBody().setLayout(ALayout.getZeroMarginLayout());
      CoverageEditor.addToToolBar(scrolledForm.getToolBarManager(), coverageEditor);

      scrolledForm.getBody().setLayout(new GridLayout(1, false));
      scrolledForm.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      managedForm.getMessageManager().setAutoUpdate(false);

      combo = new XComboViewer("Select Import Blam");
      combo.setLabelProvider(labelProvider);
      combo.setContentProvider(new ArrayTreeContentProvider());
      combo.createWidgets(managedForm, scrolledForm.getBody(), 1);
      combo.setInput(Collections.castAll(CoverageManager.getCoverageBlams()));
      combo.getCombo().setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
      combo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            createBlamSections();
         }
      });

      createDestroyableComposite();

   }

   public void simulateImport(String importName) throws OseeCoreException {
      if (!Strings.isValid(importName)) {
         throw new OseeStateException("Invalid importName [%s]", importName);
      }
      AbstractCoverageBlam blam = null;
      for (AbstractCoverageBlam abstractCoverageBlam : CoverageManager.getCoverageBlams()) {
         if (abstractCoverageBlam.getName().equals(importName)) {
            blam = abstractCoverageBlam;
         }
      }
      if (blam == null) {
         throw new OseeArgumentException("Can't find blam matching name [%s]", importName);
      }
      blam.setCoverageEditor(coverageEditor);
      combo.getComboViewer().setSelection(new StructuredSelection(blam), true);
      createBlamSections();
      blamOutputSection.simluateRun();
      isSimulateImput = true;
   }

   public void simulateImportSearch() throws OseeCoreException {
      coverageEditorMergeTab.simulateSearchAll();
   }

   private AbstractCoverageBlam getBlam() {
      if (combo.getSelected() != null) {
         AbstractCoverageBlam blam = (AbstractCoverageBlam) combo.getSelected();
         blam.setCoverageEditor(coverageEditor);
         return blam;
      }
      return null;
   }

   private void createDestroyableComposite() {
      if (destroyableComposite != null) {
         destroyableComposite.dispose();
      }
      destroyableComposite =
         getManagedForm().getToolkit().createComposite(getManagedForm().getForm().getBody(), SWT.NONE);
      destroyableComposite.setLayout(new GridLayout());
      destroyableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

   }

   private void createBlamSections() {
      if (blamUsageSection != null) {
         if (blamUsageSection != null) {
            getManagedForm().removePart(blamUsageSection);
         }
         blamUsageSection.dispose();
         if (blamInputSection != null) {
            getManagedForm().removePart(blamInputSection);
            blamInputSection.dispose();
         }
         if (blamOutputSection != null) {
            getManagedForm().removePart(blamOutputSection);
            blamOutputSection.dispose();
         }
         createDestroyableComposite();
      }
      int sectionStyle = ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE;
      blamUsageSection =
         new BlamUsageSection(getEditor(), getBlam(), destroyableComposite, getManagedForm().getToolkit(), sectionStyle);

      blamInputSection =
         new BlamInputSection(getEditor(), getBlam(), destroyableComposite, getManagedForm().getToolkit(), sectionStyle);

      blamOutputSection =
         new BlamOutputSection(getEditor(), getBlam(), destroyableComposite, getManagedForm().getToolkit(),
            sectionStyle, new ExecuteBlamAction());

      getManagedForm().addPart(blamUsageSection);
      getManagedForm().addPart(blamInputSection);
      blamInputSection.getSection().setExpanded(true);
      getManagedForm().addPart(blamOutputSection);
      blamInputSection.getSection().setExpanded(true);

      getManagedForm().refresh();
      getManagedForm().getForm().layout();

   }
   private final class BlamEditorExecutionAdapter extends JobChangeAdapter {
      private long startTime = 0;
      private final String name;

      public BlamEditorExecutionAdapter(String name) {
         this.name = name;
      }

      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
         showBusy(true);
         blamOutputSection.setText("Importing...\n");
      }

      @Override
      public void aboutToRun(IJobChangeEvent event) {
         super.aboutToRun(event);
         startTime = System.currentTimeMillis();
         blamOutputSection.setText(String.format("Starting [%s] BLAM at [%s]\n", name, Lib.getElapseString(startTime)));
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
      }
   }

   public final class ExecuteBlamAction extends Action {

      public ExecuteBlamAction() {
         super("Run BLAM in Job", IAction.AS_PUSH_BUTTON);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.RUN_EXC));
         setToolTipText("Executes the BLAM Operation");
      }

      @Override
      public void run() {
         try {
            if (coverageEditorMergeTab != null) {
               if (coverageEditorMergeIndex != 0) {
                  try {
                     coverageEditor.removePage(coverageEditorMergeIndex);
                  } catch (AssertionFailedException ex) {
                     // page already removed; do nothing
                  }
                  coverageEditorMergeIndex = 0;
               }
            }
            if (coverageImportTab != null) {
               if (coverageImportIndex != 0) {
                  try {
                     coverageEditor.removePage(coverageImportIndex);
                  } catch (AssertionFailedException ex) {
                     // page already removed; do nothing
                  }
                  coverageImportIndex = 0;
               }
            }
            if (coverageImportOverviewTab != null) {
               if (coverageImportOverviewIndex != 0) {
                  try {
                     coverageEditor.removePage(coverageImportOverviewIndex);
                  } catch (AssertionFailedException ex) {
                     // page already removed; do nothing
                  }
                  coverageImportOverviewIndex = 0;
               }
            }
            coverageImport = null;

            getBlam().execute(getBlam().getName(), blamOutputSection.getOutput(), blamInputSection.getData(),
               new BlamEditorExecutionAdapter(getBlam().getName()));
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void createImportParts() {
      coverageImportOverviewTab = new CoverageEditorOverviewTab("Import Overview", coverageEditor, coverageImport);
      coverageImportOverviewIndex = coverageEditor.addFormPage(coverageImportOverviewTab);

      coverageImportTab =
         new CoverageEditorCoverageTab(String.format("Import Items (%d)", coverageImport.getCoverageItems().size()),
            coverageEditor, coverageImport);
      coverageImportIndex = coverageEditor.addFormPage(coverageImportTab);

      try {
         coverageEditorMergeTab =
            new CoverageEditorMergeTab("Import Merge", coverageEditor,
               (CoveragePackage) coverageEditor.getCoveragePackageBase(), coverageImport);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      coverageEditorMergeIndex = coverageEditor.addFormPage(coverageEditorMergeTab);
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

      @Override
      public Image getImage(Object element) {
         return null;
      }

      @Override
      public String getText(Object element) {
         if (element instanceof AbstractBlam) {
            return ((AbstractBlam) element).getName();
         }
         return "Unknown";
      }

      @Override
      public void addListener(ILabelProviderListener listener) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener listener) {
         // do nothing
      }

   };

   public CoverageImport getCoverageImport() {
      return coverageImport;
   }

   /**
    * This method is called at the end of the import blam being run
    */
   public void setCoverageImportResults(final String blamName, final CoverageImport coverageImport) {
      this.coverageImport = coverageImport;
      showBusy(false);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            blamOutputSection.appendText("BLAM completed\n");
            coverageImport.setBlamName(blamName);
            createImportParts();

            if (isSimulateImput) {
               Thread thread = new Thread() {
                  @Override
                  public void run() {
                     try {
                        Thread.sleep(1000);
                        Displays.ensureInDisplayThread(new Runnable() {
                           @Override
                           public void run() {
                              try {
                                 coverageEditor.simulateImportPostRun();
                                 isSimulateImput = false;
                              } catch (OseeCoreException ex) {
                                 OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                              }
                           }
                        });
                     } catch (InterruptedException ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
               };
               thread.start();
            }
         }
      });
   }

   @Override
   public String getId() {
      return PAGE_ID;
   }

}
