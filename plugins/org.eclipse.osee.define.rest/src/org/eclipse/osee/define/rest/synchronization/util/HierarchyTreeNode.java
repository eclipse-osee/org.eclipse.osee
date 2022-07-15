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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A node object for use in an {@HierarchyTree}. The node contains references to:
 * <ul>
 * <li>the containing {@link HierarchyTree},</li>
 * <li>the node's hierarchical parent,</li>
 * <li>the node's previous and next sibling nodes,</li>
 * <li>the node's first and last hierarchical children nodes,</li>
 * <li>a unique identifier (key), and</li>
 * <li>a value object.</li>
 * </ul>
 *
 * @param <K> the class of the unique key to be associated with the node.
 * @param <V> the class of the value object to be associated with the node.
 * @author Loren K. Ashley
 */

class HierarchyTreeNode<K, V> {

   /**
    * Reference to the containing {@link HierarchyTree}.
    */

   HierarchyTree<K, V> tree;

   /**
    * A unique identifier for the node.
    */

   K key;

   /**
    * A reference to the value represented by this tree node.
    */

   V value;

   /**
    * Reference to the hierarchical parent node. For the root node this will be null.
    */

   HierarchyTreeNode<K, V> parent;

   /**
    * Reference to the first child node
    */

   HierarchyTreeNode<K, V> firstChild;

   /**
    * Reference to the last child node
    */

   HierarchyTreeNode<K, V> lastChild;

   /**
    * Child node list cursor used by iterators and navigation methods. The terms next and previous refer to the node
    * after or before the child node referenced by the cursor. The cursor points to the last node returned by the list
    * navigation methods.
    */

   HierarchyTreeNode<K, V> currChild;

   /**
    * Maintains a count of the nodes number of children. Used for implementation of the {@link Spliterator}.
    */

   int childCount;

   /**
    * Reference to this node's previous hierarchical sibling.
    */

   HierarchyTreeNode<K, V> previousSibling;

   /**
    * Reference to this node's following hierarchical sibling.
    */

   HierarchyTreeNode<K, V> nextSibling;

   /**
    * Creates a new hierarchically unattached node.
    *
    * @param tree the {@link HierarchyTree} the node belongs to.
    * @param key a unique identifier for the node.
    * @param value the value referenced by the node.
    */

   HierarchyTreeNode(HierarchyTree<K, V> tree, K key, V value) {
      this.tree = tree;
      this.key = key;
      this.value = value;

      this.parent = null;
      this.firstChild = null;
      this.lastChild = null;
      this.currChild = null;
      this.childCount = 0;
      this.previousSibling = null;
      this.nextSibling = null;
   }

   //Data

   /**
    * Gets the node's key value.
    *
    * @return the node key value.
    */

   K getKey() {
      return this.key;
   }

   /**
    * Gets the node's value.
    *
    * @return the node value.
    */

   V getValue() {
      return this.value;
   }

   //Navigation Up

   /**
    * Gets a reference to the {@link HierarchyTree} that contains this node.
    *
    * @return the containing {@link HierarchyTree}.
    */

   HierarchyTree<K, V> getTree() {
      return this.tree;
   }

   /**
    * Predicate to determine if the node has a hierarchical parent. The root node of the tree will not have a parent.
    *
    * @return <code>true</code>, when the node has a hierarchical parent; otherwise, <code>false</code>.
    */

   boolean hasParent() {
      return this.parent != null;
   }

   /**
    * Gets a reference to the {@link HierarchyTreeNode} that is the hierarchical parent of this node. For the root node
    * this will return <code>null</code>.
    *
    * @return the hierarchical parent node.
    */

   HierarchyTreeNode<K, V> getParent() {
      return this.parent;
   }

   //Child list navigation

   /**
    * Predicate to determine if the child list contains any nodes.
    *
    * @return <code>true</code>, when child nodes are present; otherwise, <code>false</code>.
    */

   boolean hasChild() {
      return this.firstChild != null;
   }

   /**
    * Predicate to determine if the child list contains a node after the child list cursor.
    *
    * @return <code>true</code>, when the child list contains a node after the child list cursor; otherwise,
    * <code>false</code>.
    */

   boolean hasNextChild() {
      return (this.currChild != null) && (this.currChild.nextSibling != null);
   }

   /**
    * Predicate to determine if the child list contains a node before the child list cursor.
    *
    * @return <code>true</code>, when the child list contains a node before the child list cursor; otherwise,
    * <code>false</code>.
    */

   boolean hasPreviousChild() {
      return (this.currChild != null) && (this.currChild.previousSibling != null);
   }

   /**
    * Returns the first child node and sets the child list cursor to the first node. If there are no children this
    * method will return <code>null</code>.
    *
    * @return the first child node.
    */

   HierarchyTreeNode<K, V> getFirstChild() {
      return this.currChild = this.firstChild;
   }

   /**
    * Returns the last child node and sets the child list cursor to the last node. If there are no children this method
    * will return <code>null</code>.
    *
    * @return the last child node.
    */

   HierarchyTreeNode<K, V> getLastChild() {
      return this.currChild = this.lastChild;
   }

   /**
    * Returns the node referenced by the child list cursor. If there are no children this method will return
    * <code>null</code>.
    *
    * @return the node referenced by the child list cursor.
    */

   HierarchyTreeNode<K, V> getCurrentChild() {
      return this.currChild;
   }

   /**
    * Returns the child node after the child list cursor and advances the list cursor. This method will return
    * <code>null</code> when the list is empty or the list cursor is at the end of the list.
    *
    * @return the next node on the child list or <code>null</code>.
    */

   HierarchyTreeNode<K, V> getNextChild() {
      if ((this.currChild == null) || (this.currChild.nextSibling == null)) {
         return null;
      }
      return this.currChild = this.currChild.nextSibling;
   }

   /**
    * Returns the child node before the child list cursor and moves the list cursor one node up. This method will return
    * <code>null</code> when the list is empty or the list cursor is at the beginning of the list.
    *
    * @return the next node on the child list or <code>null</code>.
    */

   HierarchyTreeNode<K, V> getPreviousChild() {
      if ((this.currChild == null) || (this.currChild.previousSibling == null)) {
         return null;
      }
      return this.currChild = this.currChild.previousSibling;
   }

   /**
    * Predicate to determine if the node has any hierarchical children.
    *
    * @return <code>true</code>, when the node has hierarchical children; otherwise, <code>false</code>.
    */

   boolean hasChildren() {
      return this.firstChild != null;
   }

   /**
    * Inserts a new node at the start of the child node list and sets the child list cursor to the new node.
    *
    * @param key a unique identifier for the new node.
    * @param value the value referenced by the node.
    */

   void insertFirst(K key, V value) {
      var newChild = new HierarchyTreeNode<K, V>(this.tree, key, value);

      newChild.parent = this;

      this.tree.put(key, newChild);

      var formerFirstChild = this.firstChild;

      this.firstChild = newChild;

      if (formerFirstChild != null) {
         newChild.nextSibling = formerFirstChild;
         formerFirstChild.previousSibling = newChild;
      }

      if (this.lastChild == null) {
         this.lastChild = newChild;
      }

      this.childCount++;
   }

   /**
    * Inserts a new node at the end of the child node list and sets the child list cursor to the new node.
    *
    * @param key a unique identifier for the new node.
    * @param value the value referenced by the node.
    */

   void insertLast(K key, V value) {
      var newChildNode = new HierarchyTreeNode<K, V>(this.tree, key, value);

      newChildNode.parent = this;

      this.tree.put(key, newChildNode);

      var formerLastChild = this.lastChild;

      this.lastChild = newChildNode;

      if (formerLastChild != null) {
         newChildNode.previousSibling = formerLastChild;
         formerLastChild.nextSibling = newChildNode;
      }

      if (this.firstChild == null) {
         this.firstChild = newChildNode;
      }

      if (this.currChild == null) {
         this.currChild = newChildNode;
      }

      this.childCount++;
   }

   /**
    * Inserts a new node before the child node list cursor.
    *
    * @param key a unique identifier for the new node.
    * @param value the value referenced by the node.
    */

   void insertBefore(K key, V value) {
      if ((this.currChild == null) || (this.currChild == this.firstChild)) {
         this.insertFirst(key, value);

         if (this.currChild == null) {
            this.currChild = this.firstChild;
         }

         return;
      }

      var newChild = new HierarchyTreeNode<K, V>(this.tree, key, value);

      var formerPreviousSibling = this.currChild.previousSibling;

      newChild.parent = this;
      newChild.previousSibling = formerPreviousSibling;
      newChild.nextSibling = this.currChild;

      this.tree.put(key, newChild);

      formerPreviousSibling.nextSibling = newChild;

      this.currChild.previousSibling = newChild;

      this.childCount++;
   }

   /**
    * Inserts a new node after the child node list cursor.
    *
    * @param key unique identifier for the new node.
    * @param value the value referenced by the node.
    */

   void insertAfter(K key, V value) {
      if ((this.currChild == null) || (this.currChild == this.lastChild)) {
         insertLast(key, value);

         if (this.currChild == null) {
            this.currChild = this.lastChild;
         }

         return;
      }

      var newChild = new HierarchyTreeNode<K, V>(this.tree, key, value);

      var formerNextSibling = this.currChild.nextSibling;

      newChild.parent = this;
      newChild.previousSibling = this.currChild;
      newChild.nextSibling = formerNextSibling;

      this.tree.put(key, newChild);

      this.currChild.nextSibling = newChild;

      formerNextSibling.previousSibling = newChild;

      this.childCount++;
   }

   /**
    * Sets this node as the current node on the parent node's child node list.
    */

   void setCurrent() {
      if (this.parent == null) {
         return;
      }

      this.parent.currChild = this;
   }

   /**
    * Returns an ordered iterator over the node's hierarchical children.<br>
    * <br>
    * {@inheritDoc}
    */

   private Iterator<HierarchyTreeNode<K, V>> iterator() {

      return new Iterator<HierarchyTreeNode<K, V>>() {

         boolean first = true;

         @Override
         public boolean hasNext() {
            return this.first ? HierarchyTreeNode.this.hasChild() : HierarchyTreeNode.this.hasNextChild();
         }

         @Override
         public HierarchyTreeNode<K, V> next() {
            if (this.first) {
               if (!HierarchyTreeNode.this.hasChild()) {
                  throw new NoSuchElementException();
               }
               this.first = false;
               return HierarchyTreeNode.this.getFirstChild();
            }

            if (!HierarchyTreeNode.this.hasNextChild()) {
               throw new NoSuchElementException();
            }

            return HierarchyTreeNode.this.getNextChild();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   /**
    * Returns an ordered iterator over the keys of the node's hierarchical children.<br>
    * <br>
    * {@inheritDoc}
    */

   Iterator<K> iteratorKeys() {

      return new Iterator<K>() {

         Iterator<HierarchyTreeNode<K, V>> iterator = HierarchyTreeNode.this.iterator();

         @Override
         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         @Override
         public K next() {
            return this.iterator.next().getKey();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   /**
    * Returns an ordered iterator over the values of the node's hierarchical children.<br>
    * <br>
    * {@inheritDoc}
    */

   Iterator<V> iteratorValues() {

      return new Iterator<V>() {

         Iterator<HierarchyTreeNode<K, V>> iterator = HierarchyTreeNode.this.iterator();

         @Override
         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         @Override
         public V next() {
            return this.iterator.next().getValue();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   /**
    * Returns an ordered non-partitionable spliterator of the node's hierarchical children.
    *
    * @return a {@link Spliterator} over the node's hierarchical children.
    */

   public Spliterator<V> spliterator() {

      return new Spliterator<V>() {

         boolean first = true;
         int index = 0;

         @Override
         public int characteristics() {
            return Spliterator.NONNULL | Spliterator.ORDERED;
         }

         @Override
         public long estimateSize() {
            return HierarchyTreeNode.this.childCount - this.index;
         }

         @Override
         public boolean tryAdvance(Consumer<? super V> action) {
            if (this.first) {
               if (!HierarchyTreeNode.this.hasChild()) {
                  return false;
               }

               var value = HierarchyTreeNode.this.getFirstChild().getValue();

               action.accept(value);

               this.first = false;
               index++;

               return true;
            }

            if (!HierarchyTreeNode.this.hasNextChild()) {
               return false;
            }

            var value = HierarchyTreeNode.this.getNextChild().getValue();

            action.accept(value);

            index++;

            return true;
         }

         @Override
         public Spliterator<V> trySplit() {
            return null;
         }
      };
   }

   /**
    * Returns an ordered non-partitionable spliterator of the keys of the node's hierarchical children.
    *
    * @return a {@link Spliterator} over the keys of the node's hierarchical children.
    */

   public Spliterator<K> spliteratorChildKeys() {

      return new Spliterator<K>() {

         boolean first = true;
         int index = 0;

         @Override
         public int characteristics() {
            return Spliterator.NONNULL | Spliterator.ORDERED;
         }

         @Override
         public long estimateSize() {
            return HierarchyTreeNode.this.childCount - this.index;
         }

         @Override
         public boolean tryAdvance(Consumer<? super K> action) {
            if (this.first) {
               if (!HierarchyTreeNode.this.hasChild()) {
                  return false;
               }

               var key = HierarchyTreeNode.this.getFirstChild().getKey();

               action.accept(key);

               this.first = false;
               this.index++;

               return true;
            }

            if (!HierarchyTreeNode.this.hasNextChild()) {
               return false;
            }

            var key = HierarchyTreeNode.this.getNextChild().getKey();

            action.accept(key);

            this.index++;

            return true;
         }

         @Override
         public Spliterator<K> trySplit() {
            return null;
         }
      };
   }

   /**
    * Returns an unordered non-partitionable spliterator of the keys of all hierarchically lower nodes.
    *
    * @return a {@link Spliterator} over the keys of all hierarchically lower nodes.
    */

   public Spliterator<K> spliteratorChildKeysDeep() {

      return new Spliterator<K>() {

         boolean first = true;
         Spliterator<K> childSpliteratorChildKeysDeep = null;

         @Override
         public int characteristics() {
            return Spliterator.NONNULL;
         }

         @Override
         public long estimateSize() {
            return Long.MAX_VALUE;
         }

         @Override
         public boolean tryAdvance(Consumer<? super K> action) {
            if (this.first) {
               if (!HierarchyTreeNode.this.hasChild()) {
                  return false;
               }

               var child = HierarchyTreeNode.this.getFirstChild();
               var key = child.getKey();
               this.childSpliteratorChildKeysDeep = child.spliteratorChildKeysDeep();

               action.accept(key);

               this.first = false;
               return true;
            }

            if (this.childSpliteratorChildKeysDeep != null) {
               if (this.childSpliteratorChildKeysDeep.tryAdvance(action)) {
                  return true;
               } else {
                  this.childSpliteratorChildKeysDeep = null;
               }
            }

            if (!HierarchyTreeNode.this.hasNextChild()) {
               return false;
            }

            var child = HierarchyTreeNode.this.getNextChild();
            var key = child.getKey();
            this.childSpliteratorChildKeysDeep = child.spliteratorChildKeysDeep();

            action.accept(key);

            return true;
         }

         @Override
         public Spliterator<K> trySplit() {
            return null;
         }
      };
   }

   /**
    * Returns a ordered {@link Stream} of the node's hierarchical children.
    *
    * @return a {@link Stream} of the node's children.
    */

   Stream<V> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   /**
    * Returns a ordered {@link Stream} of the keys of the node's hierarchical children.
    *
    * @return a {@link Stream} of the keys of the node's children.
    */

   Stream<K> streamChildKeys() {
      return StreamSupport.stream(this.spliteratorChildKeys(), false);
   }

   /**
    * Returns a {@link Stream} of the keys of all nodes hierarchically below this node.
    *
    * @return an unordered {@link Stream} of the keys of all hierarchically lower nodes.
    */

   Stream<K> streamChildKeysDeep() {
      return StreamSupport.stream(this.spliteratorChildKeysDeep(), false);
   }

   // Navigation Horizontal

   /**
    * Predicate to determine if this node has a previous sibling.
    *
    * @return <code>true</code>, when the node has a previous sibling; otherwise, <code>false</code>.
    */

   boolean hasPrevious() {
      return this.previousSibling != null;
   }

   /**
    * Gets this node's previous sibling.
    *
    * @return the previous sibling node or <code>null</code>.
    */

   HierarchyTreeNode<K, V> getPrevious() {
      return this.previousSibling;
   }

   /**
    * Predicate to determine if this node has a following sibling.
    *
    * @return <code>true</code>, when the node has a following sibling; otherwise, <code>false</code>.
    */

   boolean hasNext() {
      return this.nextSibling != null;
   }

   /**
    * Gets this node's following sibling.
    *
    * @return the following sibling node or <code>null</code>.
    */

   HierarchyTreeNode<K, V> getNext() {
      return this.nextSibling;
   }

}

/* EOF */