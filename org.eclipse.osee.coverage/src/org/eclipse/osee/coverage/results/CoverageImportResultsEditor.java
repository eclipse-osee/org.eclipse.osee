/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
