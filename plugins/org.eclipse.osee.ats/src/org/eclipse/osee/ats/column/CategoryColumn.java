/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.swt.SWT;

public class CategoryColumn extends XViewerAtsAttributeValueColumn {

   public static CategoryColumn category1 = new CategoryColumn(AtsAttributeTypes.Category1);
   public static CategoryColumn category2 = new CategoryColumn(AtsAttributeTypes.Category2);
   public static CategoryColumn category3 = new CategoryColumn(AtsAttributeTypes.Category3);

   public static CategoryColumn getCategory1Instance() {
      return category1;
   }

   public static CategoryColumn getCategory2Instance() {
      return category2;
   }

   public static CategoryColumn getCategory3Instance() {
      return category3;
   }

   public CategoryColumn(IAttributeType attributeType) {
      super(attributeType, 80, SWT.LEFT, false, SortDataType.String, true);
   }

   public CategoryColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CategoryColumn copy() {
      return new CategoryColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

}
