/*
 * Created on Jul 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeColumn extends XViewerAttributeColumn {

   public XViewerAtsAttributeColumn(XViewer viewer, String id, ATSAttributes atsAttribute, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      super(viewer, id, atsAttribute.getDisplayName(), atsAttribute.getStoreName(), width, defaultWidth, align, show,
            sortDataType, false, atsAttribute.getDescription());
   }

   public XViewerAtsAttributeColumn(XViewer viewer, String id, ATSAttributes atsAttribute, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(viewer, id, atsAttribute.getDisplayName(), atsAttribute.getStoreName(), width, defaultWidth, align, show,
            sortDataType, multiColumnEditable, description);
   }

   public XViewerAtsAttributeColumn(String id, ATSAttributes atsAttribute, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      this(null, id, atsAttribute, width, defaultWidth, align, show, sortDataType);
   }

   public XViewerAtsAttributeColumn(ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType) {
      this(null, WorldXViewerFactory.COLUMN_NAMESPACE + atsAttribute.getDisplayName(), atsAttribute, width, width,
            align, show, sortDataType, false, atsAttribute.getDescription());
   }

   public XViewerAtsAttributeColumn(XViewer viewer, String id, ATSAttributes atsAttribute, int width, int defaultWidth, int align) {
      this(viewer, id, atsAttribute, width, defaultWidth, align, true, SortDataType.String);
   }

}
