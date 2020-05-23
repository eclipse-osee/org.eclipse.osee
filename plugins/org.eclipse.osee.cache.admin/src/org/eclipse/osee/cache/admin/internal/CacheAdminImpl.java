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

import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheAdmin;
import org.eclipse.osee.cache.admin.CacheConfiguration;
import org.eclipse.osee.cache.admin.CacheDataLoader;
import org.eclipse.osee.cache.admin.CacheKeysLoader;

/**
 * Creating and keeping track of application caches.
 * 
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class CacheAdminImpl implements CacheAdmin {

   private final CacheFactory cacheFactory = new CacheFactory();

   public void start() {
      //
   }

   public void stop() {
      //
   }

   @Override
   public <K, V> Cache<K, V> createCache(CacheConfiguration configuration) throws Exception {
      return cacheFactory.createCache(configuration);
   }

   @Override
   public <K, V> Cache<K, V> createLoadingCache(CacheConfiguration configuration, CacheDataLoader<K, V> accessor, CacheKeysLoader<K> keyLoader) throws Exception {
      return cacheFactory.createLoadingCache(configuration, accessor, keyLoader);
   }

}