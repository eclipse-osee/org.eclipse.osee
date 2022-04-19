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

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.osee.framework.jdk.core.util.DoubleHashMap;
import org.eclipse.osee.framework.jdk.core.util.DoubleMap;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * Provides a rank 2 {@link Store} implementation using a {@link DoubleHashMap}.
 *
 * @author Loren K. Ashley
 */

class StoreRank2 implements Store {

   /**
    * The store's rank.
    */

   private static int rank = 2;

   /**
    * Saves the double map used to implement the store.
    */

   private final DoubleMap<Object, Object, GroveThing> doubleMap;

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
    * Creates a new empty store of rank 2.
    *
    * @param storeType specifies if the store uses primary or native keys.
    * @param primaryKeyValidator the {@link Function} used to validate primary map keys.
    * @param secondaryKeyValidator the {@link Function} used to validate secondary map keys.
    * @throws NullPointerException when <code>storeType</code>, <code>primaryKeyValidator</code>, or
    * <code>secondaryKeyValidator</code> are <code>null</code>.
    */

   @SuppressWarnings("unchecked")
   public StoreRank2(StoreType storeType, Function<Object, Boolean> primaryKeyValidator, Function<Object, Boolean> secondaryKeyValidator) {
      this.storeType = Objects.requireNonNull(storeType);
      //@formatter:off
      this.keyValidators = new Function[] {
         Objects.requireNonNull(primaryKeyValidator),
         Objects.requireNonNull(secondaryKeyValidator)};
      //@formatter:on
      this.doubleMap = new DoubleHashMap<>();
   }

   /**
    * {@inheritDoc}
    *
    * @throws DuplicateStoreEntryException {@inheritDoc}
    */

   @Override
   public void add(GroveThing groveThing) {

      assert Objects.nonNull(groveThing);

      this.storeType.getKeys(groveThing).ifPresent(keys -> {

         //@formatter:off
         assert ParameterArray.validateNonNullSizeAndElements( keys, StoreRank2.rank, StoreRank2.rank, this.keyValidators );
         //@formatter:on

         if (this.doubleMap.containsKey(keys[0], keys[1])) {
            throw new DuplicateStoreEntryException(this, groveThing);
         }

         this.doubleMap.put(keys[0], keys[1], groveThing);
      });
   }

   /**
    * {@inheritDoc}
    * <p>
    * <dl>
    * <dt>Key Count 1:</dt>
    * <dd>The {@link Store}'s {@link DoubleMap} is checked for the presence of an association for the provided primary
    * key.</dd>
    * <dt>Key Count 2:</dt>
    * <dd>The {@link Store}'s {@link DoubleMap} is checked for the presence of an association with the provided primary
    * and secondary keys.</dd>
    * </dl>
    */

   @SuppressWarnings("null")
   @Override
   public boolean contains(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateNonNullSizeAndElements( keys, 1, StoreRank2.rank, this.keyValidators );
      //@formatter:on

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {

         case 1: {
            return this.doubleMap.containsKey(keys[0]);
         }

         case 2: {
            return this.doubleMap.containsKey(keys[0], keys[1]);
         }

         default:
            throw new IllegalArgumentException();

      }

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> get(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateNonNullSizeAndElements( keys, StoreRank2.rank, StoreRank2.rank, this.keyValidators );
      //@formatter:on

      return this.doubleMap.get(keys[0], keys[1]);
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
      return StoreRank2.rank;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.doubleMap.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<GroveThing> streamDeep() {
      return this.doubleMap.values().stream();
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("null")
   @Override
   public Stream<GroveThing> stream(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateSizeAndElements( keys, 0, StoreRank2.rank, this.keyValidators );
      //@formatter:on

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {
         case 0:
            return this.doubleMap.values().stream();

         case 1:
            return this.doubleMap.get(keys[0]).map(map -> map.values().stream()).orElseGet(Stream::empty);

         case 2:
            return this.doubleMap.get(keys[0], keys[1]).map(value -> Stream.of(value)).orElseGet(Stream::empty);

         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   @SuppressWarnings("null")
   public Stream<Object> streamKeysDeep(Object... keys) {

      assert ParameterArray.validateSizeAndElements(keys, 0, StoreRank2.rank, this.keyValidators);

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {

         case 0: {
            //@formatter:off
            return
               Stream.concat
                  (
                     this.doubleMap.keySet().stream(),
                     this.doubleMap.keySet().stream().flatMap( key -> this.doubleMap.keySet( key ).map( Set::stream ).orElseGet( Stream::empty ) )
                  );
            //@formatter:on
         }

         case 1: {
            //@formatter:off
            return this.doubleMap.keySet( keys[ 0 ] ).map( Set::stream ).orElseGet( Stream::empty );
            //@formatter:on
         }

         case 2: {
            //@formatter:off
            return this.doubleMap.get( keys[ 0 ], keys[ 1 ] ).map( value -> Stream.of( keys[ 1 ] ) ).orElseGet( Stream::empty );
            //@formatter:on
         }

         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   @SuppressWarnings("null")
   public Stream<Object> streamKeysShallow(Object... keys) {

      assert ParameterArray.validateSizeAndElements(keys, 0, StoreRank2.rank, this.keyValidators);

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {

         case 0: {
            return this.doubleMap.keySet().stream();
         }

         case 1: {
            //@formatter:off
            return this.doubleMap.keySet( keys[ 0 ] ).map( Set::stream ).orElseGet( Stream::empty );
            //@formatter:on
         }

         case 2: {
            //@formatter:off
            return this.doubleMap.get( keys[ 0 ], keys[ 1 ] ).map( value -> Stream.of( keys[ 1 ] ) ).orElseGet( Stream::empty );
            //@formatter:on
         }

         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("null")
   @Override
   public Stream<Object[]> streamKeySetsDeep(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateSizeAndElements( keys, 0, StoreRank2.rank, this.keyValidators );
      //@formatter:on

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {
         case 0: {
            //@formatter:off
            return
               this.doubleMap.keySet().stream().flatMap
                  (
                     ( primaryKey ) ->
                        this.doubleMap.keySet( primaryKey ).get().stream().map
                           (
                              ( secondaryKey ) ->
                              {
                                 var keyClass = primaryKey.getClass();
                                 var keyArray = (Object[]) Array.newInstance( keyClass, 2 );
                                 keyArray[ 0 ] = primaryKey;
                                 keyArray[ 1 ] = secondaryKey;
                                 return keyArray;
                              }
                           )
                  );
            //@formatter:on
         }

         case 1: {
            //@formatter:off
            return
               this.doubleMap.keySet(keys[0]).map
                  (
                     ( keySet ) ->
                        keySet.stream().map
                           (
                              ( secondaryKey ) ->
                              {
                                 var keyClass = keys[0].getClass();
                                 var keyArray = (Object[]) Array.newInstance( keyClass, 2 );
                                 keyArray[ 0 ] = keys[ 0 ];
                                 keyArray[ 1 ] = secondaryKey;
                                 return keyArray;
                              }
                           )
                  ).orElseGet( Stream::empty );
            //@formatter:on
         }

         case 2: {
            //@formatter:off
            return
               this.doubleMap.get(keys[0], keys[1]).map
                  (
                     ( value ) ->
                     {
                        var keyClass = keys[0].getClass();
                        var keyArray = (Object[]) Array.newInstance( keyClass, 2 );
                        keyArray[ 0 ] = keys[ 0 ];
                        keyArray[ 1 ] = keys[ 1 ];
                        return Stream.<Object[]> of( keyArray );
                     }
                  ).orElseGet(Stream::empty);
            //@formatter:on
         }

         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("null")
   @Override
   public Stream<Object[]> streamKeySetsShallow(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateSizeAndElements( keys, 0, StoreRank2.rank, this.keyValidators );
      //@formatter:on

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {
         case 0: {
            //@formatter:off
            return
               this.doubleMap.keySet().stream().map
                  (
                     ( primaryKey ) ->
                     {
                        var keyClass = primaryKey.getClass();
                        var keyArray = (Object[]) Array.newInstance( keyClass, 1 );
                        keyArray[ 0 ] = primaryKey;
                        return keyArray;
                      }
                  );
            //@formatter:on
         }

         case 1: {
            //@formatter:off
            return
               this.doubleMap.keySet(keys[0]).map
                  (
                     ( keySet ) ->
                        keySet.stream().map
                           (
                              ( secondaryKey ) ->
                              {
                                 var keyClass = keys[0].getClass();
                                 var keyArray = (Object[]) Array.newInstance( keyClass, 2 );
                                 keyArray[ 0 ] = keys[ 0 ];
                                 keyArray[ 1 ] = secondaryKey;
                                 return keyArray;
                              }
                           )
                  ).orElseGet( Stream::empty );
            //@formatter:on
         }

         case 2: {
            //@formatter:off
            return
               this.doubleMap.get(keys[0], keys[1]).map
                  (
                     ( value ) ->
                     {
                        var keyClass = keys[0].getClass();
                        var keyArray = (Object[]) Array.newInstance( keyClass, 2 );
                        keyArray[ 0 ] = keys[ 0 ];
                        keyArray[ 1 ] = keys[ 1 ];
                        return Stream.<Object[]> of( keyArray );
                     }
                  ).orElseGet(Stream::empty);
            //@formatter:on
         }

         default:
            throw new IllegalArgumentException();
      }
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