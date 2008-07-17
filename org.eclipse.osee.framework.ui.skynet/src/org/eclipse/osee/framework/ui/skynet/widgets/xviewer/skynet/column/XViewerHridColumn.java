/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerHridColumn extends XViewerValueColumn {

   public XViewerHridColumn(String name, XViewer viewer) {
      super(viewer, "framework.hrid." + name, name, 75, 75, SWT.LEFT, true, SortDataType.String);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      if (element instanceof Artifact) {
         return ((Artifact) element).getHumanReadableId();
      }
      return "";
   }

}
