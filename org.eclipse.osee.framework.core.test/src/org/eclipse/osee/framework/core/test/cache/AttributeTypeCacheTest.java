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
package org.eclipse.osee.framework.core.test.cache;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.test.mocks.MockOseeDataAccessor;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test Case for {@link AttributeTypeCache}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeTypeCacheTest extends AbstractOseeCacheTest<AttributeType> {

   private static List<AttributeType> attributeTypes;
   private static AttributeTypeCache attrCache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      attributeTypes = new ArrayList<AttributeType>();

      AttributeDataAccessor attrData = new AttributeDataAccessor(attributeTypes);
      attrCache = new AttributeTypeCache(attrData);

      attrCache.ensurePopulated();
      Assert.assertTrue(attrData.wasLoaded());
   }

   public AttributeTypeCacheTest() {
      super(attributeTypes, attrCache);
   }

   private final static class AttributeDataAccessor extends MockOseeDataAccessor<AttributeType> {

      private final List<AttributeType> attributeTypes;

      public AttributeDataAccessor(List<AttributeType> attributeTypes) {
         super();
         this.attributeTypes = attributeTypes;
      }

      @Override
      public void load(IOseeCache<AttributeType> cache) throws OseeCoreException {
         super.load(cache);
         int typeId = 100;
         for (int index = 0; index < 10; index++) {
            AttributeType item = MockDataFactory.createAttributeType(index, null);
            attributeTypes.add(item);
            item.setId(typeId++);
            cache.cache(item);
         }
      }
   }
}
