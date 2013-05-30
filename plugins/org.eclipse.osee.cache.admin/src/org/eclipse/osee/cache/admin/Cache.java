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

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public interface Cache<K, V> {

   V getIfPresent(K key);

   Map<K, V> getIfPresent(Iterable<? extends K> keys);

   V get(K key, Callable<? extends V> loader) throws Exception;

   V get(K key) throws Exception;

   Map<K, V> get(Iterable<? extends K> keys) throws Exception;

   Iterable<V> getAllPresent();

   Iterable<V> getAll() throws Exception;

   Iterable<? extends K> getAllKeysPresent();

   Iterable<? extends K> getAllKeys() throws Exception;

   void refresh(K key);

   void invalidateAll();

   void invalidate(Iterable<? extends K> keys);

   void invalidate(K key);

   long size();

   boolean isEmpty();

}