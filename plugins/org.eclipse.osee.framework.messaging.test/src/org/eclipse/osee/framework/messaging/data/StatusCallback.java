/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.data;

import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author Roberto E. Escobar
 */
public class StatusCallback implements OseeMessagingStatusCallback {

   private final int expectedCount;

   private volatile int currentCount;
   private volatile boolean wasUpdateReceived;
   private volatile boolean failed;

   public StatusCallback(int expectedCount) {
      this.expectedCount = expectedCount;
      reset();
   }

   public void reset() {
      failed = false;
      currentCount = 0;
      wasUpdateReceived = false;
   }

   @Override
   public void success() {
      currentCount++;
      if (currentCount == expectedCount) {
         synchronized (this) {
            wasUpdateReceived = true;
            notify();
         }
      }
   }

   @Override
   public void fail(Throwable th) {
      synchronized (this) {
         failed = true;
         notify();
      }
   }

   public boolean failed() {
      return failed;
   }

   public int getTotalReceived() {
      return wasUpdateReceived() ? currentCount : 0;
   }

   private boolean wasUpdateReceived() {
      return wasUpdateReceived;
   }
}