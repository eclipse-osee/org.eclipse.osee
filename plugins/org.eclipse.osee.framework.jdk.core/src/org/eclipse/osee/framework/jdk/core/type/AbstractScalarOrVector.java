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

package org.eclipse.osee.framework.jdk.core.type;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * A generic skeletal implementation of a mutable storage container used to hold a scalar or vector value. This
 * implementation prohibits <code>null</code> values. This class is intended to be used when the majority of values to
 * be saved are expected to be scalar values. This saves the overhead of setting up a vector storage container for each
 * of the scalar values.
 *
 * @author Loren K. Ashley
 * @param <T> the type of value or values stored.
 * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
 * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
 * @implNote The factory for creating the vector storage containers is not saved by this class to minimize the storage
 * required for each instance. It is expected that this class will be extended for each type of vector storage
 * container.
 * @implNote Once a vector storage container is allocated it is not freed even when the removal of values leaves the
 * vector storage container with one or zero values. The {@link #trim} method can be used to free the vector storage
 * container when it contains zero or one value. When the vector storage container contains only one value, the
 * {@link #trim} method will save that value as a scalar.
 */

public abstract class AbstractScalarOrVector<T, I extends Collection<T>, C extends I> implements ScalarOrVector<T, I, C> {

   /**
    * Protected enumeration to track the type of object saved in the member {@link #store}.
    */

   protected enum StoreType {

      /**
       * Indicates the contents of the store is <code>null</code>.
       */

      EMPTY {

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addForeignVector(AbstractScalarOrVector<T, I, C> store,
            C vectorValue) {
            store.storeType = StoreType.VECTOR;
            store.store = vectorValue;
            return !vectorValue.isEmpty();
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addScalar(AbstractScalarOrVector<T, I, C> store,
            T scalarValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {
            store.storeType = StoreType.SCALAR;
            store.store = scalarValue;
            return true;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addVector(AbstractScalarOrVector<T, I, C> store,
            Collection<? extends T> vectorValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {
            var collection = vectorCollectionSupplier.get();
            store.storeType = StoreType.VECTOR;
            collection.addAll(vectorValue);
            store.store = collection;
            return true;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> void clear(AbstractScalarOrVector<T, I, C> store) {
            //No action, the store is empty
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>false</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean contains(AbstractScalarOrVector<T, I, C> store, Object o) {
            Objects.requireNonNull(o);
            return false;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean containsAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            return Objects.requireNonNull(c).isEmpty();
         }

         /**
          * {@inheritDoc}
          *
          * @return an empty {@link Optional}.
          */

         @Override
         <T, I extends Collection<T>, C extends I> Optional<T> getScalar(AbstractScalarOrVector<T, I, C> store) {
            return Optional.empty();
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>true</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isEmpty(AbstractScalarOrVector<T, I, C> store) {
            return true;
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>false</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isScalar(AbstractScalarOrVector<T, I, C> store) {
            return false;
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>false</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isVector(AbstractScalarOrVector<T, I, C> store) {
            return false;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> Iterator<T> iterator(AbstractScalarOrVector<T, I, C> store) {
            return new Iterator<T>() {

               @Override
               public boolean hasNext() {
                  return false;
               }

               @Override
               public T next() {
                  throw new NoSuchElementException();
               }

            };
         }

         /**
          * {@inheritDoc}
          *
          * @return {@link Rank#EMPTY}.
          */

         @Override
         <T, I extends Collection<T>, C extends I> Rank rank(AbstractScalarOrVector<T, I, C> store) {
            return Rank.EMPTY;
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>false</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean removeAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            return false;
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>false</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean removeScalar(AbstractScalarOrVector<T, I, C> store,
            Object scalarValue) {
            return false;
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>false</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean retainAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            return false;
         }

         /**
          * {@inheritDoc}
          *
          * @return 0.
          */

         @Override
         <T, I extends Collection<T>, C extends I> int size(AbstractScalarOrVector<T, I, C> store) {
            return 0;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> Object[] toArray(AbstractScalarOrVector<T, I, C> store) {
            return new Object[] {};
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <E, T, I extends Collection<T>, C extends I> E[] toArray(AbstractScalarOrVector<T, I, C> store, E[] a) {
            if (a.length == 0) {
               return a;
            }

            a[0] = null;

            return a;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> void trim(AbstractScalarOrVector<T, I, C> store) {
            //No action, the store is empty
         }

      },

      /**
       * Indicates the contents of the store is an object of type &lt;T&gt;.
       */

      SCALAR {

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addForeignVector(AbstractScalarOrVector<T, I, C> store,
            C vectorValue) {
            var originalSize = vectorValue.size();
            vectorValue.add(store.getStoreScalar());
            store.storeType = StoreType.VECTOR;
            store.store = vectorValue;
            return vectorValue.size() > originalSize;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addScalar(AbstractScalarOrVector<T, I, C> store,
            T scalarValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {
            var collection = Objects.requireNonNull(vectorCollectionSupplier).get();
            store.storeType = StoreType.VECTOR;
            collection.add(store.getStoreScalar());
            collection.add(scalarValue);
            store.store = collection;
            return collection.size() > 1;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addVector(AbstractScalarOrVector<T, I, C> store,
            Collection<? extends T> vectorValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {
            var collection = vectorCollectionSupplier.get();
            var scalar = store.getStoreScalar();
            store.storeType = StoreType.VECTOR;
            collection.add(scalar);
            collection.addAll(vectorValue);
            store.store = collection;
            return collection.size() > 1;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> void clear(AbstractScalarOrVector<T, I, C> store) {
            store.storeType = StoreType.EMPTY;
            store.store = null;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean contains(AbstractScalarOrVector<T, I, C> store, Object o) {
            var scalar = store.getStoreScalar();
            return scalar.equals(o);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean containsAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            if (Objects.isNull(c) || c.isEmpty()) {
               return false;
            }
            var scalar = store.getStoreScalar();
            return c.contains(scalar);
         }

         /**
          * {@inheritDoc}
          *
          * @return an {@link Optional} containing the scalar value.
          */

         @Override
         <T, I extends Collection<T>, C extends I> Optional<T> getScalar(AbstractScalarOrVector<T, I, C> store) {
            return Optional.of(store.getStoreScalar());
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>false</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isEmpty(AbstractScalarOrVector<T, I, C> store) {
            return false;
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>true</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isScalar(AbstractScalarOrVector<T, I, C> store) {
            return true;
         }

         /**
          * {@inheritDoc}
          *
          * @return <code>false</code>.
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isVector(AbstractScalarOrVector<T, I, C> store) {
            return false;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> Iterator<T> iterator(AbstractScalarOrVector<T, I, C> store) {
            return new Iterator<T>() {

               boolean done = false;

               @Override
               public boolean hasNext() {
                  return this.done;
               }

               @Override
               public T next() {
                  if (this.done) {
                     throw new NoSuchElementException();
                  }
                  this.done = true;
                  return store.getStoreScalar();
               }

            };
         }

         /**
          * {@inheritDoc}
          *
          * @return {@link Rank#SCALAR}.
          */

         @Override
         <T, I extends Collection<T>, C extends I> Rank rank(AbstractScalarOrVector<T, I, C> store) {
            return Rank.SCALAR;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean removeAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            if (Objects.nonNull(c) && !c.isEmpty() && c.contains(store.getStoreScalar())) {
               store.storeType = StoreType.EMPTY;
               store.store = null;
               return true;
            }
            return false;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean removeScalar(AbstractScalarOrVector<T, I, C> store,
            Object scalarValue) {
            if (store.store.equals(scalarValue)) {
               store.storeType = StoreType.EMPTY;
               store.store = null;
               return true;
            }
            return false;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean retainAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            if (Objects.nonNull(c) && !c.isEmpty() && !c.contains(store.getStoreScalar())) {
               store.storeType = StoreType.EMPTY;
               store.store = null;
               return true;
            }
            return false;
         }

         /**
          * {@inheritDoc}
          *
          * @return 1.
          */

         @Override
         <T, I extends Collection<T>, C extends I> int size(AbstractScalarOrVector<T, I, C> store) {
            return 1;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> Object[] toArray(AbstractScalarOrVector<T, I, C> store) {
            return new Object[] {store.getStoreScalar()};
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <E, T, I extends Collection<T>, C extends I> E[] toArray(AbstractScalarOrVector<T, I, C> store, E[] a) {
      //@formatter:off
            @SuppressWarnings("unchecked")
            var rv = ( a.length > 0 )
                         ? a
                         : (E[])Array.newInstance(a.getClass().getComponentType(), 1);
            //@formatter:on

            @SuppressWarnings("unchecked")
            var scalar = (E) store.getStoreScalar();

            rv[0] = scalar;

            if (rv.length == 1) {
               return rv;
            }

            rv[1] = null;

            return rv;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> void trim(AbstractScalarOrVector<T, I, C> store) {
            //No action the store is a scalar
         }

      },

      /**
       * Indicates the contents of the store is an object of type {@link Collection}&lt;T&gt;.
       *
       * @implNote When the {@link #StoreType} is {@link StoreType#VECTOR}, there maybe zero, one, or many objects of
       * type &lt;T&gt; saved in the store.
       */

      VECTOR {

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addForeignVector(AbstractScalarOrVector<T, I, C> store,
            C vectorValue) {
            var collection = store.getStoreVector();
            if (collection.size() >= vectorValue.size()) {
               return collection.addAll(vectorValue);
            } else {
               var rv = vectorValue.addAll(collection);
               store.store = vectorValue;
               return rv;
            }
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addScalar(AbstractScalarOrVector<T, I, C> store,
            T scalarValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {
            var collection = store.getStoreVector();
            return collection.add(scalarValue);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean addVector(AbstractScalarOrVector<T, I, C> store,
            Collection<? extends T> vectorValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {
            var collection = store.getStoreVector();
            return collection.addAll(vectorValue);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> void clear(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            collection.clear();
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean contains(AbstractScalarOrVector<T, I, C> store, Object o) {
            if (Objects.isNull(o)) {
               return false;
            }
            var collection = store.getStoreVector();
            return collection.contains(o);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean containsAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            var collection = store.getStoreVector();
            return collection.containsAll(c);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> Optional<T> getScalar(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
      //@formatter:off
            return
               ( collection.size() == 1 )
                  ? Optional.of( collection.iterator().next() )
                  : Optional.empty();
            //@formatter:on
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isEmpty(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            return collection.isEmpty();
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isScalar(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            return collection.size() == 1;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean isVector(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            return collection.size() > 1;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> Iterator<T> iterator(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            return collection.iterator();
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> Rank rank(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            switch (collection.size()) {
               case 0:
                  return Rank.EMPTY;

               case 1:
                  return Rank.SCALAR;

               default:
                  return Rank.VECTOR;
            }
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean removeAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            var collection = store.getStoreVector();
            return collection.removeAll(c);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean removeScalar(AbstractScalarOrVector<T, I, C> store,
            Object scalarValue) {
            var collection = store.getStoreVector();
            return collection.remove(scalarValue);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> boolean retainAll(AbstractScalarOrVector<T, I, C> store,
            Collection<?> c) {
            var collection = store.getStoreVector();
            return collection.retainAll(c);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> int size(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            return collection.size();
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> Object[] toArray(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            return collection.toArray();
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <E, T, I extends Collection<T>, C extends I> E[] toArray(AbstractScalarOrVector<T, I, C> store, E[] a) {
            var collection = store.getStoreVector();
            return collection.toArray(a);
         }

         /**
          * {@inheritDoc}
          */

         @Override
         <T, I extends Collection<T>, C extends I> void trim(AbstractScalarOrVector<T, I, C> store) {
            var collection = store.getStoreVector();
            if (collection.size() == 0) {
               store.storeType = StoreType.EMPTY;
               store.store = null;
               return;
            }
            if (collection.size() == 1) {
               store.storeType = StoreType.SCALAR;
               store.store = collection.iterator().next();
               return;
            }
         }

      };

      /**
       * Adds the values in <code>vectorValue</code> to the <code>store</code>. The provided vector storage container,
       * <code>vectorValue</code>, is retained for use by the <code>store</code> when:
       * <ul>
       * <li>the <code>store</code> is empty,</li>
       * <li>the <code>store</code> contains a scalar value, or</li>
       * <li>the <code>store</code> has a vector storage container will less values than the number of values in
       * <code>vectorValue</code>.</li>
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to add values to.
       * @param vectorValue the values to be added.
       * @return <code>true</code> when the contents of the storage container are modified; otherwise,
       * <code>false</code>.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean addForeignVector(AbstractScalarOrVector<T, I, C> store,
         C vectorValue);

      /**
       * Adds the <code>scalarValue</code> to the <code>store</code>.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to add a scalar value to.
       * @param scalarValue the value to be added.
       * @param vectorCollectionSupplier a supplier used to obtain an empty {@link Collection}&lt;T&gt; to use for
       * vector storage if necessary.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean addScalar(AbstractScalarOrVector<T, I, C> store,
         T scalarValue, VectorCollectionSupplier<C> vectorCollectionSupplier);

      /**
       * Adds the <code>vectorValue</code> to the <code>store</code>. If the <code>store</code> does not already contain
       * a {@link Collection}&lt;T&gt;, the provided <code>vectorValue</code> {@link Collection}&lt;T&gt; will be used
       * as the vector storage container.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to add the vector value to.
       * @param vectorValue the vector value to be added to the store.
       * @implNote If it is desired to insulate the <code>store</code> from the object suppling the vectorValue (
       * {@link Collection}&lt;T&gt; ), the extending class may perform a copy or deep-copy of the vector value before
       * calling the super class method.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean addVector(AbstractScalarOrVector<T, I, C> store,
         Collection<? extends T> vectorValue, VectorCollectionSupplier<C> vectorCollectionSupplier);

      /**
       * Removes all values from the <code>store</code>. This does not free the vector storage container if the
       * <code>store</code> has one.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to remove all values from.
       */

      abstract <T, I extends Collection<T>, C extends I> void clear(AbstractScalarOrVector<T, I, C> store);

      /**
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store
       * @param o
       * @return
       */

      abstract <T, I extends Collection<T>, C extends I> boolean contains(AbstractScalarOrVector<T, I, C> store,
         Object o);

      /**
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store
       * @param c
       * @return
       */

      abstract <T, I extends Collection<T>, C extends I> boolean containsAll(AbstractScalarOrVector<T, I, C> store,
         Collection<?> c);

      /**
       * If the <code>store</code> contains one and only one value, returns an {@link Optional} containing the scalar
       * value.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to get a scalar value from.
       * @return when the <code>store</code> contains one and only one value, an {@link Optional} containing the scalar
       * value; otherwise, an empty {@link Optional}.
       */

      abstract <T, I extends Collection<T>, C extends I> Optional<T> getScalar(AbstractScalarOrVector<T, I, C> store);

      /**
       * Predicate to determine if the <code>store</code> contains any values.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to test for a value.
       * @return <code>true</code> when the <code>store</code> contains at least one value; otherwise,
       * <code>false</code>.
       * @implNote This method's return value is based upon the number of values in the store and not upon the type of
       * storage.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean isEmpty(AbstractScalarOrVector<T, I, C> store);

      /**
       * Predicate to determine if the enumeration member is {@link StoreType#SCALAR}.
       *
       * @return <code>true</code> when the enumeration member is {@link StoreType#SCALAR}; otherwise,
       * <code>false</code>.
       */

      boolean isScalar() {
         return StoreType.SCALAR.equals(this);
      }

      /**
       * Predicate to determine if the <code>store</code> contains one and only one value.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to test for a scalar value.
       * @return <code>true</code> when the <code>store</code> contains one value; otherwise, <code>false</code>.
       * @implNote This method's return value is based upon the number of values in the store and not upon the type of
       * storage.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean isScalar(AbstractScalarOrVector<T, I, C> store);

      /**
       * Predicate to determine if the enumeration member is {@link StoreType#VECTOR}.
       *
       * @return <code>true</code> when the enumeration member is {@link StoreType#VECTOR}; otherwise,
       * <code>false</code>.
       */

      boolean isVector() {
         return StoreType.VECTOR.equals(this);
      }

      /**
       * Predicate to determine if the <code>store</code> contains more than one value.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to test for a vector value.
       * @return <code>true</code> when the <code>store</code> contains more than one value; otherwise,
       * <code>false</code>.
       * @implNote This method's return value is based upon the number of values in the store and not upon the type of
       * storage.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean isVector(AbstractScalarOrVector<T, I, C> store);

      /**
       * Creates an returns an {@link Iterator} over the values in the <code>store</code>.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstartScalarOrVector} object to create an {@link Iterator} for.
       * @return an {@link Iterator} over the objects in the <code>store</code>.
       */

      abstract <T, I extends Collection<T>, C extends I> Iterator<T> iterator(AbstractScalarOrVector<T, I, C> store);

      /**
       * When the <code>store</code> has no values returns {@link Rank#EMPTY}, when the <code>store</code> has one value
       * returns {@link Rank#SCALAR}, and when the <code>store</code> has more than one value returns
       * {@link Rank#VECTOR}.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to categorize the number of values in.
       * @return a {@link Rank} enumeration member describing the number of values in the <code>store</code>.
       * @implNote This method's return value is based upon the number of values in the store and not upon the type of
       * storage.
       */

      abstract <T, I extends Collection<T>, C extends I> Rank rank(AbstractScalarOrVector<T, I, C> store);

      /**
       * Removes all of the values in the {@link Collection} <code>c</code> from the <code>store</code>.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to remove values from.
       * @param c the values to be removed.
       * @return <code>true</code> when the contents of the storage container are modified; otherwise,
       * <code>false</code>.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean removeAll(AbstractScalarOrVector<T, I, C> store,
         Collection<?> c);

      /**
       * Removes the <code>scalarValue</code> from the <code>store</code>. If the <code>store</code> has vector storage
       * and removal of the <code>scalarValue</code> results in the vector storage having zero or one items, the vector
       * storage is not released. If the <code>store</code> does not contain the <code>scalarValue</code>, no action is
       * taken.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to remove a scalar value from.
       * @param scalarValue the value to remove from the <code>store</code>.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean removeScalar(AbstractScalarOrVector<T, I, C> store,
         Object scalarValue);

      /**
       * Removes all of the values in the <code>store</code> except for the values in the {@link Collection}
       * <code>c</code>.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to remove values from.
       * @param c the values to be retained.
       * @return <code>true</code> when the contents of the storage container are modified; otherwise,
       * <code>false</code>.
       */

      abstract <T, I extends Collection<T>, C extends I> boolean retainAll(AbstractScalarOrVector<T, I, C> store,
         Collection<?> c);

      /**
       * Gets the number of values in the <code>store</code>.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to count the values in.
       * @return the number of values in the <code>store</code>.
       */

      abstract <T, I extends Collection<T>, C extends I> int size(AbstractScalarOrVector<T, I, C> store);

      /**
       * Returns an array containing all of the values in the <code>store</code>.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to get the values from.
       * @return an array containing all of the values from the <code>store</code>.
       */

      abstract <T, I extends Collection<T>, C extends I> Object[] toArray(AbstractScalarOrVector<T, I, C> store);

      /**
       * Returns an array containing all of the values in the <code>store</code>. If the values fit in the provided
       * array, <code>a</code>, that array is returned. When the array, <code>a</code>, has extra space, a
       * <code>null</code> is inserted into the array after the last value. If the array, <code>a</code> is to small, a
       * new array is allocated and returned.
       *
       * @param <E> the type of the array <code>a</code> elements.
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to get the values from.
       * @param a when large enough the values from the <code>store</code> are saved into this array; otherwise, a new
       * array with sufficient size of the same type is allocated.
       * @return an array containing all of the values from the <code>store</code>.
       */

      abstract <E, T, I extends Collection<T>, C extends I> E[] toArray(AbstractScalarOrVector<T, I, C> store, E[] a);

      /**
       * Releases the vector storage if the <code>store</code> does not have more than one value.
       *
       * @param <T> the type of value or values stored.
       * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
       * @param <C> the type of {@link Collection} used to store multiple values of type &lt;T&gt;.
       * @param store the {@link AbstractScalarOrVector} object to release vector storage from if it is not needed.
       */

      abstract <T, I extends Collection<T>, C extends I> void trim(AbstractScalarOrVector<T, I, C> store);
   }

   /**
    * A functional interface for a supplier of vector storage containers of type &lt;C&gt;.
    *
    * @param <C> the vector storage container type.
    */

   @FunctionalInterface
   protected interface VectorCollectionSupplier<C> {

      /**
       * Gets a new empty vector storage container.
       *
       * @return an empty vector storage container.
       */

      C get();
   }

   /**
    * Saves the scalar object or the vector container for multiple objects.
    */

   private Object store;

   /**
    * Enumeration indicates whether {@link #store} is empty, contains a scalar object, or contains a vector storage
    * container.
    */

   private StoreType storeType;

   /**
    * Creates a new empty storage container.
    */

   protected AbstractScalarOrVector() {
      this.storeType = StoreType.EMPTY;
      this.store = null;
   }

   /**
    * Creates a new storage container with the <code>vectorValue</code>. When <code>vectorValue</code> is
    * <code>null</code> or empty, an empty storage container will be created. When <code>vectorValue</code> is
    * non-<code>null</code> and non-empty, the provided vector storage container will be used to save the vector value.
    * A new vector storage container will not be created. When <code>vectorValue</code> is <code>null</code> or empty,
    * an empty storage container will be created.
    *
    * @param scalarValue the value to be saved in the storage container.
    * @implNote If it is desired to insulate the storage container used from the provider, the extending class needs to
    * implement a copy or deep-copy of the provided vector storage container before calling this super class
    * constructor. An alternative is to use the no argument constructor and the {@link #addAll(Collection)} method.
    * @throws NullPointerException when <code>vectorValue</code> contains a <code>null</code> value.
    */

   protected AbstractScalarOrVector(C vectorValue) {

      validateCollection(vectorValue);

      if (!vectorValue.isEmpty()) {
         this.storeType = StoreType.VECTOR;
         this.store = vectorValue;
      } else {
         this.storeType = StoreType.EMPTY;
         this.store = null;
      }
   }

   /**
    * Creates a new storage container with the <code>scalarValue</code>. When <code>scalarValue</code> is
    * <code>null</code>, an empty storage container will be created.
    *
    * @param scalarValue the value to be saved in the storage container.
    */

   protected AbstractScalarOrVector(T scalarValue) {

      if (Objects.nonNull(scalarValue)) {
         this.storeType = StoreType.SCALAR;
         this.store = scalarValue;
      } else {
         this.storeType = StoreType.EMPTY;
         this.store = null;
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when <code>scalarValue</code> is <code>null</code>.
    * @implNote Implementations should use the method {@link #addScalarAllowDuplicates} or
    * {@link #addScalarNoDuplicates} to implement this method depending upon whether the vector storage container
    * implementation being used allows duplicate values or not.
    */

   @Override
   public abstract boolean add(T scalarValue);

   /**
    * {@inheritDoc}
    */

   @Override
   public abstract boolean addAll(Collection<? extends T> c);

   /**
    * Adds all of the value in <code>vectorValue</code> to this object.
    *
    * @param vectorValue the {@link Collection} to get values from.
    * @param vectorCollectionSupplier a supplier of vector storage containers.
    * @return <code>true</code> when the contents of the storage container are modified; otherwise, <code>false</code>.
    * @throws NullPointerException when <code>vectorValue</code> is <code>null</code>, <code>vectorValue</code> contains
    * a <code>null</code> value, or <code>vectorCollectionSupplier</code> is <code>null</code>.
    */

   protected boolean addAll(Collection<? extends T> vectorValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {

      validateCollection(vectorValue);
      Objects.requireNonNull(vectorCollectionSupplier);

      return this.storeType.addVector(this, vectorValue, vectorCollectionSupplier);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public abstract boolean addAll(ScalarOrVector<T, I, C> other);

   /**
    * Adds all of the values in <code>other</code> to this object.
    *
    * @param other the {@link ScalarOrVector} implementation to get values from.
    * @param vectorCollectionSupplier a supplier of vector storage containers.
    * @return <code>true</code> when the contents of the storage container are modified; otherwise, <code>false</code>.
    * @throws NullPointerException when <code>other</code> is <code>null</code>, <code>other</code> contains a
    * <code>null</code> value, or <code>vectorCollectionSupplier</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>other</code> has a rank other than {@link Rank#EMPTY},
    * {@link Rank#SCALAR}, or {@link Rank#VECTOR}.
    */

   protected boolean addAll(ScalarOrVector<T, I, C> other, VectorCollectionSupplier<C> vectorCollectionSupplier) {

      validateCollection(other);
      Objects.requireNonNull(vectorCollectionSupplier);

      switch (other.rank()) {

         case EMPTY: {
            return false;
         }

         case SCALAR: {
            var otherScalar = other.getScalar().orElseThrow(NullPointerException::new);
            return this.storeType.addScalar(this, otherScalar, vectorCollectionSupplier);
         }

         case VECTOR: {
            return this.storeType.addVector(this, other, vectorCollectionSupplier);
         }
      }

      throw new IllegalStateException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public void addAllUnsafe(C vectorValue) {

      validateCollection(vectorValue);

      this.storeType.addForeignVector(this, Objects.requireNonNull(vectorValue));
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public abstract void addAllUnsafe(ScalarOrVector<T, I, C> other);

   /**
    * Adds the values in <code>other</code> to this object, possibly using the vector storage container form
    * <code>other</code> as the vector storage container for this object. The vector store from <code>other</code> will
    * be used as the vector storage for this object when:
    * <ul>
    * <li>the vector storage container type of <code>other</code> is the same type as the vector storage container type
    * for this object; and</li>
    * <li>this object does not have vector storage, or</li>
    * <li>the vector storage of <code>other</code> contains less values than the number of items in this object.</li>
    * </ul>
    *
    * @param other the values to be added.
    * @param vectorCollectionSupplier a supplier of vector storage containers.
    * @throws NullPointerException when <code>vectorValue</code> is <code>null</code> or contains a <code>null</code>
    * value.
    * @throws IllegalArgumentException when <code>other</code> is not an instance of {@link AbstractScalarOrVector}; or
    * <code>other</code> has a rank other than {@link Rank#EMPTY}, {@link Rank#SCALAR}, or {@link Rank#VECTOR}.
    */

   protected void addAllUnsafe(ScalarOrVector<T, I, C> other, VectorCollectionSupplier<C> vectorCollectionSupplier) {

      validateCollection(other);
      Objects.requireNonNull(vectorCollectionSupplier);

      if (!(other instanceof AbstractScalarOrVector)) {
         throw new IllegalArgumentException();
      }

      var otherAbstractScalarOrVector = (AbstractScalarOrVector<T, I, C>) other;

      switch (otherAbstractScalarOrVector.storeType) {
         case EMPTY:
            return;

         case SCALAR:
            this.storeType.addScalar(this, otherAbstractScalarOrVector.getStoreScalar(), vectorCollectionSupplier);
            return;

         case VECTOR:
            this.storeType.addForeignVector(this, otherAbstractScalarOrVector.getStoreVector());
            return;
      }

      throw new IllegalStateException();

   }

   /**
    * Adds the <code>scalarValue</code> to the store.
    *
    * @param scalarValue the value to be added.
    * @param vectorCollectionSupplier a supplier of vector storage containers.
    * @return <code>true</code> when the contents of the storage are modified; otherwise, <code>false</code>.
    * @throw NullPointerException when <code>scalarValue</code> or <code>vectorCollectionSupplier</code> are
    * <code>null</code>.
    * @impNote The method is intended to be used to implement the {@link #add(Object)} method when the implementation of
    * the vector store being used allows duplicate values.
    */

   protected boolean addScalarAllowDuplicates(T scalarValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {

      Objects.requireNonNull(scalarValue);
      Objects.requireNonNull(vectorCollectionSupplier);

      return this.storeType.addScalar(this, scalarValue, vectorCollectionSupplier);
   }

   /**
    * Adds the <code>scalarValue</code> to the store.
    *
    * @param scalarValue the value to be added.
    * @param vectorCollectionSupplier a supplier of vector storage containers.
    * @return <code>true</code> when the contents of the storage are modified; otherwise, <code>false</code>.
    * @throw NullPointerException when <code>scalarValue</code> or <code>vectorCollectionSupplier</code> are
    * <code>null</code>.
    * @impNote The method is intended to be used to implement the {@link #add(Object)} method when the implementation of
    * the vector store being used does not allow duplicate values.
    */

   protected boolean addScalarNoDuplicates(T scalarValue, VectorCollectionSupplier<C> vectorCollectionSupplier) {

      Objects.requireNonNull(scalarValue);
      Objects.requireNonNull(vectorCollectionSupplier);

      //@formatter:off
      if(    this.storeType.isScalar()
          && store.equals(scalarValue) ) {
         return false;
      }
      //@formatter:on

      return this.storeType.addScalar(this, scalarValue, vectorCollectionSupplier);
   }

   /**
    * {@inheritDoc}
    * <p>
    * If this object has a vector storage container it will be emptied but not released.
    */

   @Override
   public void clear() {
      this.storeType.clear(this);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean contains(Object o) {
      return this.storeType.contains(this, o);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public boolean containsAll(Collection<?> c) {

      validateCollection(c);

      return this.storeType.containsAll(this, c);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public abstract I get();

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<T> getScalar() {
      return this.storeType.getScalar(this);
   }

   /**
    * Gets the store value as a scalar.
    *
    * @return the store value as a scalar.
    * @implNote This method performs an assertion check that the member {@link #storeType} is {@link StoreType#SCALAR}.
    * It also takes care of the casting from the member {@link #store} which is of type {@link Object} to the scalar
    * type &lt;T&gt;.
    */

   private T getStoreScalar() {

      assert this.storeType.isScalar();

      @SuppressWarnings("unchecked")
      var scalar = (T) this.store;

      return scalar;
   }

   /**
    * Gets the store as a vector storage container.
    *
    * @return the store as a vector storage container.
    * @implNote This method performs an assertion check that the member {@link #storeType} is {@link StoreType#VECTOR}.
    * It also takes care of the casting from the member {@link #store} which is of type {@link Object} to the vector
    * storage container type &lt;C&gt;.
    */

   private C getStoreVector() {

      assert this.storeType.isVector();

      @SuppressWarnings("unchecked")
      var vector = (C) this.store;

      return vector;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isEmpty() {
      return this.storeType.isEmpty(this);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isNotEmpty() {
      return !this.storeType.isEmpty(this);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isScalar() {
      return this.storeType.isScalar(this);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isVector() {
      return this.storeType.isVector(this);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Iterator<T> iterator() {
      return this.storeType.iterator(this);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Rank rank() {
      return this.storeType.rank(this);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public boolean remove(Object scalarValue) {

      Objects.requireNonNull(scalarValue);

      return this.storeType.removeScalar(this, scalarValue);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public boolean removeAll(Collection<?> c) {

      validateCollection(c);

      return this.storeType.removeAll(this, c);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public boolean retainAll(Collection<?> c) {

      validateCollection(c);

      return this.storeType.retainAll(this, c);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.storeType.size(this);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Object[] toArray() {
      return this.storeType.toArray(this);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public <E> E[] toArray(E[] a) {

      Objects.requireNonNull(a);

      return this.storeType.toArray(this, a);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void trim() {
      this.storeType.trim(this);
   }

   /**
    * Validates that a collection is non-<code>null</code> and does not contain any <code>null</code> values.
    *
    * @param c the {@link Collection} to test.
    */

   private void validateCollection(Collection<?> c) {

      Objects.requireNonNull(c);

      if (c.isEmpty()) {
         return;
      }

      boolean collectionIsOk = true;

      try {

         if (c.contains(null)) {
            /*
             * Collection allows and contains a null value. The collection is KO.
             */
            collectionIsOk = false;
         }

      } catch (NullPointerException e) {
         /*
          * Collection does not support null values. Eat the exception, the collection is OK.
          */
      }

      if (!collectionIsOk) {
         /*
          * Collection contains a null value.
          */

         throw new NullPointerException();
      }
   }

}

/* EOF */
