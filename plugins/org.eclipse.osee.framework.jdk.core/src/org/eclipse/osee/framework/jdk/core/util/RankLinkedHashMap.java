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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A {@link RankMap} implementation using {@link LinkedHashMap} maps for the primary and secondary maps.
 *
 * @author Loren K. Ashley
 * @param <V> the type of value stored in the map.
 */

public class RankLinkedHashMap<V> extends AbstractRankMap<V> {

   /**
    * Creates a new empty {@link RankLinkedHashMap}.
    *
    * @param identifier an identification string for the map. This parameter may be <code>null</code>/
    * @param rank the number of map levels implemented by the map.
    * @param mapSupplier a {@link Supplier} of {@link Map} implementations used to obtain the primary and sub-maps.
    * @param keyValidators an array of {@link Predicate} implementations used to validate keys when assertions are
    * enabled. This parameter may be <code>null</code>.
    * @throws IllegalArgumentException when the specified <code>rank</code> is less than one.
    * @throws NullPointerException when the <code>mapSupplier</code> is <code>null</code>.
    */

   public RankLinkedHashMap(String identifier, int rank, int initialCapacity, float loadFactor, Predicate<Object>[] keyValidators) {
      //@formatter:off
      super
         (
            identifier,
            rank,
            new Supplier<Map<Object, Object>>()
            {

               @Override
               public Map<Object, Object> get() {
                  return new LinkedHashMap<Object,Object>( initialCapacity, loadFactor );
               }

            },
            keyValidators
         );
      //@formatter:on
   }

}

/* EOF */
