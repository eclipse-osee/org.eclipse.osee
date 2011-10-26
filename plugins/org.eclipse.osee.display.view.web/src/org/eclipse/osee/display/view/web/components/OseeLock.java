/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web.components;

/**
 * @author Shawn F. cook
 */
public class OseeLock {

   private boolean isLocked = false;

   public synchronized void lock() {
      while (isLocked) {
         try {
            wait();
         } catch (InterruptedException ex) {
            //TODO: need logger
            System.out.println("OseeLock.lock - InterruptedException");
         }
      }
      isLocked = true;
   }

   public synchronized void unlock() {
      isLocked = false;
   }

   //If lock is AVAILABLE then LOCK it and return TRUE.
   // If lock is NOT AVAILABLE then do nothing and return FALSE.
   public synchronized boolean tryLockGreedy() {
      if (!isLocked) {
         lock();
         return true;
      } else {
         return false;
      }
   }

}
