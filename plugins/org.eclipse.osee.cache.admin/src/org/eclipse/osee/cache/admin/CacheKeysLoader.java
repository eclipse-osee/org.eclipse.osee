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
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public interface CacheKeysLoader<K> {

   Iterable<? extends K> getAllKeys() throws Exception;

}
