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
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditor extends AbstractArtifactEditor implements IDirtiableEditor, IActionable {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.results.ResultsEditor";
   private Integer startPage = null;

   @Override
   protected void addPages() {

      try {
         OseeContributionItem.addTo(this, true);

         IResultsEditorProvider provider = getResultsEditorProvider();
         List<IResultsEditorTab> tabs = provider.getResultsEditorTabs();
         if (tabs.isEmpty()) {
            tabs.add(new ResultsEditorHtmlTab("Error", "Error",
                  AHTML.simplePage("Error: Pages creation error for \"" + provider.getEditorName() + "\"")));
         }
         for (IResultsEditorTab tab : provider.getResultsEditorTabs()) {
            addResultsTab(tab);
         }
         if (startPage == null) {
            addResultsTab(new ResultsEditorHtmlTab(
                  "Error",
                  "Error",
                  AHTML.simplePage("Error: Pages creation error for \"" + provider.getEditorName() + "\"; StartPage == null")));
         }
         setPartName(provider.getEditorName());
         setActivePage(startPage);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public void addResultsTab(IResultsEditorTab tab) throws OseeCoreException {
      Composite comp = tab.createTab(getContainer(), this);
      int pageIndex = addPage(comp);
      if (startPage == null) {
         startPage = pageIndex;
      }
      setPageText(pageIndex, tab.getTabName());
   }

   public String getEditorId() {
      return EDITOR_ID;
   }

   public String getActionableItemName() {
      return "Result View";
   }

   public ToolBar createToolBar(Composite parent) {
      ToolBar toolBar = ALayout.createCommonToolBar(parent);

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, getEditorId(),
            getActionableItemName());

      return toolBar;
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

   public IResultsEditorProvider getResultsEditorProvider() {
      IEditorInput editorInput = getEditorInput();
      if (!(editorInput instanceof ResultsEditorInput)) {
         throw new IllegalArgumentException("Editor Input not WorldEditorInput");
      }
      ResultsEditorInput worldEditorInput = (ResultsEditorInput) editorInput;
      return worldEditorInput.getIWorldEditorProvider();
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

   @Override
   public String getActionDescription() {
      return null;
   }

   public static void open(final String tabName, final String title, final String html) throws OseeCoreException {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() throws OseeCoreException {
            return title;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
            List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
            tabs.add(new ResultsEditorHtmlTab(title, tabName, html));
            return tabs;
         }
      });
   }

   public static void open(final XResultPage xResultPage) throws OseeCoreException {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() throws OseeCoreException {
            return xResultPage.getTitle();
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
            List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
            tabs.add(new ResultsEditorHtmlTab(xResultPage));
            return tabs;
         }
      });
   }

   public static void open(final IResultsEditorProvider provider) throws OseeCoreException {
      open(provider, false);
   }

   public static void open(final IResultsEditorProvider provider, boolean forcePend) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new ResultsEditorInput(provider), EDITOR_ID);
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

   public static Collection<ResultsEditor> getEditors() {
      final List<ResultsEditor> editors = new ArrayList<ResultsEditor>();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((ResultsEditor) editor.getEditor(false));
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

}
