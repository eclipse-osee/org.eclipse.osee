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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * Provides XFlatDam as default widget for specified attribute types
 * 
 * @author Donald G. Dunne
 */
public class AtsAttributeXWidgetProvider extends DefaultAttributeXWidgetProvider {

   private static final Collection<IAttributeType> XFLAT_ATTRIBUTE_TYPES = new ArrayList<>();

   static {
      XFLAT_ATTRIBUTE_TYPES.add(CoreAttributeTypes.WorkTransition);
      XFLAT_ATTRIBUTE_TYPES.add(CoreAttributeTypes.WorkData);
      XFLAT_ATTRIBUTE_TYPES.add(AtsAttributeTypes.State);
      XFLAT_ATTRIBUTE_TYPES.add(AtsAttributeTypes.ActionableItem);
      XFLAT_ATTRIBUTE_TYPES.add(AtsAttributeTypes.TeamDefinition);
   }

   @Override
   public List<XWidgetRendererItem> getDynamicXWidgetLayoutData(IAttributeType attributeType) throws OseeCoreException {
      List<XWidgetRendererItem> layouts = new ArrayList<>();
      if (attributeType.equals(AtsAttributeTypes.BaselineBranchUuid)) {
         layouts = super.getDynamicXWidgetLayoutData(attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName("XBranchSelectWidgetDam");
      } else if (XFLAT_ATTRIBUTE_TYPES.contains(attributeType)) {
         layouts = super.getDynamicXWidgetLayoutData(attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.setXWidgetName("XTextFlatDam");
      } else if (attributeType.getName().equals(AtsAttributeTypes.DslSheet.getName()) || attributeType.equals(
         AtsAttributeTypes.TestToSourceLocator)) {
         layouts = super.getDynamicXWidgetLayoutData(attributeType);
         XWidgetRendererItem layoutData = layouts.get(0);
         layoutData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
      }
      return layouts;
   }
}
