/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeXWidgetWorkItem extends WorkWidgetDefinition {

   public AtsAttributeXWidgetWorkItem(String name, String id, String attributeTypeName, String xWidgetName, XOption... xOption) {
      super(name + " - " + id, id);
      DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null);
      data.setName(name);
      data.setId(id);
      data.setStorageName(attributeTypeName);
      data.setXWidgetName(xWidgetName);
      data.getXOptionHandler().add(xOption);
      set(data);
   }

   public AtsAttributeXWidgetWorkItem(ATSAttributes atsAttribute, String xWidgetName, XOption... xOption) {
      this(atsAttribute.getDisplayName(), atsAttribute.getStoreName(), atsAttribute.getStoreName(), xWidgetName,
            xOption);
   }
}
