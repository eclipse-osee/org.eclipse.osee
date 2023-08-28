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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An implementation of the {@link ScalarOrVector} interface using {@link HashSet} as the vector storage container. This
 * implementation does not support <code>null</code> values or duplicate values.
 *
 * @author Loren K. Ashley
 * @param <T> the type value or values stored.
 */

public class ScalarOrVectorHashSet<T> extends AbstractScalarOrVector<T, Set<T>, HashSet<T>> {

   /**
    * Creates a new empty {@link ScalarOrVectorHashSet} object.
    */

   public ScalarOrVectorHashSet() {
      super();
   }

   /**
    * Creates a new {@link ScalarOrVectorHashSet} object with the provided {@link HashSet}, <code>vectorValue</code>, to
    * be used as the vector storage container.
    *
    * @param vectorValue the vector storage container to be used by the object.
    * @implNote If a new vector storage container is desired instead of using <code>vectorValue</code>, use the no
    * argument constructor and the call {@link #addAll(Collection)} with <code>vectorValue</code> as the parameter.
    */

   public ScalarOrVectorHashSet(HashSet<T> vectorValue) {
      super(vectorValue);
   }

   /**
    * Creates a new {@link ScalarOrVectorHashSet} with the provided <code>scalarValue</code>.
    *
    * @param scalarValue the scalar value.
    */

   public ScalarOrVectorHashSet(T scalarValue) {
      super(scalarValue);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public boolean addAll(ScalarOrVector<T, Set<T>, HashSet<T>> other) {
      return super.addAll(other, HashSet<T>::new);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public boolean add(T value) {
      return super.addScalarNoDuplicates(value, HashSet<T>::new);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public boolean addAll(Collection<? extends T> c) {
      return super.addAll(c, HashSet<T>::new);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public void addAllUnsafe(ScalarOrVector<T, Set<T>, HashSet<T>> other) {
      super.addAllUnsafe(other, HashSet<T>::new);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<T> get() {
      //@formatter:off
      return
         new Set<T>() {

            /**
             * Unmodifiable, operation not supported.
             *
             * @throws UnsupportedOperationException
             */

            @Override
            public boolean add(T t) {
               throw new UnsupportedOperationException();
            }

            /**
             * Unmodifiable, operation not supported.
             *
             * @throws UnsupportedOperationException
             */

            @Override
            public boolean addAll(Collection<? extends T> c) {
               throw new UnsupportedOperationException();
            }

            /**
             * Unmodifiable, operation not supported.
             *
             * @throws UnsupportedOperationException
             */

            @Override
            public void clear() {
               throw new UnsupportedOperationException();
            }

            /**
             * {@inheritDoc}
             */

            @Override
            public boolean contains(Object o) {
               return ScalarOrVectorHashSet.super.contains(o);
            }

            /**
             * {@inheritDoc}
             */

            @Override
            public boolean containsAll(Collection<?> c) {
               return ScalarOrVectorHashSet.super.containsAll(c);
            }

            //equals - default in Object

            //forEach - default in Iterable

            //hashCode - default in Object

            /**
             * {@inheritDoc}
             */

            @Override
            public boolean isEmpty() {
               return ScalarOrVectorHashSet.super.isEmpty();
            }

            /**
             * {@inheritDoc}
             */

            @Override
            public Iterator<T> iterator() {
               return ScalarOrVectorHashSet.super.iterator();
            }

            //parallelStream - default in Collection

            /**
             * Unmodifiable, operation not supported.
             *
             * @throws UnsupportedOperationException
             */

            @Override
            public boolean remove(Object o) {
               throw new UnsupportedOperationException();
            }

            /**
             * Unmodifiable, operation not supported.
             *
             * @throws UnsupportedOperationException
             */

            @Override
            public boolean removeAll(Collection<?> c) {
               throw new UnsupportedOperationException();
            }

            /**
             * Unmodifiable, operation not supported.
             *
             * @throws UnsupportedOperationException
             */

            @Override
            public boolean removeIf(Predicate<? super T> filter) {
               throw new UnsupportedOperationException();
            }

            /**
             * Unmodifiable, operation not supported.
             *
             * @throws UnsupportedOperationException
             */

            @Override
            public boolean retainAll(Collection<?> c) {
               throw new UnsupportedOperationException();
            }

            /**
             * {@inheritDoc}
             */

            @Override
            public int size() {
               return ScalarOrVectorHashSet.super.size();
            }

            //spliterator - default in Collection

            //stream - default in Collection

            /**
             * {@inheritDoc}
             */

            @Override
            public Object[] toArray() {
               return ScalarOrVectorHashSet.super.toArray();
            }

            //<U> U[] toArray(IntFunction<U[]> generator) - default in Collection

            /**
             * {@inheritDoc}
             */

            @Override
            public <A> A[] toArray(A[] a) {
               return ScalarOrVectorHashSet.super.toArray(a);
            }

      };
   }

}

/* EOF */
