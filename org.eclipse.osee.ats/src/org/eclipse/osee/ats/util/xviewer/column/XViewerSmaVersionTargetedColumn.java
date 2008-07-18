/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import java.sql.SQLException;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerSmaVersionTargetedColumn extends XViewerValueColumn {

   public XViewerSmaVersionTargetedColumn(XViewer viewer) {
      this("Completed", viewer);
   }

   public XViewerSmaVersionTargetedColumn(String name, XViewer viewer) {
      super(viewer, WorldXViewerFactory.COLUMN_NAMESPACE + "versionTarget", name, 40, 40, SWT.LEFT, true,
            SortDataType.String, false, "Date this workflow transitioned to the Completed state.");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      if (element instanceof TeamWorkFlowArtifact) {
         return ((TeamWorkFlowArtifact) element).getWorldViewVersion();
      } else if (element instanceof ActionArtifact) {
         return ((ActionArtifact) element).getWorldViewVersion();
      }
      return super.getColumnText(element, column);
   }

}
