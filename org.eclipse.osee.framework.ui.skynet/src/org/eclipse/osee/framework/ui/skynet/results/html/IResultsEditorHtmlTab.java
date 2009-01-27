/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.html;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;

/**
 * @author Donald G. Dunne
 */
public interface IResultsEditorHtmlTab extends IResultsEditorTab {

   public String getReportHtml() throws OseeCoreException;

}
