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

package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;

/**
 * @author Donald G. Dunne
 */
public class DefaultAttributeXWidgetProvider implements IAttributeXWidgetProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IAttributeXWidgetProvider#getDynamicXWidgetLayoutData(org.eclipse.osee.framework.skynet.core.attribute.Attribute)
    */
   @Override
   public DynamicXWidgetLayoutData getDynamicXWidgetLayoutData(AttributeType attributeType) {
      int min = attributeType.getMinOccurrences();
      int max = attributeType.getMaxOccurrences();
      DynamicXWidgetLayoutData defaultData = new DynamicXWidgetLayoutData(null);
      defaultData.setName(attributeType.getName());
      defaultData.setRequired(attributeType.getMinOccurrences() > 0);
      defaultData.setToolTip(attributeType.getTipText());
      defaultData.setHorizontalLabel(true);
      defaultData.setlayoutName(attributeType.getName());
      if (min == 1) defaultData.setRequired(true);
      if (attributeType.getBaseAttributeClass().equals(EnumeratedAttribute.class)) {
         if (max == 1) {
            defaultData.setXWidgetName("XComboDam(" + Collections.toString(",",
                  EnumeratedAttribute.getChoices(attributeType)) + ")");
         } else {
            defaultData.setXWidgetName("XListDam(" + Collections.toString(",",
                  EnumeratedAttribute.getChoices(attributeType)) + ")");
            defaultData.setHorizontalLabel(false);
         }
      } else if (attributeType.getBaseAttributeClass().equals(StringAttribute.class)) {
         if (max == 1) {
            defaultData.setXWidgetName("XTextDam");
         } else {
            defaultData.setXWidgetName("XMultiXWidgetTextDam");
            System.err.println("How handle multiple text instances?");
         }
      } else if (attributeType.getBaseAttributeClass().equals(BooleanAttribute.class)) {
         if (min == 1) {
            defaultData.setXWidgetName("XCheckBox");
         } else {
            defaultData.setXWidgetName("XComboBooleanDam");
         }
      } else if (attributeType.getBaseAttributeClass().equals(DateAttribute.class)) {
         if (max <= 1) {
            defaultData.setXWidgetName("XDateDam");
         } else {
            defaultData.setXWidgetName("XLabelDam");
            System.err.println("How handle multiple text instances?");
         }
      } else {
         defaultData.setXWidgetName("XLabelDam");
      }
      return defaultData;
   }

}
