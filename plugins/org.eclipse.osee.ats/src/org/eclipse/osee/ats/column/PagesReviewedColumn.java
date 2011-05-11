/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.swt.SWT;

public class PagesReviewedColumn extends XViewerAtsAttributeValueColumn {

   public static PagesReviewedColumn instance = new PagesReviewedColumn();

   public static PagesReviewedColumn getInstance() {
      return instance;
   }

   private PagesReviewedColumn() {
      super(AtsAttributeTypes.PagesReviewed, WorldXViewerFactory.COLUMN_NAMESPACE + ".pagesReviewed",
         AtsAttributeTypes.PagesReviewed.getUnqualifiedName(), 40, SWT.CENTER, false, SortDataType.Integer, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PagesReviewedColumn copy() {
      PagesReviewedColumn newXCol = new PagesReviewedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
