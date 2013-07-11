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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.executor.admin.ExecutorConstants;
import org.eclipse.osee.logger.Log;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorAdminImpl implements ExecutorAdmin {

   public static final String DEFAULT_EXECUTOR = "default.executor";

   private ExecutorCache cache;
   private Log logger;
   private EventService eventService;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   private Log getLogger() {
      return logger;
   }

   public void setEventService(EventService eventService) {
      this.eventService = eventService;
   }

   private EventService getEventService() {
      return eventService;
   }

   public void start(Map<String, ?> props) {
      cache = new ExecutorCache();

      getEventService().postEvent(ExecutorConstants.EXECUTOR_ADMIN_REGISTRATION_EVENT, props);
   }

   public void stop(Map<String, ?> props) {
      for (Entry<String, ListeningExecutorService> entry : cache.getExecutors().entrySet()) {
         shutdown(entry.getKey(), entry.getValue());
      }
      cache = null;
      getEventService().postEvent(ExecutorConstants.EXECUTOR_ADMIN_DEREGISTRATION_EVENT, props);
   }

   public ListeningExecutorService getDefaultExecutor() throws Exception {
      return getExecutor(DEFAULT_EXECUTOR);
   }

   public ListeningExecutorService getExecutor(String id) throws Exception {
      ListeningExecutorService service = null;
      synchronized (cache) {
         service = cache.getById(id);
         if (service == null) {
            service = createExecutor(id, -1);
         }
      }
      if (service == null) {
         throw new IllegalStateException(String.format("Error creating executor [%s].", id));
      }
      if (service.isShutdown() || service.isTerminated()) {
         throw new IllegalStateException(String.format("Error executor [%s] was previously shutdown.", id));
      }
      return service;
   }

   @Override
   public <T> Future<T> schedule(Callable<T> callable) throws Exception {
      return schedule(callable, null);
   }

   @Override
   public <T> Future<T> schedule(String id, Callable<T> callable) throws Exception {
      return schedule(id, callable, null);
   }

   @Override
   public <T> Future<T> schedule(Callable<T> callable, ExecutionCallback<T> callback) throws Exception {
      return schedule(DEFAULT_EXECUTOR, callable, callback);
   }

   @Override
   public <T> Future<T> schedule(String id, Callable<T> callable, ExecutionCallback<T> callback) throws Exception {
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

   private ListeningExecutorService createExecutor(String id, int poolSize) throws Exception {
      ExecutorThreadFactory threadFactory = new ExecutorThreadFactory(id, Thread.NORM_PRIORITY);

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
   public void createFixedPoolExecutor(String id, int poolSize) throws Exception {
      createExecutor(id, poolSize);
   }

   @Override
   public void createCachedPoolExecutor(String id) throws Exception {
      createExecutor(id, -1);
   }

   @Override
   public void shutdown(String id) throws Exception {
      ListeningExecutorService service = cache.getById(id);
      if (service != null) {
         shutdown(id, service);
         cache.remove(id);
      }
   }
}
