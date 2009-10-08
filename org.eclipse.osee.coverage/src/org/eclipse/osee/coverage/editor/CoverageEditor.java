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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
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
public class CoverageEditor extends FormEditor implements IActionable {
   public static final String EDITOR_ID = "org.eclipse.osee.coverage.editor.CoverageEditor";
   private Integer startPage = null;
   private CoverageEditorImportTab coverageEditorImportTab = null;
   private CoverageEditorCoverageTab coverageEditorCoverageTab = null;

   @Override
   protected void addPages() {
      try {
         OseeContributionItem.addTo(this, true);
         addFormPage(new CoverageEditorOverviewTab("Overview", this, getCoverageEditorProvider()));
         coverageEditorCoverageTab =
               new CoverageEditorCoverageTab("Coverage Items", this, (ICoverageTabProvider) getCoverageEditorProvider());
         addFormPage(coverageEditorCoverageTab);
         if (getCoverageEditorProvider().isImportAllowed()) {
            coverageEditorImportTab = new CoverageEditorImportTab(this);
            addFormPage(coverageEditorImportTab);
         }
         setPartName(getCoverageEditorProvider().getName());
         setTitleImage(ImageManager.getImage(getCoverageEditorProvider().getTitleImage()));
         setActivePage(startPage);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void simulateImport(String importName) throws OseeCoreException {
      if (coverageEditorImportTab == null) throw new OseeStateException("Import page == null");
      setActivePage(2);
      coverageEditorImportTab.simulateImport(importName);
      Thread thread = new Thread() {
         @Override
         public void run() {
            try {
               Thread.sleep(1000);
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     simulateImportPostRun();
                  }
               });
            } catch (InterruptedException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      thread.start();
   }

   private void simulateImportPostRun() {
      setActivePage(5);
      coverageEditorImportTab.simulateImportSearch();
   }

   public int addFormPage(FormPage page) {
      try {
         int pageIndex = addPage(page);
         if (startPage == null) {
            startPage = pageIndex;
         }
         return pageIndex;
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return 0;
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

   public ICoverageEditorProvider getCoverageEditorProvider() {
      return getCoverageEditorInput().getCoverageEditorProvider();
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
               AWorkbench.getActivePage().closeEditor((editor.getEditor(false)), false);
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

}
