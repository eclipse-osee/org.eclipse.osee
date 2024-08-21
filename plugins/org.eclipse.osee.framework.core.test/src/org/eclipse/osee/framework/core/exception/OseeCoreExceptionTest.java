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

package org.eclipse.osee.framework.core.exception;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link OseeCoreException}
 *
 * @author Ryan D. Brooks
 */
public class OseeCoreExceptionTest {

   @Test
   public void testNullMessage() {
      Exception ex = new OseeCoreException((String) null);
      Assert.assertEquals(
         "Exception message could not be formatted: [null] with the following arguments [].  Cause [java.lang.NullPointerException]",
         ex.getMessage());
   }

   @Test
   public void testNullMessageNullCause() {
      Exception ex = new OseeCoreException((String) null, (Throwable) null);
      Assert.assertEquals("Exception message unavaliable - both exception and message were null", ex.getMessage());
   }

   @Test
   public void testNullCause() {
      Exception ex = new OseeCoreException("Error message", (Throwable) null);
      Assert.assertEquals("Error message", ex.getMessage());
   }

   @Test
   public void testNullMessageWithCause() {
      Exception internalException = new Exception("My error message");
      Exception ex = new OseeCoreException(null, internalException);
      Assert.assertEquals(internalException.getMessage(), ex.getMessage());
   }

   @Test
   public void testMissingArguments() {
      String messageFormat = "max = %d; min = %d; avg = %d";
      Exception ex = new OseeCoreException(messageFormat, 1, 0);
      Assert.assertTrue(ex.getMessage(), ex.getMessage().contains(
         "Exception message could not be formatted: [" + messageFormat + "] with the following arguments [1, 0].  Cause [java.util.MissingFormatArgumentException: Format specifier "));
   }

   @Test
   public void testWrongTypeArgument() {
      String messageFormat = "max = %d";
      Exception ex = new OseeCoreException(messageFormat, "1");
      Assert.assertEquals(
         "Exception message could not be formatted: [" + messageFormat + "] with the following arguments [1].  Cause [java.util.IllegalFormatConversionException: d != java.lang.String]",
         ex.getMessage());
   }

   @Test
   public void testInvalidMessageSyntax() {
      Exception ex = new OseeCoreException("%");
      Assert.assertEquals(
         "Exception message could not be formatted: [%] with the following arguments [].  Cause [java.util.UnknownFormatConversionException: Conversion = '%']",
         ex.getMessage());
   }
}