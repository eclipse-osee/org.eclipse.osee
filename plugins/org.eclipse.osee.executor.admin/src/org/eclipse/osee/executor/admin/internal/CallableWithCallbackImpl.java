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
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.executor.admin.HasExecutionCallback;

/**
 * @author Roberto E. Escobar
 */
public class CallableWithCallbackImpl<T> extends CancellableCallable<T> implements HasExecutionCallback<T> {

   private final Callable<T> innerWorker;
   private final ExecutionCallback<T> callback;

   public CallableWithCallbackImpl(Callable<T> innerWorker, ExecutionCallback<T> callback) {
      this.innerWorker = innerWorker;
      this.callback = callback;
   }

   @Override
   public T call() throws Exception {
      checkForCancelled();
      return innerWorker.call();
   }

   @Override
   public ExecutionCallback<T> getExecutionCallback() {
      return callback;
   }

   @Override
   public boolean isCancelled() {
      boolean result = super.isCancelled();
      if (innerWorker instanceof HasCancellation) {
         result = ((HasCancellation) innerWorker).isCancelled();
      }
      return result;
   }

   @Override
   public void setCancel(boolean isCancelled) {
      super.setCancel(isCancelled);
      if (innerWorker instanceof HasCancellation) {
         ((HasCancellation) innerWorker).setCancel(isCancelled);
      }
   }
}
