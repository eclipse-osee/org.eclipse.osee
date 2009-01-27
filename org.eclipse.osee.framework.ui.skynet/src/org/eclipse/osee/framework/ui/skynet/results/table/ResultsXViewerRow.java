/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.table;

import java.util.Arrays;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ResultsXViewerRow implements IResultsXViewerRow {

   private final List<String> values;

   public ResultsXViewerRow(List<String> values) {
      this.values = values;
   }

   public ResultsXViewerRow(String[] values) {
      this.values = Arrays.asList(values);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.xresults.IXViewerTestTask#getValue(int)
    */
   @Override
   public String getValue(int col) {
      return values.get(col);
   }

}
