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
package org.eclipse.osee.framework.core.server.internal.session;

import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class CacheFactory {

   public <K, V> Cache<K, V> create(final ReadDataAccessor<K, V> accessor) {
      final LoadingCache<K, V> loadingCache = CacheBuilder.newBuilder().build(new CacheLoader<K, V>() {

         @Override
         public Map<K, V> loadAll(Iterable<? extends K> keys) throws OseeCoreException {
            return accessor.load(keys);
         }

         @Override
         public V load(K key) throws OseeCoreException {
            return accessor.load(key);
         }
      });
      Cache<K, V> toReturn = new CacheProxy<K, V>(loadingCache, accessor);
      return toReturn;
   }

}
