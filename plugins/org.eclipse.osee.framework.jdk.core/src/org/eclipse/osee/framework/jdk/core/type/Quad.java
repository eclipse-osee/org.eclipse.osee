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
import java.util.function.Consumer;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

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

   public static <F, S, T, Q> @NonNull Quad<F, S, T, Q> createNullable(@Nullable F first, @Nullable S second, @Nullable T third, @Nullable Q fourth) {
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

   public static <F, S, T, Q> @NonNull Quad<F, S, T, Q> createNullableImmutable(@Nullable F first, @Nullable S second, @Nullable T third, @Nullable Q fourth) {
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
            public void setFourth(Q fourth) {
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
    * Saves the fourth member of the quadruplet.
    */

   private @Nullable Q fourth;

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

   public Quad(@Nullable F first, @Nullable S second, @Nullable T third, @Nullable Q fourth) {
      super(first, second, third);
      this.fourth = fourth;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object obj) {

      boolean superEqual = super.equals(obj);

      if (!superEqual) {
         return false;
      }

      if (!(obj instanceof Quad<?, ?, ?, ?>)) {
         return false;
      }

      Quad<?, ?, ?, ?> other = (Quad<?, ?, ?, ?>) obj;

      boolean fourthEqual;

      if (this.fourth != null) {
         fourthEqual = this.fourth.equals(other.fourth);
      } else {
         fourthEqual = (other.fourth == null);
      }

      return fourthEqual;
   }

   /**
    * Performs an action upon the second, third, and fourth {@link Quad} members when it is non-<code>null</code> and
    * returns the first member.
    *
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @param thirdAction the action to perform on the member {@link #third} when non-<code>null</code>.
    * @param fourthAction the action to perform on the member {@link #fourth} when non-<code>null</code>.
    * @throws NullPointerException when the second member is non-<code>null</code> and the {@link Consumer} parameter is
    * <code>null</code>.
    */

   public @Nullable F getFirstIfPresentOthers(@Nullable Consumer<@NonNull S> secondAction, @Nullable Consumer<@NonNull T> thirdAction, @Nullable Consumer<@NonNull Q> fourthAction) {

      Conditions.acceptWhenNonNull(this.second, secondAction);
      Conditions.acceptWhenNonNull(this.third, thirdAction);
      Conditions.acceptWhenNonNull(this.fourth, fourthAction);

      return this.first;
   }

   /**
    * Performs an action upon the second, third, and fourth {@link Quad} members when they are non-<code>null</code> and
    * returns the first member.
    *
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @param thirdAction the action to perform on the member {@link #third} when non-<code>null</code>.
    * @param fourthAction the action to perform on the member {@link #fourth} when non-<code>null</code>.
    * @throws NullPointerException when
    * <ul>
    * <li>The first member is <code>null</code>.</li>
    * <li>The second member is non-<code>null</code> and the <code>secondAction</code> {@link Consumer} parameter is
    * <code>null</code>.</li>
    * <li>The third member is non-<code>null</code> and the <code>thirdAction</code> {@link Consumer} parameter is
    * <code>null</code>.</li>
    * <li>The fourth member is non-<code>null</code> and the <code>fourthAction</code> {@link Consumer} parameter is
    * <code>null</code>.</li>
    * </ul>
    */

   public @NonNull F getFirstNonNullIfPresentOthers(@Nullable Consumer<@NonNull S> secondAction, @Nullable Consumer<@NonNull T> thirdAction, @Nullable Consumer<@NonNull Q> fourthAction) {

      Conditions.acceptWhenNonNull(this.second, secondAction);
      Conditions.acceptWhenNonNull(this.third, thirdAction);
      Conditions.acceptWhenNonNull(this.fourth, fourthAction);

      return Conditions.requireNonNull(this.first);
   }

   /**
    * Gets the fourth value of the {@link Quad}.
    *
    * @return the fourth value.
    */

   public @Nullable Q getFourth() {
      return fourth;
   }

   /**
    * Gets the fourth value of the {@link Quad}.
    *
    * @return the fourth value.
    * @throws NullPointerException when the fourth member is <code>null</code>.
    */

   public @NonNull Q getFourthNonNull() {
      return Conditions.requireNonNull(this.fourth);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {

      return Objects.hash(this.first, this.second, this.third, this.fourth);
   }

   /**
    * Performs an action upon the {@link Quad} members that are non-<code>null</code>.
    *
    * @param firstAction the action to perform on the member {@link #first} when non-<code>null</code>.
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @param thirdAction the action to perform on the member {@link #third} when non-<code>null</code>.
    * @param fourthAction the action to perform on the member {@link #fourth} when non-<code>null</code>.
    * @throws NullPointerException when a member is non-<code>null</code> and the corresponding {@link Consumer}
    * parameter is <code>null</code>.
    */

   public void ifPresent(@Nullable Consumer<@NonNull F> firstAction, @Nullable Consumer<@NonNull S> secondAction, @Nullable Consumer<@NonNull T> thirdAction, @Nullable Consumer<@NonNull Q> fourthAction) {

      Conditions.acceptWhenNonNull(this.first, firstAction);
      Conditions.acceptWhenNonNull(this.second, secondAction);
      Conditions.acceptWhenNonNull(this.third, thirdAction);
      Conditions.acceptWhenNonNull(this.fourth, fourthAction);

   }

   /**
    * Sets the first, second, third, and fourth values of the {@link Quad}.
    *
    * @param first the value for the first member of the {@link Quad}.
    * @param second the value for the second member of the {@link Quad}.
    * @param third the value for the third member of the {@link Quad}.
    * @param fourth the value for the fourth member of the {@link Quad}.
    */

   public @NonNull Quad<F, S, T, Q> set(@Nullable F first, @Nullable S second, @Nullable T third, @Nullable Q fourth) {
      super.set(first, second, third);
      setFourth(fourth);
      return this;
   }

   /**
    * Sets the fourth value of the {@link Quad}.
    *
    * @param fourth the value to be set as the fourth member of the {@link Quad}.
    */

   public void setFourth(@Nullable Q fourth) {
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
      var result = String.format("[%s, %s, %s, %s]", firstAsString, secondAsString, thirdAsString, fourthAsString);
      return Conditions.requireNonNull(result);
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

   public boolean typesKo(@NonNull Class<?> firstClass, @NonNull Class<?> secondClass, @NonNull Class<?> thirdClass, @NonNull Class<?> fourthClass) {
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

   public boolean typesOk(@NonNull Class<?> firstClass, @NonNull Class<?> secondClass, @NonNull Class<?> thirdClass, @NonNull Class<?> fourthClass) {

      var classOfFourth = Conditions.applyWhenNonNull(this.fourth, (q) -> q.getClass());
      //@formatter:off
      return
            this.typesOk(firstClass, secondClass, thirdClass)
         && (    ( classOfFourth != null )
              || secondClass.isAssignableFrom( classOfFourth ) );
      //@formatter:on
   }

}

/* EOF */