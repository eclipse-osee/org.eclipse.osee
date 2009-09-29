/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   @Override
   public List<DynamicXWidgetLayoutData> getDynamicXWidgetLayoutData(AttributeType attributeType) {
      DynamicXWidgetLayoutData layoutData = super.getDynamicXWidgetLayoutData(attributeType).iterator().next();
      if (attributeNames.contains(attributeType.getName())) {
         layoutData.setXWidgetName("XFlatDam");
      }
      return Arrays.asList(layoutData);
   }
}
