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

public class PercentReworkColumn extends XViewerAtsAttributeValueColumn {

   public static PercentReworkColumn instance = new PercentReworkColumn();

   public static PercentReworkColumn getInstance() {
      return instance;
   }

   private PercentReworkColumn() {
      super(AtsAttributeTypes.PercentRework, WorldXViewerFactory.COLUMN_NAMESPACE + ".percentRework",
         AtsAttributeTypes.PercentRework.getName(), 40, SWT.CENTER, false, SortDataType.Percent, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentReworkColumn copy() {
      PercentReworkColumn newXCol = new PercentReworkColumn();
      copy(this, newXCol);
      return newXCol;
   }

}
