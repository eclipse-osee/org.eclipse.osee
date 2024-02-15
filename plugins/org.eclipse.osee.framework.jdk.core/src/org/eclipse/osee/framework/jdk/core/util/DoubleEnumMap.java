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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;

/**
 * An implementation of the {@link DoubleMap} interface using nested {@link EnumMap}s.
 *
 * @author Loren K. Ashley
 * @param <Kp> the type of primary map keys.
 * @param <Ks> the type of the secondary map keys.
 * @param <V> the type of the map values.
 */

public class DoubleEnumMap<Kp extends Enum<Kp>, Ks extends Enum<Ks>, V> implements DoubleMap<Kp, Ks, V> {

   /**
    * Saves the primary map.
    */

   private final @NonNull EnumMap<Kp, Map<Ks, V>> primaryMap;

   /**
    * Saves the secondary key enumeration class for creating the secondary maps.
    */

   private final @NonNull Class<Ks> enumerationSecondaryKeyClass;

   /**
    * Creates a {@link DoubleEnumMap}.
    */

   public DoubleEnumMap(@NonNull Class<Kp> enumerationPrimaryKeyClass,
      @NonNull Class<Ks> enumerationSecondaryKeyClass) {

      final var safeEnumerationPrimaryKeyClass =
         Conditions.requireNonNull(enumerationPrimaryKeyClass, "enumerationPrimaryKeyClass");
      final var safeEnumerationSecondaryKeyClass =
         Conditions.requireNonNull(enumerationSecondaryKeyClass, "enumerationSecondaryKeyClass");

      this.primaryMap = new EnumMap<>(safeEnumerationPrimaryKeyClass);
      this.enumerationSecondaryKeyClass = safeEnumerationSecondaryKeyClass;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote Clears all entries from the primary map. Secondary maps are left unchanged.
    */

   @Override
   public void clear() {

      this.primaryMap.clear();

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsKey(Kp primaryKey) {
      return this.primaryMap.containsKey(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsKey(Kp primaryKey, Ks secondaryKey) {

      var secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? secondaryMap.containsKey(secondaryKey) : false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<V> get(Kp primaryKey, Ks secondaryKey) {

      var secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? Optional.ofNullable(secondaryMap.get(secondaryKey)) : Optional.empty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Map<Ks, V>> get(Kp primaryKey) {

      return Optional.ofNullable(this.primaryMap.get(primaryKey));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<Kp> keySet() {
      return primaryMap.keySet();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Set<Ks>> keySet(Kp primaryKey) {

      var secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? Optional.of(secondaryMap.keySet()) : Optional.empty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<V> put(Kp primaryKey, Ks secondaryKey, V value) {

      var secondaryMap = this.primaryMap.get(primaryKey);

      if (secondaryMap == null) {
         secondaryMap = new EnumMap<Ks, V>(this.enumerationSecondaryKeyClass);

         primaryMap.put(primaryKey, secondaryMap);

         secondaryMap.put(secondaryKey, value);

         return Optional.empty();
      }

      var priorValue = secondaryMap.put(secondaryKey, value);

      return Optional.ofNullable(priorValue);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Map<Ks, V>> put(Kp primaryKey, Map<Ks, V> secondaryMap) {

      var priorSecondaryMap = this.primaryMap.get(primaryKey);

      primaryMap.put(primaryKey, secondaryMap);

      return Optional.ofNullable(priorSecondaryMap);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {

      return this.primaryMap.values().stream().collect(Collectors.summingInt(Map<Ks, V>::size));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Integer> size(Kp primaryKey) {

      var secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? Optional.of(secondaryMap.size()) : Optional.empty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<V> values() {
      return new AbstractCollection<V>() {

         Iterator<Map<Ks, V>> primaryIterator = DoubleEnumMap.this.primaryMap.values().iterator();
         Iterator<V> secondaryIterator = primaryIterator.hasNext() ? primaryIterator.next().values().iterator() : null;

         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               @Override
               public boolean hasNext() {
                  if (secondaryIterator == null) {
                     return false;
                  }

                  if (secondaryIterator.hasNext()) {
                     return true;
                  }

                  if (primaryIterator.hasNext()) {
                     secondaryIterator = primaryIterator.next().values().iterator();

                     return this.hasNext();
                  }

                  secondaryIterator = null;

                  return false;
               }

               @Override
               public V next() {
                  if (secondaryIterator == null) {
                     throw new NoSuchElementException();
                  }

                  return secondaryIterator.next();
               }
            };
         }

         @Override
         public int size() {
            return DoubleEnumMap.this.size();
         }
      };
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<Collection<V>> values(Kp primaryKey) {

      var secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? Optional.of(secondaryMap.values()) : Optional.empty();
   }

}

/* EOF */
