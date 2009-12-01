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
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.test.mocks.MockOseeDataAccessor;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test Case for {@link RelationTypeCache}
 * 
 * @author Roberto E. Escobar
 */
public class RelationTypeCacheTest extends AbstractOseeCacheTest<RelationType> {

   private static List<RelationType> data;
   private static RelationTypeCache cache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      data = new ArrayList<RelationType>();

      RelationDataAccessor relationAccessor = new RelationDataAccessor(data);
      cache = new RelationTypeCache(relationAccessor);

      cache.ensurePopulated();
      Assert.assertTrue(relationAccessor.wasLoaded());
   }

   public RelationTypeCacheTest() {
      super(data, cache);
   }

   private final static class RelationDataAccessor extends MockOseeDataAccessor<RelationType> {
      private final List<RelationType> relationTypes;

      public RelationDataAccessor(List<RelationType> relationTypes) {
         super();
         this.relationTypes = relationTypes;
      }

      @Override
      public void load(IOseeCache<RelationType> cache) throws OseeCoreException {
         super.load(cache);
         int typeId = 100;
         for (int index = 0; index < 10; index++) {
            RelationType item = MockDataFactory.createRelationType(index, null, null);
            relationTypes.add(item);
            item.setId(typeId++);
            cache.cache(item);
         }
      }
   }

}
