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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.ElapsedTime;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditor extends FormEditor implements IActionable, IFrameworkTransactionEventListener {
   public static final String EDITOR_ID = "org.eclipse.osee.coverage.editor.CoverageEditor";
   private Integer startPage = null;
   private CoverageEditorImportTab coverageEditorImportTab = null;
   private CoverageEditorCoverageTab coverageEditorCoverageTab = null;
   private CoverageEditorOverviewTab coverageEditorOverviewTab = null;
   private CoverageEditorLoadingTab coverageEditorLoadingTab = null;
   private Collection<Artifact> artifactLoadCache = null;

   @Override
   protected void addPages() {
      try {
         OseeContributionItem.addTo(this, true);
         String loadingStr = "Loading Coverage Package \"" + getCoverageEditorInput().getPreLoadName() + "\" ...";
         coverageEditorLoadingTab = new CoverageEditorLoadingTab(loadingStr, this);
         addFormPage(coverageEditorLoadingTab);
         setPartName("Loading " + getCoverageEditorInput().getPreLoadName());
         setTitleImage(ImageManager.getImage(CoverageImage.COVERAGE));
         setActivePage(startPage);
         if (getCoverageEditorInput().isInTest()) {
            new LoadCoverage(loadingStr).doWork(null);
         } else {
            Operations.executeAsJob(new LoadCoverage(loadingStr), true);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private class LoadCoverage extends AbstractOperation {

      public LoadCoverage(String operationName) {
         super(operationName, Activator.PLUGIN_ID);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         if (artifactLoadCache == null) {
            System.out.println("Get Package Artifact " + XDate.getTimeStamp());
            if (getCoverageEditorInput().getCoveragePackageArtifact() != null) {
               try {
                  ElapsedTime elapsedTime = new ElapsedTime("Coverage - bulk load");
                  artifactLoadCache =
                        RelationManager.getRelatedArtifacts(
                              Collections.singleton(getCoverageEditorInput().getCoveragePackageArtifact()), 8,
                              CoreRelationTypes.Default_Hierarchical__Child);
                  elapsedTime.end();
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
               }
            } else {
               artifactLoadCache = Collections.emptyList();
            }
         }
         if (getCoverageEditorInput().getCoveragePackageArtifact() != null) {
            ElapsedTime elapsedTime = new ElapsedTime("Coverage - load model");
            CoveragePackage coveragePackage =
                  OseeCoveragePackageStore.get(getCoverageEditorInput().getCoveragePackageArtifact());
            getCoverageEditorInput().setCoveragePackageBase(coveragePackage);
            elapsedTime.end();
         }

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
   };

   protected void addPagesAfterLoad() {
      try {
         // remove loading page
         removePage(0);
         System.out.println("addPagesAfterLoad " + XDate.getTimeStamp());

         coverageEditorOverviewTab = new CoverageEditorOverviewTab("Overview", this, getCoveragePackageBase());
         addFormPage(coverageEditorOverviewTab);
         coverageEditorCoverageTab = new CoverageEditorCoverageTab("Coverage Items", this, getCoveragePackageBase());
         addFormPage(coverageEditorCoverageTab);
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
      setActivePage(2);
      coverageEditorImportTab.simulateImport(importName);
   }

   public void simulateImportPostRun() throws OseeArgumentException {
      setActivePage(5);
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

   public static void addToToolBar(IToolBarManager manager, CoverageEditor coverageEditor) {
      manager.add(OseeAts.createBugAction(SkynetGuiPlugin.getInstance(), coverageEditor, EDITOR_ID, "Lba Code Promote"));
      manager.update(true);
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

   public CoveragePackageBase getCoveragePackageBase() {
      return getCoverageEditorInput().getCoveragePackageBase();
   }

   public CoverageEditorInput getCoverageEditorInput() {
      IEditorInput editorInput = getEditorInput();
      if (!(editorInput instanceof CoverageEditorInput)) {
         throw new IllegalArgumentException("Editor Input not CoverageEditorInput");
      }
      return (CoverageEditorInput) getEditorInput();
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
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
      super.dispose();
   }

   @Override
   public boolean isDirty() {
      return false;
   }

   public static void open(final CoverageEditorInput coverageEditorInput) throws OseeCoreException {
      open(coverageEditorInput, false);
   }

   public static void open(final CoverageEditorInput coverageEditorInput, boolean forcePend) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(coverageEditorInput, EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }, forcePend);
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
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((CoverageEditor) editor.getEditor(false));
            }
         }
      }, true);
      return editors;
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

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (IActionable.class.equals(adapter)) {
         return new IActionable() {
            @Override
            public String getActionDescription() {
               return "";
            }
         };
      }
      return super.getAdapter(adapter);
   }

   @Override
   public String getActionDescription() {
      return null;
   }

   @Override
   public void doSaveAs() {
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      System.out.println("check and close editor");
   }

   public CoverageEditorImportTab getCoverageEditorImportTab() {
      return coverageEditorImportTab;
   }

   public CoverageEditorOverviewTab getCoverageEditorOverviewTab() {
      return coverageEditorOverviewTab;
   }
}
