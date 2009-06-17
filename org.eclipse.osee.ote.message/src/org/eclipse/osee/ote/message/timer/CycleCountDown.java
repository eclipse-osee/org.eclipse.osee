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
 * @author Ryan D. Brooks
 * 
 * This class is used as a 'Time Out' object while waiting for the desired message element value.
 * 
 */
public class CycleCountDown implements ICancelTimer {
   private int cycleCount;
   private ITimeout objToNotify;
   private IScriptControl scriptLock;

   /**
    * @param objToNotify The Object that is in wait() on which we will call notifyAll()
    * @param cycleCount The number of cycles to countdown.
    */
   public CycleCountDown(IScriptControl scriptLock, ITimeout objToNotify, int cycleCount) {
      super();
      this.scriptLock = scriptLock;
      this.cycleCount = cycleCount;
      this.objToNotify = objToNotify;
      objToNotify.setTimeout(false);
   }

   /**
    * @return true if the cycleCount == 0, otherwise false
    */
   public boolean cycleOccurred() {
      if (cycleCount == -1){
         return true;
      }
      if (cycleCount == 0) {         
         synchronized(objToNotify){
        	objToNotify.setTimeout(true);
            objToNotify.notifyAll();
         }
         return true; // so that notify is only called after a countdown becomes zero for the first time
      }
      cycleCount--;
      return false;
   }
   
   public void cancelTimer(){
      this.cycleCount = -1;  
      if(this.scriptLock != null){
         this.scriptLock.lock();
      }
   }
}