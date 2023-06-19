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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import java.util.Objects;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.ide.demo.DemoChoice;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.exceptionregistry.ExceptionRegistryEntry;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.orcs.rest.model.ExceptionRegistryEndpoint;

//@formatter:off
/**
 * Class that can be used to suppress server side exception logging for an expected exception and to apply assertions to
 * the received server exception. The general usage of this class follows the pattern:
 *
 * <pre>
 *    try (
 *          var exceptionLogBlocker =
 *              new ExceptionLogBlocker
 *                     (
 *                        "server.side.primary.exception.class.name",
 *                        "server.side.secondary.cause.excption.class.name",
 *                        "client.side.exception.class.name",
 *                        "Expected Exception Message Regular Expression"
 *                     )
 *        )
 *    {
 *       try {
 *          var returnValue = XyzEndpoint.xyzMethod(testParamA, testParamB, testParamC);
 *
 *          exceptionLogBlock.assertNoException();
 *
 *       } catch (Exception e) {
 *
 *          exceptionLogBlocker.assertExpectedException(e);
 *       }
 *    }
 * </pre>
 *
 * The class constructor sends an exception log suppression request to the server for exceptions of the specified
 * (primary) class with a (secondary) cause. The class implements the interface {@link AutoCloseable}. The class's
 * {@link #close} method sends an request to the server to remove the specified exception from the log suppression list.
 *
 * @author Loren K. Ashley
 */
//@formatter:on

public class ExceptionLogBlocker implements AutoCloseable {

   /**
    * Saves a handle to the REST API endpoint for making exception log suppression requests.
    */

   ExceptionRegistryEndpoint exceptionRegistryEndpoint;

   /**
    * Save the fully qualified class name of the expected client exception.
    */

   String expectedClientExceptionClassName;

   /**
    * An {@link ExceptionRegistryEntry} saves the fully qualified class name of the expected server exception and the
    * fully qualified class name of the secondary (cause) server exception.
    */

   ExceptionRegistryEntry expectedException;

   /**
    * Save the Regular Expression used to test the client's exception message.
    */

   Pattern expectedMessagePattern;

   /**
    * Creates a new {@link ExceptionLogBlocker} objects and registers the exception specified by the parameters
    * <code>primary</code> and <code>secondary</code> with the server for log suppression.
    *
    * @param primary The fully qualified class name of the server side exception to suppress log messages for.
    * @param secondary When specified, only server side exceptions with a secondary (cause) exception are suppressed.
    * This parameter may be <code>null</code> or a fully qualified class name.
    * @param expectedClientExceptionClassName The fully qualified class name of the expected client side exception.
    * @param expectedMessageRegex A regular expression used to verify the presence of expected text in the client side
    * exception message. This parameter may be <code>null</code>.
    */

   public ExceptionLogBlocker(String primary, String secondary, String expectedClientExceptionClassName, String expectedMessageRegex) {

      this.expectedException = new ExceptionRegistryEntry(primary, secondary);

      this.expectedClientExceptionClassName = expectedClientExceptionClassName;

      this.expectedMessagePattern =
         Objects.nonNull(expectedMessageRegex) ? Pattern.compile(expectedMessageRegex) : null;

      this.exceptionRegistryEndpoint =
         OsgiUtil.getService(DemoChoice.class, OseeClient.class).getExceptionRegistryEndpoint();

      this.exceptionRegistryEndpoint.setException(this.expectedException);
   }

   /**
    * Asserts the client side exception has the expected fully qualified class name.
    *
    * @param throwable The client side exception.
    * @throws AssertionError when the exception specified by the parameter <code>throwable</code> does not have the
    * expected fully qualified class name.
    */

   private void assertExpectedClientExceptionClassName(Throwable throwable) {

      var clientExceptionClassName = throwable.getClass().getName();

      if (!this.expectedClientExceptionClassName.equals(clientExceptionClassName)) {
         //@formatter:off
         throw
            new AssertionError
                   (
                     new Message()
                            .title( "Unexpected client exception." )
                            .indentInc()
                            .segment( "Expected Client Exception Class Name", this.expectedClientExceptionClassName )
                            .segment( "Actual Client Exception Class Name",   clientExceptionClassName )
                            .indentDec()
                            .reasonFollows( "Actual Client Exception Follows", throwable )
                            .toString()
                   );
         //@formatter:on
      }
   }

   /**
    * Tests the client side exception is as expected.
    *
    * @param throwable The client side exception to be tested.
    * @throws AssertionError when any of the following tests fail:
    * <ul>
    * <li>The client side exception fully qualified class name is not as expected;</li>
    * <li>The client side exception message does not contain the fully qualified class name of the server side primary
    * exception;</li>
    * <li>When a secondary (cause) was specified, and the client side exception message does not contain the fully
    * qualified class name of the sever side secondary (cause) exception; or</li>
    * <li>When an expected message regular expression was specified, the regular expression does not match within the
    * client side exception message.</li>
    * </ul>
    */

   public void assertExpectedException(Throwable throwable) {

      this.assertExpectedClientExceptionClassName(throwable);

      this.assertPrimaryExceptionClassNameInMessage(throwable);

      this.assertSecondaryExceptionClassNameInMessage(throwable);

      this.assertExpectedMessagePattern(throwable);
   }

   /**
    * Tests the client side exception is as expected.
    *
    * @param throwable The client side exception to be tested.
    * @throws AssertionError when any of the following tests fail:
    * <ul>
    * <li>The client side exception fully qualified class name is not as expected;</li>
    * <li>When an expected message regular expression was specified, the regular expression does not match within the
    * client side exception message.</li>
    * </ul>
    */

   public void assertExpectedExceptionNoServerExceptionClassNameChecks(Throwable throwable) {

      this.assertExpectedClientExceptionClassName(throwable);

      this.assertExpectedMessagePattern(throwable);
   }

   /**
    * Tests the client side exception message contains the expected words.
    *
    * @param throwable The client side exception to be tested.
    * @throws AssertionError when an expected message regular expression was specified and the specified regular
    * expression does not match within the exception message.
    */

   private void assertExpectedMessagePattern(Throwable throwable) {

      var exceptionMessage = throwable.getMessage();

      if (Objects.isNull(this.expectedMessagePattern)) {
         /*
          * A regular expression was not specified, nothing to do
          */
         return;
      }

      if (Objects.nonNull(exceptionMessage)) {

         var expectedMessageMatcher = this.expectedMessagePattern.matcher(exceptionMessage);

         if (!expectedMessageMatcher.find()) {
            //@formatter:off
            throw
               new AssertionError
                      (
                         new Message()
                                .title( "Expected excpetion message not found." )
                                .indentInc()
                                .segment( "Expected Message Regex", this.expectedMessagePattern.toString() )
                                .indentDec()
                                .reasonFollows( "Received Exception Follows", throwable )
                                .toString()
                      );
            //@formatter:on
         }
      } else {
         //@formatter:off
         throw
            new AssertionError
                   (
                      new Message()
                         .title( "Expected exception message not found." )
                         .indentInc()
                         .segment( "Expected Message Regex", this.expectedMessagePattern.toString() )
                         .title(   "Client exception has a (null) message." )
                         .toString()
                   );
         //@formatter:on
      }
   }

   /**
    * Throws an {@link AssertionError} with a message indicating the expected exception did not occur.
    *
    * @throws AssertionError with message indicating expected exception did not occur.
    */

   public void assertNoException() {
      //@formatter:off
      throw
         new AssertionError
                (
                   new Message()
                          .title( "Expected exception did not occur." )
                          .indentInc()
                          .segment(      "Expected Primary Exception", this.expectedException.getPrimary() )
                          .segmentIfNot( "Expected Secondary Exception", this.expectedException.getSecondary(), "(none)" )
                          .toString()
                );
      //@formatter:on
   }

   /**
    * Verifies the client side exception message contains the fully qualified server side primary exception name.
    *
    * @param throwable The client side exception to be tested.
    * @throws AssertionError when the client side exception message does not contain the fully qualified server side
    * primary exception name or the client side exception message is <code>null</code>.
    */

   private void assertPrimaryExceptionClassNameInMessage(Throwable throwable) {

      var exceptionMessage = throwable.getMessage();

      if (Objects.nonNull(exceptionMessage)) {

         if (!exceptionMessage.contains(this.expectedException.getPrimary())) {
            //@formatter:off
            throw
               new AssertionError
                      (
                        new Message()
                               .title( "Expected primary server exception class name not found in exception message." )
                               .indentInc()
                               .segment( "Expected Primary Exception Class", this.expectedException.getPrimary() )
                               .indentDec()
                               .reasonFollows( "Received Exception Follows", throwable)
                               .toString()
                      );
            //@formatter:on
         }

      } else {
         //@formatter:off
         throw
            new AssertionError
                   (
                     new Message()
                            .title( "Expected primary server exception class name not found in exception message." )
                            .indentInc()
                            .segment( "Expected Primary Exception Class", this.expectedException.getPrimary() )
                            .title(   "Client exception has a (null) message." )
                            .toString()
                   );
         //@formatter:on
      }
   }

   /**
    * Verifies the client side exception message contains the fully qualified server side secondary (cause) exception
    * name.
    *
    * @param throwable The client side exception to be tested.
    * @throws AssertionError when a secondary (cause) server side exception was specified; and the client side exception
    * message does not contain the secondary server side exception fully qualified name.
    */

   private void assertSecondaryExceptionClassNameInMessage(Throwable throwable) {

      if (this.expectedException.getSecondary().equals("(none)")) {

         /*
          * A secondary exception (cause) was not specified, nothing to test
          */

         return;
      }

      var exceptionMessage = throwable.getMessage();

      if (Objects.nonNull(exceptionMessage)) {

         if (!exceptionMessage.contains(this.expectedException.getSecondary())) {
            //@formatter:off
            throw
               new AssertionError
                      (
                        new Message()
                               .title( "Expected secondary server exception class name not found in exception message." )
                               .indentInc()
                               .segment( "Expected Secondary Exception Class", this.expectedException.getPrimary() )
                               .indentDec()
                               .reasonFollows( "Received Exception Follows", throwable)
                               .toString()
                      );
            //@formatter:on
         }

      } else {

         //@formatter:off
         throw
            new AssertionError
                   (
                     new Message()
                            .title( "Expected secondary server exception class name not found in exception message." )
                            .indentInc()
                            .segment( "Expected Secondary Exception Class", this.expectedException.getSecondary() )
                            .title(   "Client exception has a (null) message." )
                            .toString()
                   );
         //@formatter:on
      }
   }

   /**
    * Removes the expected server side exception from the log suppression list.
    */

   @Override
   public void close() {
      this.exceptionRegistryEndpoint.setInclusion(this.expectedException);
   }
}
