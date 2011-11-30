/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.executor.admin.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.executor.admin.HasExecutionCallback;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class FutureTaskWithCallback<T> implements RunnableFuture<T>, HasExecutionCallback<T> {

   private final Log logger;
   private final ExecutionCallback<T> callback;
   private final Sync sync;
   private final Runnable runnable;

   public FutureTaskWithCallback(Log logger, Callable<T> callable, ExecutionCallback<T> callback) {
      if (callable == null) {
         throw new NullPointerException();
      }
      sync = new Sync(callable);

      this.logger = logger;
      this.callback = callback;
      this.runnable = null;
   }

   public FutureTaskWithCallback(Log logger, Runnable runnable, T result, ExecutionCallback<T> callback) {
      this.runnable = runnable;
      sync = new Sync(Executors.callable(runnable, result));
      this.logger = logger;
      this.callback = callback;
   }

   @Override
   public ExecutionCallback<T> getExecutionCallback() {
      return callback;
   }

   @Override
   public boolean isCancelled() {
      return sync.innerIsCancelled();
   }

   @Override
   public boolean isDone() {
      return sync.innerIsDone();
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      return sync.innerCancel(mayInterruptIfRunning);
   }

   @Override
   public T get() throws InterruptedException, ExecutionException {
      return sync.innerGet();
   }

   @Override
   public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return sync.innerGet(unit.toNanos(timeout));
   }

   protected void done() {
      // Do nothing
   }

   protected void set(T v) {
      sync.innerSet(v);
   }

   protected void setException(Throwable t) {
      sync.innerSetException(t);
   }

   @Override
   public void run() {
      sync.innerRun();
   }

   protected boolean runAndReset() {
      return sync.innerRunAndReset();
   }

   protected void notifyOnSuccess(T result) {
      ExecutionCallback<T> callback = getExecutionCallback();
      if (callback != null) {
         try {
            callback.onSuccess(result);
         } catch (Throwable th) {
            handleNotificationError(th, "success");
         }
      }
   }

   protected void notifyOnCancelled() {
      ExecutionCallback<T> callback = getExecutionCallback();
      if (callback != null) {
         try {
            callback.onCancelled();
         } catch (Throwable th) {
            handleNotificationError(th, "cancelled");
         }
      }
   }

   protected void notifyOnFailure(Throwable throwable) {
      ExecutionCallback<T> callback = getExecutionCallback();
      if (callback != null) {
         try {
            callback.onFailure(throwable);
         } catch (Throwable th) {
            handleNotificationError(th, "failure");
         }
      }
   }

   protected void handleNotificationError(Throwable th, String value) {
      logger.error(th, "Error during on %s notification [%s]", value, callback.toString());
   }

   private final class Sync extends AbstractQueuedSynchronizer {

      private static final long serialVersionUID = 592282723700675891L;

      /** State value representing that task is running */
      private static final int RUNNING = 1;
      /** State value representing that task ran */
      private static final int RAN = 2;
      /** State value representing that task was cancelled */
      private static final int CANCELLED = 4;

      /** The underlying callable */
      private final Callable<T> callable;
      /** The result to return from get() */
      private T result;
      /** The exception to throw from get() */
      private Throwable exception;

      /**
       * The thread running task. When nulled after set/cancel, this indicates that the results are accessible. Must be
       * volatile, to ensure visibility upon completion.
       */
      private volatile Thread runner;

      Sync(Callable<T> callable) {
         this.callable = callable;
      }

      private boolean ranOrCancelled(int state) {
         return (state & (RAN | CANCELLED)) != 0;
      }

      @Override
      protected int tryAcquireShared(int ignore) {
         return innerIsDone() ? 1 : -1;
      }

      @Override
      protected boolean tryReleaseShared(int ignore) {
         runner = null;
         return true;
      }

      boolean innerIsCancelled() {
         return getState() == CANCELLED;
      }

      boolean innerIsDone() {
         return ranOrCancelled(getState()) && runner == null;
      }

      T innerGet() throws InterruptedException, ExecutionException {
         acquireSharedInterruptibly(0);
         if (getState() == CANCELLED) {
            throw new CancellationException();
         }
         if (exception != null) {
            throw new ExecutionException(exception);
         }

         return result;
      }

      T innerGet(long nanosTimeout) throws InterruptedException, ExecutionException, TimeoutException {
         if (!tryAcquireSharedNanos(0, nanosTimeout)) {
            throw new TimeoutException();
         }
         if (getState() == CANCELLED) {
            throw new CancellationException();
         }
         if (exception != null) {
            throw new ExecutionException(exception);
         }
         return result;
      }

      void innerSet(T v) {
         for (;;) {
            int s = getState();
            if (s == RAN) {
               return;
            }
            if (s == CANCELLED) {
               // aggressively release to set runner to null,
               // in case we are racing with a cancel request
               // that will try to interrupt runner
               notifyOnCancelled();
               releaseShared(0);
               return;
            }
            if (compareAndSetState(s, RAN)) {
               result = v;

               notifyOnSuccess(result);
               releaseShared(0);
               done();
               return;
            }
         }
      }

      void innerSetException(Throwable throwable) {
         for (;;) {
            int s = getState();
            if (s == RAN) {
               return;
            }
            if (s == CANCELLED) {
               // aggressively release to set runner to null,
               // in case we are racing with a cancel request
               // that will try to interrupt runner
               notifyOnCancelled();
               releaseShared(0);
               return;
            }
            if (compareAndSetState(s, RAN)) {
               exception = throwable;
               result = null;

               notifyOnFailure(throwable);
               releaseShared(0);
               done();
               return;
            }
         }
      }

      boolean innerCancel(boolean mayInterruptIfRunning) {
         for (;;) {
            int s = getState();
            if (ranOrCancelled(s)) {
               return false;
            }
            if (compareAndSetState(s, CANCELLED)) {
               break;
            }
         }

         if (callable instanceof HasCancellation) {
            HasCancellation cancellable = (HasCancellation) callable;
            cancellable.setCancel(true);
         }
         if (runnable instanceof HasCancellation) {
            HasCancellation cancellable = (HasCancellation) runnable;
            cancellable.setCancel(true);
         }

         if (mayInterruptIfRunning) {
            Thread r = runner;
            if (r != null) {
               r.interrupt();
            }
         }
         notifyOnCancelled();
         releaseShared(0);
         done();
         return true;
      }

      void innerRun() {
         if (!compareAndSetState(0, RUNNING)) {
            return;
         }
         try {
            runner = Thread.currentThread();
            if (getState() == RUNNING) {
               innerSet(callable.call());
            } else {
               releaseShared(0); // cancel
            }
         } catch (Throwable ex) {
            innerSetException(ex);
         }
      }

      boolean innerRunAndReset() {
         if (!compareAndSetState(0, RUNNING)) {
            return false;
         }
         try {
            runner = Thread.currentThread();
            if (getState() == RUNNING) {
               callable.call(); // don't set result
            }
            runner = null;
            return compareAndSetState(RUNNING, 0);
         } catch (Throwable ex) {
            innerSetException(ex);
            return false;
         }
      }
   }

}
