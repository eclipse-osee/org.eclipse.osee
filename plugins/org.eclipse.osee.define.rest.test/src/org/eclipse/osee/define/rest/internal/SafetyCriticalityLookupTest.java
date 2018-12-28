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

import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author David W. Miller
 */
public class SafetyCriticalityLookupTest {
   private final DoubleKeyHashMap<String, String, String> criticalityTable =
      new DoubleKeyHashMap<>();

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Test
   public void testCriticalityLookup() {
      initTestData();
      String[] controlCats = {"1(AT)", "2(SAT)", "3(RFT)", "4(IN)", "5(NSI)"};
      String[] sevCats = {"I", "II", "III", "IV", "NH"};

      for (int i = 0; i < 5; i++) {
         for (int j = 0; j < 5; j++) {
            Assert.assertEquals(getDALFromMap(controlCats[i], sevCats[j]),
               SafetyCriticalityLookup.getDALFromControlCategoryAndSeverity(controlCats[i], sevCats[j]));
         }
      }

   }

   @Test
   public void testInvalidStringControlCat() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid control category: crud");
      SafetyCriticalityLookup.getDALFromControlCategoryAndSeverity("crud", "II");
   }

   @Test
   public void testInvalidStringSevCat() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid severity category: crud");
      SafetyCriticalityLookup.getDALFromControlCategoryAndSeverity("1(AT)", "crud");
   }

   @Test
   public void testNullSevCat() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid severity category: null");
      SafetyCriticalityLookup.getDALFromControlCategoryAndSeverity("1(AT)", null);
   }

   @Test
   public void testGetDALLevelFromSeverityCategory() {
      String[] sevCats = {"I", "II", "III", "IV", "NH"};
      String[] expected = {"A", "B", "C", "D", "E"};
      for (int i = 0; i < 5; i++) {
         Assert.assertEquals(expected[i], SafetyCriticalityLookup.getDALLevelFromSeverityCategory(sevCats[i]));
      }
   }

   @Test
   public void testGetDALLevel() {
      String[] expected = {"A", "B", "C", "D", "E"};
      for (Integer i = 0; i < 5; i++) {
         Assert.assertEquals(expected[i], SafetyCriticalityLookup.getDALLevelFromInt(i));
      }
   }

   @Test
   public void testLevels() {
      String[] givenControlCats = {"1(AT)", "2(SAT)", "3(RFT)", "4(IN)", "5(NSI)"};
      String[] givenSevCats = {"I", "II", "III", "IV", "NH"};
      String[] givenDALs = {"A", "B", "C", "D", "E"};
      for (Integer i = 0; i < 5; i++) {
         Assert.assertEquals(i, SafetyCriticalityLookup.getControlLevel(givenControlCats[i]));
         Assert.assertEquals(i, SafetyCriticalityLookup.getSeverityLevel(givenSevCats[i]));
         Assert.assertEquals(i, SafetyCriticalityLookup.getDALLevel(givenDALs[i]));
      }
   }

   private void initTestData() {
      criticalityTable.put("1(AT)", "I", "A");
      criticalityTable.put("1(AT)", "II", "A");
      criticalityTable.put("1(AT)", "III", "C");
      criticalityTable.put("1(AT)", "IV", "D");
      criticalityTable.put("1(AT)", "NH", "E");
      criticalityTable.put("2(SAT)", "I", "A");
      criticalityTable.put("2(SAT)", "II", "B");
      criticalityTable.put("2(SAT)", "III", "C");
      criticalityTable.put("2(SAT)", "IV", "D");
      criticalityTable.put("2(SAT)", "NH", "E");
      criticalityTable.put("3(RFT)", "I", "B");
      criticalityTable.put("3(RFT)", "II", "C");
      criticalityTable.put("3(RFT)", "III", "D");
      criticalityTable.put("3(RFT)", "IV", "D");
      criticalityTable.put("3(RFT)", "NH", "E");
      criticalityTable.put("4(IN)", "I", "C");
      criticalityTable.put("4(IN)", "II", "D");
      criticalityTable.put("4(IN)", "III", "D");
      criticalityTable.put("4(IN)", "IV", "D");
      criticalityTable.put("4(IN)", "NH", "E");
      criticalityTable.put("5(NSI)", "I", "E");
      criticalityTable.put("5(NSI)", "II", "E");
      criticalityTable.put("5(NSI)", "III", "E");
      criticalityTable.put("5(NSI)", "IV", "E");
      criticalityTable.put("5(NSI)", "NH", "E");
   }

   private String getDALFromMap(String severity, String controlCategory) {
      return criticalityTable.get(severity, controlCategory);
   }
}
