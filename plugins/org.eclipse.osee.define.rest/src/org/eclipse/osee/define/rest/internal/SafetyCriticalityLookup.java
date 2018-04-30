/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author David W. Miller
 */
public class SafetyCriticalityLookup {
   private final static String[][] criticalityMap = {
      {"A", "A", "C", "D", "E"},
      {"A", "B", "C", "D", "E"},
      {"B", "C", "D", "D", "E"},
      {"C", "D", "D", "D", "E"},
      {"E", "E", "E", "E", "E"}};
   private final static Map<String, Integer> controlCategoryMap =
      ImmutableMap.of("1(AT)", 0, "2(SAT)", 1, "3(RFT)", 2, "4(IN)", 3, "5(NSI)", 4);
   private final static Map<String, Integer> severityCategoryMap =
      ImmutableMap.of("I", 0, "II", 1, "III", 2, "IV", 3, "NH", 4);
   private final static Map<String, Integer> DALCategoryMap = ImmutableMap.of("A", 0, "B", 1, "C", 2, "D", 3, "E", 4);
   private final static Map<String, String> safetyCriticalityMap =
      ImmutableMap.of("I", "A", "II", "B", "III", "C", "IV", "D", "NH", "E");

   public static String getDALFromControlCategoryAndSeverity(String controlCategory, String severity) {
      String toReturn = AttributeId.UNSPECIFIED;
      if (!AttributeId.UNSPECIFIED.equals(controlCategory) && !AttributeId.UNSPECIFIED.equals(severity)) {
         toReturn = criticalityMap[getControlLevel(controlCategory)][getSeverityLevel(severity)];
      }
      return toReturn;
   }

   public static String getDALLevelFromInt(Integer level) {
      if (level < 0 || level > 4) {
         throw new OseeArgumentException("Invalid DAL level: %d", level);
      }
      return criticalityMap[1][level];
   }

   public static String getDALLevelFromSeverityCategory(String sevCat) {
      String toReturn = AttributeId.UNSPECIFIED;
      if (!AttributeId.UNSPECIFIED.equals(sevCat)) {
         if (!safetyCriticalityMap.keySet().contains(sevCat)) {
            throw new OseeArgumentException("Invalid severity category: %s", sevCat);
         }
         toReturn = safetyCriticalityMap.get(sevCat);
      }
      return toReturn;
   }

   public static Integer getSeverityLevel(String sevCat) {
      if (!severityCategoryMap.keySet().contains(sevCat)) {
         throw new OseeArgumentException("Invalid severity category: %s", sevCat);
      }
      return severityCategoryMap.get(sevCat);
   }

   public static Integer getControlLevel(String controlCategory) {
      if (!controlCategoryMap.keySet().contains(controlCategory)) {
         throw new OseeArgumentException("Invalid control category: %s", controlCategory);
      }
      return controlCategoryMap.get(controlCategory);
   }

   public static Integer getDALLevel(String DALCategory) {
      if (!DALCategoryMap.keySet().contains(DALCategory)) {
         throw new OseeArgumentException("Invalid DAL category: %s", DALCategory);
      }
      return DALCategoryMap.get(DALCategory);
   }

}
