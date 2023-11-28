/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class of general purpose methods for validating parameters, class members, or method results.
 *
 * @author Loren K. Ashley
 */

public class Validation {

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

            var outMessage = Validation.startTitle(message, className, methodName);

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

            var outMessage = Validation.startTitle(message, className, methodName);

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

            var outMessage = Validation.startTitle(message, className, methodName);

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

   public static Predicate<Object[]> arrayContainsNull = Validation::arrayContainsNull;

   /**
    * This member contains a {@link Predicate} implementation that tests a {@link Collection} for the presence of a
    * <code>null</code> member.
    *
    * @implNote The method {@link Collection#contains} cannot be relied upon because some implementations will throw a
    * {@link NullPointerException} when searching for a <code>null</code>.
    */

   public static Predicate<Collection<?>> collectionContainsNull = Validation::collectionContainsNull;

   /**
    * This member contains a {@link Predicate} implementation that tests a {@link Map} for the presence of a
    * <code>null</code> key.
    */

   public static Predicate<Map<?, ?>> mapContainsNullKey = Validation::mapContainsNullKey;

   /**
    * This member contains a {@link Predicate} implementation that tests a {@link Map} for the presence of a
    * <code>null</code> value.
    */

   public static Predicate<Map<?, ?>> mapContainsNullValue = Validation::mapContainsNullValue;

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
         Validation.startTitle( null, className, methodName )
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
      return Validation.collectionContainsNull.test(map.keySet());
   }

   /**
    * Predicate to determine if a {@link Map} contains a <code>null</code> value.
    *
    * @param map the {@link Map} to be tested.
    * @return <code>true</code> when the {@link Map} contains a <code>null</code> value; otherwise, <code>false</code>.
    */

   public static boolean mapContainsNullValue(Map<?, ?> map) {
      return Validation.collectionContainsNull.test(map.values());
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
   public static <T> Message
      require
         (
            Message                            message,
            T                                  value,
            ValueType                          valueType,
            String                             className,
            String                             methodName,
            String                             valueDescription,
            String                             predicateText,
            Predicate<T>                       isKo
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
   public static <T> Message
      require
         (
            Message                            message,
            T                                  value,
            ValueType                          valueType,
            String                             className,
            String                             methodName,
            String                             valueDescription,
            String                             predicateTextFirst,
            Predicate<T>                       isKoFirst,
            String                             predicateTextSecond,
            Predicate<T>                       isKoSecond
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
   public static <T> T
      require
         (
            T                                  value,
            ValueType                          valueType,
            String                             className,
            String                             methodName,
            String                             valueDescription,
            String                             predicateText,
            Predicate<T>                       isKo,
            Function<String, RuntimeException> exceptionFactory
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
   public static <T> T
      require
         (
            T                                  value,
            ValueType                          valueType,
            String                             className,
            String                             methodName,
            String                             valueDescription,
            String                             predicateTextFirst,
            Predicate<T>                       isKoFirst,
            Function<String, RuntimeException> exceptionFactoryFirst,
            String                             predicateTextSecond,
            Predicate<T>                       isKoSecond,
            Function<String, RuntimeException> exceptionFactorySecond
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
   public static <T> T
      require
         (
            T                                  value,
            ValueType                          valueType,
            String                             className,
            String                             methodName,
            String                             valueDescription,
            String                             predicateTextFirst,
            Predicate<T>                       isKoFirst,
            String                             predicateTextSecond,
            Predicate<T>                       isKoSecond,
            Function<String, RuntimeException> exceptionFactory
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
    * appended with a failure message. The {@link Validation.ValueType} is defaulted to
    * {@link Validation.ValueType#PARAMETER}.
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

   public static <T> Message requireNonNull(Message message, T value, String className, String methodName, String valueDescription) {

      if (Objects.isNull(value)) {
         //@formatter:off
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
         //@formatter:on
      }

      return message;
   }

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

   public static <T> Message requireNonNull(Message message, T value, ValueType valueType, String className, String methodName, String valueDescription) {

      if (Objects.isNull(value)) {
         //@formatter:off
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
         //@formatter:on
      }

      return message;
   }

   /**
    * Tests a value with the {@link Predicate} <code>Objects::isNull</code>. When the {@link Predicate} returns
    * <code>true</code> a {@link NullPointerException} is thrown. The exception message is generated according to the
    * {@link Validation.ValueType#PARAMETER}.
    *
    * @param <T> the java type of the value being tested.
    * @param value the value to be tested.
    * @param className the name of the class the value is being tested in.
    * @param methodName the name of the method the value is being tested in.
    * @param valueDescription the parameter, member, or method name for the value being tested.
    * @return the provided <code>value</code>.
    * @throws NullPointerException when the {@link Predicate} <code>Objects::isNull</code> returns <code>true</code>.
    */

   public static <T> T requireNonNull(T value, String className, String methodName, String valueDescription) {

      if (Objects.isNull(value)) {
         //@formatter:off
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
         //@formatter:on
      }

      return value;
   }

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

   public static <T> T requireNonNull(T value, ValueType valueType, String className, String methodName, String valueDescription) {

      if (Objects.isNull(value)) {
         //@formatter:off
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
         //@formatter:on
      }

      return value;
   }

   /**
    * Tests a value with the {@link Predicate} <code>Objects::nonnNull</code>. When the {@link Predicate} returns
    * <code>true</code> a {@link IllegalStateException} is thrown. The exception message is generated according to the
    * {@link Validation.ValueType#PARAMETER}.
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

   private static Message startTitle(Message message, String className, String methodName) {

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

}

/* EOF */
