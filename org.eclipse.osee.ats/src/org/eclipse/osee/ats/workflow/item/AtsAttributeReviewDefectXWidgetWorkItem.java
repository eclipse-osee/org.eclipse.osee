/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeReviewDefectXWidgetWorkItem extends WorkWidgetDefinition {

   protected AtsAttributeReviewDefectXWidgetWorkItem(ATSAttributes atsAttribute) {
      super(atsAttribute.getDisplayName() + " - " + atsAttribute.getStoreName(), atsAttribute.getStoreName());
      DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null);
      data.setName(getName());
      data.setStorageName(getId());
      data.setXWidgetName("XDefectViewer");
      set(data);
   }
}
