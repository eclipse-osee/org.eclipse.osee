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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.utility.AttributeTypeToXWidgetName;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XIntegerDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextFlatDam;

/**
 * @author Donald G. Dunne
 */
public class DefaultAttributeXWidgetProvider implements IAttributeXWidgetProvider {

   private static final Collection<AttributeTypeId> xFlatAttributeTypes = new ArrayList<>();

   static {
      xFlatAttributeTypes.add(CoreAttributeTypes.LoginId);
      xFlatAttributeTypes.add(CoreAttributeTypes.FavoriteBranch);
   }

   private XWidgetData createDynamicXWidgetLayout(AttributeTypeToken attributeType, int minOccurrence) {
      XWidgetData widData = new XWidgetData();
      widData.setName(attributeType.getUnqualifiedName());
      widData.setStoreName(attributeType.getName());
      widData.setToolTip(attributeType.getDescription());
      if (minOccurrence > 0) {
         widData.getXOptionHandler().add(XOption.REQUIRED);
      }
      widData.getXOptionHandler().add(XOption.HORIZONTAL_LABEL);
      return widData;
   }

   public static boolean useMultiLineWidget(AttributeTypeToken attributeType) {
      return attributeType.matches(CoreAttributeTypes.WordTemplateContent) || //
         attributeType.matches(CoreAttributeTypes.PlainTextContent);
   }

   @Override
   public List<XWidgetData> getDynamicXWidgetLayoutData(ArtifactTypeToken artType, AttributeTypeToken attributeType) {
      List<XWidgetData> widDatas = new ArrayList<>();

      XWidgetData widData = createDynamicXWidgetLayout(attributeType, artType.getMin(attributeType));
      widDatas.add(widData);

      String xWidgetName;
      try {
         xWidgetName = AttributeTypeToXWidgetName.getXWidgetName(artType, attributeType);
         if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {
            widData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
            xWidgetName = XTextDam.WIDGET_ID;
         } else if (useMultiLineWidget(attributeType)) {
            xWidgetName = XTextDam.WIDGET_ID;
            widData.getXOptionHandler().add(XOption.NOT_EDITABLE);
            widData.getXOptionHandler().add(XOption.FILL_VERTICALLY);
         } else if (attributeType.isBranchId()) {
            xWidgetName = XBranchSelectWidget.WIDGET_ID;
         } else if (attributeType.isArtifactId() & artType.getMax(attributeType) == 1) {
            xWidgetName = XIntegerDam.WIDGET_ID;
         } else if (xFlatAttributeTypes.contains(attributeType)) {
            xWidgetName = XTextFlatDam.WIDGET_ID;
         }
      } catch (OseeCoreException ex) {
         xWidgetName = "XTextDam";
         StringBuilder builder = new StringBuilder();
         builder.append("Unable to determine base type for attribute type");
         builder.append(String.format("[%s]", attributeType));
         builder.append(Lib.exceptionToString(ex));
         widData.setDefaultValue(builder.toString());
      }

      widData.setXWidgetName(xWidgetName);
      widData.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
      widData.getXOptionHandler().add(XOption.NO_DEFAULT_VALUE);

      return widDatas;
   }
}
