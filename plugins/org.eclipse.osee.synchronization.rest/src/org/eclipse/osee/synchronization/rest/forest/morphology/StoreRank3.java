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
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.HierarchyTree;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * Provides a rank 3 {@link Store} implementation for primary stores only using a {@link Map} of {@link HierarchyTree}s.
 * This implementation uses the keys as follows:
 * <dl>
 * <dt>Primary Key</dt>
 * <dd>selects a {@link HierarchyTree}.</dd>
 * <dt>Secondary Key</dt>
 * <dd>a unique identifier for the hierarchical parent of the {@link GroveThing} identified by the tertiary key.</dd>
 * <dt>Tertiary Key</dt>
 * <dd>a unique identifier for the {@link GroveThing} selected by the keys.</dd>
 * </ul>
 * When requesting a {@link GroveThing} from the {@link Store} the tertiary key is sufficient to identify the
 * {@link GroveThing}. The {@link Store} will not return the {@link GroveThing} or indicate it's presence if the primary
 * key does not match the hierarchical tree the {@link GroveThing} is in and if the secondary key does not match the
 * hierarchical parent of the {@link GroveThing}.
 *
 * @author Loren K. Ashley
 */

class StoreRank3 implements Store {

   /**
    * The store's rank.
    */

   private static int rank = 3;

   /**
    * A {@link Map} of {@link HierarchyTree}s used to store the {@link GroveThing}s.
    */

   protected final Map<Identifier, HierarchyTree<Identifier, GroveThing>> hierarchyTrees;

   /**
    * Saves the {@link Function} used to validate the primary map key when assertions are enabled.
    */

   private final Function<Object, Boolean>[] keyValidators;

   /**
    * A set of the {@link Identifier}s for each {@link GroveThing} added to the {@link Store}. This set is used to
    * detect when a duplicate {@link GroveThing} is being added to the {@link Store}. This is used to prevent a
    * {@link GroveThing} from being added to more than one of the hierarchy trees.
    */

   private final Set<Identifier> quickDuplicateSet;

   /**
    * Saves the {@link StoreType} which contains a {@link Function} used to extract the map keys from a
    * {@link GroveThing} for the store type.
    */

   private final StoreType storeType;

   /**
    * Creates a new empty store of rank 3.
    *
    * @param storeType specifies if the store uses primary or native keys. Only primary stores keys are supported by
    * this implementation.
    * @param primaryKeyValidator the {@link Function} used to validate the primary map key when assertions are enabled.
    * @param secondaryKeyValidator the {@link Function} used to validate the secondary map key when assertions are
    * enabled.
    * @param tertiaryKeyValidator the {@link Function} used to validate the tertiary map key when assertions are
    * enabled.
    */

   @SuppressWarnings("unchecked")
   public StoreRank3(StoreType storeType, Function<Object, Boolean> primaryKeyValidator, Function<Object, Boolean> secondaryKeyValidator, Function<Object, Boolean> tertiaryKeyValidator) {
      this.storeType = storeType;
      this.keyValidators = new Function[] {
         Objects.requireNonNull(primaryKeyValidator),
         Objects.requireNonNull(secondaryKeyValidator),
         Objects.requireNonNull(tertiaryKeyValidator)};
      this.hierarchyTrees = new HashMap<>();
      this.quickDuplicateSet = new HashSet<>();
   }

   /**
    * {@inheritDoc}
    * <p>
    * When the {@link GroveThing} is a {@link HierarchyRootGroveThing} the {@link GroveThing} {@link Identifier} is used
    * as the primary key. If a {@link HierarchyTree} is already associated with the primary key a
    * {@link DuplicateStoreEntryException} is thrown; otherwise, a new {@link HierarchyTree} is created and associated
    * with the primary key. The {@link GroveThing} is also added to the new {@link HierarchyTree} using the primary key.
    * <p>
    * When the {@link GroveThing} is not a {@link HierarchyRootGroveThing} the {@link GroveThing} is expected to have a
    * rank of 3. If the {@link Store} already contains an entry with the tertiary key from the {@link GroveThing} a
    * {@link DuplicateStoreEntryException} is thrown. If the hierarchical parent specified by the secondary key is not
    * present in the {@link HierarchyTree} selected by the primary key a {@link RuntimeException} is thrown. Otherwise,
    * the {@link GroveThing} is added to the selected {@link HierarchyTree} as the last hierarchical child of the
    * {@link GroveThing} specified by the secondary key.
    */

   @Override
   public void add(GroveThing groveThing) {

      assert Objects.nonNull(groveThing);

      if (groveThing instanceof HierarchyRootGroveThing) {

         ((HierarchyRootGroveThing) groveThing).validate();

         var key = groveThing.getIdentifier();

         if (this.quickDuplicateSet.contains(key)) {
            throw new DuplicateStoreEntryException(this, groveThing);
         }

         var hierarchyTree = new HierarchyTree<Identifier, GroveThing>();

         hierarchyTree.setRoot(key, groveThing);

         this.hierarchyTrees.put(key, hierarchyTree);
         this.quickDuplicateSet.add(key);

      } else {
         groveThing.getPrimaryKeys().ifPresent(keys -> {

            //@formatter:off
            assert ParameterArray.validateNonNullSizeAndElements( keys, StoreRank3.rank, StoreRank3.rank, this.keyValidators );
            //@formatter:on

            if (this.quickDuplicateSet.contains(keys[2])) {
               throw new DuplicateStoreEntryException(this, groveThing);
            }

            var hierarchyTree = this.hierarchyTrees.get(keys[0]);

            if (Objects.isNull(hierarchyTree)) {
               hierarchyTree = new HierarchyTree<>();

               this.hierarchyTrees.put((Identifier) keys[0], hierarchyTree);
            }

            hierarchyTree.insertLast((Identifier) keys[1], (Identifier) keys[2], groveThing);
            this.quickDuplicateSet.add((Identifier) keys[2]);
         });
      }
   }

   /**
    * {@inheritDoc}
    * <p>
    * <dl>
    * <dt>Key Count 1:</dt>
    * <dd>The {@link Store} is checked for the presence of an {@link HierarchyTree} associated with the provided
    * key.</dd>
    * <dt>Key Count 2:</dt>
    * <dd>The {@link Store} is checked for the presence of an association for the provided secondary key in the
    * {@link HierarchyTree} specified by the primary key.</dd>
    * <dt>Key Count 3:</dt>
    * <dd>The {@link Store} is checked for the presence of a {@link GroveThing} with the {@link Identifier} specified by
    * the tertiary key that is also a child of a {@link GroveThing} with the {@link Identifier} specified by the
    * secondary key in a {@link HierarchyTree} specified by the primary key.</dd>
    * </dl>
    */

   @SuppressWarnings("null")
   @Override
   public boolean contains(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateNonNullSizeAndElements( keys, 1, StoreRank3.rank, this.keyValidators );
      //@formatter:on

      var hierarchyTree = this.hierarchyTrees.get(keys[0]);

      if (Objects.isNull(hierarchyTree)) {
         return false;
      }

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {

         case 2: {
            return hierarchyTree.containsKey((Identifier) keys[1]);
         }

         case 3: {
            return hierarchyTree.containsKey((Identifier) keys[1], (Identifier) keys[2]);
         }

         default:
            throw new IllegalArgumentException();

      }
   }

   /**
    * {@inheritDoc}
    * <p>
    * <dl>
    * <dt>Key Count 2:</dt>
    * <dd>The primary key is used to select a {@link HierarchyTree}. The {@link GroveThing} associated with the
    * secondary key will be returned.</dd>
    * <dt>Key Count 3:</dt>
    * <dd>The primary key is used to select a {@link HierarchyTree}. The {@link GroveThing} associated with the tertiary
    * key will be returned only if it's hierarchical parent is the {@link GroveThing} specified by the secondary
    * key.</dd>
    * </dl>
    */

   @SuppressWarnings("null")
   @Override
   public Optional<GroveThing> get(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateNonNullSizeAndElements( keys, 2, StoreRank3.rank, this.keyValidators );
      //@formatter:on

      var hierarchyTree = this.hierarchyTrees.get(keys[0]);

      if (Objects.isNull(hierarchyTree)) {
         return null;
      }

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {
         case 2: {
            return Optional.ofNullable(hierarchyTree.get((Identifier) keys[1]));

         }

         case 3: {
            return hierarchyTree.get((Identifier) keys[1], (Identifier) keys[2]);

         }
         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * {@inheritDoc}
    * <p>
    * The {@link StoreType} for a {@link StoreRank3} can only be {@link StoreType#PRIMARY}.
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
      return StoreRank3.rank;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.hierarchyTrees.values().stream().collect(Collectors.summingInt(HierarchyTree::size));
   }

   /**
    * {@inheritDoc}
    */

   @SuppressWarnings("null")
   @Override
   public Stream<GroveThing> stream(Object... keys) {

      //@formatter:off
      assert ParameterArray.validateSizeAndElements( keys, 0, StoreRank3.rank, this.keyValidators );
      //@formatter:on

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {
         case 0: {
            return this.hierarchyTrees.values().stream().flatMap(HierarchyTree::streamValuesDeep);
         }

         case 1: {
            var hierarchyTree = this.hierarchyTrees.get(keys[0]);
            return Objects.nonNull(hierarchyTree) ? hierarchyTree.streamValuesDeep() : Stream.empty();
         }

         case 2: {
            var hierarchyTree = this.hierarchyTrees.get(keys[0]);
            return Objects.nonNull(hierarchyTree) ? hierarchyTree.streamValuesShallow(
               (Identifier) keys[1]) : Stream.empty();
         }

         case 3: {
            var hierarchyTree = this.hierarchyTrees.get(keys[0]);
            return Objects.nonNull(hierarchyTree) ? hierarchyTree.get((Identifier) keys[1], (Identifier) keys[2]).map(
               value -> Stream.of(value)).orElseGet(Stream::empty) : Stream.empty();
         }

         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   @SuppressWarnings({"null", "unchecked"})
   public Stream<Object> streamKeysAtAndBelow(Object... keys) {

      assert ParameterArray.validateSizeAndElements(keys, 0, StoreRank3.rank, this.keyValidators);

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {

         case 0: {
            //@formatter:off
            return this.hierarchyTrees.keySet().stream().flatMap( ( treeKey ) -> this.hierarchyTrees.get( treeKey ).streamKeysDeep() );
            //@formatter:on
         }

         case 1: {
            //@formatter:off
            var hierarchyTree = this.hierarchyTrees.get( keys[ 0 ] );

            return
               (Stream<Object>) ( Objects.nonNull( hierarchyTree ) ? hierarchyTree.streamKeysDeep() : Stream.empty() );
            //@formatter:on
         }

         case 2: {
            //@formatter:off
            var hierarchyTree = this.hierarchyTrees.get( keys[ 0 ] );

            return
               (Stream<Object>) ( Objects.nonNull( hierarchyTree ) ? hierarchyTree.streamKeysDeep( (Identifier) keys[ 1 ] ) : Stream.empty() );
            //@formatter:on

         }

         case 3: {
            //@formatter:off
            var hierarchyTree = this.hierarchyTrees.get( keys[ 0 ] );
            return ( Objects.nonNull( hierarchyTree ) ? hierarchyTree.get( (Identifier) keys[ 1 ], (Identifier) keys[ 2 ] ) : Optional.empty() ).map( child -> Stream.of( keys[2] ) ).orElseGet( Stream::empty );
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
   @SuppressWarnings({"null", "unchecked"})
   public Stream<Object> streamKeysAt(Object... keys) {

      assert ParameterArray.validateSizeAndElements(keys, 0, StoreRank3.rank, this.keyValidators);

      var keyCount = Objects.nonNull(keys) ? keys.length : 0;

      switch (keyCount) {

         case 0: {
            return (Stream<Object>) (Object) this.hierarchyTrees.keySet().stream();
         }

         case 1: {
            //@formatter:off
            var hierarchyTree = this.hierarchyTrees.get( keys[ 0 ] );

            return
               (Stream<Object>) ( Objects.nonNull( hierarchyTree ) ? hierarchyTree.streamKeysShallow( (Identifier) keys[0] ) : Stream.empty() );
            //@formatter:on
         }

         case 2: {
            //@formatter:off
            var hierarchyTree = this.hierarchyTrees.get( keys[ 0 ] );

            return
               (Stream<Object>) ( Objects.nonNull( hierarchyTree ) ? hierarchyTree.streamKeysShallow( (Identifier) keys[ 1 ] ) : Stream.empty() );
            //@formatter:on

         }

         case 3: {
            //@formatter:off
            var hierarchyTree = this.hierarchyTrees.get( keys[ 0 ] );
            return ( Objects.nonNull( hierarchyTree ) ? hierarchyTree.get( (Identifier) keys[ 1 ], (Identifier) keys[ 2 ] ) : Optional.empty() ).map( child -> Stream.of( keys[2] ) ).orElseGet( Stream::empty );
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
   public Stream<Object[]> streamKeySets(Object... keys) {

      //@formatter:off
      return
         this.stream( keys )
            .map    ( GroveThing::getPrimaryKeys )
            .filter ( Optional::isPresent )
            .map    ( Optional::get )
            .map    ( ( lowerKeys ) ->
                      {
                         var keyClass = lowerKeys[0].getClass();
                         var keyArray = (Object[]) Array.newInstance( keyClass, 3 );
                         keyArray[0] = keys[0];
                         if( lowerKeys.length == 1 )
                         {
                            keyArray[1] = lowerKeys[ 0 ];
                            keyArray[2] = lowerKeys[ 0 ];
                         }
                         else
                         {
                            keyArray[1] = lowerKeys[ 0 ];
                            keyArray[2] = lowerKeys[ 1 ];
                         }
                         return keyArray;
                      });
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
