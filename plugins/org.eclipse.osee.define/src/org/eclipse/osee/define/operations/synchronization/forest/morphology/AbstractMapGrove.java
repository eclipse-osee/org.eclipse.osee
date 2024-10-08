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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.forest.Grove;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThingNotFoundWithNativeKeysException;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * This class provides a basic map like implementation of the {@link Grove} interface, to minimize the effort required
 * to implement this interface for specific Synchronization Artifact things.
 *
 * @author Loren K. Ashley
 */

public class AbstractMapGrove implements Grove {

   /**
    * The {@link IdentifierType} the grove is associated with.
    */

   protected final IdentifierType identifierType;

   /**
    * A {@link Store} (map) of {@link GroveThing}s using a unique identifier or identifiers from the native OSEE object
    * saved in the {@link GroveThing} as keys.
    */

   protected final Store nativeStore;

   /**
    * A {@link Store} (map) of {@link GroveThing}s using the {@link GroveThing} and it's parent's Synchronization
    * Artifact {@link Identifier}s as keys.
    */

   protected final Store primaryStore;

   /**
    * Creates a new empty grove. The rank of the primary store is determined from the length of the
    * <code>primaryKeyValidators</code> array. The rank of the native store is determined from the length of the
    * <code>nativeKeyValidators</code> array. Primary store rank can be from {@link #minPrimaryRank} to
    * {@link #maxPrimaryRank} and native store rank can be from {@link #minNativeRank} to {@link #maxNativeRank}. The
    * <code>primaryKeyValidators</code> and <code>nativeKeyValidators</code> arrays are used to validate keys when
    * assertions are enabled. The index position of the {@link Predicate} in the <code>primaryKeyValidators</code> or
    * <code>nativeKeyValidators</code> array corresponds with the rank of the key it is used to validate.
    *
    * @param identifierType the {@link IdentifierType} the {@link Grove} is associated with.
    * @param groveThingProvidesNativeKeys set to <code>true</code> when the {@link GroveThing}s stored in the
    * {@link Grove} can provide native keys.
    * @param primaryKeyValidators an array of {@link Predicate}s used to validate the primary keys.
    * @param nativeKeyValidators an array of {@link Predicate}s used to validate the native keys.
    * @throws NullPointerException when the parameter <code>identifierType</code> is <code>null</code>.
    */

   public AbstractMapGrove(IdentifierType identifierType, boolean groveThingProvidesNativeKeys, Predicate<Object>[] primaryKeyValidators, Predicate<Object>[] nativeKeyValidators) {

      //@formatter:off
      this.identifierType = identifierType;
      this.primaryStore   = this.createPrimaryStorage
                               (
                                 identifierType == IdentifierType.SPEC_OBJECT ? StoreType.PRIMARY_HIERARCHY : StoreType.PRIMARY,
                                 primaryKeyValidators
                               );
      this.nativeStore    = groveThingProvidesNativeKeys
                               ? this.createNativeStorage( nativeKeyValidators )
                               : null;
      //@formatter:off
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws DuplicateGroveEntryException {@inheritDoc}
    */

   @Override
   public GroveThing add(GroveThing groveThing) {

      try {
         this.primaryStore.add(groveThing);

         if (Objects.nonNull(this.nativeStore)) {
            this.nativeStore.add(groveThing);
         }

         return groveThing;
      } catch (DuplicateStoreEntryException e) {
         throw new DuplicateGroveEntryException(this, e);
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsByPrimaryKeys(Identifier... primaryKeys) {
      return this.primaryStore.contains((Object[]) primaryKeys);
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalStateException {@inheritDoc}
    */

   @Override
   public boolean containsByNativeKeys(Object... nativeKeys) {

      if (Objects.isNull(this.nativeStore)) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      new Message()
                             .title( "AbstractMapGrove::getByNativeKeys, Grove was not created with support for native keys." )
                             .indentInc()
                             .segment( "Grove Identifier Type", this.identifierType )
                             .toString()
                   );
         //@formatter:on
      }

      return this.nativeStore.contains(nativeKeys);
   }

   /**
    * Builds an array of key validator functions for the native store. The generated functions check the following:
    * <ul>
    * <li>The key is non-null.</li>
    * <li>The key is an instance of the class specified in the index of the <code>nativeKeyTypes</code> array that
    * corresponds to the rank index of the key.</li>
    * </ul>
    *
    * @param identifierTypes an array of {@link IdentifierType} arrays. Each sub-array contains the
    * {@link IdentifierType}s that are acceptable for a key with the same rank index as the index of the sub-array.
    * @return a {@link Store} implementation for the grove's primary store.
    */

   private Store createNativeStorage(Predicate<Object>[] keyValidators) {

      var keyCount = Objects.nonNull(keyValidators) ? keyValidators.length : 0;

      return keyCount > 0 ? new StoreRankN(StoreType.NATIVE, keyCount, keyValidators) : null;
   }

   /**
    * Builds an array of key validator functions for the primary store. The generated functions check the following:
    * <ul>
    * <li>The key is non-null.</li>
    * <li>The key is an instance of {@link Identifier}.</li>
    * <li>The key has a type that is one of the {@link IdentifierType}s specified in the index of the
    * <code>identifierTypes</code> array that corresponds to the rank index of the key.</li>
    * </ul>
    *
    * @param identifierTypes an array of {@link IdentifierType} arrays. Each sub-array contains the
    * {@link IdentifierType}s that are acceptable for a key with the same rank index as the index of the sub-array.
    * @return a {@link Store} implementation for the grove's primary store.
    */

   @SuppressWarnings("null")
   private Store createPrimaryStorage(StoreType storeType, Predicate<Object>[] keyValidators) {
      //@formatter:off
      var keyCount = Objects.nonNull(keyValidators) ? keyValidators.length : 0;

      return storeType == StoreType.PRIMARY_HIERARCHY
                ? new StoreRank3( StoreType.PRIMARY_HIERARCHY, keyValidators[0], keyValidators[1], keyValidators[2] )
                : new StoreRankN( StoreType.PRIMARY, keyCount, keyValidators );
      //@formatter:off
   }


   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> getByPrimaryKeys(Identifier... primaryKeys) {

      return this.primaryStore.get((Object[]) primaryKeys);
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalStateException {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> getByNativeKeys(Object... nativeKeys) {

      if( Objects.isNull( this.nativeStore ) ) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      new Message()
                             .title( "AbstractMapGrove::getByNativeKeys, Grove was not created with support for native keys." )
                             .indentInc()
                             .segment( "Grove Identifier Type", this.identifierType )
                             .toString()
                   );
         //@formatter:on
      }

      return this.nativeStore.get(nativeKeys);
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalStateException {@inheritDoc}
    * @throws GroveThingNotFoundWithNativeKeysException {@inheritDoc}
    */

   @Override
   public GroveThing getByNativeKeysOrElseThrow(Object... nativeKeys) {

      if (Objects.isNull(this.nativeStore)) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      new Message()
                             .title( "AbstractMapGrove::getByNativeKeys, Grove was not created with support for native keys." )
                             .indentInc()
                             .segment( "Grove Identifier Type", this.identifierType )
                             .toString()
                   );
         //@formatter:on
      }

      //@formatter:off
      return
         this.nativeStore
            .get( nativeKeys )
            .orElseThrow( () -> new GroveThingNotFoundWithNativeKeysException( this, nativeKeys ) );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> getByUniquePrimaryKey(Object uniquePrimaryKey) {
      return this.primaryStore.getByUniqueKey(uniquePrimaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IdentifierType getType() {
      assert (this.identifierType != null);
      return this.identifierType;
   }

   /**
    * {@inheritDOc}
    */

   @Override
   public int nativeRank() {
      return Objects.nonNull(this.nativeStore) ? this.nativeStore.rank() : 0;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int rank() {
      return this.primaryStore.rank();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.primaryStore.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<GroveThing> stream(Identifier... primaryKeys) {
      return this.primaryStore.stream((Object[]) primaryKeys);
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("unchecked")
   @Override
   public Stream<Identifier> streamIdentifiersDeep(Identifier... primaryKeys) {
      return (Stream<Identifier>) (Object) this.primaryStore.streamKeysAtAndBelow((Object[]) primaryKeys);
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("unchecked")
   @Override
   public Stream<Identifier> streamIdentifiersShallow(Identifier... primaryKeys) {
      return (Stream<Identifier>) (Object) this.primaryStore.streamKeysAt((Object[]) primaryKeys);
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("unchecked")
   @Override
   public Stream<Identifier[]> streamKeySets(Identifier... primaryKeys) {
      return (Stream<Identifier[]>) (Object) this.primaryStore.streamKeySets((Object[]) primaryKeys);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Grove" )
         .indentInc()
         .segment( "Type", this.getType() )
         .toMessage( this.primaryStore )
         .toMessage( this.nativeStore )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */
