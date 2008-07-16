/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerValueColumn extends XViewerColumn {

   public XViewerValueColumn(XViewer viewer, String name, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      super(viewer, name, width, defaultWidth, align, show, sortDataType);
   }

   public XViewerValueColumn(String name, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      super(name, width, defaultWidth, align, show, sortDataType);
   }

   public XViewerValueColumn(XViewer viewer, String xml) {
      super(viewer, xml);
   }

   public XViewerValueColumn(XViewer viewer, String name, int width, int defaultWidth, int align) {
      super(viewer, name, width, defaultWidth, align);
   }

   public Image getColumnImage(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      return null;
   }

   public String getColumnText(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      return "unhandled";
   }

}
