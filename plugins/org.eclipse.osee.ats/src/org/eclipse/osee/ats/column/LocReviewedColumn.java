/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.swt.SWT;

public class LocReviewedColumn extends XViewerAtsAttributeValueColumn {

   public static LocReviewedColumn instance = new LocReviewedColumn();

   public static LocReviewedColumn getInstance() {
      return instance;
   }

   private LocReviewedColumn() {
      super(AtsAttributeTypes.LocReviewed, WorldXViewerFactory.COLUMN_NAMESPACE + ".locReviewed",
         AtsAttributeTypes.LocReviewed.getUnqualifiedName(), 40, SWT.CENTER, false, SortDataType.Integer, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LocReviewedColumn copy() {
      LocReviewedColumn newXCol = new LocReviewedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
