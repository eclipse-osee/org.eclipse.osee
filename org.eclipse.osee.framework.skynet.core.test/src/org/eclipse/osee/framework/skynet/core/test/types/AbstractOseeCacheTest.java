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
import java.util.Comparator;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeStorableType;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeCacheTest<T extends IOseeStorableType> {
   private final List<T> data;
   private final AbstractOseeCache<T> cache;
   private final TypeComparator comparator;

   public AbstractOseeCacheTest(List<T> artifactTypes, AbstractOseeCache<T> typeCache) {
      this.comparator = new TypeComparator();
      this.data = artifactTypes;
      this.cache = typeCache;
   }

   @org.junit.Test
   public void testAllItems() throws OseeCoreException {
      List<T> actualTypes = new ArrayList<T>(cache.getAll());
      java.util.Collections.sort(actualTypes, comparator);

      java.util.Collections.sort(data, comparator);
      Assert.assertEquals(data.size(), actualTypes.size());
      for (int index = 0; index < data.size(); index++) {
         Assert.assertNotNull(actualTypes.get(index));
         checkEquals(data.get(index), actualTypes.get(index));
      }
   }

   @org.junit.Test
   public void testExistByGuid() throws OseeCoreException {
      for (T expected : data) {
         Assert.assertTrue(cache.existsByGuid(expected.getGuid()));
      }
      Assert.assertFalse(cache.existsByGuid("notExist"));
   }

   @org.junit.Test
   public void testCacheByGuid() throws OseeCoreException {
      for (T expected : data) {
         T actual = cache.getByGuid(expected.getGuid());
         Assert.assertNotNull(actual);
         checkEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheById() throws OseeCoreException {
      for (T expected : data) {
         T actual = cache.getById(expected.getId());
         Assert.assertNotNull(actual);
         checkEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheByName() throws OseeCoreException {
      for (T expected : data) {
         T actual = cache.getUniqueByName(expected.getName());
         Assert.assertNotNull(actual);
         checkEquals(expected, actual);
      }
   }

   protected void checkEquals(T expected, T actual) throws OseeCoreException {
      Assert.assertEquals(expected, actual);
   }

   @org.junit.Test
   abstract public void testDirty() throws OseeCoreException;

   private final class TypeComparator implements Comparator<T> {

      /*
       * (non-Javadoc)
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(T o1, T o2) {
         int result = -1;
         if (o1 == null && o2 == null) {
            result = 0;
         } else if (o1 != null && o2 != null) {
            result = o1.getName().compareTo(o2.getName());
         } else if (o2 == null) {
            result = 1;
         }
         return result;
      }
   }
}
