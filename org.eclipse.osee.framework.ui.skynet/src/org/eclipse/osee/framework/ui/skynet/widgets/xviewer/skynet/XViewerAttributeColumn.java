/*
 * Created on Jul 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerAttributeColumn extends XViewerValueColumn {

   public XViewerAttributeColumn(XViewer viewer, String name, String attributeTypeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType, int orderNum) {
      super(viewer, name, attributeTypeName, width, defaultWidth, align, show, sortDataType, orderNum);
   }

   public XViewerAttributeColumn(String name, String attributeTypeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType, int orderNum) {
      this(null, name, attributeTypeName, width, defaultWidth, align, show, sortDataType, orderNum);
   }

   public XViewerAttributeColumn(XViewer viewer, String name, String attributeTypeName, int width, int defaultWidth, int align) {
      this(viewer, name, attributeTypeName, width, defaultWidth, align, true, SortDataType.String, Integer.MAX_VALUE);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn column) {
      try {
         if (element instanceof Artifact) {
            ((Artifact) element).getImage();
         }
         return super.getColumnImage(element, column);
      } catch (Exception ex) {// do nothing
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column) {
      try {
         if (element instanceof Artifact) {
            return ((Artifact) element).getSoleAttributeValueAsString(column.getStoreName(), "");
         }
         return super.getColumnText(element, column);
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

}
