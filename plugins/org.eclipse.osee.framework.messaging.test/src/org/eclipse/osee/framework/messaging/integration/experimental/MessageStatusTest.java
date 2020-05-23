/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.messaging.integration.experimental;

import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageStatusTest implements OseeMessagingStatusCallback {

   private volatile boolean isDone = false;
   private volatile boolean timedOut = false;
   private volatile boolean waitedOnStatus = false;
   private final boolean shouldPass;

   public MessageStatusTest(boolean shouldPass) {
      this.shouldPass = shouldPass;
   }

   @Override
   public void fail(Throwable th) {
      if (waitedOnStatus) {
         return;
      }

      if (timedOut) {
         return;
      }
      if (shouldPass) {
         org.junit.Assert.fail(th.getMessage());
      } else {
         org.junit.Assert.assertTrue(true);
      }
      isDone = true;
   }

   @Override
   public void success() {
      if (waitedOnStatus) {
         return;
      }

      if (timedOut) {
         return;
      }
      if (shouldPass) {
         org.junit.Assert.assertTrue(true);
      } else {
         org.junit.Assert.fail("We had a status of 'success'");
      }
      isDone = true;
   }

   public void waitForStatus(int time) {
      long timeout = System.currentTimeMillis() + time;
      while (!isDone && System.currentTimeMillis() < timeout) {
         try {
            Thread.sleep(200);
         } catch (InterruptedException ex) {
            //
         }
      }
      if (!isDone) {
         timedOut = true;
         if (shouldPass) {
            org.junit.Assert.fail("We timed out waiting for status.");
         } else {
            org.junit.Assert.assertTrue(true);
         }
      }
      waitedOnStatus = true;
   }
}
