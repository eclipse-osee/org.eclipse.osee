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

package org.eclipse.osee.define.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * Class of general purpose methods for validating API method parameters.
 *
 * @author Loren K. Ashley
 */

public class Validation {

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
    * Verifies a parameter is not <code>null</code> and passes a validation {@link Predicate}. When the parameter does
    * not pass the tests a detail message of the failure is appended to a {@link Message}.
    *
    * @param <T> the type of the parameter to be checked.
    * @param parameter the API method parameter to check.
    * @param parameterName the name of the method parameter.
    * @param message a {@link Message} to append a detail message to when the parameter fails validation. This parameter
    * maybe <code>null</code>.
    * @param predicateText a description of the check performed by the predicate.
    * @param isKo a {@link Predicate} that returns <code>true</code> when the parameter is bad.
    * @return when the input parameter <code>message</code> is not <code>null</code>, the parameter
    * <code>message</code>; otherwise, a new {@link Message}.
    */

   public static <T> Message verifyParameter(T parameter, String parameterName, Message message, String predicateText, Predicate<T> isKo) {

      if (Objects.isNull(parameter) || isKo.test(parameter)) {

         message = Objects.nonNull(message) ? message : new Message();

         //@formatter:off
         message
            .title( "Parameter \"" ).append( parameterName ).append( "\" cannot be null or " ).append( predicateText ).append( "." )
            .indentInc()
            .segment( parameterName, parameter )
            .indentDec()
            .blank()
            ;
         //@formatter:on
      }

      return message;
   }

   //@formatter:off
   public static <T,E> Message
      verifyStreamableParameter
         (
            T                     parameter,
            Function<T,Stream<E>> streamFunction,
            String                parameterName,
            Message               message,
            String                predicateText,
            Predicate<T>          isKo,
            String                elementPredicateText,
            Predicate<E>          elementIsKo
         ) {

      if(    Objects.isNull( parameter )
          || isKo.test( parameter ) ) {

         message = Objects.nonNull( message ) ? message : new Message();

         message
            .title( "Parameter \"" ).append( parameterName ).append( "\" cannot be null or " ).append( predicateText ).append( "." )
            .indentInc()
            .segment( parameterName, parameter )
            .indentDec()
            .blank()
            ;

         return message;
      }

      if( streamFunction.apply( parameter ).anyMatch( elementIsKo ) ) {

         message = Objects.nonNull( message ) ? message : new Message();

         message
            .title( "An element of paramter \"" ).append( parameterName ).append( "\" cannot be ").append( elementPredicateText ).append( "." )
            .indentInc()
            .segment( parameterName, parameter )
            .indentDec()
            .blank()
            ;
      }

      return message;
   }
   //@formatter:on

   /**
    * Verifies a parameter is not <code>null</code>. When the parameter does not pass the tests a detail message of the
    * failure is appended to a {@link Message}.
    *
    * @param <T> the type of the parameter to be checked.
    * @param parameter the API method parameter to check.
    * @param parameterName the name of the method parameter.
    * @param message a {@link Message} to append a detail message to when the parameter fails validation. This parameter
    * maybe <code>null</code>.
    * @return when the input parameter <code>message</code> is not <code>null</code>, the parameter
    * <code>message</code>; otherwise, a new {@link Message}.
    */

   public static <T> Message verifyParameter(T parameter, String parameterName, Message message) {

      if (Objects.isNull(parameter)) {

         message = Objects.nonNull(message) ? message : new Message();

         //@formatter:off
         message
            .title( "Parameter \"" ).append( parameterName ).append( "\" cannot be null." )
            .blank()
            ;
         //@formatter:on
      }

      return message;
   }

}

/* EOF */
