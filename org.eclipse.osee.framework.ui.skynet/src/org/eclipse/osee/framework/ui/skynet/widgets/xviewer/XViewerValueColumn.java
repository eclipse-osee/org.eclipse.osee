/*
 * Created on Jul 13, 2008
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

   public XViewerValueColumn(XViewer viewer, String name, String storeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType, int orderNum) {
      super(viewer, name, width, defaultWidth, align, show, sortDataType, orderNum);
      setStoreName(storeName);
   }

   public XViewerValueColumn(String name, String storeName, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType, int orderNum) {
      this(null, name, storeName, width, defaultWidth, align, show, sortDataType, orderNum);

   }

   public XViewerValueColumn(XViewer viewer, String name, String storeName, int width, int defaultWidth, int align) {
      this(null, name, storeName, width, defaultWidth, align, true, SortDataType.String, Integer.MAX_VALUE);

   }

   public XViewerValueColumn(XViewer viewer, String xml) {
      super(viewer, xml);
   }

   public Image getColumnImage(Object element, XViewerColumn column) {
      return null;
   }

   public String getColumnText(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      return "unhandled";
   }

}
