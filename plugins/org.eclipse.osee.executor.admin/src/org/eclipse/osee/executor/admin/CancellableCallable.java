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
package org.eclipse.osee.executor.admin;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Roberto E. Escobar
 */
public abstract class CancellableCallable<T> implements Callable<T>, HasCancellation {

   private final AtomicBoolean cancelled = new AtomicBoolean(false);

   protected CancellableCallable() {
      // do nothing
   }

   @Override
   public boolean isCancelled() {
      cancelled.compareAndSet(false, Thread.currentThread().isInterrupted());
      return cancelled.get();
   }

   @Override
   public void setCancel(boolean isCancelled) {
      cancelled.set(isCancelled);
   }

   @Override
   public void checkForCancelled() throws CancellationException {
      if (isCancelled()) {
         throw new CancellationException();
      }
   }
}
