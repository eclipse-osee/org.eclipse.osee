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
package org.eclipse.osee.coverage.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.internal.CoveragePlugin;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * Displays a Results Editor for a single CoverageImport
 * 
 * @author Donald G. Dunne
 */
public class CoverageImportResultsEditor extends ResultsEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.coverage.results.CoverageImportResultsEditor";

   public static void open(final CoverageImport coverageImport) throws OseeCoreException {
      CoverageImportResultsEditor.openCoverageEditor(new ICoverageResultsEditorProvider() {

         @Override
         public String getEditorName() throws OseeCoreException {
            return coverageImport.getName();
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
            List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
            try {
               tabs.add(new CoverageImportOverviewResultsEditorTab(coverageImport));
            } catch (Exception ex) {
               OseeLog.log(CoveragePlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            try {
               tabs.add(new CoverageItemResultsTableTab(coverageImport.getCoverageUnits()));
            } catch (Exception ex) {
               OseeLog.log(CoveragePlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return tabs;
         }

         @Override
         public CoverageImport getCoverageImport() {
            return coverageImport;
         }

      }, false);
   }

   @Override
   public String getActionableItemName() {
      return "Coverage Import Editor";
   }

   @Override
   public String getEditorId() {
      return EDITOR_ID;
   }

   public static void openCoverageEditor(final ICoverageResultsEditorProvider provider, boolean forcePend) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new CoverageResultsEditorInput(provider), EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }, forcePend);
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
