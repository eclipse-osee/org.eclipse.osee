/*
 * Created on Jul 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.widgets;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemAttributes;

/**
 * Provides XFlatDam as default widget for specified attribute types
 * 
 * @author Donald G. Dunne
 */
public class AtsAttributeXWidgetProvider extends DefaultAttributeXWidgetProvider {

   List<String> attributeNames =
         Arrays.asList(WorkItemAttributes.TRANSITION.getAttributeTypeName(),
               WorkItemAttributes.WORK_DATA.getAttributeTypeName(), ATSAttributes.STATE_ATTRIBUTE.getStoreName(),
               ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName(),
               ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(),
               ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName());

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultAttributeXWidgetProvider#getDynamicXWidgetLayoutData(org.eclipse.osee.framework.skynet.core.attribute.AttributeType)
    */
   @Override
   public List<DynamicXWidgetLayoutData> getDynamicXWidgetLayoutData(AttributeType attributeType) {
      DynamicXWidgetLayoutData layoutData = super.getDynamicXWidgetLayoutData(attributeType).iterator().next();
      if (attributeNames.contains(attributeType.getName())) {
         layoutData.setXWidgetName("XFlatDam");
      }
      return Arrays.asList(layoutData);
   }
}
