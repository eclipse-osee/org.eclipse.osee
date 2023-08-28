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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class provides a complete generic implementation of the {@link MapCollection} interface. The methods of this
 * class may be overridden by the implementation if a more efficient implementation for the {@link Collection} type is
 * possible.
 *
 * @author Loren K. Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the collections associated with the map keys.
 * @param <C> the type of collection associated with the map keys.
 */

public class AbstractMapCollection<K, V, C extends Collection<V>> implements MapCollection<K, V, C> {

   /**
    * A {@link Supplier} of new empty {@link Collection} objects of type <code>C</code>.
    */

   protected final Supplier<C> collectionSupplier;

   /**
    * Saves the map of collections.
    */

   protected final Map<K, C> mapCollection;

   /**
    * Creates a new empty {@link Map} of {@link Collection} objects.
    *
    * @param mapCollectionSupplier a {@link Supplier} that provides an implementation of the {@link Map} interface to be
    * used as the primary {@link Map} of {@link Collection} objects for this object.
    * @param collectionSupplier a {@link Supplier} that provides new empty implementation of the {@link Collection}
    * interface for the collections saved in this object.
    * @throws NullPointerException when <code>mapCollectionSupplier</code> is <code>null</code>,
    * <code>collectionSupper</code> is <code>null</code>, or <code>mapCollectionSupplier</code> returns
    * <code>null</code>.
    */

   public AbstractMapCollection(Supplier<Map<K, C>> mapCollectionSupplier, Supplier<C> collectionSupplier) {
      this.collectionSupplier = Objects.requireNonNull(collectionSupplier);
      this.mapCollection = Objects.requireNonNull(Objects.requireNonNull(mapCollectionSupplier)).get();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clear() {
      this.mapCollection.clear();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsKey(Object key) {
      return this.mapCollection.containsKey(key);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsValue(Object value) {
      return this.mapCollection.containsValue(value);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsValueInAnyCollection(Object value) {
      for (var collection : this.mapCollection.values()) {
         if (collection.contains(value)) {
            return true;
         }
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<Entry<K, C>> entrySet() {
      return this.mapCollection.entrySet();
   }

   @Override
   public void forEachEntry(Consumer<Map.Entry<K, V>> action) {
      //@formatter:off
      this.mapCollection.forEach
         (
            ( key, collection ) -> collection.forEach
                            (
                               ( value ) -> action.accept( Map.entry( key, value ) )
                            )
         );
      //@formatter:on
   }

   @Override
   public void forEachEntry(K key, Consumer<Map.Entry<K, V>> action) {
      var collection = this.mapCollection.get(key);
      if (Objects.isNull(collection)) {
         return;
      }
      //@formatter:off
      collection.forEach
         (
            ( value ) -> action.accept( Map.entry( key, value ) )
         );
      //@formatter:on
   }

   @Override
   public void forEachValue(BiConsumer<K, V> action) {
      //@formatter:off
      this.mapCollection.forEach
         (
            ( key, collection ) -> collection.forEach
                            (
                               ( value ) -> action.accept( key, value )
                            )
         );
      //@formatter:on
   }

   @Override
   public void forEachValue(K key, Consumer<V> action) {
      var collection = this.mapCollection.get(key);
      if (Objects.isNull(collection)) {
         return;
      }
      collection.forEach(action::accept);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C get(Object key) {
      return this.mapCollection.get(key);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<C> getOptional(K key) {
      return Optional.ofNullable(this.mapCollection.get(key));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isEmpty() {
      return this.mapCollection.isEmpty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<K> keySet() {
      return this.mapCollection.keySet();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C put(K key, C value) {
      return this.mapCollection.put(key, value);
   }

   @Override
   public C putAll(K key, C values) {
      var collection = this.mapCollection.get(key);
      if (Objects.isNull(collection)) {
         collection = this.collectionSupplier.get();
         this.mapCollection.put(key, collection);
      }
      collection.addAll(values);
      return collection;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void putAll(Map<? extends K, ? extends C> m) {
      this.mapCollection.putAll(m);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C putEntry(Entry<K, V> entry) {
      return this.putValue(entry.getKey(), entry.getValue());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C putValue(K key, V value) {
      var collection = this.mapCollection.get(key);
      if (Objects.isNull(collection)) {
         collection = this.collectionSupplier.get();
         this.mapCollection.put(key, collection);
      }
      collection.add(value);
      return collection;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C remove(Object key) {
      return this.mapCollection.remove(key);
   }

   @Override
   public boolean removeEntry(Map.Entry<K, V> entry) {
      //@formatter:off
      return
         Objects.nonNull( entry )
            ? this.removeValue( entry.getKey(), entry.getValue() )
            : false;
      //@formatter:on
   }

   @Override
   public boolean removeValue(K key, V value) {
      var collection = this.mapCollection.get(key);
      if (Objects.nonNull(collection)) {
         if (collection.remove(value)) {
            if (collection.isEmpty()) {
               this.mapCollection.remove(key);
            }
            return true;
         }
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.mapCollection.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size(K key) {
      var collection = this.mapCollection.get(key);
      //@formatter:off
      var size = Objects.nonNull( collection )
                    ? collection.size()
                    : 0;
      //@formatter:on
      return size;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int sizeValues() {
      int size = 0;
      for (var collection : this.mapCollection.values()) {
         size += collection.size();
      }
      return size;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<V> stream(K key) {
      var collection = this.mapCollection.get(key);
      //@formatter:off
      return
         Objects.nonNull( collection )
            ? collection.stream()
            : Stream.empty();
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<C> streamCollections() {
      return this.mapCollection.values().stream();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<Map.Entry<K, V>> streamEntries() {
      //@formatter:off
      return
         this.mapCollection
            .keySet()
            .stream()
            .flatMap
               (
                  ( key ) -> this.mapCollection
                                .get( key )
                                .stream()
                                .map( ( value ) -> Map.entry( key,value ) )
               );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<C> values() {
      return this.mapCollection.values();
   }

}

/* EOF */
