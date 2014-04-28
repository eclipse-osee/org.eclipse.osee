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
package org.eclipse.osee.coverage.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.event.CoverageEventManager;
import org.eclipse.osee.coverage.help.ui.CoverageHelpContext;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditor extends FormEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.coverage.editor.CoverageEditor";
   private Integer startPage = null;
   private CoverageEditorImportTab coverageEditorImportTab = null;
   private CoverageEditorCoverageTab coverageEditorCoverageTab = null;
   private CoverageEditorOverviewTab coverageEditorOverviewTab = null;
   private CoverageEditorLoadingTab coverageEditorLoadingTab = null;
   private CoverageEditorWorkProductTab coverageEditorWorkProductTab = null;

   @Override
   protected void addPages() {
      try {
         OseeStatusContributionItemFactory.addTo(this, true);
         String loadingStr = "Loading Coverage Package \"" + getCoverageEditorInput().getPreLoadName() + "\" ...";
         coverageEditorLoadingTab = new CoverageEditorLoadingTab(loadingStr, this);
         addFormPage(coverageEditorLoadingTab);
         setPartName("Loading " + getCoverageEditorInput().getPreLoadName());
         setTitleImage(ImageManager.getImage(CoverageImage.COVERAGE));
         setActivePage(startPage);

         IOperation operation = new LoadCoverageEditorOperation(this, loadingStr);
         if (getCoverageEditorInput().isInTest()) {
            Operations.executeWorkAndCheckStatus(operation);
         } else {
            Operations.executeAsJob(operation, true);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      try {
         HelpUtil.setHelp(this.getContainer(), CoverageHelpContext.EDITOR);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void onLoadComplete() throws OseeCoreException {
      if (getCoverageEditorInput().isInTest()) {
         addPagesAfterLoad();
      } else {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               addPagesAfterLoad();
            }
         });
      }
   }

   private void addPagesAfterLoad() {
      try {
         // remove loading page
         removePage(0);
         coverageEditorOverviewTab = new CoverageEditorOverviewTab("Overview", this, getCoveragePackageBase());
         addFormPage(coverageEditorOverviewTab);
         coverageEditorCoverageTab = new CoverageEditorCoverageTab("Coverage Items", this, getCoveragePackageBase());
         addFormPage(coverageEditorCoverageTab);
         if (getCoveragePackageBase() instanceof CoveragePackage) {
            coverageEditorWorkProductTab =
               new CoverageEditorWorkProductTab("Work Product Tracking", this,
                  (CoveragePackage) getCoveragePackageBase());
            addFormPage(coverageEditorWorkProductTab);
         }
         if (getCoveragePackageBase().isImportAllowed()) {
            coverageEditorImportTab = new CoverageEditorImportTab(this);
            addFormPage(coverageEditorImportTab);
         }
         setPartName(getCoveragePackageBase().getName());
         setTitleImage(ImageManager.getImage(CoverageUtil.getCoveragePackageBaseImage(getCoveragePackageBase())));

         setActivePage(startPage);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void simulateImport(String importName) throws OseeCoreException {
      if (coverageEditorImportTab == null) {
         throw new OseeStateException("Import page == null");
      }
      setActivePage(CoverageEditorImportTab.PAGE_ID);
      coverageEditorImportTab.simulateImport(importName);
   }

   public void simulateImportPostRun() throws OseeCoreException {
      setActivePage(CoverageEditorMergeTab.PAGE_ID);
      coverageEditorImportTab.simulateImportSearch();
   }

   public int addFormPage(FormPage page) {
      int pageIndex = 0;
      try {
         pageIndex = addPage(page);
         if (startPage == null) {
            startPage = pageIndex;
         }
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return pageIndex;
   }

   public void setEditorTitle(final String str) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            setPartName(str);
            firePropertyChange(IWorkbenchPart.PROP_TITLE);
         }
      });
   }

   public CoveragePackageBase getCoveragePackageBase() throws OseeCoreException {
      return getCoverageEditorInput().getCoveragePackageBase();
   }

   public CoverageEditorInput getCoverageEditorInput() throws OseeCoreException {
      IEditorInput editorInput = getEditorInput();
      if (!(editorInput instanceof CoverageEditorInput)) {
         throw new OseeArgumentException("Editor Input not CoverageEditorInput");
      }
      return (CoverageEditorInput) getEditorInput();
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      // do nothing
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   public void refreshTitle() {
      firePropertyChange(IWorkbenchPart.PROP_TITLE);
   }

   @Override
   public void dispose() {
      CoverageEventManager.instance.unregister(this);
      super.dispose();
   }

   @Override
   public boolean isDirty() {
      return false;
   }

   public static void open(final CoverageEditorInput coverageEditorInput) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(coverageEditorInput, EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public void closeEditor() {
      final MultiPageEditorPart editor = this;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(editor, false);
         }
      });
   }

   public static Collection<CoverageEditor> getEditors() {
      final List<CoverageEditor> editors = new ArrayList<CoverageEditor>();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((CoverageEditor) editor.getEditor(false));
            }
         }
      });
      return editors;
   }

   public void refresh(ICoverage coverage) {
      coverageEditorCoverageTab.refresh(coverage);
      coverageEditorOverviewTab.refreshActionHandler();
   }

   public void refreshWorkProductTasks() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            coverageEditorCoverageTab.refresh();
            if (coverageEditorWorkProductTab != null) {
               coverageEditorWorkProductTab.refresh();
            }
            coverageEditorOverviewTab.refreshActionHandler();
         }
      });
   }

   public void refreshWorkProductTab() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (coverageEditorWorkProductTab != null) {
               coverageEditorWorkProductTab.refresh();
            }
         }
      });
   }

   public static void closeAll() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               AWorkbench.getActivePage().closeEditor(editor.getEditor(false), false);
            }
         }
      });
   }

   @Override
   public void doSaveAs() {
      // do nothing
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   public CoverageEditorImportTab getCoverageEditorImportTab() {
      return coverageEditorImportTab;
   }

   public CoverageEditorOverviewTab getCoverageEditorOverviewTab() {
      return coverageEditorOverviewTab;
   }

   public IOseeBranch getBranch() throws OseeCoreException {
      Artifact artifact = getCoverageEditorInput().getCoveragePackageArtifact();
      if (artifact != null) {
         return artifact.getBranch();
      }
      return null;
   }

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input);
   }
}
