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
      return createNullableImmutable(Objects.requireNonNull(first), Objects.requireNonNull(second),
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

   public static <F, S, T> @NonNull Triplet<F, S, T> createNullable(@Nullable F first, @Nullable S second, @Nullable T third) {
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

   public static <F, S, T> @NonNull Triplet<F, S, T> createNullableImmutable(@Nullable F first, @Nullable S second, @Nullable T third) {
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

   protected @Nullable T third;

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

   public Triplet(@Nullable F first, @Nullable S second, @Nullable T third) {
      super(first, second);
      this.third = third;
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

      if (!(obj instanceof Triplet<?, ?, ?>)) {
         return false;
      }

      Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;

      boolean thirdEqual;

      if (this.third != null) {
         thirdEqual = this.third.equals(other.third);
      } else {
         thirdEqual = (other.third == null);
      }

      return thirdEqual;
   }

   /**
    * Performs an action upon the second and third {@link Triplet} members when it is non-<code>null</code> and returns
    * the first member.
    *
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @param thirdAction the action to perform on the member {@link #third} when non-<code>null</code>.
    * @throws NullPointerException when the second member is non-<code>null</code> and the {@link Consumer} parameter is
    * <code>null</code>.
    */

   public @Nullable F getFirstIfPresentOthers(@Nullable Consumer<@NonNull S> secondAction, @Nullable Consumer<@NonNull T> thirdAction) {

      Conditions.acceptWhenNonNull(this.second, secondAction);
      Conditions.acceptWhenNonNull(this.third, thirdAction);

      return this.first;
   }

   /**
    * Performs an action upon the second and third {@link Triplet} members when they are non-<code>null</code> and
    * returns the first member.
    *
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @param thirdAction the action to perform on the member {@link #third} when non-<code>null</code>.
    * @throws NullPointerException when
    * <ul>
    * <li>The first member is <code>null</code>.</li>
    * <li>The second member is non-<code>null</code> and the <code>secondAction</code> {@link Consumer} parameter is
    * <code>null</code>.</li>
    * <li>The third member is non-<code>null</code> and the <code>thirdAction</code> {@link Consumer} parameter is
    * <code>null</code>.</li>
    * </ul>
    */

   public @NonNull F getFirstNonNullIfPresentOthers(@Nullable Consumer<@NonNull S> secondAction, @Nullable Consumer<@NonNull T> thirdAction) {

      Conditions.acceptWhenNonNull(this.second, secondAction);
      Conditions.acceptWhenNonNull(this.third, thirdAction);

      return Conditions.requireNonNull(this.first);
   }

   /**
    * Gets the third value of the {@link Triplet}.
    *
    * @return the third value.
    */

   public @Nullable T getThird() {
      return this.third;
   }

   /**
    * Gets the third value of the {@link Triplet}.
    *
    * @return the third value.
    * @throws NullPointerException when the third member is <code>null</code>.
    */

   public @NonNull T getThirdNonNull() {
      return Conditions.requireNonNull(this.third);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {

      return Objects.hash(this.first, this.second, this.third);
   }

   /**
    * Performs an action upon the {@link Triplet} members that are non-<code>null</code>.
    *
    * @param firstAction the action to perform on the member {@link #first} when non-<code>null</code>.
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @param thirdAction the action to perform on the member {@link #third} when non-<code>null</code>.
    * @throws NullPointerException when a member is non-<code>null</code> and the corresponding {@link Consumer}
    * parameter is <code>null</code>.
    */

   public void ifPresent(@Nullable Consumer<@NonNull F> firstAction, @Nullable Consumer<@NonNull S> secondAction, @Nullable Consumer<@NonNull T> thirdAction) {

      Conditions.acceptWhenNonNull(this.first, firstAction);
      Conditions.acceptWhenNonNull(this.second, secondAction);
      Conditions.acceptWhenNonNull(this.third, thirdAction);
   }

   /**
    * Sets the first, second, and third values of the {@link Triplet}.
    *
    * @param first the value for the first member of the {@link Triplet}.
    * @param second the value for the second member of the {@link Triplet}.
    * @param third the value for the third member of the {@link Triplet}.
    */

   public @NonNull Triplet<F, S, T> set(@Nullable F first, @Nullable S second, @Nullable T third) {
      super.set(first, second);
      this.setThird(third);
      return this;
   }

   /**
    * Sets the third value of the {@link Triplet}.
    *
    * @param third the value to be set as the third member of the {@link Triplet}.
    */

   public void setThird(@Nullable T third) {
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
      var result = String.format("[%s, %s, %s]", firstAsString, secondAsString, thirdAsString);
      return Conditions.requireNonNull(result);
   }

   /**
    * Predicate to determine if the {@link Class} of any value is not as expected. A <code>null</code> value is
    * considered as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @param thridClass the expected {@link Class} of the third value.
    * @return <code>false</code> when all values are of the expected {@link Class}; otherwise, <code>true</code>.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public boolean typesKo(@NonNull Class<?> firstClass, @NonNull Class<?> secondClass, @NonNull Class<?> thirdClass) {
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
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public boolean typesOk(@NonNull Class<?> firstClass, @NonNull Class<?> secondClass, @NonNull Class<?> thirdClass) {

      var classOfThird = Conditions.applyWhenNonNull(this.third, (t) -> t.getClass());
      //@formatter:off
      return
            this.typesOk(firstClass, secondClass)
         && (    ( classOfThird == null )
              || secondClass.isAssignableFrom( classOfThird ) );
      //@formatter:on
   }

}

/* EOF */