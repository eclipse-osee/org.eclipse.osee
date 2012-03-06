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

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.HasExecutionCallback;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorServiceImpl extends ThreadPoolExecutor {

   private final String id;
   private final Log logger;
   private final ExecutorServiceLifecycleListener listener;

   public ExecutorServiceImpl(Log logger, String id, int corePoolSize, ThreadFactory threadFactory, ExecutorServiceLifecycleListener listener) {
      super(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
      this.logger = logger;
      this.id = id;
      this.listener = listener;
   }

   public String getId() {
      return id;
   }

   private Log getLogger() {
      return logger;
   }

   @Override
   protected void terminated() {
      super.terminated();
      listener.onTerminate(getId());
   }

   @SuppressWarnings("unchecked")
   private <V> ExecutionCallback<V> getCallBack(Object object) {
      ExecutionCallback<V> callback = null;
      if (object instanceof HasExecutionCallback) {
         HasExecutionCallback<V> item = (HasExecutionCallback<V>) object;
         callback = item.getExecutionCallback();
      }
      return callback;
   }

   @Override
   protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
      UUID uuid = createID();
      ExecutionCallback<T> callback = getCallBack(runnable);
      FutureTaskWithCallback<T> future = new FutureTaskWithCallback<T>(uuid, getLogger(), runnable, value, callback);
      onScheduled(future);
      return future;
   }

   @Override
   protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
      UUID uuid = createID();
      ExecutionCallback<T> callback = getCallBack(callable);
      FutureTaskWithCallback<T> future = new FutureTaskWithCallback<T>(uuid, getLogger(), callable, callback);
      onScheduled(future);
      return future;
   }

   private UUID createID() {
      return UUID.randomUUID();
   }

   /**
    * Tasks waiting
    */
   @Override
   public long getTaskCount() {
      return super.getTaskCount();
   }

   /**
    * Number of Tasks Completed
    */
   @Override
   public long getCompletedTaskCount() {
      return super.getCompletedTaskCount();
   }

   private void onScheduled(FutureTaskWithCallback<?> future) {
      listener.onScheduled(getId(), future.getUUID(), future);
   }

   @Override
   protected void beforeExecute(Thread t, Runnable r) {
      FutureTaskWithCallback<?> future = (FutureTaskWithCallback<?>) r;
      listener.onBeforeExecute(getId(), future.getUUID(), future);
      super.beforeExecute(t, r);
   }

   @Override
   protected void afterExecute(Runnable r, Throwable t) {
      FutureTaskWithCallback<?> future = (FutureTaskWithCallback<?>) r;
      listener.onAfterExecute(getId(), future.getUUID(), future);
      super.afterExecute(r, t);
   }

}
