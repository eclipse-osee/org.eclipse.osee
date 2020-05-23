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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactReferenceAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BinaryAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BranchReferenceAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;

/**
 * @author Donald G. Dunne
 */
public class AttributeTypeToXWidgetName {

   public static String getXWidgetName(AttributeTypeToken attributeType) {
      int minOccurrence = AttributeTypeManager.getMinOccurrences(attributeType);
      int maxOccurrence = AttributeTypeManager.getMaxOccurrences(attributeType);
      String xWidgetName = "";
      if (attributeType.equals(CoreAttributeTypes.AccessContextId)) {
         xWidgetName = "XTextFlatDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeType)) {
         if (maxOccurrence == 1) {
            xWidgetName =
               "XComboDam(" + Collections.toString(",", AttributeTypeManager.getEnumerationValues(attributeType)) + ")";
         } else {
            xWidgetName = "XSelectFromMultiChoiceDam(" + Collections.toString(",",
               AttributeTypeManager.getEnumerationValues(attributeType)) + ")";
         }
      } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeType)) {
         if (minOccurrence == 1) {
            xWidgetName = "XCheckBoxDam";
         } else {
            xWidgetName = "XComboBooleanDam";
         }
      } else if (AttributeTypeManager.isBaseTypeCompatible(WordAttribute.class,
         attributeType) || attributeType.equals(CoreAttributeTypes.RelationOrder)) {
         xWidgetName = "XStackedDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
         xWidgetName = "XDateDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeType)) {
         xWidgetName = "XIntegerDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(LongAttribute.class, attributeType)) {
         xWidgetName = "XLongDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeType)) {
         xWidgetName = "XFloatDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(BinaryAttribute.class, attributeType)) {
         xWidgetName = "XLabelDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(BranchReferenceAttribute.class, attributeType)) {
         xWidgetName = "XBranchSelectWidget";
      } else if (AttributeTypeManager.isBaseTypeCompatible(ArtifactReferenceAttribute.class, attributeType)) {
         xWidgetName = "XListDropViewWithSave";
      } else if (attributeType.equals(CoreAttributeTypes.IdValue)) {
         xWidgetName = "XTextFlatDam";
      } else if (AttributeTypeManager.isBaseTypeCompatible(StringAttribute.class, attributeType)) {
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

}
