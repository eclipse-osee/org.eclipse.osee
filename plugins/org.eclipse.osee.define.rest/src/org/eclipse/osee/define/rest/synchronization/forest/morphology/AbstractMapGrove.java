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
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.osee.define.rest.synchronization.IdentifierType;
import org.eclipse.osee.define.rest.synchronization.IdentifierType.Identifier;
import org.eclipse.osee.define.rest.synchronization.forest.Grove;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThingNotFoundWithNativeKeysException;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;

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
    * <code>identifierTypes</code> array. The rank of the native store is determined from the length of the
    * <code>nativeKeyTypes</code> array. Primary store rank can be from {@link #minPrimaryRank} to
    * {@link #maxPrimaryRank} and native store rank can be from {@link #minNativeRank} to {@link #maxNativeRank}. The
    * <code>identifierType</code> and <code>nativeKeyTypes</code> arrays are used to validate keys when assertions are
    * enabled. Primary keys are all {@link Identifier} objects. Each {@link Identifier} has a type specified with a
    * member of the {@link IdentifierType} enumeration. The <code>identifierTypes</code> array is an array of arrays
    * with an element for each rank of the primary store. Each array element of the <code>identifierTypes</code> array
    * contains the {@link IdentifierType}s that are acceptable for keys which match the same rank as the array element.
    * For each rank of native key only one class type is accepted. The <code>nativeKeyTypes</code> array contains the
    * acceptable class for native keys which match the same rank as the array element.
    *
    * @param identifierType the {@link IdentifierType} the {@link Grove} is associated with.
    * @param identifierTypes the expected {@link IdentifierType}s of keys for the grove's {@link Store} using primary
    * keys.
    * @param nativeKeyTYpes the expected key types for the grove's {@link Store} using native keys.
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
    */

   @Override
   public boolean containsByNativeKeys(Object... nativeKeys) {
      return Objects.nonNull(this.nativeStore) ? this.nativeStore.contains(nativeKeys) : false;
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
    */

   @Override
   public Optional<GroveThing> getByNativeKeys(Object... nativeKeys) {

      return this.nativeStore.get(nativeKeys);
   }

   /**
    * {@inheritDoc}
    *
    * @throws GroveThingNotFoundWithNativeKeysException {@inheritDoc}
    */

   @Override
   public GroveThing getByNativeKeysOrElseThrow(Object... nativeKeys) {

      return this.nativeStore.get(nativeKeys).orElseThrow( () -> new GroveThingNotFoundWithNativeKeysException( this, nativeKeys ) );
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
      return this.nativeStore.rank();
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
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      //@formatter:off
      outMessage
         .append( indent0 ).append( "Grove:" ).append( "\n" )
         .append( indent1 ).append( "Type:          " ).append( this.getType() ).append( "\n" )
         .append( indent1 ).append( "Primary Store: " ).append( "\n" )
         ;
      //@formatter:on

      this.primaryStore.toMessage(indent + 2, outMessage);

      //@formatter:off
      outMessage
         .append( indent1 ).append( "Native Store:  " ).append( "\n" )
         ;
      //@formatter:on

      this.nativeStore.toMessage(indent + 2, outMessage);

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
