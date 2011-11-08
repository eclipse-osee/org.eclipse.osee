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

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.utility.AttributeTypeToXWidgetName;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;

/**
 * @author Donald G. Dunne
 */
public class DefaultAttributeXWidgetProvider implements IAttributeXWidgetProvider {

   private DynamicXWidgetLayoutData createDynamicXWidgetLayout(IAttributeType attributeType, int minOccurrence) {
      DynamicXWidgetLayoutData defaultData = new DynamicXWidgetLayoutData(null);
      defaultData.setName(attributeType.getName());
      defaultData.setStoreName(attributeType.getName());
      defaultData.setToolTip(attributeType.getDescription());
      if (minOccurrence > 0) {
         defaultData.getXOptionHandler().add(XOption.REQUIRED);
      }
      defaultData.getXOptionHandler().add(XOption.HORIZONTAL_LABEL);
      return defaultData;
   }

   public static boolean useMultiLineWidget(IAttributeType attributeType) throws OseeCoreException {
      return AttributeTypeManager.isBaseTypeCompatible(WordAttribute.class, attributeType) || attributeType.equals(CoreAttributeTypes.RelationOrder);
   }

   @Override
   public List<DynamicXWidgetLayoutData> getDynamicXWidgetLayoutData(IAttributeType attributeType) throws OseeCoreException {
      List<DynamicXWidgetLayoutData> xWidgetLayoutData = new ArrayList<DynamicXWidgetLayoutData>();

      DynamicXWidgetLayoutData defaultData =
         createDynamicXWidgetLayout(attributeType, AttributeTypeManager.getMinOccurrences(attributeType));
      xWidgetLayoutData.add(defaultData);

      String xWidgetName;
      try {
         xWidgetName = AttributeTypeToXWidgetName.getXWidgetName(attributeType);
         if (useMultiLineWidget(attributeType)) {
            xWidgetName = "XStackedDam";
            defaultData.getXOptionHandler().add(XOption.NOT_EDITABLE);
         }
      } catch (OseeCoreException ex) {
         xWidgetName = "XTextDam";
         StringBuilder builder = new StringBuilder();
         builder.append("Unable to determine base type for attribute type");
         builder.append(String.format("[%s]", attributeType));
         builder.append(Lib.exceptionToString(ex));
         defaultData.setDefaultValue(builder.toString());
      }

      defaultData.setXWidgetName(xWidgetName);
      defaultData.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
      defaultData.getXOptionHandler().add(XOption.NO_DEFAULT_VALUE);

      return xWidgetLayoutData;
   }

}
