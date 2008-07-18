/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerValueColumn extends XViewerColumn {

   public XViewerValueColumn(XViewer viewer, String id, String name, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      super(viewer, id, name, width, defaultWidth, align, show, sortDataType);
   }

   public XViewerValueColumn(XViewer viewer, String id, String name, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(viewer, id, name, width, defaultWidth, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerValueColumn(String id, String name, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      super(id, name, width, defaultWidth, align, show, sortDataType);
   }

   public XViewerValueColumn(XViewer viewer, String xml) {
      super(viewer, xml);
   }

   public XViewerValueColumn(XViewer viewer, String id, String name, int width, int defaultWidth, int align) {
      super(viewer, id, name, width, defaultWidth, align);
   }

   public Image getColumnImage(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      return null;
   }

   public String getColumnText(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      return "unhandled";
   }

   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

}
