/*
 * Created on Nov 8, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.workflow.item.AtsAttributeXWidgetWorkItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;

public class EstimatedHoursXWidget extends AtsAttributeXWidgetWorkItem {

   public static final String ATS_ESTIMATED_HOURS_NOT_REQUIRED_ID = AtsAttributeTypes.EstimatedHours + ".notRequired";

   public EstimatedHoursXWidget() {
      super(AtsAttributeTypes.EstimatedHours.getUnqualifiedName(), ATS_ESTIMATED_HOURS_NOT_REQUIRED_ID,
         AtsAttributeTypes.EstimatedHours, "XFloatDam", XOption.NOT_REQUIRED);
   }

}
