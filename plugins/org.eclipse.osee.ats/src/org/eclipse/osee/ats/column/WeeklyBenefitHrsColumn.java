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

public class WeeklyBenefitHrsColumn extends XViewerAtsAttributeValueColumn {

   public static WeeklyBenefitHrsColumn instance = new WeeklyBenefitHrsColumn();

   public static WeeklyBenefitHrsColumn getInstance() {
      return instance;
   }

   private WeeklyBenefitHrsColumn() {
      super(AtsAttributeTypes.WeeklyBenefit, WorldXViewerFactory.COLUMN_NAMESPACE + ".weeklyBenefitHrs",
         AtsAttributeTypes.WeeklyBenefit.getUnqualifiedName(), 40, SWT.CENTER, false, SortDataType.Float, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WeeklyBenefitHrsColumn copy() {
      WeeklyBenefitHrsColumn newXCol = new WeeklyBenefitHrsColumn();
      copy(this, newXCol);
      return newXCol;
   }

}
