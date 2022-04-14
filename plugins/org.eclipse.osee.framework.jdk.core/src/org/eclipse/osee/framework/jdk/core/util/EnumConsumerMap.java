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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementation of the {@link EnumFunctionalInterfaceMap} interface for {@link Consumer} functional interfaces.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <T> the type of the first argument to the {@link Consumer} functional interface.
 */

public class EnumConsumerMap<K extends Enum<K>, T> extends AbstractEnumFunctionalInterfaceMap<K, Consumer<T>> {

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
    * @throws NullPointerException when the provided key is <code>null</code>.
    * @throws NoSuchElementException when there is no map association for the provided key.
    */

   public void accept(K key, T t) {
      var consumer = this.enumMap.get(Objects.requireNonNull(key));
      if (consumer == null) {
         throw new NoSuchElementException();
      }
      consumer.accept(t);
   }

   /**
    * Creates an immutable {@link EnumConsumerMap} with the specified entries.
    *
    * @param <K> the enumeration type whose members may be used as keys in this map.
    * @param <T> the type of the argument to the {@link Consumer} functional interface.
    * @param enumerationKeyClass the {@link Class} of the enumeration whose members may be used as map keys.
    * @param entries the entries to be contained in the map.
    * @return the created {@link EnumConsumerMap}.
    * @throws NullPointerException when:
    * <ul>
    * <li>the <code>entries</code> array reference is <code>null</code>, or</li>
    * <li>an entry in the <code>entries</code> array is <code>null</code>.
    * </ul>
    * @throws EnumMapDuplicateEntryException when an attempt is made to add an entry to the map when a mapping for the
    * provided key already exists.
    */

   @SafeVarargs
   @SuppressWarnings({"unchecked", "varargs"})
   public static <K extends Enum<K>, T> EnumConsumerMap<K, T> ofEntries(Class<K> enumerationKeyClass, Map.Entry<K, Consumer<T>>... entries) {

      return (EnumConsumerMap<K, T>) new EnumConsumerMap<K, T>(enumerationKeyClass) {
         @Override
         public void put(K key, Consumer<T> function) {
            throw new UnsupportedOperationException();
         }
      }.ofEntriesLoader(entries);
   }

}

/* EOF */
