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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheConfiguration;
import org.eclipse.osee.cache.admin.CacheDataLoader;
import org.eclipse.osee.cache.admin.CacheKeysLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link CacheFactory, CacheProxy}
 * 
 * @author Roberto E. Escobar
 */
public class LoadingCacheTest {

   private static final String KEY_1 = "key1";
   private static final String KEY_2 = "key2";
   private static final String KEY_3 = "key3";

   private static final Object OBJECT_1 = new Object();
   private static final Object OBJECT_2 = new Object();
   private static final Object OBJECT_3 = new Object();
   private static final Object OBJECT_4 = new Object();

   //@formatter:off
   @Mock private CacheDataLoader<String, Object> dataLoader;
   @Mock private CacheKeysLoader<String> keyLoader;
   @Captor private ArgumentCaptor<Iterable<? extends String>> keysCaptor;
   //@formatter:on

   private Cache<String, Object> cache;
   private final CacheFactory factory = new CacheFactory();
   private final CacheConfiguration config = CacheConfiguration.newConfiguration();

   @Before
   public void setup() throws Exception {
      MockitoAnnotations.initMocks(this);

      cache = factory.createLoadingCache(config, dataLoader, keyLoader);
   }

   @Test
   public void testGetIfPresent() throws Exception {
      Object value = cache.getIfPresent(KEY_1);
      assertNull(value);

      verify(dataLoader, never()).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, never()).load(anyString());

      value = cache.get(KEY_1, createCallable(OBJECT_1));
      assertEquals(OBJECT_1, value);

      assertEquals(1, cache.size());

      verify(dataLoader, never()).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, never()).load(anyString());

      value = cache.getIfPresent(KEY_1);
      assertEquals(OBJECT_1, value);
   }

   @Test
   public void testGetAllPresent() throws Exception {
      Iterable<Object> values = cache.getAllPresent();
      assertEquals(false, values.iterator().hasNext());

      verify(dataLoader, never()).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, never()).load(anyString());

      Object value1 = cache.get(KEY_1, createCallable(OBJECT_1));
      Object value2 = cache.get(KEY_2, createCallable(OBJECT_2));
      Object value3 = cache.get(KEY_3, createCallable(OBJECT_3));

      assertEquals(OBJECT_1, value1);
      assertEquals(OBJECT_2, value2);
      assertEquals(OBJECT_3, value3);

      assertEquals(3, cache.size());

      verify(dataLoader, never()).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, never()).load(anyString());

      values = cache.getAllPresent();

      Assert.assertTrue(Iterables.contains(values, OBJECT_1));
      Assert.assertTrue(Iterables.contains(values, OBJECT_2));
      Assert.assertTrue(Iterables.contains(values, OBJECT_3));

      verify(dataLoader, never()).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, never()).load(anyString());
   }

   @Test
   public void testGet() throws Exception {
      assertEquals(true, cache.isEmpty());

      when(dataLoader.load(KEY_1)).thenReturn(OBJECT_1);

      Object value1 = cache.get(KEY_1);

      assertEquals(OBJECT_1, value1);
      assertEquals(1, cache.size());
      assertEquals(false, cache.isEmpty());

      verify(dataLoader, never()).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, times(1)).load(KEY_1);
   }

   @Test
   public void testGetAll() throws Exception {
      Iterable<? extends String> keysInStore = Arrays.asList(KEY_1, KEY_2);
      when(keyLoader.getAllKeys()).thenAnswer(createAnswer(keysInStore));

      Map<String, Object> data = new LinkedHashMap<>();
      data.put(KEY_1, OBJECT_1);
      data.put(KEY_2, OBJECT_2);
      when(dataLoader.load(Matchers.<Iterable<? extends String>> any())).thenAnswer(createAnswer(data));

      assertEquals(true, cache.isEmpty());

      Iterable<Object> value = cache.getAll();

      assertEquals(2, cache.size());
      assertEquals(false, cache.isEmpty());

      assertTrue(Iterables.contains(value, OBJECT_1));
      assertTrue(Iterables.contains(value, OBJECT_2));

      verify(dataLoader, times(1)).load(keysCaptor.capture());
      verify(dataLoader, never()).load(Matchers.anyString());

      Iterable<? extends String> capturedIt = keysCaptor.getValue();
      assertTrue(Iterables.contains(capturedIt, KEY_1));
      assertTrue(Iterables.contains(capturedIt, KEY_2));

      Object value3 = cache.get(KEY_3, createCallable(OBJECT_3));
      assertEquals(OBJECT_3, value3);

      verify(dataLoader, never()).load(Matchers.anyString());

      Iterable<Object> value2 = cache.getAll();

      assertEquals(3, cache.size());

      assertTrue(Iterables.contains(value2, OBJECT_1));
      assertTrue(Iterables.contains(value2, OBJECT_2));
      assertTrue(Iterables.contains(value2, OBJECT_3));

      // Load not called again
      verify(dataLoader, times(1)).load(keysCaptor.capture());
      verify(dataLoader, times(0)).load(anyString());
   }

   @Test
   public void testGetAllKeys() throws Exception {
      Iterable<? extends String> keysInStore = Arrays.asList(KEY_1, KEY_2);
      when(keyLoader.getAllKeys()).thenAnswer(createAnswer(keysInStore));

      Map<String, Object> data = new LinkedHashMap<>();
      data.put(KEY_1, OBJECT_1);
      data.put(KEY_2, OBJECT_2);
      when(dataLoader.load(Matchers.<Iterable<? extends String>> any())).thenAnswer(createAnswer(data));

      Iterable<? extends String> value = cache.getAllKeys();
      assertTrue(Iterables.contains(value, KEY_1));
      assertTrue(Iterables.contains(value, KEY_2));

      Iterable<? extends String> presentValue = cache.getAllKeysPresent();
      assertFalse(Iterables.contains(presentValue, KEY_1));
      assertFalse(Iterables.contains(presentValue, KEY_2));

      assertEquals(true, cache.isEmpty());

      Object value3 = cache.get(KEY_3, createCallable(OBJECT_3));
      assertEquals(OBJECT_3, value3);
      assertEquals(false, cache.isEmpty());

      Iterable<? extends String> value1 = cache.getAllKeys();
      assertTrue(Iterables.contains(value1, KEY_1));
      assertTrue(Iterables.contains(value1, KEY_2));
      assertTrue(Iterables.contains(value1, KEY_3));

      Iterable<? extends String> presentValue2 = cache.getAllKeysPresent();
      assertFalse(Iterables.contains(presentValue2, KEY_1));
      assertFalse(Iterables.contains(presentValue2, KEY_2));
      assertTrue(Iterables.contains(presentValue2, KEY_3));

      Iterable<Object> allPresent = cache.getAllPresent();
      assertFalse(Iterables.contains(allPresent, OBJECT_1));
      assertFalse(Iterables.contains(allPresent, OBJECT_2));
      assertTrue(Iterables.contains(allPresent, OBJECT_3));

      Iterable<? extends String> presentValue3 = cache.getAllKeysPresent();
      assertFalse(Iterables.contains(presentValue3, KEY_1));
      assertFalse(Iterables.contains(presentValue3, KEY_2));
      assertTrue(Iterables.contains(presentValue3, KEY_3));

      Iterable<Object> all = cache.getAll();
      assertTrue(Iterables.contains(all, OBJECT_1));
      assertTrue(Iterables.contains(all, OBJECT_2));
      assertTrue(Iterables.contains(all, OBJECT_3));

      Iterable<? extends String> presentValue4 = cache.getAllKeysPresent();
      assertTrue(Iterables.contains(presentValue4, KEY_1));
      assertTrue(Iterables.contains(presentValue4, KEY_2));
      assertTrue(Iterables.contains(presentValue4, KEY_3));
   }

   @Test
   public void testInvalidateAll() throws Exception {
      Iterable<? extends String> keysInStore = Arrays.asList(KEY_1, KEY_2, KEY_3);
      when(keyLoader.getAllKeys()).thenAnswer(createAnswer(keysInStore));

      Map<String, Object> data = new LinkedHashMap<>();
      data.put(KEY_1, OBJECT_1);
      data.put(KEY_2, OBJECT_2);
      data.put(KEY_3, OBJECT_3);
      when(dataLoader.load(Matchers.<Iterable<? extends String>> any())).thenAnswer(createAnswer(data));

      assertEquals(0, cache.size());

      cache.getAll();

      assertEquals(3, cache.size());

      cache.invalidateAll();

      assertEquals(0, cache.size());
   }

   @Test
   public void testInvalidateKeys() throws Exception {
      Iterable<? extends String> keysInStore = Arrays.asList(KEY_1, KEY_2, KEY_3);
      when(keyLoader.getAllKeys()).thenAnswer(createAnswer(keysInStore));

      Map<String, Object> data = new LinkedHashMap<>();
      data.put(KEY_1, OBJECT_1);
      data.put(KEY_2, OBJECT_2);
      data.put(KEY_3, OBJECT_3);
      when(dataLoader.load(Matchers.<Iterable<? extends String>> any())).thenAnswer(createAnswer(data));

      assertEquals(0, cache.size());

      cache.getAll();

      assertEquals(3, cache.size());

      cache.invalidate(Arrays.asList(KEY_1, KEY_3));
      assertEquals(1, cache.size());

      assertNull(cache.getIfPresent(KEY_1));
      assertEquals(OBJECT_2, cache.getIfPresent(KEY_2));
      assertNull(cache.getIfPresent(KEY_3));
   }

   @Test
   public void testInvalidate() throws Exception {
      Iterable<? extends String> keysInStore = Arrays.asList(KEY_1, KEY_2, KEY_3);
      when(keyLoader.getAllKeys()).thenAnswer(createAnswer(keysInStore));

      Map<String, Object> data = new LinkedHashMap<>();
      data.put(KEY_1, OBJECT_1);
      data.put(KEY_2, OBJECT_2);
      data.put(KEY_3, OBJECT_3);
      when(dataLoader.load(Matchers.<Iterable<? extends String>> any())).thenAnswer(createAnswer(data));

      assertEquals(0, cache.size());

      cache.getAll();

      assertEquals(3, cache.size());

      cache.invalidate(KEY_2);
      assertEquals(2, cache.size());

      assertEquals(OBJECT_1, cache.getIfPresent(KEY_1));
      assertNull(cache.getIfPresent(KEY_2));
      assertEquals(OBJECT_3, cache.getIfPresent(KEY_3));
   }

   @Test
   public void testRefresh() throws Exception {
      Iterable<? extends String> keysInStore = Arrays.asList(KEY_1, KEY_2, KEY_3);
      when(keyLoader.getAllKeys()).thenAnswer(createAnswer(keysInStore));

      Map<String, Object> data = new LinkedHashMap<>();
      data.put(KEY_1, OBJECT_1);
      data.put(KEY_2, OBJECT_2);
      data.put(KEY_3, OBJECT_3);
      when(dataLoader.load(Matchers.<Iterable<? extends String>> any())).thenAnswer(createAnswer(data));

      when(dataLoader.reload(KEY_2, OBJECT_2)).thenReturn(OBJECT_4);

      assertEquals(0, cache.size());

      cache.getAll();

      assertEquals(3, cache.size());

      verify(dataLoader, times(1)).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, never()).load(anyString());

      cache.refresh(KEY_2);

      verify(dataLoader, times(1)).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, times(0)).load(anyString());
      verify(dataLoader, times(1)).reload(KEY_2, OBJECT_2);

      Assert.assertEquals(OBJECT_4, cache.get(KEY_2));

      verify(dataLoader, times(1)).load(Matchers.<Iterable<? extends String>> any());
      verify(dataLoader, times(0)).load(anyString());
      verify(dataLoader, times(1)).reload(KEY_2, OBJECT_2);
   }

   private static <K> Answer<K> createAnswer(final K object) {
      return new Answer<K>() {

         @Override
         public K answer(InvocationOnMock invocation) throws Throwable {
            return object;
         }

      };
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
