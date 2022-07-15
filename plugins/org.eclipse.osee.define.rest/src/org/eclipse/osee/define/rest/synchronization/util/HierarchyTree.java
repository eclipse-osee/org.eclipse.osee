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

package org.eclipse.osee.define.rest.synchronization.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A hash table that maintains a hierarchical tree of its entries. A cursor or current node is maintained for navigation
 * and iteration methods for nodes within the hierarchy tree. Each node in the hash table may have only one position
 * within the hierarchical tree.
 *
 * @param <K> the class of the unique key to be associated with each entry.
 * @param <V> the class of the value to be associated with each key.
 * @author Loren K. Ashley
 */
public class HierarchyTree<K, V> {

   /**
    * A hash map containing all the nodes of the hierarchical tree.
    */

   Map<K, HierarchyTreeNode<K, V>> nodeMap;

   /**
    * Reference to the root node of the hierarchical tree. A value of <code>null</code> indicates an empty tree.
    */

   HierarchyTreeNode<K, V> rootNode;

   /**
    * Reference to the hierarchical tree node pointed to by the cursor. This might be <code>null</code> if the cursor
    * position has not been set.
    */

   HierarchyTreeNode<K, V> currNode;

   /**
    * Creates a new empty {@link HierarchyTree}.
    */

   public HierarchyTree() {
      this.nodeMap = new HashMap<>();
      this.rootNode = null;
      this.currNode = null;
   }

   /**
    * Predicate to determine if an association for the provided key exists in the map. When assertions are enabled an
    * assertion error will be thrown if the parameter <code>key</code> is <code>null</code>.
    *
    * @param key the key to test for an association.
    * @return <code>true</code>, when an association for the provided key exists in the map; otherwise,
    * <code>false</code>.
    */

   public boolean containsKey(K key) {
      assert Objects.nonNull(key);

      return this.nodeMap.containsKey(key);
   }

   public boolean containsKey(K parentKey, K childKey) {
      assert Objects.nonNull(parentKey) && Objects.nonNull(childKey);

      var child = this.nodeMap.get(childKey);

      //@formatter:off
      return
         Objects.nonNull( child )
            ? child.hasParent()
                 ? child.getParent().getKey().equals( parentKey )
                      ? true
                      : false
                 : false
            : false;
   }

   /**
    * Get the value associated with the specified key or <code>null</code> when the key is not associated with a value.
    * When assertions are enabled an assertion error will be thrown if the parameter <code>key</code> is
    * <code>null</code>.
    *
    * @param key the key whose associated value is to be returned.
    * @return the value associated with the specified key; otherwise, <code>null</code>.
    */

   public V get(K key) {
      assert Objects.nonNull(key);

      var hierarchyTreeNode = nodeMap.get(key);

      if (hierarchyTreeNode == null) {
         return null;
      }

      return hierarchyTreeNode.getValue();
   }

   public Optional<V> get(K parentKey, K childKey) {
      assert Objects.nonNull(parentKey) && Objects.nonNull(childKey);

      var child = nodeMap.get(childKey);

      //@formatter:off
      return
         child.hasParent()
            ? child.getParent().getKey().equals( parentKey )
                 ? Optional.of( child.getValue() )
                 : Optional.empty()
            : Optional.empty();
   }

   /**
    * Sets the {@link HierarchyTree} cursor to the node associated with the provided key. When assertions are enabled an
    * assertion error will be thrown if the parameter <code>key</code> is <code>null</code>.
    *
    * @param key the key of the node to be set as the cursor.
    * @throws RunTimeException when the {@link HierarcyTree} does not contain an association for the provided key.
    */

   public void setCurrent(K key) {
      assert Objects.nonNull(key);

      var hierarchyTreeNode = this.nodeMap.get(key);

      if (hierarchyTreeNode == null) {
         throw new RuntimeException("HierarchyTree::setCurrent: Node not found.");
      }

      this.currNode = hierarchyTreeNode;
      hierarchyTreeNode.setCurrent();
   }

   /**
    * Performs the provided action on the keys of the current node's hierarchical children.
    *
    * @param action the action to be performed on each element.
    */

   public void forEachChildKey(Consumer<? super K> action) {
      this.currNode.iteratorKeys().forEachRemaining(action);
   }

   /**
    * Performs the provided action on the values of the current node's hierarchical children.
    *
    * @param action the action to be performed on each element.
    */

   public void forEachChildValue(Consumer<? super V> action) {
      this.currNode.iteratorValues().forEachRemaining(action);
   }

   /*
    * To be cleaned up in future stories
    */

   public void put(K key, HierarchyTreeNode<K, V> node) {
      this.nodeMap.put(key, node);
   }

   // Insert Methods

   public void setRoot(K key, V value) {
      if (this.rootNode != null) {
         throw new RuntimeException("Root node is already set");
      }

      var rootNode = new HierarchyTreeNode<K, V>(this, key, value);
      this.rootNode = rootNode;
      this.nodeMap.put(key, rootNode);
   }

   public void insertFirst(K parentKey, K key, V value) {
      HierarchyTreeNode<K, V> parent = this.nodeMap.get(parentKey);

      if (parent == null) {
         throw new RuntimeException("Parent node not found");
      }

      parent.insertFirst(key, value);
   }

   public void insertLast(K parentKey, K key, V value) {
      var parentNode = this.nodeMap.get(parentKey);

      if (parentNode == null) {
         throw new RuntimeException("Parent node not found");
      }

      parentNode.insertLast(key, value);
   }

   public void insertBefore(K parentKey, K key, V value) {
      HierarchyTreeNode<K, V> parent = this.nodeMap.get(parentKey);

      if (parent == null) {
         throw new RuntimeException("Parent node not found");
      }

      parent.insertBefore(key, value);
   }

   public void insertAfter(K parentKey, K key, V value) {
      HierarchyTreeNode<K, V> parent = this.nodeMap.get(parentKey);

      if (parent == null) {
         throw new RuntimeException("Parent node not found");
      }

      parent.insertAfter(key, value);
   }

   // Find Methods

   public HierarchyTreeNode<K, V> root() {
      return this.rootNode;
   }

   public HierarchyTreeNode<K, V> getAssertNotNull(K key) {
      HierarchyTreeNode<K, V> node = this.nodeMap.get(key);

      if (node == null) {
         throw new RuntimeException("Node not found");
      }

      return node;
   }

   public HierarchyTreeNode<K, V> getFirstChild(K parentKey) {
      return this.getAssertNotNull(parentKey).getFirstChild();
   }

   public HierarchyTreeNode<K, V> getLastChild(K parentKey) {
      return this.getAssertNotNull(parentKey).getLastChild();
   }

   public HierarchyTreeNode<K, V> getCurrentChild(K parentKey) {
      return this.getAssertNotNull(parentKey).getCurrentChild();
   }

   public HierarchyTreeNode<K, V> getPreviousChild(K parentKey) {
      return this.getAssertNotNull(parentKey).getPreviousChild();
   }

   public HierarchyTreeNode<K, V> getNextChild(K parentKey) {
      return this.getAssertNotNull(parentKey).getNextChild();
   }

   public Iterator<HierarchyTreeNode<K, V>> iterator() {
      this.currNode = this.rootNode;

      return new Iterator<HierarchyTreeNode<K, V>>() {

         @Override
         public boolean hasNext() {
            //@formatter:off
            return HierarchyTree.this.currNode.hasChildren()
                      ? true
                      : HierarchyTree.this.currNode.hasNext()
                           ? true
                           : HierarchyTree.this.currNode.getParent().hasNext();
            //@formatter:on
         }

         @Override
         public HierarchyTreeNode<K, V> next() {
            //@formatter:off
            return HierarchyTree.this.currNode =
                      HierarchyTree.this.currNode.hasChildren()
                         ? HierarchyTree.this.currNode.getFirstChild()
                         : HierarchyTree.this.currNode.hasNext()
                              ? HierarchyTree.this.currNode.getNext()
                              : HierarchyTree.this.currNode.getParent().getNext();
            //@formatter:on

         }
      };
   }

   public void forEach(BiConsumer<K, K> biConsumer) {

      this.iterator().forEachRemaining(hierarchyTreeNode -> {
         var parent = hierarchyTreeNode.getParent();
         var parentKey = parent != null ? parent.getKey() : null;

         var key = hierarchyTreeNode.getKey();

         biConsumer.accept(parentKey, key);

      });
   }

   public Spliterator<HierarchyTreeNode<K, V>> spliterator() {
      this.currNode = this.rootNode;

      return new Spliterator<HierarchyTreeNode<K, V>>() {
         int index = 0;
         int size = HierarchyTree.this.nodeMap.size();

         @Override
         public int characteristics() {
            return Spliterator.NONNULL | Spliterator.ORDERED;
         }

         @Override
         public long estimateSize() {
            return size - index;
         }

         @Override
         public boolean tryAdvance(Consumer<? super HierarchyTreeNode<K, V>> action) {
            HierarchyTreeNode<K, V> nextNode = HierarchyTree.this.currNode.getFirstChild();

            if (nextNode == null) {
               nextNode = HierarchyTree.this.currNode.getNext();
            }

            if (nextNode == null) {
               return false;
            }

            action.accept(nextNode);

            index++;
            HierarchyTree.this.currNode = nextNode;

            return true;
         }

         @Override
         public Spliterator<HierarchyTreeNode<K, V>> trySplit() {
            return null;
         }

      };
   }

   /**
    * Returns a unordered {@link Stream} of all values stored in the hierarchy tree.
    *
    * @return a unordered {@link Stream} of the values in the hierarchy tree.
    */

   public Stream<V> streamValuesDeep() {
      return this.nodeMap.values().stream().map(HierarchyTreeNode::getValue);
   }

   /**
    * Returns an ordered {@link Stream} of the values of the hierarchy tree nodes that are immediate children of the
    * specified node.
    *
    * @param parentKey the key for the node to stream the children of.
    * @return when the node specified by <code>parentKey</code> exists, an ordered {@link Stream} of the specified
    * node's children; otherwise, an empty {@link Stream}.
    */

   public Stream<V> streamValuesShallow(K parentKey) {
      {
         assert Objects.nonNull(parentKey);

         var hierarchyTreeNode = nodeMap.get(parentKey);

         if (hierarchyTreeNode == null) {
            return Stream.empty();
         }

         return hierarchyTreeNode.stream();
      }

   }

   /**
    * Returns a unordered {@link Stream} of the keys of all nodes stored in the hierarchy tree.
    *
    * @return a unordered {@link Stream} of the keys in the hierarchy tree.
    */

   public Stream<K> streamKeysDeep() {
      return this.nodeMap.keySet().stream();
   }

   /**
    * Returns an ordered {@link Stream} of the keys of the nodes hierarchically lower than the specified node.
    *
    * @param parentKey the key for the node to stream the keys of subordinate nodes.
    * @return when the node specified by <code>parentKey</code> exists, an unordered {@link Stream} of the specified
    * node's subordinates nodes keys; otherwise, an empty {@link Stream}.
    */

   public Stream<K> streamKeysDeep(K parentKey) {
      assert Objects.nonNull(parentKey);

      var hierarchyTreeNode = nodeMap.get(parentKey);

      if (hierarchyTreeNode == null) {
         return Stream.empty();
      }

      return hierarchyTreeNode.streamChildKeysDeep();
   }

   /**
    * Returns an ordered {@link Stream} of the keys of the hierarchy tree nodes that are immediate children of the
    * specified node.
    *
    * @param parentKey the key for the node to stream the children keys of.
    * @return when the node specified by <code>parentKey</code> exists, an ordered {@link Stream} of the specified
    * node's children keys; otherwise, an empty {@link Stream}.
    */

   public Stream<K> streamKeysShallow(K parentKey) {
      assert Objects.nonNull(parentKey);

      var hierarchyTreeNode = nodeMap.get(parentKey);

      if (hierarchyTreeNode == null) {
         return Stream.empty();
      }

      return hierarchyTreeNode.streamChildKeys();
   }

   /**
    * Returns an ordered {@link Stream} of the keys of the current nodes immediate hierarchical children.
    *
    * @param when the current node is set, an ordered {@link Stream} of the keys of the current nodes immediate
    * hierarchical children; otherwise, an empty {@link Stream}.
    */

   public Stream<K> streamCurrentNodeKeysShallow() {
      return Objects.nonNull(this.currNode) ? this.currNode.streamChildKeys() : Stream.empty();
   }

   /**
    * Returns a unordered {@link Stream} of the key sets of all nodes stored in the hierarchy tree.
    *
    * @return a unordered {@link Stream} of the key sets in the hierarchy tree.
    */

   @SuppressWarnings("unchecked")
   public Stream<K[]> streamKeySetsDeep() {
      //@formatter:off
      return this.nodeMap.values().stream().map
         (
            ( hierarchyTreeNode ) ->
            {
               K parentKey = hierarchyTreeNode.hasParent() ? hierarchyTreeNode.getParent().getKey() : hierarchyTreeNode.getKey();
               K childKey  = hierarchyTreeNode.getKey();
               Class<?> keyClass = childKey.getClass();
               K[] keyArray = (K[]) Array.newInstance( keyClass, 2);
               keyArray[0] = parentKey;
               keyArray[1] = childKey;
               return keyArray;
            }
         );
      //@formatter:on
   }

   /**
    * Returns an ordered {@link Stream} of the key sets of the nodes hierarchically lower than the specified node.
    *
    * @param parentKey the key for the node to stream the key sets of subordinate nodes.
    * @return when the node specified by <code>parentKey</code> exists, an unordered {@link Stream} of the specified
    * node's subordinates nodes key sets; otherwise, an empty {@link Stream}.
    */

   @SuppressWarnings("unchecked")
   public Stream<K[]> streamKeySetsDeep(K parentKey) {
      //@formatter:off
      var parent = this.nodeMap.get( parentKey );

      if( parent == null ) {
         return Stream.empty();
      }

      return parent.streamChildKeysDeep().map
         (
            ( childKey ) ->
            {
               Class<?> keyClass = childKey.getClass();
               K[] keyArray = (K[]) Array.newInstance( keyClass, 2);
               keyArray[0] = parentKey;
               keyArray[1] = childKey;
               return keyArray;
            }
         );
      //@formatter:on
   }

   /**
    * Returns an ordered {@link Stream} of the key sets of the hierarchy tree nodes that are immediate children of the
    * specified node.
    *
    * @param parentKey the key for the node to stream the children key sets of.
    * @return when the node specified by <code>parentKey</code> exists, an ordered {@link Stream} of the specified
    * node's children key sets; otherwise, an empty {@link Stream}.
    */

   @SuppressWarnings("unchecked")
   public Stream<K[]> streamKeySetsShallow(K parentKey) {
      //@formatter:off
      var parent = this.nodeMap.get( parentKey );

      if( parent == null ) {
         return Stream.empty();
      }

      return parent.streamChildKeys().map
         (
            ( childKey ) ->
            {
               Class<?> keyClass = childKey.getClass();
               K[] keyArray = (K[]) Array.newInstance( keyClass, 2);
               keyArray[0] = parentKey;
               keyArray[1] = childKey;
               return keyArray;
            }
         );
      //@formatter:on
   }

   public int size() {
      return this.nodeMap.size();
   }

}

/* EOF */
