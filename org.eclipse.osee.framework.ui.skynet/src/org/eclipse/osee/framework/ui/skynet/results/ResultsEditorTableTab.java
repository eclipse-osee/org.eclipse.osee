/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.xresults.IResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorTableTab implements IResultsEditorTableTab {

   private final String tabName;
   private final List<XViewerColumn> columns;
   private final Collection<IResultsXViewerRow> rows;

   public ResultsEditorTableTab(String tabName, List<XViewerColumn> columns, Collection<IResultsXViewerRow> rows) {
      this.tabName = tabName;
      this.columns = columns;
      this.rows = rows;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTableTab#getTableColumns()
    */
   @Override
   public List<XViewerColumn> getTableColumns() throws OseeCoreException {
      return columns;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTableTab#getTableRows()
    */
   @Override
   public Collection<IResultsXViewerRow> getTableRows() throws OseeCoreException {
      return rows;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#getTabName()
    */
   @Override
   public String getTabName() {
      return tabName;
   }

}
