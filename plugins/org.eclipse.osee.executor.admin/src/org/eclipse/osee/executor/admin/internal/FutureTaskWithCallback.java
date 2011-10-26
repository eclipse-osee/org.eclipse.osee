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
import java.util.concurrent.FutureTask;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.HasExecutionCallback;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class FutureTaskWithCallback<T> extends FutureTask<T> implements HasExecutionCallback<T> {

   private final Runnable runnable;
   private final ExecutionCallback<T> callback;
   private final Log logger;

   public FutureTaskWithCallback(Log logger, Callable<T> callable, ExecutionCallback<T> callback) {
      super(callable);
      this.logger = logger;
      this.callback = callback;
      this.runnable = null;
   }

   public FutureTaskWithCallback(Log logger, Runnable runnable, T result, ExecutionCallback<T> callback) {
      super(runnable, result);
      this.logger = logger;
      this.callback = callback;
      this.runnable = runnable;
   }

   @Override
   public ExecutionCallback<T> getExecutionCallback() {
      return callback;
   }

   private String getWorkerName() {
      String name;
      if (callback != null) {
         name = callback.toString();
      } else if (runnable != null) {
         name = runnable.toString();
      } else {
         name = this.toString();
      }
      return name;
   }

   @Override
   protected void done() {
      super.done();
      try {
         callback.onSuccess(get());
      } catch (Throwable ex) {
         logger.error(ex, "Error onSuccess callback for - [%s]", getWorkerName());
      }
   }

   @Override
   protected void setException(Throwable throwable) {
      super.setException(throwable);
      try {
         callback.onFailure(throwable);
      } catch (Throwable ex) {
         logger.error(ex, "Error onFailure callback for - [%s]", getWorkerName());
      }
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      boolean result = super.cancel(mayInterruptIfRunning);
      try {
         callback.onCancelled();
      } catch (Throwable ex) {
         logger.error(ex, "Error onCancel callback for - [%s]", getWorkerName());
      }
      return result;
   }
}
