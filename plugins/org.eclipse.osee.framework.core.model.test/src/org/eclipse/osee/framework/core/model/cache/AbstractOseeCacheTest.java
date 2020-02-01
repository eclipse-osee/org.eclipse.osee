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
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Test Case for {@link AbstractOseeCache}
 *
 * @author Roberto E. Escobar
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractOseeCacheTest<T extends NamedId> {
   private final List<T> data;
   private final AbstractOseeCache<T> cache;
   private final TypeComparator comparator;

   public AbstractOseeCacheTest(List<T> artifactTypes, AbstractOseeCache<T> typeCache) {
      this.comparator = new TypeComparator();
      this.data = artifactTypes;
      this.cache = typeCache;
   }

   @org.junit.Test
   public void testAllItems() {
      List<T> actualTypes = new ArrayList<>(cache.getAll());
      java.util.Collections.sort(actualTypes, comparator);

      java.util.Collections.sort(data, comparator);
      Assert.assertEquals(data.size(), actualTypes.size());
      for (int index = 0; index < data.size(); index++) {
         Assert.assertNotNull(actualTypes.get(index));
         checkEquals(data.get(index), actualTypes.get(index));
      }
   }

   @org.junit.Test
   public void testCacheByGuid() {
      for (T expected : data) {
         T actual = cache.getByGuid(expected.getId());
         Assert.assertNotNull(actual);
         checkEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheById() {
      for (T expected : data) {
         T actual = cache.getById(expected.getId());
         Assert.assertNotNull(actual);
         checkEquals(expected, actual);
      }
   }

   @org.junit.Test
   public void testCacheByName() {
      for (T expected : data) {
         T actual = cache.getByName(expected.getName());
         Assert.assertNotNull(actual);
         checkEquals(expected, actual);
      }
   }

   @SuppressWarnings("unchecked")
   @org.junit.Test
   public void testDecache() {
      T item1 = data.get(0);
      checkCached(item1, true);

      cache.decache(item1);
      checkCached(item1, false);

      cache.cache(item1);
      checkCached(item1, true);

      T item2 = data.get(1);
      checkCached(item2, true);

      cache.decache(item1, item2);
      checkCached(item1, false);
      checkCached(item2, false);

      cache.cache(item1, item2);
      checkCached(item1, true);
      checkCached(item2, true);
   }

   private void checkCached(T item, boolean isInCacheExpected) {
      if (isInCacheExpected) {
         Assert.assertEquals(item, cache.getByGuid(item.getId()));
         Assert.assertEquals(item, cache.getById(item.getId()));
         Assert.assertEquals(item, cache.getByName(item.getName()));
         Assert.assertTrue(cache.getAll().contains(item));
      } else {
         Assert.assertNull(cache.getByGuid(item.getId()));
         Assert.assertNull(cache.getById(item.getId()));
         Assert.assertFalse(cache.getAll().contains(item));
      }
   }

   @org.junit.Test
   public void testGetByName() {
      for (T expected : data) {
         T actual = cache.getByName(expected.getName());
         checkEquals(expected, actual);
      }
   }

   @Test
   public void testReload() {
      if (cache instanceof IOseeLoadingCache) {
         int fullCacheSize = cache.size();
         Assert.assertTrue(fullCacheSize > 0);
         for (T type : cache.getAll()) {
            cache.decache(type);
         }

         Assert.assertEquals(0, cache.size());
         if (cache instanceof IOseeLoadingCache) {
            Assert.assertTrue(((IOseeLoadingCache<?>) cache).reloadCache());
         }
         Assert.assertEquals(fullCacheSize, cache.size());
      }
   }

   protected abstract Long createKey();

   //OseeCoreException is thrown by inheriting class.
   protected void checkEquals(T expected, T actual) {
      Assert.assertEquals(expected, actual);
   }

   private final class TypeComparator implements Comparator<T> {

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
