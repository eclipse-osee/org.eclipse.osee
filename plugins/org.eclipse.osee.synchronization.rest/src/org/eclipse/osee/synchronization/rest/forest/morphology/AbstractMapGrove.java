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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.IndentedString;

/**
 * This class provides a basic map like implementation of the {@link Grove} interface, to minimize the effort required
 * to implement this interface for specific Synchronization Artifact things.
 *
 * @author Loren K. Ashley
 */

public class AbstractMapGrove implements Grove {

   /**
    * Assertion guard rail for the minimum rank of a grove's primary store.
    */

   private static int minPrimaryRank = 1;

   /**
    * Assertion guard rail for the maximum rank of a grove's primary store.
    */

   private static int maxPrimaryRank = 3;

   /**
    * Assertion guard rail for the minimum rank of a grove's native store.
    */

   private static int minNativeRank = 1;

   /**
    * Assertion guard rail for the maximum rank of a grove's native store.
    */

   private static int maxNativeRank = 3;

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

   public AbstractMapGrove(IdentifierType identifierType, IdentifierType[][] identifierTypes, Class<?>[] nativeKeyTypes) {

      this.identifierType = identifierType;
      this.primaryStore = this.createPrimaryStorage(identifierTypes);
      this.nativeStore = this.createNativeStorage(nativeKeyTypes);
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

   private Store createNativeStorage(Class<?>[] nativeKeyTypes) {

      if (Objects.isNull(nativeKeyTypes)) {
         return null;
      }

      int nativeRank = nativeKeyTypes.length;

      if ((nativeRank < AbstractMapGrove.minNativeRank) || (nativeRank > AbstractMapGrove.maxNativeRank)) {
         throw new IllegalArgumentException();
      }

      @SuppressWarnings("unchecked")
      Function<Object, Boolean>[] nativeKeyValidators = new Function[nativeRank];

      for (int i = 0; i < nativeRank; i++) {
         var keyClassForRank = nativeKeyTypes[i];
         nativeKeyValidators[i] = new Function<Object, Boolean>() {
            Class<?> keyClass = keyClassForRank;

            @Override
            public Boolean apply(Object key) {

               //@formatter:off
                  return
                     Objects.nonNull( key )
                     && keyClass.isInstance( key );
                  //@formatter:on
            }
         };
      }

      return Store.create(StoreType.NATIVE, nativeKeyValidators);
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

   private Store createPrimaryStorage(IdentifierType[][] identifierTypes) {
      Objects.requireNonNull(identifierTypes);

      int rank = identifierTypes.length;

      if ((rank < AbstractMapGrove.minPrimaryRank) || (rank > AbstractMapGrove.maxPrimaryRank)) {
         throw new IllegalArgumentException();
      }

      @SuppressWarnings("unchecked")
      Function<Object, Boolean>[] primaryKeyValidators = new Function[rank];

      for (int i = 0; i < rank; i++) {
         var identifierTypesForRank = identifierTypes[i];

         if (identifierTypesForRank.length == 1) {

            primaryKeyValidators[i] = new Function<Object, Boolean>() {

               IdentifierType identifierType = identifierTypesForRank[0];

               @Override
               public Boolean apply(Object key) {
               //@formatter:off
                  return
                     Objects.nonNull( key )
                     && ( key instanceof Identifier )
                     && ((Identifier)key).getType().equals( identifierType );
                  //@formatter:on
               }
            };

         } else {

            primaryKeyValidators[i] = new Function<Object, Boolean>() {

               IdentifierType[] identifierTypes = identifierTypesForRank;

               @Override
               public Boolean apply(Object key) {

                  if (Objects.isNull(key) || !(key instanceof Identifier)) {
                     return false;
                  }

                  var keyType = ((Identifier) key).getType();

                  for (int i = 0; i < identifierTypes.length; i++) {
                     if (keyType.equals(identifierTypes[i])) {
                        return true;
                     }
                  }

                  return false;
               }
            };

         }
      }

      //@formatter:off
      return
         Store.create
            (
              this.identifierType.equals(IdentifierType.SPEC_OBJECT) ? StoreType.PRIMARY_HIERARCHY : StoreType.PRIMARY,
              primaryKeyValidators
            );
      //@ofrmatter:on
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

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .append( indent0 ).append( name ).append( ":" ).append( "\n" )
         .append( indent1 ).append( "Primary Store:" ).append( "\n" )
         ;
      //@formatter:on

      this.primaryStore.toMessage(indent + 2, outMessage);

      //@formatter:off
      outMessage
         .append( indent1 ).append( "Native Store:" ).append( "\n" )
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
