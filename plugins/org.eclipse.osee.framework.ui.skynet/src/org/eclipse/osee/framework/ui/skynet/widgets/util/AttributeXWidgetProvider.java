/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class AttributeXWidgetProvider implements IAttributeXWidgetProvider {

   private static AttributeXWidgetProvider instance;

   public static AttributeXWidgetProvider get() {
      if (instance == null) {
         instance = new AttributeXWidgetProvider();
      }
      return instance;
   }

   private XWidgetData createDynamicXWidgetLayout(AttributeTypeToken attributeType, int minOccurrence) {
      XWidgetData widData = new XWidgetData();
      widData.setName(attributeType.getUnqualifiedName());
      widData.setStoreName(attributeType.getName());
      widData.setToolTip(attributeType.getDescription());
      if (minOccurrence > 0) {
         widData.add(XOption.REQUIRED);
      }
      widData.add(XOption.HORIZONTAL_LABEL);
      return widData;
   }

   @Override
   public List<XWidgetData> getDynamicXWidgetLayoutData(ArtifactTypeToken artType, AttributeTypeToken attributeType) {
      List<XWidgetData> widDatas = new ArrayList<>();

      XWidgetData widData = createDynamicXWidgetLayout(attributeType, artType.getMin(attributeType));
      widDatas.add(widData);

      WidgetId widgetId;
      try {
         widgetId = getXWidgetId(artType, attributeType, widData);
      } catch (OseeCoreException ex) {
         widgetId = WidgetId.XXTextWidget;
         StringBuilder builder = new StringBuilder();
         builder.append("Unable to determine base type for attribute type");
         builder.append(String.format("[%s]", attributeType));
         builder.append(Lib.exceptionToString(ex));
         widData.setDefaultValue(builder.toString());
      }

      widData.setWidgetId(widgetId);
      widData.add(XOption.FILL_HORIZONTALLY);

      return widDatas;
   }

   public static WidgetId getXWidgetId(ArtifactTypeToken artType, AttributeTypeToken attributeType,
      XWidgetData widData) {
      int minOccurrence = artType.getMin(attributeType);
      int maxOccurrence = artType.getMax(attributeType);
      WidgetId widgetId = WidgetId.SENTINEL;

      Set<DisplayHint> displayHints = attributeType.getDisplayHints();

      // Attempt to get from DisplayHints if type declared which WidgetId
      if (displayHints.contains(DisplayHint.XTextFlat)) {
         widgetId = WidgetId.XTextFlatArtWidget;
      } else if (displayHints.contains(DisplayHint.XArtRef)) {
         widgetId = WidgetId.XHyperlinkArtifactRefIdEntryWidget;
      } else if (displayHints.contains(DisplayHint.XBranchSel)) {
         widgetId = WidgetId.XBranchSelectArtWidget;
      }

      // Else select widget depending on type
      else if (attributeType.isEnumerated()) {
         widgetId = WidgetId.XHyperlinkWfdForEnumAttrArtWidget;
         if (maxOccurrence == 1) {
            widData.add(XOption.SINGLE_SELECT);
         } else {
            widData.add(XOption.MULTI_SELECT);
         }
      } else if (attributeType.isBoolean()) {
         if (minOccurrence == 1) {
            widgetId = WidgetId.XCheckBoxArtWidget;
         } else {
            widgetId = WidgetId.XComboBooleanArtWidget;
         }
      } else if (attributeType.isDate()) {
         widgetId = WidgetId.XDateArtWidget;
      } else if (attributeType.isInteger()) {
         widgetId = WidgetId.XIntegerArtWidget;
      } else if (attributeType.isLong()) {
         widgetId = WidgetId.XLongArtWidget;
      } else if (attributeType.isDouble()) {
         widgetId = WidgetId.XFloatArtWidget;
      } else if (attributeType.isInputStream()) {
         widgetId = WidgetId.XLabelArtWidget;
      } else if (attributeType.isBranchId()) {
         widgetId = WidgetId.XBranchSelectWidget;
      } else if (attributeType.isArtifactId()) {
         widgetId = WidgetId.XListDropViewPersistArtWidget;
      } else if (attributeType.isString()) {
         if (maxOccurrence == 1) {
            if (attributeType.isMultiLine()) {
               widgetId = WidgetId.XStackedArtWidget;
            } else {
               widgetId = WidgetId.XXTextWidget;
            }
         } else {
            if (attributeType.isSingleLine()) {
               widgetId = WidgetId.XTextFlatArtWidget;
            } else {
               widgetId = WidgetId.XStackedArtWidget;
            }
         }
      } else {
         widgetId = WidgetId.XStackedArtWidget;
      }
      return widgetId;
   }

}
