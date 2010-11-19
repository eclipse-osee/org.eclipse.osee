/*
 * Created on Nov 19, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.history;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.editor.history.column.AuthorColumn;
import org.eclipse.osee.ats.editor.history.column.DateColumn;
import org.eclipse.osee.ats.editor.history.column.EventColumn;
import org.eclipse.osee.ats.editor.history.column.TransactionColumn;

public class HistoryXViewerFactory extends XViewerFactory {

   public HistoryXViewerFactory() {
      super("ats.history");
      registerColumns(TransactionColumn.getInstance(), EventColumn.getInstance(), DateColumn.getInstance(),
         AuthorColumn.getInstance());
   }

   @Override
   public boolean isAdmin() {
      return false;
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId() == TransactionColumn.getInstance().getId()) {
            xCol.setSortForward(false);
         }
      }
      customizeData.getSortingData().setSortingNames(TransactionColumn.getInstance().getId());
      return customizeData;
   }

}