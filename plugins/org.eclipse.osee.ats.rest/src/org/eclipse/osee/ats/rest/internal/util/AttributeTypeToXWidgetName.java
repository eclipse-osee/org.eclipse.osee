/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.EnumEntry;

/**
 * @author Donald G. Dunne
 */
public class AttributeTypeToXWidgetName {

   public static String getXWidgetName(OrcsApi orcsApi, AttributeTypeId attributeType) {
      int minOccurrence = orcsApi.getOrcsTypes().getAttributeTypes().getMinOccurrences(attributeType);
      int maxOccurrence = orcsApi.getOrcsTypes().getAttributeTypes().getMaxOccurrences(attributeType);
      String xWidgetName = "";
      String baseType = orcsApi.getOrcsTypes().getAttributeTypes().getBaseAttributeTypeId(attributeType);
      if (baseType != null) {
         baseType = baseType.toLowerCase();
         if (attributeType.equals(CoreAttributeTypes.AccessContextId)) {
            xWidgetName = "XTextFlatDam";
         } else if (baseType.contains("enum")) {
            if (maxOccurrence == 1) {
               xWidgetName =
                  "XComboDam(" + Collections.toString(",", getEnumerationValues(orcsApi, attributeType)) + ")";
            } else {
               xWidgetName = "XSelectFromMultiChoiceDam(" + Collections.toString(",",
                  getEnumerationValues(orcsApi, attributeType)) + ")";
            }
         } else if (baseType.contains("boolean")) {
            if (minOccurrence == 1) {
               xWidgetName = "XCheckBoxDam";
            } else {
               xWidgetName = "XComboBooleanDam";
            }
         } else if (baseType.contains("date")) {
            xWidgetName = "XDateDam";
         } else if (baseType.contains("integer")) {
            xWidgetName = "XIntegerDam";
         } else if (baseType.contains("floating")) {
            xWidgetName = "XFloatDam";
         } else if (baseType.contains("binary")) {
            xWidgetName = "XLabelDam";
         } else if (baseType.contains("branchreference")) {
            xWidgetName = "XBranchSelectWidget";
         } else if (baseType.contains("artifactreference")) {
            xWidgetName = "XListDropViewWithSave";
         } else if (attributeType.equals(CoreAttributeTypes.IdValue)) {
            xWidgetName = "XTextFlatDam";
         } else if (baseType.contains("string")) {
            if (maxOccurrence == 1) {
               xWidgetName = "XTextDam";
            } else {
               xWidgetName = "XStackedDam";
            }
         } else {
            xWidgetName = "XStackedDam";
         }
      }
      return xWidgetName;
   }

   private static Collection<String> getEnumerationValues(OrcsApi orcsApi, AttributeTypeId attributeType) {
      List<String> values = new ArrayList<>();
      for (EnumEntry entry : orcsApi.getOrcsTypes().getAttributeTypes().getEnumType(attributeType).values()) {
         values.add(entry.getName());
      }
      return values;
   }
}
