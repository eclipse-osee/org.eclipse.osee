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
import java.util.Optional;

/**
 * An interface for a mutable storage container used to hold a scalar or vector value. Implementations of this interface
 * are intended to be used when the majority of values to be stored are expected to be scalar values. This saves the
 * overhead of setting up a vector storage container for each of the scalar values.
 *
 * @author Loren K. Ashley
 * @param <T> the type of value or values stored.
 * @param <I> an interface that is a specialization of the {@link Collection}&lt;T&gt; interface.
 * @param <C> the implementation type for the {@link Collection}&lt;T&gt; used to store vector values.
 * @implSpec Once a vector storage container is allocated, implementations are not required to free the vector storage
 * even when the removal of values leaves the vector storage container with one or zero values.
 */

public interface ScalarOrVector<T, I extends Collection<T>, C extends I> extends Collection<T> {

   /**
    * Public enumeration used to describe the number of values stored.
    *
    * @implNote This enumeration is used to indicate the number of stored values and not the type of value storage.
    */

   public enum Rank {

      /**
       * Indicates the contents of the store is empty.
       */

      EMPTY,

      /**
       * Indicates the store contains only one value.
       */

      SCALAR,

      /**
       * Indicates the store contains more than one value.
       */

      VECTOR;
   }

   /**
    * Adds the values in <code>other</code> to the {@link ScalarOrVector} implementation.
    *
    * @param other the values to be added.
    * @return <code>true</code> when the contents of the storage container are modified; otherwise, <code>false</code>.
    * @throws NullPointerException when <code>other</code> is <code>null</code>, <code>other</code> contains a
    * <code>null</code> value, or <code>vectorCollectionSupplier</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>other</code> has a rank other than {@link Rank#EMPTY},
    * {@link Rank#SCALAR}, or {@link Rank#VECTOR}.
    */

   boolean addAll(ScalarOrVector<T, I, C> other);

   /**
    * Adds the values in <code>vectorValue</code> to the {@link ScalarOrVector} implementation, possibly using
    * <code>vectorValue</code> as the vector storage container for this object.
    *
    * @param vectorValue the values to be added.
    * @throws NullPointerException when <code>vectorValue</code> is <code>null</code> or contains a <code>null</code>
    * value.
    * @implSpec Implementations of this method are required to use the provided vector store , <code>vectorValue</code>,
    * as the vector storage for the {@link ScalarOrVector} implementation when:
    * <ul>
    * <li>the {@link ScalarOrVector} implementation does not have vector storage, or</li>
    * <li>the vector storage of the {@link ScalarOrVector} implementation contains less values than the number of items
    * in <code>vectorValue</code>.</li>
    * </ul>
    */

   void addAllUnsafe(C vectorValue);

   /**
    * Adds the values in <code>other</code> to the {@link ScalarOrVector} implementation, possibly using the vector
    * storage container form <code>other</code> as the vector storage container for this object.
    *
    * @param other the values to be added.
    * @throws NullPointerException when <code>vectorValue</code> is <code>null</code> or contains a <code>null</code>
    * value.
    * @throws IllegalArgumentException when <code>other</code> is not an instance of {@link AbstractScalarOrVector}; or
    * <code>other</code> has a rank other than {@link Rank#EMPTY}, {@link Rank#SCALAR}, or {@link Rank#VECTOR}.
    * @implSpec Implementations of this method are required to use the vector store from the other
    * {@link ScalarOrVector} implementation as the vector storage for the {@link ScalarOrVector} implementation when
    * <ul>
    * <li>the other {@link ScalarOrVector} implementation vector storage is the same type; and</li>
    * <li>the {@link ScalarOrVector} implementation does not have vector storage, or</li>
    * <li>the vector storage of the {@link ScalarOrVector} implementation contains less values than the number of items
    * in <code>other</code>.</li>
    * </ul>
    */

   void addAllUnsafe(ScalarOrVector<T, I, C> other);

   /**
    * Gets an unmodifiable view implementing the interface &lt;I&gt; of the contents of the {@link ScalarOrVector}
    * implementation. Changes to the {@link ScalarOrVector} implementation will be reflected in the returned view.
    *
    * @return an unmodifiable view of the contents.
    */

   I get();

   /**
    * If the {@link ScalarOrVector} implementation contains one and only one value, returns an {@link Optional}
    * containing the scalar value.
    *
    * @return when the {@link ScalarOrVector} implementation contains one and only one value, an {@link Optional}
    * containing the scalar value; otherwise, an empty {@link Optional}.
    */

   Optional<T> getScalar();

   /**
    * Predicate to determine if the {@link ScalarOrVector} implementation does not contain any values.
    *
    * @return <code>true</code> when the {@link ScalarOrVector} implementation does not contains any values; otherwise,
    * <code>false</code>.
    * @implSpec This method's return value is required to be based upon the number of values in the store and not upon
    * the type of storage.
    */

   @Override
   boolean isEmpty();

   /**
    * Predicate to determine if the {@link ScalarOrVector} implementation contains any values.
    *
    * @return <code>true</code> when the {@link ScalarOrVector} implementation contains at least one value; otherwise,
    * <code>false</code>.
    * @implSpec This method's return value is required to be based upon the number of values in the store and not upon
    * the type of storage.
    */

   boolean isNotEmpty();

   /**
    * Predicate to determine if the {@link ScalarOrVector} implementation contains one and only one value.
    *
    * @return <code>true</code> when the {@link ScalarOrVector} implementation contains one value; otherwise,
    * <code>false</code>.
    * @implSpec This method's return value is required to be based upon the number of values in the store and not upon
    * the type of storage.
    */

   boolean isScalar();

   /**
    * Predicate to determine if the {@link ScalarOrVector} implementation contains more than one value.
    *
    * @return <code>true</code> when the {@link ScalarOrVector} implementation contains more than one value; otherwise,
    * <code>false</code>.
    * @implSpec This method's return value is required to be based upon the number of values in the store and not upon
    * the type of storage.
    */

   boolean isVector();

   /**
    * When the {@link ScalarOrVector} implementation has no values returns {@link Rank#EMPTY}, when the
    * {@link ScalarOrVector} implementation has one value returns {@link Rank#SCALAR}, and when the
    * {@link ScalarOrVector} implementation has more than one value returns {@link Rank#VECTOR}.
    *
    * @return a {@link Rank} enumeration member describing the number of values in the <code>store</code>.
    * @implSpec This method's return value is required to be based upon the number of values in the store and not upon
    * the type of storage.
    */

   Rank rank();

   /**
    * When the {@link ScalarOrVector} implementation has vector storage and contains zero or one values, the vector
    * storage is released. If the vector storage contains a value, the sole value is saved as a scalar.
    */

   void trim();

}

/* EOF */
