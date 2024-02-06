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
import java.util.function.Consumer;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * A container object for two values.
 *
 * @param F the type of the first member of the pair.
 * @param S the type of the second member of the pair.
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

public class Pair<F, S> implements Serializable {

   /**
    * A static empty immutable {Pair} literal.
    */

   private static @NonNull Pair<?, ?> EMPTY = Pair.createNullableImmutable(null, null);

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

   public static <F, S> @NonNull Pair<F, S> createNullable(@Nullable F first, @Nullable S second) {
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

   public static <F, S> @NonNull Pair<F, S> createNullableImmutable(@Nullable F first, @Nullable S second) {
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

   @SuppressWarnings("unchecked")
   public static <F, S> @NonNull Pair<F, S> empty() {
      return (Pair<F, S>) Pair.EMPTY;
   }

   /**
    * Saves the first member of the pair.
    */

   protected @Nullable F first;

   /**
    * Saves the second member of the pair.
    */

   protected @Nullable S second;

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

   public Pair(@Nullable F first, @Nullable S second) {
      this.first = first;
      this.second = second;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object obj) {

      if (!(obj instanceof Pair<?, ?>)) {
         return false;
      }

      Pair<?, ?> other = (Pair<?, ?>) obj;

      boolean firstEqual;

      if (this.first != null) {
         firstEqual = this.first.equals(other.first);
      } else {
         firstEqual = (other.first == null);
      }

      if (!firstEqual) {
         return false;
      }

      boolean secondEqual;

      if (this.second != null) {
         secondEqual = this.second.equals(other.second);
      } else {
         secondEqual = (other.second == null);
      }

      return secondEqual;
   }

   /**
    * Gets the first value of the {@link Pair}.
    *
    * @return the first value.
    */

   public @Nullable F getFirst() {
      return this.first;
   }

   /**
    * Gets the first value of the {@link Pair}.
    *
    * @return the first value.
    * @throws NullPointerException if the first value is <code>null</code>.
    */

   public @NonNull F getFirstNonNull() {
      return Conditions.requireNonNull(this.first);
   }

   /**
    * Performs an action upon the second {@link Pair} member when it is non-<code>null</code> and returns the first
    * member.
    *
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @throws NullPointerException when the second member is non-<code>null</code> and the {@link Consumer} parameter is
    * <code>null</code>.
    */

   public @Nullable F getFirstIfPresentOthers(@Nullable Consumer<@NonNull S> secondAction) {

      Conditions.acceptWhenNonNull(this.second, secondAction);

      return this.first;
   }

   /**
    * Performs an action upon the second {@link Pair} member when it is non-<code>null</code> and returns the first
    * member.
    *
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @throws NullPointerException when
    * <ul>
    * <li>The first member is <code>null</code>.</li>
    * <li>The second member is non-<code>null</code> and the {@link Consumer} parameter is <code>null</code>.</li>
    * </ul>
    */

   public @NonNull F getFirstNonNullIfPresentOthers(@Nullable Consumer<@NonNull S> secondAction) {

      Conditions.acceptWhenNonNull(this.second, secondAction);

      return Conditions.requireNonNull(this.first);
   }

   /**
    * Gets the second value of the {@link Pair}.
    *
    * @return the second value.
    */

   public @Nullable S getSecond() {
      return this.second;
   }

   /**
    * Gets the second value of the {@link Pair}.
    *
    * @return the second value.
    * @throws NullPointerException if the first value is <code>null</code>.
    */

   public @NonNull S getSecondNonNull() {
      return Conditions.requireNonNull(this.second);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {

      return Objects.hash(this.first, this.second);

   }

   /**
    * Performs an action upon the {@link Pair} members that are non-<code>null</code>.
    *
    * @param firstAction the action to perform on the member {@link #first} when non-<code>null</code>.
    * @param secondAction the action to perform on the member {@link #second} when non-<code>null</code>.
    * @throws NullPointerException when a member is non-<code>null</code> and the corresponding {@link Consumer}
    * parameter is <code>null</code>.
    */

   public void ifPresent(@Nullable Consumer<@NonNull F> firstAction, @Nullable Consumer<@NonNull S> secondAction) {

      Conditions.acceptWhenNonNull(this.first, firstAction);
      Conditions.acceptWhenNonNull(this.second, secondAction);

   }

   /**
    * Sets the first and second values of the {@link Pair}.
    *
    * @param first the value for the first member of the {@link Pair}.
    * @param second the value for the second member of the {@link Pair}.
    */

   public @NonNull Pair<F, S> set(@Nullable F first, @Nullable S second) {
      this.first = first;
      this.second = second;
      return this;
   }

   /**
    * Sets the first value of the {@link Pair}.
    *
    * @param first the value to be set as the {@link #first} member.
    */

   public void setFirst(@Nullable F first) {
      this.first = first;
   }

   /**
    * Sets the second value of the {@link Pair}.
    *
    * @param second the value to be set as the {@link #second} member.
    */

   public void setSecond(@Nullable S second) {
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
      var result = String.format("[%s, %s]", firstAsString, secondAsString);
      return Conditions.requireNonNull(result);
   }

   /**
    * Predicate to determine if the {@link Class} of either value is not as expected. A <code>null</code> value is
    * considered as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @return <code>false</code> when both values are of the expected {@link Class}; otherwise, <code>true</code>.
    * @throws NullPointerException when <code>firstClass</code> or <code>secondClass</code> is <code>null</code>.
    */

   public boolean typesKo(@NonNull Class<?> firstClass, @NonNull Class<?> secondClass) {
      return !this.typesOk(firstClass, secondClass);
   }

   /**
    * Predicate to determine if the {@link Class} of each value is as expected. A <code>null</code> value is considered
    * as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @param secondClass the expected {@link Class} of the second value.
    * @return <code>true</code> when both values are of the expected {@link Class}; otherwise, <code>false</code>.
    * @throws NullPointerException when <code>firstClass</code> or <code>secondClass</code> is <code>null</code>.
    */

   public boolean typesOk(@NonNull Class<?> firstClass, @NonNull Class<?> secondClass) {

      var classOfFirst = Conditions.applyWhenNonNull(this.first, (f) -> f.getClass());
      var classOfSecond = Conditions.applyWhenNonNull(this.second, (s) -> s.getClass());

      //@formatter:off
      return
            (    ( classOfFirst == null )
              || firstClass.isAssignableFrom( classOfFirst ) )
         && (    ( classOfSecond == null )
              || secondClass.isAssignableFrom( classOfSecond ) );
      //@formatter:on
   }

}

/* EOF */