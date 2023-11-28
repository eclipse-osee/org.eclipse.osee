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

import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;

/**
 * A container object for three values.
 *
 * @param F the type of the first member of the triplet.
 * @param S the type of the second member of the triplet.
 * @param T the type of the third member of the triplet.
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

public class Triplet<F, S, T> extends Pair<F, S> {
   private static final long serialVersionUID = -3319956950656820062L;

   /**
    * Creates a new {@link Triplet} with non-<code>null</code> values.
    *
    * @param <F> the type of the first object in the {@link Triplet}.
    * @param <S> the type of the second object in the {@link Triplet}.
    * @param <T> the type of the third member of the {@link Triplet}.
    * @param first the first object to store in the new {@link Triplet}.
    * @param second the second object to store in the new {@link Triplet}.
    * @param third the third object to store in the new {@link Triplet}.
    * @return a new {@link Triplet} containing the objects <code>first</code>, <code>second</code>, and
    * <code>third</code>.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public static <F, S, T> @NonNull Triplet<F, S, T> createNonNull(@NonNull F first, @NonNull S second, @NonNull T third) {
      return new Triplet<>(Objects.requireNonNull(first), Objects.requireNonNull(second),
         Objects.requireNonNull(third));
   }

   /**
    * Creates a new immutable {@link Triplet} with non-<code>null</code> values. The setter methods of the returned
    * {@link Triplet} will all throw an {@link UnsupportedOperationException}.
    *
    * @param <F> the type of the first object in the {@link Triplet}.
    * @param <S> the type of the second object in the {@link Triplet}.
    * @param <T> the type of the third member of the {@link Triplet}.
    * @param first the first object to store in the new {@link Triplet}.
    * @param second the second object to store in the new {@link Triplet}.
    * @param third the third object to store in the new {@link Triplet}.
    * @return a new immutable {@link Triplet} containing the objects <code>first</code>, <code>second</code>, and
    * <code>third</code>.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public static <F, S, T> @NonNull Triplet<F, S, T> createNonNullImmutable(@NonNull F first, @NonNull S second, @NonNull T third) {
      return createNullable(Objects.requireNonNull(first), Objects.requireNonNull(second),
         Objects.requireNonNull(third));
   }

   /**
    * Creates a new {@link Triplet} with possibly <code>null</code> values.
    *
    * @param <F> the type of the first object in the {@link Triplet}.
    * @param <S> the type of the second object in the {@link Triplet}.
    * @param <T> the type of the third member of the {@link Triplet}.
    * @param first the first object to store in the new {@link Triplet}.
    * @param second the second object to store in the new {@link Triplet}.
    * @param third the third object to store in the new {@link Triplet}.
    * @return a new {@link Triplet} containing the objects <code>first</code>, <code>second</code>, and
    * <code>third</code>.
    */

   public static <F, S, T> Triplet<F, S, T> createNullable(F first, S second, T third) {
      return new Triplet<>(first, second, third);
   }

   /**
    * Creates a new immutable {@link Triplet} with possibly <code>null</code> values. The setter methods of the returned
    * {@link Triplet} will all throw an {@link UnsupportedOperationException}.
    *
    * @param <F> the type of the first object in the {@link Triplet}.
    * @param <S> the type of the second object in the {@link Triplet}.
    * @param <T> the type of the third member of the {@link Triplet}.
    * @param first the first object to store in the new {@link Triplet}.
    * @param second the second object to store in the new {@link Triplet}.
    * @param third the third object to store in the new {@link Triplet}.
    * @return a new {@link Triplet} containing the objects <code>first</code>, <code>second</code>, and
    * <code>third</code>.
    */

   public static <F, S, T> Triplet<F, S, T> createNullableImmutable(F first, S second, T third) {
      //@formatter:off
      return
         new Triplet<>( first, second, third ) {

            private static final long serialVersionUID = Triplet.serialVersionUID;

            @Override
            public Pair<F, S> set(F first, S second) {
               throw new UnsupportedOperationException();
            }

            @Override
            public Triplet<F, S, T> set(F first, S second, T third) {
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

            @Override
            public void setThird(T third) {
               throw new UnsupportedOperationException();
            }

         };
      //@formatter:on
   }

   /**
    * Saves the third member of the triplet.
    */

   protected T third;

   /**
    * Creates a new {@link Triplet} with <code>null</code> values.
    */

   public Triplet() {
      super();
      this.third = null;
   }

   /**
    * Creates a new {@link Triplet} with the possibly <code>null</code> values <code>first</code>, <code>second</code>,
    * and <code>third</code>.
    *
    * @param first the value for the first member of the {@link Triplet}.
    * @param second the value for the second member of the {@link Triplet}.
    * @param third the value for the third member of the {@link Triplet}.
    */

   public Triplet(F first, S second, T third) {
      super(first, second);
      this.third = third;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof Triplet<?, ?, ?>) {
         Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
         boolean thirdEquals = third == null ? other.third == null : third.equals(other.third);
         result = thirdEquals && super.equals(other);
      }
      return result;
   }

   /**
    * Gets the third value of the {@link Triplet}.
    *
    * @return the third value.
    */

   public T getThird() {
      return this.third;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = super.hashCode();
      if (third != null) {
         result = prime * result + third.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   /**
    * Sets the first, second, and third values of the {@link Triplet}.
    *
    * @param first the value for the first member of the {@link Triplet}.
    * @param second the value for the second member of the {@link Triplet}.
    * @param third the value for the third member of the {@link Triplet}.
    */

   public @NonNull Triplet<F, S, T> set(F first, S second, T third) {
      super.set(first, second);
      setThird(third);
      return this;
   }

   /**
    * Sets the third value of the {@link Triplet}.
    *
    * @param third the value to be set as the third member of the {@link Triplet}.
    */

   public void setThird(T third) {
      this.third = third;
   }

   /**
    * Generates a string representation of the {@link Triplet} using the {@link Object#toString} method of each value.
    * The message is formatted as follows:
    * <p>
    * <code>
    *    "[" &lt;first-to-string&gt; ", " &lt;second-to-string&gt; ", " &lt;third-to-string&gt; "]"
    * </code>
    *
    * @return a {@link String} representation of the {@link Triplet}.
    */

   @Override
   public @NonNull String toString() {
      var firstAsString = String.valueOf(this.first);
      var secondAsString = String.valueOf(this.second);
      var thirdAsString = String.valueOf(this.third);
      return String.format("[%s, %s, %s]", firstAsString, secondAsString, thirdAsString);
   }

   /**
    * Predicate to determine if the {@link Class} of any value is not as expected. A <code>null</code> value is
    * considered as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @param thridClass the expected {@link Class} of the third value.
    * @return <code>false</code> when all values are of the expected {@link Class}; otherwise, <code>true</code>.
    */

   public boolean typesKo(Class<?> firstClass, Class<?> secondClass, Class<?> thirdClass) {
      return !this.typesOk(firstClass, secondClass, thirdClass);
   }

   /**
    * Predicate to determine if the {@link Class} of each value is as expected. A <code>null</code> value is considered
    * as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @param thridClass the expected {@link Class} of the third value.
    * @return <code>true</code> when all values are of the expected {@link Class}; otherwise, <code>false</code>.
    */

   public boolean typesOk(Class<?> firstClass, Class<?> secondClass, Class<?> thirdClass) {
      //@formatter:off
      return
            this.typesOk(firstClass, secondClass)
         && (    Objects.isNull( this.third )
              || secondClass.isAssignableFrom( this.third.getClass() ) );
      //@formatter:on
   }

}

/* EOF */