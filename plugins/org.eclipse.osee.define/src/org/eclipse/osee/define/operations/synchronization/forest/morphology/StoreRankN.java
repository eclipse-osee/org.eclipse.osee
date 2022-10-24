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

package org.eclipse.osee.define.operations.synchronization.forest.morphology;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ParameterArray;
import org.eclipse.osee.framework.jdk.core.util.RankHashMap;
import org.eclipse.osee.framework.jdk.core.util.RankMap;

/**
 * Provides a rank N {@link Store} implementation.
 *
 * @author Loren K. Ashley
 */

class StoreRankN implements Store {

   /**
    * Saves the primary map used to implement the store.
    */

   private final RankMap<GroveThing> primaryMap;

   /**
    * Saves the {@link StoreType} which contains a {@link Function} used to extract the map keys from a
    * {@link GroveThing} for the store type.
    */

   private final StoreType storeType;

   /**
    * When the {@link StoreType} is {@link StoreType#PRIMARY} the member saves a {@link Map} to provide the associations
    * between the lowest rank keys and the {@link GroveThing}s. <code>null</code> is used as a sentinel value to
    * indicate the {@link Store} does not support unique key mappings.
    */

   private final Map<Object, GroveThing> uniquePrimaryKeyMap;

   /**
    * When key validators are provided and the {@link StoreType} is {@link StoreType#PRIMARY} the key validator for the
    * lowest rank key (highest validator array index) will be saved.
    */

   private final Predicate<Object> uniqueKeyValidator;

   /**
    * Creates a new empty store of the specified type and rank. For {@link StoreType#PRIMARY} a map of
    * {@link GroveThing}s by the lowest rank key (highest key set array index) will also be maintained. For
    * {@link StoreType#PRIMARY} {@link Store}s the lowest rank key must uniquely identify the {@link GroveThing}.
    *
    * @param storeType specifies if the store uses primary or native keys.
    * @param keyValidators an array of {@link Predicate}s used to validate the map keys.
    * @throws NullPointerException when:
    * <ul>
    * <li>the <code>storeType</code> is <code>null</code>, or</li>
    * <li>the <code>keyValidators</code> array is <code>null</code>.
    * </ul>
    */

   public StoreRankN(StoreType storeType, int rank, Predicate<Object>[] keyValidators) {

      //@formatter:off
      assert
            Objects.nonNull( storeType )
         && ( storeType.equals( StoreType.NATIVE ) || storeType.equals( StoreType.PRIMARY ) )
         && ( rank >= 1 )
         && ParameterArray.validateNonNullAndSize( keyValidators, rank, rank );
      //@formatter:on

      this.storeType = Objects.requireNonNull(storeType);
      this.primaryMap = new RankHashMap<>(null, rank, 1024, 0.75f, keyValidators);
      if (StoreType.PRIMARY.equals(storeType) && (rank > 1)) {
         this.uniquePrimaryKeyMap = new HashMap<>(1024, 0.75f);
         this.uniqueKeyValidator = keyValidators[rank - 1];
      } else {
         this.uniquePrimaryKeyMap = null;
         this.uniqueKeyValidator = null;
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws DuplicateStoreEntryException {@inheritDoc}
    */

   @Override
   public void add(GroveThing groveThing) {
      assert Objects.nonNull(groveThing);

      //@formatter:off
      this.storeType.getKeys(groveThing).ifPresent
         (
            ( keys ) ->
            {
               this.primaryMap.associate(groveThing, keys);

               if (Objects.nonNull(this.uniquePrimaryKeyMap)) {
                  this.uniquePrimaryKeyMap.put(keys[keys.length - 1], groveThing);
               }
            }
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean contains(Object... keys) {
      return this.primaryMap.containsKeys(keys);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> get(Object... keys) {
      return this.primaryMap.get(keys);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> getByUniqueKey(Object uniqueKey) {

      if (Objects.isNull(this.uniquePrimaryKeyMap)) {
         return this.primaryMap.get(uniqueKey);
      }

      //@formatter:off
      assert
              Objects.nonNull( uniqueKey )
           && (    Objects.isNull( this.uniqueKeyValidator )
                || this.uniqueKeyValidator.test( uniqueKey ) )
         : "StoreRankN::getByUniqueKey, key failed validation.";
      //@formatter:on

      return Optional.ofNullable(this.uniquePrimaryKeyMap.get(uniqueKey));
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
      return this.primaryMap.rank();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.primaryMap.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<GroveThing> stream(Object... keys) {
      return this.primaryMap.stream(keys);
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public Stream<Object> streamKeysAtAndBelow(Object... keys) {
      return this.primaryMap.streamKeysAtAndBelow(keys);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<Object> streamKeysAt(Object... keys) {
      return this.primaryMap.streamKeysAt(keys);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<Object[]> streamKeySets(Object... keys) {

      //@formatter:off
      return
         this.primaryMap.stream( keys )
            .map    ( ( value ) -> this.storeType == StoreType.PRIMARY ? value.getPrimaryKeys() : value.getNativeKeys() )
            .filter ( Optional::isPresent )
            .map    ( Optional::get );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( this.getClass().getSimpleName() )
         .indentInc()
         .segment( "Rank",         this.rank()    )
         .segment( "Store Type",   this.storeType )
         .segment( "Current Size", this.size()    )
         .indentDec()
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
      return this.toMessage(0, (Message) null).toString();
   }

}

/* EOF */