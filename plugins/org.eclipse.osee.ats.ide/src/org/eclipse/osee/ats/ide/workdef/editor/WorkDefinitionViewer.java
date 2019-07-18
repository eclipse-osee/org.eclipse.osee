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
package org.eclipse.osee.ats.ide.workdef.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.ide.editor.tab.workflow.util.WfeOutlinePage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorOutlineProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.IResultsEditorTableListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionViewer extends XNavigateItemAction {

   public static final String TITLE = "Work Definition Viewer";
   private IAtsWorkDefinition workDef;
   private WfeOutlinePage outlinePage;

   public WorkDefinitionViewer(XNavigateItem parent) {
      super(parent, TITLE, PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return TITLE;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<>();
               IResultsEditorTableTab workDefinitionTab = createWorkDefinitionTab();
               workDefinitionTab.addListener(getListener());
               workDefinitionTab.addOutlineProvider(getOutlineProvider());
               tabs.add(workDefinitionTab);
            }
            return tabs;
         }

      });
   }

   private IResultsEditorOutlineProvider getOutlineProvider() {
      final WorkDefinitionViewer definitionViewer = this;
      return new IResultsEditorOutlineProvider() {

         @Override
         public ContentOutlinePage getOutlinePage() {
            outlinePage = new WfeOutlinePage();
            outlinePage.setWorkDefViewer(definitionViewer);
            return outlinePage;
         }

      };
   }

   private IResultsEditorTableListener getListener() {
      final WorkDefinitionViewer definitionViewer = this;
      return new IResultsEditorTableListener() {

         @Override
         public void handleSelectionListener(Collection<ResultsXViewerRow> selectedRows) {
            handleSelection(definitionViewer, selectedRows);
         }

         private void handleSelection(final WorkDefinitionViewer definitionViewer, Collection<ResultsXViewerRow> selectedRows) {
            if (!selectedRows.isEmpty()) {
               IWorkbenchPage page = AWorkbench.getActivePage();
               try {
                  page.showView("org.eclipse.ui.views.ContentOutline", null, IWorkbenchPage.VIEW_ACTIVATE);
               } catch (PartInitException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
               workDef = (IAtsWorkDefinition) selectedRows.iterator().next().getData();
               outlinePage.setInput(definitionViewer);
            }
         }

         @Override
         public void handleDoubleClick(ArrayList<ResultsXViewerRow> selectedRows) {
            handleSelection(definitionViewer, selectedRows);
            // TBD            IResultsEditorTableListener.super.handleDoubleClick(selectedRows);
            //            IWorkbenchPage page = AWorkbench.getActivePage();
            //            ResultsXViewerRow row = selectedRows.iterator().next();
            //            IAtsWorkDefinition workDef = (IAtsWorkDefinition) row.getData();
            //
            //            //            String workDefJavaFilename = workDef.getName().replaceAll("_", "");
            //            //            workDefJavaFilename = workDefJavaFilename + ".java";
            //
            //            IFile file = OseeData.getIFile("workdef.java");
            //            IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
            //            try {
            //               page.openEditor(new FileEditorInput(file), desc.getId());
            //            } catch (PartInitException ex) {
            //               System.err.println(Lib.exceptionToString(ex));
            //            }
         }

      };
   }

   private IResultsEditorTableTab createWorkDefinitionTab() {
      List<XViewerColumn> artColumns =
         Arrays.asList(new XViewerColumn("Name", "Name", 300, XViewerAlign.Left, true, SortDataType.String, false, ""),
            new XViewerColumn("ID", "ID", 200, XViewerAlign.Left, true, SortDataType.String, false, ""));

      List<IResultsXViewerRow> workDefRows = new ArrayList<>();
      try {
         List<IAtsWorkDefinition> workDefs = new ArrayList<>();
         workDefs.addAll(AtsClientService.get().getWorkDefinitionService().getAllWorkDefinitions());
         Collections.sort(workDefs, new Comparator<IAtsWorkDefinition>() {

            @Override
            public int compare(IAtsWorkDefinition o1, IAtsWorkDefinition o2) {
               return o1.getName().compareTo(o2.getName());
            }
         });
         for (IAtsWorkDefinition workDef : workDefs) {
            workDefRows.add(new ResultsXViewerRow(new String[] {workDef.getName(), workDef.getIdString()}, workDef));
         }
      } catch (OseeCoreException ex) {
         // do nothing
      }

      return new ResultsEditorTableTab("Work Definitions", artColumns, workDefRows);

   }

   public IAtsWorkDefinition getWorkDef() {
      return workDef;
   }

}
