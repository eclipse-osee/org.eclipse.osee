/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerValueColumn extends XViewerColumn {

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    * 
    * @param col
    */
   public XViewerValueColumn copy() {
      return new XViewerValueColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
            isMultiColumnEditable(), getDescription());
   }

   public XViewerValueColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerValueColumn(XViewer viewer, String xml) {
      super(viewer, xml);
   }

   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException {
      return null;
   }

   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException {
      return "unhandled";
   }

   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      return null;
   }

   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      return null;
   }

   //This method will only be called be the XViewerStyledTextLabelProvider
   public StyledString getStyledText(Object element, XViewerColumn viewerColumn, int columnIndex) throws OseeCoreException {
      return new StyledString(getColumnText(element, viewerColumn, columnIndex));
   }

   //This method will only be called be the XViewerStyledTextLabelProvider
   public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) throws OseeCoreException {
      return null;
   }

}
