/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.io.Serializable;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;

/**
 * A container object for two values.
 *
 * @param F the type of the first member of the pair.
 * @param S the type of the second member of the pair.
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

public class Pair<F, S> implements Serializable {

   private static final long serialVersionUID = 1764353834209869140L;

   /**
    * Creates a new {@link Pair} with non-<code>null</code> values.
    *
    * @param <F> the type of the first object in the {@link Pair}.
    * @param <S> the type of the second object in the {@link Pair}.
    * @param first the first object to store in the new {@link Pair}.
    * @param second the second object to store in the new {@link Pair}.
    * @return a new {@link Pair} containing the objects <code>first</code> and <code>second</code>.
    * @throws NullPointerException when either parameter is <code>null</code>.
    */

   public static <F, S> @NonNull Pair<F, S> createNonNull(@NonNull F first, @NonNull S second) {
      return new Pair<>(Objects.requireNonNull(first), Objects.requireNonNull(second));
   }

   /**
    * Creates a new immutable {@link Pair} with non-<code>null</code> values. The setter methods of the returned
    * {@link Pair} will all throw an {@link UnsupportedOperationException}.
    *
    * @param <F> the type of the first object in the {@link Pair}.
    * @param <S> the type of the second object in the {@link Pair}.
    * @param first the first object to store in the new {@link Pair}.
    * @param second the second object to store in the new {@link Pair}.
    * @return a new immutable {@link Pair} containing the objects <code>first</code> and <code>second</code>.
    * @throws NullPointerException when either parameter is <code>null</code>.
    */

   public static <F, S> @NonNull Pair<F, S> createNonNullImmutable(@NonNull F first, @NonNull S second) {
      return createNullableImmutable(Objects.requireNonNull(first), Objects.requireNonNull(second));
   }

   /**
    * Creates a new {@link Pair} with possibly <code>null</code> values.
    *
    * @param <F> the type of the first object in the {@link Pair}.
    * @param <S> the type of the second object in the {@link Pair}.
    * @param first the first object to store in the new {@link Pair}.
    * @param second the second object to store in the new {@link Pair}.
    * @return a new {@link Pair} containing the objects <code>first</code> and <code>second</code>.
    */

   public static <F, S> @NonNull Pair<F, S> createNullable(F first, S second) {
      return new Pair<>(first, second);
   }

   /**
    * Creates a new immutable {@link Pair} with possibly <code>null</code> values. The setter methods of the returned
    * {@link Pair} will all throw an {@link UnsupportedOperationException}.
    *
    * @param <F> the type of the first object in the {@link Pair}.
    * @param <S> the type of the second object in the {@link Pair}.
    * @param first the first object to store in the new {@link Pair}.
    * @param second the second object to store in the new {@link Pair}.
    * @return a new {@link Pair} containing the objects <code>first</code> and <code>second</code>.
    */

   public static <F, S> @NonNull Pair<F, S> createNullableImmutable(F first, S second) {
      //@formatter:off
      return
         new Pair<>( first, second ) {

            private static final long serialVersionUID = Pair.serialVersionUID;

            @Override
            public Pair<F, S> set(F first, S second) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void setFirst(F first) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void setSecond(S second) {
               throw new UnsupportedOperationException();
            }

         };
      //@formatter:on
   }

   /**
    * Saves the first member of the pair.
    */

   protected F first;

   /**
    * Saves the second member of the pair.
    */

   protected S second;

   /**
    * Creates a new {@link Pair} with <code>null</code> values.
    */

   public Pair() {
      this.first = null;
      this.second = null;
   }

   /**
    * Creates a new {@link Pair} with the possibly <code>null</code> values <code>first</code> and <code>second</code>.
    *
    * @param first the value for the first member of the {@link Pair}.
    * @param second the value for the second member of the {@link Pair}.
    */

   public Pair(F first, S second) {
      this.first = first;
      this.second = second;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof Pair<?, ?>) {
         Pair<?, ?> other = (Pair<?, ?>) obj;
         boolean left = first == null ? other.first == null : first.equals(other.first);
         boolean right = second == null ? other.second == null : second.equals(other.second);
         result = left && right;
      }
      return result;
   }

   /**
    * Gets the first value of the {@link Pair}.
    *
    * @return the first value.
    */

   public F getFirst() {
      return this.first;
   }

   /**
    * Gets the second value of the {@link Pair}.
    *
    * @return the second value.
    */

   public S getSecond() {
      return this.second;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = 17;
      if (first != null) {
         result = prime * result + first.hashCode();
      } else {
         result = prime * result;
      }
      if (second != null) {
         result = prime * result + second.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   /**
    * Sets the first and second values of the {@link Pair}.
    *
    * @param first the value for the first member of the {@link Pair}.
    * @param second the value for the second member of the {@link Pair}.
    */

   public @NonNull Pair<F, S> set(F first, S second) {
      this.first = first;
      this.second = second;
      return this;
   }

   /**
    * Sets the first value of the {@link Pair}.
    *
    * @param first the value to be set as the {@link #first} member.
    */

   public void setFirst(F first) {
      this.first = first;
   }

   /**
    * Sets the second value of the {@link Pair}.
    *
    * @param second the value to be set as the {@link #second} member.
    */

   public void setSecond(S second) {
      this.second = second;
   }

   /**
    * Generates a string representation of the {@link Pair} using the {@link Object#toString} method of each value. The
    * message is formatted as follows:
    * <p>
    * <code>
    *    "[" &lt;first-to-string&gt; ", " &lt;second-to-string&gt; "]"
    * </code>
    *
    * @return a {@link String} representation of the {@link Pair}.
    */

   @Override
   public @NonNull String toString() {
      var firstAsString = String.valueOf(this.first);
      var secondAsString = String.valueOf(this.second);
      return String.format("[%s, %s]", firstAsString, secondAsString);
   }

   /**
    * Predicate to determine if the {@link Class} of either value is not as expected. A <code>null</code> value is
    * considered as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @return <code>false</code> when both values are of the expected {@link Class}; otherwise, <code>true</code>.
    */

   public boolean typesKo(Class<?> firstClass, Class<?> secondClass) {
      return !this.typesOk(firstClass, secondClass);
   }

   /**
    * Predicate to determine if the {@link Class} of each value is as expected. A <code>null</code> value is considered
    * as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @return <code>true</code> when both values are of the expected {@link Class}; otherwise, <code>false</code>.
    */

   public boolean typesOk(Class<?> firstClass, Class<?> secondClass) {
      //@formatter:off
      return
            (    Objects.isNull( this.first )
              || firstClass.isAssignableFrom( this.first.getClass() ) )
         && (    Objects.isNull( this.second )
              || secondClass.isAssignableFrom( this.second.getClass() ) );
      //@formatter:on
   }

}

/* EOF */