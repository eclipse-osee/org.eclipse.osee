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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * A container object which may contain a value, an error, or be empty. A <code>true</code> response from one of the
 * predicates {@link #isPresentValue}, {@link #isPresentError}, or {@link #isEmpty} can be used to determine the state
 * of the {@link Result} container.
 *
 * @apiNote {@link Result} is intended for use as a method return type when it is desirable to return a value, error
 * object, or nothing instead of returning an {@link Optional} and throwing an {@link Exception}. Variables of type
 * {@link Result} should never be <code>null</code>.
 * @author Loren K. Ashley
 * @param <V> the type of the {@link Result} value.
 * @param <E> the type of the {@link Result} error.
 */

public final class Result<V, E> implements ToMessage {

   /**
    * Functional interface for performing an action upon a {@link Result} error object.
    *
    * @param <E> the type of the {@link Result} error.
    */

   @FunctionalInterface
   public interface ErrorAction<E> {

      /**
       * Performs an action upon or with the <code>errorObject</code>.
       *
       * @param errorObject the object to perform an action upon or with.
       */

      public void accept(@NonNull E errorObject);
   }

   /**
    * Functional interface for mapping a {@link Result} error object into another error object of the same or different
    * type.
    *
    * @param <E> the type of the {@link Result} error.
    * @param <EM> the type of the mapped error.
    */

   @FunctionalInterface
   public interface ErrorMapper<E, EM> {

      /**
       * Creates an error object of the same or different type from the <code>errorObject</code>.
       *
       * @param errorObject
       * @return the created error object.
       */

      public @Nullable EM apply(@NonNull E errorObject);
   }

   /**
    * Functional interface for merging two {@link Result} error objects into a single error object.
    *
    * @param <E> the type of the {@link Result} error objects.
    */

   @FunctionalInterface
   public interface ErrorMerger<E> {

      /**
       * Combines two error objects into a single error object.
       *
       * @param errorA the first error object.
       * @param errorB the second error object.
       * @return the combined error object.
       */

      public @NonNull E apply(@NonNull E errorA, @NonNull E errorB);
   }

   /**
    * Functional interface for creating a new {@link Result} with a value created from a {@link Result} error object.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    */

   @FunctionalInterface
   public interface ErrorToResultMapper<V, E, EM> {

      /**
       * Creates a new {@link Result} with a value created from a {@link Result} error object.
       *
       * @param errorObject the error object.
       * @return a new {@link Result} with the value generated from the <code>errorObject</code>.
       */

      public @NonNull Result<V, EM> apply(@NonNull E errorObject);
   }

   /**
    * Functional interface for creating a {@link Throwable} from a {@link Result} error object.
    *
    * @param <E> the type of the {@link Result} error.
    * @param <T> the type of the created {@link Throwable}.
    */

   @FunctionalInterface
   public interface ErrorToThrowableMapper<E, T extends Throwable> {

      public @NonNull T apply(@NonNull E errorObject);
   }

   /**
    * Functional interface for creating a new {@link Result} value created from a {@link Result} error object.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    */

   @FunctionalInterface
   public interface ErrorToValueMapper<V, E> {

      /**
       * Creates a {@link Result} value from a {@link Result} error object.
       *
       * @param errorObject the {@link Result} error object.
       * @return a value created from the error object.
       */

      public V apply(@NonNull E errorObject);
   }

   /**
    * Functional interface for obtaining a {@link Result}.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    */

   @FunctionalInterface
   public interface ResultSupplier<V, E> {

      /**
       * Gets a {@link Result}.
       *
       * @return a {@link Result}.
       */

      public @NonNull Result<V, E> get();
   }

   /**
    * A functional interface for a supplier of a {@link Throwable}.
    *
    * @param <T> the type of {@link Throwable} to be created.
    */

   @FunctionalInterface
   public interface ThrowableSupplier<T extends Throwable> {

      /**
       * Gets a {@link Throwable}.
       *
       * @return a {@link Throwable}.
       */

      public @NonNull T get();
   }

   /**
    * A functional interface for transforming an exception thrown by a {@link ValueMapper} into an error object of type
    * &lt;E&gt;.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    */

   @FunctionalInterface
   public interface ThrowableToErrorMapper<V, E> {

      /**
       * Returns a composed function the first applies the {@link MapExceptionMapper} and then the <code>after</code>
       * {@link Function} to the result of the {@link MapExceptionMapper}.
       *
       * @param <E2> the type of the error object returned by the <code>after</code> {@link Function}.
       * @param after the {@link Function} to apply to the result of the {@link MapExceptionMapper}.
       * @return a composed function that applies the <code>after</code> {@link Function} to the result of the
       * {@link MapExceptionMapper}.
       * @throws NullPointerException when the parameter <code>after</code> is <code>null</code>.
       */

      default <E2> ThrowableToErrorMapper<V, E2> andThen(Function<? super E, ? extends E2> after) {
         Objects.requireNonNull(after);
         return (v, t) -> after.apply(this.apply(v, t));
      }

      /**
       * Creates a error object from a {@link Result} value and the {@link Throwable} thrown by a {@link ValueMapper}.
       *
       * @param value the value that was being mapped.
       * @param throwable the {@link Throwable} that was thrown.
       * @return an error object of type &lt;E&gt;.
       */

      public @Nullable E apply(@NonNull V value, @NonNull Throwable throwable);
   }

   /**
    * Functional interface for performing an action upon or with a {@link Result} value.
    *
    * @param <V> the type of the {@link Result} value.
    */

   @FunctionalInterface
   public interface ValueAction<V> {

      /**
       * Performs an action upon or with the <code>value</code>.
       *
       * @param value the object to perform an action upon or with.
       */

      public void accept(@NonNull V value);
   }

   /**
    * Functional interface for mapping a {@link Result} value into another value of the same or different type.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <VM> the type of the mapped value.
    */

   @FunctionalInterface
   public interface ValueMapper<V, VM> {

      /**
       * Creates a value of the same or different type from the provided <code>value</code>.
       *
       * @param errorObject
       * @return the created error object.
       */

      public @Nullable VM apply(@NonNull V value);
   }

   /**
    * Functional interface for obtaining a value.
    *
    * @param <V> the type of the {@link Result} value.
    */

   @FunctionalInterface
   public interface ValueSupplier<V> {

      /**
       * Gets a value.
       *
       * @return a value;
       */

      public @Nullable V get();
   }

   /**
    * Functional interface for creating a new {@link Result} error object from a {@link Result} value.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    */

   @FunctionalInterface
   public interface ValueToErrorMapper<V, E> {

      /**
       * Creates a {@link Result} error object from a {@link Result} value.
       *
       * @param value the {@link Result} value.
       * @return an error object created from the value.
       */

      public @Nullable E apply(@NonNull V value);
   }

   /**
    * Functional interface for creating a new {@link Optional} with a value created from a {@link Result} value.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <VM> the type of the {@link Optional} value.
    */

   @FunctionalInterface
   public interface ValueToOptionalMapper<V, VM> {

      /**
       * Creates a new {@link Optional} with a value created from a {@link Result} value.
       *
       * @param value the value.
       * @return a new {@link Optional} with the value generated from the <code>value</code>.
       */

      @NonNull
      Optional<VM> apply(@NonNull V value);
   }

   /**
    * Functional interface for creating a new {@link Result} value created from another {@link Result} value.
    *
    * @param <V> the type of the source {@link Result} value.
    * @param <VM> the type of the returned {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    */

   @FunctionalInterface
   public interface ValueToResultMapper<V, VM, E> {

      /**
       * Creates a new {@link Result} with a value created from the value of another {@link Result}.
       *
       * @param value the value to be mapped.
       * @return a {@link Result} containing the mapped value.
       */

      public @NonNull Result<VM, E> apply(@NonNull V value);
   }

   /**
    * Saves an instance of an empty {@link Result}.
    */

   private static @NonNull Result<?, ?> emptyResult = new Result<>();

   /**
    * Returns an empty {@link Result} instance.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    * @return an empty {@link Result}.
    */

   public static <V, E> @NonNull Result<V, E> empty() {
      @SuppressWarnings("unchecked")
      var result = (Result<V, E>) Result.emptyResult;
      return result;
   }

   /**
    * Combines the error objects from the provided {@link Result} objects using the provided merging function.
    *
    * @param <E> the type of error objects.
    * @param errorMerger a method taking two error objects and returning a combined error object.
    * @param results the {@link Result} objects to combine their contained error objects. Not all or any of the
    * {@link Result} objects have to contain an error object.
    * @return when the {@link Result} objects in <code>result</code> contain one or more error objects an
    * {@link Optional} containing the combined error object produced with the merging function <code>merge</code>;
    * otherwise, an empty {@link Optional}.
    * @throws NullPointerException when <code>errorMerger</code> is <code>null</code> and <code>results</code> contains
    * at least one {@link Result}.
    */

   @SafeVarargs
   public static <E> Optional<E> getMergedError(@NonNull ErrorMerger<E> errorMerger, @Nullable Result<?, E>... results) {

      if (Objects.isNull(results) || results.length == 0) {
         return Optional.empty();
      }

      Objects.requireNonNull(errorMerger);

      @Nullable
      E accumulator = null;

      for (int i = 0; i < results.length; i++) {

         var result = results[i];

         if (result == null) {
            continue;
         }

         if (result.isPresentError()) {
            //@formatter:off
            accumulator = Objects.nonNull( accumulator )
                             ? errorMerger.apply(accumulator, result.getError() )
                             : result.getError();
           //@formatter:on
         }
      }

      if (Objects.isNull(accumulator)) {
         return Optional.empty();
      }

      return Optional.of(accumulator);
   }

   /**
    * Creates a {@link Result} containing the non-<code>null</code> <code>error</code> object.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    * @param error the non-<code>null</code> error object.
    * @return a {@link Result} containing the <code>error</code> object.
    * @throws NullPointerException when <code>error</code> is <code>null</code>.
    */

   public static <V, E> @NonNull Result<V, E> ofError(@NonNull E error) {
      return new Result<V, E>(null, Objects.requireNonNull(error));
   }

   /**
    * Perform a safe type cast of a {@link Result} with a different value type and the same error object type to a
    * {@link Result} object with a value type of &lt;V&gt; and error type of &lt;E&gt;. The cast is safe since the value
    * is <code>null</code>.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    * @param errorResult the {@link Result} containing an error object to be cast.
    * @return the <code>errorResult</code> object.
    * @throws NullPointerException when <code>errorResult</code> is <code>null</code>.
    * @throws IllegalArgumentException when the <code>errorResult</code> object does not contain an error.
    * @implNote This method is intended to cast a {@link Result} object containing an value from a sub-method call to be
    * returned as a method result when both {@link Result} types have the same value type but different error types.
    */

   public static <V, E> @NonNull Result<V, E> ofError(@NonNull Result<?, E> errorResult) {
      if (!Objects.requireNonNull(errorResult).isPresentError()) {
         throw new IllegalArgumentException();
      }
      @SuppressWarnings("unchecked")
      var result = (Result<V, E>) errorResult;
      return result;
   }

   /**
    * Creates a {@link Result} with the possibly <code>null</code> <code>error</code> object.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    * @param error the non-<code>null</code> error object.
    * @return a {@link Result} containing the <code>error</code> object.
    * @throws NullPointerException when <code>error</code> is <code>null</code>.
    */

   public static <V, E> @NonNull Result<V, E> ofErrorNullable(@Nullable E error) {
      //@formatter:off
      return
         Objects.nonNull( error )
            ? new Result<V, E>( null, error )
            : Result.empty();
     //@formatter:on
   }

   /**
    * Perform a safe type cast of a {@link Result} with a different error object type and the same value type to a
    * {@link Result} object with a value type of &lt;V&gt; and error type of &lt;E&gt;. The cast is safe since the error
    * object is <code>null</code>.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    * @param errorResult the {@link Result} containing an error object to be cast.
    * @return the <code>errorResult</code> object.
    * @throws NullPointerException when <code>errorResult</code> is <code>null</code>.
    * @throws IllegalArgumentException when the <code>errorResult</code> object does not contain an error.
    * @implNote This method is intended to cast a {@link Result} object containing an value from a sub-method call to be
    * returned as a method result when both {@link Result} types have the same value type but different error types.
    */

   public static <V, E> @NonNull Result<V, E> ofValue(@NonNull Result<V, ?> valueResult) {
      if (!Objects.requireNonNull(valueResult).isPresentValue()) {
         throw new IllegalArgumentException();
      }
      @SuppressWarnings("unchecked")
      var result = (Result<V, E>) valueResult;
      return result;
   }

   /**
    * Creates a {@link Result} containing the non-<code>null</code> <code>value</code>.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    * @param value the non-<code>null</code> value object.
    * @return a {@link Result} containing the <code>value</code> object.
    * @throws NullPointerException when <code>value</code> is <code>null</code>.
    */

   public static <V, E> @NonNull Result<V, E> ofValue(@NonNull V value) {
      return new Result<V, E>(Objects.requireNonNull(value), null);
   }

   /**
    * Creates a {@link Result} with the possibly <code>null</code> <code>value</code>.
    *
    * @param <V> the type of the {@link Result} value.
    * @param <E> the type of the {@link Result} error.
    * @param value the possibly <code>null</code> value object.
    * @return a {@link Result} containing the <code>value</code> object when non-<code>null</code>; otherwise an empty
    * {@link Result}.
    */

   public static <V, E> @NonNull Result<V, E> ofValueNullable(@Nullable V value) {
      //@formatter:off
      return
         Objects.nonNull( value )
            ? new Result<V, E>( value, null )
            : Result.empty();
      //@formatter:on
   }

   /**
    * Combines the error objects from the provided {@link Result} objects using the provided merging function. If a
    * combined error object is produced, an exception is created from the error object using the
    * <code>exceptionFunction</code> and the exception is thrown.
    *
    * @param <E> the type of error object.
    * @param errorMerger an {@link ErrorMerger} implementation that combines two error objects into a combined error
    * object.
    * @param errorToThrowableMapper an {@link ErrorToThrowableMapper} implementation that creates a {@link Throwable}
    * from an error object.
    * @param results the {@link Result} objects to combine their contained error objects. Not all or any of the
    * {@link Result} objects have to contain an error object.
    * @throws NullPointerException when <code>results</code> contains at least one {@link Result} and <code>merge</code>
    * is <code>null</code>; or when <code>results</code> contains at least one {@link Result} with an error and
    * <code>exceptionFunction</code> is <code>null</code>.
    */

   @SafeVarargs
   public static <E, T extends Throwable> void throwIfError(@NonNull ErrorMerger<E> errorMerger, @NonNull ErrorToThrowableMapper<E, T> errorToThrowableMapper, @Nullable Result<?, E>... results) throws Throwable {

      if (Objects.isNull(results) || results.length == 0) {
         return;
      }

      Objects.requireNonNull(errorMerger);

      E accumulator = null;

      for (int i = 0; i < results.length; i++) {

         var result = results[i];

         if (result == null) {
            continue;
         }

         if (result.isPresentError()) {
            //@formatter:off
            accumulator = Objects.nonNull( accumulator )
                             ? errorMerger.apply(accumulator, result.getError() )
                             : result.getError();
           //@formatter:on
         }
      }

      if (Objects.isNull(accumulator)) {
         return;
      }

      throw Objects.requireNonNull(errorToThrowableMapper).apply(accumulator);
   }

   /**
    * Saves the error object. This member may be <code>null</code>.
    */

   private final E error;

   /**
    * Saves the value object. This member may be <code>null</code>.
    */

   private final V value;

   /**
    * Creates the empty {@link Result} object.
    */

   private Result() {
      this.value = null;
      this.error = null;
   }

   /**
    * Creates a new {@link Result} with the specified <code>value</code>, <code>error</code> or neither.
    *
    * @param value the value object.
    * @param error the error object.
    * @throws IllegalArgumentException when <code>value</code> and <code>error</code> are both non-<code>null</code>.
    */

   private Result(@Nullable V value, @Nullable E error) {

      if (Objects.nonNull(value) && Objects.nonNull(error)) {
         throw new IllegalArgumentException();
      }

      this.value = value;
      this.error = error;
   }

   /**
    * When an error is present and the error object passes the <code>errorPredicate</code> this {@link Result} is
    * returned; otherwise, an empty {@link Result} is returned.
    *
    * @param errorPredicate the {@link Predicate} to test the errorObject with.
    * @return when an error is present and the error passes the <code>errorPredicate</code> this {@link Result};
    * otherwise, an empty {@link Result}.
    * @throws NullPointerException when an error is present and the <code>errorPredicate</code> is <code>null</code>.
    */

   public @NonNull Result<V, E> filterError(@NonNull Predicate<? super E> errorPredicate) {
      //@formatter:off
      return
         Objects.nonNull( this.error )
            ? Objects.requireNonNull( errorPredicate ).test( this.error )
                  ? this
                  : Result.empty()
            : this;
      //@formatter:on
   }

   /**
    * When an error is present and the error object passes the <code>errorPredicate</code> this {@link Result} is
    * returned; otherwise, a value {@link Result} is returned with the value created from the error object by the
    * <code>failedPredicateErrorToValueMapper</code> {@link ErrorToValueMapper}.
    *
    * @param errorPredicate the {@link Predicate} to test the error object with.
    * @return when an error is present and the error object passes the <code>errorPredicate</code> this {@link Result};
    * otherwise, a value {@link Result} is returned with the value created from the error object by the
    * <code>failedPredicateErrorToValueMapper</code> {@link ErrorToValueMapper}.
    * @throws NullPointerException when:
    * <ul>
    * <li>An error is present and the <code>errorPredicate</code> is <code>null</code>.</li>
    * <li>An error is present, the <code>errorPredicate</code> failed, and the
    * <code>failedPredicateErrorToValueMapper</code> is <code>null</code>.</li>
    * </ul>
    */

   public @NonNull Result<V, E> filterError(@NonNull Predicate<? super E> errorPredicate, @NonNull ErrorToValueMapper<? extends V, ? super E> failedPredicateErrorToValueMapper) {
      //@formatter:off
      return
        ( Objects.nonNull( this.error ) && !Objects.requireNonNull( errorPredicate ).test( this.error ) )
            ? Result.ofValueNullable( Objects.requireNonNull( failedPredicateErrorToValueMapper ).apply( this.error ) )
            : this;
      //@formatter:on
   }

   /**
    * When a value is present and the value passes the <code>valuePredicate</code> this {@link Result} is returned;
    * otherwise, an empty {@link Result} is returned.
    *
    * @param valuePredicate the {@link Predicate} to test the value with.
    * @return when a value is present and the value passes the <code>valuePredicate</code> this {@link Result};
    * otherwise, an empty {@link Result}.
    * @throws NullPointerException when a value is present and the <code>valuePredicate</code> is <code>null</code>.
    */

   public @NonNull Result<V, E> filterValue(@NonNull Predicate<? super V> valuePredicate) {
      //@formatter:off
      return
         Objects.nonNull( this.value )
            ? Objects.requireNonNull( valuePredicate ).test( this.value )
                  ? this
                  : Result.empty()
            : this;
      //@formatter:on
   }

   /**
    * When a value is present and the value passes the <code>valuePredicate</code> this {@link Result} is returned;
    * otherwise, an error {@link Result} is returned with the error object created from the value by the
    * <code>failedPredicateValueToErrorMapper</code> {@link ValueToErrorMapper}.
    *
    * @param valuePredicate the {@link Predicate} to test the value with.
    * @return when a value is present and the value passes the <code>valuePredicate</code> this {@link Result};
    * otherwise, error {@link Result} is returned with the error object created from the value object by the
    * <code>failedPredicateValueToErrorMapper</code> {@link ValueToErrorMapper}.
    * @throws NullPointerException when:
    * <ul>
    * <li>A value is present and the <code>valuePredicate</code> is <code>null</code>.</li>
    * <li>A value is present, the <code>valuePredicate</code> failed, and the
    * <code>failedPredicateValueToErrorMapper</code> is <code>null</code>.</li>
    * </ul>
    */

   public @NonNull Result<V, E> filterValue(@NonNull Predicate<? super V> valuePredicate, @NonNull ValueToErrorMapper<? super V, ? extends E> failedPredicateValueToErrorMapper) {
      //@formatter:off
      return
        ( Objects.nonNull( this.value ) && !Objects.requireNonNull( valuePredicate ).test( this.value ) )
            ? Result.ofErrorNullable( Objects.requireNonNull( failedPredicateValueToErrorMapper ).apply( this.value ) )
            : this;
      //@formatter:on
   }

   /**
    * Maps a {@link Result}<code>&lt;V,E&gt;</code> into a {@link Result}<code>&lt;V,EM&gt;</code> as follows:
    * <dl>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> does not contain an error,</dt>
    * <dd>it is type cast into a {@link Result}<code>&ltV,EM&gt;</code> and returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains an error,</dt>
    * <dd>a new {@link Result}<code>&lt;V,EM&gt;</code> created with <code>errorToResultMapper</code> is returned.</dd>
    * </dl>
    *
    * @param <EM> the type of the error in the returned {@link Result}.
    * @param errorToResultMapper a {@link Result}<code>&lt;V,EM&gt;</code>-bearing function that receives the error of
    * type <code>&lt;E&gt;</code> from this {@link Result} and returns a new {@link Result}.
    * @return a {@link Result}<code>&lt;V,EM&gt;</code>.
    * @throws NullPointerException when <code>errorToResultMapper</code> is <code>null</code> and this {@link Result}
    * contains an error.
    * @throws IllegalStateException when <code>errorToResultMapper</code> returns a {@link Result} with a value.
    */

   public <EM> @NonNull Result<V, EM> flatMapError(@NonNull ErrorToResultMapper<V, E, EM> errorToResultMapper) {

      if (Objects.isNull(this.error)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<V, EM>) this;
         return rv;
      }

      var result = Objects.requireNonNull(errorToResultMapper).apply(this.error);

      if (result.isPresentValue()) {
         throw new IllegalStateException();
      }

      return result;
   }

   /**
    * Maps a {@link Result}<code>&lt;V,E&gt;</code> into a {@link Result}<code>&lt;V,EM&gt;</code> as follows:
    * <dl>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> does not contain an error,</dt>
    * <dd>it is type cast into a {@link Result}<code>&ltV,EM&gt;</code> and returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains an error and <code>errorToResultMapper</code>
    * completes successfully,</dt>
    * <dd>a new {@link Result}<code>&lt;V,EM&gt;</code> created with <code>errorToResultMapper</code> is returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains an error and <code>errorToResultMapper</code> throws
    * an exception,</dt>
    * <dd>the error and the exception are passed to the method <code>throwableToErrorMapper</code> and a new
    * {@link Result}<code>&ltV,EM&gt;</code> is created from the return value with {@link Result#ofErrorNullable}.</dd>
    * </dl>
    *
    * @param <EM> the type of the error in the returned {@link Result}.
    * @param errorToResultMapper a {@link Result}<code>&lt;V,EM&gt;</code>-bearing function that receives the error of
    * type <code>&lt;E&gt;</code> from this {@link Result} and returns a new {@link Result}.
    * @param throwableToErrorMapper a method that receives the error from this {@link Result} and the exception thrown
    * by <code>errorToResultMapper</code> and returns an error of type <code>&lt;EM&gt;</code>.
    * @return a {@link Result}<code>&lt;V,EM&gt;</code>.
    * @throws NullPointerException when:
    * <ul>
    * <li><code>errorToResultMapper</code> is <code>null</code> and this {@link Result} contains an error, or</li>
    * <li><code>errorToResultMapper</code> throws an exception and <code>throwableToErrorMapper</code> is
    * <code>null</code>.</li>
    * </ul>
    * @throws IllegalStateException when <code>errorToResultMapper</code> returns a {@link Result} with a value.
    */

   public <EM> @NonNull Result<V, EM> flatMapError(@NonNull ErrorToResultMapper<V, E, EM> errorToResultMapper, @NonNull ThrowableToErrorMapper<E, EM> throwableToErrorMapper) {

      if (Objects.isNull(this.error)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<V, EM>) this;
         return rv;
      }

      Result<V, EM> result;

      try {

         result = Objects.requireNonNull(errorToResultMapper).apply(this.error);

      } catch (Throwable t) {

         result = Result.ofErrorNullable(Objects.requireNonNull(throwableToErrorMapper).apply(this.error, t));

      }

      if (result.isPresentValue()) {
         throw new IllegalStateException();
      }

      return result;
   }

   /**
    * Maps a {@link Result}<code>&lt;V,E&gt;</code> into a {@link Result}<code>&lt;VM,E&gt;</code> as follows:
    * <dl>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> does not contain a value,</dt>
    * <dd>it is type cast into a {@link Result}<code>&ltVM,E&gt;</code> and returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains a value,</dt>
    * <dd>a new {@link Result}<code>&lt;VM,E&gt;</code> created from the value in the {@link Optional} returned from
    * <code>valueToOptionalMapper</code> is returned.</dd>
    * </dl>
    *
    * @param <VM> the type of the value in the returned {@link Optional}.
    * @param valueToOptionalMapper an {@link Optional}<code>&lt;VM&gt;</code>-bearing function that receives the value
    * of type <code>&lt;V&gt;</code> from this {@link Result} and returns an {@link Optional}<code>&lt;VM&gt;</code>.
    * @return a {@link Result}<code>&lt;VM,E&gt;</code>.
    * @throws NullPointerException when <code>valueToOptionalMapper</code> is <code>null</code> and this {@link Result}
    * contains a value.
    */

   public <VM> @NonNull Result<VM, E> flatMapOptionalValue(@NonNull ValueToOptionalMapper<V, VM> valueToOptionalMapper) {

      if (Objects.isNull(this.value)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<VM, E>) this;
         return rv;
      }

      //@formatter:off
      var result =
         Objects
            .requireNonNull( valueToOptionalMapper )
            .apply( this.value )
            .map( ( value ) -> Result.<VM,E>ofValue( value ) )
            .orElse( Result.empty() );
      //@formatter:on
      return result;
   }

   /**
    * Maps a {@link Result}<code>&lt;V,E&gt;</code> into a {@link Result}<code>&lt;VM,E&gt;</code> as follows:
    * <dl>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> does not contain a value,</dt>
    * <dd>it is type cast into a {@link Result}<code>&ltVM,E&gt;</code> and returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains a value and <code>valueToOptionalMapper</code>
    * completes successfully,</dt>
    * <dd>a new {@link Result}<code>&lt;VM,E&gt;</code> created from the value in the {@link Optional} returned from
    * <code>valueToOptionalMapper</code> is returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains a value and <code>valueToOptionalMapper</code>
    * throws an exception,</dt>
    * <dd>the value and the exception are passed to the method <code>throwableToErrorMapper</code> and a new
    * {@link Result}<code>&ltVM,E&gt;</code> is created from the return value with {@link Result#ofErrorNullable}.</dd>
    * </dl>
    *
    * @param <VM> the type of the value in the returned {@link Result}.
    * @param valueToOptionalMapper an {@link Optional}<code>&lt;VM&gt;</code>-bearing function that receives the value
    * of type <code>&lt;V&gt;</code> from this {@link Result} and returns an {@link Optional}<code>&lt;VM&gt;</code>.
    * @param throwableToErrorMapper a method that receives the value from this {@link Result} and the exception thrown
    * by <code>valueToOptionalMapper</code> and returns a value of type <code>&lt;VM&gt;</code>.
    * @return a {@link Result}<code>&lt;VM,E&gt;</code>.
    * @throws NullPointerException when:
    * <ul>
    * <li><code>valueToOptionalMapper</code> is <code>null</code> and this {@link Result} contains an error, or</li>
    * <li><code>valueToOptionalMapper</code> throws an exception and <code>throwableToErrorMapper</code> is
    * <code>null</code>.</li>
    * </ul>
    */

   public <VM> @NonNull Result<VM, E> flatMapOptionalValue(@NonNull ValueToOptionalMapper<V, VM> valueToOptionalMapper, @NonNull ThrowableToErrorMapper<V, E> throwableToErrorMapper) {

      if (Objects.isNull(this.value)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<VM, E>) this;
         return rv;
      }

      try {
         //@formatter:off
         var result =
            Objects
               .requireNonNull( valueToOptionalMapper )
               .apply( this.value )
               .map( ( value ) -> Result.<VM,E>ofValue( value ) )
               .orElse( Result.empty() );
         //@formatter:on
         return result;

      } catch (Exception e) {

         @Nullable
         E error = throwableToErrorMapper.apply(this.value, e);

         return Result.ofErrorNullable(error);
      }
   }

   /**
    * Maps a {@link Result}<code>&lt;V,E&gt;</code> into a {@link Result}<code>&lt;VM,E&gt;</code> as follows:
    * <dl>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> does not contain a value,</dt>
    * <dd>it is type cast into a {@link Result}<code>&ltVM,E&gt;</code> and returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains a value,</dt>
    * <dd>a new {@link Result}<code>&lt;VM,E&gt;</code> created with <code>valueToResultMapper</code> is returned.</dd>
    * </dl>
    *
    * @param <VM> the type of the value in the returned {@link Result}.
    * @param valueToResultMapper a {@link Result}<code>&lt;VM,E&gt;</code>-bearing function that receives the value of
    * type <code>&lt;V&gt;</code> from this {@link Result} and returns a new {@link Result}.
    * @return a {@link Result}<code>&lt;VM,E&gt;</code>.
    * @throws NullPointerException when <code>valueToResultMapper</code> is <code>null</code> and this {@link Result}
    * contains a value.
    */

   public <VM> @NonNull Result<VM, E> flatMapValue(@NonNull ValueToResultMapper<V, VM, E> valueToResultMapper) {

      if (Objects.isNull(this.value)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<VM, E>) this;
         return rv;
      }

      var result = Objects.requireNonNull(valueToResultMapper).apply(this.value);

      return result;
   }

   /**
    * Maps a {@link Result}<code>&lt;V,E&gt;</code> into a {@link Result}<code>&lt;VM,E&gt;</code> as follows:
    * <dl>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> does not contain a value,</dt>
    * <dd>it is type cast into a {@link Result}<code>&ltVM,E&gt;</code> and returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains a value and <code>valueToResultMapper</code>
    * completes successfully,</dt>
    * <dd>a new {@link Result}<code>&lt;VM,E&gt;</code> created with <code>valueToResultMapper</code> is returned.</dd>
    * <dt>When this {@link Result}<code>&lt;V,E&gt;</code> contains a value and <code>valueToResultMapper</code> throws
    * an exception,</dt>
    * <dd>the value and the exception are passed to the method <code>throwableToErrorMapper</code> and a new
    * {@link Result}<code>&ltVM,E&gt;</code> is created from the return value with {@link Result#ofErrorNullable}.</dd>
    * </dl>
    *
    * @param <VM> the type of the value in the returned {@link Result}.
    * @param valueToResultMapper a {@link Result}<code>&lt;VM,E&gt;</code>-bearing function that receives the value of
    * type <code>&lt;V&gt;</code> from this {@link Result} and returns a new {@link Result}.
    * @param throwableToErrorMapper a method that receives the value from this {@link Result} and the exception thrown
    * by <code>valueToResultMapper</code> and returns a value of type <code>&lt;VM&gt;</code>.
    * @return a {@link Result}<code>&lt;VM,E&gt;</code>.
    * @throws NullPointerException when:
    * <ul>
    * <li><code>valueToResultMapper</code> is <code>null</code> and this {@link Result} contains an error, or</li>
    * <li><code>valueToResultMapper</code> throws an exception and <code>throwableToErrorMapper</code> is
    * <code>null</code>.</li>
    * </ul>
    */

   public <VM> @NonNull Result<VM, E> flatMapValue(@NonNull ValueToResultMapper<V, VM, E> valueToResultMapper, @NonNull ThrowableToErrorMapper<V, E> throwableToErrorMapper) {

      if (Objects.isNull(this.value)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<VM, E>) this;
         return rv;
      }

      try {

         var result = Objects.requireNonNull(valueToResultMapper).apply(this.value);

         return result;

      } catch (Exception e) {

         var result =
            Result.<VM, E> ofErrorNullable(Objects.requireNonNull(throwableToErrorMapper).apply(this.value, e));

         return result;
      }
   }

   /**
    * When an error object is not present the possibly <code>null</code> value object is returned with
    * {@link Optional#ofNullable}; otherwise, the <code>exceptionMapper</code> function is applied to the error object
    * and the resulting exception is thrown.
    *
    * @param <EM> the type of {@link Throwable} returned by the <code>exceptionMapper</code>.
    * @param errorToThrowableMapper a function that generates a {@link Throwable} implementation from the error object.
    * @return when an error object is not present the possibly <code>null</code> value object is returned with
    * {@link Optional#ofNullable}.
    * @throws EM when an error object is present the <code>exceptionMapper</code> function is applied to the error
    * object and the resulting exception is thrown.
    */

   public <T extends Throwable> Optional<V> getAsOptionalOrElseThrow(@NonNull ErrorToThrowableMapper<E, T> errorToThrowableMapper) throws T {

      if (this.error == null) {
         return Optional.ofNullable(this.value);
      }

      throw Conditions.requireNonNull(errorToThrowableMapper).apply(this.error);
   }

   /**
    * When an error object is present it is returned; otherwise an exception is thrown.
    *
    * @return the error object.
    * @throws NoSuchElementException when the {@link Result} does not contain an error object.
    */

   public @NonNull E getError() {

      if (Objects.nonNull(this.error)) {
         return this.error;
      }

      throw new NoSuchElementException("Result::getError, error not present.");
   }

   /**
    * When a value object is present it is returned; otherwise an exception is thrown.
    *
    * @return the value object.
    * @throws NoSuchElementException when the {@link Result} does not contain a value object.
    */

   public @NonNull V getValue() {

      if (Objects.nonNull(this.value)) {
         return this.value;
      }

      throw new NoSuchElementException("Result::getValue, value not present.");
   }

   /**
    * When neither an error or value is present, a {@link NoSuchElementException} is thrown.
    *
    * @return when an error or value is present, <code>this</code>.
    * @throws NoSuchElementException when the {@link Result} is empty.
    */

   public @NonNull Result<V, E> ifEmptyThrow() {
      if (Objects.isNull(this.error) && Objects.isNull(this.value)) {
         throw new NoSuchElementException();
      }

      return this;
   }

   /**
    * When neither an error or value is present, the {@link Throwable} obtained from <code>throwableSupplier</code> is
    * thrown.
    *
    * @param <T> the type of {@link Throwable} thrown when the {@link Result} is empty.
    * @param throwableSupplier method invoked when the {@link Result} is empty to obtain the {@link Throwable} to throw.
    * @return when an error or value is present, <code>this</code>.
    * @throws NullPointerException when the {@link Result} is empty and <code>throwableSupplier</code> is
    * <code>null</code>.
    * @throws T when the {@link Result} is empty.
    */

   public <T extends Throwable> @NonNull Result<V, E> ifEmptyThrow(@NonNull ThrowableSupplier<T> throwableSupplier) throws T {
      if (Objects.isNull(this.error) && Objects.isNull(this.value)) {
         throw Objects.requireNonNull(throwableSupplier).get();
      }

      return this;
   }

   /**
    * When an error is present, performs the provided <code>errorAction</code> with the error.
    *
    * @param errorAction the action to be performed upon the error.
    * @throws NullPointerException when an error is present and the <code>errorAction</code> is <code>null</code>.
    */

   public void ifErrorAction(@NonNull ErrorAction<E> errorAction) {

      if (Objects.nonNull(this.error)) {
         Objects.requireNonNull(errorAction).accept(this.error);
      }
   }

   /**
    * When an error is present the <code>errorAction</code> is applied to the error; otherwise, the
    * <code>notPresentAction</code> is run. The <code>notPresentAction</code> is run when the {@link Result} is empty or
    * contains a value.
    *
    * @param errorAction the action to be performed upon the error when present.
    * @param errorNotPresentAction the {@link Runnable} to be run when the value is not present.
    * @throws NullPointerException when a value object is present and <code>action</code> is <code>null</code>; or when
    * a value object is not present and <code>notPresentAction</code> is <code>null</code>.
    */

   public void ifErrorActionElseAction(@NonNull ErrorAction<E> errorAction, @NonNull Runnable errorNotPresentAction) {

      if (Objects.nonNull(this.error)) {
         Objects.requireNonNull(errorAction).accept(this.error);
      } else {
         Objects.requireNonNull(errorNotPresentAction).run();
      }
   }

   /**
    * When an error is present a {@link NoSuchElementException} is thrown with the string representation of the error
    * object.
    *
    * @return when an error is not present, <code>this</code>.
    */

   public @NonNull Result<V, E> ifErrorThrow() {

      if (Objects.nonNull(this.error)) {
         throw new NoSuchElementException(this.error.toString());
      }

      return this;
   }

   /**
    * When an error is present, the {@link Throwable} obtained from <code>throwableFunction</code> is thrown.
    *
    * @param <T> the type of {@link Throwable} thrown when the {@link Result} contains an error.
    * @param errorToThrowableMapper method invoked with the error from this {@link Result} to create the
    * {@link Throwable} to throw.
    * @return when an error is not present, <code>this</code>.
    * @throws T when the {@link Result} contains an error.
    */

   public <T extends Throwable> @NonNull Result<V, E> ifErrorThrow(@NonNull ErrorToThrowableMapper<E, T> errorToThrowableMapper) throws T {

      if (Objects.nonNull(this.error)) {
         throw Objects.requireNonNull(errorToThrowableMapper).apply(this.error);
      }

      return this;
   }

   /**
    * When a value is present, performs the provided <code>valueAction</code> with the value.
    *
    * @param valueAction the action to be performed upon the value.
    * @throws NullPointerException when a value is present and the <code>valueAction</code> is <code>null</code>.
    */

   public void ifValueAction(@NonNull ValueAction<V> valueAction) {

      if (Objects.nonNull(this.value)) {
         Objects.requireNonNull(valueAction).accept(this.value);
      }
   }

   /**
    * When a value is present the <code>valueAction</code> is applied to the value; otherwise, the
    * <code>notPresentAction</code> is run. The <code>notPresentAction</code> is run when the {@link Result} is empty or
    * contains an error.
    *
    * @param valueAction the action to be performed upon the value when present.
    * @param valueNotPresentAction the {@link Runnable} to be run when the value is not present.
    * @throws NullPointerException when a value object is present and <code>action</code> is <code>null</code>; or when
    * a value object is not present and <code>notPresentAction</code> is <code>null</code>.
    */

   public void ifValueActionElseAction(@NonNull ValueAction<V> valueAction, @NonNull Runnable valueNotPresentAction) {

      if (Objects.nonNull(this.value)) {
         Objects.requireNonNull(valueAction).accept(this.value);
      } else {
         Objects.requireNonNull(valueNotPresentAction).run();
      }
   }

   /**
    * When a value is present the <code>valueAction</code> is applied to the value; when an error is present a
    * {@link Throwable} created from the error is thrown; otherwise, a {@link NoSuchElementException} is thrown.
    *
    * @param <EM> the type of {@link Throwable} returned by the <code>errorToThrowableMapper</code>.
    * @param valueAction the action to be performed upon the value when present.
    * @param errorToThrowableMapper a method that creates a {@link Throwable} implementation from the error when
    * present.
    * @throws NullPointerException when a value is present and <code>valueAction</code> is <code>null</code>; or when an
    * error is present and <code>errorToThrowableMapper</code> is <code>null</code>.
    */

   public <T extends Throwable> void ifValueActionElseThrow(@NonNull ValueAction<V> valueAction, @NonNull ErrorToThrowableMapper<E, T> errorToThrowableMapper) throws T {

      if (Objects.nonNull(this.value)) {
         Objects.requireNonNull(valueAction).accept(this.value);
         return;
      }

      if (Objects.nonNull(this.error)) {
         throw Objects.requireNonNull(errorToThrowableMapper).apply(this.error);
      }

      throw new NoSuchElementException();
   }

   /**
    * When a value is present the <code>valueAction</code> is applied to the value; when an error is present a
    * {@link Throwable} created from the error by <code>errorToThrowableMapper</code> is thrown; otherwise,
    * <code>throwableSupplier</code> is used to obtain the {@link Throwable} to throw.
    *
    * @param <EM> the type of {@link Throwable} returned by the <code>errorToThrowableMapper</code>.
    * @param valueAction the action to be performed upon the value when present.
    * @param errorToThrowableMapper a method that creates a {@link Throwable} implementation from the error when
    * present.
    * @param throwableSupplier a method that creates a {@link Throwable} to be thrown when the {@link Result} is empty.
    * @throws NullPointerException when a value is present and <code>valueAction</code> is <code>null</code>; or when an
    * error is present and <code>errorToThrowableMapper</code> is <code>null</code>.
    */

   public <T extends Throwable> void ifValueActionElseThrow(@NonNull ValueAction<V> valueAction, @NonNull ErrorToThrowableMapper<E, T> errorToThrowableMapper, @NonNull ThrowableSupplier<T> throwableSupplier) throws T {

      if (Objects.nonNull(this.value)) {
         Objects.requireNonNull(valueAction).accept(this.value);
         return;
      }

      if (Objects.nonNull(this.error)) {
         throw Objects.requireNonNull(errorToThrowableMapper).apply(this.error);
      }

      throw Objects.requireNonNull(throwableSupplier).get();
   }

   /**
    * Applies a {@link ValueAction} when a value is present, applies an {@link ErrorAction} when an error is present, or
    * a {@link Runnable} is run when empty.
    *
    * @param valueAction the function to be applied to the value when present.
    * @param errorToThrowableMapper a function that generates a {@link Throwable} implementation from the error when
    * present.
    * @param emptyAction the {@link Runnable} to be run when the {@link Result} is empty.
    * @throws NullPointerException when a value object is present and <code>valueAction</code> is <code>null</code>; or
    * when an error is present and <code>errorAction</code> is <code>null</code>; or when the {@link Result} is empty
    * and <code>emptyAction</code> is <code>null</code>.
    */

   public void ifValueActionIfErrorActionElseAction(@NonNull ValueAction<V> valueAction, @NonNull ErrorAction<E> errorAction, @NonNull Runnable emptyAction) {

      if (Objects.nonNull(this.value)) {
         Objects.requireNonNull(valueAction).accept(this.value);
         return;
      }

      if (Objects.nonNull(this.error)) {
         Objects.requireNonNull(errorAction).accept(this.error);
      }

      emptyAction.run();
   }

   /**
    * Applies a {@link ValueAction} when a value is present, throws an exception when an error is present, or a
    * {@link Runnable} is run when empty.
    *
    * @param <EM> the type of {@link Throwable} returned by the <code>exceptionMapper</code>.
    * @param action the {@link Consumer} to be applied to the value when present.
    * @param errorToThrowableMapper a function that generates a {@link Throwable} implementation from the error object
    * when present.
    * @param notPresentAction the {@link Runnable} to be run when the {@link Result} is empty.
    * @throws NullPointerException when a value object is present and <code>valueAction</code> is <code>null</code>; or
    * when an error object is present and <code>errorToThrowableMapper</code> is <code>null</code>; or when the
    * {@link Result} is empty and <code>emptyAction</code> is <code>null</code>.
    */

   public <T extends Throwable> void ifValueActionIfErrorThrowElseAction(@NonNull ValueAction<V> valueAction, @NonNull ErrorToThrowableMapper<E, T> errorToThrowableMapper, @NonNull Runnable emptyAction) throws T {

      if (Objects.nonNull(this.value)) {
         Objects.requireNonNull(valueAction).accept(this.value);
         return;
      }

      if (Objects.nonNull(this.error)) {
         throw Objects.requireNonNull(errorToThrowableMapper).apply(this.error);
      }

      emptyAction.run();
   }

   /**
    * Predicate to determine if the {@link Result} is empty.
    *
    * @return <code>true</code> when the {@link Result} does not have a value or an error; otherwise,
    * <code>false</code>.
    */

   public boolean isEmpty() {
      return Objects.isNull(this.value);
   }

   /**
    * Predicate to determine if the {@link Result} contains an error.
    *
    * @return <code>true</code> when the {@link Result} contains an error; otherwise, <code>false</code>.
    */

   public boolean isPresentError() {
      return Objects.nonNull(this.error);
   };

   /**
    * Predicate to determine if the {@Link Result} contains an value.
    *
    * @return <code>true</code> when the {@link Result} contains a value; otherwise, <code>false</code>.
    */

   public boolean isPresentValue() {
      return Objects.nonNull(this.value);
   };

   /**
    * When the {@link Result} contains an error, the <code>errorMapper</code> is applied to the error and the result is
    * returned with {@link Result#ofErrorNullable}.
    *
    * @param <EM> the return type of the <code>errorMapper</code> function.
    * @param errorMapper the mapping function applied to the error when present.
    * @return when an error is present a {@link Result} with the mapped error value; otherwise, <code>this</code>.
    * @throws NullPointerException when the {@link Result} contains an error and <code>errorMapper</code> is
    * <code>null</code>.
    */

   public <EM> @NonNull Result<V, EM> mapError(@NonNull ErrorMapper<E, EM> errorMapper) {

      if (Objects.isNull(this.error)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<V, EM>) this;
         return rv;
      }

      return Result.ofErrorNullable(Objects.requireNonNull(errorMapper).apply(this.error));
   };

   /**
    * When the {@link Result} contains an error, the <code>errorToValueMapper</code> is applied to the error and the
    * result is returned with {@link Result#ofValueNullable}.
    *
    * @param <VM> the return type of the <code>errorToValueMapper</code> function.
    * @param errorToValueMapper the mapping function applied to the error when present.
    * @return when an error is present a {@link Result} with the mapped value; otherwise, <code>this</code>.
    * @throws NullPointerException when the {@link Result} contains an error and <code>errorToValueMapper</code> is
    * <code>null</code>.
    */

   public <VM> @NonNull Result<VM, E> mapError(@NonNull ErrorToValueMapper<VM, E> errorToValueMapper) {

      if (Objects.isNull(this.error)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<VM, E>) this;
         return rv;
      }

      return Result.ofValueNullable(Objects.requireNonNull(errorToValueMapper).apply(this.error));
   }

   /**
    * When the {@link Result} contains a value, the <code>valueMapper</code> function is applied to the value and the
    * result is returned with {@link Result#ofValueNullable}.
    *
    * @param <VM> the return type of the <code>valueMapper</code> function.
    * @param valueMapper the mapping function applied the value when present.
    * @return when a value is present a {@link Result} with the mapped value; otherwise, <code>this</code>.
    * @throws NullPointerException when the {@link Result} contains a value and <code>valueMapper</code> is
    * <code>null</code>.
    */

   public <VM> @NonNull Result<VM, E> mapValue(@NonNull ValueMapper<V, VM> valueMapper) {

      if (Objects.isNull(this.value)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<VM, E>) this;
         return rv;
      }

      return Result.ofValueNullable(Objects.requireNonNull(valueMapper).apply(this.value));
   }

   /**
    * When the {@link Result} contains a value, the <code>valueMapper</code> function is applied to the value and the
    * result is returned with {@link Result#ofValueNullable}. If the <code>valueMapper</code> function throws an
    * exception, the <code>throwableToErrorMapper</code> function is applied to the value and exception; and the result
    * is returned with {@link Result#ofErrorNullable}.
    *
    * @param <VM> the return type of the <code>valueMapper</code> function.
    * @param valueMapper the mapping function to applied to the value when present.
    * @param throwableToErrorMapper the mapping function to apply to an exception thrown by the <code>valueMapper</code>
    * function.
    * @return When:
    * <ul>
    * <li>The value is present and the <code>valueMapper</code> completes without exception, a {@link Result} with the
    * return value of the <code>valueMapper</code> function.</li>
    * <li>The value is present and the <code>valueMapper</code> throws an exception, a {@link Result} with an error that
    * is the result of applying the <code>exceptionMapper</code> to the thrown exception.</li>
    * <li>The value and error are not present, an empty {@Result}.</li>
    * <li>An error is present, <code>this</code>.</li>
    * </ul>
    * @throws NullPointerException when the {@link Result} contains a value and <code>valueMapper</code> is
    * <code>null</code>; or when the {@link Result} contains a value, <code>valueMapper</code> throws an exception, and
    * <code>mapExceptionMapper</code> is <code>null</code>.
    */

   public <VM> @NonNull Result<VM, E> mapValue(@NonNull ValueMapper<V, VM> valueMapper, @NonNull ThrowableToErrorMapper<? super V, ? extends E> throwableToErrorMapper) {

      if (Objects.isNull(this.value)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<VM, E>) this;
         return rv;
      }

      try {
         return Result.ofValueNullable(Objects.requireNonNull(valueMapper).apply(this.value));
      } catch (Exception e) {
         return Result.ofErrorNullable(Objects.requireNonNull(throwableToErrorMapper).apply(this.value, e));
      }
   }

   /**
    * When the {@link Result} contains a value, the <code>valueToErrorMapper</code> function is applied to the value and
    * the result is returned with {@link Result#ofErrorNullable}.
    *
    * @param <EM> the return type of the <code>valueToErrorMapper</code> function.
    * @param valueToErroprMapper the mapping function applied to the value when present.
    * @return when a value is present a {@link Result} with the mapped error; otherwise, <code>this</code>.
    * @throws NullPointerException when the {@link Result} contains a value and <code>valueToErrorMapper</code> is
    * <code>null</code>.
    */

   public <EM> @NonNull Result<V, EM> mapValue(@NonNull ValueToErrorMapper<V, EM> valueToErrorMapper) {

      if (Objects.isNull(this.value)) {
         @SuppressWarnings("unchecked")
         var rv = (Result<V, EM>) this;
         return rv;
      }

      return Result.ofErrorNullable(Objects.requireNonNull(valueToErrorMapper).apply(this.value));
   }

   /**
    * When the {@link Result} does not contain a value, the {@link Result} obtained from the <code>resultSupplier</code>
    * is returned; otherwise <code>this</code> {@link Result} is returned.
    *
    * @param resultSupplier a method that provides a {@link Result}<code>&lt;V,E&gt;</code>.
    * @return when a value is present <code>this</code>; otherwise, the {@link Result} returned from the
    * <code>resultSupplier</code>.
    * @throws NullPointerException when the {@link Result} does not contain a value and <code>resultSupplier</code> is
    * <code>null</code>.
    */

   public @NonNull Result<V, E> or(@NonNull ResultSupplier<V, E> resultSupplier) {

      //@formatter:off
      var result =
         Objects.nonNull( this.value )
            ? this
            : Objects.requireNonNull( resultSupplier ).get();
      //@formatter:on
      return result;
   }

   /**
    * <dl>
    * <dt>When the {@link Result} contains a value,</dt>
    * <dd>the value is returned.</dd>
    * <dt>When the {@link Result} contains an error,</dt>
    * <dd>the error is mapped into a {@link Result} with <code>errorToResultMapper</code> and if that {@link Result}
    * contains a value that value is returned; otherwise, the value returned from <code>valueSupplier</code> is
    * returned.</dd>
    * <dt>When the {@link Result} is empty,</dt>
    * <dd>the value returned from <code>valueSupplier</code> is returned.</dd>
    * </dl>
    *
    * @param errorToResultMapper a function to generate a {@link Result} from this {@link Result}'s error.
    * @param valueSupplier a function that returns a value of type <code>&lt;V&gt;</code>.
    * @return the determined value.
    * @throws NullPointerException When:
    * <ul>
    * <li>the {@link Result} contains an error and <code>errorToResultMapper</code> is <code>null</code>, or</li>
    * <li>the {@link Result} contains an error and <code>errorToResultMapper</code> returns a {@link Result} that does
    * not contain a value and <code>valueSupplier</code> is <code>null</code>.
    * <li>the {@link Result} is empty and <code>valueSupplier</code> is <code>null</code>.
    */

   public @Nullable V orElseGet(@NonNull ErrorToResultMapper<V, E, E> errorToResultMapper, @NonNull ValueSupplier<V> valueSupplier) {

      //@formatter:off
      var result =
         Objects.nonNull( this.value )
            ? this.value
            : Objects.nonNull( this.error )
                 ? Objects.requireNonNull(errorToResultMapper).apply(this.error).orElseGet(valueSupplier)
                 : Objects.requireNonNull(valueSupplier).get();
     //@formatter:on
      return result;
   }

   /**
    * When the {@link Result} contains a value the value is returned; otherwise, the <code>otherValue</code> is
    * returned.
    *
    * @param otherValue the value to be returned when a value is not present in the {@link Result}.
    * @return the value when present; otherwise <code>otherValue</code> when a value is not present in the
    * {@link Result}.
    */

   public @Nullable V orElseGet(@Nullable V otherValue) {

      //@formatter:off
      var result =
         Objects.nonNull( this.value )
            ? this.value
            : otherValue;
      //@formatter:on
      return result;
   }

   /**
    * When the {@link Result} contains a value the value is returned; otherwise, the value returned from the
    * <code>valueSupplier</code> is returned.
    *
    * @param valueSupplier a method that returns a value of type <code>&lt;V&gt;</code>.
    * @return the value when present; otherwise the return value from <code>valueSupplier</code>.
    * @throws NullPointerException when the {@link Result} does not contain a value and <code>valueSupplier</code> is
    * <code>null</code>.
    */

   public @Nullable V orElseGet(@NonNull ValueSupplier<V> valueSupplier) {

      //@formatter:off
      return
         Objects.nonNull( this.value )
            ? this.value
            : Objects.requireNonNull( valueSupplier ).get();
      //@formatter:on
   }

   /**
    * <dl>
    * <dt>When the {@link Result} contains a value</dt>
    * <dd>the value is returned.</dd>
    * <dt>When the {@link Result} contains an error</dt>
    * <dd>a {@link NoSuchElementException} is thrown with the string representation of the error as the exception
    * message.</dd>
    * <dt>When the {@link Result} is empty</dt>
    * <dd>a {@link NoSuchElementException} without a message is thrown.</dd>
    * </dl>
    *
    * @return the {@link Result} value.
    * @throws NoSuchElementException when the {@link Result} does not contain a value.
    */

   public @NonNull V orElseThrow() {

      if (Objects.nonNull(this.value)) {
         return this.value;
      }

      if (Objects.nonNull(this.error)) {
         throw new NoSuchElementException(this.error.toString());
      }

      throw new NoSuchElementException();
   }

   /**
    * <dl>
    * <dt>When the {@link Result} contains a value</dt>
    * <dd>the value is returned.</dd>
    * <dt>When the {@link Result} contains an error</dt>
    * <dd>the <code>errorToThrowableMapper</code> is applied to the error and the returned {@link Throwable} is
    * thrown.</dd>
    * <dt>When the {@link Result} is empty</dt>
    * <dd>a {@link NoSuchElementException} without a message is thrown.</dd>
    * </dl>
    *
    * @param <T> the type of {@link Throwable} to be thrown when the {@link Result} contains an error.
    * @param errorToThrowableMapper the mapping function applied to the error to produce a {@link Throwable}.
    * @return the {@link Result} value.
    * @throws T when the {@link Result} contains an error.
    * @throws NoSuchElementException when the {@link Result} is empty.
    * @throws NullPointerException when the {@link Result} contains an error and <code>errorToThrowableMapper</code> is
    * <code>null</code>.
    */

   public <T extends Throwable> @NonNull V orElseThrow(@NonNull ErrorToThrowableMapper<E, T> errorToThrowableMapper) throws T {

      if (Objects.nonNull(this.value)) {
         return this.value;
      }

      if (Objects.nonNull(this.error)) {
         throw Objects.requireNonNull(errorToThrowableMapper).apply(this.error);
      }

      throw new NoSuchElementException();
   }

   /**
    * When the {@link Result} is empty, the {@link Result} obtained from the <code>resultSupplier</code> is returned;
    * otherwise <code>this</code> {@link Result} is returned.
    *
    * @param resultSupplier a method that provides a {@link Result}<code>&lt;V,E&gt;</code>.
    * @return when a value is present <code>this</code>; otherwise, the {@link Result} returned from the
    * <code>resultSupplier</code>.
    * @throws NullPointerException when the {@link Result} does not contain a value and <code>resultSupplier</code> is
    * <code>null</code>.
    */

   public @NonNull Result<V, E> orWhenEmpty(@NonNull ResultSupplier<V, E> resultSupplier) {

      //@formatter:off
      var result =
         Objects.nonNull( this.value ) || Objects.nonNull( this.error )
            ? this
            : Objects.requireNonNull(Objects.requireNonNull(resultSupplier).get());
      //@formatter:on
      return result;

   }

   /**
    * When the {@link Result} contains an error, the {@link Result} obtained from the <code>resultSupplier</code> is
    * returned; otherwise <code>this</code> {@link Result} is returned.
    *
    * @param resultSupplier a method that provides a {@link Result}<code>&lt;V,E&gt;</code>.
    * @return when an error is present <code>this</code>; otherwise, the {@link Result} returned from the
    * <code>resultSupplier</code>.
    * @throws NullPointerException when the {@link Result} does contains an error and <code>resultSupplier</code> is
    * <code>null</code>.
    */

   public @NonNull Result<V, E> orWhenError(@NonNull ResultSupplier<V, E> resultSupplier) {

      //@formatter:off
      var result =
         Objects.nonNull( this.value ) || Objects.isNull( this.error )
            ? this
            : Objects.requireNonNull(Objects.requireNonNull(resultSupplier).get());
      //@formatter:on
      return result;

   }

   /**
    * When the {@link Result} is empty, the {@link Result} obtained from the <code>resultSupplier</code> is returned;
    * otherwise <code>this</code> {@link Result} is returned.
    *
    * @param resultSupplier a method that provides a {@link Result}<code>&lt;V,E&gt;</code>.
    * @return when a value or error is present <code>this</code>; otherwise, the {@link Result} returned from the
    * <code>resultSupplier</code>.
    * @throws NullPointerException when the {@link Result} is empty and <code>resultSupplier</code> is
    * <code>null</code>.
    */

   public @NonNull Result<V, E> peekEmpty(@NonNull Runnable action) {
      if (Objects.isNull(this.value) && Objects.isNull(this.error)) {
         action.run();
      }
      return this;
   }

   /**
    * When the {@link Result} contains an error the <code>errorAction</code> is applied to the error.
    *
    * @param errorAction the {@link ErrorAction} to be applied to the error.
    * @return this {@link Result}.
    */

   public @NonNull Result<V, E> peekError(@NonNull ErrorAction<E> errorAction) {

      if (Objects.nonNull(this.error)) {
         Objects.requireNonNull(errorAction).accept(this.error);
      }
      return this;
   }

   /**
    * When the {@link Result} contains a value the <code>valueAction</code> is applied to the value.
    *
    * @param valueAction the {@link ValueAction} to be applied to the value.
    * @return this {@link Result}.
    */

   public @NonNull Result<V, E> peekValue(@NonNull ValueAction<V> valueAction) {

      if (Objects.nonNull(this.value)) {
         Objects.requireNonNull(valueAction).accept(this.value);
      }
      return this;
   }

   /**
    * When the {@link Result} contains an error object it is processed with the <code>errorReporter</code> and the
    * method returns <code>true</code>; otherwise the method returns <code>false</code>.
    *
    * @param errorReporter the implementation of the {@link Consumer} functional interface that will be called with the
    * error object, when present.
    * @return <code>true</code> when an error is present; otherwise, <code>false</code>.
    * @throws NullPointerException when the {@link Result} contains an error and <code>errorReporter</code> is
    * <code>null</code>.
    */

   public boolean reportIfError(@NonNull ErrorAction<E> errorAction) {

      if (Objects.nonNull(this.error)) {
         Objects.requireNonNull(errorAction).accept(this.error);
         return true;
      }

      return false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull Message toMessage(int indent, @Nullable Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Result" )
         .indentInc()
         ;

      if( this.value instanceof ToMessage ) {
         outMessage
            .title( "Value" )
            .indentInc()
            .toMessage( (ToMessage) this.value )
            .indentDec()
            ;
      } else {
         outMessage
            .segment( "Value", this.value )
            ;
      }

      if( this.error instanceof ToMessage ) {
         outMessage
            .title( "Error" )
            .indentInc()
            .toMessage( (ToMessage) this.error )
            .indentDec()
            ;
      } else {
         outMessage
            .segment( "Error", this.error )
            ;
      }

      outMessage
         .indentDec()
         ;

      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull String toString() {
      return Conditions.requireNonNull(this.toMessage(0, null).toString());
   }

}

/* EOF */
