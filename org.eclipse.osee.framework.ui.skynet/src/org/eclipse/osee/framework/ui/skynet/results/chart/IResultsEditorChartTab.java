/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.chart;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;

/**
 * @author Donald G. Dunne
 */
public interface IResultsEditorChartTab extends IResultsEditorTab {

   public Chart getChart() throws OseeCoreException;
}
