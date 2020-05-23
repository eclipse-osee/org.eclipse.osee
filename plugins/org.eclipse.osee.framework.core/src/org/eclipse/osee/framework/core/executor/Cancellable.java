/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.executor;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ryan D. Brooks
 */
public class Cancellable implements HasCancellation {
   private final AtomicBoolean cancelled = new AtomicBoolean(false);

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