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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * Provides a rank 1 {@link Store} implementation using a {@link HashMap}.
 *
 * @author Loren K. Ashley
 */

class StoreRank1 implements Store {

   /**
    * The store's rank.
    */

   private static int rank = 1;

   /**
    * Saves the {@link Function} used to validate a map key when assertions are enabled.
    */

   private final Function<Object, Boolean>[] keyValidators;

   /**
    * Saves the map used to implement the store.
    */

   private final Map<Object, GroveThing> map;

   /**
    * Saves the {@link StoreType} which contains a {@link Function} used to extract the map key from a
    * {@link GroveThing} for the store type.
    */

   private final StoreType storeType;

   /**
    * Creates a new empty store of rank 1.
    *
    * @param storeType specifies if the store uses primary or native keys.
    * @param keyValidator the {@link Function} used to validate a map key when assertions are enabled.
    * @throws NullPointerException when <code>storeType</code> or <code>keyValidator</code> are <code>null</code>.
    */

   @SuppressWarnings("unchecked")
   public StoreRank1(StoreType storeType, Function<Object, Boolean> keyValidator) {
      this.storeType = Objects.requireNonNull(storeType);
      this.keyValidators = new Function[] {Objects.requireNonNull(keyValidator)};
      this.map = new HashMap<>();
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
         assert ParameterArray.validateNonNullSizeAndElements( keys, StoreRank1.rank, StoreRank1.rank, this.keyValidators );
         //@formatter:on

         if (this.map.containsKey(keys[0])) {
            throw new DuplicateStoreEntryException(this, groveThing);
         }

         this.map.put(keys[0], groveThing);
      });
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean contains(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateNonNullSizeAndElements( keys, StoreRank1.rank, StoreRank1.rank, this.keyValidators );
      //@formatter:on

      return this.map.containsKey(keys);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> get(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateNonNullSizeAndElements( keys, StoreRank1.rank, StoreRank1.rank, this.keyValidators );
      //@formatter:on

      return Optional.ofNullable(this.map.get(keys[0]));
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
      return StoreRank1.rank;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.map.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<GroveThing> streamDeep() {
      return this.map.values().stream();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<GroveThing> stream(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateSizeAndElements( keys, 0, StoreRank1.rank, this.keyValidators );
      //@formatter:on

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {
         case 0:
            return this.map.values().stream();

         case 1:
            @SuppressWarnings("null")
            var groveThing = this.map.get(keys[0]);
            if (Objects.isNull(groveThing)) {
               return Stream.empty();
            }
            return Stream.of(groveThing);

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

      assert ParameterArray.validateSizeAndElements(keys, 0, StoreRank1.rank, this.keyValidators);

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {

         case 0: {
            return this.map.keySet().stream();
         }

         case 1: {
            //@formatter:off
            return this.map.containsKey( keys[ 0 ] ) ? Stream.of( keys[ 0 ] ) : Stream.empty();
            //@formatter:on
         }

         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * {@inheritDoc}
    *
    * @implNote At rank 1 there is no functional difference between {@link #streamKeysDeep} and
    * {@link #streamKeysShallow}.
    */

   @Override
   public Stream<Object> streamKeysShallow(Object... keys) {

      return this.streamKeysDeep(keys);
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("null")
   @Override
   public Stream<Object[]> streamKeySetsDeep(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateSizeAndElements( keys, 0, StoreRank1.rank, this.keyValidators );
      //@formatter:on

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {
         case 0: {
            //@formatter:off
            return
               this.map.keySet().stream().map
                  (
                     ( key ) ->
                     {
                        var keyClass = key.getClass();
                        var keyArray = (Object[]) Array.newInstance( keyClass, 1 );
                        keyArray[ 0 ] = key;
                        return keyArray;
                     }
                  );
            //@formatter:on
         }

         case 1: {
            //@formatter:off
            if( this.map.containsKey( keys[ 0 ] ) )
            {
               var keyClass = keys[0].getClass();
               var keyArray = (Object[]) Array.newInstance( keyClass, 1 );
               keyArray[ 0 ] = keys[ 0 ];
               return Stream.<Object[]> of( keyArray );
            }
            else
            {
               return Stream.empty();
            }
            //@formatter:on
         }

         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * {@inheritDoc}
    *
    * @implNote At rank 1 there is no functional difference between {@link #streamKeysDeep} and
    * {@link #streamKeysShallow}.
    */

   @Override
   public Stream<Object[]> streamKeySetsShallow(Object... keys) {
      return this.streamKeySetsDeep(keys);
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