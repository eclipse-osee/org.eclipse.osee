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
package org.eclipse.osee.executor.admin.internal;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.executor.admin.mock.MockEventService;
import org.eclipse.osee.executor.admin.mock.MockExecutionCallback;
import org.eclipse.osee.executor.admin.mock.MockLog;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class ExecutionCallbackTest {

   @Test
   public void testCallbackOnSuccess() throws Exception {
      ExecutorAdminImpl admin = new ExecutorAdminImpl();
      admin.setLogger(new MockLog());
      admin.setEventService(new MockEventService());
      admin.start(new HashMap<String, Object>());

      final String expected = "Was Called";

      MockExecutionCallback<String> callback = new MockExecutionCallback<String>(500);
      Callable<String> callable = new Callable<String>() {

         @Override
         public String call() throws Exception {
            return expected;
         }

      };
      Future<String> future = admin.schedule(callable, callback);
      String actual = future.get();

      Assert.assertEquals(expected, actual);

      Assert.assertTrue(callback.wasOnSuccess());
      Assert.assertFalse(callback.wasOnCancelled());
      Assert.assertFalse(callback.wasOnFailure());

      Assert.assertEquals(expected, callback.getResult());
      Assert.assertNull(callback.getThrowable());
   }

   @Test
   public void testCallbackOnFailure() throws Exception {
      ExecutorAdminImpl admin = new ExecutorAdminImpl();
      admin.setLogger(new MockLog());
      admin.setEventService(new MockEventService());
      admin.start(new HashMap<String, Object>());

      final Exception expectedException = new IllegalStateException();
      MockExecutionCallback<String> callback = new MockExecutionCallback<String>(500);
      Callable<String> callable = new Callable<String>() {

         @Override
         public String call() throws Exception {
            throw expectedException;
         }

      };
      Future<String> future = admin.schedule(callable, callback);

      try {
         future.get();
         Assert.assertTrue("An exception should have been thrown", false);
      } catch (Exception ex) {
         Assert.assertEquals(ExecutionException.class, ex.getClass());
         Assert.assertEquals(expectedException, ex.getCause());
      }

      Assert.assertFalse(callback.wasOnSuccess());
      Assert.assertFalse(callback.wasOnCancelled());
      Assert.assertTrue(callback.wasOnFailure());

      Assert.assertNull(callback.getResult());
      Assert.assertEquals(IllegalStateException.class, callback.getThrowable().getClass());
   }

   @Test
   public void testCallbackOnCancel() throws Exception {
      ExecutorAdminImpl admin = new ExecutorAdminImpl();
      admin.setLogger(new MockLog());
      admin.setEventService(new MockEventService());
      admin.start(new HashMap<String, Object>());

      final String results = "results";

      MockExecutionCallback<String> callback = new MockExecutionCallback<String>(500);

      TestCancellableCallable callable = new TestCancellableCallable(results);
      Future<String> future = admin.schedule(callable, callback);
      future.cancel(true);

      Assert.assertFalse(callback.wasOnSuccess());
      Assert.assertTrue(callback.wasOnCancelled());
      Assert.assertFalse(callback.wasOnFailure());

      Assert.assertNull(callback.getResult());
      Assert.assertNull(callback.getThrowable());

      Assert.assertEquals(true, callable.isCancelled());
      Assert.assertEquals(true, future.isCancelled());

      try {
         future.get();
         Assert.assertTrue("An exception should have been thrown", false);
      } catch (Exception ex) {
         Assert.assertEquals(CancellationException.class, ex.getClass());
      }
   }

   private class TestCancellableCallable extends CancellableCallable<String> {

      private final String results;

      public TestCancellableCallable(String results) {
         this.results = results;
      }

      @Override
      public String call() throws Exception {
         while (!isCancelled()) {
            checkForCancelled();
            // System.out.println("working...");
         }
         return results;
      }
   }
}
