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