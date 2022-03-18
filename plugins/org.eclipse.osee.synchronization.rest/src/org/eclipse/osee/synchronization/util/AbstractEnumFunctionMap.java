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

import java.util.AbstractSet;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * This class provides a skeletal implementation of the the {@link EnumFunctionMap} interface, to minimize the effort
 * required to implement this interface for maps of various different functional interfaces.<br>
 * <br>
 * To create an implementation the programmers needs to:
 * <ul>
 * <li>Create a class extending the {@link AbstractEnumFunctionMap}.</li>
 * <li>Specify an enumerated type for the parameter <code>K</code>.</li>
 * <li>Specify an functional interface for the parameter <code>F</code>.</li>
 * <li>Implement a constructor receiving a single parameter of type K that calls the single parameter constructor of the
 * super class.</li>
 * <li>Optionally implement a method with the same signature of the functional interface <code>F</code> except with one
 * additional parameter which is the key <code>K</code>. The implementation of this method should use the provided key
 * to obtain an implementation of the functional interface from the super class and then invoke the functional interface
 * implementation with the remaining provided parameters.</li>
 * </ul>
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <F> the mapped functional interfaces.
 */

public class AbstractEnumFunctionMap<K extends Enum<K>, F> implements EnumFunctionMap<K, F> {

   /**
    * The encapsulated {@link EnumMap} used to implement the map.
    */

   EnumMap<K, F> enumMap;

   /**
    * Creates a empty map with the specified key type.
    *
    * @param enumerationKeyClass the class object of the key type for this map.
    */

   AbstractEnumFunctionMap(Class<K> enumerationKeyClass) {

      /*
       * Create the EnumMap with an overloaded keySet method that returns a Set that is not modifiable by the user.
       */

      this.enumMap = new EnumMap<>(enumerationKeyClass) {

         /**
          * Serialization identifier for the encapsulated map.
          */

         private static final long serialVersionUID = 1L;

         /**
          * {@inheritDoc}<br>
          * <br>
          * The returned {@link Set} is not modifiable by the user. It is backed by the map, so changes to the map will
          * be reflected in the returned {@link Set}.
          */

         @Override
         public Set<K> keySet() {

            /*
             * Get the set of keys from super class.
             */

            Set<K> superKeySet = super.keySet();

            /*
             * Return an unmodifiable set to the user.
             */

            return new AbstractSet<K>() {

               @Override
               public boolean add(K k) {
                  throw new UnsupportedOperationException();
               }

               @Override
               public Iterator<K> iterator() {
                  Iterator<K> rvIterator = superKeySet.iterator();

                  return new Iterator<K>() {

                     @Override
                     public boolean hasNext() {
                        return rvIterator.hasNext();
                     }

                     @Override
                     public K next() {
                        return rvIterator.next();
                     }
                  };
               }

               @Override
               public int size() {
                  return superKeySet.size();
               }
            };

         }
      };
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsKey(K key) {
      if (key == null) {
         throw new NullPointerException();
      }

      return this.enumMap.containsKey(key);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<F> getFunction(K key) {
      return Optional.ofNullable(this.enumMap.get(key));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isEmpty() {
      return this.enumMap.isEmpty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<K> keySet() {
      return this.enumMap.keySet();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void put(K key, F function) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(function);

      if (this.enumMap.containsKey(key)) {
         throw new EnumMapDuplicateEntryException(key);
      }

      this.enumMap.put(key, function);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.enumMap.size();
   }

}

/* EOF */
