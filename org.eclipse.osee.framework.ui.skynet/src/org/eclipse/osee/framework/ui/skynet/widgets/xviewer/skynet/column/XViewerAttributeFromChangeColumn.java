/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerAttributeFromChangeColumn extends XViewerValueColumn {

   private final String attributeTypeName;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   public XViewerAttributeFromChangeColumn(XViewerColumn col) {
      super(col);
      this.attributeTypeName = ((XViewerAttributeFromChangeColumn) col).attributeTypeName;
   }

   public XViewerAttributeFromChangeColumn(XViewer viewer, String name, String attributeTypeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      super(viewer, "attribute." + name, name, width, align, show, sortDataType);
      this.attributeTypeName = attributeTypeName;
   }

   public XViewerAttributeFromChangeColumn(String name, String attributeTypeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      this(null, name, attributeTypeName, width, defaultWidth, align, show, sortDataType);
   }

   public XViewerAttributeFromChangeColumn(XViewer viewer, String name, String attributeTypeName, int width, int defaultWidth, int align) {
      this(viewer, name, attributeTypeName, width, defaultWidth, align, true, SortDataType.String);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException, SQLException {
      if (element instanceof Change) {
         return ((Change) element).getArtifact().getAttributesToString(attributeTypeName);
      }
      return super.getColumnText(element, column, columnIndex);
   }
}
