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

import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;

/**
 * Maps OSEE RelationTypeToken to SysML V2 connection def using the relation's metadata (name, side names, side types,
 * multiplicity).
 *
 * @author Donald G. Dunne
 */
public class OseeRelationToSysmlMapper {

   /**
    * Creates a SysmlConnectionDef from an OSEE RelationTypeToken.
    */
   public static SysmlConnectionDef toConnectionDef(RelationTypeToken relType) {
      String name = toSysmlRelationName(relType.getName());
      String endAName = toEndName(relType.getSideName(RelationSide.SIDE_A));
      String endAType = toTypeName(relType.getArtifactType(RelationSide.SIDE_A).getName());
      String endAMult = toMultiplicitySideA(relType.getMultiplicity());
      String endBName = toEndName(relType.getSideName(RelationSide.SIDE_B));
      String endBType = toTypeName(relType.getArtifactType(RelationSide.SIDE_B).getName());
      String endBMult = toMultiplicitySideB(relType.getMultiplicity());
      return new SysmlConnectionDef(name, endAName, endAType, endAMult, endBName, endBType, endBMult);
   }

   /**
    * Creates a SysmlConnection instance from actual relation link data.
    */
   public static SysmlConnection toConnection(RelationTypeToken relType, String sourcePartName,
      String targetPartName) {
      return new SysmlConnection(toSysmlRelationName(relType.getName()), sourcePartName, targetPartName);
   }

   private static String toMultiplicitySideA(RelationTypeMultiplicity mult) {
      switch (mult) {
         case ONE_TO_ONE:
         case ONE_TO_MANY:
            return "[1]";
         case MANY_TO_ONE:
         case MANY_TO_MANY:
            return "[*]";
         default:
            return "[*]";
      }
   }

   private static String toMultiplicitySideB(RelationTypeMultiplicity mult) {
      switch (mult) {
         case ONE_TO_ONE:
         case MANY_TO_ONE:
            return "[1]";
         case ONE_TO_MANY:
         case MANY_TO_MANY:
            return "[*]";
         default:
            return "[*]";
      }
   }

   /**
    * Converts an OSEE relation type name to a SysML-safe identifier (PascalCase, no spaces).
    */
   static String toSysmlRelationName(String oseeName) {
      if (oseeName == null || oseeName.isEmpty()) {
         return "UnknownRelation";
      }
      StringBuilder result = new StringBuilder();
      boolean capitalizeNext = true;
      for (int i = 0; i < oseeName.length(); i++) {
         char c = oseeName.charAt(i);
         if (c == ' ' || c == '-' || c == '/') {
            capitalizeNext = true;
         } else {
            if (capitalizeNext) {
               result.append(Character.toUpperCase(c));
               capitalizeNext = false;
            } else {
               result.append(c);
            }
         }
      }
      return result.toString();
   }

   /**
    * Converts a side name to a camelCase SysML end name.
    */
   static String toEndName(String sideName) {
      if (sideName == null || sideName.isEmpty()) {
         return "end";
      }
      StringBuilder result = new StringBuilder();
      boolean capitalizeNext = false;
      for (int i = 0; i < sideName.length(); i++) {
         char c = sideName.charAt(i);
         if (c == ' ' || c == '-' || c == '/') {
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
      return result.toString();
   }

   /**
    * Converts an artifact type name to a PascalCase SysML type name.
    */
   static String toTypeName(String artTypeName) {
      if (artTypeName == null || artTypeName.isEmpty()) {
         return "Artifact";
      }
      // Strip "ats." prefix if present
      if (artTypeName.startsWith("ats.")) {
         artTypeName = artTypeName.substring(4);
      }
      StringBuilder result = new StringBuilder();
      boolean capitalizeNext = true;
      for (int i = 0; i < artTypeName.length(); i++) {
         char c = artTypeName.charAt(i);
         if (c == ' ' || c == '-' || c == '/' || c == '.') {
            capitalizeNext = true;
         } else {
            if (capitalizeNext) {
               result.append(Character.toUpperCase(c));
               capitalizeNext = false;
            } else {
               result.append(c);
            }
         }
      }
      return result.toString();
   }
}
