/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.lang.model.util.Elements;

/**
 * A wrapper upon a {@link HashMap} and an {@link ArrayList} to maintain the submission order of the entries. The
 * motivation for this implementation over the JDK {@link LinkedHashMap} is to have the ability to generate
 * {@link Spliterator}s that start with a list element specified by key or by index.
 *
 * @author Loren K. Ashley
 * @param <K> the type for the map keys.
 * @param <V> the type for the values stored.
 */

public class ListMap<K, V> {

   /**
    * Instances of this class are stored on the internal map and list so that the map key can be obtained from the list
    * and the list position can be obtained from the map.
    *
    * @implNote Unfortunately the hook methods in {@link HashMap} used by classes like {@link LinkedHashMap} are package
    * private an cannot be overridden by classes outside the JDK package. The best we can do here is to make a map entry
    * that gets put into another map entry and just accept the overhead. Otherwise a complete map implementation would
    * have to be done from scratch.
    * @param <K> the type for the map keys.
    * @param <V> the type for the map values.
    */

   public static class Element<K, V> {

      /**
       * The key for the map entry.
       */

      private final K key;

      /**
       * The list position of the map entry.
       */

      private final int position;

      /**
       * The value for the map entry.
       */

      private final V value;

      /**
       * Creates a new immutable map and list entry.
       *
       * @param key the key for the entry.
       * @param value the value for the entry.
       * @param position the list position for the entry.
       * @throws NullPointerException when <code>key</code> or <code>value</code> are <code>null</code>.
       * @throws IndexOutOfBoundsException when <code>position</code> is less than 0.
       */

      Element(K key, V value, int position) {
         this.key = Objects.requireNonNull(key, "ListMap.Element::new, null keys are not allowed.");
         this.value = Objects.requireNonNull(value, "ListMap.Element::new, null values are not allowed.");
         if (position < 0) {
            throw new IndexOutOfBoundsException("ListMap.Element::new, position is less than zero.");
         }
         this.position = position;
      }

      /**
       * Gets the map key for the {@link Element}.
       *
       * @return the map key.
       */

      public K getKey() {
         return this.key;
      }

      /**
       * Gets the list index position of the {@link Element}.
       *
       * @return the list index position of the {@link Element}.
       */

      int getPosition() {
         return this.position;
      }

      /**
       * Gets the map value for the {@link Element}.
       *
       * @return the map value.
       */

      public V getValue() {
         return this.value;
      }

   }

   /**
    * Implements a view of the {@link ListMap} that implements the {@link List} interface.
    */

   public final class ListView implements List<V> {

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>value</code> is <code>null</code>.
       * @throws UnsupportedOperationException when a key extractor function was not provided to the constructor.
       */

      @Override
      public void add(int index, V value) {

         Objects.requireNonNull(value, "ListMap.ListView::add, parameter \"value\" cannot be null.");

         if (Objects.isNull(ListMap.this.keyExtractor)) {
            throw new UnsupportedOperationException(
               "ListMap.ListView::add(), a key extractor function is not available.");
         }

         ListMap.this.put(ListMap.this.keyExtractor.apply(value), value, index);
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>value</code> is <code>null</code>.
       * @throws UnsupportedOperationException when a key extractor function was not provided to the constructor.
       */

      @Override
      public boolean add(V value) {

         Objects.requireNonNull(value, "ListMap.ListView::add, parameter \"value\" cannot be null.");

         if (Objects.isNull(ListMap.this.keyExtractor)) {
            throw new UnsupportedOperationException(
               "ListMap.ListView::add(), a key extractor function is not available.");
         }

         var key = ListMap.this.keyExtractor.apply(value);
         var currentValue = ListMap.this.map.get(key);

         if (Objects.isNull(currentValue)) {
            ListMap.this.put(key, value);
            return true;
         }

         if (currentValue.equals(value)) {
            return false;
         }

         ListMap.this.put(key, value);

         return true;
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>value</code> is <code>null</code>.
       * @throws UnsupportedOperationException when a key extractor function was not provided to the constructor.
       */

      @Override
      public boolean addAll(Collection<? extends V> collection) {

         Objects.requireNonNull(collection, "ListMap.ListView::addAll, parameter \"collection\" cannot be null.");

         if (Objects.isNull(ListMap.this.keyExtractor)) {
            throw new UnsupportedOperationException(
               "ListMap.ListView::addAll(), a key extractor function is not available.");
         }

         if (collection.isEmpty()) {
            return false;
         }

         for (var value : collection) {

            ListMap.this.put(ListMap.this.keyExtractor.apply(value), value);
         }

         return true;
      }

      /**
       * {@inheritDoc}
       *
       * @implNote Functional but not efficient.
       * @throws NullPointerException when <code>value</code> is <code>null</code>.
       * @throws UnsupportedOperationException when a key extractor function was not provided to the constructor.
       * @throws IndexOutOfBoundsException when <code>index</code> is less than zero or greater than size.
       */

      @Override
      public boolean addAll(int index, Collection<? extends V> collection) {

         Objects.requireNonNull(collection, "ListMap.ListView::addAll, parameter \"collection\" cannot be null.");

         if (Objects.isNull(ListMap.this.keyExtractor)) {
            throw new UnsupportedOperationException(
               "ListMap.ListView::addAll(), a key extractor function is not available.");
         }

         if ((index < 0) || (index > ListMap.this.size())) {
            throw new IndexOutOfBoundsException();
         }

         if (collection.isEmpty()) {
            return false;
         }

         for (var value : collection) {

            ListMap.this.put(ListMap.this.keyExtractor.apply(value), value, index++);
         }

         return true;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public void clear() {
         ListMap.this.clear();
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>object</code> is <code>null</code>.
       * @throws ClassCastException when <code>object</code> does not extend <code>V</code>.
       */

      @Override
      public boolean contains(Object object) {

         Objects.requireNonNull(object, "ListMap.ListView::contains, parameter \"object\" cannot be null.");

         @SuppressWarnings("unchecked")
         var value = (V) object;

         if (Objects.nonNull(ListMap.this.keyExtractor)) {
            var key = ListMap.this.keyExtractor.apply(value);
            return ListMap.this.map.containsKey(key);
         }

         for (var entry : ListMap.this.list) {
            if (value.equals(entry.getValue())) {
               return true;
            }
         }

         return false;
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>collection</code> is <code>null</code>.
       */

      @Override
      public boolean containsAll(Collection<?> collection) {

         Objects.requireNonNull(collection, "ListMap.ListView::addAll, parameter \"collection\" cannot be null.");

         for (var object : collection) {
            if (!this.contains(object)) {
               return false;
            }
         }

         return true;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public V get(int index) {

         if (ListMap.this.outOfBounds(index)) {
            //@formatter:off
            throw
               new IndexOutOfBoundsException
                      (
                         new Message()
                                .title( "ListMap.ListView::get, index is out of bounds." )
                                .indentInc()
                                .segment( "index", index )
                                .segment( "List Map Size", ListMap.this.size() )
                                .toString()
                      );
            //@formatter:on
         }

         return ListMap.this.get(index).orElse(null);
      }

      /**
       * {@inheritDoc}
       *
       * @throws ClassCastException when <code>object</code> does not implement <code>V</code>.
       */

      @Override
      public int indexOf(Object object) {

         @SuppressWarnings("unchecked")
         var value = (V) object;

         return ListMap.this.findValue(value).map(Element::getPosition).orElse(-1);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean isEmpty() {
         return ListMap.this.isEmpty();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Iterator<V> iterator() {
         //@formatter:off
         return
            new Iterator<V>() {

               /**
                * Get an {@link Iterator} from the {@link ListMap#list}.
                */

               Iterator<Element<K,V>> entryIterator = ListMap.this.list.iterator();

               /**
                * {@inheritDoc}
                */

               @Override
               public boolean hasNext() {
                  return this.entryIterator.hasNext();
               }

               /**
                * {@inheritDoc}
                */

               @Override
               public V next() {
                  var element = this.entryIterator.next();
                  return element.getValue();
               }

               /**
                * {@inheritDoc}
                */

               @Override
               public void remove() {
                  this.entryIterator.remove();
               }

            };
         //@formatter:on
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public int lastIndexOf(Object o) {
         throw new UnsupportedOperationException();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public ListIterator<V> listIterator() {
         throw new UnsupportedOperationException();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public ListIterator<V> listIterator(int index) {
         throw new UnsupportedOperationException();
      }

      /**
       * Gets {@link ListMap} implementing the list view.
       *
       * @return the implementing {@link ListMap}.
       */

      public ListMap<K, V> listMapView() {
         return ListMap.this;
      }

      /**
       * {@inheritDoc}
       *
       * @throws IndexOutOfBoundsException when <code>index</code> is less than zero or greater than or equal to the
       * {@link ListMap} size.
       */

      @Override
      public V remove(int index) {

         if (ListMap.this.outOfBounds(index)) {
            //@formatter:off
            throw
               new IndexOutOfBoundsException
                      (
                         new Message()
                                .title( "ListMap.ListView::remove, index is out of bounds." )
                                .indentInc()
                                .segment( "index", index )
                                .segment( "List Map Size", ListMap.this.size() )
                                .toString()
                      );
            //@formatter:on
         }

         var element = ListMap.this.list.get(index);

         var rv = element.getValue();

         ListMap.this.remove(element.getKey());

         return rv;
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>object</code> is <code>null</code>.
       * @throws ClassCastException when <code>object</code> does not implement <code>V</code>.
       */

      @Override
      public boolean remove(Object object) {

         Objects.requireNonNull(object, "ListMap.ListView::remove, parameter \"object\" cannot be null.");

         @SuppressWarnings("unchecked")
         var value = (V) object;

         //@formatter:off
         return
            ListMap.this
               .findValue( value )
               .map( ( element ) -> ListMap.this.remove( element.getKey() ) )
               .isPresent();
         //@formatter:on
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>collection</code> or a member of the <code>collection</code> is
       * <code>null</code>.
       * @throws ClassCastException when a member of <code>collection<code> does not implement <code>V</code>.
       */

      @Override
      public boolean removeAll(Collection<?> collection) {

         Objects.requireNonNull(collection, "ListMap.ListView::remove, parameter \"collection\" cannot be null.");

         var result = false;

         for (var object : collection) {
            result |= this.remove(object);
         }

         return result;
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>collection</code> is <code>null</code>.
       */

      @Override
      public boolean retainAll(Collection<?> collection) {

         var result = false;

         for (var element : ListMap.this.list) {

            if (!collection.contains(element.getValue())) {
               result |= ListMap.this.remove(element.getKey()).isPresent();
            }
         }

         return result;
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>value</code> is <code>null</code>.
       * @throws IndexOutOfBoundsException when <code>index</code> is less than zero or greater than or equal to the
       * {@link ListMap} size.
       */

      @Override
      public V set(int index, V value) {

         Objects.requireNonNull(value, "ListMap.ListView::set, parameter \"value\" cannot be null.");

         if (ListMap.this.outOfBounds(index)) {
            //@formatter:off
            throw
               new IndexOutOfBoundsException
                      (
                         new Message()
                                .title( "ListMap.ListView::remove, index is out of bounds." )
                                .indentInc()
                                .segment( "index", index )
                                .segment( "List Map Size", ListMap.this.size() )
                                .toString()
                      );
            //@formatter:on
         }

         var oldElement = ListMap.this.list.get(index);
         var newElement = new Element<K, V>(oldElement.getKey(), value, index);
         ListMap.this.list.set(index, newElement);
         return oldElement.getValue();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public int size() {
         return ListMap.this.size();
      }

      /**
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public List<V> subList(int fromIndex, int toIndex) {
         throw new UnsupportedOperationException();
      }

      /**
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Object[] toArray() {
         throw new UnsupportedOperationException();
      }

      /**
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public <T> T[] toArray(T[] a) {
         throw new UnsupportedOperationException();
      }

   }

   /**
    * Implements a view of the {@link ListMap} that implements the {@link Map} interface.
    */

   final class MapView implements Map<K, V> {

      /**
       * Implements a {@link Set} of {@link Map.Entry} view of the {@link ListMap} {@link Map} view.
       */

      final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

         /**
          * {@inheritDoc}
          */

         @Override
         public Iterator<Entry<K, V>> iterator() {
            //@formatter:off
            return
               new Iterator<Map.Entry<K,V>>() {

                  /**
                   * Get an {@link Iterator} from the {@link ListMap#list}.
                   */

                  Iterator<Element<K,V>> entryIterator = ListMap.this.list.iterator();

                  /**
                   * {@inheritDoc}
                   */

                  @Override
                  public boolean hasNext() {
                     return this.entryIterator.hasNext();
                  }

                  /**
                   * {@inheritDoc}
                   */

                  @Override
                  public Entry<K, V> next() {
                     var element = this.entryIterator.next();
                     var entry = new Map.Entry<K,V>() {
                        Element<K,V> entryElement = element;

                        @Override
                        public K getKey() {
                           return this.entryElement.getKey();
                        }

                        @Override
                        public V getValue() {
                           return this.entryElement.getValue();
                        }

                        @Override
                        public V setValue(V value) {
                           throw new UnsupportedOperationException();
                        }

                     };

                     return entry;
                  }

                  /**
                   * {@inheritDoc}
                   */

                  @Override
                  public void remove() {
                     this.entryIterator.remove();
                  }

               };
            //@formatter:on
         }

         /**
          * {@inheritDoc}
          */

         @Override
         public int size() {
            return ListMap.this.map.size();
         }

      }

      /**
       * Implements a {@link Set} view of the values in the {@link ListMap}.
       */

      final class ValueSet extends AbstractCollection<V> {

         /**
          * {@inheritDoc}
          */

         @Override
         public Iterator<V> iterator() {
            //@formatter:off
            return
               new Iterator<V>() {

                  /**
                   * Get an {@link Iterator} from the {@link ListMap#list}.
                   */

                  Iterator<Element<K,V>> entryIterator = ListMap.this.list.iterator();

                  /**
                   * {@inheritDoc}
                   */

                  @Override
                  public boolean hasNext() {
                     return this.entryIterator.hasNext();
                  }

                  /**
                   * {@inheritDoc}
                   */

                  @Override
                  public V next() {
                     var element = this.entryIterator.next();
                     var value = element.getValue();
                     return value;
                  }

                  /**
                   * {@inheritDoc}
                   */

                  @Override
                  public void remove() {
                     this.entryIterator.remove();
                  }
            };
            //@formatter:on
         }

         /**
          * {@inheritDoc}
          */

         @Override
         public int size() {
            return ListMap.this.map.size();
         }

      }

      /**
       * Caches the entry set view.
       */

      private transient Set<Map.Entry<K, V>> entrySet = null;

      /**
       * Caches the value set view.
       */

      private transient Collection<V> valueSet = null;

      /**
       * {@inheritDoc}
       */

      @Override
      public void clear() {
         ListMap.this.clear();
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>key</code> is <code>null</code>.
       */

      @Override
      public boolean containsKey(Object key) {
         Objects.requireNonNull(key, "ListMap.MapView::containsKey, parameter \"key\" cannot be null.");
         return ListMap.this.map.containsKey(key);
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>object</code> is <code>null</code>.
       * @throws ClassCastException when <code>object</code> does not implement <code>V</code>.
       */

      @Override
      public boolean containsValue(Object object) {

         Objects.requireNonNull(object, "ListMap.MapView::containsValue, parameter \"object\" cannot be null.");

         @SuppressWarnings("unchecked")
         var value = (V) object;

         if (Objects.nonNull(ListMap.this.keyExtractor)) {
            var key = ListMap.this.keyExtractor.apply(value);
            return ListMap.this.map.containsKey(key);
         }

         for (var entry : ListMap.this.list) {
            if (value.equals(entry.getValue())) {
               return true;
            }
         }

         return false;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Set<Entry<K, V>> entrySet() {
      //@formatter:off
         return
            Objects.nonNull( this.entrySet )
               ? this.entrySet
               : ( this.entrySet = new EntrySet() );
         //@formatter:on
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>key</code> is <code>null</code>.
       * @throws ClassCastException when <code>key</code> does not implement <code>K</code>.
       */

      @Override
      public V get(Object key) {

         Objects.requireNonNull(key, "ListMap.MapView::get, parameter \"key\" cannot be null.");

         @SuppressWarnings("unchecked")
         var kKey = (K) key;

         return ListMap.this.get(kKey).orElse(null);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean isEmpty() {
         return ListMap.this.isEmpty();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Set<K> keySet() {
         return Collections.unmodifiableSet(ListMap.this.map.keySet());
      }

      /**
       * Gets {@link ListMap} implementing the list view.
       *
       * @return the implementing {@link ListMap}.
       */

      public ListMap<K, V> listMapView() {
         return ListMap.this;
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>key</code> or <code>value</code> are <code>null</code>.
       */

      @Override
      public V put(K key, V value) {
         Objects.requireNonNull(key, "ListMap.MapView::put, parameter \"key\" cannot be null.");
         Objects.requireNonNull(value, "ListMap.MapView::put, parameter \"value\" cannot be null.");
         return ListMap.this.put(key, value).orElse(null);
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>map</code> is <code>null</code>.
       */

      @Override
      public void putAll(Map<? extends K, ? extends V> map) {
         ListMap.this.putAll(map);
      }

      /**
       * {@inheritDoc}
       *
       * @throws NullPointerException when <code>key</code> is <code>null</code>.
       * @throws ClassCastException when <code>key</code> does not implement <code>K</code>.
       */

      @Override
      public V remove(Object key) {
         @SuppressWarnings("unchecked")
         var kKey = (K) key;
         return ListMap.this.remove(kKey).orElse(null);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public int size() {
         return ListMap.this.size();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Collection<V> values() {
         //@formatter:off
         return
            Objects.nonNull( this.valueSet )
               ? this.valueSet
               : ( this.valueSet = new ValueSet() );
         //@formatter:on
      }

   }

   /**
    * The default initial capacity for the internal map and list.
    */

   private static final int DEFAULT_INITIAL_CAPACITY = 64;

   /**
    * The default load factor for the internal map.
    */

   private static final float DEFAULT_LOAD_FACTOR = 0.75f;

   /**
    * The default minimum size for a {@link Spliterator} to split.
    */

   private static final int DEFAULT_MIN_SPLIT_SIZE = 128;

   /**
    * Keeps the index position of the list cursor.
    */

   private int cursor;

   /**
    * If provided, the <code>keyExtractor</code> is used to extract the map key from the value when the keyless put
    * method is used.
    */

   private final Function<V, K> keyExtractor;

   /**
    * The list used to store {@link Element} objects. The list is used to provide a deterministic iteration order for
    * the map {@link Element}s.
    */

   private final ArrayList<Element<K, V>> list;

   /**
    * Caches the {@link ListView}.
    */

   private transient List<V> listView;

   /**
    * The map used to store the {@link Element} objects. The user provided value is wrapped in an {@link Element} along
    * with the key and list index position the {@link Element} will be stored at. The {@link Element} is stored in this
    * map using the user provided key.
    */

   private final HashMap<K, Element<K, V>> map;

   /**
    * Caches the {@link MapView}.
    */
   private transient Map<K, V> mapView;

   /**
    * The minimum number of remaining {@link Element} objects that must be left for iteration to allow a
    * {@link Spliterator} to split.
    */

   private final int minSplitSize;

   /**
    * A counter used to detect map modification during an iteration.
    */

   private long safety;

   /**
    * Creates a new {@link ListMap} with the {@link ListMap#DEFAULT_INITIAL_CAPACITY},
    * {@link ListMap#DEFAULT_LOAD_FACTOR}, and {@link ListMap#DEFAULT_MIN_SPLIT_SIZE}. {@link ListMap}s created with
    * this constructor will throw a {@link UnsupportedOperationException} when the method {@link ListMap#put(Object)} is
    * called.
    */

   public ListMap() {
      this(ListMap.DEFAULT_INITIAL_CAPACITY, ListMap.DEFAULT_LOAD_FACTOR, ListMap.DEFAULT_MIN_SPLIT_SIZE, null);
   }

   /**
    * Creates a new {@link ListMap} with the specified <code>initialCapacity</code>, <code>loadFactor</code>, and
    * <code>minimumSplitSize</code>. {@link ListMap}s created with this constructor will throw a
    * {@link UnsupportedOperationException} when the method {@link ListMap#put(V)} is called.
    *
    * @param initialCapacity the initial capacity for the internal {@link HashMap} and {@link ArrayList}.
    * @param loadFactor the load factor for the internal {@link HashMap}.
    * @param minimumSplitSize the minimum number of {@link Element}s that must be remaining to iterate to allow a
    * {@link Spliterator} to split.
    * @throws IllegalArgumentException when the initial capacity is negative or the load factor is non-positive.
    */

   public ListMap(int initialCapacity, float loadFactor, int minimumSplitSize) {
      this(initialCapacity, loadFactor, minimumSplitSize, null);
   }

   /**
    * Creates a new {@link ListMap} with the specified <code>initialCapacity</code>, <code>loadFactor</code>, and
    * <code>minimumSplitSize</code>. {@link ListMap}s created with this constructor will throw a
    * {@link UnsupportedOperationException} when the method {@link ListMap#put(V)} is called.
    *
    * @param initialCapacity the initial capacity for the internal {@link HashMap} and {@link ArrayList}.
    * @param loadFactor the load factor for the internal {@link HashMap}.
    * @param minimumSplitSize the minimum number of {@link Element}s that must be remaining to iterate to allow a
    * {@link Spliterator} to split.
    * @param keyExtractor a {@link Function<V,K>} implementation used to extract a key from a value that was passed to
    * the keyless {@link #put} method.
    * @throws IllegalArgumentException when the initial capacity is negative, the load factor is non-positive, or the
    * minimum split size is less than one.
    */

   public ListMap(int initialCapacity, float loadFactor, int minSplitSize, Function<V, K> keyExtractor) {

      if (minSplitSize < 1) {
         throw new IllegalArgumentException("ListMap::new, the parameter \"minSpitSize\" cannot be less than 1.");
      }

      this.map = new HashMap<>(initialCapacity, loadFactor);
      this.list = new ArrayList<>(initialCapacity);
      this.minSplitSize = minSplitSize;
      this.cursor = -1;
      this.safety = 0;
      this.keyExtractor = keyExtractor;
      this.mapView = null;
      this.listView = null;
   }

   /**
    * Gets a {@link Stream} of the {@link ListMap} {@link Element} objects starting at the end of the list and
    * proceeding to the beginning of the list.
    *
    * @param parallel <code>true</code> for parallel {@link Stream} and <code>false</code> for serial {@link Stream}.
    * @return a {@link Stream} of the {@link ListMap} {@link Element} objects in reverse order.
    */

   public Stream<Element<K, V>> backwardsStream(boolean parallel) {
      //@formatter:off
      return
         this
            .getBackwardsSpliterator( )
            .map(
                   ( spliterator ) -> StreamSupport
                                         .stream( spliterator, parallel )
                )
            .orElse( Stream.empty() );
      //@formatter:on
   }

   /**
    * Gets a {@link Stream} of the {@link ListMap} {@link Element} objects starting at the <code>index</code> position
    * and proceeding to the beginning of the list.
    *
    * @param parallel <code>true</code> for parallel {@link Stream} and <code>false</code> for serial {@link Stream}.
    * @return when <code>index</code> is in bounds and not at the start, a {@link Stream} of the {@link ListMap}
    * {@link Element} objects starting at the {@link Element} at the <code>index</code> position in reverse order;
    * otherwise, and empty {@link Stream}.
    */

   public Stream<Element<K, V>> backwardsStream(int index, boolean parallel) {
      //@formatter:off
      return
         this
            .getBackwardsSpliterator( index )
            .map(
                   ( spliterator ) -> StreamSupport
                                         .stream( spliterator, parallel )
                )
            .orElse( Stream.empty() )
            ;
      //@formatter:on
   }

   /**
    * Gets a {@link Stream} of the {@link ListMap} {@link Element} objects starting with the {@Element} that is
    * associated with <code>key</code> and proceeding to the beginning of the list.
    *
    * @param parallel <code>true</code> for parallel {@link Stream} and <code>false</code> for serial {@link Stream}.
    * @return when <code>key</code> is associated with an {@link Element} and that {@link Element} is not at the start,
    * a {@link Stream} of the {@link ListMap} {@link Element}s in reverse order starting with the associated
    * {@link Element}; otherwise, and empty {@link Stream}.
    */

   public Stream<Element<K, V>> backwardsStream(K key, boolean parallel) {
      //@formatter:off
      return
         this
            .getBackwardsSpliterator( key )
            .map(
                   ( spliterator ) -> StreamSupport
                                         .stream( spliterator, parallel )
                )
            .orElse( Stream.empty() )
            ;
      //@formatter:on
   }

   /**
    * Removes all of the entries from the {@link ListMap}.
    *
    * @implNote Clearing the {@link ListMap} does not reduce it's memory size.
    */

   public void clear() {
      this.cursor = -1;
      this.map.clear();
      this.list.clear();
   }

   private Optional<Element<K, V>> findValue(V value) {

      if (Objects.nonNull(this.keyExtractor)) {
         var key = this.keyExtractor.apply(value);
         return Optional.of(this.map.get(key));
      }

      for (var element : this.list) {
         if (element.getValue().equals(value)) {
            return Optional.of(element);
         }
      }

      return Optional.empty();
   }

   /**
    * Gets a {@link Stream} of the {@link ListMap} {@link Element} objects starting at the start of the list and
    * proceeding to the end of the list.
    *
    * @param parallel <code>true</code> for parallel {@link Stream} and <code>false</code> for serial {@link Stream}.
    * @return a {@link Stream} of the {@link ListMap} {@link Element} objects in order.
    */

   public Stream<Element<K, V>> forwardStream(boolean parallel) {
      //@formatter:off
      return
         this
            .getForwardSpliterator(  )
            .map(
                   ( spliterator ) -> StreamSupport
                                         .stream( spliterator, parallel )
                )
            .orElse( Stream.empty() )
            ;
      //@formatter:on
   }

   /**
    * Gets a {@link Stream} of the {@link ListMap} {@link Elements} starting at the <code>index</code> position and
    * proceeding to the end of the list.
    *
    * @param parallel <code>true</code> for parallel {@link Stream} and <code>false</code> for serial {@link Stream}.
    * @return when <code>index</code> is in bounds and not at the end, a {@link Stream} of the {@link ListMap}
    * {@link Elements} starting at the {@link Element} at the <code>index</code> position in order; otherwise, an empty
    * {@link Stream}.
    */

   public Stream<Element<K, V>> forwardStream(int index, boolean parallel) {
      //@formatter:off
      return
         this
            .getForwardSpliterator( index )
            .map(
                   ( spliterator ) -> StreamSupport
                                         .stream( spliterator, parallel )
                )
            .orElse( Stream.empty() )
            ;
      //@formatter:on
   }

   /**
    * Gets a {@link Stream} of the {@link ListMap} {@link Elements} starting with the {@Element} that is associated with
    * <code>key</code> and proceeding to the end of the list.
    *
    * @param parallel <code>true</code> for parallel {@link Stream} and <code>false</code> for serial {@link Stream}.
    * @return when <code>key</code> is associated with an {@link Element} and that {@link Element} is not at the end, a
    * {@link Stream} of the {@link ListMap} {@link Element}s in reverse order starting with the associated
    * {@link Element}; otherwise, and empty {@link Stream}.
    */

   public Stream<Element<K, V>> forwardStream(K key, boolean parallel) {
      //@formatter:off
      return
         this
            .getForwardSpliterator( key )
            .map(
                   ( spliterator ) -> StreamSupport
                                         .stream( spliterator, parallel )
                )
            .orElse( Stream.empty() )
            ;
      //@formatter:on
   }

   /**
    * Gets the value from the specified list index position. When a value exists an the <code>index</code> position, the
    * cursor is set to the <code>index</code> position; otherwise, the cursor is invalidated.
    *
    * @param index the position to get the value from.
    * @return when the index is in bounds, an {@link Optional} with the value from the specified list
    * <code>index</code>; otherwise, and empty @link Optional} is returned.
    */

   public Optional<V> get(int index) {
      if (this.outOfBounds(index)) {
         this.cursor = -1;
         return Optional.empty();
      }

      var element = this.list.get(index);
      this.cursor = index;
      return Optional.of(element.getValue());
   }

   /**
    * Gets the value associated with the specified key. When a value is associated with the <code>key</code>, the list
    * cursor is set to the list position of the element selected by the key; otherwise, the list cursor is invalidated.
    *
    * @param key the key to get the associated value for.
    * @return when a value is associated with the <code>key</code>, an {@link Optional} with the associated value;
    * otherwise, an empty {@link Optional}.
    */

   public Optional<V> get(K key) {

      if (Objects.isNull(key)) {
         return Optional.empty();
      }

      var element = this.map.get(key);

      if (Objects.isNull(element)) {
         this.cursor = -1;
         return Optional.empty();
      }

      this.cursor = element.getPosition();

      //@formatter:off
      assert
           this.inBounds( this.cursor )
         : new Message()
                  .title( "ListMap::get, position from an element is out of bounds.")
                  .indentInc()
                  .segment( "Element", element )
                  .toString();
       //@formatter:off

      return Optional.of(element.getValue());

   }

   /**
    * Gets a {@link Spliterator} that iterates the list {@link Element}s starting with the last position through
    * the beginning of the list. The returned {@link Spliterator} will report the characteristics:
    * <ul>
    * <li>{@link Spliterator#ORDERED},</li>
    * <li>{@link Spliterator#SIZED},</li>
    * <li>{@link Spliterator#NONNULL}, and</li>
    * <li>{@link Spliterator#SUBSIZED}.</li>
    * </ul>
    * The list cursor is not modified by the {@link Spliterator}.
    *
    * @return when the list cursor is in bounds, an {@link Optional} with a {@link Spliterator} of {@link Element}s;
    * otherwise an empty {@link Optional}.
    */

   public Optional<Spliterator<Element<K, V>>> getBackwardsSpliterator() {

      var spliterator = this.getBackwardsSpliterator( this.list.size()-1, 0, this.safety);

      return Optional.ofNullable(spliterator);
   }

   /**
    * Gets a {@link Spliterator} that iterates the list {@link Element}s starting with the list element that is
    * specified by the <code>index</code> through the beginning of the list. The returned {@link Spliterator} will
    * report the characteristics:
    * <ul>
    * <li>{@link Spliterator#ORDERED},</li>
    * <li>{@link Spliterator#SIZED},</li>
    * <li>{@link Spliterator#NONNULL}, and</li>
    * <li>{@link Spliterator#SUBSIZED}.</li>
    * </ul>
    * The list cursor is not modified. The {@link Spliterator} will not modify
    * the list cursor.
    *
    * @return when the <code>index</code> is in bounds, an {@link Optional} with a {@link Spliterator} of
    * {@link Element}s; otherwise an empty {@link Optional}.
    */

   public Optional<Spliterator<Element<K, V>>> getBackwardsSpliterator(int index) {

      if ( this.outOfBounds(index) ) {
         return Optional.empty();
      }

      var spliterator = this.getBackwardsSpliterator( index, 0, this.safety );

      return Optional.ofNullable(spliterator);
   }

   /**
    * Creates a backwards {@link Spliterator} that starts with the {@link Element} at list position <code>start</code>
    * and ends with the {@link Element} at the list position <code>end</code>. The returned {@link Spliterator} will
    * throw an {@link ConcurrentModificationException} if the {@link ListMap#safety} count becomes different that it was
    * when the {@link Spliterator} was created.
    *
    * @param start the start list index.
    * @param end the ending list index.
    * @param safetyMark the list {@link #safety} count at the time the {@link Spliterator} was completed.
    * @return a new forward {@link Spliterator} from <code>start</code> to <code>end</code>.
    * @throw IndexOutOfBoundsException when the <code>start</code> or <code>end</code> positions are not within the
    * current range of the list; or when <code>end</code> is greater than <code>start</code>.
    */

   private Spliterator<Element<K, V>> getBackwardsSpliterator(int start, int end, long safetyMark) {
      //@formatter:off

      if(    ( start <  0                )
          || ( start >= this.list.size() )
          || ( end   >  start            )
          || ( end   <  0                )
        ) {
         return null;
      }

      var spliterator = new Spliterator<Element<K,V>>() {

         /**
          * Initialized to the list element to start iterating from. It is updated each time the iterator
          * advances. It contains the next element to be returned.
          */

         private Element<K,V> currentElement = ListMap.this.list.get(start);

         /**
          * Initialized to the list element that is the last to be returned from the {@link Spliterator}.
          */

         private Element<K,V> lastElement    = ListMap.this.list.get(end);


         /**
          * Saves the {@link ListMap} safety count at the time of the {@link Spliterator}'s creation.
          */

         private final long safety = safetyMark;

         /**
          * {@inheritDoc}
          *
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         @Override
         public int characteristics() {
            this.checkSafety();
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.NONNULL | Spliterator.SUBSIZED;
         }

         /**
          * Compares the safety count at the time the {@link Spliterator} was created with the
          * current safety count of the {@link ListMap}.
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         private void checkSafety() {
            if( this.safety != ListMap.this.safety ) {
               throw
                  new ConcurrentModificationException
                         (
                           "A ListMap forwared spliterator detected a modification of the ListMap."
                         );
            }
         }

         /**
          * @{inheritDoc}
          *
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         @Override
         public long estimateSize() {
            this.checkSafety();
            return this.currentElement.getPosition() - this.lastElement.getPosition() + 1;
         }

         /**
          * {@inheritDoc}
          *
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         @Override
         public boolean tryAdvance(Consumer<? super Element<K, V>> action) {

            this.checkSafety();

            if( Objects.isNull( this.currentElement ) ) {
               return false;
            }

            action.accept( this.currentElement );

            if( this.currentElement == this.lastElement ) {
               this.currentElement = null;
               return true;
            }

            var position = this.currentElement.getPosition();
            this.currentElement = ListMap.this.list.get(--position);
            return true;
         }

         /**
          * {@inheritDoc}
          * <p>
          * When the remaining number of elements to be iterated is less than {@link ListMap#minSplitSize}
          * the {@link Spliterator} will not spit.
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         @Override
         public Spliterator<Element<K, V>> trySplit() {
            this.checkSafety();
            var c  = this.currentElement.getPosition();
            var l  = this.lastElement.getPosition();
            var r  = c - l  + 1;
            var h  = (r >>> 1);

            if( h < ListMap.this.minSplitSize ) {
               return null;
            }

            var ac = c;
            var al = c - h + 1;
            var bc = c - h;
            var bl = l;

            this.currentElement = ListMap.this.list.get(bc);
            this.lastElement    = ListMap.this.list.get(bl);

            return ListMap.this.getBackwardsSpliterator(ac,al,this.safety);
         }

      };

      return spliterator;
      //@formatter:on
   }

   /**
    * Gets a {@link Spliterator} that iterates the list {@link Element}s starting with the list element that is
    * associated with the <code>key</code> through the beginning of the list. The returned {@link Spliterator} will
    * report the characteristics:
    * <ul>
    * <li>{@link Spliterator#ORDERED},</li>
    * <li>{@link Spliterator#SIZED},</li>
    * <li>{@link Spliterator#NONNULL}, and</li>
    * <li>{@link Spliterator#SUBSIZED}.</li>
    * </ul>
    * The list cursor is not modified. The {@link Spliterator} will not modify the list cursor.
    *
    * @return when the map contains an entry for the <code>key</code>, an {@link Optional} with a {@link Spliterator} of
    * {@link Element}s; otherwise an empty {@link Optional}.
    */

   public Optional<Spliterator<Element<K, V>>> getBackwardsSpliterator(K key) {

      var element = this.map.get(key);

      if (Objects.isNull(element)) {
         return Optional.empty();
      }

      var index = element.getPosition();

      return this.getBackwardsSpliterator(index);
   }

   /**
    * Returns the value from the element at the current list cursor position.
    *
    * @return when the list cursor is valid, an {@link Optional} with the value from the list cursor position;
    * otherwise, an empty {@link Optional}.
    */

   public Optional<V> getCurrent() {
      if (this.outOfBounds(this.cursor)) {
         this.cursor = -1;
         return Optional.empty();
      }

      var element = this.list.get(this.cursor);

      //@formatter:off
      assert
           ( element.getPosition() == this.cursor )
         : new Message()
                  .title( "ListMap::getCurrent, position from element at cursor does not match cursor position.")
                  .indentInc()
                  .segment( "Cursor Position", this.cursor )
                  .segment( "Element",         element     )
                  .toString();
      //@formatter:on

      return Optional.of(element.getValue());
   }

   /**
    * Returns the key from the element at the current list cursor position.
    *
    * @return when the list cursor is valid, an {@link Optional} with the key from the list cursor position; otherwise,
    * an empty {@link Optional}.
    */

   public Optional<K> getCurrentKey() {
      if (this.outOfBounds(this.cursor)) {
         this.cursor = -1;
         return Optional.empty();
      }

      var element = this.list.get(this.cursor);

      //@formatter:off
      assert
           ( element.getPosition() == this.cursor )
         : new Message()
                  .title( "ListMap::getCurrentKey, position from element at cursor does not match cursor position.")
                  .indentInc()
                  .segment( "Cursor Position", this.cursor )
                  .segment( "Element",         element     )
                  .toString();
      //@formatter:on

      return Optional.of(element.getKey());
   }

   /**
    * Get the current list cursor position.
    *
    * @return when the list cursor is in range and valid, the list cursor position; otherwise, -1.
    */

   public int getCurrentPosition() {
      return this.inBounds(this.cursor) ? this.cursor : (this.cursor = -1);
   }

   /**
    * Gets the value for the first entry on the list. If the list is not empty, the list cursor is set to the first
    * element of the list; otherwise the list cursor is invalidated.
    *
    * @return when the list is not empty, and {@link Optional} with the value from the first list entry; otherwise, and
    * empty {@link Optional}.
    */

   public Optional<V> getFirst() {
      if (this.list.isEmpty()) {
         this.cursor = -1;
         return Optional.empty();
      }

      var element = this.list.get(0);

      //@formatter:off
      assert
           ( element.getPosition() == 0 )
         : new Message()
                  .title( "ListMap::getFirst, position from element at start of list is not zero.")
                  .indentInc()
                  .segment( "Element", element )
                  .toString();
      //@formatter:on

      this.cursor = element.getPosition();
      return Optional.of(element.getValue());
   }

   /**
    * Gets a {@link Spliterator} that iterates the list {@link Element}s starting with the first position through the
    * end of the list. The returned {@link Spliterator} will report the characteristics:
    * <ul>
    * <li>{@link Spliterator#ORDERED},</li>
    * <li>{@link Spliterator#SIZED},</li>
    * <li>{@link Spliterator#NONNULL}, and</li>
    * <li>{@link Spliterator#SUBSIZED}.</li>
    * </ul>
    * The list cursor is not modified by the {@link Spliterator}.
    *
    * @return when the list cursor is in bounds, an {@link Optional} with a {@link Spliterator} of {@link Element}s;
    * otherwise an empty {@link Optional}.
    */

   public Optional<Spliterator<Element<K, V>>> getForwardSpliterator() {

      var spliterator = this.getForwardSpliterator(0, this.list.size() - 1, this.safety);

      return Optional.ofNullable(spliterator);
   }

   /**
    * Gets a {@link Spliterator} that iterates the list {@link Element}s starting with the list element that is
    * specified by the <code>index</code> through the end of the list. The returned {@link Spliterator} will report the
    * characteristics:
    * <ul>
    * <li>{@link Spliterator#ORDERED},</li>
    * <li>{@link Spliterator#SIZED},</li>
    * <li>{@link Spliterator#NONNULL}, and</li>
    * <li>{@link Spliterator#SUBSIZED}.</li>
    * </ul>
    * The list cursor is not modified. The {@link Spliterator} will not modify the list cursor.
    *
    * @return when the <code>index</code> is in bounds, an {@link Optional} with a {@link Spliterator} of
    * {@link Element}s; otherwise an empty {@link Optional}.
    */

   public Optional<Spliterator<Element<K, V>>> getForwardSpliterator(int index) {

      if (this.outOfBounds(index)) {
         return Optional.empty();
      }

      var spliterator = this.getForwardSpliterator(index, this.list.size() - 1, this.safety);

      return Optional.ofNullable(spliterator);
   }

   /**
    * Creates a forward {@link Spliterator} that starts with the {@link Element} at list position <code>start</code> and
    * ends with the {@link Element} at the list position <code>end</code>. The returned {@link Spliterator} will throw
    * an {@link ConcurrentModificationException} if the {@link ListMap#safety} count becomes different that it was when
    * the {@link Spliterator} was created.
    *
    * @param start the start list index.
    * @param end the ending list index.
    * @param safetyMark the list {@link #safety} count at the time the {@link Spliterator} was completed.
    * @return a new forward {@link Spliterator} from <code>start</code> to <code>end</code>.
    * @throw IndexOutOfBoundsException when the <code>start</code> or <code>end</code> positions are not within the
    * current range of the list; or when <code>end</code> is less than <code>start</code>.
    */

   private Spliterator<Element<K, V>> getForwardSpliterator(int start, int end, long safetyMark) {
      //@formatter:off

      if (this.outOfBounds(start) || this.outOfBounds(end) || (end < start) ) {
         throw new IndexOutOfBoundsException();
      }

      var spliterator = new Spliterator<Element<K, V>>() {

         /**
          * Initialized to the list element to start iterating from. It is updated each time the iterator
          * advances. It contains the next element to be returned.
          */

         private Element<K, V> currentElement = ListMap.this.list.get(start);

         /**
          * Initialized to the list element that is the last to be returned from the {@link Spliterator}.
          */

         private Element<K, V> lastElement = ListMap.this.list.get(end);

         /**
          * Saves the {@link ListMap} safety count at the time of the {@link Spliterator}'s creation.
          */

         private final long safety = safetyMark;

         /**
          * {@inheritDoc}
          *
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         @Override
         public int characteristics() {
            this.checkSafety();
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.NONNULL | Spliterator.SUBSIZED;
         }

         /**
          * Compares the safety count at the time the {@link Spliterator} was created with the
          * current safety count of the {@link ListMap}.
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         private void checkSafety() {
            if (this.safety != ListMap.this.safety) {
               throw
                  new ConcurrentModificationException
                         (
                            "A ListMap forwared spliterator detected a modification of the ListMap."
                         );
            }
         }

         /**
          * @{inheritDoc}
          *
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         @Override
         public long estimateSize() {
            this.checkSafety();
            return this.lastElement.getPosition() - this.currentElement.getPosition() - 1;
         }

         /**
          * {@inheritDoc}
          *
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         @Override
         public boolean tryAdvance(Consumer<? super Element<K, V>> action) {

            this.checkSafety();

            if (Objects.isNull(this.currentElement)) {
               return false;
            }

            action.accept(this.currentElement);

            if (this.currentElement == this.lastElement) {
               this.currentElement = null;
               return true;
            }

            var position = this.currentElement.getPosition();
            this.currentElement = ListMap.this.list.get(++position);
            return true;
         }

         /**
          * {@inheritDoc}
          * <p>
          * When the remaining number of elements to be iterated is less than {@link ListMap#minSplitSize}
          * the {@link Spliterator} will not spit.
          * @throws ConcurrentModificationException when the {@link ListMap#safety} count is not
          * equal to the {@link @Spliterator} safety count.
          */

         @Override
         public Spliterator<Element<K, V>> trySplit() {

            this.checkSafety();

            var c = this.currentElement.getPosition();
            var l = this.lastElement.getPosition();
            var r = l - c + 1;
            var h = (r >>> 1);

            if (h < ListMap.this.minSplitSize) {
               return null;
            }

            var ac = c;
            var al = c + h - 1;
            var bc = c + h;
            var bl = l;

            this.currentElement = ListMap.this.list.get(bc);
            this.lastElement = ListMap.this.list.get(bl);

            return ListMap.this.getForwardSpliterator(ac, al, this.safety);
         }

      };

      return spliterator;
      //@formatter:on
   }

   /**
    * Gets a {@link Spliterator} that iterates the list {@link Element}s starting with the list element that is
    * associated with the <code>key</code> through the end of the list. The returned {@link Spliterator} will report the
    * characteristics:
    * <ul>
    * <li>{@link Spliterator#ORDERED},</li>
    * <li>{@link Spliterator#SIZED},</li>
    * <li>{@link Spliterator#NONNULL}, and</li>
    * <li>{@link Spliterator#SUBSIZED}.</li>
    * </ul>
    * The list cursor is not modified. The {@link Spliterator} will not modify the list cursor.
    *
    * @return when the map contains an entry for the <code>key</code>, an {@link Optional} with a {@link Spliterator} of
    * {@link Element}s; otherwise an empty {@link Optional}.
    */

   public Optional<Spliterator<Element<K, V>>> getForwardSpliterator(K key) {

      var element = this.map.get(key);

      if (Objects.isNull(element)) {
         return Optional.empty();
      }

      var index = element.getPosition();

      return this.getForwardSpliterator(index);
   }

   /**
    * Gets the value for the last entry on the list. If the list is not empty, the list cursor is set to the last
    * element of the list; otherwise the list cursor is invalidated.
    *
    * @return when the list is not empty, and {@link Optional} with the value from the last list entry; otherwise, and
    * empty {@link Optional}.
    */

   public Optional<V> getLast() {
      if (this.list.isEmpty()) {
         this.cursor = -1;
         return Optional.empty();
      }
      var element = this.list.get(this.list.size() - 1);
      this.cursor = element.getPosition();
      return Optional.of(element.getValue());
   }

   /**
    * Moves the cursor one position towards the end of the list and returns the value from the element at the new list
    * cursor position. If the list cursor is at the end of the list, the list cursor is invalidated and an empty
    * {@link Optional} is returned.
    *
    * @return when the list cursor is valid and not at the end of the list, an {@link Optional} with the value form the
    * element on position after the current list cursor; otherwise, an empty {@link Optional}.
    */

   public Optional<V> getNext() {
      if (this.outOfBoundsOrAtEnd(this.cursor)) {
         this.cursor = -1;
         return Optional.empty();
      }
      var element = this.list.get(++this.cursor);
      return Optional.of(element.getValue());
   }

   /**
    * Gets the value from the element one position after the specified index. The list cursor is set to the index + 1.
    * If the index is out of bounds, or at the end of the list, the cursor is invalidated and an empty {@link Optional}
    * will be returned.
    *
    * @param index the list position to get the value from the element after it.
    * @return when the index is in bounds and not at the end of the list, an {@link Optional} with the value form the
    * element one position after the <code>index</code>; otherwise, and empty {@link Optional}.
    */

   public Optional<V> getNext(int index) {
      if (this.outOfBoundsOrAtEnd(index)) {
         this.cursor = -1;
         return Optional.empty();
      }
      this.cursor = index + 1;
      var element = this.list.get(this.cursor);
      return Optional.of(element.getValue());
   }

   /**
    * Gets the value from the element one position after the element associated with the specified <code>key</code>. The
    * list cursor is set to the position after the position of the associated element. If there is not association with
    * the <code>key</code> or the associate element is at the end of the list, the cursor is invalidated and an empty
    * {@link Optional} will be returned.
    *
    * @param key the key of the element to get the value from the element after it.
    * @return when there is an element associated with the <code>key</code> and the associated element is not at the end
    * of the list, an {@link Optional} with the value from the element one position after the associate element;
    * otherwise, an empty {@link Optional}.
    */

   public Optional<V> getNext(K key) {
      var element = this.map.get(key);

      if (Objects.isNull(element)) {
         this.cursor = -1;
         return Optional.empty();
      }

      var position = element.getPosition();

      if (this.outOfBoundsOrAtEnd(position)) {
         this.cursor = -1;
         return Optional.empty();
      }

      this.cursor = position + 1;
      element = this.list.get(this.cursor);

      return Optional.of(element.getValue());
   }

   /**
    * Moves the cursor one position towards the start of the list and returns the value from the element at the new list
    * cursor position. If the list cursor is at the start of the list, the list cursor is invalidated and an empty
    * {@link Optional} is returned.
    *
    * @return when the list cursor is valid and not at the start of the list, an {@link Optional} with the value form
    * the element on position before the current list cursor; otherwise, an empty {@link Optional}.
    */

   public Optional<V> getPrevious() {
      if (this.outOfBoundsOrAtStart(this.cursor)) {
         this.cursor = -1;
         return Optional.empty();
      }
      var element = this.list.get(--this.cursor);
      return Optional.of(element.getValue());
   }

   /**
    * Gets the value from the element one position before the specified index. The list cursor is set to the index - 1.
    * If the index is out of bounds, or at the start of the list, the cursor is invalidated an an empty {@link Optional}
    * will be returned.
    *
    * @param index the list position to get the value from the element before it.
    * @return when the index is in bounds and not at the start of the list, an {@link Optional} with the value form the
    * element one position before the <code>index</code>; otherwise, and empty {@link Optional}.
    */

   public Optional<V> getPrevious(int index) {
      if (this.outOfBoundsOrAtStart(index)) {
         this.cursor = -1;
         return Optional.empty();
      }
      this.cursor = index - 1;
      var element = this.list.get(this.cursor);
      return Optional.of(element.getValue());
   }

   /**
    * Gets the value from the element one position before the element associated with the specified <code>key</code>.
    * The list cursor is set to the position before the position of the associated element. If there is not association
    * with the <code>key</code> or the associate element is at the start of the list, the cursor is invalidated and an
    * empty {@link Optional} will be returned.
    *
    * @param key the key of the element to get the value from the element before it.
    * @return when there is an element associated with the <code>key</code> and the associated element is not at the
    * start of the list, an {@link Optional} with the value from the element one position before the associate element;
    * otherwise, an empty {@link Optional}.
    */

   public Optional<V> getPrevious(K key) {
      var element = this.map.get(key);

      if (Objects.isNull(element)) {
         this.cursor = -1;
         return Optional.empty();
      }

      var position = element.getPosition();

      if (this.outOfBoundsOrAtStart(position)) {
         this.cursor = -1;
         return Optional.empty();
      }

      this.cursor = position - 1;
      element = this.list.get(this.cursor);

      return Optional.of(element.getValue());
   }

   /**
    * Predicate to determine if the <code>index</code> is within the list bounds.
    *
    * @param index the index to test.
    * @return <code>true</code> when the index is within the list bounds; otherwise, <code>false</code>.
    */

   private boolean inBounds(int index) {
      return ((index >= 0) && (index < this.list.size()));
   }

   /**
    * Predicate to determine if the {@link ListMap} is empty.
    *
    * @return <code>true</code> when the {@link ListMap} is empty; otherwise, <code>false</code>.
    */

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   /**
    * Gets a view of the {@link ListMap} implementing the {@link List} interface.
    *
    * @return a {@link List} view.
    */

   public List<V> listView() {
      //@formatter:off
      return
         Objects.nonNull( this.listView )
            ? this.listView
            : ( this.listView = new ListView() );
      //@formatter:off
   }

   /**
    * Gets a view of the {@link ListMap} implementing the {@link Map} interface.
    *
    * @return a {@link Map} view.
    */

   public Map<K,V> mapView() {
      //@formatter:off
      return
         Objects.nonNull( this.mapView )
            ? this.mapView
            : ( this.mapView = new MapView() );
      //@formatter:on
   }

   /**
    * Predicate to determine if the <code>index</code> is outside the list bounds
    *
    * @param index the index to test.
    * @return <code>true</code> when the index is outside the list list bounds otherwise, <code>false</code>.
    */

   private boolean outOfBounds(int index) {
      return ((index < 0) || (index >= this.list.size()));
   }

   /**
    * Predicate to determine if the <code>index</code> is outside the list bounds or at the end of the list.
    *
    * @param index the index to test.
    * @return <code>true</code> when the index is outside the list list bounds or at the end of the list; otherwise,
    * <code>false</code>.
    */

   private boolean outOfBoundsOrAtEnd(int index) {
      return (outOfBounds(index) || (index == this.list.size() - 1));
   }

   /**
    * Predicate to determine if the <code>index</code> is outside the list bounds or at the start of the list.
    *
    * @param index the index to test.
    * @return <code>true</code> when the index is outside the list list bounds or at the start of the list; otherwise,
    * <code>false</code>.
    */

   private boolean outOfBoundsOrAtStart(int index) {
      return (outOfBounds(index) || (index == 0));
   }

   /**
    * The <code>key</code> is associated with the <code>value</code>. If the map already contains an entry for the key,
    * the old value is replaced with the new value and the old value is returned. When a value is replaced, the list
    * index position of the entry for that value does not change. It is a replacement operation and not a remove and
    * put. If an existing value was not replaced, the new entry is added to the end of the list.
    *
    * @param value the value to be added to the {@link ListMap}.
    * @return when an old value was replaced, an {@link Optional} with the old value; otherwise, an empty
    * {@link Optional}.
    * @throws NullPointerException when either of the parameters <code>key</code> or <code>value</code> is
    * <code>null</code>.
    */

   public Optional<V> put(K key, V value) {

      Objects.requireNonNull(key, "ListMap::put, parameter \"key\" cannot be null.");
      Objects.requireNonNull(value, "ListMap::put parameter \"value\" cannot be null.");

      var oldElement = this.map.get(key);

      if (Objects.nonNull(oldElement)) {

         /*
          * Replace the old element with the new data
          */

         var position = oldElement.getPosition();

         var element = new Element<K, V>(key, value, position);

         this.map.put(key, element);
         this.list.set(position, element);
         this.safety++;
         this.cursor = position;

         return Optional.of(oldElement.getValue());
      }

      /*
       * Add a new element
       */

      var position = this.list.size();

      var element = new Element<K, V>(key, value, position);

      this.map.put(key, element);
      this.list.add(element);
      this.safety++;
      this.cursor = position;

      return Optional.empty();

   }

   /**
    * The <code>key</code> is associated with the <code>value</code>. If the map already contains an entry for the key,
    * the old value is replaced with the new value, the old value is returned, and this list position of the entry is
    * not changed. When an existing value was not replaced, the new entry is inserted into the list at the position
    * specified by <code>index</code>.
    *
    * @param key the key for the map entry.
    * @param value the value for the map entry.
    * @param index when a new entry is added to the map, the position on the list for the new entry.
    * @return when an old value was replaced, an {@link Optional} with the old value; otherwise, an empty
    * {@link Optional}.
    * @throws NullPointerException when either of the parameters <code>key</code> or <code>value</code> is
    * <code>null</code>.
    * @throws IndexOutOfBoundsException when the <code>index</code> is less than zero or greater than the size of the
    * {@link ListMap}.
    */

   public Optional<V> put(K key, V value, int index) {

      Objects.requireNonNull(key, "ListMap::put, parameter \"key\" cannot be null.");
      Objects.requireNonNull(value, "ListMap::put parameter \"value\" cannot be null.");

      if ((index < 0) || (index > this.list.size())) {
         throw new IndexOutOfBoundsException("ListMap::put parameter \"index\" is out of bounds.");
      }

      var oldElement = this.map.get(key);

      if (Objects.nonNull(oldElement)) {

         /*
          * Replace the old element with the new data
          */

         var position = oldElement.getPosition();

         var element = new Element<K, V>(key, value, position);

         this.map.put(key, element);
         this.list.set(position, element);
         this.safety++;
         this.cursor = position;

         return Optional.of(oldElement.getValue());
      }

      var element = new Element<K, V>(key, value, index);

      this.map.put(key, element);
      this.list.add(index, element);
      this.safety++;
      this.cursor = index;

      for (var i = index + 1; i < this.list.size(); i++) {
         var oldListElement = this.list.get(i);
         var newListElement = new Element<K, V>(oldListElement.getKey(), oldListElement.getValue(), i);
         this.list.set(i, newListElement);
      }

      return Optional.empty();
   }

   /**
    * Uses the key extractor {@link Function} passed to the constructor to extract a key from the provided
    * <code>value</code> and associates the <code>value</code> with the extracted key. If the map already contains an
    * entry for the key, the old value is replaced with the new value and the old value is returned. When a value is
    * replaced, the list index position of the entry for that value does not change. It is a replacement operation and
    * not a remove and put. If an existing value was not replaced, the new entry is added to the end of the list.
    *
    * @param value the value to be added to the {@link ListMap}.
    * @return when an old value was replaced, an {@link Optional} with the old value; otherwise, an empty
    * {@link Optional}.
    * @throws UnspportedOperationException when a key extractor function is not available.
    * @throws NullPointerException when the parameter <code>value</code> is <code>null</code>.
    */

   public Optional<V> put(V value) {

      if (Objects.isNull(this.keyExtractor)) {
         throw new UnsupportedOperationException("ListMap::put(V value), a key extractor function is not available.");
      }

      Objects.requireNonNull(value, "ListMap::put parameter \"value\" cannot be null.");

      return this.put(this.keyExtractor.apply(value), value);
   }

   /**
    * Adds all of the values from the provided <code>list</code>. The key extractor {@link Function} passed to the
    * constructor is used to extract a key from each of the values on the <code>list</code>. If the map already contains
    * an entry for a key, the old value is replaced with the value from the <code>list</code>.
    *
    * @param list the values to be added to this {@link ListMap}.
    */

   public void putAll(List<? extends V> list) {

      if (Objects.isNull(this.keyExtractor)) {
         throw new UnsupportedOperationException(
            "ListMap::putAll(List<V> list), a key extractor function is not available.");
      }

      list.forEach((value) -> this.put(this.keyExtractor.apply(value), value));
   }

   /**
    * Adds all of the key value pairs from the provided {@link ListMap}. If this {@link ListMap} already contains an
    * entry for a key from the <code>listMap</code>, the value in this {@link ListMap} will be replaced by the value
    * from <code>listMap</code>.
    *
    * @param listMap the mappings to be stored in this {@link ListMap}.
    */

   public void putAll(ListMap<? extends K, ? extends V> listMap) {
      listMap.list.forEach((entry) -> this.put(entry.getKey(), entry.getValue()));
   }

   /**
    * Adds all of the key value pairs from the provided {@link Map}. If the {@link ListMap} already contains an entry
    * for a key from the {@link Map}, <code>map</code>, the value in the {@link ListMap} will be replaced by the value
    * from <code>map</code>.
    *
    * @param map the mappings to be stored in this {@link ListMap}.
    */

   public void putAll(Map<? extends K, ? extends V> map) {
      map.entrySet().forEach((entry) -> this.put(entry.getKey(), entry.getValue()));
   }

   /**
    * Removes the entry associated with the provided <code>key</code> from the {@link ListMap}.
    *
    * @param key the key for the entry to be removed.
    * @return when an entry exists in the {@link ListMap} for the provided <code>key</code>, an {@link Optional} with
    * the value associated with the key; otherwise, an empty {@link Optional}.
    */

   public Optional<V> remove(K key) {

      if (Objects.isNull(key)) {
         return Optional.empty();
      }

      this.cursor = -1;

      var element = this.map.remove(key);

      if (Objects.isNull(element)) {
         return Optional.empty();
      }

      var position = element.getPosition();

      this.list.remove(position);

      return Optional.of(element.getValue());
   }

   /**
    * Gets the number of entries in the {@link ListMap}.
    *
    * @return the number of entries.
    */

   public int size() {
      return this.list.size();
   }

} /* EOF */
