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
package org.eclipse.osee.ote.message.elements.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ken J. Aguilar
 */
class SequenceHandle implements ISequenceHandle {

   private final ReentrantLock lock = new ReentrantLock();
   private final Condition endSequenceCondition = lock.newCondition();
   private boolean endSequence = false;

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.test.ISequenceHandle#waitForEndSequence()
    */
   @Override
   public boolean waitForEndSequence(long timeout, TimeUnit timeUnit) throws InterruptedException {
      lock.lock();
      long nanos = timeUnit.toNanos(timeout);
      try {
         while (!endSequence) {
            if (nanos > 0) {
               nanos = endSequenceCondition.awaitNanos(nanos);
            } else {
               return false;
            }
         }
         return true;
      } finally {
         lock.unlock();
      }
   }

   void signalEndSequence() {
      lock.lock();
      try {
         endSequence = true;
         endSequenceCondition.signalAll();
      } finally {
         lock.unlock();
      }
   }
}
