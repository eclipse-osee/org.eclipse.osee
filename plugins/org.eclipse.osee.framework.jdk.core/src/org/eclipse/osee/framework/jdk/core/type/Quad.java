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
 * A container object for four values.
 *
 * @param F the type of the first member of the quadruplet.
 * @param S the type of the second member of the quadruplet.
 * @param T the type of the third member of the quadruplet.
 * @param Q the type of the fourth member of the quadruplet.
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

public class Quad<F, S, T, Q> extends Triplet<F, S, T> {
   private static final long serialVersionUID = -3319956950656820062L;

   /**
    * Creates a new {@link Quad} with non-<code>null</code> values.
    *
    * @param <F> the type of the first object in the {@link Quad}.
    * @param <S> the type of the second object in the {@link Quad}.
    * @param <T> the type of the third member of the {@link Quad}.
    * @param <Q> the type of the fourth member of the {@link Quad}.
    * @param first the first object to store in the new {@link Quad}.
    * @param second the second object to store in the new {@link Quad}.
    * @param third the third object to store in the new {@link Quad}.
    * @param fourth the fourth object to store in the new {@link Quad}.
    * @return a new {@link Quad} containing the objects <code>first</code>, <code>second</code>, <code>third</code>, and
    * <code>fourth</code>.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public static <F, S, T, Q> @NonNull Quad<F, S, T, Q> createNonNull(@NonNull F first, @NonNull S second, @NonNull T third, @NonNull Q fourth) {
      return new Quad<>(Objects.requireNonNull(first), Objects.requireNonNull(second), Objects.requireNonNull(third),
         Objects.requireNonNull(fourth));
   }

   /**
    * Creates a new immutable {@link Quad} with non-<code>null</code> values. The setter methods of the returned
    * {@link Quad} will all throw an {@link UnsupportedOperationException}.
    *
    * @param <F> the type of the first object in the {@link Quad}.
    * @param <S> the type of the second object in the {@link Quad}.
    * @param <T> the type of the third member of the {@link Quad}.
    * @param <Q> the type of the fourth member of the {@link Quad}.
    * @param first the first object to store in the new {@link Quad}.
    * @param second the second object to store in the new {@link Quad}.
    * @param third the third object to store in the new {@link Quad}.
    * @param fourth the fourth object to store in the new {@link Quad}.
    * @return a new immutable {@link Quad} containing the objects <code>first</code>, <code>second</code>, and
    * <code>third</code>.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public static <F, S, T, Q> @NonNull Quad<F, S, T, Q> createNonNullImmutable(@NonNull F first, @NonNull S second, @NonNull T third, @NonNull Q fourth) {
      return createNullable(Objects.requireNonNull(first), Objects.requireNonNull(second),
         Objects.requireNonNull(third), Objects.requireNonNull(fourth));
   }

   /**
    * Creates a new {@link Quad} with possibly <code>null</code> values.
    *
    * @param <F> the type of the first object in the {@link Quad}.
    * @param <S> the type of the second object in the {@link Quad}.
    * @param <T> the type of the third member of the {@link Quad}.
    * @param <Q> the type of the fourth member of the {@link Quad}.
    * @param first the first object to store in the new {@link Quad}.
    * @param second the second object to store in the new {@link Quad}.
    * @param third the third object to store in the new {@link Quad}.
    * @param fourth the fourth object to store in the new {@link Quad}.
    * @return a new {@link Quad} containing the objects <code>first</code>, <code>second</code>, <code>third</code>, and
    * <code>fourth</code>.
    */

   public static <F, S, T, Q> @NonNull Quad<F, S, T, Q> createNullable(F first, S second, T third, Q fourth) {
      return new Quad<>(first, second, third, fourth);
   }

   /**
    * Creates a new immutable {@link Quad} with possibly <code>null</code> values. The setter methods of the returned
    * {@link Quad} will all throw an {@link UnsupportedOperationException}.
    *
    * @param <F> the type of the first object in the {@link Quad}.
    * @param <S> the type of the second object in the {@link Quad}.
    * @param <T> the type of the third member of the {@link Quad}.
    * @param <Q> the type of the fourth member of the {@link Quad}.
    * @param first the first object to store in the new {@link Quad}.
    * @param second the second object to store in the new {@link Quad}.
    * @param third the third object to store in the new {@link Quad}.
    * @param fourth the fourth object to store in the new {@link Quad}.
    * @return a new {@link Quad} containing the objects <code>first</code>, <code>second</code>, <code>third</code>, and
    * <code>fourth</code>.
    */

   public static <F, S, T, Q> @NonNull Quad<F, S, T, Q> createNullableImmutable(F first, S second, T third, Q fourth) {
      //@formatter:off
      return
         new Quad<>( first, second, third, fourth ) {

            private static final long serialVersionUID = 1L;

            @Override
            public Pair<F, S> set(F first, S second) {
               throw new UnsupportedOperationException();
            }

            @Override
            public Triplet<F, S, T> set(F first, S second, T third) {
               throw new UnsupportedOperationException();
            }

            @Override
            public Quad<F, S, T, Q> set(F first, S second, T third, Q fourth) {
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

            @Override
            public void setFourth(Q fourth) {
               throw new UnsupportedOperationException();
            }
         };
      //@formatter:on
   }

   /**
    * Saves the fourth member of the quadruplet.
    */

   private Q fourth;

   /**
    * Creates a new {@link Quad} with <code>null</code> values.
    */

   public Quad() {
      super();
      this.fourth = null;
   }

   /**
    * Creates a new {@link Quad} with the possibly <code>null</code> values <code>first</code>, <code>second</code>,
    * <code>third</code>, and <code>fourth</code>.
    *
    * @param first the value for the first member of the {@link Quad}.
    * @param second the value for the second member of the {@link Quad}.
    * @param third the value for the third member of the {@link Quad}.
    * @param fourth the value for the fourth member of the {@link Quad}.
    */

   public Quad(F first, S second, T third, Q fourth) {
      super(first, second, third);
      this.fourth = fourth;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof Quad<?, ?, ?, ?>) {
         Quad<?, ?, ?, ?> other = (Quad<?, ?, ?, ?>) obj;
         boolean fourthEquals = fourth == null ? other.fourth == null : fourth.equals(other.fourth);
         result = fourthEquals && super.equals(other);
      }
      return result;
   }

   /**
    * Gets the fourth value of the {@link Quad}.
    *
    * @return the fourth value.
    */

   public Q getFourth() {
      return fourth;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = super.hashCode();
      if (fourth != null) {
         result = prime * result + fourth.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   /**
    * Sets the first, second, third, and fourth values of the {@link Quad}.
    *
    * @param first the value for the first member of the {@link Quad}.
    * @param second the value for the second member of the {@link Quad}.
    * @param third the value for the third member of the {@link Quad}.
    * @param fourth the value for the fourth member of the {@link Quad}.
    */

   public @NonNull Quad<F, S, T, Q> set(F first, S second, T third, Q fourth) {
      super.set(first, second, third);
      setFourth(fourth);
      return this;
   }

   /**
    * Sets the fourth value of the {@link Quad}.
    *
    * @param fourth the value to be set as the fourth member of the {@link Quad}.
    */

   public void setFourth(Q fourth) {
      this.fourth = fourth;
   }

   /**
    * Generates a string representation of the {@link Triplet} using the {@link Object#toString} method of each value.
    * The message is formatted as follows:
    * <p>
    * <code>
    *    "[" &lt;first-to-string&gt; ", " &lt;second-to-string&gt; ", " &lt;third-to-string&gt;  ", " &lt;fourth-to-string&gt; "]"
    * </code>
    *
    * @return a {@link String} representation of the {@link Triplet}.
    */

   @Override
   public String toString() {
      var firstAsString = String.valueOf(this.first);
      var secondAsString = String.valueOf(this.second);
      var thirdAsString = String.valueOf(this.third);
      var fourthAsString = String.valueOf(this.fourth);
      return String.format("[%s, %s, %s, %s]", firstAsString, secondAsString, thirdAsString, fourthAsString);
   }

   /**
    * Predicate to determine if the {@link Class} of any value is not as expected. A <code>null</code> value is
    * considered as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @param thridClass the expected {@link Class} of the third value.
    * @param fourthClass the expected {@link Class} of the fourth value.
    * @return <code>false</code> when all values are of the expected {@link Class}; otherwise, <code>true</code>.
    */

   public boolean typesKo(Class<?> firstClass, Class<?> secondClass, Class<?> thirdClass, Class<?> fourthClass) {
      return !this.typesOk(firstClass, secondClass, thirdClass, fourthClass);
   }

   /**
    * Predicate to determine if the {@link Class} of each value is as expected. A <code>null</code> value is considered
    * as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @param thridClass the expected {@link Class} of the third value.
    * @param fourthClass the expected {@link Class} of the fourth value.
    * @return <code>true</code> when all values are of the expected {@link Class}; otherwise, <code>false</code>.
    */

   public boolean typesOk(Class<?> firstClass, Class<?> secondClass, Class<?> thirdClass, Class<?> fourthClass) {
      //@formatter:off
      return
            this.typesOk(firstClass, secondClass, thirdClass)
         && (    Objects.isNull( this.fourth )
              || secondClass.isAssignableFrom( this.fourth.getClass() ) );
      //@formatter:on
   }

}

/* EOF */