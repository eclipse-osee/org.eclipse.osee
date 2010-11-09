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

public class CategoryColumn extends XViewerAtsAttributeValueColumn {

   public static final IAttributeType Category1Attribute = new AtsAttributeTypes("AAMFEdrYniOQYrYUKKQA", "Category",
      "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final IAttributeType Category2Attribute = new AtsAttributeTypes("AAMFEdthBkolbJKLXuAA", "Category2",
      Category1Attribute.getDescription());
   public static final IAttributeType Category3Attribute = new AtsAttributeTypes("AAMFEd06oxr8LMzZxdgA", "Category3",
      Category1Attribute.getDescription());

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
