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

package org.eclipse.osee.framework.skynet.core.test.types;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeManagerTest {

   //   private static OseeTypeCache typeCache;
   //   private static List<OseeEnumType> oseeEnumTypes;
   //   private static OseeTypeFactory factory;
   //   private static TestDataAccessor testAccessor;
   //
   //   @BeforeClass
   //   public static void prepareTestData() throws OseeCoreException {
   //      oseeEnumTypes = new ArrayList<OseeEnumType>();
   //      factory = new OseeTypeFactory();
   //
   ////      testAccessor = new TestDataAccessor(oseeEnumTypes);
   ////      typeCache = new OseeTypeCache(testAccessor, factory);
   ////
   ////      typeCache.getEnumTypeCache().getAllTypes();
   ////      Assert.assertTrue(testAccessor.isLoadAllArtifactTypes());
   ////      Assert.assertTrue(testAccessor.isLoadAllAttributeTypes());
   ////      Assert.assertTrue(testAccessor.isLoadAllRelationTypes());
   ////      Assert.assertTrue(testAccessor.isLoadAllOseeEnumTypes());
   //   }

   @org.junit.Test
   public void testThis() throws OseeCoreException {
      assertTrue("Fix this test - These test cases need to be moved into new database types tests - Roberto", false);
   }
   //   //   @org.junit.Test
   //   //   public void testCreateEnumTypeFromXml() throws OseeCoreException {
   //   //      String enumTypeName = "EnumType1";
   //   //      String xmlDefinition = "<Root><Enum>one</Enum><Enum>two</Enum><Enum>three</Enum></Root>";
   //   //
   //   //      OseeEnumTypeManager.createEnumTypeFromXml(enumTypeName, xmlDefinition);
   //   //      OseeEnumType actual = OseeEnumTypeManager.getUniqueType(enumTypeName);
   //   //      try {
   //   //         checkOseeEnumType(enumTypeName, new String[] {"one", "two", "three"}, new Integer[] {0, 1, 2}, actual);
   //   //      } finally {
   //   //         OseeEnumTypeManager.deleteEnumType(actual);
   //   //      }
   //   //      checkOseeEnumTypeDeleted(actual);
   //   //   }
   //
   //   @org.junit.Test
   //   public void testCreateEnumType() throws OseeCoreException {
   //      //      String enumTypeName = "EnumType2";
   //      //      String[] entryNames = new String[] {"oneA", "twoA", "threeA"};
   //      //      Integer[] entryOrdinals = new Integer[] {1, 50, 100};
   //      //
   //      //      List<Pair<String, Integer>> entries = new ArrayList<Pair<String, Integer>>();
   //      //      for (int index = 0; index < entryNames.length && index < entryOrdinals.length; index++) {
   //      //         entries.add(new Pair<String, Integer>(entryNames[index], entryOrdinals[index]));
   //      //      }
   //      //
   //      //      OseeEnumType enumType = factory.createEnumType(GUID.create(), enumTypeName, typeCache);
   //      //      enumType.addEntries(entries);
   //      //
   //      //      typeCache.getEnumTypeData().getTypeByGuid(enumType.getGuid());
   //      //
   //      //      OseeEnumType actual = OseeEnumTypeManager.getUniqueType(enumTypeName);
   //      //      try {
   //      //         TestOseeTypesUtil.checkEnumType(enumTypeName, entryNames, entryOrdinals, actual);
   //      //      } finally {
   //      //         OseeEnumTypeManager.deleteEnumType(actual);
   //      //      }
   //      //      checkOseeEnumTypeDeleted(actual);
   //   }
   //
   //   @SuppressWarnings("unchecked")
   //   @org.junit.Test
   //   public void testAddEntriesToType() throws OseeCoreException {
   //      String enumTypeName = "EnumType10";
   //      String xmlDefinition = "<Root><Enum>one</Enum><Enum>two</Enum><Enum>three</Enum></Root>";
   //      String[] entryNames = new String[] {"one", "two", "three"};
   //      Integer[] entryOrdinals = new Integer[] {0, 1, 2};
   //
   //      OseeEnumType oseeEnumType = OseeEnumTypeManager.createEnumTypeFromXml(enumTypeName, xmlDefinition);
   //      try {
   //         TestOseeTypesUtil.checkEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);
   //
   //         try {
   //            oseeEnumType.addEntries(new Pair<String, Integer>("one", 3));
   //            assertTrue("Should have exceptioned - Error", false);
   //         } catch (Exception ex) {
   //            assertTrue("name violated", ex instanceof OseeArgumentException);
   //            // check for no change
   //            TestOseeTypesUtil.checkEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);
   //         }
   //
   //         try {
   //            oseeEnumType.addEntries(new Pair<String, Integer>("four", 2));
   //            assertTrue("Should have exceptioned - Error", false);
   //         } catch (Exception ex) {
   //            assertTrue("Ordinal violated", ex instanceof OseeArgumentException);
   //            // Check for no change
   //            TestOseeTypesUtil.checkEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);
   //         }
   //
   //         oseeEnumType.addEntries(new Pair<String, Integer>("four", 3));
   //         TestOseeTypesUtil.checkEnumType(enumTypeName, new String[] {"one", "two", "three", "four"}, new Integer[] {0,
   //               1, 2, 3}, oseeEnumType);
   //
   //         OseeEnumType actual = OseeEnumTypeManager.getUniqueType(enumTypeName);
   //         assertEquals(oseeEnumType, actual);
   //
   //      } finally {
   //         OseeEnumTypeManager.deleteEnumType(oseeEnumType);
   //      }
   //      checkOseeEnumTypeDeleted(oseeEnumType);
   //   }
   //
   //   @org.junit.Test
   //   public void testRemoveEntriesToType() throws OseeCoreException {
   //      String enumTypeName = "EnumType4";
   //      String xmlDefinition = "<Root><Enum>one</Enum><Enum>two</Enum><Enum>three</Enum></Root>";
   //      String[] entryNames = new String[] {"one", "two", "three"};
   //      Integer[] entryOrdinals = new Integer[] {0, 1, 2};
   //
   //      OseeEnumType oseeEnumType = OseeEnumTypeManager.createEnumTypeFromXml(enumTypeName, xmlDefinition);
   //      try {
   //         TestOseeTypesUtil.checkEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);
   //
   //         List<String> names = new ArrayList<String>(Arrays.asList(entryNames));
   //         List<Integer> ordinals = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
   //         for (OseeEnumEntry entry : oseeEnumType.values()) {
   //            oseeEnumType.removeEntry(entry);
   //            names.remove(0);
   //            ordinals.remove(0);
   //            TestOseeTypesUtil.checkEnumType(enumTypeName, names.toArray(new String[names.size()]),
   //                  ordinals.toArray(new Integer[ordinals.size()]), oseeEnumType);
   //            OseeEnumType actual = OseeEnumTypeManager.getUniqueType(enumTypeName);
   //            assertEquals(oseeEnumType, actual);
   //         }
   //
   //         assertEquals(0, oseeEnumType.values().length);
   //      } finally {
   //         OseeEnumTypeManager.deleteEnumType(oseeEnumType);
   //      }
   //      checkOseeEnumTypeDeleted(oseeEnumType);
   //   }
   //
   //   @org.junit.Test
   //   public void testDeletedRetrieval() throws OseeCoreException {
   //      String enumTypeName = "EnumType5";
   //      String xmlDefinition = "<Root><Enum>one</Enum><Enum>two</Enum><Enum>three</Enum></Root>";
   //      String[] entryNames = new String[] {"one", "two", "three"};
   //      Integer[] entryOrdinals = new Integer[] {0, 1, 2};
   //
   //      OseeEnumType oseeEnumType = OseeEnumTypeManager.createEnumTypeFromXml(enumTypeName, xmlDefinition);
   //      try {
   //         TestOseeTypesUtil.checkEnumType(enumTypeName, entryNames, entryOrdinals, oseeEnumType);
   //      } finally {
   //         OseeEnumTypeManager.deleteEnumType(oseeEnumType);
   //      }
   //      checkOseeEnumTypeDeleted(oseeEnumType);
   //
   //      assertTrue(OseeEnumTypeManager.getAllTypeNames(true).contains(enumTypeName));
   //      assertTrue(OseeEnumTypeManager.getAllTypes(true).contains(oseeEnumType));
   //
   //      OseeEnumType actual = OseeEnumTypeManager.getType(oseeEnumType.getTypeId(), true);
   //      assertEquals(oseeEnumType, actual);
   //
   //      actual = OseeEnumTypeManager.getUniqueType(enumTypeName, true);
   //      assertEquals(oseeEnumType, actual);
   //   }
   //
   //   @org.junit.Test
   //   public void testDeletedNotAllowedWhileInUseByAttribute() throws OseeCoreException {
   //      Collection<OseeEnumType> types = OseeEnumTypeManager.getAllTypes();
   //      boolean wasTestedAtLeastOnce = false;
   //      for (OseeEnumType type : types) {
   //         for (AttributeType attrType : AttributeTypeManager.getAllTypes()) {
   //            if (attrType.getOseeEnumTypeId() == type.getTypeId()) {
   //               // Found an enum that is in use;
   //               wasTestedAtLeastOnce = true;
   //               try {
   //                  OseeEnumTypeManager.deleteEnumType(type);
   //               } catch (Exception ex) {
   //                  assertTrue(ex instanceof OseeStateException);
   //               }
   //               assertTrue(!type.isDeleted());
   //            }
   //         }
   //      }
   //      assertTrue(wasTestedAtLeastOnce);
   //   }
   //
   //   private void checkOseeEnumTypeDeleted(OseeEnumType actual) throws OseeCoreException {
   //      assertTrue(actual.isDeleted());
   //      try {
   //         OseeEnumTypeManager.getType(actual.getTypeId());
   //      } catch (Exception ex) {
   //         assertTrue(ex instanceof OseeTypeDoesNotExist);
   //      }
   //      try {
   //         OseeEnumTypeManager.getUniqueType(actual.getName());
   //      } catch (Exception ex) {
   //         assertTrue(ex instanceof OseeTypeDoesNotExist);
   //      }
   //
   //      assertTrue(!OseeEnumTypeManager.getAllTypes().contains(actual));
   //      assertTrue(!OseeEnumTypeManager.getAllTypeNames().contains(actual.getName()));
   //   }

   //   private static class TestDataAccessor extends TestOseeTypeDataAccessor {
   //      private final List<OseeEnumType> oseeEnumTypes;
   //
   //      public TestDataAccessor(List<OseeEnumType> oseeEnumTypes) {
   //         this.oseeEnumTypes = oseeEnumTypes;
   //      }
   //
   //      private OseeEnumType createEnumTypeHelper(OseeTypeCache cache, IOseeTypeFactory factory, String guid, String name, Object... entries) throws OseeCoreException {
   //         OseeEnumType type = factory.createEnumType(guid, name, cache);
   //         if (entries != null && entries.length > 0) {
   //            List<OseeEnumEntry> items = new ArrayList<OseeEnumEntry>();
   //            for (int index = 0; index < entries.length; index++) {
   //               String itemName = (String) entries[index];
   //               Integer ordinal = (Integer) entries[++index];
   //               items.add(factory.createEnumEntry(null, itemName, ordinal, cache));
   //            }
   //            type.setEntries(items);
   //         }
   //         return type;
   //      }
   //
   //      @Override
   //      public void loadAllOseeEnumTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
   //         super.loadAllOseeEnumTypes(cache, factory);
   //         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E2", "Enum2", "AAA", 1, "BBB", 2, "CCC", 3));
   //         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E3", "Enum3", "DDD", 4, "EEE", 5, "FFF", 6));
   //         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E4", "Enum4", "GGG", 7, "HHH", 8, "III", 9));
   //         oseeEnumTypes.add(createEnumTypeHelper(cache, factory, "E5", "Enum5", "JJJ", 10, "KKK", 11, "LLL", 12));
   //         int typeId = 400;
   //         for (OseeEnumType type : oseeEnumTypes) {
   //            type.setTypeId(typeId++);
   //            cache.getEnumTypeCache().cacheType(type);
   //         }
   //      }
   //   }
}
