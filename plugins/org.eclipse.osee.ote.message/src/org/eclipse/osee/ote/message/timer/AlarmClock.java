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

import java.util.TimerTask;

import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;



/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class AlarmClock extends TimerTask implements ICancelTimer {
   private ITimeout sleeper;

   /**
    * @param sleeper
    */
   public AlarmClock(ITimeout sleeper) {
      super();
      this.sleeper = sleeper;
      sleeper.setTimeout(false);
   }

   /** 
    * @see java.lang.Runnable#run()
    */
   public void run() {
      synchronized(sleeper) {
         sleeper.notify();
         sleeper.setTimeout(true);            
      }
   }
   
   public void cancelTimer() {
      this.cancel();
   }
}