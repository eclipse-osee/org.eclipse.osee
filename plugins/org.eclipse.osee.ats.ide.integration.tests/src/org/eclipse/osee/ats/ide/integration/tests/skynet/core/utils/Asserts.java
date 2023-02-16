/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.Assert;

/**
 * Collection of static assertion methods for JUnit testing.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public final class Asserts {

   private Asserts() {
      // Utility
   }

   /**
    * Asserts that an {@link Exception} object is of the expected class and contains the expected {@link String} within
    * it's message.
    *
    * @param message if an {@link AssertionError} is thrown, this parameter is used as the title for the
    * {@link AssertionError} message.
    * @param expectedExceptionClass the expected {@link Class} of the exception.
    * @param expectedExceptionMessageFragment a {@link String} that is expected to be contained within the exception's
    * message.
    * @param exception the exception to be tested.
    * @throws AssertionError when:
    * <ul>
    * <li>the exception is not of the class specified by <code>expectedExceptionClass</code>, or</li>
    * <li>the exception message does not contain the {@link String} specified by
    * <code>expectedExceptionMessageFragment</code>.</li>
    * </ul>
    */

   public static void assertException(String message, Class<? extends Throwable> expectedExceptionClass, String expectedExceptionMessageFragment, Exception exception) {

      if (!expectedExceptionClass.isInstance(exception)) {
         //@formatter:off
         throw
            new AssertionError
                   (
                      new Message()
                             .title( message )
                             .title( "Exception is not of the expected class." )
                             .indentInc()
                             .segment( "Expected Exception Class", expectedExceptionClass.getName() )
                             .segment( "Actual Exception Class",   exception.getClass().getName()   )
                             .toString()
                   );
         //@formatter:on
      }

      if (!exception.getMessage().contains(expectedExceptionMessageFragment)) {
         //@formatter:off
         throw
            new AssertionError
                   (
                      new Message()
                             .title( message )
                             .title( "Exception does not contain expected message." )
                             .segment( "Expected Exception Message Fragment", expectedExceptionMessageFragment )
                             .segment( "Actual Exception Message",            exception.getMessage()           )
                             .toString()
                   );
         //@formatter:on
      }
   }

   public static IStatus assertOperation(IOperation operation, int expectedSeverity) {
      IStatus status = Operations.executeWork(operation);
      Assert.assertEquals(status.toString(), expectedSeverity, status.getSeverity());
      return status;
   }

   public static void assertThatEquals(Map<String, Integer> prevCount, Map<String, Integer> postCount) {
      for (String tableName : prevCount.keySet()) {
         if (!OseeProperties.isInTest()) {
            String equalStr = postCount.get(tableName).equals(prevCount.get(tableName)) ? "Equal" : "ERROR, NotEqual";
            System.out.println(String.format(equalStr + ": [%s] pre[%d] post[%d]", tableName, prevCount.get(tableName),
               postCount.get(tableName)));
         }
      }
      for (String tableName : prevCount.keySet()) {
         Assert.assertTrue(String.format("[%s] count not equal pre[%d] post[%d]", tableName, prevCount.get(tableName),
            postCount.get(tableName)), postCount.get(tableName).equals(prevCount.get(tableName)));
      }
   }

   public static void assertThatIncreased(Map<String, Integer> prevCount, Map<String, Integer> postCount) {
      for (String name : prevCount.keySet()) {
         if (!OseeProperties.isInTest()) {
            String incStr = postCount.get(name) > prevCount.get(name) ? "Increased" : "ERROR, Not Increased";
            System.out.println(
               String.format(incStr + ": [%s] pre[%d] vs post[%d]", name, prevCount.get(name), postCount.get(name)));
         }
      }
      for (String name : prevCount.keySet()) {
         Assert.assertTrue(String.format("[%s] did not increase as expected: pre[%d] vs post[%d]", name,
            prevCount.get(name), postCount.get(name)), postCount.get(name) > prevCount.get(name));
      }
   }

   /**
    * Asserts that a condition is true. If it isn't it throws an {@link AssertionError} with the given message generated
    * by the {@link Supplier}.
    *
    * @param messageSupplier a message {@link Supplier} for the {@link AssertionError} (nullokay)
    * @param condition condition to be checked
    * @throws AssertionError when the <code>condition</code> is <code>false</code>.
    */

   public static void assertTrue(Supplier<String> messageSupplier, boolean condition) {
      if (!condition) {
         var message = Objects.nonNull(messageSupplier) ? messageSupplier.get() : null;
         if (Objects.isNull(message)) {
            throw new AssertionError();
         }
         throw new AssertionError(message);
      }
   }

}
