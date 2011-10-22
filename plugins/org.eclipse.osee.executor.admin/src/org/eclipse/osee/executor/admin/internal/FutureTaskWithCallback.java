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

/**
 * @author Roberto E. Escobar
 */
public class FutureTaskWithCallback<T> extends FutureTask<T> implements HasExecutionCallback<T> {

   private final ExecutionCallback<T> callback;
   private T result;

   public FutureTaskWithCallback(Callable<T> callable, ExecutionCallback<T> callback) {
      super(callable);
      this.callback = callback;
   }

   public FutureTaskWithCallback(Runnable runnable, T result, ExecutionCallback<T> callback) {
      super(runnable, result);
      this.callback = callback;
      this.result = result;
   }

   @Override
   public ExecutionCallback<T> getExecutionCallback() {
      return callback;
   }

   @Override
   protected void set(T result) {
      super.set(result);
      this.result = result;
   }

   @Override
   protected void done() {
      super.done();
      callback.onSuccess(result);
   }

   @Override
   protected void setException(Throwable throwable) {
      super.setException(throwable);
      callback.onFailure(throwable);
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      boolean result = super.cancel(mayInterruptIfRunning);
      callback.onCancelled();
      return result;
   }
}
