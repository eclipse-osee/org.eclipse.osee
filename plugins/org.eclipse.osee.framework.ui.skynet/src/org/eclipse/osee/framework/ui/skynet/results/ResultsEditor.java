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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.util.SelectionProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditor extends AbstractArtifactEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.results.ResultsEditor";
   private Integer startPage = null;
   private List<IResultsEditorTab> tabs;
   private int lastPageSelected = -1;
   private ISelectionChangedListener selectionListener;

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input);
      defaultSelectionProvider = new SelectionProvider();
      getSite().setSelectionProvider(defaultSelectionProvider);

      selectionListener = new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            List<Object> objects = rowsToData(selection);
            getSite().getSelectionProvider().setSelection(new StructuredSelection(objects));

         }
      };
   }

   List<IResultsEditorTab> getTabs() {
      if (tabs == null) {
         tabs = getResultsEditorProvider().getResultsEditorTabs();
         if (tabs == null || tabs.isEmpty()) {
            tabs.add(new ResultsEditorHtmlTab("Error", "Error", AHTML.simplePage(
               "Error: No tabs were defined for \"" + getResultsEditorProvider().getEditorName() + "\"")));
         }
      }
      return tabs;
   }

   private List<Object> rowsToData(ISelection selection) {
      List<Object> datas = new LinkedList<>();
      if (selection instanceof IStructuredSelection) {
         IStructuredSelection selected = (IStructuredSelection) selection;
         Iterator<?> iterator = selected.iterator();
         while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof ResultsXViewerRow) {
               Object data = ((ResultsXViewerRow) object).getData();
               if (data != null) {
                  datas.add(data);
               }
            }
         }
      }
      return datas;
   }

   @Override
   protected void pageChange(int newPageIndex) {
      super.pageChange(newPageIndex);
      setSelectionListenerOn(newPageIndex);
   }

   private synchronized void setSelectionListenerOn(int pageIndex) {
      if (lastPageSelected > -1) {
         ResultsXViewer oldViewer = getViewerForPage(lastPageSelected);
         if (oldViewer != null) {
            oldViewer.removeSelectionChangedListener(selectionListener);
         }
      }
      lastPageSelected = pageIndex;
      ResultsXViewer viewer = getViewerForPage(pageIndex);
      if (viewer != null) {
         viewer.addSelectionChangedListener(selectionListener);
      }
   }

   private ResultsXViewer getViewerForPage(int index) {
      ResultsXViewer viewer = null;
      try {
         IResultsEditorTab iResultsEditorTab = getTabs().get(index);
         if (iResultsEditorTab instanceof ResultsEditorTableTab) {
            ResultsEditorTableTab tableTab = (ResultsEditorTableTab) iResultsEditorTab;
            viewer = tableTab.getResultsXViewer();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return viewer;
   }

   @Override
   protected void addPages() {
      try {
         OseeStatusContributionItemFactory.addTo(this, true);
         String editorName = getResultsEditorProvider().getEditorName();
         for (IResultsEditorTab tab : getTabs()) {
            addResultsTab(tab);
         }
         if (startPage == null) {
            addResultsTab(new ResultsEditorHtmlTab("Error", "Error",
               AHTML.simplePage("Error: Pages creation error for \"" + editorName + "\"; StartPage == null")));
         }
         setPartName(editorName);
         setActivePage(startPage);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void addResultsTab(IResultsEditorTab tab) {
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
      return ALayout.createCommonToolBar(parent);
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
   public boolean isDirty() {
      return false;
   }

   public static void open(final String tabName, final String title, final String html) {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() {
            return title;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            List<IResultsEditorTab> tabs = new ArrayList<>();
            tabs.add(new ResultsEditorHtmlTab(title, tabName, html));
            return tabs;
         }
      });
   }

   public static void open(final String name, final XResultData data) {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() {
            return name;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            List<IResultsEditorTab> tabs = new ArrayList<>();
            tabs.add(new ResultsEditorHtmlTab(XResultDataUI.getReport(data, name)));
            return tabs;
         }
      });

   }

   public static void open(final XResultPage xResultPage) {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() {
            return xResultPage.getTitle();
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            List<IResultsEditorTab> tabs = new ArrayList<>();
            tabs.add(new ResultsEditorHtmlTab(xResultPage));
            return tabs;
         }
      });
   }

   public static void open(final IResultsEditorProvider provider) {
      open(provider, false);
   }

   public static void open(final IResultsEditorProvider provider, boolean forcePend) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               ResultsEditorInput input = new ResultsEditorInput(provider);
               page.openEditor(input, EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
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
      final List<ResultsEditor> editors = new ArrayList<>();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((ResultsEditor) editor.getEditor(false));
            }
         }
      });
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
}
