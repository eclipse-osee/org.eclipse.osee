/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.synchronization.util;

import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

/**
 * Implementation of the {@link EnumFunctionMap} interface for {@link BiConsumer} functional interfaces.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <T> the type of the first argument to the {@link BiConsumer} functional interface.
 * @param <U> the type of the second argument to the {@link BiConsumer} functional interface.
 */

public class EnumBiConsumerMap<K extends Enum<K>, T, U> extends AbstractEnumFunctionMap<K, BiConsumer<T, U>> {

   /**
    * Creates an empty map with the specified key type.
    *
    * @param enumerationKeyClass the class object of the key type for this map.
    */

   public EnumBiConsumerMap(Class<K> enumerationKeyClass) {
      super(enumerationKeyClass);
   }

   /**
    * Looks up and performs the {@link BiConsumer} associated with the provided key.
    *
    * @param key the key whose associated {@link BiConsumer} is to be performed.
    * @param t the first input argument to the {@link BiConsumer}.
    * @param u the second input argument to the {@link BiConsumer}.
    * @throws NoSuchElementException when there is no map association for the provided key.
    */

   public void accept(K key, T t, U u) {
      var biConsumer = this.enumMap.get(key);
      if (biConsumer == null) {
         throw new NoSuchElementException();
      }
      biConsumer.accept(t, u);
   }

}

/* EOF */
