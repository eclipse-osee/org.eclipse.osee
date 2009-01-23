/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorChartTab implements IResultsEditorChartTab {

   private final Chart chart;
   private final String tabName;

   public ResultsEditorChartTab(String tabName, Chart chart) {
      this.tabName = tabName;
      this.chart = chart;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorChartTab#getChart()
    */
   @Override
   public Chart getChart() throws OseeCoreException {
      return chart;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#getTabName()
    */
   @Override
   public String getTabName() {
      return tabName;
   }

}
