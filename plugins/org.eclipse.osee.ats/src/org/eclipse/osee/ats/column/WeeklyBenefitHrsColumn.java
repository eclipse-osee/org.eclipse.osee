/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.swt.SWT;

public class WeeklyBenefitHrsColumn extends XViewerAtsAttributeValueColumn {

   public static WeeklyBenefitHrsColumn instance = new WeeklyBenefitHrsColumn();

   public static WeeklyBenefitHrsColumn getInstance() {
      return instance;
   }

   public WeeklyBenefitHrsColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".weeklyBenefitHrs", AtsAttributeTypes.WeeklyBenefit, 40,
         SWT.CENTER, false, SortDataType.Float, true);
   }

   public WeeklyBenefitHrsColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WeeklyBenefitHrsColumn copy() {
      return new WeeklyBenefitHrsColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

}
