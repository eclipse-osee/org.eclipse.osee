/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerEstimatedHoursColumn extends XViewerAtsAttributeColumn {

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   @Override
   public XViewerEstimatedHoursColumn copy() {
      return new XViewerEstimatedHoursColumn(getId(), getWidth(), getAlign(), isShow(), getSortDataType(),
            isMultiColumnEditable(), getDescription());
   }

   public XViewerEstimatedHoursColumn(String id, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE, width, align, show, sortDataType, multiColumnEditable,
            description);
   }

   public XViewerEstimatedHoursColumn() {
      super(
            WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedHours",
            ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE,
            40,
            SWT.CENTER,
            false,
            SortDataType.Float,
            true,
            "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn#getToolTip()
    */
   @Override
   public String getToolTip() {
      return getDescription();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException {
      if (element instanceof IWorldViewArtifact) {
         return AtsLib.doubleToStrString(((IWorldViewArtifact) element).getWorldViewEstimatedHours());
      }
      return "";
   }

}
