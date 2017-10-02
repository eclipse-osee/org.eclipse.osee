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
package org.eclipse.osee.framework.core.model.cache;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.model.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.junit.BeforeClass;

/**
 * Test Case for {@link RelationTypeCache}
 *
 * @author Roberto E. Escobar
 */
public class RelationTypeCacheTest extends AbstractOseeTypeCacheTest<RelationType> {

   private static List<RelationType> data;
   private static RelationTypeCache cache;

   @BeforeClass
   public static void prepareTestData() {
      data = new ArrayList<>();

      cache = new RelationTypeCache();
      long typeId = 100;
      for (int index = 0; index < 10; index++) {
         RelationType item = MockDataFactory.createRelationType(index, null, null, typeId++);
         data.add(item);
         cache.cache(item);
      }
   }

   public RelationTypeCacheTest() {
      super(data, cache);
   }

}
