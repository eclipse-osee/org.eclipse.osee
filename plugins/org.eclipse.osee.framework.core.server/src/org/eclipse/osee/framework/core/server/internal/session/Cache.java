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

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public interface Cache<K, V> {

   V get(K key) throws OseeCoreException;

   V get(K key, Callable<? extends V> callable) throws OseeCoreException;

   Iterable<V> getAll() throws OseeCoreException;

   void refresh(K key);

   void invalidateAll();

   void invalidateAll(Iterable<? extends K> keys);

   void invalidate(K key);

   long size();

}