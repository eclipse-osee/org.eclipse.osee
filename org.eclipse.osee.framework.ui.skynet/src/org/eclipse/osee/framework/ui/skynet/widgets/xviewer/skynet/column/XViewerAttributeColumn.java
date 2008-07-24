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

/**
 * @author Donald G. Dunne
 */
public class XViewerAttributeColumn extends XViewerValueColumn {

   private final String attributeTypeName;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   public XViewerAttributeColumn(XViewerColumn col) {
      super(col);
      this.attributeTypeName = ((XViewerAttributeColumn) col).attributeTypeName;
   }

   public XViewerAttributeColumn(XViewer viewer, String id, String name, String attributeTypeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      super(viewer, id, name, width, align, show, sortDataType);
      this.attributeTypeName = attributeTypeName;
   }

   public XViewerAttributeColumn(XViewer viewer, String id, String name, String attributeTypeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(viewer, id, name, width, defaultWidth, align, show, sortDataType, multiColumnEditable, description);
      this.attributeTypeName = attributeTypeName;
   }

   public XViewerAttributeColumn(String id, String name, String attributeTypeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      this(null, id, name, attributeTypeName, width, defaultWidth, align, show, sortDataType);
   }

   public XViewerAttributeColumn(XViewer viewer, String id, String name, String attributeTypeName, int width, int defaultWidth, int align) {
      this(viewer, id, name, attributeTypeName, width, defaultWidth, align, true, SortDataType.String);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException, SQLException {
      if (element instanceof Artifact) {
         return ((Artifact) element).getAttributesToString(attributeTypeName);
      }
      return super.getColumnText(element, column, columnIndex);
   }

   public String getAttributeTypeName() {
      return attributeTypeName;
   }
}
