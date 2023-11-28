/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Collection;
import java.util.Map;

/**
 * An extension of the {@link AbstractMapCollection} with all the methods that can modify the map collection overridden
 * to throw an {@link UnsupportedOperationException}.
 *
 * @author Loren K Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the collections associated with the map keys.
 * @param <C> the type of collection associated with the map keys.
 */

public class AbstractImmutableMapCollection<K, V, C extends Collection<V>> extends AbstractMapCollection<K, V, C> {

   /**
    * Creates a new {@link AbstractImmutableMapCollection} as a wrapper on the immutable <code>mapCollection</code>.
    *
    * @param mapCollection an immutable {@link Map} of immutable {@link Collection}s.
    */

   AbstractImmutableMapCollection(Map<K, C> mapCollection) {
      super(mapCollection);
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public C put(K key, C value) {
      throw new UnsupportedOperationException();
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public C putAll(K key, C values) {
      throw new UnsupportedOperationException();
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public void putAll(Map<? extends K, ? extends C> m) {
      throw new UnsupportedOperationException();
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public C putEntry(Entry<K, V> entry) {
      throw new UnsupportedOperationException();
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public C putValue(K key, V value) {
      throw new UnsupportedOperationException();
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public C remove(Object key) {
      throw new UnsupportedOperationException();
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public boolean removeEntry(Map.Entry<K, V> entry) {
      throw new UnsupportedOperationException();
   }

   /**
    * Throws {@link UnsupportedOperationException}.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public boolean removeValue(K key, V value) {
      throw new UnsupportedOperationException();
   }

}

/* EOF */