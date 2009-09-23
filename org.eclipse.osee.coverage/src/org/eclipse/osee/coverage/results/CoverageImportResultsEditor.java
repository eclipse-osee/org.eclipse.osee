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
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;

/**
 * Displays a Results Editor for a single CoverageImport
 * 
 * @author Donald G. Dunne
 */
public class CoverageImportResultsEditor {

   private final CoverageImport coverageImport;

   public CoverageImportResultsEditor(CoverageImport coverageImport) {
      this.coverageImport = coverageImport;
   }

   public void open() throws OseeCoreException {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() throws OseeCoreException {
            return coverageImport.getName();
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
            List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
            tabs.add(new CoverageImportOverviewResultsEditorTab(coverageImport));
            tabs.add(new CoverageItemResultsTableTab(coverageImport.getCoverageUnits()));
            return tabs;
         }

      });
   }
}
