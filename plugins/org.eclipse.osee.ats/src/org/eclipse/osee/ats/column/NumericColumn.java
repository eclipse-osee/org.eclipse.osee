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

public class NumericColumn extends XViewerAtsAttributeValueColumn {

   public static NumericColumn numeric1 = new NumericColumn(AtsAttributeTypes.Numeric1);
   public static NumericColumn numeric2 = new NumericColumn(AtsAttributeTypes.Numeric2);

   public static NumericColumn getNumeric1Instance() {
      return numeric1;
   }

   public static NumericColumn getNumeric2Instance() {
      return numeric2;
   }

   public NumericColumn(IAttributeType attributeType) {
      super(attributeType, 40, SWT.LEFT, false, SortDataType.Float, true, "");
   }

   private NumericColumn() {
      super();
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public NumericColumn copy() {
      NumericColumn newXCol = new NumericColumn();
      copy(this, newXCol);
      return newXCol;
   }

}
