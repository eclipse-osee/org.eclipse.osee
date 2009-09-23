/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.results;

import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.util.CoverageMetrics;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class CoverageImportOverviewResultsEditorTab extends ResultsEditorHtmlTab {

   public CoverageImportOverviewResultsEditorTab(CoverageImport coverageImport) {
      super("Overview");

      XResultData rd = new XResultData();
      rd.log(AHTML.getLabelStr("Date: ", XDate.getDateStr(coverageImport.getRunDate(), XDate.HHMMSSSS)));
      rd.log(AHTML.getLabelStr("Coverage Units: ", String.valueOf(coverageImport.getCoverageUnits().size())));
      rd.log(AHTML.getLabelStr("Coverage Items: ", String.valueOf(coverageImport.getCoverageItems().size())));
      rd.log(AHTML.getLabelStr("Coverage Percent: ", String.format("%d",
            CoverageMetrics.getPercentCoverage(coverageImport.getCoverageUnits()))));
      setHtml(coverageImport.getName(), rd.getReport(coverageImport.getName()).getManipulatedHtml());
   }
}
