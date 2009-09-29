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

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeEnumTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.junit.BeforeClass;

/**
 * Low-level OseeTypeCache Test - Does not require database access
 * 
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeCacheTest extends AbstractOseeCacheTest<OseeEnumType> {

   private static List<OseeEnumType> data;
   private static IOseeTypeFactory factory;
   private static OseeEnumTypeCache cache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      factory = new OseeTypeFactory();
      data = new ArrayList<OseeEnumType>();

      EnumDataAccessor enumAccessor = new EnumDataAccessor(data);

      cache = new OseeEnumTypeCache(factory, enumAccessor);

      cache.ensurePopulated();
      Assert.assertTrue(enumAccessor.wasLoaded());
   }

   public OseeEnumTypeCacheTest() {
      super(data, cache);
   }

   @Override
   protected void checkEquals(OseeEnumType expected, OseeEnumType actual) throws OseeCoreException {
      OseeTypesUtil.checkEnumType(expected, actual);
   }

   @Override
   public void testDirty() throws OseeCoreException {
   }

   @org.junit.Test
   public void testAddOseeEnumEntry() throws OseeCoreException {
      OseeEnumType enum1 =
            OseeTypesUtil.createEnumType(cache, factory, "Test 1", "Test 1", "OneEntry", 0, "TwoEntry", 1);
      OseeTypesUtil.checkOseeEnumEntries(enum1.values(), "OneEntry", 0, "TwoEntry", 1);

      OseeEnumEntry entry = factory.createEnumEntry(cache, "C", "AddedEntry", 4);
      enum1.addEntry(entry);

      OseeTypesUtil.checkOseeEnumEntries(enum1.values(), "OneEntry", 0, "TwoEntry", 1, "AddedEntry", 4);

      enum1.removeEntry(entry);
      OseeTypesUtil.checkOseeEnumEntries(enum1.values(), "OneEntry", 0, "TwoEntry", 1);

      enum1.removeEntry(enum1.values()[0]);
      OseeTypesUtil.checkOseeEnumEntries(enum1.values(), "TwoEntry", 1);
   }

   private final static class EnumDataAccessor extends OseeTestDataAccessor<OseeEnumType> {
      private final List<OseeEnumType> oseeEnumTypes;

      public EnumDataAccessor(List<OseeEnumType> oseeEnumTypes) {
         super();
         this.oseeEnumTypes = oseeEnumTypes;
      }

      @Override
      public void load(AbstractOseeCache<OseeEnumType> cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E1", "Enum1", "AAA", 1, "BBB", 2, "CCC", 3));
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E2", "Enum2", "DDD", 4, "EEE", 5, "FFF", 6));
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E3", "Enum3", "GGG", 7, "HHH", 8, "III", 9));
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E4", "Enum4", "JJJ", 10, "KKK", 11, "LLL", 12));
         oseeEnumTypes.add(OseeTypesUtil.createEnumType(cache, factory, "E5", "Enum5", "MMM", 1, "NNN", 2, "OOO", 3));
         int typeId = 400;
         for (OseeEnumType type : oseeEnumTypes) {
            type.setId(typeId++);
            cache.cache(type);
         }
      }
   }
}
