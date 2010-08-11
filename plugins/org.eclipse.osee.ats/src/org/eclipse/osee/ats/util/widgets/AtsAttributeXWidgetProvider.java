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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;

/**
 * Provides XFlatDam as default widget for specified attribute types
 * 
 * @author Donald G. Dunne
 */
public class AtsAttributeXWidgetProvider extends DefaultAttributeXWidgetProvider {

   private static final Collection<IAttributeType> XFLAT_ATTRIBUTE_TYPES = new ArrayList<IAttributeType>();
   static {
      XFLAT_ATTRIBUTE_TYPES.add(CoreAttributeTypes.WorkTransition);
      XFLAT_ATTRIBUTE_TYPES.add(CoreAttributeTypes.WorkData);
      XFLAT_ATTRIBUTE_TYPES.add(AtsAttributeTypes.State);
      XFLAT_ATTRIBUTE_TYPES.add(AtsAttributeTypes.ActionableItem);
      XFLAT_ATTRIBUTE_TYPES.add(AtsAttributeTypes.TeamDefinition);
   }

   @Override
   public List<DynamicXWidgetLayoutData> getDynamicXWidgetLayoutData(AttributeType attributeType) {
      List<DynamicXWidgetLayoutData> layouts = super.getDynamicXWidgetLayoutData(attributeType);
      DynamicXWidgetLayoutData layoutData = layouts.get(0);
      if (XFLAT_ATTRIBUTE_TYPES.contains(attributeType)) {
         layoutData.setXWidgetName("XFlatDam");
      }
      return Arrays.asList(layoutData);
   }
}
