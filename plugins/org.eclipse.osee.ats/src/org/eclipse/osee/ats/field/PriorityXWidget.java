/*
 * Created on Oct 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.osee.ats.workflow.item.AtsAttributeSoleComboXWidgetWorkItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;

public class PriorityXWidget extends AtsAttributeSoleComboXWidgetWorkItem {

   public PriorityXWidget() {
      super(PriorityColumn.PriorityTypeAttribute, "OPTIONS_FROM_ATTRIBUTE_VALIDITY", XOption.REQUIRED);
   }
}
