/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.exception;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * {@link OseeCoreException}
 *
 * @author Karol M. Wilk
 */
public class OseeCoreExceptionsTest {

   @Rule
   public TestName test = new TestName();

   @Test
   public void test_wrapAndThrow_RuntimeException() {
      String resultMessage = null;

      try {
         OseeCoreException.wrapAndThrow(new RuntimeException(test.getMethodName()));
      } catch (Exception ex) {
         Assert.assertTrue(ex.getStackTrace().length > 0);
         resultMessage = ex.getMessage();
      }
      Assert.assertNotNull(resultMessage);
      Assert.assertTrue(resultMessage.contains(test.getMethodName()));
   }

   @Test
   public void test_wrapAndThrow_OseeCoreException() {
      String resultMessage = null;

      try {
         OseeCoreException.wrapAndThrow(new OseeCoreException(test.getMethodName()));
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex.getStackTrace().length > 0);
         resultMessage = ex.getMessage();
      }
      Assert.assertNotNull(resultMessage);
      Assert.assertTrue(resultMessage.contains(test.getMethodName()));
   }

   @Test
   public void test_wrapAndThrow_OseeWrappedException() {
      String resultMessage = null;

      try {
         OseeCoreException.wrapAndThrow(new OseeWrappedException(test.getMethodName(), new Throwable()));
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex.getStackTrace().length > 0);
         resultMessage = ex.getMessage();
      }
      Assert.assertNotNull(resultMessage);
      Assert.assertTrue(resultMessage.contains(test.getMethodName()));
   }
}
