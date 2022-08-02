/*
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
import java.util.function.BiFunction;

/**
 * Implementation of the {@link EnumFunctionalInterfaceMap} interface for {@link BiFunction} functional interfaces.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <T> the type of the first parameter for the {@link BiFunction} functional interface.
 * @param <U> the type of the second parameter for the {@link BiFunction} functional interface.
 * @param <R> the type of results supplied by the {@link BiFunction} functional interface.
 */

public class EnumBiFunctionMap<K extends Enum<K>, T, U, R> extends AbstractEnumFunctionalInterfaceMap<K, BiFunction<T, U, R>> {

   /**
    * Creates an empty map with the specified key type.
    *
    * @param enumerationKeyClass the class object of the key type for this map.
    */

   public EnumBiFunctionMap(Class<K> enumerationKeyClass) {
      super(enumerationKeyClass);
   }

   /**
    * Looks up and performs the {@link BiFunction} associated with the provided key.
    *
    * @param key the key whose associated {@link BiFunction} is to be performed.
    * @param t the first function argument
    * @param u the second function argument
    * @return the result provided by the {@link BiFunction} functional interface implementation.
    * @throws NullPointerException when the provided key is <code>null</code>.
    * @throws NoSuchElementException when there is no map association for the provided key.
    */

   public R apply(K key, T t, U u) {
      var biFunction = this.enumMap.get(Objects.requireNonNull(key));
      if (biFunction == null) {
         throw new NoSuchElementException();
      }
      return biFunction.apply(t, u);
   }

   /**
    * Creates an immutable {@link EnumFunctionMap} with the specified entries.
    *
    * @param <K> the enumeration type whose members may be used as keys in this map.
    * @param <T> the type of the first argument to the {@link BiFunction} functional interface.
    * @param <U> the type of the second argument to the {@link BiFunction} functional interface.
    * @param <R> the type of the result from the {@link BiFunction} functional interface.
    * @param enumerationKeyClass the {@link Class} of the enumeration whose members may be used as map keys.
    * @param entries the entries to be contained in the map.
    * @return the created {@link EnumBiFunctionMap}.
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
   public static <K extends Enum<K>, T, U, R> EnumBiFunctionMap<K, T, U, R> ofEntries(Class<K> enumerationKeyClass, Map.Entry<K, BiFunction<T, U, R>>... entries) {

      return (EnumBiFunctionMap<K, T, U, R>) new EnumBiFunctionMap<K, T, U, R>(enumerationKeyClass) {
         @Override
         public void put(K key, BiFunction<T, U, R> biFunction) {
            throw new UnsupportedOperationException();
         }
      }.ofEntriesLoader(entries);
   }
}

/* EOF */
