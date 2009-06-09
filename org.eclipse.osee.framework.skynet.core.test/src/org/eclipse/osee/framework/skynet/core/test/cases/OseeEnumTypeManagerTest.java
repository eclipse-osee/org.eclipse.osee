/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType.OseeEnumEntry;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Before;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeManagerTest {

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(TestUtil.isProductionDb());
   }

   @org.junit.Test
   public void testCreateEnumTypeFromXml() throws OseeCoreException {
      String enumTypeName = "EnumType1";
      String xmlDefinition = "<Root><Enum>one</Enum><Enum>two</Enum><Enum>three</Enum></Root>";

      OseeEnumTypeManager.createEnumTypeFromXml(enumTypeName, xmlDefinition);
      OseeEnumType actual = OseeEnumTypeManager.getUniqueType(enumTypeName);
      try {
         checkOseeEnumType(enumTypeName, new String[] {"one", "two", "three"}, new Integer[] {0, 1, 2}, actual);
      } finally {
         OseeEnumTypeManager.deleteEnumType(actual);
      }
      checkOseeEnumTypeDeleted(actual);
   }

   @org.junit.Test
   public void testCreateEnumType() throws OseeCoreException {
      String enumTypeName = "EnumType2";
      String[] entryNames = new String[] {"oneA", "twoA", "threeA"};
      Integer[] entryOrdinals = new Integer[] {1, 50, 100};

      List<ObjectPair<String, Integer>> entries = new ArrayList<ObjectPair<String, Integer>>();
      for (int index = 0; index < entryNames.length && index < entryOrdinals.length; index++) {
         entries.add(new ObjectPair<String, Integer>(entryNames[index], entryOrdinals[index]));
      }

      OseeEnumTypeManager.createEnumType(enumTypeName, entries);
      OseeEnumType actual = OseeEnumTypeManager.getUniqueType(enumTypeName);
      try {
         checkOseeEnumType(enumTypeName, entryNames, entryOrdinals, actual);
      } finally {
         OseeEnumTypeManager.deleteEnumType(actual);
      }
      checkOseeEnumTypeDeleted(actual);
   }

   @SuppressWarnings("unchecked")
   @org.junit.Test
   public void testAddEntriesToType() throws OseeCoreException {
      String enumTypeName = "EnumType10";
      String xmlDefinition = "<Root><Enum>one</Enum><Enum>two</Enum><Enum>three</Enum></Root>";
      String[] entryNames = new String[] {"one", "two", "three"};
      Integer[] entryOrdinals = new Integer[] {0, 1, 2};

      OseeEnumType oseeEnumType = OseeEnumTypeManager.createEnumTypeFromXml(enumTypeName, xmlDefinition);
      try {
         checkOseeEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);

         try {
            OseeEnumTypeManager.addEntries(oseeEnumType, new ObjectPair<String, Integer>("one", 3));
            assertTrue("Should have exceptioned - Error", false);
         } catch (Exception ex) {
            assertTrue("name violated", ex instanceof OseeArgumentException);
            // check for no change
            checkOseeEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);
         }

         try {
            OseeEnumTypeManager.addEntries(oseeEnumType, new ObjectPair<String, Integer>("four", 2));
            assertTrue("Should have exceptioned - Error", false);
         } catch (Exception ex) {
            assertTrue("Ordinal violated", ex instanceof OseeArgumentException);
            // Check for no change
            checkOseeEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);
         }

         OseeEnumTypeManager.addEntries(oseeEnumType, new ObjectPair<String, Integer>("four", 3));
         checkOseeEnumType(enumTypeName, new String[] {"one", "two", "three", "four"}, new Integer[] {0, 1, 2, 3},
               oseeEnumType);

         OseeEnumType actual = OseeEnumTypeManager.getUniqueType(enumTypeName);
         assertEquals(oseeEnumType, actual);

      } finally {
         OseeEnumTypeManager.deleteEnumType(oseeEnumType);
      }
      checkOseeEnumTypeDeleted(oseeEnumType);
   }

   @org.junit.Test
   public void testRemoveEntriesToType() throws OseeCoreException {
      String enumTypeName = "EnumType4";
      String xmlDefinition = "<Root><Enum>one</Enum><Enum>two</Enum><Enum>three</Enum></Root>";
      String[] entryNames = new String[] {"one", "two", "three"};
      Integer[] entryOrdinals = new Integer[] {0, 1, 2};

      OseeEnumType oseeEnumType = OseeEnumTypeManager.createEnumTypeFromXml(enumTypeName, xmlDefinition);
      try {
         checkOseeEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);

         List<String> names = new ArrayList<String>(Arrays.asList(entryNames));
         List<Integer> ordinals = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
         for (OseeEnumEntry entry : oseeEnumType.values()) {
            OseeEnumTypeManager.removeEntries(oseeEnumType, entry);
            names.remove(0);
            ordinals.remove(0);
            checkOseeEnumType(enumTypeName, names.toArray(new String[names.size()]),
                  ordinals.toArray(new Integer[ordinals.size()]), oseeEnumType);
            OseeEnumType actual = OseeEnumTypeManager.getUniqueType(enumTypeName);
            assertEquals(oseeEnumType, actual);
         }

         assertEquals(0, oseeEnumType.values().length);
      } finally {
         OseeEnumTypeManager.deleteEnumType(oseeEnumType);
      }
      checkOseeEnumTypeDeleted(oseeEnumType);
   }

   @org.junit.Test
   public void testDeletedRetrieval() throws OseeCoreException {
      String enumTypeName = "EnumType5";
      String xmlDefinition = "<Root><Enum>one</Enum><Enum>two</Enum><Enum>three</Enum></Root>";
      String[] entryNames = new String[] {"one", "two", "three"};
      Integer[] entryOrdinals = new Integer[] {0, 1, 2};

      OseeEnumType oseeEnumType = OseeEnumTypeManager.createEnumTypeFromXml(enumTypeName, xmlDefinition);
      try {
         checkOseeEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);
      } finally {
         OseeEnumTypeManager.deleteEnumType(oseeEnumType);
      }
      checkOseeEnumTypeDeleted(oseeEnumType);

      assertTrue(OseeEnumTypeManager.getAllTypeNames(true).contains(enumTypeName));
      assertTrue(OseeEnumTypeManager.getAllTypes(true).contains(oseeEnumType));

      OseeEnumType actual = OseeEnumTypeManager.getType(oseeEnumType.getEnumTypeId(), true);
      assertEquals(oseeEnumType, actual);

      actual = OseeEnumTypeManager.getUniqueType(enumTypeName, true);
      assertEquals(oseeEnumType, actual);
   }

   @org.junit.Test
   public void testDeletedNotAllowedWhileInUseByAttribute() throws OseeCoreException {
      Collection<OseeEnumType> types = OseeEnumTypeManager.getAllTypes();
      boolean wasTestedAtLeastOnce = false;
      for (OseeEnumType type : types) {
         for (AttributeType attrType : AttributeTypeManager.getAllTypes()) {
            if (attrType.getOseeEnumTypeId() == type.getEnumTypeId()) {
               // Found an enum that is in use;
               wasTestedAtLeastOnce = true;
               try {
                  OseeEnumTypeManager.deleteEnumType(type);
               } catch (Exception ex) {
                  assertTrue(ex instanceof OseeStateException);
               }
               assertTrue(!type.isDeleted());
            }
         }
      }
      assertTrue(wasTestedAtLeastOnce);
   }

   private void checkOseeEnumTypeDeleted(OseeEnumType actual) throws OseeDataStoreException {
      assertTrue(actual.isDeleted());
      try {
         OseeEnumTypeManager.getType(actual.getEnumTypeId());
      } catch (Exception ex) {
         assertTrue(ex instanceof OseeTypeDoesNotExist);
      }
      try {
         OseeEnumTypeManager.getUniqueType(actual.getEnumTypeName());
      } catch (Exception ex) {
         assertTrue(ex instanceof OseeTypeDoesNotExist);
      }

      assertTrue(!OseeEnumTypeManager.getAllTypes().contains(actual));
      assertTrue(!OseeEnumTypeManager.getAllTypeNames().contains(actual.getEnumTypeName()));
   }

   private void checkOseeEnumType(String expectedName, String[] expectedEntries, Integer[] expectedOrdinals, OseeEnumType actualEnumType) throws OseeDataStoreException, OseeTypeDoesNotExist {
      assertEquals(expectedName, actualEnumType.getEnumTypeName());
      OseeEnumEntry[] enumEntries = actualEnumType.values();
      assertEquals(expectedEntries.length, enumEntries.length);
      for (int index = 0; index < expectedEntries.length && index < expectedOrdinals.length; index++) {
         checkEntry(expectedEntries[index], expectedOrdinals[index], actualEnumType, enumEntries[index]);
      }
   }

   private void checkEntry(String expectedName, int expectedOrdinal, OseeEnumType parent, OseeEnumEntry entry) {
      assertEquals(expectedName, entry.name());
      assertEquals(expectedOrdinal, entry.ordinal());
      assertEquals(parent, entry.getDeclaringClass());
      assertEquals(parent.getEnumTypeId(), entry.getEnumTypeId());
      assertEquals(parent.getEnumTypeName(), entry.getEnumTypeName());
      OseeEnumEntry[] expected = parent.values();
      OseeEnumEntry[] actual = entry.values();
      assertEquals(expected.length, actual.length);
      for (int index = 0; index < expected.length; index++) {
         assertEquals(expected[index], actual[index]);
      }
   }
}
