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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of the {@link EnumFunctionalInterfaceMap} interface for {@link Function} functional interfaces.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <T> the type of the parameter for the {@link Function} functional interface.
 * @param <R> the type of results supplied by the {@link Function} functional interface.
 */

public class EnumFunctionMap<K extends Enum<K>, T, R> extends AbstractEnumFunctionalInterfaceMap<K, Function<T, R>> {

   /**
    * Creates an empty map with the specified key type.
    *
    * @param enumerationKeyClass the class object of the key type for this map.
    */

   public EnumFunctionMap(Class<K> enumerationKeyClass) {
      super(enumerationKeyClass);
   }

   /**
    * Looks up and performs the {@link Supplier} associated with the provided key.
    *
    * @param key the key whose associated {@link Supplier} is to be performed.
    * @return the result provided by the {@link Supplier} functional interface implementation.
    * @throws NullPointerException when the provided key is <code>null</code>.
    * @throws NoSuchElementException when there is no map association for the provided key.
    */

   public R apply(K key, T value) {
      var function = this.enumMap.get(Objects.requireNonNull(key));
      if (function == null) {
         throw new NoSuchElementException();
      }
      return function.apply(value);
   }

   /**
    * Creates an immutable {@link EnumFunctionMap} with the specified entries.
    *
    * @param <K> the enumeration type whose members may be used as keys in this map.
    * @param <T> the type of the argument to the {@link Function} functional interface.
    * @param <R> the type of the result from the {@link Function} functional interface.
    * @param enumerationKeyClass the {@link Class} of the enumeration whose members may be used as map keys.
    * @param entries the entries to be contained in the map.
    * @return the created {@link EnumFunctionMap}.
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
   public static <K extends Enum<K>, T, R> EnumFunctionMap<K, T, R> ofEntries(Class<K> enumerationKeyClass, Map.Entry<K, Function<T, R>>... entries) {

      return (EnumFunctionMap<K, T, R>) new EnumFunctionMap<K, T, R>(enumerationKeyClass) {
         @Override
         public void put(K key, Function<T, R> function) {
            throw new UnsupportedOperationException();
         }
      }.ofEntriesLoader(entries);
   }
}

/* EOF */
