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
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.HasExecutionCallback;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorServiceImpl extends ThreadPoolExecutor {

   private final String id;
   private final ExecutorServiceLifecycleListener listener;

   public ExecutorServiceImpl(String id, int corePoolSize, ThreadFactory threadFactory, ExecutorServiceLifecycleListener listener) {
      super(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
      this.id = id;
      this.listener = listener;
   }

   @Override
   protected void terminated() {
      super.terminated();
      listener.onTerminate(id);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Future<T> submit(Callable<T> task) {
      Future<T> toReturn = null;
      ExecutionCallback<T> callback = getCallBack(task);
      if (callback != null) {
         FutureTask<T> fTask = new FutureTaskWithCallback<T>(task, callback);
         toReturn = (Future<T>) super.submit(fTask);
      } else {
         toReturn = super.submit(task);
      }
      return toReturn;
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      Runnable toRun = task;
      ExecutionCallback<T> callback = getCallBack(task);
      if (callback != null) {
         toRun = new FutureTaskWithCallback<T>(task, result, callback);
      }
      return super.submit(toRun, result);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public Future<?> submit(Runnable task) {
      Runnable toRun = task;
      ExecutionCallback<?> callback = getCallBack(task);
      if (callback != null) {
         toRun = new FutureTaskWithCallback(task, null, callback);
      }
      return super.submit(toRun);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public void execute(Runnable command) {
      Runnable toRun = command;
      ExecutionCallback<?> callback = getCallBack(command);
      if (callback != null) {
         toRun = new FutureTaskWithCallback(command, null, callback);
      }
      super.execute(toRun);
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
}
