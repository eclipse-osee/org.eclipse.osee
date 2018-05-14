/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.executor.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author John Misinco
 */
public class ExecutorTests {

   private ExecutorService executor;

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Before
   public void setup() {
      executor = Executors.newFixedThreadPool(2);
   }

   @After
   public void tearDown() {
      executor.shutdownNow();
   }

   @Test(timeout = 5000)
   public void testCancellation() throws InterruptedException {
      TestCallable test = new TestCallable() {

         @Override
         public void testCode() {
            while (true) {
               testStarted = true;
               if (Thread.currentThread().isInterrupted()) {
                  return;
               }
            }
         }
      };

      Future<Void> future = executor.submit(test);

      while (!test.testStarted()) {
         Thread.sleep(20);
      }

      future.cancel(true);
      Exception caught = null;
      try {
         future.get();
      } catch (Exception ex) {
         caught = ex;
      }

      assertTrue(test.callMethodFinished());
      assertTrue(caught instanceof CancellationException);
      assertTrue(future.isCancelled());
      assertTrue(future.isDone());
   }

   // does isCancelled = true prior to callable finished... ie isCancelled=true and isDone=false
   @Test(timeout = 5000)
   public void testFutureFlagsPriorToFinish() throws InterruptedException {
      TestFlagsCallable test = new TestFlagsCallable();

      Future<Void> future = executor.submit(test);

      while (!test.testStarted()) {
         Thread.sleep(20);
      }

      future.cancel(true);
      Exception caught = null;
      try {
         future.get();
      } catch (Exception ex) {
         caught = ex;
      }

      // test should still be running
      assertFalse(test.callMethodFinished());
      assertTrue(caught instanceof CancellationException);
      assertTrue(future.isCancelled());
      assertTrue(future.isDone());

      test.stopTest();
   }

   private class TestFlagsCallable extends TestCallable {

      private volatile boolean stopTest = false;

      public void stopTest() {
         stopTest = true;
      }

      @Override
      public void testCode() throws Exception {
         testStarted = true;
         while (!stopTest) {
            // do nothing
         }
      }
   }

   // how does running a long database query handle the interrupted flag?

   // one callable spawns another and then the originating thread is cancelled but is pending on a get, how can it cancel the other threads?
   @Test
   public void testCancellationAfterRunningCallabeFromCallable() throws InterruptedException {
      final AtomicBoolean gotInterrupted = new AtomicBoolean(false);

      final TestCallable toSpawn = new TestCallable() {

         @Override
         public void testCode() {
            testStarted = true;
            while (!Thread.currentThread().isInterrupted()) {
               // do nothing
            }
            gotInterrupted.set(true);
         }
      };

      TestCallable original = new TestCallable() {

         @Override
         public void testCode() throws ExecutionException {
            testStarted = true;
            Future<Void> spawned = executor.submit(toSpawn);
            while (!toSpawn.testStarted()) {
               // do nothing
            }
            try {
               spawned.get();
            } catch (InterruptedException ex) {
               spawned.cancel(true);
            }
         }
      };

      Future<Void> future = executor.submit(original);

      while (!original.testStarted()) {
         Thread.sleep(20);
      }
      Thread.sleep(200);

      future.cancel(true);
      Exception caught = null;
      try {
         future.get();
      } catch (Exception ex) {
         caught = ex;
      }

      while (!gotInterrupted.get()) {
         // do nothing
      }

      assertTrue(original.callMethodFinished());
      assertTrue(toSpawn.callMethodFinished());
      assertTrue(gotInterrupted.get());
      assertTrue(caught instanceof CancellationException);
      assertTrue(future.isCancelled());
      assertTrue(future.isDone());
   }

   @Test(timeout = 5000)
   public void testCancellationDuringWait() throws InterruptedException {
      TestCallable test = new TestCallable() {

         @Override
         public void testCode() throws InterruptedException {
            Object t = new Object();
            synchronized (t) {
               testStarted = true;
               t.wait();
            }
         }
      };

      Future<Void> future = executor.submit(test);

      while (!test.testStarted()) {
         Thread.sleep(20);
      }
      Thread.sleep(200);

      future.cancel(true);
      Exception caught = null;
      try {
         future.get();
      } catch (Exception ex) {
         caught = ex;
      }

      assertFalse(test.callMethodFinished());
      assertTrue(caught instanceof CancellationException);
      assertTrue(future.isCancelled());
      assertTrue(future.isDone());
   }

   @Test(timeout = 5000)
   public void testCancellationDuringSleep() throws InterruptedException {
      TestCallable test = new TestCallable() {

         @Override
         public void testCode() throws InterruptedException {
            testStarted = true;
            Thread.sleep(60 * 1000);
         }
      };

      Future<Void> future = executor.submit(test);

      while (!test.testStarted()) {
         Thread.sleep(20);
      }
      Thread.sleep(200);

      future.cancel(true);
      Exception caught = null;
      try {
         future.get();
      } catch (Exception ex) {
         caught = ex;
      }

      assertFalse(test.callMethodFinished());
      assertTrue(caught instanceof CancellationException);
      assertTrue(future.isCancelled());
      assertTrue(future.isDone());
   }

   @Test(timeout = 5000)
   public void testCancellationDuringIO() throws InterruptedException {
      TestCallable test = new TestCallable() {

         @Override
         public void testCode() throws IOException {
            PrintWriter writer = null;
            File toWrite = folder.newFile();
            writer = new PrintWriter(toWrite);
            try {
               while (true) {
                  testStarted = true;
                  writer.println("The first line");
               }
            } finally {
               writer.close();
            }
         }
      };

      Future<Void> future = executor.submit(test);

      while (!test.testStarted()) {
         Thread.sleep(20);
      }
      Thread.sleep(200);

      future.cancel(true);
      Exception caught = null;
      try {
         future.get();
      } catch (Exception ex) {
         caught = ex;
      }

      assertFalse(test.callMethodFinished());
      assertTrue(caught instanceof CancellationException);
      assertTrue(future.isCancelled());
      assertTrue(future.isDone());
   }

   private abstract class TestCallable implements Callable<Void> {

      protected volatile boolean testStarted = false;
      protected volatile boolean callCompleted = false;

      public boolean testStarted() {
         return testStarted;
      }

      public boolean callMethodFinished() {
         return callCompleted;
      }

      public abstract void testCode() throws Exception;

      @Override
      public Void call() throws Exception {
         testCode();
         callCompleted = true;
         return null;
      }
   }
}
