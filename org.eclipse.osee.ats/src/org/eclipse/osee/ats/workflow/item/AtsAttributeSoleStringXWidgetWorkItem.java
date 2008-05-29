/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemXWidgetDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData.Fill;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeSoleStringXWidgetWorkItem extends WorkItemXWidgetDefinition {

   protected AtsAttributeSoleStringXWidgetWorkItem(ATSAttributes atsAttribute, Fill fill) {
      super(atsAttribute.getDisplayName(), atsAttribute.getStoreName());
      DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null);
      data.setName(getName());
      data.setlayoutName(getId());
      data.setXWidgetName("XTextDam");
      data.setFill(fill);
      set(data);
   }
}
