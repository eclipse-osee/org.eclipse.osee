/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.EnumEntry;

/**
 * @author Donald G. Dunne
 */
public class AttributeTypeToXWidgetName {

   public static String getXWidgetName(OrcsApi orcsApi, AttributeTypeToken attributeType) {
      int minOccurrence = orcsApi.getOrcsTypes().getAttributeTypes().getMinOccurrences(attributeType);
      int maxOccurrence = orcsApi.getOrcsTypes().getAttributeTypes().getMaxOccurrences(attributeType);
      String xWidgetName = "";
      if (attributeType.equals(CoreAttributeTypes.AccessContextId)) {
         xWidgetName = "XTextFlatDam";
      } else if (attributeType.isEnumerated()) {
         if (maxOccurrence == 1) {
            xWidgetName = "XComboDam(" + Collections.toString(",", getEnumerationValues(orcsApi, attributeType)) + ")";
         } else {
            xWidgetName = "XSelectFromMultiChoiceDam(" + Collections.toString(",",
               getEnumerationValues(orcsApi, attributeType)) + ")";
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
      } else if (attributeType.isDouble()) {
         xWidgetName = "XFloatDam";
      } else if (attributeType.isLong()) {
         xWidgetName = "XLongDam";
      } else if (attributeType.isInputStream()) {
         xWidgetName = "XLabelDam";
      } else if (attributeType.isBranchId()) {
         xWidgetName = "XBranchSelectWidget";
      } else if (attributeType.isArtifactId()) {
         xWidgetName = "XListDropViewWithSave";
      } else if (attributeType.equals(CoreAttributeTypes.IdValue)) {
         xWidgetName = "XTextFlatDam";
      } else if (attributeType.isString()) {
         if (maxOccurrence == 1) {
            xWidgetName = "XTextDam";
         } else {
            xWidgetName = "XStackedDam";
         }
      } else {
         xWidgetName = "XStackedDam";
      }
      return xWidgetName;
   }

   private static Collection<String> getEnumerationValues(OrcsApi orcsApi, AttributeTypeToken attributeType) {
      List<String> values = new ArrayList<>();
      for (EnumEntry entry : orcsApi.getOrcsTypes().getAttributeTypes().getEnumType(attributeType).values()) {
         values.add(entry.getName());
      }
      return values;
   }
}
