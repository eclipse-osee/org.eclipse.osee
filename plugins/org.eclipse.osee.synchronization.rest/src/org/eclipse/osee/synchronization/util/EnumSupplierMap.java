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
import java.util.function.Supplier;

/**
 * Implementation of the {@link EnumFunctionMap} interface for {@link Supplier} functional interfaces.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <T> the type of results supplied by the {@link Supplier} functional interface.
 */

public class EnumSupplierMap<K extends Enum<K>, T> extends AbstractEnumFunctionMap<K, Supplier<T>> {

   /**
    * Creates an empty map with the specified key type.
    *
    * @param enumerationKeyClass the class object of the key type for this map.
    */

   public EnumSupplierMap(Class<K> enumerationKeyClass) {
      super(enumerationKeyClass);
   }

   /**
    * Looks up and performs the {@link Supplier} associated with the provided key.
    *
    * @param key the key whose associated {@link Supplier} is to be performed.
    * @return the result provided by the {@link Supplier} functional interface implementation.
    * @throws NoSuchElementException when there is no map association for the provided key.
    */

   public T get(K key) {
      var supplier = this.enumMap.get(key);
      if (supplier == null) {
         throw new NoSuchElementException();
      }
      return supplier.get();
   }

}

/* EOF */
