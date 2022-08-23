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

package org.eclipse.osee.define.rest.synchronization.forest.morphology;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;
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
    * Creates a new empty store of the specified rank.
    *
    * @param storeType specifies if the store uses primary or native keys.
    * @param primaryKeyValidator the {@link Predicate} used to validate map keys.
    * @param secondaryKeyValidator the {@link Function} used to validate secondary map keys.
    * @throws NullPointerException when <code>storeType</code>, <code>primaryKeyValidator</code>, or
    * <code>secondaryKeyValidator</code> are <code>null</code>.
    */

   public StoreRankN(StoreType storeType, int rank, Predicate<Object>[] keyValidators) {

      //@formatter:off
      assert
            Objects.nonNull( storeType )
         && ( rank >= 0 )
         && ParameterArray.validateNonNullAndSize( keyValidators, rank, rank );
      //@formatter:on

      this.storeType = Objects.requireNonNull(storeType);
      this.primaryMap = new RankHashMap<>(null, rank, 1024, 0.75f, keyValidators);
   }

   /**
    * {@inheritDoc}
    *
    * @throws DuplicateStoreEntryException {@inheritDoc}
    */

   @Override
   public void add(GroveThing groveThing) {
      assert Objects.nonNull(groveThing);
      this.storeType.getKeys(groveThing).ifPresent(keys -> this.primaryMap.associate(groveThing, keys));
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