/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.test.exception;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Test;

/**
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
   public void testMissingArguments() {
      String messageFormat = "max = %d; min = %d; avg = %d";
      Exception ex = new OseeCoreException(messageFormat, 1, 0);
      Assert.assertEquals(
         "Exception message could not be formatted: [" + messageFormat + "] with the following arguments [1,0].  Cause [java.util.MissingFormatArgumentException: Format specifier 'd']",
         ex.getMessage());
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