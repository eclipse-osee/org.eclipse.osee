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
package org.eclipse.osee.ote.message.timer;

import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;

/**
 * This class is used as a 'Time Out' object while waiting for the desired message element value.
 * 
 * @author Ryan D. Brooks
 */
public class CycleCountDown implements ICancelTimer {
   private int cycleCount;
   private volatile boolean completed; 
   private final ITimeout objToNotify;
   private final IScriptControl scriptLock;

   /**
    * @param objToNotify The Object that is in wait() on which we will call notifyAll()
    * @param cycleCount The number of cycles to countdown.
    */
   public CycleCountDown(IScriptControl scriptLock, ITimeout objToNotify, int cycleCount) {
      super();
      this.scriptLock = scriptLock;
      this.cycleCount = cycleCount;
      this.objToNotify = objToNotify;
      this.completed = false;
      objToNotify.setTimeout(false);
   }

   /**
    * @return true if the countdown is complete/canceled, otherwise false
    */
   public boolean cycleOccurred() {
      if (cycleCount == 0 && !completed) {
         completed = true;
         synchronized (objToNotify) {
            objToNotify.setTimeout(true);
            objToNotify.notifyAll();
         }
      }
      else {
         cycleCount--;
      }
      return completed;
   }

   @Override
   public void cancelTimer() {
      this.completed = true;
      if (this.scriptLock != null) {
         this.scriptLock.lock();
      }
   }
  
}