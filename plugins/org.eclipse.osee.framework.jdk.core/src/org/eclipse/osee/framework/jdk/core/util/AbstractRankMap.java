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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An implementation of the {@link RankMap} interface using nested maps that implement the {@link Map} interface. Access
 * time for basic operations (get and associate) will increase with the rank of the map as a map operation is performed
 * for each rank of the {@link RankMap}. The implementation provided by this class is not synchronized. Using
 * synchronized sub-maps is not sufficient to support Concurrency.
 *
 * @param <V> the type of value stored in the map.
 * @author Loren K. Ashley
 */

public class AbstractRankMap<V> implements RankMap<V> {

   /**
    * An implementation of the {@link RankMap.Entry} interface for the associations stored in the
    * {@link AbstractRankMap}.
    *
    * @param <V> the type of value stored in the map.
    */

   static class AbstractRankMapEntry<V> implements RankMap.Entry<V> {

      /**
       * A reference to the {@link AbstractRankMap} the {@link AbstractRankMapEntry} belongs to. This member is used to
       * make two instances of the {@link AbstractRankMapEntry} with the same keys and values that belong to different
       * maps unequal.
       */

      private final AbstractRankMap<V> abstractRankMap;

      /**
       * Caches the hash code value for the {@link AbstractRankMapEntry}.
       */

      private int hashCode;

      /**
       * An array containing the full key set for the association represented by the {@link AbstractRankMapEntry}.
       */

      private final Object[] keys;

      /**
       * <code>false</code>, when the {@link AbstractRankMapEntry} represents an association in the map. When the
       * association is removed from the map this flag is set to <code>true</code>.
       */

      private boolean removed;

      /**
       * The value associated with the keys.
       */

      private V value;

      /**
       * Creates a new {@link AbstractRankMapEntry} with the provided full key set and value. The keys array is copied
       * so that changes made to the provided array will not affect the entry.
       *
       * @param keys the full key set for the association.
       * @param value the associated value.
       * @param abstractRankMap the map the association belongs to.
       */

      AbstractRankMapEntry(Object[] keys, V value, AbstractRankMap<V> abstractRankMap) {

         //@formatter:off
         assert
                 Objects.nonNull( keys )
              && Objects.nonNull( abstractRankMap )
              && Objects.nonNull( value )
              && ( keys.length == abstractRankMap.rank() )
            : "AbstractRankMapEntry::new, invalid parameters.";
         //@formatter:on

         this.keys = keys.clone();
         this.value = value;
         this.removed = false;
         this.abstractRankMap = abstractRankMap;
         this.calculateHashCode();
      }

      /**
       * Predicate to determine if this {@link AbstractRankMapEntry} belongs to the specified map.
       *
       * @param abstractRankMap the map to test for membership.
       * @return <code>true</code>, when the {@link AbstractRankMapEntry} is a member of the specified
       * {@link AbstractRankMap}; otherwise, <code>false</code>.
       */

      boolean belongsTo(AbstractRankMap<V> abstractRankMap) {
         return !this.removed && (this.abstractRankMap == abstractRankMap);
      }

      /**
       * Calculates and caches a hash code for the map entry from the full key set and the associated value.
       */

      private void calculateHashCode() {
         var hashCode = this.value.hashCode();

         for (int i = 0; i < this.keys.length; i++) {
            var memberHash = this.keys[i].hashCode() * 31;
            memberHash = (memberHash << i) | (memberHash >> (Integer.SIZE - i));
            hashCode = hashCode ^ memberHash;
         }

         this.hashCode = hashCode;
      }

      /**
       * {@inheritDoc}
       *
       * @implNote The cached hash code is used to short-circuit the equality comparison as the value and key
       * comparisons could be expensive.
       */

      @Override
      public boolean equals(Object other) {
         if (!(other instanceof AbstractRankMapEntry<?>)) {
            return false;
         }

         @SuppressWarnings("unchecked")
         var otherEntry = (AbstractRankMapEntry<V>) other;

         //@formatter:off
         return
                ( this.abstractRankMap == otherEntry.abstractRankMap )
             && ( this.hashCode        == otherEntry.hashCode        )
             && !this.removed
             && !otherEntry.removed
             && this.value.equals( otherEntry.value )
             && Arrays.equals( this.keys, otherEntry.keys );
         //@formatter:on
      }

      /**
       * {@inheritDoc}
       *
       * @throws IllegalStateException {@inheritDoc}
       */

      @Override
      public Object getKey(int index) {
         if (this.removed) {
            throw new IllegalStateException();
         }

         return this.keys[index];
      }

      /**
       * {@inheritDoc}
       *
       * @throws IllegalStateException {@inheritDoc}
       */

      @Override
      public Object[] getKeyArray() {
         if (this.removed) {
            throw new IllegalStateException();
         }

         return this.keys.clone();
      }

      /**
       * {@inheritDoc}
       *
       * @throws IllegalStateException {@inheritDoc}
       */

      @Override
      public V getValue() {
         if (this.removed) {
            throw new IllegalStateException();
         }

         return this.value;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public int hashCode() {
         return this.hashCode;
      }

      /**
       * {@inheritDoc}
       *
       * @throws IllegalStateException {@inheritDoc}
       */

      @Override
      public int rank() {
         if (this.removed) {
            throw new IllegalStateException();
         }

         return this.keys.length;
      }

      /**
       * This method is called by the map when the entry is removed from the map.
       */

      void setRemoved() {
         this.removed = true;
      }

      /**
       * This method is called by the map when the value associated with the entry's keys is changed. A new hash code is
       * calculated and cached.
       *
       * @param value the new value to be associated with the entry's keys.
       */

      void setValue(V value) {
         this.value = value;
         this.calculateHashCode();
      }

      /**
       * {@inheritDoc}
       *
       * @throws IllegalStateException {@inheritDoc}
       */

      @Override
      public Stream<Object> streamKeys() {
         if (this.removed) {
            throw new IllegalStateException();
         }
         return Arrays.stream(this.keys);
      }
   }

   /**
    * A simple {@link Iterator} class used for providing a {@link EntrySet} view of the map with only one
    * {@link RankMap.Entry}.
    */

   class SingleEntryIterator implements Iterator<RankMap.Entry<V>> {

      /**
       * The sole entry to be iterated. Set to <code>null</code> after the {@link Iterator} has provided the
       * {@link Entry}.
       */

      Entry<V> entry;

      /**
       * Creates a new {@link Iterator} to "iterate" over the provided single {@link Entry}.
       *
       * @param entry the sole {@link Entry} to be returned by the {@link Iterator}.
       */

      SingleEntryIterator(Entry<V> entry) {
         this.entry = entry;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean hasNext() {
         return Objects.nonNull(this.entry);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Entry<V> next() {
         if (Objects.isNull(this.entry)) {
            throw new NoSuchElementException();
         }

         var rv = this.entry;
         this.entry = null;
         return rv;
      }
   }

   /**
    * An {@link Iterator} class used for providing a {@link EntrySet} view or a {@link Stream} of the map {@link Entry}
    * objects under a full or partial key set.
    */

   class EntryIterator implements Iterator<RankMap.Entry<V>> {

      /**
       * The index in the <code>iterators</code> array that is currently active.
       */

      int atRankIndex;

      /**
       * The number of sub-maps to be iterated.
       */

      int entryIteratorRank;

      /**
       * An array of the sub-map iterators at each level of the map currently iterating.
       */

      Iterator<Object>[] iterators;

      /**
       * Creates a new {@link Iterator} for the the map {@link Entry} objects under the provided full or partial key
       * set.
       *
       * @param keys a full or partial key set.
       */

      EntryIterator(Object... keys) {

         var keyCount = Objects.nonNull(keys) ? keys.length : 0;

         /*
          * keyCount > rank, error
          */

         if (keyCount > AbstractRankMap.this.rank) {
            throw new RankMapTooManyKeysException(AbstractRankMap.this, keys);
         }

         //@formatter:off
         assert
                 Objects.isNull( AbstractRankMap.this.keyValidators )
              || ParameterArray.validateSizeAndElements( keys, 0, AbstractRankMap.this.rank, AbstractRankMap.this.keyValidators )
            : "AbstractRankMap::EntryIterator, key set failed validation.";
         //@formatter:on

         this.entryIteratorRank = AbstractRankMap.this.rank - keyCount;

         if (this.entryIteratorRank == 0) {
            @SuppressWarnings("unchecked")
            Iterator<Object>[] iteratorArray = new Iterator[1];
            this.iterators = iteratorArray;

            @SuppressWarnings({"unchecked", "null"})
            var baseIterator = (Iterator<Object>) (Object) new SingleEntryIterator(
               (Entry<V>) AbstractRankMap.this.getSubMapOrEntry(keys, keys.length));
            this.iterators[0] = baseIterator;

            this.entryIteratorRank = 1;
            return;
         }

         this.atRankIndex = this.entryIteratorRank - 1;

         @SuppressWarnings("unchecked")
         Iterator<Object>[] iteratorArray = new Iterator[this.entryIteratorRank];
         this.iterators = iteratorArray;

         @SuppressWarnings({"unchecked", "null"})
         var baseIterator =
            ((Map<Object, Object>) AbstractRankMap.this.getSubMapOrEntry(keys, keys.length)).values().iterator();
         this.iterators[0] = baseIterator;

         for (int r = 1; r < this.entryIteratorRank; r++) {

            if (Objects.nonNull(this.iterators[r - 1]) && this.iterators[r - 1].hasNext()) {
               @SuppressWarnings("unchecked")
               var nextIterator = ((Map<Object, Object>) this.iterators[r - 1].next()).values().iterator();
               this.iterators[r] = nextIterator;
            } else {
               this.iterators[r] = null;
            }
         }
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean hasNext() {

         if (Objects.isNull(this.iterators[this.entryIteratorRank - 1])) {
            return false;
         }

         if (this.iterators[this.entryIteratorRank - 1].hasNext()) {
            return true;
         }

         int i;

         for (i = this.entryIteratorRank - 2; i >= 0; i--) {
            if (this.iterators[i].hasNext()) {
               break;
            }
         }

         if (i < 0) {
            return false;
         }

         for (i = i + 1; i < this.entryIteratorRank; i++) {
            if (Objects.nonNull(this.iterators[i - 1]) && this.iterators[i - 1].hasNext()) {
               @SuppressWarnings("unchecked")
               var nextIterator = ((Map<Object, Object>) this.iterators[i - 1].next()).values().iterator();
               this.iterators[i] = nextIterator;
            } else {
               this.iterators[i] = null;
            }
         }

         if (Objects.isNull(this.iterators[this.entryIteratorRank - 1])) {
            return false;
         }

         return this.iterators[this.entryIteratorRank - 1].hasNext();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Entry<V> next() {
         if (Objects.isNull(this.iterators[this.entryIteratorRank - 1])) {
            throw new NoSuchElementException();
         }

         @SuppressWarnings("unchecked")
         var entry = (RankMap.Entry<V>) this.iterators[this.entryIteratorRank - 1].next();

         return entry;
      }

   }

   /**
    * A non-splitting {@link Spliterator} class used for providing a {@link Stream} of the map {@link Entry} objects
    * under a full or partial key set.
    */

   class EntrySpliterator implements Spliterator<RankMap.Entry<V>> {

      /**
       * Counts the number of {@link Entry} objects provided by the {@link Spliterator} to provide an estimate of the
       * remaining number of {@link Entry} objects in the {@link Stream}.
       */

      int index;

      /**
       * The {@link Iterator} implementation used by the {@link Spliterator}.
       */

      EntryIterator entryIterator;

      /**
       * Creates a new {@link Spliterator} for the the map {@link Entry} objects under the provided full or partial key
       * set.
       *
       * @param keys a full or partial key set.
       */

      EntrySpliterator(Object... keys) {
         this.index = 0;
         this.entryIterator = new EntryIterator(keys);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean tryAdvance(Consumer<? super Entry<V>> action) {

         if (this.entryIterator.hasNext()) {
            action.accept(this.entryIterator.next());
            return true;
         }

         return false;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Spliterator<Entry<V>> trySplit() {
         return null;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public long estimateSize() {
         return AbstractRankMap.this.size - index;
      }

      /**
       * {@inheritDoc}
       *
       * @return {@link Spliterator.NONNULL}
       */

      @Override
      public int characteristics() {
         return Spliterator.NONNULL;
      }

   }

   /**
    * An implementation of the {@link RankMap.EntrySet} interface used to provide {@link Set} views of
    * {@link RankMap.Entry} objects stored in the map.
    *
    * @param <W> the type of value stored in the map.
    */

   class AbstractRankMapEntrySet<W extends V> extends AbstractSet<RankMap.Entry<V>> implements RankMap.EntrySet<V> {

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean contains(Object object) {
         if (!(object instanceof AbstractRankMap.AbstractRankMapEntry)) {
            return false;
         }

         @SuppressWarnings("unchecked")
         var entry = (AbstractRankMap.AbstractRankMapEntry<V>) object;

         if (!entry.belongsTo(AbstractRankMap.this)) {
            return false;
         }

         return entry == AbstractRankMap.this.getEntry(entry.getKeyArray()).orElse(null);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean containsKeys(Object... keys) {
         return AbstractRankMap.this.containsKeys(keys);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Iterator<Entry<V>> iterator() {
         return new EntryIterator();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public int size() {
         return AbstractRankMap.this.size();
      }

   }

   /**
    * Once a full entry set has been created it is cached for future requests.
    */

   private AbstractRankMapEntrySet<V> entrySet;

   /**
    * A {@link String} identifier for the map.
    */

   private final String identifier;

   /**
    * Saves the {@link Function} used to validate the keys when assertions are enabled.
    */

   private final Predicate<Object>[] keyValidators;

   /**
    * Saves the {@link Supplier} used to obtain map implementations.
    */

   private final Supplier<Map<Object, Object>> mapSupplier;

   /**
    * Saves the primary map used to implement the store.
    */

   private final Map<Object, Object> primaryMap;

   /**
    * The map's rank.
    */

   private final int rank;

   /**
    * Counter used to track the number of objects in the map.
    *
    * @implNote Finding all the maps of maps of maps etc to get a collection of all the maps containing the values so
    * the number of values in each of those maps can be summed can be an expensive process. This counter is incremented
    * every time a new value is added and decrement every time a value is removed.
    */

   private int size;

   /**
    * Creates a new empty {@link AbstractRankMap}.
    *
    * @param identifier an identification string for the map. This parameter may be <code>null</code>/
    * @param rank the number of map levels implemented by the map.
    * @param mapSupplier a {@link Supplier} of {@link Map} implementations used to obtain the primary and sub-maps.
    * @param keyValidators an array of {@link Predicate} implementations used to validate keys when assertions are
    * enabled. This parameter may be <code>null</code>.
    * @throws IllegalArgumentException when the specified <code>rank</code> is less than one.
    * @throws NullPointerException when the <code>mapSupplier</code> is <code>null</code>.
    */

   AbstractRankMap(String identifier, int rank, Supplier<Map<Object, Object>> mapSupplier, Predicate<Object>[] keyValidators) {

      if (rank < 1) {
         throw new IllegalArgumentException();
      }

      this.identifier = Objects.nonNull(
         identifier) ? identifier : new StringBuilder(512).append(this.getClass().getName()).append(":").append(
            super.hashCode()).toString();

      this.rank = rank;

      this.mapSupplier = Objects.requireNonNull(mapSupplier);

      this.keyValidators = keyValidators;

      this.size = 0;
      this.primaryMap = mapSupplier.get();

      this.entrySet = null;
   }

   /**
    * Private method gets the sub-map for the specified number of the highest rank keys in the key array. This method
    * assumes that the <code>keys</code> array has already been validated.
    *
    * @param keys array of keys to get the sub map for.
    * @param keyCount the number of keys in the <code>keys</code> array to use.
    * @return if there is an association with the provided keys, the sub-map or entry associated with the keys;
    * otherwise, <code>null</code>.
    */

   @SuppressWarnings("unchecked")
   private Object getSubMapOrEntry(Object[] keys, int keyCount) {

      var limit = keyCount < keys.length ? keyCount : keys.length;

      Object subMapOrValue = this.primaryMap;

      for (int i = 0; i < limit; i++) {

         subMapOrValue = ((Map<Object, Object>) subMapOrValue).get(keys[i]);

         if (Objects.isNull(subMapOrValue)) {
            break;
         }

      }

      return subMapOrValue;
   }

   /**
    * Private method that gets the sub-maps and entry associated the provided full or partial key set. This method
    * assumes that the <code>keys</code> array has already been validated.
    *
    * @param keys array of keys to get the sub-maps and entry under.
    * @param keyCount the number of keys in the <code>keys</code> array to use.
    * @return an array the primary map in index 0 and with the sub-maps or entry associated with each provided key in
    * the index corresponding to the keys rank. When the map does not contain an association for a key in the key set,
    * the index corresponding to that key's rank and all higher indexes of the array will be <code>null</code>.
    */

   @SuppressWarnings("unchecked")
   private Object[] getSubMapsOrEntries(Object[] keys, int keyCount) {

      var mapsOrValues = new Object[keyCount + 1];

      var limit = keyCount < keys.length ? keyCount : keys.length;

      Object subMapOrValue = this.primaryMap;
      mapsOrValues[0] = subMapOrValue;

      for (int k = 0, o = 1; k < limit; k++, o++) {

         subMapOrValue = ((Map<Object, Object>) subMapOrValue).get(keys[k]);

         mapsOrValues[o] = subMapOrValue;

         if (Objects.isNull(subMapOrValue)) {
            break;
         }

      }

      return mapsOrValues;
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    * @throws RankMapInsufficientKeysException {@inheritDoc}
    * @throws RankMapNullValueException {@inheritDoc}
    */

   @Override
   public Optional<V> associate(V value, Object... keys) {

      if (Objects.isNull(keys) || keys.length < this.rank) {
         throw new RankMapInsufficientKeysException(this, keys);
      }

      if (keys.length > this.rank) {
         throw new RankMapTooManyKeysException(this, keys);
      }

      if (Objects.isNull(value)) {
         throw new RankMapNullValueException(this, keys);
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateNonNullSizeAndElements( keys, this.rank, this.rank, this.keyValidators )
         : "AbstractRankMap::associate, key set failed validation.";
      //@formatter:on

      var subMap = this.primaryMap;

      for (int i = 0; i < this.rank - 1; i++) {
         var parentMap = subMap;

         @SuppressWarnings("unchecked")
         var nextSubMap = (Map<Object, Object>) subMap.get(keys[i]);

         if (Objects.isNull(nextSubMap)) {
            subMap = this.mapSupplier.get();
            parentMap.put(keys[i], subMap);
         } else {
            subMap = nextSubMap;
         }
      }

      @SuppressWarnings("unchecked")
      var priorEntry = (AbstractRankMapEntry<V>) subMap.get(keys[this.rank - 1]);

      if (Objects.nonNull(priorEntry)) {
         var priorValue = priorEntry.getValue();
         priorEntry.setValue(value);
         return Optional.of(priorValue);
      }

      subMap.put(keys[this.rank - 1], new AbstractRankMapEntry<>(keys, value, this));
      this.size++;
      return Optional.empty();
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    * @throws RankMapInsufficientKeysException {@inheritDoc}
    * @throws RankMapNullValueException {@inheritDoc}
    * @throws RankMapDuplicateEntryException {@inheritDoc}
    */

   @Override
   public void associateThrowOnDuplicate(V value, Object... keys) {

      if (Objects.isNull(keys) || keys.length < this.rank) {
         throw new RankMapInsufficientKeysException(this, keys);
      }

      if (keys.length > this.rank) {
         throw new RankMapTooManyKeysException(this, keys);
      }

      if (Objects.isNull(value)) {
         throw new RankMapNullValueException(this, keys);
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateNonNullSizeAndElements( keys, this.rank, this.rank, this.keyValidators )
         : "AbstractRankMap::associateThrowOnDuplicate, key set failed validation.";
      //@formatter:on

      var subMap = this.primaryMap;

      for (int i = 0; i < this.rank - 1; i++) {
         var parentMap = subMap;

         @SuppressWarnings("unchecked")
         var nextSubMap = (Map<Object, Object>) subMap.get(keys[i]);

         if (Objects.isNull(nextSubMap)) {
            subMap = this.mapSupplier.get();
            parentMap.put(keys[i], subMap);
         } else {
            subMap = nextSubMap;
         }
      }

      if (subMap.containsKey(keys[this.rank - 1])) {
         throw new RankMapDuplicateEntryException(this, value, keys);
      }

      subMap.put(keys[this.rank - 1], new AbstractRankMapEntry<>(keys, value, this));
      this.size++;
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    * @throws RankMapInsufficientKeysException {@inheritDoc}
    */

   @Override
   public boolean containsKeys(Object... keys) {

      if (Objects.isNull(keys) || (keys.length == 0)) {
         throw new RankMapInsufficientKeysException(this, keys);
      }

      if (keys.length > this.rank) {
         throw new RankMapTooManyKeysException(this, keys);
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateNonNullSizeAndElements( keys, 1, this.rank, this.keyValidators )
         : "AbstractRankMap::containsKeys, key set failed validation.";
      //@formatter:on

      return Objects.nonNull(this.getSubMapOrEntry(keys, keys.length));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsKeysNoExceptions(Object... keys) {

      //@formatter:off
      assert
             (    Objects.isNull( this.keyValidators )
               || ParameterArray.validateElements( keys, this.keyValidators ) )
         : "AbstractRankMap::containsKeysNoExceptions, key set failed validation.";
      //@formatter:on

      if (Objects.isNull(keys) || (keys.length == 0) || (keys.length > this.rank)) {
         return false;
      }

      return Objects.nonNull(this.getSubMapOrEntry(keys, keys.length));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public RankMap.EntrySet<V> entrySet() {
      return Objects.nonNull(this.entrySet) ? this.entrySet : (this.entrySet = new AbstractRankMapEntrySet<>());
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    * @throws RankMapInsufficientKeysException {@inheritDoc}
    */

   @Override
   public Optional<V> get(Object... keys) {

      if (Objects.isNull(keys) || (keys.length < this.rank)) {
         throw new RankMapInsufficientKeysException(this, keys);
      }

      if (keys.length > this.rank) {
         throw new RankMapTooManyKeysException(this, keys);
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateNonNullSizeAndElements( keys, this.rank, this.rank, this.keyValidators )
         : "AbstractRankMap::get, key set failed validation.";
      //@formatter:on

      @SuppressWarnings("unchecked")
      var entry = (AbstractRankMapEntry<V>) this.getSubMapOrEntry(keys, keys.length);

      return Objects.nonNull(entry) ? Optional.of(entry.getValue()) : Optional.empty();
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    * @throws RankMapInsufficientKeysException {@inheritDoc}
    */

   @Override
   public Optional<RankMap.Entry<V>> getEntry(Object... keys) {

      if (Objects.isNull(keys) || (keys.length < this.rank)) {
         throw new RankMapInsufficientKeysException(this, keys);
      }

      if (keys.length > this.rank) {
         throw new RankMapTooManyKeysException(this, keys);
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateNonNullSizeAndElements( keys, this.rank, this.rank, this.keyValidators )
         : "AbstractRankMap::getEntry, key set failed validation.";
      //@formatter:on

      @SuppressWarnings("unchecked")
      var entry = (AbstractRankMapEntry<V>) this.getSubMapOrEntry(keys, keys.length);

      return Optional.ofNullable(entry);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<RankMap.Entry<V>> getEntryNoExceptions(Object... keys) {

      if (Objects.isNull(keys) || (keys.length != this.rank)) {
         return Optional.empty();
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateNonNullSizeAndElements( keys, this.rank, this.rank, this.keyValidators )
         : "AbstractRankMap::getEntry, key set failed validation.";
      //@formatter:on

      @SuppressWarnings("unchecked")
      var entry = (AbstractRankMapEntry<V>) this.getSubMapOrEntry(keys, keys.length);

      return Optional.ofNullable(entry);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<V> getNoExceptions(Object... keys) {

      if (Objects.isNull(keys) || (keys.length != this.rank)) {
         return Optional.empty();
      }

      //@formatter:off
      assert
             Objects.isNull( this.keyValidators )
           || ParameterArray.validateElements( keys, this.keyValidators )
         : "AbstractRankMap::getNoExceptions, key set failed validation.";
      //@formatter:on

      @SuppressWarnings("unchecked")
      var entry = (AbstractRankMapEntry<V>) this.getSubMapOrEntry(keys, keys.length);

      return Objects.nonNull(entry) ? Optional.of(entry.getValue()) : Optional.empty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String identifier() {
      return this.identifier;
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    * @throws RankMapInsufficientKeysException {@inheritDoc}
    */

   @SuppressWarnings("unchecked")
   @Override
   public Optional<V> remove(Object... keys) {

      if (Objects.isNull(keys) || (keys.length < this.rank)) {
         throw new RankMapInsufficientKeysException(this, keys);
      }

      if (keys.length > this.rank) {
         throw new RankMapTooManyKeysException(this, keys);
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateNonNullSizeAndElements( keys, this.rank, this.rank, this.keyValidators )
         : "AbstractRankMap::remove, key set failed validation.";
      //@formatter:on

      var mapsOrEntries = this.getSubMapsOrEntries(keys, keys.length);

      if (Objects.isNull(mapsOrEntries[this.rank])) {

         /*
          * No entry and hence no value was associated with the key set
          */

         return Optional.empty();
      }

      for (int i = this.rank - 1; (i >= 0); i--) {

         ((Map<Object, Object>) mapsOrEntries[i]).remove(keys[i]);

         if (((Map<Object, Object>) mapsOrEntries[i]).size() > 0) {
            break;
         }
      }

      var removedEntry = (AbstractRankMapEntry<V>) mapsOrEntries[this.rank];
      var removedValue = removedEntry.getValue();
      removedEntry.setRemoved();
      this.size--;

      return Optional.of(removedValue);
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("unchecked")
   @Override
   public Optional<V> removeNoException(Object... keys) {

      //@formatter:off
      assert
             (    Objects.isNull( this.keyValidators )
               || ParameterArray.validateElements( keys, this.keyValidators ) )
         : "AbstractRankMap::removeNoExceptions, key set failed validation.";
      //@formatter:on

      if (Objects.isNull(keys) || (keys.length != this.rank)) {
         return Optional.empty();
      }

      var mapsOrEntries = this.getSubMapsOrEntries(keys, keys.length);

      if (Objects.isNull(mapsOrEntries[this.rank])) {

         /*
          * No value was associated with the key set
          */

         return Optional.empty();
      }

      for (int i = this.rank - 1; (i >= 0); i--) {

         ((Map<Object, Object>) mapsOrEntries[i]).remove(keys[i]);

         if (((Map<Object, Object>) mapsOrEntries[i]).size() > 0) {
            break;
         }
      }

      var removedEntry = (AbstractRankMapEntry<V>) mapsOrEntries[this.rank];
      var removedValue = removedEntry.getValue();
      removedEntry.setRemoved();
      this.size--;

      return Optional.ofNullable(removedValue);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int rank() {
      return this.rank;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.size;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size(Object... keys) {

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      if (keyCount > this.rank) {
         throw new RankMapTooManyKeysException(this, keys);
      }

      //@formatter:off
      assert
             (    Objects.isNull( this.keyValidators )
               || ParameterArray.validateElements( keys, this.keyValidators ) )
         : "AbstractRankMap::size, key set failed validation.";
      //@formatter:on

      var subMapOrEntry = this.getSubMapOrEntry(keys, keyCount);

      if (keyCount == this.rank) {
         //got entry
         return Objects.nonNull(subMapOrEntry) ? 1 : 0;
      }

      //got submap

      if (Objects.isNull(subMapOrEntry)) {
         return 0;
      }

      var subMap = (Map<Object, Object>) subMapOrEntry;

      return subMap.size();
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    */

   @Override
   public Stream<V> stream(Object... keys) {
      return this.streamEntries(keys).map(RankMap.Entry<V>::getValue);
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    */

   @Override
   public Stream<RankMap.Entry<V>> streamEntries(Object... keys) {
      return StreamSupport.stream(new EntrySpliterator(keys), false);
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    */

   @Override
   public Stream<Object> streamKeysAtAndBelow(Object... keys) {

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      if (keyCount > this.rank - 1) {
         throw new RankMapTooManyKeysException(this, keys);
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateSizeAndElements( keys, 0, this.rank - 1, this.keyValidators )
         : "AbstractRankMap::streamKeysAtAndBelow, key set failed validation.";
      //@formatter:on

      /*
       * 0 <= keyCount < rank - 1
       */

      /*
       * Array used to accumulate a list of the sub-maps at each level
       */

      @SuppressWarnings("unchecked")
      List<Map<Object, Object>>[] levelListArray = new List[this.rank - keyCount];
      int l = 0;

      /*
       * Get the sub-map specified by the provided keys. Since the number of keys must be less than the rank of the
       * store the returned value will be a map or null.
       */

      @SuppressWarnings({"unchecked", "null"})
      var subMap = (Map<Object, Object>) this.getSubMapOrEntry(keys, keys.length);

      if (Objects.isNull(subMap)) {
         return Stream.empty();
      }

      /*
       * Save a list for the selected sub-map
       */

      var subMapList = new ArrayList<Map<Object, Object>>();

      subMapList.add(subMap);

      levelListArray[l++] = subMapList;

      /*
       * Create a list of the all the sub-maps at each level below the selected sub-map
       */

      //@formatter:off
      for( int i = keyCount + 1; i < this.rank; i++, l++)
      {
         @SuppressWarnings("unchecked")
         var levelList = (List<Map<Object, Object>>) (Object) levelListArray[l - 1].stream()
                            .flatMap( ( map ) -> map.values().stream() )
                            .collect( Collectors.toList() );

         levelListArray[l] = levelList;
      }

      /*
       * Generate a stream of the keys from each sub-map in the levelListArray
       */

      return
         Arrays.stream(levelListArray)
            .flatMap(List::stream)
            .map(Map::keySet)
            .flatMap(Set::stream);

      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    */

   @Override
   public Stream<Object> streamKeysAt(Object... keys) {

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      if (keyCount > this.rank - 1) {

         throw new RankMapTooManyKeysException(this, keys);
      }

      //@formatter:off
      assert
              Objects.isNull( this.keyValidators )
           || ParameterArray.validateSizeAndElements( keys, 0, this.rank - 1, this.keyValidators )
         : "AbstractRankMap::remove, key set failed validation.";
      //@formatter:on

      //@formatter:off

      /*
       * 0 <= keyCount < rank
       */

      /*
       * Get the sub-map specified by the provided keys. Since the number of keys must be less than
       * the rank of the store the returned value will be a map or null.
       */

      @SuppressWarnings({"unchecked", "null"})
      var subMap = (Map<Object, Object>) this.getSubMapOrEntry(keys, keys.length);

      return Objects.nonNull( subMap ) ? subMap.keySet().stream() : Stream.empty();
   }

   /**
    * {@inheritDoc}
    *
    * @throws RankMapTooManyKeysException {@inheritDoc}
    */

   @Override
   public Stream<Object[]> streamKeySets(Object... keys) {
      return
         this.streamEntries( keys ).map( RankMap.Entry<V>::getKeyArray );

   }

}

/* EOF */
