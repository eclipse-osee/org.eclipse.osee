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

import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheKeysLoader;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class LoadingCacheProxy<K, V> implements Cache<K, V> {

   private final LoadingCache<K, V> proxied;
   private final CacheKeysLoader<K> keyProvider;

   public LoadingCacheProxy(LoadingCache<K, V> proxied, CacheKeysLoader<K> keyProvider) {
      this.proxied = proxied;
      this.keyProvider = keyProvider;
   }

   @Override
   public V getIfPresent(K key) {
      return proxied.getIfPresent(key);
   }

   @Override
   public Iterable<V> getAllPresent() {
      Iterable<? extends K> keys = getAllKeysPresent();
      return getIfPresent(keys).values();
   }

   @Override
   public Iterable<? extends K> getAllKeysPresent() {
      return Iterables.unmodifiableIterable(proxied.asMap().keySet());
   }

   @Override
   public Iterable<? extends K> getAllKeys() throws Exception {
      Iterable<? extends K> iterator1 = getAllKeysPresent();
      Iterable<? extends K> iterator2 = keyProvider.getAllKeys();
      Iterable<? extends K> joined = Iterables.concat(iterator1, iterator2);
      return Sets.newHashSet(joined);

   }

   @Override
   public Map<K, V> getIfPresent(Iterable<? extends K> keys) {
      return proxied.getAllPresent(keys);
   }

   @Override
   public Map<K, V> get(Iterable<? extends K> keys) throws Exception {
      ImmutableMap<K, V> items = null;
      try {
         items = proxied.getAll(keys);
      } catch (InvalidCacheLoadException ex) {
         throw new Exception(ex);
      } catch (UncheckedExecutionException ex) {
         throw new Exception(ex);
      } catch (ExecutionError ex) {
         throw new Exception(ex);
      }
      return items;
   }

   @Override
   public Iterable<V> getAll() throws Exception {
      Iterable<? extends K> allKeys = getAllKeys();
      return get(allKeys).values();
   }

   @Override
   public V get(K key) throws Exception {
      V toReturn = null;
      try {
         toReturn = proxied.get(key);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return toReturn;
   }

   @Override
   public void refresh(K key) {
      proxied.refresh(key);
   }

   @Override
   public void invalidateAll() {
      proxied.invalidateAll();
   }

   @Override
   public void invalidate(Iterable<? extends K> keys) {
      proxied.invalidateAll(keys);
   }

   @Override
   public void invalidate(K key) {
      proxied.invalidate(key);
   }

   @Override
   public long size() {
      return proxied.size();
   }

   @Override
   public V get(K key, Callable<? extends V> callable) throws Exception {
      V toReturn = null;
      try {
         toReturn = proxied.get(key, callable);
      } catch (InvalidCacheLoadException ex) {
         throw new Exception(ex);
      } catch (UncheckedExecutionException ex) {
         throw new Exception(ex);
      } catch (ExecutionError ex) {
         throw new Exception(ex);
      }
      return toReturn;
   }

   @Override
   public boolean isEmpty() {
      return proxied.size() == 0;
   }

}
