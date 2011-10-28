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

/**
 * @author Roberto E. Escobar
 */
public abstract class CancellableCallable<T> implements Callable<T>, HasCancellation {

   private volatile boolean cancelled;

   protected CancellableCallable() {
      super();
      cancelled = false;
   }

   @Override
   public boolean isCancelled() {
      return cancelled;
   }

   @Override
   public void setCancel(boolean isCancelled) {
      cancelled = isCancelled;
   }

   @Override
   public void checkForCancelled() throws CancellationException {
      if (isCancelled()) {
         throw new CancellationException();
      }
   }
}
