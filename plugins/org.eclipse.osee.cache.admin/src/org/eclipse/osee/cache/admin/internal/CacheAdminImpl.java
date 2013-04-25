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
package org.eclipse.osee.cache.admin.internal;

import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheAdmin;
import org.eclipse.osee.cache.admin.CacheConfiguration;
import org.eclipse.osee.cache.admin.CacheDataLoader;
import org.eclipse.osee.cache.admin.CacheKeysLoader;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

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
   public <K, V> Cache<K, V> createCache(CacheConfiguration configuration) throws OseeCoreException {
      return cacheFactory.createCache(configuration);
   }

   @Override
   public <K, V> Cache<K, V> createLoadingCache(CacheConfiguration configuration, CacheDataLoader<K, V> accessor, CacheKeysLoader<K> keyLoader) throws OseeCoreException {
      return cacheFactory.createLoadingCache(configuration, accessor, keyLoader);
   }

}