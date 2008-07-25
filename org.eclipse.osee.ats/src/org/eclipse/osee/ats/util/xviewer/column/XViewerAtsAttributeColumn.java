/*
 * Created on Jul 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeColumn extends XViewerAttributeColumn {

   public XViewerAtsAttributeColumn(String id, ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, atsAttribute.getDisplayName(), atsAttribute.getStoreName(), width, align, show, sortDataType,
            multiColumnEditable, description);
   }

   public XViewerAtsAttributeColumn(ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(
            WorldXViewerFactory.COLUMN_NAMESPACE + "." + (atsAttribute.getDisplayName().replaceAll(" ", "").toLowerCase()),
            atsAttribute.getDisplayName(), atsAttribute.getStoreName(), width, align, show, sortDataType,
            multiColumnEditable, description);
   }

}
