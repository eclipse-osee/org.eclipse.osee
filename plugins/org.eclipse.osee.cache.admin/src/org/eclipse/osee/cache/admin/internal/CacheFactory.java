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

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheConfiguration;
import org.eclipse.osee.cache.admin.CacheDataLoader;
import org.eclipse.osee.cache.admin.CacheKeysLoader;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class CacheFactory {

   public <K, V> Cache<K, V> createCache(final CacheConfiguration config) throws Exception {
      Preconditions.checkNotNull(config, "cacheConfiguration");
      com.google.common.cache.Cache<K, V> cache = createCacheBuilder(config).build();
      Cache<K, V> toReturn = new CacheProxy<>(cache);
      return toReturn;
   }

   public <K, V> Cache<K, V> createLoadingCache(final CacheConfiguration config, final CacheDataLoader<K, V> dataLoader, final CacheKeysLoader<K> keyLoader) throws Exception {
      Preconditions.checkNotNull(config, "cacheConfiguration");
      Preconditions.checkNotNull(dataLoader, "cacheDataLoader");
      Preconditions.checkNotNull(keyLoader, "cacheKeysLoader");

      final LoadingCache<K, V> loadingCache = createCacheBuilder(config).build(new CacheLoader<K, V>() {

         @Override
         public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
            return dataLoader.load(keys);
         }

         @Override
         public V load(K key) throws Exception {
            return dataLoader.load(key);
         }

         @Override
         public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
            V newValue = dataLoader.reload(key, oldValue);
            return Futures.immediateFuture(newValue);
         }
      });
      Cache<K, V> toReturn = new LoadingCacheProxy<>(loadingCache, keyLoader);
      return toReturn;
   }

   private CacheBuilder<Object, Object> createCacheBuilder(CacheConfiguration config) {
      CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
      if (config.hasInitialCapacity()) {
         builder = builder.initialCapacity(config.getInitialCapacity());
      }
      if (config.hasMaximumSize()) {
         builder = builder.maximumSize(config.getMaximumSize());
      }
      if (config.isExpireAfterAccess()) {
         Pair<Long, TimeUnit> data = config.getExpireAfterAccess();
         builder = builder.expireAfterAccess(data.getFirst(), data.getSecond());
      }
      if (config.isExpireAfterWrite()) {
         Pair<Long, TimeUnit> data = config.getExpireAfterWrite();
         builder = builder.expireAfterWrite(data.getFirst(), data.getSecond());
      }
      if (config.isRefreshAfterWrite()) {
         Pair<Long, TimeUnit> data = config.getRefreshAfterWrite();
         builder = builder.refreshAfterWrite(data.getFirst(), data.getSecond());
      }
      return builder;
   }
}
