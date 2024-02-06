/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Class of general purpose methods for validating parameters, class members, or method results.
 *
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

public final class Conditions {

   /**
    * An enumeration to indicate the type of value being validated.
    */

   public enum ValueType {

      /**
       * This member indicates the value being validated is a class member.
       */

      MEMBER {

         /**
          * {@inheritDoc}
          * <p>
          * Creates a predicate failed message for a class member.
          */

         @Override
         <T> Message koPredicateMessage(Message message, T value, String className, String methodName, String valueDescription, String predicateText) {

            var outMessage = Conditions.startTitle(message, className, methodName);

      //@formatter:off
            outMessage
               .append( "Member \"" )
               .append( valueDescription )
               .append( "\" failed " )
               .append( predicateText )
               .append( " test." )
               .indentInc()
               .segment( valueDescription, value )
               .indentDec();
            //@formatter:on

            return outMessage;
         }
      },

      /**
       * This member indicates the value being validated is a method parameter.
       */

      PARAMETER {

         /**
          * {@inheritDoc}
          * <p>
          * Creates a predicate failed message for a method parameter.
          */

         @Override
         <T> Message koPredicateMessage(Message message, T value, String className, String methodName, String valueDescription, String predicateText) {

            var outMessage = Conditions.startTitle(message, className, methodName);

      //@formatter:off
            outMessage
               .append( "Parameter \"" )
               .append( valueDescription )
               .append( "\" failed " )
               .append( predicateText )
               .append( " test." )
               .indentInc()
               .segment( valueDescription, value )
               .indentDec();
            //@formatter:on

            return outMessage;
         }
      },

      /**
       * This member indicates the value being validated is a method result.
       */

      RESULT {

         /**
          * {@inheritDoc}
          * <p>
          * Creates a predicate failed message for a method result.
          */

         @Override
         <T> Message koPredicateMessage(Message message, T value, String className, String methodName, String valueDescription, String predicateText) {

            var outMessage = Conditions.startTitle(message, className, methodName);

      //@formatter:off
            outMessage
               .append( "The result of \"" )
               .append( valueDescription )
               .append( "\" failed " )
               .append( predicateText )
               .append( " test." )
               .indentInc()
               .segment( valueDescription, value )
               .indentDec();
            //@formatter:on

            return outMessage;
         }
      };

      /**
       * Creates a new enumeration member.
       */

      ValueType() {
      }

      /**
       * Creates a {@link Message} for a failed predicate test.
       *
       * @param <T> the java type of the value that was tested.
       * @param message when non-<code>null</code> the predicate test failed message will be appended to the provided
       * {@link Message}; otherwise, a new {@link Message} will be created.
       * @param value the value that failed the predicate test.
       * @param className the name of the class the validation was performed in.
       * @param methodName the name of the method the validation was performed in.
       * @param valueDescription the parameter name, member name, or method name being validated.
       * @param predicateText a description of the validation pass condition.
       * @return the provided {@link Message} or a new {@link Message} with the predicate test failure message appended.
       */

      abstract <T> Message koPredicateMessage(Message message, T value, String className, String methodName, String valueDescription, String predicateText);

   }

   /**
    * This member contains a {@link Predicate} implementation that tests an {@link Array} for the presence of a
    * <code>null</code> element.
    */

   public static Predicate<Object[]> arrayContainsNull = Conditions::arrayContainsNull;

   /**
    * This member contains a {@link Predicate} implementation that tests a {@link Collection} for the presence of a
    * <code>null</code> member.
    *
    * @implNote The method {@link Collection#contains} cannot be relied upon because some implementations will throw a
    * {@link NullPointerException} when searching for a <code>null</code>.
    */

   public static Predicate<Collection<?>> collectionContainsNull = Conditions::collectionContainsNull;

   /**
    * This member contains a {@link Predicate} implementation that tests a {@link Map} for the presence of a
    * <code>null</code> key.
    */

   public static Predicate<Map<?, ?>> mapContainsNullKey = Conditions::mapContainsNullKey;

   /**
    * This member contains a {@link Predicate} implementation that tests a {@link Map} for the presence of a
    * <code>null</code> value.
    */

   public static Predicate<Map<?, ?>> mapContainsNullValue = Conditions::mapContainsNullValue;

   /**
    * @return true if all of the parameters are null, otherwise returns false. Also returns true when objects is an
    * empty array
    */
   public static boolean allNull(Object... objects) {
      for (Object object : objects) {
         if (object != null) {
            return false;
         }
      }
      return true;
   }

   /**
    * @return true if any of the parameters are null, otherwise returns false.
    */
   public static boolean anyNull(Object... objects) {
      for (Object object : objects) {
         if (object == null) {
            return true;
         }
      }
      return false;
   }

   /**
    * When the <code>value</code> is non-<code>null</code> the <code>function</code> is applied to the
    * <code>value</code> and the result of the <code>function</code> is returned. When <code>value</code> is
    * <code>null</code>, <code>null</code> is returned.
    *
    * @param <T> the return type of the function.
    * @param <V> the type of the function input.
    * @param value the value to be applied to the function when non-<code>null</code>.
    * @param function the {@link Function} to be applied to the <code>value</code> when the <code>value</code> is
    * non-<code>null</code>.
    * @return when <code>value</code> is non-<code>null</code> the result of applying the <code>value</code> to the
    * <code>function</code>; otherwise, <code>null</code>.
    */

   public static <T, V> @Nullable T applyWhenNonNull(@Nullable V value, @Nullable Function<@NonNull V, @Nullable T> function) {

      @Nullable
      T result;
      if ((value != null) && (function != null)) {
         result = function.apply(value);
      } else {
         result = null;
      }
      return result;
   }

   /**
    * When the <code>value</code> is non-<code>null</code> the <code>consumer</code> is applied to the
    * <code>value</code>.
    *
    * @param <V> the type of the function input.
    * @param value the value to be applied to the consumer when non-<code>null</code>.
    * @param consumer the {@link Consumer} to be applied to the <code>value</code> when the <code>value</code> is
    * non-<code>null</code>.
    */

   public static <V> void acceptWhenNonNull(@Nullable V value, @Nullable Consumer<@NonNull V> consumer) {

      if ((value != null) && (consumer != null)) {
         consumer.accept(value);
      }
   }

   /**
    * Predicate to determine if an array contains a <code>null</code> entry.
    *
    * @param array the array to test.
    * @return <code>true</code> when <code>array</code> contains a null element or <code>array</code> is
    * <code>null</code>; otherwise, <code>false</code>.
    */

   public static <T> boolean arrayContainsNull(T[] array) {

      if (Objects.isNull(array)) {
         return true;
      }

      for (var element : array) {
         if (Objects.isNull(element)) {
            return true;
         }
      }

      return false;
   }

   /**
    * Method creates a {@link Predicate} implementation that will apply the <code>elementPredicate</code> to the members
    * of an array in a fail fast manner.
    *
    * @param <T> the type of the array elements.
    * @param elementPredicate the {@link Predicate} to be applied to each member of the array.
    * @return <code>true</code> when all members of the array pass the <code>elementPredicate</code>; otherwise,
    * <code>false</code>.
    */

   public static <T> Predicate<T[]> arrayElementPredicate(Predicate<T> elementPredicate) {
      return (t) -> testArrayMembers(t, elementPredicate);
   }

   public static void assertEquals(int value1, int value2) {
      assertEquals(value1, value2, "");
   }

   public static void assertEquals(int value1, int value2, String message) {
      checkExpressionFailOnTrue(value1 != value2, message + " - Expected %d; Actual %d", value1, value2);
   }

   public static void assertEquals(String expected, String actual) {
      assertTrue(expected.equals(actual), "Expected %1; Actual %2", expected, actual);
   }

   public static void assertFalse(boolean value, String message, Object... data) {
      assertTrue(!value, message, data);
   }

   public static void assertNotEquals(int value1, int value2, String message) {
      checkExpressionFailOnTrue(value1 == value2, message + " - Expected %d; Actual %d", value1, value2);
   }

   public static void assertNotNull(Object obj, String message, Object... data) {
      if (obj == null) {
         throw new OseeArgumentException(message, data);
      }
   }

   public static void assertNotNullOrEmpty(Collection<? extends Object> values, String message, Object... data) {
      if (values == null || values.isEmpty()) {
         throw new OseeArgumentException(message, data);
      }
   }

   public static void assertNotNullOrEmpty(String value, String message, Object... data) {
      if (!Strings.isValid(value)) {
         throw new OseeArgumentException(message, data);
      }
   }

   public static void assertNotSentinel(Id id) {
      if (Id.SENTINEL.equals(id)) {
         throw new AssertionError("Id cannot be negative");
      }
   }

   public static void assertNotSentinel(Id id, String message) {
      if (Id.SENTINEL.equals(id)) {
         throw new AssertionError(message);
      }
   }

   public static void assertSentinel(Id id) {
      if (Id.SENTINEL.equals(id)) {
         return;
      }
      throw new AssertionError("Object is not sentinel when it should be");
   }

   public static void assertTrue(boolean value, String message, Object... data) {
      if (!value) {
         throw new OseeArgumentException(message, data);
      }
   }

   public static void assertValid(Long teamId, String message, Object... data) {
      if (teamId == null || teamId <= 0) {
         throw new OseeArgumentException(message, data);
      }
   }

   /**
    * Builds the message for an {@link IllegalArgumentException} from the class name, method name, and a detail
    * description of the bad input parameters.
    *
    * @param className the simple name of the class
    * @param methodName the name of the method
    * @param message a detail description of the bad input parameters.
    * @return the completed exception message.
    */

   public static String buildIllegalArgumentExceptionMessage(String className, String methodName, Message message) {
      //@formatter:off
      return
         new Message()
                .blank()
                .title( className ).append( "::" ).append( methodName ).append( ", illegal arguments provided." )
                .indentInc()
                .copy( message )
                .blank()
                .toString();
      //@formatter:on
   }

   public static void checkDoesNotContainNulls(Object object, String message, Object... data) {
      checkNotNull(object, message);
      Collection<?> toCheck = null;
      if (object instanceof Collection<?>) {
         toCheck = (Collection<?>) object;
      } else if (object instanceof Object[]) {
         toCheck = Arrays.asList((Object[]) object);
      }
      if (toCheck != null) {
         for (Object item : toCheck) {
            if (item == null) {
               throw new OseeArgumentException(message, data);
            }
         }
      } else {
         throw new OseeArgumentException("object is not an array or a collection");
      }
   }

   public static void checkExpressionFailOnTrue(boolean result, String message, Object... data) {
      if (result) {
         throw new OseeArgumentException(message, data);
      }
   }

   public static void checkNotNull(Object object, String objectName) {
      if (object == null) {
         throw new OseeArgumentException("%s cannot be null", objectName);
      }
   }

   public static void checkNotNull(Object object, String objectName, String details, Object... data) {
      if (object == null) {
         String message = String.format(details, data);
         throw new OseeArgumentException("%s cannot be null - %s", objectName, message);
      }
   }

   public static void checkNotNullOrContainNull(Collection<? extends Object> collection, String objectName) {
      checkNotNull(collection, objectName);
      for (Object object : collection) {
         checkNotNull(object, objectName);
      }
   }

   public static void checkNotNullOrEmpty(Collection<? extends Object> collection, String objectName) {
      checkNotNull(collection, objectName);
      if (collection.isEmpty()) {
         throw new OseeArgumentException("%s cannot be empty", objectName);
      }
   }

   public static void checkNotNullOrEmpty(Object[] array, String objectName) {
      checkNotNull(array, objectName);
      if (array.length <= 0) {
         throw new OseeArgumentException("%s cannot be empty", objectName);
      }
   }

   public static void checkNotNullOrEmpty(String object, String objectName) {
      checkNotNull(object, objectName);
      if (object.length() == 0) {
         throw new OseeArgumentException("%s cannot be empty", objectName);
      }
   }

   public static void checkNotNullOrEmpty(String object, String objectName, String details, Object... data) {
      checkNotNull(object, objectName, details, data);
      if (object.length() == 0) {
         String message = String.format(details, data);
         throw new OseeArgumentException("%s cannot be empty - %s", objectName, message);
      }
   }

   public static void checkNotNullOrEmptyOrContainNull(Collection<? extends Object> collection, String objectName) {
      checkNotNullOrEmpty(collection, objectName);
      for (Object object : collection) {
         checkNotNull(object, objectName);
      }
   }

   public static void checkValid(Id id, String string, Object... data) {
      assertTrue(id.isValid(), string);
   }

   /**
    * Predicate to determine if a {@link Collection} contains a <code>null</code>.
    *
    * @param collection the {@link Collection} to be searched.
    * @return <code>true</code> when the {@link Collection} <code>collection</code> contains a <code>null</code>
    * element, <code>collection</code> is not an instance of {@link Collection}, or <code>collection</code> is
    * <code>null</code>; otherwise, <code>false</code>.
    * @implNote The method {@link Collection#contains} cannot be relied upon because some implementations will throw a
    * {@link NullPointerException} when searching for a <code>null</code>.
    */

   public static <T> boolean collectionContainsNull(T collection) {

      if (!(collection instanceof Collection)) {
         return true;
      }

      for (var entry : (Collection<?>) collection) {
         if (Objects.isNull(entry)) {
            return true;
         }
      }

      return false;
   }

   /**
    * Method creates a {@link Predicate} implementation that will apply the <code>elementPredicate</code> to the members
    * of a {@link Collection} in a fail fast manner.
    *
    * @param <T> the type of the {@link Collection} elements.
    * @param elementPredicate the {@link Predicate} to be applied to each member of the collection.
    * @return <code>true</code> when all members of the {@link Collection} pass the <code>elementPredicate</code>;
    * otherwise, <code>false</code>.
    */

   public static <T> Predicate<Collection<T>> collectionElementPredicate(Predicate<T> elementPredicate) {
      return (t) -> testCollectionMembers(t, elementPredicate);
   }

   public static <T> @NonNull T getNotNull(T obj, String message, Object... data) {
      if (obj == null) {
         throw new OseeArgumentException(message, data);
      }
      return obj;
   }

   /**
    * @return false if the parameter is null or empty, otherwise return true
    */
   public static boolean hasValues(Collection<?> toCheck) {
      return toCheck != null && !toCheck.isEmpty();
   }

   /**
    * @return false if the parameter is null or empty, otherwise return true
    */
   public static boolean hasValues(Object[] toCheck) {
      return toCheck != null && toCheck.length > 0;
   }

   /**
    * @return true if any of the objects are equal to the equalTo object, otherwise returns false.
    */
   public static boolean in(Object equalTo, Object... objects) {
      for (Object object : objects) {
         if (equalTo.equals(object)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Generates an exception message for an unexpected switch case and throws the exception produced by the
    * <code>exceptionFactory</code>.
    *
    * @param <T> the type of value that is the switch parameter.
    * @param value the switch value.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @param exceptionFactory a {@link Function} that creates a {@link RuntimeException} with an error message.
    */

   public static <T> RuntimeException invalidCase(T value, String className, String methodName, String valueDescription, Function<String, RuntimeException> exceptionFactory) {

      //@formatter:off
      var message =
         Conditions.startTitle( null, className, methodName )
            .append( "Unexpected switch case with parameter \"" )
            .append( valueDescription )
            .append( "\"." )
            .indentInc()
            .segment( valueDescription, value )
            .indentDec()
            .toString();
      //@formatter:on

      return exceptionFactory.apply(message);
   }

   /**
    * Predicate to determine if a {@link Map} contains a <code>null</code> key.
    *
    * @param map the {@link Map} to be tested.
    * @return <code>true</code> when the {@link Map} contains a <code>null</code> key; otherwise, <code>false</code>.
    */

   public static boolean mapContainsNullKey(Map<?, ?> map) {
      return Conditions.collectionContainsNull.test(map.keySet());
   }

   /**
    * Predicate to determine if a {@link Map} contains a <code>null</code> value.
    *
    * @param map the {@link Map} to be tested.
    * @return <code>true</code> when the {@link Map} contains a <code>null</code> value; otherwise, <code>false</code>.
    */

   public static boolean mapContainsNullValue(Map<?, ?> map) {
      return Conditions.collectionContainsNull.test(map.values());
   }

   /**
    * @return false if any of the parameters are null, otherwise returns true.
    */

   public static boolean notNull(Object... objects) {
      for (Object object : objects) {
         if (object == null) {
            return false;
         }
      }
      return true;
   }

   /**
    * Method creates a short circuit logical OR {@link Predicate} implementation from two predicates.
    *
    * @param <T> The type to be tested.
    * @param firstPredicate the first predicate to be evaluated.
    * @param secondPredicate the second predicate to be evaluated.
    * @return the logic OR of the results from the predicates <code>firstPredicate</code> and
    * <code>secondPredicate</code>.
    */

   public static <T> Predicate<T> or(Predicate<T> firstPredicate, Predicate<T> secondPredicate) {
      return (t) -> firstPredicate.test(t) || secondPredicate.test(t);
   }

   /**
    * A method that can be used to wrap a lambda expression as a {@link Predicate}.
    *
    * @param <T> the type of value tested by the predicate.
    * @param predicate the lambda expression to be wrapped.
    * @return a {@link Predicate}
    */

   public static <T> Predicate<T> predicate(Predicate<T> predicate) {
      return predicate::test;
   }

   /**
    * Tests a value with <code>isKo</code> {@link Predicate}. When the {@link Predicate} returns <code>true</code> the
    * provided <code>message</code> when non-<code>null</code> or a new {@link Message} is appended with a failure
    * message.
    *
    * @param <T> the java type of the value being tested.
    * @param message when not <code>null</code> failure messages will be appended to the provided {@link Message};
    * otherwise a new {@link Message} will be created.
    * @param value the value to be tested.
    * @param valueType the {@link ValueType} of the value being tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @param predicateText a description of the test the value is expected to pass.
    * @param isKo the {@link Predicate} used to test the value for a failure condition.
    * @return the following:
    * <dl>
    * <dt>When the parameter <code>message</code> is <code>null</code> and the <code>isKo</code> predicate returns
    * <code>true</code></dt>
    * <dd>a new {@link Message} containing the failure message.</dd>
    * <dt>When the parameter <code>message</code> is <code>null</code> and the <code>isKo</code> predicate returns
    * <code>false</code></dt>
    * <dd><code>null</code> is returned.</dd>
    * <dt>When the parameter <code>message</code> is non-<code>null</code> and the <code>isKo</code> predicate returns
    * <code>true</code></dt>
    * <dd>the provided <code>message</code> is with the failure message appended.</dd>
    * <dt>When the parameter <code>message</code> is non-<code>null</code> and the <code>isKo</code> predicate returns
    * <code>false</code></dt>
    * <dd>the unmodified provided <code>message</code>.</dd>
    * </dl>
    */

   //@formatter:off
   public static <T> @Nullable Message
      require
         (
            @Nullable Message      message,
            @Nullable T            value,
            @NonNull  ValueType    valueType,
            @NonNull  String       className,
            @NonNull  String       methodName,
            @NonNull  String       valueDescription,
            @NonNull  String       predicateText,
            @NonNull  Predicate<T> isKo
         ) {

      if( isKo.test( value ) ) {

         message =
            valueType
               .koPredicateMessage
                  (
                     message,
                     value,
                     className,
                     methodName,
                     valueDescription,
                     predicateText
                  );

      }

      return message;
   }
   //@formatter:on

   /**
    * Tests the <code>value</code> with the <code>isKoFirst</code> {@link Predicate} and if it returns
    * <code>false</code> the value is then tested with the <code>isKoSecond</code> {@link Predicate}. When either
    * {@link Predicate} returns <code>true</code> the provided <code>message</code> when non-<code>null</code> or a new
    * {@link Message} is appended with a failure message.
    *
    * @param <T> the java type of the value being tested.
    * @param message when not <code>null</code> failure messages will be appended to the provided {@link Message};
    * otherwise a new {@link Message} will be created.
    * @param value the value to be tested.
    * @param valueType the {@link ValueType} of the value being tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @param predicateTextFirst a description of the first test the value is expected to pass.
    * @param isKoFirst the first {@link Predicate} used to test the value for a failure condition.
    * @param predicateTextSecond a description of the second test the value is expected to pass.
    * @param isKoSecond the second {@link Predicate} used to test the value for a failure condition.
    * @return the following:
    * <dl>
    * <dt>When the parameter <code>message</code> is <code>null</code> and the <code>isKoFirst</code> or
    * <code>isKoSecond</code> predicate returns <code>true</code></dt>
    * <dd>a new {@link Message} containing the failure message.</dd>
    * <dt>When the parameter <code>message</code> is <code>null</code> and the <code>isKoFirst</code> and
    * <code>isKoSecond</code> predicates returns <code>false</code></dt>
    * <dd><code>null</code> is returned.</dd>
    * <dt>When the parameter <code>message</code> is non-<code>null</code> and the <code>isKoFirst</code> or
    * <code>isKoSecond</code> predicate returns <code>true</code></dt>
    * <dd>the provided <code>message</code> is with the failure message appended.</dd>
    * <dt>When the parameter <code>message</code> is non-<code>null</code> and the <code>isKoFirst</code> and
    * <code>isKoSecond</code> predicates returns <code>false</code></dt>
    * <dd>the unmodified provided <code>message</code>.</dd>
    * </dl>
    */

   //@formatter:off
   public static <T> @Nullable Message
      require
         (
            @Nullable Message      message,
            @Nullable T            value,
            @NonNull  ValueType    valueType,
            @NonNull  String       className,
            @NonNull  String       methodName,
            @NonNull  String       valueDescription,
            @NonNull  String       predicateTextFirst,
            @NonNull  Predicate<T> isKoFirst,
            @NonNull  String       predicateTextSecond,
            @NonNull  Predicate<T> isKoSecond
         ) {

      if( isKoFirst.test( value ) ) {

         message =
            valueType
               .koPredicateMessage
                  (
                     message,
                     value,
                     className,
                     methodName,
                     valueDescription,
                     predicateTextFirst
                  );

         return message;
      }

      if( isKoSecond.test( value ) ) {

         message =
            valueType
               .koPredicateMessage
                  (
                     message,
                     value,
                     className,
                     methodName,
                     valueDescription,
                     predicateTextSecond
                  );

      }

      return message;
   }
   //@formatter:on

   /**
    * Tests a value with <code>isKo</code> {@link Predicate}. When the {@link Predicate} returns <code>true</code> a
    * {@link RuntimeException} generated by the <code>exceptionFactory</code> is thrown. The exception message is
    * generated according to the <code>valueType</code> and the <code>predicateText</code>.
    *
    * @param <T> the java type of the value being tested.
    * @param value the value to be tested.
    * @param valueType the {@link ValueType} of the value being tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @param predicateText a description of the test the value is expected to pass.
    * @param isKo the {@link Predicate} used to test the value for a failure condition.
    * @param exceptionFactory a {@link Function} that creates a {@link RuntimeException} with an error message.
    * @return the provided <code>value</code>.
    * @throws RuntimeException when the {@link Predicate} <code>isKo</code> returns <code>true</code> a
    * {@link RuntimeException} created by the <code>exceptionFactory</code> is thrown.
    */

   //@formatter:off
   public static <T> @Nullable T
      require
         (
            @Nullable T                                  value,
            @NonNull  ValueType                          valueType,
            @NonNull  String                             className,
            @NonNull  String                             methodName,
            @NonNull  String                             valueDescription,
            @NonNull  String                             predicateText,
            @NonNull  Predicate<T>                       isKo,
            @NonNull  Function<String, RuntimeException> exceptionFactory
         ) {

      if( isKo.test( value ) ) {

         throw
            exceptionFactory.apply
               (
                  valueType
                     .koPredicateMessage
                        (
                           null,
                           value,
                           className,
                           methodName,
                           valueDescription,
                           predicateText
                        )
                     .toString()
               );

      }

      return value;
   }
   //@formatter:on

   /**
    * Tests the <code>value</code> with the <code>isKoFirst</code> {@link Predicate} and if it returns
    * <code>false</code> the value is then tested with the <code>isKoSecond</code> {@link Predicate}. When the predicate
    * <code>isKoFirst</code> returns <code>true</code> a {@link RuntimeException} created with the
    * <code>exceptionFactoryFirst</code> is thrown. The exception message is generated according to the
    * <code>valueType</code> and the <code>predicateTextFirst</code>. When the predicate <code>isKoSecond</code> returns
    * <code>true</code> a {@link RuntimeException} created with the <code>exceptionFactorySecond</code> is thrown. The
    * exception message is generated according to the <code>valueType</code> and the <code>predicateTextSecond</code>.
    *
    * @param <T> the java type of the value being tested.
    * @param value the value to be tested.
    * @param valueType the {@link ValueType} of the value being tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @param predicateTextFirst a description of the first test the value is expected to pass.
    * @param isKo the first {@link Predicate} used to test the value for a failure condition.
    * @param exceptionFactoryFirst a {@link Function} that creates a {@link RuntimeException} with an error message when
    * the predicate <code>isKoFirst</code> returns <code>true</code>.
    * @param predicateTextSecond a description of the second test the value is expected to pass.
    * @param isKoSecond the second {@link Predicate} used to test the value for a failure condition.
    * @param exceptionFactorySecond a {@link Function} that creates a {@link RuntimeException} with an error message
    * when the predicate <code>isKoSecond</code> returns <code>true</code>.
    * @throws RuntimeException
    * <ul>
    * <li>When the {@link Predicate} <code>isKoFirst</code> returns <code>true</code> a {@link RuntimeException} created
    * by the <code>exceptionFactoryFirst</code> is thrown.</li>
    * <li>When the {@link Predicate} <code>isKoSecond</code> returns <code>true</code> a {@link RuntimeException}
    * created by the <code>exceptionFactorySecond</code> is thrown.</li>
    * </ul>
    */

   //@formatter:off
   public static <T> @Nullable T
      require
         (
            @Nullable T                                  value,
            @NonNull  ValueType                          valueType,
            @NonNull  String                             className,
            @NonNull  String                             methodName,
            @NonNull  String                             valueDescription,
            @NonNull  String                             predicateTextFirst,
            @NonNull  Predicate<T>                       isKoFirst,
            @NonNull  Function<String, RuntimeException> exceptionFactoryFirst,
            @NonNull  String                             predicateTextSecond,
            @NonNull  Predicate<T>                       isKoSecond,
            @NonNull  Function<String, RuntimeException> exceptionFactorySecond
         ) {

      if( isKoFirst.test( value ) ) {

         throw
            exceptionFactoryFirst.apply
               (
                  valueType
                     .koPredicateMessage
                        (
                           null,
                           value,
                           className,
                           methodName,
                           valueDescription,
                           predicateTextFirst
                        )
                     .toString()
               );

      }

      if( isKoSecond.test( value ) ) {

         throw
            exceptionFactorySecond.apply
               (
                  valueType
                     .koPredicateMessage
                        (
                           null,
                           value,
                           className,
                           methodName,
                           valueDescription,
                           predicateTextSecond
                        )
                     .toString()
               );

      }

      return value;
   }
   //@formatter:on

   /**
    * Tests the <code>value</code> with the <code>isKoFirst</code> {@link Predicate} and if it returns
    * <code>false</code> the value is then tested with the <code>isKoSecond</code> {@link Predicate}. When the predicate
    * <code>isKoFirst</code> returns <code>true</code> a {@link RuntimeException} created with the
    * <code>exceptionFactory</code> is thrown. The exception message is generated according to the
    * <code>valueType</code> and the <code>predicateTextFirst</code>. When the predicate <code>isKoSecond</code> returns
    * <code>true</code> a {@link RuntimeException} created with the <code>exceptionFactory</code> is thrown. The
    * exception message is generated according to the <code>valueType</code> and the <code>predicateTextSecond</code>.
    *
    * @param <T> the java type of the value being tested.
    * @param value the value to be tested.
    * @param valueType the {@link ValueType} of the value being tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @param predicateTextFirst a description of the first test the value is expected to pass.
    * @param isKo the first {@link Predicate} used to test the value for a failure condition. predicate
    * <code>isKoFirst</code> returns <code>true</code>.
    * @param predicateTextSecond a description of the second test the value is expected to pass.
    * @param isKoSecond the second {@link Predicate} used to test the value for a failure condition.
    * @param exceptionFactory a {@link Function} that creates a {@link RuntimeException} with an error message when the
    * predicate either predicate <code>isKoFirst</code> or <code>isKoSecond</code> returns <code>true</code>.
    * @throws RuntimeException when either {@link Predicate} <code>isKoFirst</code> or <code>isKoSecond</code> returns
    * <code>true</code> a {@link RuntimeException} created by the <code>exceptionFactory</code> is thrown.</li>
    */

   //@formatter:off
   public static <T> @Nullable T
      require
         (
            @Nullable T                                  value,
            @NonNull  ValueType                          valueType,
            @NonNull  String                             className,
            @NonNull  String                             methodName,
            @NonNull  String                             valueDescription,
            @NonNull  String                             predicateTextFirst,
            @NonNull  Predicate<T>                       isKoFirst,
            @NonNull  String                             predicateTextSecond,
            @NonNull  Predicate<T>                       isKoSecond,
            @NonNull  Function<String, RuntimeException> exceptionFactory
         ) {

      if( isKoFirst.test( value ) ) {

         throw
            exceptionFactory.apply
               (
                  valueType
                     .koPredicateMessage
                        (
                           null,
                           value,
                           className,
                           methodName,
                           valueDescription,
                           predicateTextFirst
                        )
                     .toString()
               );

      }

      if( isKoSecond.test( value ) ) {

         throw
            exceptionFactory.apply
               (
                  valueType
                     .koPredicateMessage
                        (
                           null,
                           value,
                           className,
                           methodName,
                           valueDescription,
                           predicateTextSecond
                        )
                     .toString()
               );

      }

      return value;
   }
   //@formatter:on

   /**
    * Tests a value with <code>Objects::isNull</code> {@link Predicate}. When the {@link Predicate} returns
    * <code>true</code> the provided <code>message</code> when non-<code>null</code> or a new {@link Message} is
    * appended with a failure message. The {@link ValueType} is defaulted to {@link ValueType#PARAMETER}.
    *
    * @param <T> the java type of the value being tested.
    * @param message when not <code>null</code> failure messages will be appended to the provided {@link Message};
    * otherwise a new {@link Message} will be created.
    * @param value the value to be tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @return the following:
    * <dl>
    * <dt>When the parameter <code>message</code> is <code>null</code> and the <code>Objects::isNull</code> predicate
    * returns <code>true</code></dt>
    * <dd>a new {@link Message} containing the failure message.</dd>
    * <dt>When the parameter <code>message</code> is <code>null</code> and the <code>Objects::isNull</code> predicate
    * returns <code>false</code></dt>
    * <dd><code>null</code> is returned.</dd>
    * <dt>When the parameter <code>message</code> is non-<code>null</code> and the <code>Objects::isNull</code>
    * predicate returns <code>true</code></dt>
    * <dd>the provided <code>message</code> is with the failure message appended.</dd>
    * <dt>When the parameter <code>message</code> is non-<code>null</code> and the <code>Objects::isNull</code>
    * predicate returns <code>false</code></dt>
    * <dd>the unmodified provided <code>message</code>.</dd>
    * </dl>
    */

   //@formatter:off
   public static <T> @Nullable Message
      requireNonNull
         (
            @Nullable Message message,
            @Nullable T       value,
            @NonNull  String  className,
            @NonNull  String  methodName,
            @NonNull  String  valueDescription
         ) {

      if (Objects.isNull(value)) {

         ValueType.PARAMETER
            .koPredicateMessage
               (
                  message,
                  value,
                  className,
                  methodName,
                  valueDescription,
                  "cannot be null"
               );
      }

      return message;
   }
   //@formatter:on

   /**
    * Tests a value with <code>Objects::isNull</code> {@link Predicate}. When the {@link Predicate} returns
    * <code>true</code> the provided <code>message</code> when non-<code>null</code> or a new {@link Message} is
    * appended with a failure message.
    *
    * @param <T> the java type of the value being tested.
    * @param message when not <code>null</code> failure messages will be appended to the provided {@link Message};
    * otherwise a new {@link Message} will be created.
    * @param value the value to be tested.
    * @param valueType the {@link ValueType} of the value being tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @return the following:
    * <dl>
    * <dt>When the parameter <code>message</code> is <code>null</code> and the <code>Objects::isNull</code> predicate
    * returns <code>true</code></dt>
    * <dd>a new {@link Message} containing the failure message.</dd>
    * <dt>When the parameter <code>message</code> is <code>null</code> and the <code>Objects::isNull</code> predicate
    * returns <code>false</code></dt>
    * <dd><code>null</code> is returned.</dd>
    * <dt>When the parameter <code>message</code> is non-<code>null</code> and the <code>Objects::isNull</code>
    * predicate returns <code>true</code></dt>
    * <dd>the provided <code>message</code> is with the failure message appended.</dd>
    * <dt>When the parameter <code>message</code> is non-<code>null</code> and the <code>Objects::isNull</code>
    * predicate returns <code>false</code></dt>
    * <dd>the unmodified provided <code>message</code>.</dd>
    * </dl>
    */

   //@formatter:off
   public static <T> @Nullable Message
      requireNonNull
         (
            @Nullable Message   message,
            @Nullable T         value,
            @NonNull  ValueType valueType,
            @NonNull  String    className,
            @NonNull  String    methodName,
            @NonNull  String    valueDescription
         ) {

      if (Objects.isNull(value)) {

         valueType
            .koPredicateMessage
               (
                  message,
                  value,
                  className,
                  methodName,
                  valueDescription,
                  "cannot be null"
               );
      }

      return message;
   }
   //@formatter:on

   public static <T> @NonNull T requireNonNull(@Nullable T value) {
      if (value == null) {
         throw new NullPointerException();
      }
      return value;
   }

   /**
    * Tests a value with the {@link Predicate} <code>Objects::isNull</code>. When the {@link Predicate} returns
    * <code>true</code> a {@link NullPointerException} is thrown. The exception message is generated according to the
    * {@link ValueType#PARAMETER}.
    *
    * @param <T> the java type of the value being tested.
    * @param value the value to be tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @return the provided <code>value</code>.
    * @throws NullPointerException when the {@link Predicate} <code>Objects::isNull</code> returns <code>true</code>.
    */

   //@formatter:off
   public static <T> @NonNull T
      requireNonNull
         (
            @Nullable T      value,
            @NonNull  String className,
            @NonNull  String methodName,
            @NonNull  String valueDescription
         ) {

      if ( value == null ) {

         throw
            new NullPointerException
                   (
                      ValueType.PARAMETER
                         .koPredicateMessage
                            (
                               null,
                               value,
                               className,
                               methodName,
                               valueDescription,
                               "cannot be null"
                            )
                         .toString()
                   );
      }

      return value;
   }
   //@formatter:on

   /**
    * Tests a value with the {@link Predicate} <code>Objects::isNull</code>. When the {@link Predicate} returns
    * <code>true</code> a {@link NullPointerException} is thrown. The exception message is generated according to the
    * <code>valueType</code>.
    *
    * @param <T> the java type of the value being tested.
    * @param value the value to be tested.
    * @param valueType the {@link ValueType} of the value being tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @return the provided <code>value</code>.
    * @throws NullPointerException when the {@link Predicate} <code>Objects::isNull</code> returns <code>true</code>.
    */

   //@formatter:off
   public static <T> @NonNull T
      requireNonNull
         (
            @Nullable T         value,
            @NonNull  ValueType valueType,
            @NonNull  String    className,
            @NonNull  String    methodName,
            @NonNull  String    valueDescription
         ) {

      if ( value == null ) {

         throw
            new NullPointerException
                   (
                      valueType
                         .koPredicateMessage
                            (
                               null,
                               value,
                               className,
                               methodName,
                               valueDescription,
                               "cannot be null"
                            )
                         .toString()
                   );

      }

      return value;
   }
   //@formatter:on

   /**
    * Tests a value with the {@link Predicate} <code>Objects::nonnNull</code>. When the {@link Predicate} returns
    * <code>true</code> a {@link IllegalStateException} is thrown. The exception message is generated according to the
    * {@link ValueType#PARAMETER}.
    *
    * @param <T> the java type of the value being tested.
    * @param value the value to be tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @return the provided <code>value</code>.
    * @throws IllegalStateException when the {@link Predicate} <code>Objects::nonNull</code> returns <code>true</code>.
    */

   public static <T> T requireNull(T value, String className, String methodName, String valueDescription) {

      if (Objects.nonNull(value)) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      ValueType.MEMBER
                         .koPredicateMessage
                            (
                               null,
                               value,
                               className,
                               methodName,
                               valueDescription,
                               "cannot be set"
                            )
                         .toString()
                   );
         //@formatter:on
      }

      return value;
   }

   /**
    * Tests a value with the {@link Predicate} <code>Objects::nonnNull</code>. When the {@link Predicate} returns
    * <code>true</code> a {@link IllegalStateException} is thrown. The exception message is generated according to the
    * <code>valueType</code>.
    *
    * @param <T> the java type of the value being tested.
    * @param value the value to be tested.
    * @param valueType the {@link ValueType} of the value being tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @return the provided <code>value</code>.
    * @throws IllegalStateException when the {@link Predicate} <code>Objects::nonNull</code> returns <code>true</code>.
    */

   public static <T> T requireNull(T value, ValueType valueType, String className, String methodName, String valueDescription) {

      if (Objects.nonNull(value)) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      valueType
                         .koPredicateMessage
                            (
                               null,
                               value,
                               className,
                               methodName,
                               valueDescription,
                               "cannot be set"
                            )
                         .toString()
                   );
         //@formatter:on
      }

      return value;
   }

   /**
    * Generates starting portion of the message title as:
    * <ul style="list-style: none;">
    * <li>&lt;class-name&gt; ":" &lt;method-name&gt; ","</li>
    * </ul>
    *
    * @param message when non-<code>null</code> the title prefix is appended to the provided {@link Message}; otherwise,
    * a new {@link Message} is created.
    * @param className the name of the class the validation is performed in.
    * @param methodName the name of the method the validation is performed in.
    * @return a {@link Message} with the starting portion of the title appended.
    */

   static Message startTitle(Message message, String className, String methodName) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .title( className )
         .append( "::" )
         .append( methodName )
         .append( ", " );
      //@formatter:on

      return outMessage;
   }

   /**
    * Tests each member of an array with a {@link Predicate} in a fail fast manner.
    *
    * @param <T> the type of the array members.
    * @param array the array to be tested.
    * @param elementPredicate the {@link Predicate} to test the array members with.
    * @return <code>true</code> when the {@link Predicate} returns <code>true</code> for the first array member;
    * otherwise <code>false</code> when the {@link Predicate} returns <code>false</code> for all {@link Collection}
    * members.
    */

   private static <T> boolean testArrayMembers(T[] array, Predicate<T> elementPredicate) {

      for (var entry : array) {
         if (elementPredicate.test(entry)) {
            return true;
         }
      }
      return false;

   }

   /**
    * Tests each member of a {@link Collection} with a {@link Predicate} in a fail fast manner.
    *
    * @param <T> the type of the collection members.
    * @param collection the {@link Collection} to be tested.
    * @param elementPredicate the {@link Predicate} to test the {@link Collection} members with.
    * @return <code>true</code> when the {@link Predicate} returns <code>true</code> for the first {@link Collection}
    * member; otherwise <code>false</code> when the {@link Predicate} returns <code>false</code> for all
    * {@link Collection} members.
    */

   private static <T> boolean testCollectionMembers(Collection<T> collection, Predicate<T> elementPredicate) {

      for (var entry : collection) {
         if (elementPredicate.test(entry)) {
            return true;
         }
      }
      return false;

   }

   private Conditions() {
      // Utility Class
   }
}
