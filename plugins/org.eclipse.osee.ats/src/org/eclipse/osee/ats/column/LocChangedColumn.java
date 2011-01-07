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

public class LocChangedColumn extends XViewerAtsAttributeValueColumn {

   public static LocChangedColumn instance = new LocChangedColumn();

   public static LocChangedColumn getInstance() {
      return instance;
   }

   private LocChangedColumn() {
      super(AtsAttributeTypes.LocChanged, WorldXViewerFactory.COLUMN_NAMESPACE + ".locChanged",
         AtsAttributeTypes.LocChanged.getUnqualifiedName(), 40, SWT.CENTER, false, SortDataType.Integer, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LocChangedColumn copy() {
      LocChangedColumn newXCol = new LocChangedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
