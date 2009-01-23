/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorHtmlTab implements IResultsEditorHtmlTab {

   private final String tabName;
   private final String html;

   public ResultsEditorHtmlTab(String tabName, String html) {
      this.tabName = tabName;
      this.html = html;
      org.eclipse.core.runtime.Assert.isNotNull(tabName);
      org.eclipse.core.runtime.Assert.isNotNull(html);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorHtmlTab#getReportHtml()
    */
   @Override
   public String getReportHtml() throws OseeCoreException {
      return html;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#getTabName()
    */
   @Override
   public String getTabName() {
      return tabName;
   }

}
