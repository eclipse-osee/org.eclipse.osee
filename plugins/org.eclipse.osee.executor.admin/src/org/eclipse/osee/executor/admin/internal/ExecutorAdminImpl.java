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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorAdminImpl implements ExecutorAdmin {

   public static final String DEFAULT_EXECUTOR = "default.executor";

   private ExecutorCache cache;
   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   private Log getLogger() {
      return logger;
   }

   public void start(Map<String, ?> props) {
      logger.trace("Starting [%s]...", getClass().getSimpleName());
      cache = new ExecutorCache();
   }

   public void stop(Map<String, ?> props) {
      logger.trace("Stopping [%s]...", getClass().getSimpleName());
      for (Entry<String, ListeningExecutorService> entry : cache.getExecutors().entrySet()) {
         shutdown(entry.getKey(), entry.getValue());
      }
      cache = null;
   }

   public ListeningExecutorService getDefaultExecutor() {
      return getExecutor(DEFAULT_EXECUTOR);
   }

   public ListeningExecutorService getExecutor(String id) {
      ListeningExecutorService service = null;
      synchronized (cache) {
         service = cache.getById(id);
         if (service == null) {
            service = createExecutor(id, -1);
         }
      }
      if (service == null) {
         throw new OseeStateException("Error creating executor [%s].", id);
      }
      if (service.isShutdown() || service.isTerminated()) {
         throw new OseeStateException("Error executor [%s] was previously shutdown.", id);
      }
      return service;
   }

   @Override
   public <T> Future<T> schedule(Callable<T> callable) {
      return schedule(callable, null);
   }

   @Override
   public <T> Future<T> schedule(String id, Callable<T> callable) {
      return schedule(id, callable, null);
   }

   @Override
   public <T> Future<T> schedule(Callable<T> callable, ExecutionCallback<T> callback) {
      return schedule(DEFAULT_EXECUTOR, callable, callback);
   }

   @Override
   public <T> Future<T> schedule(String id, Callable<T> callable, ExecutionCallback<T> callback) {
      ListenableFuture<T> listenableFuture = getExecutor(id).submit(callable);
      if (callback != null) {
         FutureCallback<T> futureCallback = asFutureCallback(callback);
         Futures.addCallback(listenableFuture, futureCallback);
      }
      return listenableFuture;
   }

   private <T> FutureCallback<T> asFutureCallback(final ExecutionCallback<T> callback) {
      return new FutureCallback<T>() {

         @Override
         public void onFailure(Throwable arg0) {
            if (arg0 instanceof CancellationException) {
               callback.onCancelled();
            } else {
               callback.onFailure(arg0);
            }
         }

         @Override
         public void onSuccess(T arg0) {
            callback.onSuccess(arg0);
         }
      };
   }

   private ListeningExecutorService createExecutor(String id, int poolSize) {
      ThreadFactory threadFactory = new ThreadFactoryBuilder()//
         .setNameFormat(id + "- [%s]")//
         .setPriority(Thread.NORM_PRIORITY)//
         .build();

      ExecutorService executor = null;
      if (poolSize > 0) {
         executor = Executors.newFixedThreadPool(poolSize, threadFactory);
      } else {
         executor = Executors.newCachedThreadPool(threadFactory);
      }

      ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(executor);
      cache.put(id, listeningExecutor);
      return listeningExecutor;
   }

   private void shutdown(String id, ExecutorService executor) {
      try {
         executor.shutdown();
         boolean completed = false;
         try {
            completed = executor.awaitTermination(5, TimeUnit.SECONDS);
         } catch (Exception ex) {
            // Do nothing;
         }
         if (!completed) {
            //List<Runnable> runnables =
            executor.shutdownNow();
            // TODO figure out what didn't execute
            //               for (Runnable runable : runnables) {
            //                  runnable.
            //               }
         }
      } catch (Exception ex) {
         getLogger().error(ex, "Error shutting down executor [%s]", id);
      }
   }

   @Override
   public void createFixedPoolExecutor(String id, int poolSize) {
      createExecutor(id, poolSize);
   }

   @Override
   public void createCachedPoolExecutor(String id) {
      createExecutor(id, -1);
   }

   @Override
   public void shutdown(String id) {
      ListeningExecutorService service = cache.getById(id);
      if (service != null) {
         shutdown(id, service);
         cache.remove(id);
      }
   }

   public ListeningScheduledExecutorService getScheduledExecutor(String id) {
      ListeningScheduledExecutorService service = null;
      synchronized (cache) {
         ListeningExecutorService executor = cache.getById(id);
         if (executor instanceof ListeningScheduledExecutorService) {
            service = (ListeningScheduledExecutorService) executor;
         } else {
            service = createScheduledExecutor(id, -1);
         }
      }
      if (service == null) {
         throw new OseeStateException("Error creating executor [%s].", id);
      }
      if (service.isShutdown() || service.isTerminated()) {
         throw new OseeStateException("Error executor [%s] was previously shutdown.", id);
      }
      return service;
   }

   private ListeningScheduledExecutorService createScheduledExecutor(String id, int poolSize) {
      ThreadFactory threadFactory = new ThreadFactoryBuilder()//
         .setNameFormat(id + "- [%s]")//
         .setPriority(Thread.NORM_PRIORITY)//
         .build();
      ScheduledExecutorService executor = null;
      if (poolSize > 0) {
         executor = Executors.newScheduledThreadPool(poolSize, threadFactory);
      } else {
         executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
      }
      ListeningScheduledExecutorService listeningExecutor = MoreExecutors.listeningDecorator(executor);
      cache.put(id, listeningExecutor);
      return listeningExecutor;
   }

   @Override
   public void createScheduledPoolExecutor(String id, int poolSize) {
      createScheduledExecutor(id, poolSize);
   }

   @Override
   public <T> Future<T> scheduleAtFixedRate(String id, Callable<T> callable, long executionRate, TimeUnit timeUnit) {
      return scheduleAtFixedRate(id, callable, -1, executionRate, timeUnit);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> Future<T> scheduleAtFixedRate(String id, final Callable<T> callable, long startAfter, long executionRate, TimeUnit timeUnit) {
      ListeningScheduledExecutorService executor = getScheduledExecutor(id);
      Runnable runnable = asRunnable(callable);
      ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(runnable, startAfter, executionRate, timeUnit);
      return (Future<T>) scheduledFuture;
   }

   private Runnable asRunnable(final Callable<?> callable) {
      return new Runnable() {

         @Override
         public void run() {
            try {
               callable.call();
            } catch (Throwable th) {
               logger.error(th, "Error executing scheduled task [%s]", callable);
            }
         }
      };
   }

   @Override
   public Future<?> scheduleOnce(String name, Runnable task) {
      return getExecutor(DEFAULT_EXECUTOR).submit(task);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(String name, Runnable task, long initialDelay, long executionRate, TimeUnit timeUnit) {
      return getScheduledExecutor(DEFAULT_EXECUTOR).scheduleAtFixedRate(task, initialDelay, executionRate, timeUnit);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(String name, Runnable task, long initialDelay, long delay, TimeUnit unit) {
      return getScheduledExecutor(DEFAULT_EXECUTOR).scheduleWithFixedDelay(task, initialDelay, delay, unit);
   }
}