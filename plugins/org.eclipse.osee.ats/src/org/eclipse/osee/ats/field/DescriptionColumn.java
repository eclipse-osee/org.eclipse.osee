/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.swt.SWT;

public class DescriptionColumn extends XViewerAtsAttributeValueColumn {

   public static DescriptionColumn instance = new DescriptionColumn();

   public static DescriptionColumn getInstance() {
      return instance;
   }

   public DescriptionColumn() {
      super(AtsAttributeTypes.Description, 150, SWT.LEFT, false, SortDataType.String, true);
   }

   public DescriptionColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public DescriptionColumn copy() {
      return new DescriptionColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

   @Override
   public boolean isMultiLineStringAttribute() {
      return true;
   }

}
