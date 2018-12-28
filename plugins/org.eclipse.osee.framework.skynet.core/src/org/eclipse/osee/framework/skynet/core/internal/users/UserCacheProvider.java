/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.users;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheAdmin;
import org.eclipse.osee.cache.admin.CacheConfiguration;
import org.eclipse.osee.cache.admin.CacheDataLoader;
import org.eclipse.osee.cache.admin.CacheKeysLoader;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Roberto E. Escobar
 */
public class UserCacheProvider extends LazyObject<Cache<String, User>> {

   private final CacheAdmin cacheAdmin;
   private final CacheDataLoader<String, User> loader;
   private final CacheKeysLoader<String> keyProvider;

   public UserCacheProvider(CacheAdmin cacheAdmin, CacheDataLoader<String, User> loader, CacheKeysLoader<String> keyProvider) {
      this.cacheAdmin = cacheAdmin;
      this.loader = loader;
      this.keyProvider = keyProvider;
   }

   @Override
   protected FutureTask<Cache<String, User>> createLoaderTask() {
      Callable<Cache<String, User>> callable = new Callable<Cache<String, User>>() {

         @Override
         public Cache<String, User> call() throws Exception {
            CacheConfiguration configuration = CacheConfiguration.newConfiguration();
            Cache<String, User> userCache = cacheAdmin.createLoadingCache(configuration, loader, keyProvider);

            // Prime the cache
            userCache.getAll();

            return userCache;
         }
      };
      return new FutureTask<>(callable);
   }

}