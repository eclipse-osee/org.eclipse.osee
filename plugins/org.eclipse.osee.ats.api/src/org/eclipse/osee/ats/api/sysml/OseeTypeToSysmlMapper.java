/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.api.sysml;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * Maps OSEE AttributeTypeToken to SysML V2 type strings using the token's type introspection methods.
 *
 * @author Donald G. Dunne
 */
public class OseeTypeToSysmlMapper {

   /**
    * Returns the SysML V2 type name for a given OSEE attribute type.
    */
   public static String toSysmlType(AttributeTypeToken attrType) {
      if (attrType.isEnumerated()) {
         return toSysmlIdentifier(attrType.getName());
      } else if (attrType.isBoolean()) {
         return SysmlConstants.BOOLEAN;
      } else if (attrType.isInteger()) {
         return SysmlConstants.INTEGER;
      } else if (attrType.isLong() || attrType.isArtifactId() || attrType.isBranchId()) {
         return SysmlConstants.INTEGER;
      } else if (attrType.isDouble()) {
         return SysmlConstants.REAL;
      } else {
         // Date, String, and anything else -> String
         return SysmlConstants.STRING;
      }
   }

   /**
    * Creates a SysmlEnumDef from an enumerated attribute type.
    */
   public static SysmlEnumDef toEnumDef(AttributeTypeToken attrType) {
      if (!attrType.isEnumerated()) {
         throw new IllegalArgumentException("Attribute type is not enumerated: " + attrType.getName());
      }
      AttributeTypeEnum<?> enumType = attrType.toEnum();
      return new SysmlEnumDef(toSysmlIdentifier(attrType.getName()), enumType.getEnumStrValues());
   }

   /**
    * Converts an OSEE attribute name to a valid SysML identifier (camelCase, no spaces).
    */
   public static String toSysmlIdentifier(String oseeName) {
      if (oseeName == null || oseeName.isEmpty()) {
         return "_";
      }
      // Replace spaces and special chars
      StringBuilder result = new StringBuilder();
      boolean capitalizeNext = false;
      for (int i = 0; i < oseeName.length(); i++) {
         char c = oseeName.charAt(i);
         if (c == ' ' || c == '-' || c == '/' || c == '.') {
            capitalizeNext = true;
         } else {
            if (capitalizeNext && result.length() > 0) {
               result.append(Character.toUpperCase(c));
               capitalizeNext = false;
            } else {
               result.append(i == 0 ? Character.toLowerCase(c) : c);
            }
         }
      }
      String id = result.toString();
      if (id.isEmpty()) {
         return "_";
      }
      if (Character.isDigit(id.charAt(0))) {
         return "_" + id;
      }
      return id;
   }

   /**
    * Returns the SysML multiplicity string for an OSEE attribute declaration pattern. The multiplicity is determined by
    * how the artifact type declares the attribute (exactlyOne, zeroOrOne, any).
    *
    * @param isMultiValued true if the attribute can have multiple values (.any())
    * @param isRequired true if the attribute must have a value (.exactlyOne())
    */
   public static String toSysmlMultiplicity(boolean isMultiValued, boolean isRequired) {
      if (isMultiValued) {
         return SysmlConstants.MULTIPLICITY_ZERO_OR_MORE;
      }
      if (isRequired) {
         return SysmlConstants.MULTIPLICITY_ONE;
      }
      return SysmlConstants.MULTIPLICITY_ZERO_OR_ONE;
   }
}
