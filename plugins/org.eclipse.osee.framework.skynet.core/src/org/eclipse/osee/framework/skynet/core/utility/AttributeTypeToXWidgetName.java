/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Donald G. Dunne
 */
public class AttributeTypeToXWidgetName {

   public static <T extends EnumToken> String getXWidgetName(ArtifactTypeToken artType, AttributeTypeToken attributeType) {
      int minOccurrence = artType.getMin(attributeType);
      int maxOccurrence = artType.getMax(attributeType);
      String xWidgetName = "";
      if (attributeType.isEnumerated()) {
         AttributeTypeEnum<T> enumeratedType =
            (AttributeTypeEnum<T>) ServiceUtil.getOrcsTokenService().getAttributeType(attributeType.getId());
         artType = ServiceUtil.getOrcsTokenService().getArtifactType(artType.getId());
         if (maxOccurrence == 1) {
            xWidgetName = "XComboDam(" + Collections.toString(",", artType.getValidEnumValues(enumeratedType)) + ")";
         } else {
            xWidgetName = "XSelectFromMultiChoiceDam(" + Collections.toString(",",
               artType.getValidEnumValues(enumeratedType)) + ")";
         }
      } else if (attributeType.isBoolean()) {
         if (minOccurrence == 1) {
            xWidgetName = "XCheckBoxDam";
         } else {
            xWidgetName = "XComboBooleanDam";
         }
      } else if (attributeType.isDate()) {
         xWidgetName = "XDateDam";
      } else if (attributeType.isInteger()) {
         xWidgetName = "XIntegerDam";
      } else if (attributeType.isLong()) {
         xWidgetName = "XLongDam";
      } else if (attributeType.isDouble()) {
         xWidgetName = "XFloatDam";
      } else if (attributeType.isInputStream()) {
         xWidgetName = "XLabelDam";
      } else if (attributeType.isBranchId()) {
         xWidgetName = "XBranchSelectWidget";
      } else if (attributeType.isArtifactId()) {
         xWidgetName = "XListDropViewWithSave";
      } else if (attributeType.isString()) {
         if (maxOccurrence == 1) {
            if (attributeType.isMultiLine()) {
               xWidgetName = "XStackedDam";
            } else {
               xWidgetName = "XTextDam";
            }
         } else {
            if (attributeType.isSingleLine()) {
               xWidgetName = "XTextFlatDam";
            } else {
               xWidgetName = "XStackedDam";
            }
         }
      } else {
         xWidgetName = "XStackedDam";
      }
      return xWidgetName;
   }
}