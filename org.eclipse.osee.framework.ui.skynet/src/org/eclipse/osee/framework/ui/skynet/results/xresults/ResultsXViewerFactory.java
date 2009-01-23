/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.xresults;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;

/**
 * @author Donald G. Dunne
 */
public class ResultsXViewerFactory extends XViewerFactory {

   private static String COLUMN_NAMESPACE = "org.eclipse.osee.table";
   private final List<XViewerColumn> columns;

   public ResultsXViewerFactory(List<XViewerColumn> columns) {
      super("xviewer.test");
      this.columns = columns;
      for (XViewerColumn xCol : columns) {
         registerColumn(xCol);
      }
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return new XViewerCustomizations();
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.IXViewerFactory#isAdmin()
    */
   @Override
   public boolean isAdmin() {
      return true;
   }

}
