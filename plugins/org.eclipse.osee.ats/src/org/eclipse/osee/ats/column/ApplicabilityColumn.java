/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityColumn extends XViewerAtsAttributeValueColumn {

   public static ApplicabilityColumn instance = new ApplicabilityColumn();

   public static ApplicabilityColumn getInstance() {
      return instance;
   }

   private ApplicabilityColumn() {
      super(AtsAttributeTypes.ApplicableToProgram, 60, SWT.LEFT, false, SortDataType.String, true, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ApplicabilityColumn copy() {
      ApplicabilityColumn newXCol = new ApplicabilityColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
