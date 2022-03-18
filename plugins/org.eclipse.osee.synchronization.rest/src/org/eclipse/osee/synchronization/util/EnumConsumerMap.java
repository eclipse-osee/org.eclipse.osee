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
import java.util.function.Consumer;

/**
 * Implementation of the {@link EnumFunctionMap} interface for {@link Consumer} functional interfaces.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <T> the type of the first argument to the {@link Consumer} functional interface.
 */

public class EnumConsumerMap<K extends Enum<K>, T> extends AbstractEnumFunctionMap<K, Consumer<T>> {

   /**
    * Creates an empty map with the specified key type.
    *
    * @param enumerationKeyClass the class object of the key type for this map.
    */

   public EnumConsumerMap(Class<K> enumerationKeyClass) {
      super(enumerationKeyClass);
   }

   /**
    * Looks up and performs the {@link Consumer} associated with the provided key.
    *
    * @param key the key whose associated {@link Consumer} is to be performed.
    * @param t the first input argument to the {@link Consumer}.
    * @throws NoSuchElementException when there is no map association for the provided key.
    */

   public void accept(K key, T t) {
      var consumer = this.enumMap.get(key);
      if (consumer == null) {
         throw new NoSuchElementException();
      }
      consumer.accept(t);
   }

}

/* EOF */
