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

public class PagesChangedColumn extends XViewerAtsAttributeValueColumn {

   public static PagesChangedColumn instance = new PagesChangedColumn();

   public static PagesChangedColumn getInstance() {
      return instance;
   }

   private PagesChangedColumn() {
      super(AtsAttributeTypes.PagesChanged, WorldXViewerFactory.COLUMN_NAMESPACE + ".pagesChanged",
         AtsAttributeTypes.PagesChanged.getUnqualifiedName(), 40, SWT.CENTER, false, SortDataType.Integer, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PagesChangedColumn copy() {
      PagesChangedColumn newXCol = new PagesChangedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
