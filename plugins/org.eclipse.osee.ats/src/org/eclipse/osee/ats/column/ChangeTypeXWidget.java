/*
 * Created on Oct 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.workflow.item.AtsAttributeSoleComboXWidgetWorkItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;

public class ChangeTypeXWidget extends AtsAttributeSoleComboXWidgetWorkItem {

   public ChangeTypeXWidget() {
      super(AtsAttributeTypes.ChangeType, "OPTIONS_FROM_ATTRIBUTE_VALIDITY", XOption.REQUIRED,
         XOption.BEGIN_COMPOSITE_6);
   }
}
