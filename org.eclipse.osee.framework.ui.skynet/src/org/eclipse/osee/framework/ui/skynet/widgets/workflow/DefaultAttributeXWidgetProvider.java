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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BinaryAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;

/**
 * @author Donald G. Dunne
 */
public class DefaultAttributeXWidgetProvider implements IAttributeXWidgetProvider {

   private DynamicXWidgetLayoutData createDynamicXWidgetLayout(AttributeType attributeType) {
      DynamicXWidgetLayoutData defaultData = new DynamicXWidgetLayoutData(null);
      defaultData.setName(attributeType.getName());
      defaultData.setStorageName(attributeType.getName());
      defaultData.setToolTip(attributeType.getDescription());
      if (attributeType.getMinOccurrences() > 0) {
         defaultData.getXOptionHandler().add(XOption.REQUIRED);
      }
      defaultData.getXOptionHandler().add(XOption.HORIZONTAL_LABEL);
      return defaultData;
   }

   @Override
   public List<DynamicXWidgetLayoutData> getDynamicXWidgetLayoutData(AttributeType attributeType) throws OseeCoreException {
      int minOccurrence = attributeType.getMinOccurrences();
      int maxOccurrence = attributeType.getMaxOccurrences();

      List<DynamicXWidgetLayoutData> xWidgetLayoutData = new ArrayList<DynamicXWidgetLayoutData>();

      DynamicXWidgetLayoutData defaultData = createDynamicXWidgetLayout(attributeType);
      xWidgetLayoutData.add(defaultData);

      String xWidgetName;
      try {
         xWidgetName = getXWidgetName(defaultData, attributeType, minOccurrence, maxOccurrence);
      } catch (OseeCoreException ex) {
         xWidgetName = "XTextDam";
         StringBuilder builder = new StringBuilder();
         builder.append("Unable to determine base type for attribute type");
         builder.append(String.format("[%]", attributeType.getName()));
         builder.append(Lib.exceptionToString(ex));
         defaultData.setDefaultValue(builder.toString());
      }

      defaultData.setXWidgetName(xWidgetName);
      defaultData.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
      defaultData.getXOptionHandler().add(XOption.NO_DEFAULT_VALUE);

      return xWidgetLayoutData;
   }

   private String getXWidgetName(DynamicXWidgetLayoutData defaultData, AttributeType attributeType, int minOccurrence, int maxOccurrence) throws OseeCoreException {
      String xWidgetName = "";
      if (AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeType)) {
         if (maxOccurrence == 1) {
            xWidgetName =
                  "XComboDam(" + Collections.toString(",", AttributeTypeManager.getEnumerationValues(attributeType)) + ")";
         } else {
            xWidgetName =
                  "XSelectFromMultiChoiceDam(" + Collections.toString(",",
                        AttributeTypeManager.getEnumerationValues(attributeType)) + ")";
         }
      } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeType)) {
         if (minOccurrence == 1) {
            xWidgetName = "XCheckBoxDam";
         } else {
            xWidgetName = "XComboBooleanDam";
         }
      } else if (AttributeTypeManager.isBaseTypeCompatible(WordAttribute.class, attributeType) || attributeType.equals(CoreAttributeTypes.RELATION_ORDER)) {
         xWidgetName = "XStackedDam";
         defaultData.getXOptionHandler().add(XOption.NOT_EDITABLE);
      } else if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
         xWidgetName = "XDateDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeType)) {
         xWidgetName = "XIntegerDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeType)) {
         xWidgetName = "XFloatDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(BinaryAttribute.class, attributeType)) {
         xWidgetName = "XLabelDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(StringAttribute.class, attributeType)) {
         xWidgetName = "XTextDam";
      } else {
         xWidgetName = "XStackedDam";
      }
      return xWidgetName;
   }
}
