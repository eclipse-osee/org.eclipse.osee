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

package org.eclipse.osee.cache.admin;

/**
 * Service in-charge of creating application caches.
 * 
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public interface CacheAdmin {

   <K, V> Cache<K, V> createCache(CacheConfiguration configuration) throws Exception;

   <K, V> Cache<K, V> createLoadingCache(CacheConfiguration configuration, CacheDataLoader<K, V> dataLoader, CacheKeysLoader<K> keyLoader) throws Exception;

}