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

package org.eclipse.osee.synchronization.rest.forest.morphology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * Provides a rank N {@link Store} implementation.
 *
 * @author Loren K. Ashley
 */

class StoreRankN implements Store {

   /**
    * The store's rank.
    */

   private final int rank;

   /**
    * Counter used to track the number of objects in the {@link Store}.
    *
    * @implNote Finding all the maps of maps of maps etc to get a collection of all the maps containing the values so
    * the number of values in each of those maps can be summed can be an expensive process. This counter is incremented
    * every time a new {@link GroveThing} is added to the store.
    */

   private int size;

   /**
    * Saves the primary map used to implement the store.
    */

   private final Map<Object, Object> primaryMap;

   /**
    * Saves the {@link Function} used to validate the primary map key when assertions are enabled.
    */

   private final Function<Object, Boolean>[] keyValidators;

   /**
    * Saves the {@link StoreType} which contains a {@link Function} used to extract the map keys from a
    * {@link GroveThing} for the store type.
    */

   private final StoreType storeType;

   /**
    * Creates a new empty store of the specified rank.
    *
    * @param storeType specifies if the store uses primary or native keys.
    * @param primaryKeyValidator the {@link Function} used to validate primary map keys.
    * @param secondaryKeyValidator the {@link Function} used to validate secondary map keys.
    * @throws NullPointerException when <code>storeType</code>, <code>primaryKeyValidator</code>, or
    * <code>secondaryKeyValidator</code> are <code>null</code>.
    */

   public StoreRankN(StoreType storeType, int rank, Function<Object, Boolean>[] keyValidators) {

      //@formatter:off
      assert
            Objects.nonNull( storeType )
         && ( rank >= 0 )
         && ParameterArray.validateNonNullAndSize( keyValidators, rank, rank );
      //@formatter:on

      this.storeType = Objects.requireNonNull(storeType);
      this.rank = rank;
      this.keyValidators = keyValidators;
      this.primaryMap = new HashMap<>();
      this.size = 0;
   }

   /**
    * Private method gets the sub-map for the provided keys. This method assumes that the <code>keys</code> array has
    * already been validated.
    *
    * @param keys array of keys to get the sub map for.
    * @return if there is an association with the provided keys, the sub-map or value associated with the keys;
    * otherwise, <code>null</code>.
    */

   private Object getSubMapOrValue(Object[] keys) {
      return this.getSubMapOrValue(keys, keys.length);
   }

   /**
    * Private method gets the sub-map for the specified number of the highest rank keys in the provided array. This
    * method assumes that the <code>keys</code> array has already been validated.
    *
    * @param keys array of keys to get the sub map for.
    * @param keyCount the number of keys in the <code>keys</code> array to use.
    * @return if there is an association with the provided keys, the sub-map or value associated with the keys;
    * otherwise, <code>null</code>.
    */

   @SuppressWarnings("unchecked")
   private Object getSubMapOrValue(Object[] keys, int keyCount) {
      var limit = keyCount < keys.length ? keyCount : keys.length;

      Object subMapOrValue = this.primaryMap;

      for (int i = 0; i < limit; i++) {
         subMapOrValue = ((Map<Object, Object>) subMapOrValue).get(keys[i]);

         if (Objects.isNull(subMapOrValue)) {
            return null;
         }
      }

      return subMapOrValue;

   }

   /**
    * {@inheritDoc}
    *
    * @throws DuplicateStoreEntryException {@inheritDoc}
    */

   @SuppressWarnings("unchecked")
   @Override
   public void add(GroveThing groveThing) {

      assert Objects.nonNull(groveThing);

      this.storeType.getKeys(groveThing).ifPresent(keys -> {

         //@formatter:off
         assert ParameterArray.validateNonNullSizeAndElements( keys, this.rank, this.rank, this.keyValidators );
         //@formatter:on

         var subMap = this.primaryMap;

         for (int i = 0; i < this.rank - 1; i++) {
            var parentMap = subMap;

            subMap = (Map<Object, Object>) subMap.get(keys[i]);

            if (Objects.isNull(subMap)) {
               subMap = new HashMap<Object, Object>();
               parentMap.put(keys[i], subMap);
            }
         }

         if (subMap.containsKey(keys[this.rank - 1])) {
            throw new DuplicateStoreEntryException(this, groveThing);
         }

         subMap.put(keys[this.rank - 1], groveThing);
         this.size++;

      });
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean contains(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateNonNullSizeAndElements( keys, 1, this.rank, this.keyValidators );
      //@formatter:on

      return Objects.nonNull(this.getSubMapOrValue(keys));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> get(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateNonNullSizeAndElements( keys, this.rank, this.rank, this.keyValidators );
      //@formatter:on

      return Optional.ofNullable((GroveThing) this.getSubMapOrValue(keys));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StoreType getType() {
      return this.storeType;
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

   @SuppressWarnings({"unchecked"})
   @Override
   public Stream<GroveThing> stream(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateSizeAndElements( keys, 0, this.rank, this.keyValidators );
      //@formatter:on

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      if (keyCount == this.rank) {
         return this.get(keys).map(value -> Stream.of(value)).orElseGet(Stream::empty);
      }

      // 0 < keyCount < rank

      var subMap = this.getSubMapOrValue(keys);

      if (Objects.isNull(subMap)) {
         return Stream.empty();
      }

      var stream = ((Map<Object, Object>) subMap).values().stream();

      for (int i = keyCount + 1; i < this.rank; i++) {
         stream = stream.flatMap((subMapLambda) -> ((Map<Object, Object>) subMapLambda).values().stream());
      }

      return (Stream<GroveThing>) (Object) stream;
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   @SuppressWarnings({"null", "unchecked"})
   public Stream<Object> streamKeysAtAndBelow(Object... keys) {

      assert ParameterArray.validateSizeAndElements(keys, 0, this.rank - 1, this.keyValidators);

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      if (keyCount >= this.rank) {

         /*
          * This exception can only be thrown when assertions are disabled.
          */

         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new StringBuilder( 1024 )
                             .append( "\n" )
                             .append( "To many keys provided to the method streamKeysDeep for the Store's rank." ).append( "\n" )
                             .append( "   Number of provided keys: " ).append( keys.length   ).append( "\n" )
                             .append( "   Rank of store:           " ).append( this.rank     ).append( "\n" )
                             .append( "   Maximum keys allowed:    " ).append( this.rank - 1 ).append( "\n" )
                             .toString()
                   );
         //@formatter:on
      }

      //@formatter:off

      /*
       * 0 <= keyCount < rank
       */

      /*
       * Array used to accumulate a list of the sub-maps at each level
       */

      List<Map<Object,Object>>[] levelListArray = new List[this.rank - keyCount];
      int l = 0;

      /*
       * Get the sub-map specified by the provided keys. Since the number of keys must be less than
       * the rank of the store the returned value will be a map or null.
       */

      var subMap = (Map<Object, Object>) this.getSubMapOrValue(keys);

      if( Objects.isNull( subMap ) )
      {
         return Stream.empty();
      }

      /*
       * Save a list with the selected sub-map
       */

      var subMapList = new ArrayList<Map<Object, Object>>();

      subMapList.add(subMap);

      levelListArray[l++] = subMapList;

      /*
       * Create a list of the all the sub-maps at each level below the selected sub-map
       */

      for (int i = keyCount + 1; i < this.rank; i++, l++)
      {
         levelListArray[l] =
            (List<Map<Object, Object>>) (Object) levelListArray[l-1].stream()
               .flatMap( map -> map.values().stream() )
               .collect( Collectors.toList() );
      }

      /*
       * Generate a stream of the keys from each sub-map in the levelListArray
       */

      return Arrays.stream(levelListArray)
                .flatMap(List::stream)
                .map(Map::keySet)
                .flatMap(Set::stream);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   @SuppressWarnings("null")
   public Stream<Object> streamKeysAt(Object... keys) {

      assert ParameterArray.validateSizeAndElements(keys, 0, this.rank - 1, this.keyValidators);

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      if (keyCount >= this.rank) {

         /*
          * This exception can only be thrown when assertions are disabled.
          */

         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new StringBuilder( 1024 )
                             .append( "\n" )
                             .append( "To many keys provided to the method streamKeysDeep for the Store's rank." ).append( "\n" )
                             .append( "   Number of provided keys: " ).append( keys.length   ).append( "\n" )
                             .append( "   Rank of store:           " ).append( this.rank     ).append( "\n" )
                             .append( "   Maximum keys allowed:    " ).append( this.rank - 1 ).append( "\n" )
                             .toString()
                   );
         //@formatter:on
      }

      //@formatter:off

      /*
       * 0 <= keyCount < rank
       */

      /*
       * Get the sub-map specified by the provided keys. Since the number of keys must be less than
       * the rank of the store the returned value will be a map or null.
       */

      @SuppressWarnings("unchecked")
      var subMap = (Map<Object, Object>) this.getSubMapOrValue(keys);

      return Objects.nonNull( subMap ) ? subMap.keySet().stream() : Stream.empty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<Object[]> streamKeySets(Object... keys) {

      //@formatter:off
      return
         this.stream( keys )
            .map    ( ( value ) -> this.storeType == StoreType.PRIMARY ? value.getPrimaryKeys() : value.getNativeKeys() )
            .filter ( Optional::isPresent )
            .map    ( Optional::get );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      //@formatter:off
      outMessage
         .append( indent0 ).append( this.getClass().getName() ).append( "\n" )
         .append( indent1 ).append( "Rank:         " ).append( this.rank()    ).append( "\n" )
         .append( indent1 ).append( "Store Type:   " ).append( this.storeType ).append( "\n" )
         .append( indent1 ).append( "Current Size: " ).append( this.size()    ).append( "\n" )
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * Provides a string message describing the {@link Store}.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */