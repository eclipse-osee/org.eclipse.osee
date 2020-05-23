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

import java.util.Map;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public interface CacheDataLoader<K, V> {

   Map<K, V> load(Iterable<? extends K> keys) throws Exception;

   V load(K key) throws Exception;

   V reload(K key, V oldValue) throws Exception;
}