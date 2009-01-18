/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IResultsEditorProvider {

   public String getName() throws OseeCoreException;

   public String getReportHtml() throws OseeCoreException;

   public Chart getChart() throws OseeCoreException;

}
