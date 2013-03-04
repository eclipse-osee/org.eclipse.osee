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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.executor.admin.ExecutorConstants;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorAdminImpl implements ExecutorAdmin {

   public static final String DEFAULT_EXECUTOR = "default.executor";
   private static final int THREAD_CHECK_TIME = 5000; // every 5 seconds

   private ExecutorCache cache;
   private Log logger;
   private EventService eventService;
   private Timer timer;

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

      timer = new Timer();
      TimerTask task = new ThreadCleaner(getLogger(), cache);
      timer.scheduleAtFixedRate(task, 0, THREAD_CHECK_TIME);

      getEventService().postEvent(ExecutorConstants.EXECUTOR_ADMIN_REGISTRATION_EVENT, props);
   }

   public void stop(Map<String, ?> props) {
      timer.cancel();
      timer = null;
      for (Entry<String, ExecutorService> entry : cache.getExecutors().entrySet()) {
         shutdown(entry.getKey(), entry.getValue());
      }
      cache = null;
      getEventService().postEvent(ExecutorConstants.EXECUTOR_ADMIN_DEREGISTRATION_EVENT, props);
   }

   public ExecutorService getDefaultExecutor() throws Exception {
      return getExecutor(DEFAULT_EXECUTOR);
   }

   public ExecutorService getExecutor(String id) throws Exception {
      ExecutorService service = null;
      synchronized (cache) {
         service = cache.getById(id);
         if (service == null) {
            service = createExecutor(id);
            cache.put(id, service);
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

   public <T> Callable<T> addCallback(Callable<T> callable, ExecutionCallback<T> callback) {
      return new CallableWithCallbackImpl<T>(callable, callback);
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
      Callable<T> toExecute = callable;
      if (callback != null) {
         toExecute = addCallback(callable, callback);
      }
      return getExecutor(id).submit(toExecute);
   }

   private ExecutorService createExecutor(String id) throws Exception {
      ExecutorThreadFactory threadFactory = new ExecutorThreadFactory(id, Thread.NORM_PRIORITY);
      cache.put(id, threadFactory);

      // TODO: Better way to control pool size per executor service
      int corePoolSize = Math.min(4, Runtime.getRuntime().availableProcessors());
      return new ExecutorServiceImpl(getLogger(), id, corePoolSize, threadFactory, cache);
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
   public int cancelTasks(String id) throws Exception {
      int itemsCancelled = 0;
      ExecutorWorkCache workCache = cache.getWorkerCache(id);
      if (workCache != null) {
         itemsCancelled = workCache.cancelAll();
      }
      return itemsCancelled;
   }
}
