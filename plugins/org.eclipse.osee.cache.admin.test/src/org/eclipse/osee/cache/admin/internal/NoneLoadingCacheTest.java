/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.cache.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link CacheFactory, CacheProxy}
 * 
 * @author Roberto E. Escobar
 */
public class NoneLoadingCacheTest {

   private static final String KEY_1 = "key1";
   private static final String KEY_2 = "key2";
   private static final String KEY_3 = "key3";

   private static final Object OBJECT_1 = new Object();
   private static final Object OBJECT_2 = new Object();
   private static final Object OBJECT_3 = new Object();

   private Cache<String, Object> cache;
   private final CacheFactory factory = new CacheFactory();
   private final CacheConfiguration config = CacheConfiguration.newConfiguration();

   @Before
   public void setup() throws Exception {
      MockitoAnnotations.initMocks(this);

      cache = factory.createCache(config);
   }

   @Test
   public void testGetIfPresent() throws Exception {
      Object value = cache.getIfPresent(KEY_1);
      assertNull(value);

      value = cache.get(KEY_1, createCallable(OBJECT_1));
      assertEquals(OBJECT_1, value);

      assertEquals(1, cache.size());

      value = cache.getIfPresent(KEY_1);
      assertEquals(OBJECT_1, value);
   }

   @Test
   public void testGetAllPresent() throws Exception {
      Iterable<Object> values = cache.getAllPresent();
      assertEquals(false, values.iterator().hasNext());

      Object value1 = cache.get(KEY_1, createCallable(OBJECT_1));
      Object value2 = cache.get(KEY_2, createCallable(OBJECT_2));
      Object value3 = cache.get(KEY_3, createCallable(OBJECT_3));

      assertEquals(OBJECT_1, value1);
      assertEquals(OBJECT_2, value2);
      assertEquals(OBJECT_3, value3);

      assertEquals(3, cache.size());

      values = cache.getAllPresent();

      Assert.assertTrue(Iterables.contains(values, OBJECT_1));
      Assert.assertTrue(Iterables.contains(values, OBJECT_2));
      Assert.assertTrue(Iterables.contains(values, OBJECT_3));
   }

   @Test
   public void testGet() throws Exception {
      assertEquals(true, cache.isEmpty());

      Object value = cache.get(KEY_1);
      assertNull(value);

      Object value1 = cache.get(KEY_1, createCallable(OBJECT_1));

      assertEquals(OBJECT_1, value1);
      assertEquals(1, cache.size());
      assertEquals(false, cache.isEmpty());

      Object value2 = cache.get(KEY_1);
      assertEquals(OBJECT_1, value2);
      assertEquals(1, cache.size());
      assertEquals(false, cache.isEmpty());
   }

   @Test
   public void testGetAll() throws Exception {
      assertEquals(true, cache.isEmpty());

      cache.get(KEY_1, createCallable(OBJECT_1));
      cache.get(KEY_2, createCallable(OBJECT_2));
      cache.get(KEY_3, createCallable(OBJECT_3));

      Iterable<Object> value = cache.getAll();

      assertEquals(3, cache.size());
      assertEquals(false, cache.isEmpty());

      assertTrue(Iterables.contains(value, OBJECT_1));
      assertTrue(Iterables.contains(value, OBJECT_2));
      assertTrue(Iterables.contains(value, OBJECT_3));
   }

   @Test
   public void testGetAllKeys() throws Exception {
      assertEquals(true, cache.isEmpty());

      cache.get(KEY_1, createCallable(OBJECT_1));
      cache.get(KEY_2, createCallable(OBJECT_2));
      cache.get(KEY_3, createCallable(OBJECT_3));

      Iterable<? extends String> value = cache.getAllKeys();

      assertEquals(3, cache.size());
      assertEquals(false, cache.isEmpty());

      assertTrue(Iterables.contains(value, KEY_1));
      assertTrue(Iterables.contains(value, KEY_2));
      assertTrue(Iterables.contains(value, KEY_3));
   }

   @Test
   public void testInvalidateAll() throws Exception {
      assertEquals(true, cache.isEmpty());
      assertEquals(0, cache.size());

      cache.get(KEY_1, createCallable(OBJECT_1));
      cache.get(KEY_2, createCallable(OBJECT_2));
      cache.get(KEY_3, createCallable(OBJECT_3));

      assertEquals(3, cache.size());

      cache.invalidateAll();

      assertEquals(0, cache.size());
   }

   @Test
   public void testInvalidateKeys() throws Exception {
      assertEquals(true, cache.isEmpty());
      assertEquals(0, cache.size());

      cache.get(KEY_1, createCallable(OBJECT_1));
      cache.get(KEY_2, createCallable(OBJECT_2));
      cache.get(KEY_3, createCallable(OBJECT_3));

      assertEquals(3, cache.size());

      cache.invalidate(Arrays.asList(KEY_1, KEY_3));
      assertEquals(1, cache.size());

      assertNull(cache.getIfPresent(KEY_1));
      assertEquals(OBJECT_2, cache.getIfPresent(KEY_2));
      assertNull(cache.getIfPresent(KEY_3));
   }

   @Test
   public void testInvalidate() throws Exception {
      assertEquals(true, cache.isEmpty());
      assertEquals(0, cache.size());

      cache.get(KEY_1, createCallable(OBJECT_1));
      cache.get(KEY_2, createCallable(OBJECT_2));
      cache.get(KEY_3, createCallable(OBJECT_3));

      assertEquals(3, cache.size());

      cache.invalidate(KEY_2);
      assertEquals(2, cache.size());

      assertEquals(OBJECT_1, cache.getIfPresent(KEY_1));
      assertNull(cache.getIfPresent(KEY_2));
      assertEquals(OBJECT_3, cache.getIfPresent(KEY_3));
   }

   @Test
   public void testRefresh() throws Exception {
      assertEquals(true, cache.isEmpty());
      assertEquals(0, cache.size());

      @SuppressWarnings("unchecked")
      Callable<Object> mockedCallable = mock(Callable.class);

      when(mockedCallable.call()).thenReturn(OBJECT_2);

      cache.get(KEY_1, createCallable(OBJECT_1));

      cache.get(KEY_2, mockedCallable);
      verify(mockedCallable, times(1)).call();
      assertEquals(2, cache.size());

      cache.get(KEY_3, createCallable(OBJECT_3));
      assertEquals(3, cache.size());

      cache.refresh(KEY_2);

      verify(mockedCallable, times(1)).call();
   }

   private static Callable<Object> createCallable(final Object object) {
      return new Callable<Object>() {

         @Override
         public Object call() throws Exception {
            return object;
         }
      };
   }
}
