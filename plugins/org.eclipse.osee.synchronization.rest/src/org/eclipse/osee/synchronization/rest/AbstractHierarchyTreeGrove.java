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

package org.eclipse.osee.synchronization.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.util.HierarchyTree;

/**
 * This class provides a map of trees implementation of the {@link Grove} interface, to minimize the effort required to
 * implement this interface for specific Synchronization Artifact things.
 *
 * @author Loren K. Ashley
 */

public abstract class AbstractHierarchyTreeGrove implements Grove {

   /**
    * Save the {@link IdentifierType} associated with the {@link GroveThing}s saved in the grove.
    */

   protected final IdentifierType identifierType;

   /**
    * A {@link Map} of {@link HierarchyTree}s used to store the {@link GroveThing}s.
    */

   protected final Map<Identifier, HierarchyTree<Identifier, GroveThing>> hierarchyTrees;

   /**
    * When the native things stored in the map implement the {@link Id} interface, this map provides an association
    * between the {@link GroveThing}s and the key of their associated native things.
    */

   protected final Map<Long, GroveThing> nativeKeyMap;

   /**
    * Creates a new empty grove.
    *
    * @param identifierType the {@link IdentifierType} of the {@link GroveThing}s to be saved in the grove.
    * @throws NullPointerException when the parameter <code>identifierType</code> is <code>null</code>.
    */

   public AbstractHierarchyTreeGrove(IdentifierType identifierType) {
      this.identifierType = Objects.requireNonNull(identifierType);
      this.hierarchyTrees = new HashMap<>();
      this.nativeKeyMap = new HashMap<>();
   }

   /**
    * Adds the provided {@link GroveThing} as the root of a hierarchy tree to the {@link Grove}.
    * <p>
    * {@inheritDoc}
    * <p>
    *
    * @throws NullPointerException when the provided {@link GroveThing} is <code>null</code>.
    * @throws DuplicateGroveEntry when a hierarchy tree is already associated with the provided {@link GroveThing}'s
    * key.
    */

   @Override
   public GroveThing add(GroveThing groveThing) {
      Objects.requireNonNull(groveThing);

      var groveThingKey = groveThing.getGroveThingKey();

      if (this.hierarchyTrees.containsKey(groveThingKey)) {
         throw new DuplicateGroveEntry(this, groveThing);
      }

      var hierarchyTree = new HierarchyTree<Identifier, GroveThing>();

      this.hierarchyTrees.put(groveThingKey, hierarchyTree);

      hierarchyTree.setRoot(groveThingKey, groveThing);

      groveThing.getNativeKey().ifPresent(nativeKey -> this.nativeKeyMap.put(nativeKey, groveThing));

      return groveThing;
   }

   /**
    * Adds an association of a {@link GroveThing} and it's key to the {@link Grove} in the specified hierarchical
    * position.
    *
    * @param treeKey the {@link Identifier} of the hierarchical tree to hold the {@link GroveThing}.
    * @param parentKey the {@link Identifier} of the hierarchical parent to the {@link GroveThing} being added.
    * @param groveThing the {@link GroveThing} object to be added to the grove.
    * @throws NullPointerException when one of the parameters <code>treeKey</code>, <code>parentKey</code>, or
    * <code>groveThing</code> are <code>null</code>.
    * @throws DuplicateGroveEntry when the hierarchy tree specified by <code>treeKey</code> already contains an entry
    * with the provided {@link GroveThing}'s key.
    */

   public void add(Identifier treeKey, Identifier parentKey, GroveThing groveThing) {
      Objects.requireNonNull(treeKey);
      Objects.requireNonNull(parentKey);
      Objects.requireNonNull(groveThing);

      var hierarchyTree = this.hierarchyTrees.get(treeKey);
      assert Objects.nonNull(hierarchyTree);

      var groveThingKey = groveThing.getGroveThingKey();

      if (hierarchyTree.containsKey(groveThingKey)) {
         throw new DuplicateGroveEntry(this, groveThing);
      }

      hierarchyTree.insertLast(parentKey, groveThingKey, groveThing);

      groveThing.getNativeKey().ifPresent(nativeKey -> this.nativeKeyMap.put(nativeKey, groveThing));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean contains(Long nativeKey) {
      return this.nativeKeyMap.containsKey(nativeKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void createForeignThings(SynchronizationArtifactBuilder synchronizationArtifactBuilder) {

      Optional<Consumer<GroveThing>> converterOptional =
         synchronizationArtifactBuilder.getConverter(this.identifierType);

      converterOptional.ifPresent(converter -> this.stream().forEach(converter::accept));
   }

   /**
    * Performs the given {@link BiConsumer} with the parent key and key of each {@link GroveThing} in the hierarchy tree
    * specified by the parameter <code>treeKey</code>.
    *
    * @param treeKey the hierarchy tree to process with the {@link BiConsumer}.
    * @param biConsumer a {@link BiConsumer} that takes each {@link GroveThing}'s parent key as the first parameter and
    * the {@link GroveThing}'s key as the second parameter. The first parameter will be <code>null</code> when the
    * {@link GroveThing} is the root thing in the hierarchy tree.
    */

   public void forEachGroveThing(Identifier treeKey, BiConsumer<Identifier, Identifier> biConsumer) {
      var hierarchyTree = this.hierarchyTrees.get(treeKey);

      hierarchyTree.forEach(biConsumer);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<GroveThing> getByNativeKey(Long nativeKey) {
      return Optional.ofNullable(this.nativeKeyMap.get(nativeKey));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IdentifierType getType() {
      return this.identifierType;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<GroveThing> stream() {
      return this.hierarchyTrees.values().stream().flatMap(HierarchyTree::stream);
   }

   /**
    * Returns a {@link Stream} of the keys of the hierarchical children of the specified {@link GroveThing}.
    *
    * @param treeKey the hierarchy tree containing the {@link GroveThing}.
    * @param parentKey the key of the {@link GroveThing} to stream the child keys of.
    * @return a {@link Stream} of the keys of the hierarchical children of the specified {@link GroveThing}.
    */

   public Stream<Identifier> streamGroveThingChildKeys(Identifier treeKey, Identifier parentKey) {
      assert Objects.nonNull(treeKey) && Objects.nonNull(parentKey);

      var hierarchyTree = this.hierarchyTrees.get(treeKey);
      assert Objects.nonNull(hierarchyTree);

      assert hierarchyTree.containsKey(parentKey);

      hierarchyTree.setCurrent(parentKey);

      return hierarchyTree.streamChildKeys();
   }

   /**
    * Returns a {@link Stream} of the keys for the root {@link GroveThing} in each hierarchy tree.
    *
    * @return a {@link Stream} of the {@link Identifier} associated with the root {@link GroveThing} in each hierarchy
    * tree.
    */

   public Stream<Identifier> streamRootGroveThingKeys() {
      return this.hierarchyTrees.keySet().stream();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      return null;
   }

}

/* EOF */