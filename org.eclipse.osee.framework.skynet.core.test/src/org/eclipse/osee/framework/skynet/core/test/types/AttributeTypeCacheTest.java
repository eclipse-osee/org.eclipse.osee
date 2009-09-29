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
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.AttributeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.junit.BeforeClass;

/**
 * Low-level OseeTypeCache Test - Does not require database access
 * 
 * @author Roberto E. Escobar
 */
public class AttributeTypeCacheTest extends AbstractOseeCacheTest<AttributeType> {

   private static List<AttributeType> attributeTypes;
   private static AttributeTypeCache attrCache;
   private static IOseeTypeFactory factory;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      attributeTypes = new ArrayList<AttributeType>();

      factory = new OseeTypeFactory();

      AttributeDataAccessor attrData = new AttributeDataAccessor(attributeTypes);

      attrCache = new AttributeTypeCache(factory, attrData);
      attrCache.ensurePopulated();
      Assert.assertTrue(attrData.wasLoaded());
   }

   public AttributeTypeCacheTest() {
      super(attributeTypes, attrCache);
   }

   @Override
   public void testDirty() throws OseeCoreException {
      AttributeType attributeType = OseeTypesUtil.createAttributeType(attrCache, factory, "GUID", "AttributeDirtyTest");
      Assert.assertTrue(attributeType.isDirty());
      attributeType.clearDirty();

      String initialValue = attributeType.getName();
      attributeType.setName("My Name Has Changes");
      Assert.assertTrue(attributeType.isDirty());

      // Remains Dirty
      attributeType.setName(initialValue);
      Assert.assertTrue(attributeType.isDirty());

      //      attributeType.setFields(name, baseAttributeTypeId, attributeProviderNameId, baseAttributeClass,
      //            providerAttributeClass, fileTypeExtension, defaultValue, oseeEnumType, minOccurrences, maxOccurrences,
      //            description, taggerId);

   }

   //   private void checkDirty(AttributeType attributeType) {
   //      //      DirtyStateDetail details = attributeType.getDirtyDetails();
   //      //      details.
   //   }

   private final static class AttributeDataAccessor extends OseeTestDataAccessor<AttributeType> {

      private final List<AttributeType> attributeTypes;

      public AttributeDataAccessor(List<AttributeType> attributeTypes) {
         super();
         this.attributeTypes = attributeTypes;
      }

      @Override
      public void load(AbstractOseeCache<AttributeType> cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
         attributeTypes.add(OseeTypesUtil.createAttributeType(cache, factory, "AAA", "Attribute1"));
         attributeTypes.add(OseeTypesUtil.createAttributeType(cache, factory, "BBB", "Attribute2"));
         attributeTypes.add(OseeTypesUtil.createAttributeType(cache, factory, "CCC", "Attribute3"));
         attributeTypes.add(OseeTypesUtil.createAttributeType(cache, factory, "DDD", "Attribute4"));
         attributeTypes.add(OseeTypesUtil.createAttributeType(cache, factory, "EEE", "Attribute5"));
         attributeTypes.add(OseeTypesUtil.createAttributeType(cache, factory, "FFF", "Attribute6"));
         attributeTypes.add(OseeTypesUtil.createAttributeType(cache, factory, "GGG", "Attribute7"));
         attributeTypes.add(OseeTypesUtil.createAttributeType(cache, factory, "HHH", "Attribute8"));
         int typeId = 200;
         for (AttributeType type : attributeTypes) {
            type.setId(typeId++);
            cache.cache(type);
         }
      }
   }
}
