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
package org.eclipse.osee.framework.core.executor.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.executor.ExecutionCallback;
import org.eclipse.osee.logger.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Roberto E. Escobar
 */
public class ExecutionCallbackTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private Log logger;
   @Mock private Callable<String> callable;
   @Mock private ExecutionCallback<String> callback;
   //@formatter:on

   private ExecutorAdminImpl admin;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      admin = new ExecutorAdminImpl();
      admin.setLogger(logger);

      admin.start(new HashMap<String, Object>());
   }

   @After
   public void tearDown() {
      if (admin != null) {
         admin.stop(new HashMap<String, Object>());
      }
   }

   @Test
   public void testCallbackOnSuccess() throws Exception {
      String expected = "Was Called";

      when(callable.call()).thenReturn(expected);

      Future<String> future = admin.schedule(ExecutorAdminImpl.DEFAULT_EXECUTOR, callable, callback);
      String actual = future.get();

      assertEquals(expected, actual);

      verify(callback).onSuccess(expected);
      verify(callback, times(0)).onCancelled();
      verify(callback, times(0)).onFailure(Matchers.<Throwable> any());
   }

   @Test
   public void testCallbackOnFailure() throws Exception {
      Exception expectedException = new IllegalStateException();

      when(callable.call()).thenThrow(expectedException);

      Future<String> future = admin.schedule(ExecutorAdminImpl.DEFAULT_EXECUTOR, callable, callback);

      thrown.expect(ExecutionException.class);
      future.get();

      verify(callback, times(0)).onSuccess(Matchers.anyString());
      verify(callback, times(0)).onCancelled();
      verify(callback).onFailure(expectedException);
   }

   @Test(timeout = 5000)
   public void testCallbackOnCancel() throws Exception {
      TestCancellableCallable callable = new TestCancellableCallable("results");
      Future<String> future = admin.schedule(ExecutorAdminImpl.DEFAULT_EXECUTOR, callable, callback);

      while (!callable.hasBeenCalled()) {
         Thread.sleep(20);
      }
      future.cancel(true);

      thrown.expect(CancellationException.class);
      future.get();

      verify(callback, times(0)).onSuccess(Matchers.anyString());
      verify(callback).onCancelled();
      verify(callback, times(0)).onFailure(Matchers.<Throwable> any());

      assertEquals(true, callable.isCancelled());
      assertEquals(true, future.isCancelled());
   }

   private class TestCancellableCallable extends CancellableCallable<String> {

      private final String results;
      private volatile boolean beenCalled = false;

      public TestCancellableCallable(String results) {
         this.results = results;
      }

      public boolean hasBeenCalled() {
         return beenCalled;
      }

      @Override
      public String call() throws Exception {
         beenCalled = true;
         while (!isCancelled()) {
            // do nothing
         }
         return results;
      }
   }
}
