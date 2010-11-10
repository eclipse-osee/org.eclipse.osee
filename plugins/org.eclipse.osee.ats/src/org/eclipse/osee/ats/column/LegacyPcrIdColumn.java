/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.swt.SWT;

public class LegacyPcrIdColumn extends XViewerAtsAttributeValueColumn {

   public static LegacyPcrIdColumn instance = new LegacyPcrIdColumn();

   public static LegacyPcrIdColumn getInstance() {
      return instance;
   }

   public LegacyPcrIdColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".legacyPcr", AtsAttributeTypes.LegacyPcrId, 40, SWT.LEFT, false,
         SortDataType.String, false);
   }

   public LegacyPcrIdColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LegacyPcrIdColumn copy() {
      return new LegacyPcrIdColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

}
