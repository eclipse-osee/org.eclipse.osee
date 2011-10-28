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

   private Log getLogger() {
      return logger;
   }

   @Override
   protected void terminated() {
      super.terminated();
      listener.onTerminate(id);
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
      ExecutionCallback<T> callback = getCallBack(runnable);
      return new FutureTaskWithCallback<T>(getLogger(), runnable, value, callback);
   }

   @Override
   protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
      ExecutionCallback<T> callback = getCallBack(callable);
      return new FutureTaskWithCallback<T>(getLogger(), callable, callback);
   }
}
